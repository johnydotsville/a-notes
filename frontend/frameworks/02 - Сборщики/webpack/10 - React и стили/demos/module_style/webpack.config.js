const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = (settings, argv) => {
  const isDev = argv.mode === "development";

  const htmlWebpack = new HtmlWebpackPlugin({
      title: "Hello, webpack!",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
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
      new MiniCssExtractPlugin()
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
            MiniCssExtractPlugin.loader,
            {
              loader: "css-loader",
              options: {
                modules: {
                  localIdentName: isDev ? "[path][name]__[local]" : "[hash:base64:8]"
                },
              }
            }
          ],
        }
      ],
    },
    resolve: {
      extensions: [".tsx", ".ts", ".js"]
    }
  }
};