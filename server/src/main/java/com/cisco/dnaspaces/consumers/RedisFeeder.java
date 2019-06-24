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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisFeeder {

    private static final Logger log = LogManager.getLogger(RedisFeeder.class);
    private static Jedis jedis;

    private static void init(){
        if(jedis == null)
            jedis = new Jedis("localhost");
    }

    public static Jedis getJedis(){
        init();
        return jedis;
    }

    public void accept(JSONObject eventData){
        init();
        String eventType = eventData.getString("eventType");
        if ("DEVICE_LOCATION_UPDATE".equalsIgnoreCase(eventType)) {
            String macAddress = eventData.getJSONObject("deviceLocationUpdate").getJSONObject("device").getString("macAddress");
            log.info("published event to :: DEVICE_LOCATION_UPDATE::"+macAddress);
//            jedis.publish("DEVICE_LOCATION_UPDATE::"+macAddress, eventData.toString());
            SetParams params = new SetParams();
//            params.ex(10);
            jedis.set("DEVICE_LOCATION_UPDATE::"+macAddress, eventData.toString(),params);
        }
    }

}
