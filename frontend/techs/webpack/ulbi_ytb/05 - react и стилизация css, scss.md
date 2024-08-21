# React

## Пакеты

```
npm i react react-dom
```

```
npm i -D @types/react @types/react-dom
```

P.S. Это, похоже, типы для конфига вебпака.

## Файлы

Файлы должны иметь расширение `tsx` (react + typescript). ts-loader умеет работать с jsx, поэтому ставить дополнительные лоадеры не надо, если используется связка typescript + react.

## Исходники

Компонент приложения:

```react
import React from "react";

export const App = () => {
  return (
    <div>
      Hello, world!
    </div>
  )
}
```

Файл `index.tsx`:

```typescript
import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from "./components/App";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <App />
);
```

В конфиге вебпака `webpack.config.ts` не забываем поправить точку входа:

```yaml
entry: path.resolve(__dirname, './src/index.tsx')
```

Еще может понадобиться поменять в ts-конфиге `tsconfig.json` одну опцию:

```yaml
{
  "compilerOptions": {
    "jsx": "react-jsx",  # Чтобы можно было не импортировать React в файлах с исходниками.
    # Прочие опции
  }
}
```

# Стилизация

При сборке стили могут встраиваться в файл с исходным кодом, а могут формироваться в виде отдельного файла. TODO: поподробнее загуглить и расписать. Например, тут уже я увидел, как плагин может извлечь стили в отдельный файл. Разобраться, какой лоадер \ или что-то отвечает исходно за встраивание стилей в код.

## Обычный CSS

| Зависимость  | Документация                                                 | Зачем нужна |
| ------------ | ------------------------------------------------------------ | ----------- |
| style-loader | [Документация](https://webpack.js.org/loaders/style-loader/) | TODO        |
| css-loader   | [Документация](https://webpack.js.org/loaders/css-loader/)   | TODO        |

```
npm i -D css-loader
```

```
npm i -D style-loader
```

В конфиг вебпака добавляем эти лоадеры:

```typescript
const config: webpack.Configuration = {
  // Остальные параметры конфига
  module: {
    rules: [
      {  // <-- Правило для поиска файлов со стилями.
        test: /\.css$/i,
        use: ["style-loader", "css-loader"],
      },
      // Остальные правила для других файлов.
    ],
  }
}
```

TODO: массив, в нем важен порядок. Как именно?

Файл со стилями `App.css` (продолжая пример выше, из раздела про реакт)

```css
p {
  color: red;
  background-color: aquamarine;
  font-size: 40px;
}
```

Файл с компонентом:

```react
import React from "react";
import "./App.css";  // <-- Импортируем файл со стилями.

export const App = () => {
  return (
    <div>
      <p>Hello, world!</p>
    </div>
  )
}
```

## Препроцессоры, SCSS

| Зависимость | Документация                                                | Зачем нужен |
| ----------- | ----------------------------------------------------------- | ----------- |
| sass-loader | [Документация](https://webpack.js.org/loaders/sass-loader/) | TODO        |
| sass        |                                                             | TODO        |

```
npm i -D sass sass-loader
```

Добавляем лодер в конфиг вебпака:

```typescript
const config: webpack.Configuration = {
  // Остальные параметры конфига
  module: {
    rules: [
      {  // <-- Правило для поиска препроцессорных файлов стилей.
        test: /\.s[ac]ss$/i,
        use: [  // <-- Последовательность важна. Идет от конца к началу. SASS > CSS > strings
          // Creates `style` nodes from JS strings
          "style-loader",
          // Translates CSS into CommonJS
          "css-loader",
          // Compiles Sass to CSS
          "sass-loader",
        ],
      },
      // Остальные правила для других файлов.
    ]
  }
}
```

## Плагины

### MiniCssExtractPlugin

| Плагин               | Документация                                                 | Зачем нужен                                                  |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| MiniCssExtractPlugin | [Документация](https://webpack.js.org/plugins/mini-css-extract-plugin/) | При сборке проекта оформляет стили в отдельный файл, вместо того, чтобы встраивать их в виде строк в исходный код бандла. Файл появляется в директории рядом с бандлом и подключается в html. |

```
npm i -D mini-css-extract-plugin
```

TODO: а зачем вообще извлекать в отдельный файл стили?

Создаем в конфиге вебпака экземпляр плагина и добавляем в массив плагинов:

```typescript
import MiniCssExtractPlugin from "mini-css-extract-plugin";
...
const config: webpack.Configuration = {
  // Остальные параметры конфига.
  module: {
    rules: [
      {
        test: /\.s[ac]ss$/i,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
          "sass-loader",
        ],
      },
      // Остальные правила для других файлов.
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "css/[name].[contenthash:8].css",
      chunkFilename: "css/[name].[contenthash:8].css"
    }),
    // Остальные плагины
  ]
}
```

P.S. Здесь использован фрагмент конфига из сквозного примера, из раздела выше. Если sass-препроцессор не используется, то здесь например не нужен будет в этом массиве sass-loader. TODO: так что возможно переписать этот фрагмент потом более нейтрально, без sass.