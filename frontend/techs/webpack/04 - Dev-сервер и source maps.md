# Dev-сервер

## Зачем нужен

Dev-сервер упрощает разработку за счет таких вещей как например автоматический ребилд и перезагрузка страницы при внесении в код изменений.

| Документация                                                 | Зачем нужна                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [webpack.js.org](https://webpack.js.org/guides/development/#using-webpack-dev-server) | Дока вебпака по dev-серверу.                                 |
| [npmjs.com](https://www.npmjs.com/package/webpack-dev-server) | Дока самого dev-сервера. Например, как использовать его вместе с TS. |

## Установка

Понадобится пакет:

```
npm i -D webpack-dev-server
```

TODO: проверить, ставится ли автоматически с ним "@types/webpack-dev-server": "^4.7.2",

## Настройка

### Конфиг вебпака

Настройка конфига вебпака `webpack.config.ts`:

```typescript
import path from 'path';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import webpack from 'webpack';
import type { Configuration as DevServerConfiguration } from "webpack-dev-server";  // <-- Конфиг видел поле devServer

type Mode = "development" | "production";

interface EnvVariables {
  mode: Mode;
  port: number;
}

export default (env: EnvVariables) => {
  const isDev = env.mode === "development";
    
  const config: webpack.Configuration = {
    // Остальная часть конфига
    devServer: isDev ? {  // <-- Если режим не development, то devServer не нужен.
      port: env.port ?? 3000,  // <-- Если порт не укажут при запуске, будет 3000 по умолчанию.
      open: true
    } : undefined
  };

  return config;
};
```

Поскольку мы используем ts и переменная конфига типизирована, то каждое поле должно быть известно. Для этого у конфига должен быть специальный тип Configuration:

```typescript
import type { Configuration as DevServerConfiguration } from "webpack-dev-server";
```

TODO: но ведь используется как будто не он, а тот же webpack.Configuration, но без этого импорта ругается, а с импортом - нет. Почему?

### Скрипт запуска

Теперь надо добавить в конфиг нод-проекта `package.json` скрипт для запуска dev-сервера:

```yaml
{
  # Прочие секции конфига
  "scripts": {
    "build:dev": "webpack --env mode=development",
    "build:prod": "webpack --env mode=production",
    "start": "webpack serve --env port=5000 mode=development"  # <-- Добавляем скрипт запуска dev-сервера.
  }
}
```

При желании использовать другой порт, не обязательно править конфиг, можно подставить любой при вызове скрипта:

```
npm start --env port=7000
```

# Source maps

Первое упоминание ровно на 40 минуте. https://www.youtube.com/watch?v=acAH2_YT6bs

Аффтар говорит, что позже вернется подробнее, поэтому в этом моменте не рассказывает подробно. Оставляю тайминг на случай, если придется все-таки вернуться в этот момент.

В целом, сорс мапы нужны для отладки. Поскольку после сборки весь код сливается в один файл, то при возникновении ошибки во время выполнения не понятно, в каком именно исходном файле она произошла. Вот для этого сопоставления результирующего кода и исходных файлов и нужны сорс мапы.