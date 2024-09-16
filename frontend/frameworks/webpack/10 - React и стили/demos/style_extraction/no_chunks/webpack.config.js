const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = (settings, argv) => {
  const isDev = argv.mode === "development";

  const htmlWebpack = new HtmlWebpackPlugin({
      title: "Hello, webpack!",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
    });

  const miniCss = new MiniCssExtractPlugin({
      filename: "css/[name].[contenthash:8].entry.css",  // <-- Для файла точки входа.
      chunkFilename: "css/[name].[contenthash:8].css"  // <-- Для остальных файлов.
    });

  return {
    mode: argv.mode,
    entry: './src/index.tsx',
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, 'dist'),
      clean: true
    },
    plugins: [
      htmlWebpack,
      miniCss
    ],
    devServer: {
      port: settings.port,
      open: true,
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          use: 'ts-loader',
          exclude: /node_modules/,
        },
        {
          test: /\.css$/i,
          use: [
            //"style-loader",
            MiniCssExtractPlugin.loader,
            "css-loader"
          ],
        }
      ],
    },
    resolve: {
      extensions: [".tsx", ".ts", ".js"]
    }
  }
};