/*
 * Copyright (c) 2019 Cisco Systems, Inc. and/or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
