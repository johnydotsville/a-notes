import { ModuleOptions } from "webpack";

export function getLoaders(): ModuleOptions {
  return {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
    ],
  }
}