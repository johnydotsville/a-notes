const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (settings, argv) => {
  const htmlWebpack = new HtmlWebpackPlugin({
      title: "Hello, webpack!",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
    });

  return {
    mode: 'development',
    entry: './src/index.js',
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'dist'),
        clean: true
    },
    plugins: [
      htmlWebpack
    ]
  };
};