# Документация

| Источник       | Документация                                                 |
| -------------- | ------------------------------------------------------------ |
| webpack.js.org | https://webpack.js.org/configuration/configuration-languages/ |

# Пакеты

* Если еще не установлены, то устанавливаем сам компилятор ts и лоадер:

  ```
  npm i -D typescript ts-loader
  ```

* Устанавливаем заранее заготовленные ts-типы для вебпака. Для многих известных проектов есть такие типы, созданные в рамках проекта DefinitelyTyped ([ссылка](https://definitelytyped.org/) на офф доку):

  ```
  npm i -D @types/webpack
  ```

  Среди этих типов будет тип для объекта конфига вебпака.

* Устанавливаем ts-node и его типы:

  ```
  npm i -D ts-node @types/node
  ```

  TODO: насколько я понял, ts-node позволяет компилировать и запускать ts-файлы без полного билда проекта. Соответственно, наверное его можно использовать для проверки типов в отдельном процессе. Была там такая тема и плагин. Возможно, без этого пакета они работать не будут. Так что пока я ставить его не буду, но надо иметь ввиду, что он есть. Даже в официальной доке вебпака его ставят при переводе конфига на ts.
  
  Но конкретно для конфига на ts он нужен, похоже, чтобы работала модульная система ES6, т.е. import \ export, а не require.

# tsconfig.json

В ts-конфиг нужно добавить несколько опций:

```yaml
{
  "compilerOptions": {
    "outDir": "./dist/",
    "noImplicitAny": false,
    "module": "ESNext",  # <--
    "allowSyntheticDefaultImports": true,  # <--
    "esModuleInterop": true,  # <--
    "target": "es5",
    "allowJs": true,
    "moduleResolution": "node"
  },
  "ts-node": {  # <--
    "compilerOptions": {
      "module": "CommonJS"
    }
  }
}
```

# webpack.config.ts

## Типизация объекта конфига

* Расширение конфигу вебпака ставим `.ts`

* Чтобы типизировать объект конфига вебпака нам понадобится импортировать модуль `webpack`:

  ```javascript
  import webpack from 'webpack';
  ```

  Внутри него описан интерфейс `Configuration`. Поэтому тип для объекта будет выглядеть как `webpack.Configuration`.

* В качестве альтернативы, можно сделать импорт вот так:

  ```javascript
  import { Configuration } from 'webpack';
  ```

  И в качестве типа писать просто `Configuration`, а не `webpack.Configuration`.

Пример:

```typescript
import path from 'path';  // <-- Теперь можно использовать импорты в ES6-стиле
import HtmlWebpackPlugin from 'html-webpack-plugin';
import webpack from 'webpack';  // <-- Импортируем модуль webpack

// <-- У конфига вебпака тип webpack.Configuration
export default function buildWebpack(settings, argv): webpack.Configuration {

  const htmlWebpack = new HtmlWebpackPlugin({
      title: "Hello, webpack!",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
    });

  const config: webpack.Configuration = {  // <-- Тип объекта конфига вебпака webpack.Configuration
    mode: argv.mode,
    entry: './src/index.ts',
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, 'dist'),
      clean: true
    },
    plugins: [
      htmlWebpack
    ],
    // // <-- в типе webpack.Configuration нет поля под dev-сервер, вызывает ошибку, см. следующий раздел.
    // devServer: {
    //   port: settings.port,
    //   open: true
    // },
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
      extensions: [".tsx", ".ts", ".js"]
    }
  }

  return config;
};
```



## Ошибка с dev-сервером

После типизации вебпак-конфига может появиться ошибка `'devServer' does not exist in type 'Configuration'`. Самый простой способ избавиться от нее - добавить в конфиг вот такой импорт:

```javascript
import 'webpack-dev-server';
```

В мануале предлагают ставить дополнительный пакет `@types/webpack-dev-server`, но похоже он входит в состав пакета dev-сервера, так что ставить не обязательно. Так же есть и другие махинации с импортом типа, но и без них как будто работает нормально.

P.S. Про типизацию самого поля dev-сервера и вынос его настроек в отдельную функцию см. конспект по самому dev-серверу.

# Типизация секций конфига

## Два способа указать тип для секции

Конфиг вебпака состоит из разных секций, вроде `plugins`, `module` и т.д. У каждой из них тоже есть тип. Знать эти типы понадобится, когда мы разделяем конфиг на части. Например, делаем отдельную функцию, которая возвращает набор плагинов, отдельную функцию для лоадеров и т.д. И нам потребуется указать тип этих функций.

Посмотреть эти типы можно в IDE, если щелкнуть через Ctrl + ЛКМ и выбрать `types.d.ts node_modules\webpack`. Там описан интерфейс `interface Configuration`. Какие-то его поля представлены простыми типами, а какие-то тоже являются интерфейсами. Они описаны в этом же файле.

Обращаться к типу можно двумя способами. На примере поля `module`:

* `Configuration["mode"]` - такой способ в основном используется для полей, у которых нет отдельного типа. Например, значение поля `mode` - это просто строка. Его тип - объединение нескольких строк, выглядит вот так `mode?: "none" | "development" | "production";`. Т.е. для поля mode не определен отдельный интерфейс. Так что для него и подобных полей `Configuration["поле"]` является единственным способом указать тип.

  ```javascript
  import { Configuration } from 'webpack';
  let foo: Configuration["mode"] = "development";
  ```

* `ModuleOptions` - под тип поля module есть отдельный интерфейс, который называется ModuleOptions, поэтому можно использовать его. Но все еще можно обратиться как `Configuration["module"]`. Это полезно, потому что почему-то не для всех полей, у которых есть собственный тип, этот тип доступен для импорта. Например, для поля `output` есть интерфейс `Output`, но при попытке его импортировать `import { Output } from 'webpack';` возникает ошибка. Поэтому `Configuration["output"]`  является единственным способом сослаться на тип.

  ```javascript
  import { ModuleOptions } from "webpack";
  const mops: ModuleOptions = {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/
      },
    ];
  }
  ```

## Секции и их типы

### mode

```javascript
import { Configuration } from 'webpack';
```

```javascript
function getMode(): Configuration["mode"] {
  return "development";
}
```

### entry

```javascript
import { Configuration } from 'webpack';
```

```javascript
function getEntry(): Configuration["entry"] {
  return './src/index.ts';
}
```

### output

```javascript
import { Configuration } from 'webpack';
// import { Output } from 'webpack';  // <-- Почему-то не импортируется
```

```javascript
function getOutput(): Configuration["output"] {
  return {
    filename: '[name].[contenthash].js',
    path: path.resolve(__dirname, 'dist'),
    clean: true
  }
}
```

### plugins

```javascript
import { WebpackPluginInstance } from "webpack";
```

```javascript
function getPlugins(): WebpackPluginInstance[] {
  const htmlWebpack = new HtmlWebpackPlugin({
    title: "Hello, webpack!",
    template: path.resolve(__dirname, "./public/template.html"),
    filename: "index.html"
  });

  return [
    htmlWebpack
  ];
}
```

### module

```javascript
import { ModuleOptions } from "webpack";
```

```javascript
function getModule(): ModuleOptions {
  const rulesList: RuleSetRule[] =  [
    {
      test: /\.tsx?$/,
      use: 'ts-loader',
      exclude: /node_modules/
    },
  ];

  return {
    rules: rulesList
  }
}
```

### resolve

```javascript
import { ResolveOptions } from "webpack";
```

```javascript
function getResolve(): ResolveOptions {
  const extensionsList: ResolveOptions["extensions"] = [".tsx", ".ts", ".js"];

  return {
    extensions: extensionsList
  }
}
```

### devServer

См. конспект по dev-серверу.

