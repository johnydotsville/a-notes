# Базовая структура react-проекта

## HTML

### index.html

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>React App</title>
  </head>
  <body>
    <noscript>You need to enable JavaScript to run this app.</noscript>
    <div id="root"></div>
  </body>
</html>
```

html-часть как правило минималистична и представляет собой единственный div, в который рендерится приложение.

## JavaScript

### index.js

```react
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <App />
);

// // Альтернативный способ
// ReactDOM.render(<App />, document.getElementById('root'));
```

TODO: почитать про классы (или что это такое) React, ReactDOM и расписать. Мб конкретно тут не надо импортировать React даже.

### App.js

```react
import React from 'react';

function App() {
  return (
    <div className="App">
      Реакт
    </div>
  );
}

export default App;
```

TODO: почитать во что и как превращается этот jsx, что в итоге возвращается из функции App.