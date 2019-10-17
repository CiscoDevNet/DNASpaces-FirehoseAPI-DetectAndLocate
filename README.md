# Cisco DNA Spaces Firehose API Sample Application - Detect And Locate

To realise the use case "Detect and Locate", this sample application consumes "Device Location Update" Event of the Cisco DNA Spaces Firehose API. This can be used as a starting point and reference for consuming the Cisco DNA Spaces Firehose API events.

Sample Application consists of 3 components namely
1) API Server
2) Proxy Server
3) Client
4) Kafka Consumer Application

#### Clone the Repository and follow below instructions to run the application.

## 1) API Server
  The API Server consumes "Device Location Update" event and keeps updating Redis cache for each Device MAC address. Server also exposes an HTTP GET API which can be invoked with a MAC address param(mac), to get recent location update for the given mac.
  API server also writes every events into given corressponding Kafka topic(only if Kafka configuration is enable) which can be used by a Kafka client for further processing.

### Steps to run the API Server application
1) Navigate to /server/ folder in the cloned repository.
2) Rename or copy /server/src/main/resources/app.default.properties file to /server/src/main/resources/app.properties and Update app.properties file (/server/src/main/resources/app.properties) with appropriate values. All the below mentioned properties are mandatory.
```properties

api.key={{Firehose API Key}}
api.url={{Firehose API URL}}

http.port={{http server port}}

```
3) Build the project by using ```mvn install```.
4) Set the classes folder path in the classspath and execute ``com.cisco.dnaspaces.APIConsumer`` class to run the application.


## 2) Proxy Server
Proxy Server is used to avoid CORS restriction while using the partners api for retrieving map information and image from client application. This takes the request and communicates with API and serves response to client application without any CORS Restriction

### Steps to run the Proxy Server application
1) Navigate to /client/ folder in the cloned repository.
2) Rename or copy /proxy-server/proxy-server.default.properties file to /proxy-server/proxy-server.properties and update below mentioned properties with appropriate Partners API details
```properties

apiserver.host={{Partners API Host}}
apiserver.apikey={{Partners API Key}}

```
3) In console move to /client directory of project
4) Run command ```npm install```
4) Start the node server using command ```node server```

## 3) Client

The client application provides an UI to enter MAC address of client and when user clicks on "start polling", Ajax polling starts with a certain time interval to server. On each location update, co-ordinates are stacked to the screen.

### Steps to run the Client application
1) Navigate to /client/ folder in the cloned repository.
2) Rename or copy /client/src/environments/environment.default.ts file to /client/src/environments/environment.ts and update below mentioned properties with appropriate Partners API details
```json
  {
  "apiUrl":"{{API Server url}}",
  "serverUrl":"{{Proxy Server url}}"
  }

```
3) In console move to /client directory of project
4) Start the Angular application by using command ```ng serve```

## 4) Kafka Consumer Application

This is a standalone sample application which subscribes to the provided Kafka Topic into which API server pushes the event data. Kafka Consumer Application subscribes to given Kafka Topic and writes every received events to console.

### Steps to run the Consumer application
1) Navigate to /kafka client folder in the cloned repository
2) Execute ``com.cisco.dnaspaces.clients.kafka.Application`` class to run the application.
3) Application will prompt you Kafka specific configurations which you need to enter optionally. Leaving it blank will take default value.

### DEMO
Once all the applications are started,
1) Client UI can be accessed by opening http://localhost:4200 in the browser.
2) By providing the Device MAC Address, Device can be detected and located.
3) Given user current location is plotted in the map using small red dot icon.
4) Given user locatoin updates starting from the time of request are listed below the map

[![published](https://static.production.devnetcloud.com/codeexchange/assets/images/devnet-published.svg)](https://developer.cisco.com/codeexchange/github/repo/CiscoDevNet/DNASpaces-FirehoseAPI-DetectAndLocate)
