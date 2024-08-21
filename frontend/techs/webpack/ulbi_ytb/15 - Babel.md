Альтернативы:

* swc-loader
* esbuild

TODO: загуглить, что это за инструменты.

Babel - это компилятор JS, который позволяет использовать самые новые возможности JS, компилировать TS и не беспокоиться насчет того, в каком браузере это все будет запускаться.

[Документация](babeljs.io)

Babel - в тренировочном проекте будем использоваться вместо ts-loader

[Документация по пресетам](https://babeljs.io/docs/presets)

# Пакеты

```
npm i -D @babel/core babel-loader
```

Конфиг вебпака, лоадеры TODO: сделать на монолитный конфиг

```typescript
import { ModuleOptions } from "webpack";
import { BuildOptions } from "./types/types";
import MiniCssExtractPlugin from "mini-css-extract-plugin";
import ReactRefreshTypescript from "react-refresh-typescript";

export function buildLoaders(options: BuildOptions): ModuleOptions["rules"] {
  const isDev = options.mode === "development";

  const cssLoaderWithModules = {
    loader: "css-loader",
    options: {
      modules: {
        localIdentName: isDev ? "[path][name]__[local]" : "[hash:base64:8]"
      },
    },
  }

  const assetLoader = {
    test: /\.(png|jpg|jpeg|gif)$/i,
    type: 'asset/resource',
  };

  const svgLoader = {
    test: /\.svg$/i,
    issuer: /\.[jt]sx?$/,
    use: [
      { 
        loader: '@svgr/webpack',
        options: { 
          icon: true,
          svgoConfig: {
            plugins: [
              {
                name: "convertColors",
                params: {
                  currentColor: true
                }
              }
            ]
          }
        }
      }
    ],
  };

  const scssLoader = {
    test: /\.s[ac]ss$/i,
    use: [
      // Creates `style` nodes from JS strings
      isDev ? "style-loader" : MiniCssExtractPlugin.loader,
      // Translates CSS into CommonJS
      cssLoaderWithModules,
      // Compiles Sass to CSS
      "sass-loader",
    ],
  };

  const tsLoader = {
    test: /\.tsx?$/,
    exclude: /node_modules/,
    use: [
      {
        loader: 'ts-loader',
        options: {
          transpileOnly: true,
          getCustomTransformers: () => ({
            before: [isDev && ReactRefreshTypescript()].filter(Boolean),
          })
        }
      }
    ]
  };

  const babelLoader = {  // <-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    test: /\.tsx?$/,
    exclude: /node_modules/,
    use: {
      loader: "babel-loader",
      options: {
        presets: [
          '@babel/preset-env',
          "@babel/preset-typescript",
          [
            "@babel/preset-react", 
            {
              runtime: "automatic"
            }
          ]
        ]
      }
    }
  };

  return [
    svgLoader,
    assetLoader,
    scssLoader,
    // tsLoader,
    babelLoader  // <-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  ];
}
```

P.S. В доке используется файл babel.config.json, но поскольку у нас webpack, то пресеты добавляются в его конфиг, как в примере выше. Но можно создать отдельный файл `babel.config.json`, положить его рядом с конфигом вебпака и оформить настройки в нем, а вебпак его умеет искать автоматически. Это может быть полезно, когда эти настройки бейбеля используются и другими инструментами и поэтому выгоднее, чтобы они лежали отдельно:

```yaml
{
  "presets": [
    "@babel/preset-env",
    "@babel/preset-typescript",
    [
      "@babel/preset-react", 
      {
        "runtime": "automatic"
      }
    ]
  ]
}
```

P.S. Тут потребуется поставить дополнительные кавычки для свойств, поскольку это json.

# Настройка TS

Нужно установить пакет с пресетом

```
npm i -D @babel/preset-typescript
```



# Настройка react

Пресеты

```
npm i -D @babel/preset-react
```







# Собственный плагин

Примерно с 2:26:00 https://www.youtube.com/watch?v=acAH2_YT6bs

