# Вопросы

- [ ] Может ли процесс работать с устройствами IO напрямую, без участия ОС?
  - [ ] Какую роль играют системные вызовы ОС при работе процесса с IO?
  - [ ] Чем концептуально отличаются синхронные и асинхронные системные вызовы ОС?
  - [ ] Опишите, что происходит, когда процесс делает системный вызов (для обоих их видов), в какое состояние она переходит?
- [ ] Как виды системных вызовов связаны с концепциями "блокирующего" и "неблокирующего" IO? TODO: хорошо бы потом копнуть посильнее, чтобы знать наверняка
  - [ ] Как в случае синхронного СВ процесс понимает, что запрошенные данные готовы и можно продолжать работу? А в случае асинхронного СВ?
- [ ] Чем концептуально отличаются блочные и символьные устройства IO?
  - [ ] К какому типу относятся hdd, ssd, сетевая карта? Какие из них поддерживают позиционирование, а какие нет, и почему?
  - [ ] Как концептуально устроено любое IO-устройство? Какие два элемента у него есть?
  - [ ] Как (в общих чертах) взаимодействуют ОС и IO устройство?

# Блокирующий\неблокирующий IO

Термины "блокирующий" и "неблокирующий" IO позволяют понять, что происходит с потоком, который запросил операцию IO. Поскольку IO это в основном работа с устройствами (диски, сетевая карта), то почти любая операция IO - это системный вызов к операционной системе, т.е. поток при этом отдает процессор операционной системе и переходит в состояние блокировки.

При блокирующем IO, ОС не пробуждает поток до тех пор, пока операция IO не будет полностью выполнена. Поэтому, когда поток продолжает выполнение, можно быть уверенным, что все данные, которые он ожидал получить в результате запрошенной операции IO, готовы и он может их обработать.

При неблокирующем IO, ОС запускает операцию IO и возвращает управление потоку, т.е. он продолжает выполняться, пока не закончится его квант времени. При этом конечно у него нет никаких гарантий того, что интересующие его данные готовы. Говорят, что "пока операция IO не закончена, поток может заняться чем-то еще". Но ведь не просто так же он запросил IO, он ведь наверняка хотел получить данные и обработать их, так чем же тогда "еще" он может заняться? Разве что сидеть и спрашивать "Ну что, готовы данные, которые я запросил? А теперь? А теперь? А теперь?". Поэтому на деле это "чем-то еще" означает, что скорее всего в этом потоке используется много "источников" данных и он просто инициирует вторую IO операцию, потом третью, четвертую и т.д. И так глядишь пока он это делает, первый источник заполнится данными и можно будет их обработать.

# Устройство IO-оборудования

Устройства IO делятся на несколько типов, два из которых это:

* Блочные - hdd, sdd, флэшки и прочие, поддерживающие позиционирование
* Символьные - сетевая карта и прочие, которые позиционирование не поддерживают в виду своей природы (бесконечный поток байтов)

У этих устройств есть два концептуальных компонента: буфер и контроллер с регистрами. В буфере накапливаются данные, которые устройство получает\отправляет, а благодаря регистрам устройство и ОС общаются друг с другом. Например, по сети приходят данные и попадают в буфер сетевой карты. При этом она устанавливает в условный регистр "статуса" значение, сигнализирующее о том, что "данные пришли, можно забирать". ОС периодически проверяет этот "регистр статуса" и когда видит этот сигнал, копирует данные из буфера в память потока, которому они были нужны, и оттуда поток уже может их читать. А когда поток хочет отправить что-то по сети, он делает системный вызов, ОС берет эти данные, копирует из памяти потока в буфер сетевой карты и записывает в условный регистр "управления" какое-то значение и тогда сетевая карта понимает, что эти данные из буфера надо вылить в сеть.

> Где находится контроллер - на материнке или это часть самого устройства, пока не знаю. И так-то это не сильно важно вроде

Реальная картина такого взаимодействия может отличаться, но суть примерно такая и есть. Важен сам факт того, что ОС поддерживает режим IO без блокировок за счет того, что часть ее собственных системных вызовов реализована именно как неблокирующие.

Оповещения о готовности для самой ОС тоже отдельная тема, но один из способов - так называемый *event loop*, поток, занимающийся в цикле опросом устройств о готовности результата IO. Упоминаю об этом потому, что в NIO есть селекторы, которые очень похожи на этот самый event loop.

Некоторые процессы:

* Вот как я понял процесс отправки данных: клиентская программа вызывает IO операцию, которая должна отсылать некоторые данные. Эти данные записываются в буфер сокета (этот буфер находится в RAM) и когда буфер заполняется, тред блокируется и управление уходит ОС. Она начинает этот буфер читать и отправлять по сети (вероятно, перемещая данные из RAM-буфера сокета в буфер сетевой карты). Когда все из буфера прочитано, он опустошается и ОС снова запускает процесс, чтобы он докинул оставшиеся данные в буфер.

  Соответственно на стороне сервера

# Системные вызовы

## Синхронные и асинхронные

Системный вызов (СВ) бывает синхронный и асинхронный. Синхронный предполагает, что вызвавший СВ процесс блокируется до полного завершения СВ. Когда СВ завершается, ОС переводит запросивший СВ процесс в состояние готовности и когда снова наступает его очередь выполняться, он гарантированно получает результаты работы СВ.

Асинхронный не предполагает блокировку вызвавшего процесса. При АСВ происходит, например, запуск операции IO, управление возвращается вызвавшему СВ процессу и он использует свой квант времени дальше. При этом подходе ему конечно приходится потом самому как-то узнавать, готовы ли результаты.

Большая часть СВ является синхронной, но в современных ОС количество АСВ увеличивается.

> Теперь бы выяснить, как ОС обрабатывает АСВ. Ведь процесс как-то должен узнать, завершеная ли операция, ради которой он делал СВ. Если, он, грубо говоря, забьет на нее? Могут ли результаты быть утеряны? В общем, как ведет себя ОС при таком сценарии.

# Полезные ссылки

Ссылки, в которых действительно были найдены важные, ключевые для понимания вещи:

* https://ps-group.github.io/os/nonblocking_io_posix
* https://studfile.net/preview/9886821/page:4/