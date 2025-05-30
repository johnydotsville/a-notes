import { ResolveOptions } from "webpack";

export function getResolver(): ResolveOptions {
  return {
    extensions: [".tsx", ".ts", ".js"]
  }
}