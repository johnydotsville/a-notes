# favicon

favicon - это иконка, отображающаяся рядом с именем страницы в табе браузера. TODO: вроде бы формат ico, но скачать готовое такая проблема, что возможно там все какое-то хитрое, а не просто картинка. UPD. Как будто и обычное png работает.

* Создать папку public на уровне с src (все это не обязательно скорее всего, а только чтобы сориентироваться в черновиках)
* Туда закинуть картинку.

Зафигачу полными кусками, ибо некогда разбираться, надо быстрее заканчивать и переделывать на чистовик:

Конфиг вебпака:

```typescript
import webpack from 'webpack';
import path from 'path';
import { buildWebpack } from './config/build/buildWebpack';
import { BuildMode, BuildPaths, BuildPlatform } from './config/build/types/types';

interface EnvVariables {
  mode: BuildMode;
  port: number;
  platform: BuildPlatform;
}

export default (env: EnvVariables) => {
  const paths: BuildPaths = {
    output: path.resolve(__dirname, "build"),
    entry: path.resolve(__dirname, "src", "index.tsx"),
    html: path.resolve(__dirname, "public", "index.html"),
    src: path.resolve(__dirname, "src"),
    public: path.resolve(__dirname, "public"),  // <-- Путь до папки public, где лежит иконка.
  };

  const config: webpack.Configuration = buildWebpack({
    port: env.port ?? 3000,
    mode: env.mode ?? "development",
    paths,
    platform: env.platform ?? "desktop"
  });

  return config;
};
```

Интерфейс, добавляем туда это поле:

```typescript
export interface BuildPaths {
  entry: string;
  html: string;
  output: string;
  src: string;
  public: string;  // <-- Вот оно
}
```

Сама иконка добавляется в плагин HtmlWebpackPlugin:

```typescript
import webpack, { DefinePlugin } from "webpack";
import { Configuration } from "webpack";
import { BuildOptions } from "./types/types";
import HtmlWebpackPlugin from 'html-webpack-plugin';
import MiniCssExtractPlugin from "mini-css-extract-plugin";
import ForkTsCheckerWebpackPlugin from "fork-ts-checker-webpack-plugin";
import ReactRefreshWebpackPlugin from "@pmmmwh/react-refresh-webpack-plugin";
import path from "path";

export function buildPlugins(options: BuildOptions): Configuration["plugins"]{
  const { mode, paths, platform } = options;
  const isDev = mode === "development";
  const isProd = mode === "production";

  const plugins: Configuration["plugins"] = [
    new HtmlWebpackPlugin({
      template: paths.html,
      favicon: path.resolve(paths.public, "favicon.png")  // <-- Вот тут
    }),
    new DefinePlugin({
      __PLATFORM__: JSON.stringify(platform),
    }),
    new ForkTsCheckerWebpackPlugin()
  ];

  if (isDev) {
    plugins.push(
      new webpack.ProgressPlugin(),
      new ReactRefreshWebpackPlugin()
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



# copy-webpack-plugin

Нужен чтобы перемещать в папку со сборкой нужные файлы. Например, какой-нибудь файл с английской версией сообщений \ интерфейса.

```
npm i -D copy-webpack-plugin
```

Вебпак конфиг:

```typescript
import webpack, { DefinePlugin } from "webpack";
import { Configuration } from "webpack";
import { BuildOptions } from "./types/types";
import HtmlWebpackPlugin from 'html-webpack-plugin';
import MiniCssExtractPlugin from "mini-css-extract-plugin";
import ForkTsCheckerWebpackPlugin from "fork-ts-checker-webpack-plugin";
import ReactRefreshWebpackPlugin from "@pmmmwh/react-refresh-webpack-plugin";
import path from "path";
import CopyPlugin from "copy-webpack-plugin";  // <-- Импортируем плагин

export function buildPlugins(options: BuildOptions): Configuration["plugins"]{
  const { mode, paths, platform } = options;
  const isDev = mode === "development";
  const isProd = mode === "production";

  const plugins: Configuration["plugins"] = [
    new HtmlWebpackPlugin({
      template: paths.html,
      favicon: path.resolve(paths.public, "favicon.png")
    }),
    new DefinePlugin({
      __PLATFORM__: JSON.stringify(platform),
    }),
    new ForkTsCheckerWebpackPlugin()
  ];

  if (isDev) {
    plugins.push(
      new webpack.ProgressPlugin(),
      new ReactRefreshWebpackPlugin(),
      new CopyPlugin({  // <-- Используем
        patterns: [
          { 
            from: path.resolve(paths.public, "locales"), 
            to: path.resolve(paths.output, "locales")
          }
        ]
  })
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

