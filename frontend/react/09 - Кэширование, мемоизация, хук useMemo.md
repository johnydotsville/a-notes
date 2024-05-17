# Кэширование, мемоизация

## Проблема

Рассмотрим такой пример: у нас компонент с набором пословиц и поле, в которое мы можем ввести слово и отфильтровать только те пословицы, в которых это слово встречается:

```react
import {useState} from 'react';

export default function Mentions() {
  const [bookmarks, setBookmarks] = useState(getBookmarks());
  const [needle, setNeedle] = useState('');
  const [foobar, setFoobar] = useState('');

  let displayedBookmarks = bookmarks;
  if (needle) {
    console.log(`Сработала фильтрация. needle: '${needle}'`);
    displayedBookmarks = displayedBookmarks.filter(b => b.content.toLowerCase().includes(needle.toLowerCase()));
  }

  return (
    <>
      <div>Иголку не меняем, а фильтр все равно срабатывает<input onChange={e => setFoobar(e.target.value)} /></div>
      Мгновенный поиск <input onChange={e => setNeedle(e.target.value)} />
      <section>
        {displayedBookmarks.map(b => <div key={b.id}>{b.title}: {b.content}</div>)}
      </section>
    </>
  );
}

function getBookmarks() {
  return [
    {id: 0, title: 'О дружбе и вражде', content: 'Неверный друг опаснее врага.'},
    {id: 1, title: 'О родине', content: 'Глупа та птица, которой гнездо свое немило.'},
    {id: 2, title: 'О труде и работе', content: 'С мастерством люди не родятся, а добытым ремеслом гордятся.'},
    {id: 3, title: 'О времени', content: 'Иное время, иное бремя.'},
    {id: 4, title: 'О книгах и чтении', content: 'Книга - маленькое окошко, через него весь мир видно.'},
  ];
}
```

Логика такая: мы оформляем иголку (искомое слово) в виде одного из состояний компонента и связываем ее с полем поиска. Так что когда меняется поле поиска, то меняется и состояние (иголка) и это приводит к перерисовке компонента. В теле компонента проверяем, если иголка задана, то фильтруем массив пословиц. В итоге у нас отрисовывается фильтрованный массив.

Но есть нюанс - компонент перерисовывается при изменении любого состояния. Поэтому, если у нас кроме иголки есть и другое состояние (неважно какое именно, в данном примере это некий foobar), то при его изменении опять будет вызываться функция компонента, а стало быть опять будет проверка иголки и фильтровка.

Таким образом, получаются лишние действия. Если иголка не менялась, то нет смысла фильтровать массив снова, можно отрисовать предыдущие элементы.

## хук useMemo

Документация: https://react.dev/reference/react/useMemo

```react
import {useMemo} from 'react';
```

Синтаксис:

```react
const result = useMemo(
  () => doSome(),
  [dependency1, dependencyN]
);
```

* Первый параметр - это работа, которую нужно выполнить и закэшировать ее результат.
* Второй параметр - *массив* зависимостей. Зависимости - это данные, на основе которых выполняется работа. Если эти данные к следующему разу не изменились, то нет смысла выполнять работу еще раз, а можно просто вернуть закешированный результат. Если же хотя бы одна из зависимостей изменила значение, то работа выполняется заново.

Пример использования, решающий обозначенную выше проблему:

