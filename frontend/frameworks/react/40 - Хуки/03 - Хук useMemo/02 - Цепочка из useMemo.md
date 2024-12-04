# Цепочки из useMemo

Иногда для достижения результата можно использовать цепочки useMemo. В этом случае результат предыдущего useMemo становится зависимостью следующего useMemo.

Например, задача в которой нужно реализовать и фильтр, и сортировку данных. Зависимостей получается три: исходные данные, иголка и поле сортировки. Но изменение сортировки вовсе не означает, что нужно повторно выполнять фильтровку. Если иголка не поменялась и исходные данные прежние, значит можно просто взять из кеша прошлый результат фильтровки и выполнить только сортировку.

Цепочка из useMemo в примере находится в самописном хуке.

# Полный текст примера

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
  };

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

# Пример по частям

## Компонент с данными и логикой отображения

```react
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
  };

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
```

## Самописный хук useDisplayedBookmarks, цепочка из useMemo

```react
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
```

Здесь useMemo идет в оптимальном порядке. Т.е. если поменять мемо сортировки и мемо фильтровки местами, то может получиться лишнее действие. Например, сортировка изменилась, а фильтровка и набор заметок остался старый. После сортировки мы получаем новый массив, стало быть фильтровка должна выполниться заново, хотя вообще-то это не надо, ведь заметки старые и уже были отфильтрованы. Поэтому оптимальнее мемо фильтровки располагать в начале. Так мы всегда будем получать уже фильтрованный набор заметок, который потом при необходимости уже будем сортировать.

## Компонент добавления новой пословицы

```react
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
```

## Компонент выбора поля сортировки

```react
// <-- Компонент выбора поля сортировки
function Select({options, onSelect}) {
  return (
    <select defaultValue="" onChange={e => onSelect(e.target.value)}>
      <option disabled value="">Не выбрано</option>
      {options.map(o => <option key={o.field} value={o.field}>{o.displayName}</option>)}
    </select>
  );
}
```

## Компонент обычного поиска

```react
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
```

## Компонент живого поиска

```react
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
```

## Компонент элемента

```react
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

