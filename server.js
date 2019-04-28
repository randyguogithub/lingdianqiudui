const express = require('express');
const app = express();
var readFileSync = require('fs').readFileSync
const Renderer = require('./build.js')

var renderer = new Renderer({
  template: readFileSync('./template.html', 'utf-8'),
  maxAge: 60 * 60 * 1000,
  config: {
    basePath: 'https://docsify.js.org/',
    alias: {
      '/de-de/changelog': '/changelog',
      '/zh-cn/changelog': '/changelog',
      '/changelog': 'https://raw.githubusercontent.com/QingWei-Li/docsify/master/CHANGELOG'
    },
    auto2top: true,
    coverpage: true,
    executeScript: true,
    loadSidebar: true,
    loadNavbar: true,
    mergeNavbar: true,
    requestHeaders: {
      'token': 'test'
    },
    maxLevel: 4,
    subMaxLevel: 2,
    name: 'docsify',
    search: {
      noData: {
        '/de-de/': 'Keine Ergebnisse!',
        '/zh-cn/': '没有结果!',
        '/': 'No results!'
      },
      paths: 'auto',
      placeholder: {
        '/de-de/': 'Suche',
        '/zh-cn/': '搜索',
        '/': 'Search'
      }
    }
  }
});
app.use('/lib', express.static('lib'))
app.get('/', function (req, res) {
  renderer.renderToString(req.url).then(html => res.send(html));
});
app.use('/', function (req, res) {
  renderer.renderToString(req.url).then(html => res.send(html));
});
app.listen(3000);