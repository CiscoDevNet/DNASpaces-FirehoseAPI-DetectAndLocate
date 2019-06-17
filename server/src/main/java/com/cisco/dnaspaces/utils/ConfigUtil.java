package com.cisco.dnaspaces.utils;


import java.io.InputStream;
import java.util.Properties;

/**
 * Utility to provide configuration needed by the application.
 */
public class ConfigUtil {

    /**
     * Reads configuratoins from a file which will be used wherever needed by the application
     *
     * @return Properties object containing list of configured application properties
     */
    public static Properties getConfig() {
        Properties prop = new Properties();
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("app.properties")) {
            // load a properties file
            prop.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return prop;

    }


}
