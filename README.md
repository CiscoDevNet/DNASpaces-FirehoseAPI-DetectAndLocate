# Cisco DNA Spaces Firehose API Sample Application - Detect And Locate

To realise the use case "Detect and Locate", this sample application consumes "Device Location Update" Event of the Cisco DNA Spaces Firehose API. This can be used as a starting point and reference for consuming the Cisco DNA Spaces Firehose API events.

The server consumes "Device Location Update" event and keeps updating Redis cache for each Device MAC address. Server also exposes an HTTP GET API which can be invoked with a MAC address param(mac), to get recent location update for the given mac

Steps to run the server application
1) Clone the Repository
2) Update app.properties file (/server/src/main/resources/app.properties) with appropriate values. All the below mentioned properties are mandatory.
```properties

api.key={{Firehose API Key}}
api.url={{Firehose API URL}}

http.port={{http server port}}

```
3) Build the project by using ```mvn install```.
4) Set the classes folder path in the classspath and execute com.cisco.dnaspaces.APIConsumer class to run the application.


The client application provides an UI to enter MAC address of client and when user clicks on "start polling", Ajax polling starts with a certain time interval to server. On each location update, co-ordinates are stacked to the screen.

Once the server application is started,
1) Hosted server API URL needs to be updated in index.html file.
2) Client UI can be accessed by opening the HTML(index.html) in the browser.
3) By providing the Device MAC Address, Device can be detected and located.
4) By using PROFILE_UDPATE event data, we should be able to associate the device MAC with the User/Tool.
