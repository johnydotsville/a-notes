# Концепция редюсера

Концептуально, редюсер - это функция, которая выполняет действие над текущим состоянием и возвращает новое состояние.

Редюсеры - это альтернативный способ управлять изменением состояния. Для работы с редюсерами есть хук `useReducer()`. Работа с состоянием организуется примерно так:

* Мы собираем все операции по изменению определенного состояния в одну функцию ("редюсер").
* Вызываем хук `useReducer()` и передаем ему состояние, которое должен обрабатывать редюсер, и сам редюсер.
* Хук возвращает нам переменную для чтения состояния, и функцию-"диспетчер".
* Теперь, когда нам надо изменить состояние, мы вызываем диспетчер и передаем ему объект действия. Этот объект обычно содержит два поля:
  * `type` - строка с именем события.
  * `payload` - данные, которые нужны для выполнения действия по обработке события. Эти данные обычно называют термином "нагрузка".
* Наш вызов диспетчера триггерит реакт на вызов соответствующего редюсера. Реакт передает редюсеру состояние и объект действия, который мы отдали диспетчеру.
* Редюсер по полю type из объекта действия понимает, что именно надо сделать, берет переданное ему состояние, данные из объекта действия, вычисляет новое состояние и возвращает это новое состояние реакту.
* Т.о. и происходит изменение состояния.

Главный плюс такого подхода - если логика изменения состоянием становится объемной, то вынос ее в отдельную функцию делает компонент визуально чище.

# Компонент с редюсером

Готовый целостный пример компонента, использующего редюсер. Более детальное объяснение - в отдельном разделе.

```react
import {useState} from 'react';
import {useReducer} from 'react';  // <-- Импортируем хук

// <-- Компонент с данными и логикой отображения
export default function Mentions() {
  // <-- Отдаем функцию-редюсер и начальные данные, а получаем ссылку на данные и функцию-диспетчер
  const [bookmarks, bookmarksDispatch] = useReducer(bookmarksReducer, getBookmarks());

  const addBookmark = (newBookmark) => {  // <-- Обработчики пишем и развешиваем как обычно
    bookmarksDispatch({  // <-- В обработчиках вызываем диспетчер и формируем ему объект события
      type: 'add',
      payload: newBookmark
    });
  };
  const deleteBookmark = (id) => {
    bookmarksDispatch({
      type: 'delete',
      payload: id
    });
  }

  return (
    <>
      <NewBookmark addBookmark={addBookmark} />  {/* Развешиваем обработчики */}
      <section>
        {bookmarks.map(b => 
          <Bookmark key={b.id} deleteBookmark={() => deleteBookmark(b.id)} title={b.title}>{b.content}</Bookmark>  {/* Развешиваем обработчики */}
        )}
      </section>
    </>
  );
}

function bookmarksReducer(bookmarks, action) {
  switch (action.type) {  // <-- В функции-редюсере
    case 'add': {  // <-- определяем вид события
      return [...bookmarks, action.payload];  // <-- И выполняем нужные действия над состоянием
    }
    case 'delete': {
      return bookmarks.filter(b => b.id !== action.payload);
    }
    default: {
      throw Error('Неизвестное действия над пословицами.');
    }
  }
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

# Объяснение реализации

## Импортируем хук

```react
import {useReducer} from 'react';
```

## Пишем функцию-редюсер

У редюсера два параметра - состояние, которое он обрабатывает, и действие. И то, и другое редюсеру передаст реакт, когда мы зарегистрируем событие через диспетчер. Например, пусть у нас состояние - это массив пословиц, а возможные действия - добавить пословицу и удалить. Тогда редюсер может выглядеть так:

```react
function bookmarksReducer(bookmarks, action) {  // <-- Получает состояние и действие от реакта
  switch (action.type) {  // <-- По названию действия понимаем, что надо сделать с нагрузкой
    case 'add': {  // <-- При добавлении
      return [...bookmarks, action.payload];  // <-- В нагрузке будет добавляемая пословица
    }
    case 'delete': {  // <-- При удалении
      return bookmarks.filter(b => b.id !== action.payload);  // <-- В нагрузке будет id удаляемой пословицы
    }
    default: {
      throw Error('Неизвестное действия над пословицами.');
    }
  }
}
```

## Используем хук useReducer

Передаем в хук редюсер и исходные данные для состояния. Хук нам возвращает переменную, через которую мы сможем читать состояние, и функцию-диспетчер для регистрации события:

```react
const [bookmarks, bookmarksDispatch] = useReducer(bookmarksReducer, initialData);
```

## Пишем обрабочики событий

Обработчики для событий пишем как обычно, но внутри них не выполняем действия над состоянием, а вызываем диспетчер и передаем ему объект действия:

```react
const addBookmark = (newBookmark) => {  // <-- Пишем обработчик события как обычно.
  bookmarksDispatch({  // <-- Но нем вызываем диспетчер, а не меняем состояние сами.
    type: 'add',  // <-- Ставим для события имя, чтобы редюсер понял, что ему надо делать,
    payload: newBookmark  // <-- И указываем полезную нагрузку.
  });
};
const deleteBookmark = (id) => {
  bookmarksDispatch({
    type: 'delete',
    payload: id
  });
}

return (
  <>
    <NewBookmark addBookmark={addBookmark} />
    <section>
      {bookmarks.map(b => 
        <Bookmark key={b.id} deleteBookmark={() => deleteBookmark(b.id)} title={b.title}>{b.content}</Bookmark>
      )}
    </section>
  </>
);
```

# Компонент без редюсера

Просто для сравнения:

```react
import {useState} from 'react';

// <-- Компонент с данными и логикой отображения
export default function Mentions() {
  const [bookmarks, setBookmarks] = useState(getBookmarks());

  const addBookmark = (newBookmark) => {
    setBookmarks([...bookmarks, newBookmark]);  // <-- Действия реализованы непосредственно в компоненте
  };
  const deleteBookmark = (id) => {
    setBookmarks(bookmarks.filter(b => b.id !== id));  // <-- Действия реализованы непосредственно в компоненте
  }

  return (
    <>
      <NewBookmark addBookmark={addBookmark} />
      <section>
        {bookmarks.map(b => 
          <Bookmark key={b.id} deleteBookmark={() => deleteBookmark(b.id)} title={b.title}>{b.content}</Bookmark>
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

# Требования к редюсерам

* Редюсеры выполняются во время рендеринга, поэтому они должны быть чистыми. Никаких вызовов внутри них, никаких сайд-эффектов, установки таймеров и так далее. Одинаковый вход должен давать одинаковый выход.
* Для редюсера устанавливается часть состояния компонента и каждое действие описывает пользовательское событие, которое может затрагивать много данных из этой части состояния. Например, состояние выражено набором полей формы, пусть пять полей. Действие "очистить форму" будет очищать все пять полей и все эти очистки будут проводиться в едином действии, а не дробиться на пять разных действий (хз, зачем я это пишу, звучит как самоочевидный бред, но пусть будет).