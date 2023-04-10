package com.cisco.dnaspaces.consumers;
import com.cisco.dnaspaces.utils.ConfigUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class RocksDBFeeder {
    private static final Logger log = LogManager.getLogger(RocksDBFeeder.class);
    private static RocksDB rocksDBFeeder;

    private static void init() {
        try {
            Path dbPath;
            File dbDir;
            RocksDB.loadLibrary();
            dbPath = Paths.get(ConfigUtil.getConfig().getProperty("rocksdb.store.path"));
            dbDir = dbPath.toFile();
            if(dbDir.exists()) {
                FileUtils.deleteDirectory(dbDir);
            }
            dbDir.mkdirs();
            rocksDBFeeder = RocksDB.open(dbPath.toString());
        } catch (RocksDBException rocksDBException) {
            log.error("Exception occurred on rocksdb ");
            rocksDBException.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static  RocksDB getRocksDB() {
        if(rocksDBFeeder == null)
            init();
        return rocksDBFeeder;
    }

    public void accept(JSONObject eventData) {
        if(rocksDBFeeder == null)
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
                boolean isActive = eventData.getJSONObject("devicePresence").getBoolean("wasInActive");
                if(!isActive) {
                    String macAddress = eventData.getJSONObject("devicePresence").getJSONObject("device").getString("macAddress");
                    log.debug("published event to RocksDB:: DEVICE_PRESENCE::"+macAddress + " status: false");
                    delete("DEVICE_LOCATION_UPDATE::"+macAddress);
                }
            }
        } catch (Exception exception) {
            log.error("Error on rocksdb accept", exception);

        }
    }

    public void write(String key, String value) {
        try {
            rocksDBFeeder.put(ConfigUtil.toByteArray(key), ConfigUtil.toByteArray(value));
        } catch (RocksDBException rocksDBException) {
            log.error("Error on writing to rocksdb", rocksDBException);
        }
    }

    public void delete(String key) {
        try {
            rocksDBFeeder.delete(ConfigUtil.toByteArray(key));
        } catch (RocksDBException rocksDBException) {
            log.error("Error on deletion in rocksdb", rocksDBException);
        }
    }

    public static String read(String key) {
        try {
            byte [] value = rocksDBFeeder.get(ConfigUtil.toByteArray(key));
            if(value != null) {
                return ConfigUtil.toString(value);
            } else {
                return null;
            }
        } catch (RocksDBException rocksDBException) {
            log.error("Error on read from rocksdb", rocksDBException);
            return null;
        }
    }
}
