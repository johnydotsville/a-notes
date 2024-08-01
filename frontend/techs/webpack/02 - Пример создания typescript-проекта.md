# Создание проекта с webpack

Вебпак работает под Node и устанавливается, соответственно, в node-проект.

* Создаем директорию под новый проект, заходим в нее через консоль и создаем пустой node-проект:

  ```
  npm init -y
  ```

  В результате в этой директории появится файл конфига Node-проекта, `package.json`

* Находясь в корневой директории проекта, устанавливаем вебпак:

  ```
  npm i webpack webpack-cli -D
  ```

  * `webpack` - это основная зависимость, собственно сам вебпак.
  * `webpack-cli` - это модуль для работы с вебпаком через консоль.
  * `-D` - это ключ, указывающий на то что эти зависимости нам нужны только для разработки нашей программы. Когда наша программа будет выполняться, эти зависимости ей уже не пригодятся.


* Создаем в корневой директории проекта файл конфигурации вебпака `webpack.config.js`:

  ```javascript
  const path = require('path');
  
  module.exports = {
    mode: "production",
    entry: path.resolve(__dirname, './src/index.ts'),  // <-- Главный файл программы, "точка входа".
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          use: 'ts-loader',
          exclude: /node_modules/,
        },
      ],
    },
    resolve: {
      extensions: ['.tsx', '.ts', '.js'],
    },
    output: {
      filename: 'bundle.js',  // <-- Имя файла ("бандла"), в который все запакуется при сборке проекта.
      path: path.resolve(__dirname, 'dist'),  // <-- Директория, в которую сложить итоговые файлы.
    },
  };
  ```

  Про структуру конфига подробнее в отдельном конспекте.

* Модифицируем конфиг самого Node-проекта (файл `package.json`). Добавляем в секцию scripts скрипт билда, чтобы проект билдился с помощью вебпака:

  ```json
  "scripts": {
    "build": "webpack"
  },
  ```

# Подключаем typescript

* Устанавливаем пакеты для использования typescript:

  ```
  npm i typescript ts-loader -D
  ```

  * `typescript` - это компилятор тайпскрипта. Можно установить его отдельно глобально, а можно для каждого конкретного проекта. В данном случае он устанавливается для конкретного проекта.
  * `ts-loader` - лоадер нужен, чтобы вебпак смог обработать ts-файлы, потому что изначально он умеет обрабатывать только js.


# Подключаем плагины

* Устанавливаем плагины. Например, плагин html-шаблонов `html-webpack-plugin`, который после сборки будет автоматически подключать бандл к странице:

```
npm i html-webpack-plugin -D
```

* Подключаем плагин в конфиге вебпака:

```javascript
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');  // <-- Импортируем класс плагина.

module.exports = {
  ...
  plugins: [  // <-- В секции плагинов создаем и добавляем экземпляр плагина.
    new HtmlWebpackPlugin()
  ]
};
```

* Создадим файлы с кодом, чтобы было что обрабатывать. В корне проекта создадим папку `src` и положим в нее файл `index.ts`. Это "точка входа", главный файл программы, который мы описали в webpack.config.js:

```typescript
window.addEventListener("load", () => {
  const header = document.createElement("h1");
  header.innerText = "Webpack❤️TS"

  const body = document.querySelector("body");
  body.appendChild(header);
})
```

* В папке src создадим папку `templates`, а в ней - файл `index.html`. В него плагин html-webpack-plugin подключит файл скрипта, который получится после сборки ("бандл"):

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Webpack❤️TS</title>
</head>
<body>
  
</body>
</html>
```

# Запускаем билд

Билдить проект будем с помощью скрипта `build`, который мы добавили в файле конфигурации Node-проекта (package.json). Скрипты запускаются командой `npm run имяСкрипта`:

```
npm run build
```

В итоге в папке `/dist` должен появиться файл с бандлом и html-файл. Если запустить этот html-файл, можем убедиться что скрипт сработал.

На этом базовый пример можно считать завершенным.

# Development server



```
npm i webpack-dev-server webpack-merge -D
```

