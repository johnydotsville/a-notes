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

