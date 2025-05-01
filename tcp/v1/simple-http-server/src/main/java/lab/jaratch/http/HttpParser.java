package lab.jaratch.http;

import lab.jaratch.constant.HttpMethod;
import lab.jaratch.constant.HttpStatusCode;
import lab.jaratch.exception.HttpParsingException;
import lab.jaratch.exception.NotSupportHttpVersionException;
import lab.jaratch.http.component.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20; // char ' '(space), ASCII 32
    private static final int CR = 0x0D; // char '\r' (carriage return), ASCII 13
    private static final int LF = 0x0A; // char '\n' (line feed), ASCII 10

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        HttpRequest request = new HttpRequest();

        try {
            parseRequestLine(reader, request);
            parseHeaders(reader, request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder dataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int _byte;
        // https://datatracker.ietf.org/doc/html/rfc7230#section-3.1.1
        // Example: GET / HTTP/1.1 -> <method>SP<request target path>SP<protocol versioning>CR+LF
        while ((_byte = reader.read()) >= 0) {
            switch (_byte) {
                case CR:
                    _byte = reader.read();
                    if (_byte != LF) {
                        // Not process CR without LF
                        throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
                    }
                    // For CRLF -> end line
                    // Ensure the method and resource path was read
                    if (!methodParsed || !requestTargetParsed) {
                        throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
                    }

                    try {
                        request.setHttpVersion(dataBuffer.toString());
                    } catch (NotSupportHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
                    }

                    LOGGER.debug("Request Line: {}", dataBuffer.toString());
                    return;
                case SP:
                    if (!methodParsed) {
                        LOGGER.debug("Request Line Method: {}", dataBuffer.toString());
                        request.setMethod(dataBuffer.toString());
                        methodParsed = true;
                    } else if (!requestTargetParsed) {
                        LOGGER.debug("Request Line Method: {}", dataBuffer.toString());
                        requestTargetParsed = true;
                    }
                    dataBuffer.delete(0, dataBuffer.length());
                    break;
                default:
                    dataBuffer.append((char) _byte);
                    if (!methodParsed) {
                        if (dataBuffer.length() > HttpMethod.MAX_LENGTH) {
                            throw new HttpParsingException(HttpStatusCode.NOT_IMPLEMENTED);
                        }
                    }
            }

        }
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) throws HttpParsingException, IOException {
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean crlfFound = false;

        int _byte;
        while ((_byte = reader.read()) >=0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    if (!crlfFound) {
                        crlfFound = true;

                        // Do Things like processing
                        processSingleHeaderField(processingDataBuffer, request);
                        // Clear the buffer
                        processingDataBuffer.delete(0, processingDataBuffer.length());
                    } else {
                        // Two CRLF received, end of Headers section
                        return;
                    }
                } else {
                    throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
                }
            } else {
                crlfFound = false;
                // Append to Buffer
                processingDataBuffer.append((char)_byte);
            }
        }
    }

    private void processSingleHeaderField(StringBuilder processingDataBuffer, HttpRequest request) throws HttpParsingException {
        String rawHeaderField = processingDataBuffer.toString();
        Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%&’*+\\-./^_‘|˜\\dA-Za-z]+):\\s?(?<fieldValue>[!#$%&’*+\\-./^_‘|˜(),:;<=>?@[\\\\]{}\" \\dA-Za-z]+)\\s?$");

        Matcher matcher = pattern.matcher(rawHeaderField);
        if (matcher.matches()) {
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeader(fieldName, fieldValue);
        } else{
            throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
        }
    }


}
