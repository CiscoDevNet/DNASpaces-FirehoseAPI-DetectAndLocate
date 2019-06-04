package com.cisco.dnaspaces.client;

import com.cisco.dnaspaces.utils.ConfigUtil;
import redis.clients.jedis.Jedis;

public class RedisClient {

    private static Jedis jedis;

    private static void init(){
        String host = ConfigUtil.getConfig().getProperty("redis.host");
        jedis = new Jedis(host);
    }

    public static Jedis getJedis(){
        if(jedis == null)
            init();
        return jedis;
    }
}
