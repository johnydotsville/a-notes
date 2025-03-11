# Динамический маршрут

Динамический маршрут - это маршрут, в котором есть изменяющийся параметр, например:

```
www.foobar.com/notes/7
```

где 7 - id заметки и он изменяется в зависимости от того, какую заметку нужно открыть.

# Организация динамического маршрута

Работа с динамическими маршрутами строится примерно так:

* В компоненте `<Route>` через атрибут path задаем путь с изменяющимся параметром:

  ```react
  <Route path='/notes/:id' element={<Note />} />
  ```

  Изменяющийся параметр отделяем двоеточием `:`

* Организуем переходы. Как именно - не важно, можно через компонент Link, можно через хук useNavigate. Главное правильно формировать ссылки.

* Создаем компонент для динамического маршрута. В нем с помощью хука `useParams` можем извлечь значение динамического параметра из url.

  * Параметр будет называться так же, как он описан в Route. Например, если там `/notes/:id`, то и при извлечении к нему надо обращаться как `id`
  
    ```javascript
    const params = useParams();  // <-- Получаем параметры из url
    const id = params.id;
    ```
  
    

# Пример

```react
import { BrowserRouter } from 'react-router-dom';
import { Routes, Route } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/notes' element={<Notes />} />
        <Route path='/notes/:id' element={<Note />} />  {/* <-- Динамический маршрут */}
      </Routes>
    </BrowserRouter>
  );
}

// <-- Компонент со списком заметок
function Notes() {
  const notes = [...Array(10)].map((_, i) => { return { 
      id: i + 1, 
      short: `Заметка #${i + 1}`
    }
  });

  const navigate = useNavigate();
  const read = (id) => navigate(`/notes/${id}`);  // <-- Реализация перехода через useNavigate.

  return (
    <>
      <h1>Страница со списком заметок</h1>
      {
        notes.map(n => <div>{n.short}
            <Link to={`/notes/${n.id}`}>Читать</Link> {/* Реализация перехода через Link */}
            <button onClick={() => read(n.id)}>Читать</button>
          </div>
        )
      }
    </>
  );
}

// <-- Компонент конкретной заметки
function Note() {
  const params = useParams();  // <-- Получаем параметры из url
  // <-- Используем параметры для дальнейших действий
  return <h1>Страница заметки #{params.id}</h1>
}
```

# Настройка webpack

При использовании динамических маршрутов в приложении, базированном на вебпаке + его плагине dev-server, появилась проблема:

```
Refused to execute script from 'http://localhost:3000/...' because its MIME type ('text/html')...
```

Решилась добавлением настройки `publicPath` в конфиг вебпака

```javascript
return {
  mode:  argv.mode,
  entry: paths.entryFile,
  output: {
    filename: '[name].[contenthash].js',
    path: paths.outputDir,
    publicPath: "/",  // <-- Вот эта настройка.
    clean: true
  }
  // Остальная часть конфига
}
```


