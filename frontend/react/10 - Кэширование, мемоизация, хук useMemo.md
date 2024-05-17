# Кэширование, мемоизация

TODO: почитать как useMemo работает с массивами. Прямо по элементам сравнивает или только ссылку на сам массив?

## Проблема

> Здесь я приведу лишь обрывки компонентов, потому что целиком было бы много, а писать отдельную демку долго. Надеюсь, с помощью комментариев мне удастся растолковать суть в этой ситуации.

Когда компонент состоит из нескольких элементов и один элемент меняется, то приходится перерисовывать весь компонент. Например:

```react
function App() {
    
const [posts, setPosts] = useState([
    {id: 1, title: 'Javascript', body: 'Javascript - это язык программирования.'},
    {id: 2, title: 'Java', body: 'Java - это язык программирования, в основном используется на бэкэнде.'},
    {id: 3, title: 'C#', body: 'C# - это как Java, только лучше.'}
  ]);

function getSortedPosts() {
  if (selectedSort) {
    return [...posts].sort(
      (a, b) => a[selectedSort].localeCompare(b[selectedSort])
    );
  }
  return posts;
}
const sortedPosts = getSortedPosts();
const [a, setA] = useState("ololo");

return (
  <div className="App">
    <PostForm createNewPost={createPost} />
    <input value={a} onChange={event => setA(event.target.value)} />  <!-- Управляемый input -->
    {
      posts.length != 0
        ? <PostList title={"Посты"} posts={sortedPosts} remove={removePost} />  <!-- Вызов функции сортировки -->
        : <div>Пока нет ни одного поста.</div>
    }
  </div>
);
    
}
```

Здесь у нас есть компонент создания нового поста, управляемый input и компонент для отображения постов. Переменная с постами заполняется через выполнение функции сортировки, которая может быть достаточно тяжелой в зависимости от количества постов.

Проблема в том, что при изменении значения в input будет перерисовывать и PostList, а значит будет вызывать и сортировка. По сути, если мы напечатаем в инпут слово "Привет", то сортировка постов будет вызвана 6 раз. Стоит отметить, что этой проблемы не будет, когда мы что-то вводим в инпуты, расположенные в PostForm. TODO: Предположительно это из-за того, что у PostForm свое состояние и его изменение приводит к перерисовке только компонента PostForm. А переменная, связанная с инпутом, является состоянием компонента App и соответственно при его изменении происходит перерисовка App, а заодно и перерасчет сортированных постов.

## хук useMemo

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

* Первый параметр - это колбэк, который нужно выполнить и закэшировать его результат.
* Второй параметр - массив зависимостей. Зависимости - это переменные, при изменении которых нужно заново выполнить колбэк. Если массив пустой, то колбэк выполнится всего один раз и его результат будет запомнен.

Решим проблему из примера с помощью useMemo:

```react
const sortedPosts = useMemo(() => {
  console.log("Вызов функции сортировки постов.");
  if (selectedSort) {
    return [...posts].sort(
      (a, b) => a[selectedSort].localeCompare(b[selectedSort])
    );
  }
  return posts;
}, [selectedSort, posts]);
```

Мы просто перенесли логику из метода getSortedPosts в колбэк, а метод удалили. Перерасчет сортировки постов нас интересует только в случае если меняется сам набор постов или поле сортировки, поэтому мы в зависимостях указали переменные состояния постов и выбранной сортировки. Самого компонента сортировки тут нет для краткости, но сути это не меняет.









Наглядный пример пользы useMemo

```react
import {useState} from 'react';
import {useRef} from 'react';

function getBookmarks() {
  return [
    {id: 0, title: 'О дружбе и вражде', content: 'Неверный друг опаснее врага.'},
    {id: 1, title: 'О родине', content: 'Глупа та птица, которой гнездо свое немило.'},
    {id: 2, title: 'О труде и работе', content: 'С мастерством люди не родятся, а добытым ремеслом гордятся.'},
    {id: 3, title: 'О времени', content: 'Иное время, иное бремя.'},
    {id: 4, title: 'О книгах и чтении', content: 'Книга - маленькое окошко, через него весь мир видно.'},
  ];
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
function Bookmark({title, children}) {
  return (
    <div style={{ border: '1px solid black', margin: '10px', padding: '5px' }}>
      <strong>{title}</strong>
      <div>{children}</div>
    </div>
  );
}

// <-- Компонент с данными и логикой отображения
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
      <div>Нужен useMemo<input onChange={e => setFoobar(e.target.value)} /></div>
      Обычный поиск <Search setNeedle={setNeedle} />
      Мгновенный поиск <LiveSearch setNeedle={setNeedle} />
      <section>
        {displayedBookmarks.map(b => <Bookmark title={b.title}>{b.content}</Bookmark>)}
      </section>
    </>
  );
}
```