```react
import {useState} from 'react';
import {useMemo} from 'react';

export default function Mentions() {
  const [bookmarks, setBookmarks] = useState(getBookmarks());
  const [needle, setNeedle] = useState('');
  const [foobar, setFoobar] = useState('');

  const displayedBookmarks = useMemo(() => {
    if (needle) {
      console.log(`Сработала фильтрация. needle: '${needle}'`);
      return bookmarks.filter(b => b.content.toLowerCase().includes(needle.toLowerCase()));
    }
    return bookmarks;
  }, [bookmarks, needle]);

  const deleteBookmark = (id) => {
    setBookmarks(bookmarks.filter(b => b.id !== id));
  }

  return (
    <>
      <div>Теперь фильтр не срабатывает при изменении этого поля<input onChange={e => setFoobar(e.target.value)} /></div>
      Мгновенный поиск <input onChange={e => setNeedle(e.target.value)} />
      <section>
        {displayedBookmarks.map(b =>
          <div key={b.id}>
            {b.title}: {b.content}
            <button onClick={() => deleteBookmark(b.id)}>Удалить</button>
          </div>
        )}
      </section>
    </>
  );
}

function getBookmarks() {
  return [
    {id: 0, title: 'О дружбе и вражде', content: 'Неверный друг опаснее врага.'},
    {id: 1, title: 'О родине', content: 'Глупа та птица, которой гнездо свое немило.'},
    {id: 2, title: 'О труде и работе', content: 'С мастерством люди не родятся, а добытым ремеслом гордятся.'},
    {id: 3, title: 'О времени', content: 'Иное время, иное бремя.'},
    {id: 4, title: 'О книгах и чтении', content: 'Книга - маленькое окошко, через него весь мир видно.'},
  ];
}
```

Условие проверки иголки и фильтровку помещаем внутрь useMemo, а зависимостями ставим исходный массив данных и иголку. При инициализации компонента работа выполнится первый раз, результат закешируется, а useMemo запомнит значения needle и bookmark. Теперь при перерисовке компонента, useMemo возьмет текущие значения needle и bookmark и сравнит с запомненными. Если хотя бы одно будет отличаться, то снова выполнится фильтровка. Если же оба значения остались старыми, то useMemo просто вернет значение из кэша.

Т.о., изменение состояния foobar теперь не провоцирует повторную фильтровку.

## Дополнительная информация по useMemo

### Правила использования useMemo

* Не помещать useMemo в конструкцию с условием и в циклы. Обычно это формулируется как “поместите хук в самый верх своего компонента”.
* Функция, переданная в качестве первого аргумента, должна быть чистой, не принимать аргументов и всегда иметь возвращаемое значение.
* useMemo должен всегда содержать массив зависимостей, даже если он пустой. Без массива зависимостей работа будет выполнятся при каждом рендере. Если массив есть и он пустой, то работа выполнится всего один раз и ее результат будет запомнен.

### Особенности useMemo

* Для сравнения зависимостей useMemo использует метод `Object.is`
  * P.S. В какой-то момент я подумал, а как useMemo сравнивает массивы? Как он определит, изменился массив или нет? Ведь если добавить элемент в массив, то технически это тот же самый массив, если сравнивать по адресу в памяти. И только потом я сообразил, что вопрос не актуален, потому что если мы меняем исходные данные, то делаем это путем создания нового массива на основе старого. Так что даже если сравнивать по ссылке, то все равно это будут разные массивы.

## Более сложный пример на useMemo

Иногда для достижения результата можно использовать цепочки useMemo. В этом случае результат предыдущего useMemo становится зависимостью следующего useMemo.

Например, задача в которой нужно реализовать и фильтр, и сортировку данных. Зависимостей получается три: исходные данные, иголка и поле сортировки. Но изменение сортировки вовсе не означает, что нужно повторно выполнять фильтровку. Если иголка не поменялась и исходные данные прежние, значит можно просто взять из кеша прошлый результат фильтровки и выполнить только сортировку. Реализация:

