import { Configuration } from 'webpack';
import { ScriptEnvironment } from "./types/ScriptEnvironment";
import { ScriptFlags } from "./types/ScriptFlags";
import { Paths } from "./types/Paths";

import { getPlugins   } from "./chunks/getPlugins";
import { getDevServer } from "./chunks/getDevServer";
import { getLoaders   } from "./chunks/getLoaders";
import { getResolver  } from "./chunks/getResolver";

export function getWebpackConfig(settings: ScriptEnvironment, 
                                 argv: ScriptFlags,
                                 paths: Paths
                                ): Configuration {
  return {
    mode:  argv.mode,
    entry: paths.entryFile,
    output: {
      filename: '[name].[contenthash].js',
      path: paths.outputDir,
      clean: true
    },
    plugins: getPlugins(paths.htmlTemplate),
    devServer: getDevServer(settings.port),
    module: getLoaders(),
    resolve: getResolver()
  };
}