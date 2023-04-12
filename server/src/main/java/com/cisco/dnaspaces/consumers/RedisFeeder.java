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

import com.cisco.dnaspaces.utils.ConfigUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.rocksdb.RocksDBException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisFeeder {

    private static final Logger log = LogManager.getLogger(RedisFeeder.class);
    private static Jedis jedis;

    private static void init(){
        try {
            String redisHost = ConfigUtil.getConfig().getProperty("redis.host");
            int redisPort =  Integer.parseInt(ConfigUtil.getConfig().getProperty("redis.port"));
            if(redisHost!= null && !redisHost.isEmpty()) {
                if(jedis == null) {
                    jedis = new Jedis(redisHost, redisPort);
                }
            } else {
                log.error("Please check redis host/port");
            }
        } catch (Exception exception) {
            log.error("Exception occurred on making redis connection");
        }
    }

    public static Jedis getJedis(){
        if(jedis == null)
            init();
        return jedis;
    }

    public void accept(JSONObject eventData){
        if(jedis == null)
            init();
        try {
            String eventType = eventData.getString("eventType");
            if ("DEVICE_LOCATION_UPDATE".equalsIgnoreCase(eventType)) {
                String macAddress = eventData.getJSONObject("deviceLocationUpdate").getJSONObject("device").getString("macAddress");
                log.debug("published event to RocksDB:: DEVICE_LOCATION_UPDATE::"+macAddress);
                write("DEVICE_LOCATION_UPDATE::"+macAddress, eventData.toString());
            } else if("DEVICE_EXIT".equalsIgnoreCase(eventType)) {
                String macAddress = eventData.getJSONObject("deviceExit").getJSONObject("device").getString("macAddress");
                log.debug("published event to RocksDB:: DEVICE_EXIT::"+macAddress);
                delete("DEVICE_LOCATION_UPDATE::"+macAddress);
            } else if("DEVICE_PRESENCE".equalsIgnoreCase(eventType)) {
                String presenceEventType = eventData.getJSONObject("devicePresence").getString("presenceEventType");
                if(presenceEventType.equalsIgnoreCase("DEVICE_EXIT_EVENT")) {
                    String macAddress = eventData.getJSONObject("devicePresence").getJSONObject("device").getString("macAddress");
                    log.debug("published event to RocksDB:: DEVICE_PRESENCE::"+macAddress + " status: false");
                    delete("DEVICE_LOCATION_UPDATE::"+macAddress);
                }
            }
        } catch (Exception exception) {
            log.error("Error on redis accept", exception);

        }
    }


    public void write(String key, String value) {
            jedis.set(ConfigUtil.toByteArray(key), ConfigUtil.toByteArray(value));
    }

    public void delete(String key) {
            jedis.del(ConfigUtil.toByteArray(key));
    }

    public static String read(String key) {
        try {
            byte [] value = jedis.get(ConfigUtil.toByteArray(key));
            if(value != null) {
                return ConfigUtil.toString(value);
            } else {
                return null;
            }
        } catch (Exception exception) {
            log.error("Error on read from redis", exception);
            return null;
        }
    }

}
