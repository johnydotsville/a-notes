# ProgressPlugin

Используется, чтобы в процессе сборки показывать процент завершенности. Не рекомендуется использовать в production-режиме, потому что может сильно замедлять сборку.

# Установка

Устанавливать отдельно его не  надо, он является частью вебпака.

# Использование

В конфиге вебпака `webpack.config.js` создаем экземпляр плагина и добавляем в секцию плагинов:

```javascript
const path = require('path');
const webpack = require('webpack');  // <-- 1

const progressPlugin = new webpack.ProgressPlugin();  // <-- 2

module.exports =  {
  // ...
  plugins: [
    progressPlugin  // <-- 3
  ]
};
```

