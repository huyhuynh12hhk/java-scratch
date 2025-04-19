package lab.jaratch.server;

import lab.jaratch.config.Configuration;
import lab.jaratch.config.ConfigurationManager;
import lab.jaratch.core.ServerListenerThread;

import java.io.IOException;

public class HttpServer {

    public static void run() {
        System.out.println("Sever starting");
//        TODO: Consider the root path is root path of this project
        var path = "simple-http-server/src/main/resources/http.json";
        ConfigurationManager.getInstance().loadConfigurationFile(path);
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

//        System.out.println("Using Port: "+config.getPort());
//        System.out.println("Using WebRoot: "+config.getWebroot());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle server crashing
        }


    }
}
