const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (settings, argv) => {

  const htmlWebpack = new HtmlWebpackPlugin({
      title: "Hello, webpack!",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
    });

  return {
    mode: argv.mode,
    entry: './src/index.js',
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, 'dist'),
      clean: true
    },
    plugins: [
      // new webpack.DefinePlugin({}),
      htmlWebpack
    ],
    devServer: {
      port: settings.port,
      open: true
    },
    module: {
      rules: [
        {  // <-- Лоадер для sass-, scss- и css-файлов
          test: /\.(sa|sc|c)ss$/,
          use: ["style-loader", "css-loader", "sass-loader"]
        },
        {
          test: /\.less$/i,
          use: ["style-loader", "css-loader", "less-loader"],
        }
      ],
    }
  }
};