```react
import {useState} from 'react';
import {useMemo} from 'react';
import {useRef} from 'react';

// <-- Компонент с данными и логикой отображения
export default function Mentions() {
  const [bookmarks, setBookmarks] = useState(getBookmarks());
  const [sortBy, setSortBy] = useState('');
  const [needle, setNeedle] = useState('');
  const [foobar, setFoobar] = useState('');

  const displayedBookmarks = useDisplayedBookmarks(bookmarks, sortBy, needle);
  const addBookmark = (newBookmark) => {
    setBookmarks([...bookmarks, newBookmark]);
  };
  const deleteBookmark = (id) => {
    setBookmarks(bookmarks.filter(b => b.id !== id));
  }

  return (
    <>
      <div>Нужен useMemo<input onChange={e => setFoobar(e.target.value)} /></div>
      Обычный поиск <Search setNeedle={setNeedle} />
      Мгновенный поиск <LiveSearch setNeedle={setNeedle} />
      <div>Сортировать по
        <Select 
          options={[
            {field: 'title', displayName: 'Заголовок'},
            {field: 'content', displayName: 'Содержимое'}
          ]} 
          onSelect={f => setSortBy(f)} />
      </div>
      <NewBookmark addBookmark={addBookmark} />
      <section>
        {displayedBookmarks.map(b => 
          <Bookmark key={b.id} deleteBookmark={() => deleteBookmark(b.id)} title={b.title}>{b.content}</Bookmark>
        )}
      </section>
    </>
  );
}

// <-- Вынесли логику в самостоятельный хук, чтобы сделать компонент компактнее
function useDisplayedBookmarks(bookmarks, sortBy, needle) {
  let result = bookmarks;
  
  result = useMemo(() => {  // <-- Будет цепочка useMemo
    if (needle) {
      console.log(`Сработала фильтрация. needle: '${needle}'`);
      return result.filter(b => b.content.includes(needle));
    }
    return result;
  }, [needle, result]);

  result = useMemo(() => {
    if (sortBy) {
      console.log(`Сработала сортировка. sortBy: '${sortBy}'`);
      return [...result].sort((a, b) => a[sortBy].localeCompare(b[sortBy]));
    }
    return result;
  }, [sortBy, result]);

  return result;
}

function getBookmarks() {
  return [
    {id: 0, title: 'О дружбе и вражде', content: 'Неверный друг опаснее врага.'},
    {id: 1, title: 'О родине', content: 'Глупа та птица, которой гнездо свое немило.'},
    {id: 2, title: 'О труде и работе', content: 'С мастерством люди не родятся, а добытым ремеслом гордятся.'},
    {id: 3, title: 'О времени', content: 'Иное время, иное бремя.'},
    {id: 4, title: 'О книгах и чтении', content: 'Книга - маленькое окошко, через него весь мир видно.'},
  ];
}

// <-- Компонент добавления новой пословицы
function NewBookmark({addBookmark}) {
  const [id, setId] = useState(0);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const createNewBookmark = () => {
    addBookmark({id: id, title: title, content: content});
    setId(0);
    setTitle('');
    setContent('');
  }

  return (
    <div style={{ border: '2px solid red' }}>
      <div>ID:<input onChange={e => setId(e.target.value)} /></div>
      <div>Тема:<input onChange={e => setTitle(e.target.value)} /></div>
      <div>Текст:<input onChange={e => setContent(e.target.value)} /></div>
      <button onClick={createNewBookmark}>Добавить</button>
    </div>
  );
}

// <-- Компонент выбора поля сортировки
function Select({options, onSelect}) {
  return (
    <select defaultValue="" onChange={e => onSelect(e.target.value)}>
      <option disabled value="">Не выбрано</option>
      {options.map(o => <option key={o.field} value={o.field}>{o.displayName}</option>)}
    </select>
  );
}

// <-- Компонент обычного поиска
function Search({setNeedle}) {
  const needle = useRef();

  const resetNeedle = () => {
    setNeedle('');
    needle.current.value = '';
  }

  return (
    <div>
      <input ref={needle} />
      <button onClick={() => setNeedle(needle.current.value)}>Искать</button>
      <button onClick={resetNeedle}>Сбросить</button>
    </div>
  );
}

// <-- Компонент живого поиска
function LiveSearch({setNeedle}) {
  const needle = useRef();

  const resetNeedle = () => {
    setNeedle('');
    needle.current.value = '';
  }

  return (
    <div>
      <input ref={needle} onChange={e => setNeedle(e.target.value)} />
      <button onClick={resetNeedle}>Сбросить</button>
    </div>
  );
}

// <-- Компонент элемента
function Bookmark({title, children, deleteBookmark}) {
  return (
    <div style={{ border: '1px solid black', margin: '10px', padding: '5px' }}>
      <strong>{title}</strong>
      <div>{children}</div>
      <button onClick={deleteBookmark}>Удалить</button>
    </div>
  );
}
```



# TODO

TODO:

* Условия, при которых кэш очищается.
* Мемоизация при lazy-загрузке. https://habr.com/ru/companies/otus/articles/800549/

