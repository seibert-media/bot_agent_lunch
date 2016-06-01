package de.sjanusch.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigurationLoader {

    private Properties properties = new Properties();

    private boolean loadedFlag = false;

    private String bundle;

    public ConfigurationLoader(final String bundle) {
        this.bundle = bundle;
    }

    private void loadProperties() throws IOException {
        properties.load(ConfigurationLoader.class.getClassLoader().getResourceAsStream(this.bundle));
        loadedFlag = true;
    }

    public String getPropertyStringValue(final String key) throws IOException {
        checkPropertiesLoaded();
        return new String(properties.getProperty(key).getBytes("ISO-8859-1"), "UTF-8");
    }

    public String[] getPropertyStringArrayValue(final String key) throws IOException {
        checkPropertiesLoaded();
        final String value = new String(properties.getProperty(key).getBytes("ISO-8859-1"), "UTF-8");
        return value.split(";");
    }

    public List<String> getPropertyStringListValue(final String key) throws IOException {
        return Arrays.asList(getPropertyStringArrayValue(key));
    }

    private void checkPropertiesLoaded() throws IOException {
        if (!loadedFlag) {
            loadProperties();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public boolean isLoadedFlag() {
        return loadedFlag;
    }

    public void setLoadedFlag(final boolean loadedFlag) {
        this.loadedFlag = loadedFlag;
    }

}
