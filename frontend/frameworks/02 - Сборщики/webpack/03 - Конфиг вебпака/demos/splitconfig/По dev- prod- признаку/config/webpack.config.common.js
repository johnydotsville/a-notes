const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (settings, argv, projectRoot) => {
  const htmlWebpack = new HtmlWebpackPlugin({
      title: "Hello, webpack!",
      template: path.resolve(projectRoot, "public/template.html"),
      filename: "index.html"
    });

  return {
    mode: argv.mode,
    entry: path.resolve(projectRoot, 'src/index.js'),
    output: {
      path: path.resolve(projectRoot, 'dist'),
      clean: true
    },
    plugins: [
      htmlWebpack
    ]
  }
};