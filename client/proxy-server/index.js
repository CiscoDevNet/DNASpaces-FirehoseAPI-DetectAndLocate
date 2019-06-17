var express = require('express'),
app = express(),
fs = require('fs'),
router = express.Router(),
PropertiesReader = require('properties-reader'),
proxy = require('express-http-proxy'),
path = require('path'),
urlResolver = require('url'),
properties = PropertiesReader(appRoot + '/proxy-server/proxy-server.properties'),
url = properties.get('serverUrls.apiserver');

router.options('*',function(req, res, next) {
        // Website you wish to allow to connect
        res.setHeader('Access-Control-Allow-Origin', '*');
    
        // Request methods you wish to allow
        res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
    
        // Request headers you wish to allow
        res.setHeader('Access-Control-Allow-Headers', '*');
    
        // Set to true if you need the website to include cookies in the requests sent
        // to the API (e.g. in case you use sessions)
        res.setHeader('Access-Control-Allow-Credentials', true);
    
        res.status(200).end();
        // write your callback code here.
      });
router.use('/api/partners/v1/maps/6d22e37b407cf201500b23f17ce45054/image',function(req, res, next) {
        res.setHeader('Access-Control-Allow-Origin', '*');

        fs.readFile('/cisco/projects/FirehoseAPI-Sample-DetectAndLocate/client/src/assets/images/floormap-csgmeraki.jpeg', function (err, content) {
                if (err) {
                        res.writeHead(400, {'Content-type':'text/html'})
                        console.log(err);
                        res.end("No such image");    
                } else {
                        //specify the content type in the response will be an image
                        res.writeHead(200,{'Content-type':'image/jpeg'});
                        res.end(content);
                }
        });
        // write your callback code here.
      });
router.use('/api/partners/v1/maps/6d22e37b407cf201500b23f17ce45054', function(req, res, next) {
        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Content-Type', 'application/json');
        res.status(200).json({
                "mapId": "6d22e37b407cf201500b23f17ce45054",
                "imageWidth":2304,
                "imageHeight":1268
                });
        // write your callback code here.
      });

router.use('/', proxy(properties.get('serverUrls.apiserver.host'),{
        proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
          // you can update/add headers
          proxyReqOpts.headers['X-API-Key'] = properties.get('serverUrls.apiserver.apikey');
          return proxyReqOpts;
        },
        userResHeaderDecorator(headers, userReq, userRes, proxyReq, proxyRes) {
                // recieves an Object of headers, returns an Object of headers.
                headers["Access-Control-Allow-Origin"] = "*";
                return headers;
        }
}));
module.exports = router