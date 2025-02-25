const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (settings, argv) => {
  const htmlWebpack = new HtmlWebpackPlugin({
      title: "typescript practice",
      template: path.resolve(__dirname, "./public/template.html"),
      filename: "index.html"
    });

  return {
    mode: 'development',
    entry: './src/index.ts',
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'dist'),
        clean: true
    },
    plugins: [
      htmlWebpack
    ],
    devServer: {
      port: settings.port,
      open: true,
      historyApiFallback: true
    },
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
  };
};