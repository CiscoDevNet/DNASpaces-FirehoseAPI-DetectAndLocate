package com.cisco.dnaspaces.consumers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class JsonEventConsumer {

    private static final Logger log = LogManager.getLogger(JsonEventConsumer.class);
    private long lastSuccessTimeStamp = -1;
    private RedisFeeder redisFeeder;

    public long getLastSuccessTimeStamp() {
        return lastSuccessTimeStamp;
    }

    public void setLastSuccessTimeStamp(long lastSuccessTimeStamp) {
        this.lastSuccessTimeStamp = lastSuccessTimeStamp;
    }

    public void accept(JSONObject eventData) {
        if(redisFeeder == null)
            redisFeeder = new RedisFeeder();
        String eventType = eventData.getString("eventType");
        log.debug("eventType : " + eventType);
        log.trace(eventData.toString());
        redisFeeder.accept(eventData);
        this.setLastSuccessTimeStamp(System.currentTimeMillis());

    }

}
