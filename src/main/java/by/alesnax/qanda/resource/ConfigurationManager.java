package by.alesnax.qanda.resource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class that implements method of getting application configurations from configuration properties file
 *
 * @author Aliaksandr Nakhankou
 * @see ResourceBundle
 */
public class ConfigurationManager {
    private static Logger logger = LogManager.getLogger(ConfigurationManager.class);

    /**
     * path of configuration file
     */
    private final static String PATH = "config";

    /**
     * instance of ResourceBundle
     */
    private ResourceBundle resourceBundle;

    /**
     * Default constructor that initialise resourceBundle with configuration file path
     */
    public ConfigurationManager() {
        resourceBundle = ResourceBundle.getBundle(PATH);
    }

    /**
     * method that returns value of property by key,
     * may throw RuntimeException if property not found with FATAL log
     *
     * @param key of parameters located in properties file
     * @return value of configuration property
     */
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
