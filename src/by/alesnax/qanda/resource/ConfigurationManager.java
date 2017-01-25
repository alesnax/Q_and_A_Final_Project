package by.alesnax.qanda.resource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ConfigurationManager {
    private static Logger logger = LogManager.getLogger(ConfigurationManager.class);

    private final static String PATH = "resources.config";

    private ResourceBundle resourceBundle;

    public ConfigurationManager() {
        resourceBundle = ResourceBundle.getBundle(PATH);
    }

    public String getProperty(String key) {
        String property;
        try {
            property = resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            logger.log(Level.FATAL, e);
            throw new RuntimeException(e);
        }
        return property;
    }
}
