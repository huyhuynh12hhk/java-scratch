package lab.jaratch.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lab.jaratch.exception.HttpConfigurationException;
import lab.jaratch.utils.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {
    private static ConfigurationManager configurationManager;
    private static Configuration localConfiguration;

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (configurationManager == null)
            configurationManager = new ConfigurationManager();
        return configurationManager;
    }

    public void loadConfigurationFile(String filePath) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException(e);
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        while (true){
            try {
                if (!((i = fileReader.read())!=-1)) break;
            } catch (IOException e) {
                throw new HttpConfigurationException(e);
            }
            stringBuilder.append((char) i);
        }
        JsonNode config = null;
        try {
            config = Json.parse(stringBuilder.toString());
        } catch (IOException e) {
            throw new HttpConfigurationException("Error when parsing the configuration file",e);
        }
        try {
            localConfiguration = Json.fromJson(config,Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error when parsing the configuration file, internal",e);
        }
    }

    public Configuration getCurrentConfiguration(){
        if(localConfiguration == null){
            throw new HttpConfigurationException("No configuration has set.");
        }
        return localConfiguration;
    }
}
