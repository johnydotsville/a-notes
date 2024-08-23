const { merge } = require('webpack-merge');

module.exports = (settings, argv) => {
  const common = require("./config/webpack.config.common.js");
  const rest = require('./config/webpack.config.' + argv.mode + ".js");

  return merge(
    common(settings, argv, __dirname), 
    rest(settings, argv, __dirname)
  );
};