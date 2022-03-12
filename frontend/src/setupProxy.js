const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
      target: "http://localhost:3001",
      changeOrigin: true,
    })
  );

  app.use(
    "/apiNone",
    createProxyMiddleware({
      target: "http://localhost:3032",
      changeOrigin: true,
    })
  );
};
