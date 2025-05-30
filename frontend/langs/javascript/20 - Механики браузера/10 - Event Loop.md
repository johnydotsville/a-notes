# Event Loop

Event Loop - это бесконечный цикл, который запускается после того, как движок выполнил весь начальный JS-код. Это выглядит примерно так: браузер загрузил скрипты, подключенные к странице, и сопутствующие скрипты внутри них (подключенные через статический import) и начинает выполнять их. В процессе их выполнения могут запускаться асинхронные операции, которые попадают в очередь задач и будут выполнены в будущем, после окончания выполнения всех этих начальных скриптов.

В итоге, когда код этих скриптов выполнился, запускается Event Loop и бесконечно крутится, проверяя очереди микро- и макротасок и выполняя их и рендер страницы по необходимости. На псевдокоде это выглядит примерно так:

```javascript
// 1. Выполнить ВЕСЬ начальный синхронный код (все <script>)
runAllInitialScripts();

// 2. Стартовать Event Loop
while (true) {
  // 1. Выполнить ВСЕ микротаски (пока очередь не опустеет)
  while (hasMicrotasks()) {
    execute(dequeueMicrotask());
  }

  // 2. Проверить, не пора ли рендерить (~60 FPS)
  if (shouldRender()) {
    render(); // Style → Layout → Paint → Composite
  } else if (hasMacrotasks()) {
    // 3. Если рендер не нужен (или уже выполнен) — взять одну макротаску
    execute(dequeueMacrotask());
  }
}
```

```javascript
// Синхронная стадия, когда страница только загрузилась и подключаются скрипты
выполнитьВсеСкриптыПодключенныеКСтранице();

// Входим в бесконечный Event Loop, начинается асинхронная стадия
while (true) {
  while (естьМикротаски) {
	делатьМикроТаски();
  }

  if (пораРендерить) {
    рендер(); // Style → Layout → Paint → Composite
  } else if (естьМАкроТаски) {
	сделатьОднуМакротаску();
  }
}
```



Т.о., вся дальнейшая работа страницы контролируется Event Loop'ом. Например, пользователь нажал кнопку. В результате этого API браузера поместил обработчик этой кнопки в очередь макротаксок. Потом Event Loop извлечет эту макротаску из очереди и запустит на выполнение. Рендер не обязательно выполняется в каждой итерации Event Loop'а, потому что частота выполнения рендера плюс-минус фиксированная и если таски легкие, то можно успеть выполниться несколько итераций цикла, прежде чем возникнет необходимость провести рендер.

P.S. Event Loop обрабатывает очереди задач только когда Call Stack пуст, т.е. когда в данный момент нет выполняющегося кода. Для браузеров этот факт не очень актуален, потому что по определению Event Loop запускается только когда выполнится весь "начальный код", соответственно после его выполнения Call Stack пуст, а в процессе выполнения задач из очередей Call Stack наполняется и опустошается, так что к новой итерации цикла Call Stack опять же пуст. Но этот факт важен для других сред, т.к. теоретически Call Stack может быть не пуст. Соответственно, Event Loop не должен ничего делать в этом случае, он должен дождаться, пока код из стэка отработает, и только потом запускать задачи из очередей.

