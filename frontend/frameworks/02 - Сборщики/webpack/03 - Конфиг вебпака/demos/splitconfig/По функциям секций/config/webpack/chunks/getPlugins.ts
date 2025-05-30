import HtmlWebpackPlugin from 'html-webpack-plugin';
import { WebpackPluginInstance } from "webpack";

export function getPlugins(htmlTemplate: string): WebpackPluginInstance[] {
  const htmlWebpack = new HtmlWebpackPlugin({
    title: "Hello, webpack!",
    template: htmlTemplate,
    filename: "index.html"
  });

  return [
    htmlWebpack
  ]
}