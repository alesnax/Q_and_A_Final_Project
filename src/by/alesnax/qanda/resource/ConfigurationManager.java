package by.alesnax.qanda.resource;

import java.util.ResourceBundle;

public enum ConfigurationManager {
    INSTANCE;
    private ResourceBundle resourceBundle;
    private final static String PATH = "resources.config";

    ConfigurationManager() {
        resourceBundle = ResourceBundle.getBundle(PATH);
    }

    public static String getProperty(String key) {
        return INSTANCE.resourceBundle.getString(key);
    }
}




/*public enum ConfigurationManager {
    INSTANCE;

    private static Logger logger = LogManager.getLogger(ConfigurationManager.class);

    private ResourceBundle resourceBundle;
    private final static String PATH = "resources.config";

    ConfigurationManager() {
        resourceBundle = ResourceBundle.getBundle(PATH);
    }

    public static String getProperty(String key) {
        String result = null;

        try {
            result = INSTANCE.resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            logger.log(Level.FATAL, e);
            throw new RuntimeException(e);
        }
        return result;
    }
}*/