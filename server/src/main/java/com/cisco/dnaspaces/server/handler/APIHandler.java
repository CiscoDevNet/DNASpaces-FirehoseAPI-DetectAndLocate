package com.cisco.dnaspaces.server.handler;

import com.cisco.dnaspaces.consumers.RedisFeeder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APIHandler {
    private static final Logger log = LogManager.getLogger(APIHandler.class);

    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        router.get("/findmac").handler(this::handleDetectAndLocate);

        return router;
    }

    private void handleDetectAndLocate(RoutingContext routingContext){
        String mac = routingContext.request().params().get("mac");
        HttpServerResponse response = routingContext.response();
        response.putHeader("Access-Control-Allow-Origin", "*");
        log.info("mac :: "+mac);
        if(mac != null) {
            String key = "DEVICE_LOCATION_UPDATE::"+mac;
            String redisValue = RedisFeeder.getJedis().get(key);
            if(redisValue == null || redisValue.equals(""))
                redisValue = "{}";
            response.putHeader("content-type", "application/json; charset=utf-8");
            response.setStatusCode(200);
            response.end(redisValue);
        } else {
            sendError(response);
        }
    }

    private void sendError(HttpServerResponse response){
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.setStatusCode(400);
        response.end("{'error':'Bad Request'}");
    }

}
