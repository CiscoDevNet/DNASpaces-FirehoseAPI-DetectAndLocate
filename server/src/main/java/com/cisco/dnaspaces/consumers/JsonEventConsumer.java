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

public class JsonEventConsumer {

    private static final Logger log = LogManager.getLogger(JsonEventConsumer.class);
    private long lastSuccessTimeStamp = -1;
    private RedisFeeder redisFeeder;

    private RocksDBFeeder rocksDBFeeder;

    public long getLastSuccessTimeStamp() {
        return lastSuccessTimeStamp;
    }

    public void setLastSuccessTimeStamp(long lastSuccessTimeStamp) {
        this.lastSuccessTimeStamp = lastSuccessTimeStamp;
    }

    public void accept(JSONObject eventData) {
        boolean isRedisEnabled = Boolean.parseBoolean(ConfigUtil.getConfig().getProperty("redis.feeder.enabled"));
        boolean isRocksDBEnabled = Boolean.parseBoolean(ConfigUtil.getConfig().getProperty("rocksdb.feeder.enabled"));
        String eventType = eventData.getString("eventType");
        log.info("eventType : " + eventType);
        log.trace(eventData.toString());

        if(isRedisEnabled) {
            if(redisFeeder == null)
                redisFeeder = new RedisFeeder();
            redisFeeder.accept(eventData);
        }
        if(isRocksDBEnabled) {
            if(rocksDBFeeder == null)
                rocksDBFeeder = new RocksDBFeeder();
            rocksDBFeeder.accept(eventData);
        }
        this.setLastSuccessTimeStamp(System.currentTimeMillis());

    }

}
