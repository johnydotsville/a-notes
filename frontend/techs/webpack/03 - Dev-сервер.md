# Dev-сервер

Dev-сервер упрощает разработку за счет таких вещей как например:

* Автоматический ребилд.
* Перезагрузка страницы при внесении в код изменений.

| Документация                                                 | Зачем нужна                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [webpack.js.org](https://webpack.js.org/guides/development/#using-webpack-dev-server) | Дока вебпака по dev-серверу.                                 |
| https://webpack.js.org/configuration/dev-server/             |                                                              |
| [stackoverflow](https://stackoverflow.com/a/47302958/23146485) | Интересный ответ с разными опциями.                          |
| [npmjs.com](https://www.npmjs.com/package/webpack-dev-server) | Дока самого dev-сервера. Например, как использовать его вместе с TS. |

# Пакеты

Понадобится пакет:

```
npm i -D webpack-dev-server
```

# Настройка

## Скрипт запуска

Добавляем в конфиг node-проекта `package.json` скрипт запуска проекта на dev-сервере:

```yaml
{
  # Остальная часть конфига
  "scripts": {
    "build:dev":  "webpack --mode=development",
    "build:prod": "webpack --mode=production",
    "start": "webpack serve --mode=development --env port=3000"  # <-- Скрипт запуска dev-сервера.
  }
}

```

## Конфиг вебпака

Для настройки dev-сервера в конфиге вебпака есть отдельная секция `devServer`:

```javascript
module.exports = (settings, argv) => {
  return {
    // <-- Остальная часть конфига
    devServer: {
      port: settings.port,
      open: true
    }
  }
};
```

TODO: Есть вероятность, что не надо отдельно контролировать "не-создание" dev-сервера, потому что он и так не создается, если режим production. Это не точно, но вероятно так и есть, поэтому пока буду исходить из этого. Если выяснится обратное, поправлю.

# Плагины

Обычно еще требуется html-webpack-plugin для генерации html-страницы и подключения бандла к ней. Здесь я не стал это писать, чтобы не дублировать, так что про подключение этого плагина см. конспект по плагинам ("Встроить бандл в html-страницу").

После установки и настройки этого плагина при старте dev-сервера сгенерированная страница откроется в браузере и можно будет посмотреть как работает программа.

# Конфиг dev-сервера на typescript

## Ошибка dev-сервера

После типизации вебпак-конфига может появиться ошибка `'devServer' does not exist in type 'Configuration'`. Самый простой способ избавиться от нее - добавить в конфиг вот такой импорт:

```javascript
import 'webpack-dev-server';
```

В мануале предлагают ставить дополнительный пакет `@types/webpack-dev-server`, но похоже он входит в состав пакета dev-сервера, так что ставить не обязательно. Так же есть и другие махинации с импортом типа, но и без них как будто работает нормально.

## Тип DevServerConfiguration

Тип `DevServerConfiguration` может понадобиться, если мы выносим настройку dev-сервера в отдельную функцию и нам нужно задать ее тип.

```javascript
import type { Configuration as DevServerConfiguration } from "webpack-dev-server";
```

Например:

```javascript
import 'webpack-dev-server';
import type { Configuration as DevServerConfiguration } from "webpack-dev-server";  // <-- Импортируем тип

export default function buildWebpack(settings, argv): webpack.Configuration {
  const { port } = settings;

  const config: webpack.Configuration = {
    // Остальная часть конфига
    devServer: getDevServer(port),
  }

  return config;
};

// <-- Для наглядности, функция в этом же файле, но может конечно быть и в отдельном.
function getDevServer(port: number): DevServerConfiguration {  // <-- Пользуемся типом DevServerConfiguration
  const config: DevServerConfiguration = {
    port: port,
    open: true
  };
  return config;
}
```

Про структуру самого типа писать не буду, потому что пока кроме порта ничем пользоваться не приходилось.







Здесь написать про типизацию самих полей объекта в поле dev-сервер? И мб про отдельную функцию сбора конфига сервера?



Дописать, что надо дополнительно поставить пакет

TODO: проверить, ставится ли автоматически с ним "@types/webpack-dev-server": "^4.7.2", 

UPD. Нет, не ставится.



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

