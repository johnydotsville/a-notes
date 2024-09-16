# CopyWebpackPlugin

| Документация                                        |
| --------------------------------------------------- |
| https://webpack.js.org/plugins/copy-webpack-plugin/ |

Плагин нужен, чтобы копировать файлы из директорий с *исходниками* в директории с бандлом. Он не может копировать файлы, которые получаются в процессе билда.

# Использование

## Установка

```
npm i -D copy-webpack-plugin
```

## Применение плагина

В конфиге вебпака `webpack.config.js` создаем экземпляр плагина и добавляем в секцию плагинов:

```javascript
const CopyWebpackPlugin = require("copy-webpack-plugin");  // <-- Node-стиль импорт.
// import CopyWebpackPlugin from "copy-webpack-plugin";  // <-- ES2015-стиль.

module.exports = (settings, argv) => {
  const copyPlugin = new CopyWebpackPlugin({  // <-- Создаем экземпляр плагина.
    patterns: [
      {
        from: "assets/img/heroes",
        to: "assets/img/heroes"
      }
    ]
  });

  return {
    // Остальная часть конфига
    plugins: [
      // Остальные плагины
      copyPlugin  // <-- Добавляем его в секцию плагинов.
    ]
  }
};
```

## Настройки плагина

### from и to

```javascript
new CopyWebpackPlugin({  // <-- Создаем экземпляр плагина.
    patterns: [
      { from: "source", to: "dest" },  // <-- Пишем сколько нужно правил,
      { from: "source", to: "dest" },  // <-- каждое в отдельном объекте.
      { from: "assets/img/heroes", to: "assets/img/heroes" }
    ]
  });
```

Путь во `from` отсчитывается от корневой директории проекта, а путь в `to` - от target-директории, в которую происходит билд. В примере выше папка assets лежит в корне проекта, а target-директория настроена на `dist` по умолчанию. Поэтому копирование произойдет в `dist/assets/img/heroes`.