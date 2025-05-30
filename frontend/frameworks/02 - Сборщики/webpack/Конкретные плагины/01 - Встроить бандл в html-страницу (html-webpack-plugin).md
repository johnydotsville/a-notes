# html-webpack-plugin

Нужен, чтобы автоматически подключать бандл к html-странице при сборке.

| Ресурс         | Документация                                        |
| -------------- | --------------------------------------------------- |
| webpack.js.org | https://webpack.js.org/plugins/html-webpack-plugin/ |
| npmjs.com      | https://www.npmjs.com/package/html-webpack-plugin   |

# Пакет

```
npm i html-webpack-plugin -D
```

# Использование

## html-шаблон

Нужно создать в любом месте проекта html-файл. Плагин использует этот файл как шаблон, создаст из него конечный html-файл и подключит к нему скомпилированный бандл. Специальных требований к файлу нет, можно разве что добавить в тело `<div id="root"></div>` на случай дальнейшнего использования реакта например.

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title><%= htmlWebpackPlugin.options.title %></title>
</head>
<body>
  <div id="root"></div>
</body>
</html>
```

Конструкции вида `<%= htmlWebpackPlugin.options.СВОЙСТВО %>` позволят вставить в соответствующие места шаблона конкретные значения, переданные плагину.

## Применение плагина

В конфиге вебпака `webpack.config.js` создаем экземпляр плагина и добавляем в секцию плагинов:

```js
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');  // <-- Импортируем плагин.
// import HtmlWebpackPlugin from "html-webpack-plugin";

module.exports = (settings, argv) => {
    
  const htmlWebpack = new HtmlWebpackPlugin({  // <-- Создаем экземпляр плагина и задаем его настройки.
      title: "Hello, webpack!",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
    });

  return {
    mode: argv.mode,
    entry: './src/index.js',
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, 'dist'),
      clean: true
    },
    plugins: [
      htmlWebpack  // <-- Добавляем экземпляр в массив плагинов.
    ]
  }
};
```

## Настройки плагина

* `template` - путь до html-шаблона, к которому надо подключить бандл. Если шаблон не указать, то плагин создаст дефолтную страницу, но мы на нее никак влиять не сможем, поэтому обычно шаблон создается самостоятельно.
* `filename` - имя для html-файла, который получится из html-шаблона и окажется в папке сборки.
* `title` - произвольное свойство. Можно добавлять свойства с любыми именами, а потом в шаблоне обращаться к ним через конструкцию `<%= htmlWebpackPlugin.options.title %>`.

