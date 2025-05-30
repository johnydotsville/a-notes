Здесь про стилизацию исключительно в паре с react.

# CSS

## Пакеты

Для использования css потребуется два лоадера:

| Зависимость  | Документация                                                 | Зачем нужен                                                  |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| css-loader   | [Документация](https://webpack.js.org/loaders/css-loader/)   | Трансформирует css-файл в CommonJS-модуль, не более. После этого вебпак может загрузить этот модуль себе. |
| style-loader | [Документация](https://webpack.js.org/loaders/style-loader/) | Встраивает стили в DOM. Более конкретно - добавляет тег `<style>` в тег `<head>` страницы. P.S. Сомнительно. Бывает тега style нет в html, а стили применены. Как так? Плюс, если мы при сборке не извлекаем стили в отдельный файл и они встраиваются в код бандла, то тега style тем более нет, а стили применены. Как так? |

```
npm i -D css-loader style-loader
```

## webpack.config.js

Добавляем лоадеры в конфиг вебпака:

```javascript
module.exports = (settings, argv) => {
  return {
    module: {
      rules: [
        {  // <-- Лоадер для css-файлов
          test: /\.css$/i,
          use: ["style-loader", "css-loader"],
        },
        // Остальные лоадеры
      ],
    }
    // Остальная часть конфига
  }
};
```

Для обработки css-файлов мы добавляем цепочку из двух лоадеров: `["style-loader", "css-loader"]`. Лоадеры из массива выполняются от последнего элемента к первому.

## Пример использования

### Файл стилей

Файл `src/components/App.css`:

```css
h1 {
  color: red;
  background-color: aquamarine;
  font-size: 40px;
}

.message {
  color: green;
  background-color: bisque;
  font-size: 20px;
  padding-left: 80px;
}
```

### Компонент приложения

Файл `src/components/App.tsx`:

```react
import "./App.css";  // <-- Импортируем стили, чтобы использовать их в компоненте.

export const App = () => {
  return (
    <div>
      <h1>Hello, world!</h1>
      <p className="message">Раз прислал мне барин чаю и велел его сварить.</p>
    </div>
  )
}
```

# SCSS, SASS, LESS

SASS и SCSS по сути одно и то же, поэтому и для того, и для другого используется один и тот же лоадер. Отличие в том, что в SASS используются пробелы для формирования, а не фигурные скобки и точки запятые. А в SCSS синтаксис полностью совместим с CSS, обычные фигурные скобки, точки с запятыми + несколько дополнительных возможностей.

## Пакеты

Для использования sass потребуется два лоадера:

| Зависимость | Зачем нужен                                  |
| ----------- | -------------------------------------------- |
| sass-loader | Трансформирует sass-файл в обычный css-файл. |
| sass        | Вроде для какой-то интеграции sass'а с Node. |

```
npm i -D sass sass-loader
```

Аналогично, если нужен LESS:

```
npm i -D less less-loader
```

## webpack.config.js

Добавляем лоадеры в конфиг вебпака:

```javascript
module.exports = (settings, argv) => {
  return {
    module: {
      rules: [
        {  // <-- Лоадер для sass-, scss- и css-файлов
          test: /\.(sa|sc|c)ss$/,
          use: ["style-loader", "css-loader", "sass-loader"]
        },
        {  // <-- Лоадер для less-файлов
          test: /\.less$/i,
          use: ["style-loader", "css-loader", "less-loader"],
        }
        // Остальные лоадеры
      ],
    }
    // Остальная часть конфига
  }
};
```

Для обработки sass-файлов мы добавляем цепочку из трех лоадеров: `["style-loader", "css-loader", "sass-loader"]`. Лоадеры из массива выполняются от последнего элемента к первому.

## Пример использования

### Файл стилей

Файл `src/components/App.scss`:

```css
$h1-color: yellow;
$message-color: bisque;

h1 {
  color: red;
  background-color: $h1-color;
  font-size: 40px;
}

.message {
  color: green;
  background-color: $message-color;
  font-size: 20px;
  padding-left: 80px;
}
```

### Компонент приложения

Файл `src/components/App.tsx`:

```react
import "./App.scss";  // <-- Импортируем стили, написанные на SCSS.

export const App = () => {
  return (
    <div>
      <h1>Hello, world!</h1>
      <p className="message">Раз прислал мне барин чаю и велел его сварить.</p>
    </div>
  )
}
```