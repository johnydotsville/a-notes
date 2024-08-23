module.exports = (settings, argv, projectRoot) => {
  return {
    devServer: {
      port: settings.port,
      open: true
    },
    output: {
      filename: '[name].[contenthash].DEV.js',
    }
  }
};