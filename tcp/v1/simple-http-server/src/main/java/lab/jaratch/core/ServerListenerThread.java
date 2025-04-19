package lab.jaratch.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    private int port;
    private String webroot;
    private ServerSocket serverSocket;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    public ServerListenerThread(int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {

        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                LOGGER.info("Sever is listening on port: {}", port);

                // From here the actions will execute when trigger server listening
                Socket socket = serverSocket.accept();
                LOGGER.info("Connect accepted: " + socket.getInetAddress());

                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket);
                workerThread.start();


            }
//            serverSocket.close();

        } catch (IOException e) {
            LOGGER.error("Issue when setting socket", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }

    }

    // Old workflow not allow asynchronous call,
    // it was waiting one done to response and do the next
//    private void oldWorkFlow(Socket socket) throws IOException, InterruptedException {
//        InputStream inputStream = socket.getInputStream();
//        OutputStream outputStream = socket.getOutputStream();
//
//        // Content
//        String html = "<html><head><title>Java Simple HTTP Server</title></head>"
//                + "<body><h1>This page was served using my Java Simple HTTP Server</h1></body>"
//                + "</html>";
//
//        // CRLF
//        final String CRLF = "\n\r"; // ASCII 13, 10
//
//        // Status code
//        // HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
//        String response = "HTTP/1.1 200 OK" + CRLF
//                + "Content-Length:" + html.getBytes().length + CRLF
//                + CRLF
//                + html + CRLF
//                + CRLF;
//
//        outputStream.write(response.getBytes());
//
//        inputStream.close();
//        outputStream.close();
//        sleep(5000);
//        socket.close();
//    }
}
