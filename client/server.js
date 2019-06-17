'use strict';

// BASE SETUP
// =============================================================================
// call the packages we need
let express = require('express'); // calling express
let app = express(); // define app using express
let bodyParser = require('body-parser');
let path = require('path');
var helmet = require('helmet');
var port = process.env.PORT || 9090; // set our port

global.appRoot = path.resolve(__dirname);
global.appPort = port;

app.use(express.static(path.join(appRoot, 'dist')));
app.use(helmet());
// configure app to use bodyParser()
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended : true
}));


// ROUTES FOR API
app.use(require('./proxy-server'))

// START THE SERVER
// =============================================================================

app.listen(port, function() {
	console.log('Server started listening on port ' + port + '\n Please wait for ng to start');
})