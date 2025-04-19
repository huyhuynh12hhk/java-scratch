package lab.jaratch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            // Content
            String html = "<html><head><title>Java Simple HTTP Server</title></head>"
                    + "<body><h1>This page was served using my Java Simple HTTP Server</h1></body>"
                    + "</html>";

            // CRLF
            final String CRLF = "\n\r"; // ASCII 13, 10

            // Status code
            // HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
            String response = "HTTP/1.1 200 OK" + CRLF
                    + "Content-Length:" + html.getBytes().length + CRLF
                    + CRLF
                    + html + CRLF
                    + CRLF;

            outputStream.write(response.getBytes());

            inputStream.close();
            outputStream.close();
            sleep(5000);
            socket.close();


        } catch (IOException e) {
            LOGGER.error("Issue when communication", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
            }
        }
    }

}
