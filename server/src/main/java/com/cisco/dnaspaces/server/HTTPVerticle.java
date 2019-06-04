package com.cisco.dnaspaces.server;

import com.cisco.dnaspaces.consumers.RedisFeeder;
import com.cisco.dnaspaces.utils.ConfigUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTTPVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(HTTPVerticle.class);
    public void start() {
        Integer httpPort = Integer.parseInt(ConfigUtil.getConfig().getProperty("http.port"));
        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                .putHeader("Access-Control-Allow-Origin", "*");
            String mac = req.params().get("mac");
            log.info("mac :: "+mac);
            if(mac != null) {
                sendResponse(req.response(), mac);
            } else {
                sendError(req.response());
            }
        }).listen(httpPort);
    }

    private void sendError(HttpServerResponse response){
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.setStatusCode(400);
        response.end("{'error':'Bad Request'}");
    }

    private void sendResponse(HttpServerResponse response, String mac){
        String key = "DEVICE_LOCATION_UPDATE::"+mac;
        String redisValue = RedisFeeder.getJedis().get(key);
        if(redisValue == null || redisValue.equals(""))
            redisValue = "{}";
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.setStatusCode(200);
        response.end(redisValue);
    }
}