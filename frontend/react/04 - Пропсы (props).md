# Пропсы

## Базовый синтаксис

Пропсы - это некоторые входные данные для компонента. Можно сказать, параметры для него, т.к. компонент это по сути функция.

Компонент с пропсами:

```react
const Info = (props) => {  // <-- Пропсы - это просто параметр функции
  console.log(props);
  return (
    <div>
      <h1>{props.header}</h1>  <!-- Извлекаем нужные данные из пропсов -->
      <div>{props.article.title}</div>
      <div>{props.article.author}</div>
      <div>{props.article.content}</div>
    </div>
  );
};

export default Info;
```

Передача пропсов в компонент:

```react
function App() {
  return (
    <div className="App">
      <PostItem />
      <Info header={"Важная информация"} article={{title: "О наркотиках", author: "Мистер Маки", content: "Наркотики - эт плохо, пнятненько?"}} />
    </div>
  );
}
```

Комментарии:

* Все "атрибуты" компонента собираются в пропс. В данном случае в пропсах будет два поля - header и article.
* Значение "атрибуту" задается через фигурные скобки `{}`.
  * Значением может быть как простое значение, так и сложный объект.

## Деструктуризация

Предыдущий пример можно переписать, используя деструктуризацию. В принципе это должно быть очевидно, если нормально знать JS, но на всякий случай напишу:

```react
const Info = ({header, article}) => {  // <-- Деструктуризируем пропсы
  return (
    <div>
      <h1>{header}</h1>
      <div>{article.title}</div>
      <div>{article.author}</div>
      <div>{article.content}</div>
    </div>
  );
};

export default Info;
```

## Ключ компонента

В качестве более интересного примера сделаем список постов.

Компонент поста:

```react
const PostItem = (props) => {
  return (
    <div className="post">
      <div className="post__content">
        <strong>{props.post.id} {props.post.title}</strong>
        <div>{props.post.body}</div>
      </div>
      <div className="post__buttons">
        <button>Удалить</button>
      </div>
    </div>
  )
}

export default PostItem;
```

Компонент, содержащий посты:

```react
function App() {
  const [posts, setPosts] = useState([
    {id: 1, title: 'Javascript', body: 'Javascript - это язык программирования.'},
    {id: 2, title: 'Java', body: 'Java - это язык программирования, в основном используется на бэкэнде.'},
    {id: 3, title: 'C#', body: 'C# - это как Java, только лучше.'}
  ]);

  return (
    <div className="App">
      <h1>Список постов:</h1>
      {posts.map(p => <PostItem post={p} key={p.id} />)}
    </div>
  );
}

export default App;
```

Комментарии:

* Когда мы формируем таким образом списки компонентов, нужно задавать им уникальный индекс через атрибут `key`, чтобы реакт более эффективно их перерисовывал. Этот ключ должен быть уникальным и статичным.

# Стандартные пропсы

## children

Этот пропс содержит в себе контент компонента. Например:

```react
<MyButton>Создать</MyButton>
```

```react
const MyButton = ({children, ...rest}) => {
  return (
    <button {...rest}>{children}</button>
  );
};
```

Здесь children будет содержать надпись "Создать".

Комментарий насчет синтаксиса. Изначально props содержит children, плюс все "атрибуты", установленные компоненту. Например, если написать `<MyButton disabled>Создать</MyButton>`, то объект props переданный компоненту, будет содержать два свойства - children и disabled. Конструкцией `{children, ...rest}` мы выдергиваем свойство children в одноименную переменную, а все оставшиеся свойства обратно собираем в объект rest. В данном случае там окажется одно свойство - disabled. Далее через`<button {...rest}` мы обратно разбиваем объект rest на свойства и у кнопки оказывается свойство disabled. Таким образом можно задать компоненту разные атрибуты, актуальные для кнопки, и все они окажутся внутри подлежащей кнопки.