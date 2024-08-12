# html-webpack-plugin

Нужен, чтобы автоматически подключать бандл к html-странице при сборке.

# Установка

```
npm i html-webpack-plugin
```

# Использование

В конфиге вебпака `webpack.config.js`:

```javascript
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');  // <-- Импортируем дефолтный класс

const htmlWebpackPlugin = new HtmlWebpackPlugin({  // <-- Создаем экземпляр плагина
  template: path.resolve(__dirname, "./public/index.html")
});

module.exports = {
  ...
  plugins: [
    htmlWebpackPlugin  // <-- Добавляем экземпляр в массив плагинов
  ]
};
```

Настройки:

* `template` - путь до html-файла, к которому надо подключить бандл. Если шаблон не указать, то плагин создаст дефолтную страницу, но мы на нее никак влиять не сможем, поэтому обычно шаблон создается самостоятельно.

