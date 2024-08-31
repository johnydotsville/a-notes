Горячая перезагрузка модулей нужна, чтобы при изменениях в проекте страница не перезагружалась. Это позволит, например, править какую-то логику в проекте и не терять введенные в форму данные или не сбрасывать состояние реакта.

Без использования фреймворков, вроде react, достаточно просто поставить опцию для dev-сервера:

[Документация](https://webpack.js.org/concepts/hot-module-replacement/)

```typescript
export function buildDevServer(options: BuildOptions): DevServerConfiguration {
  return {
    port: options.port,
    open: true,
    historyApiFallback: true,
    hot: true  // <-- Горячая перезагрузка модулей.
  }
}
```

TODO: переделать на монолитный конфиг.

При использовании реакта потребуются плагины:

```
npm i @pmmmwh/react-refresh-webpack-plugin
```

```
npm i react-refresh-typescript
```



Добавляем плагин в конфиг вебпака: TODO: переделать в монолитный

```typescript
import webpack, { DefinePlugin } from "webpack";
import { Configuration } from "webpack";
import { BuildOptions } from "./types/types";
import HtmlWebpackPlugin from 'html-webpack-plugin';
import MiniCssExtractPlugin from "mini-css-extract-plugin";
import ForkTsCheckerWebpackPlugin from "fork-ts-checker-webpack-plugin";
import ReactRefreshWebpackPlugin from "@pmmmwh/react-refresh-webpack-plugin";  // <-- Раз

export function buildPlugins(options: BuildOptions): Configuration["plugins"]{
  const { mode, paths, platform } = options;
  const isDev = mode === "development";
  const isProd = mode === "production";

  const plugins: Configuration["plugins"] = [
    new HtmlWebpackPlugin({
      template: paths.html
    }),
    new DefinePlugin({
      __PLATFORM__: JSON.stringify(platform),
    }),
    new ForkTsCheckerWebpackPlugin()
  ];

  if (isDev) {
    plugins.push(
      new webpack.ProgressPlugin(),
      new ReactRefreshWebpackPlugin()  // <-- Два
    );
  }

  if (isProd) {
    plugins.push(
      new MiniCssExtractPlugin({
        filename: "css/[name].[contenthash:8].css",
        chunkFilename: "css/[name].[contenthash:8].css"
      }),
    );
  }

  return plugins;
}
```

И правим ts-лоадер:

```typescript
import ReactRefreshTypescript from "react-refresh-typescript";

...

const tsLoader = {
    test: /\.tsx?$/,
    exclude: /node_modules/,
    use: [
      {
        loader: 'ts-loader',
        options: {
          transpileOnly: true,
          getCustomTransformers: () => ({  // <-- Вот это надо.
            before: [isDev && ReactRefreshTypescript()].filter(Boolean),
          })
        }
      }
    ]
  };
```

