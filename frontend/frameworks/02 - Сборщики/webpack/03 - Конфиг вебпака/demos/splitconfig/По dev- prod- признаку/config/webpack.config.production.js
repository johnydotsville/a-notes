module.exports = (settings, argv, projectRoot) => {
  return {
    output: {
      filename: '[name].[contenthash].PROD.js',
    }
  }
};