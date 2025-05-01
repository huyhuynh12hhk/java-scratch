package lab.jaratch.http.component;

import lab.jaratch.constant.HttpMethod;
import lab.jaratch.constant.HttpStatusCode;
import lab.jaratch.constant.HttpVersion;
import lab.jaratch.exception.HttpParsingException;
import lab.jaratch.exception.NotSupportHttpVersionException;

import java.util.HashMap;

public class HttpRequest {
    private HttpMethod method;
    private String requestTarget;
    private String incomingHttpVersion;
    private HttpVersion compatibleHttpVersion;
    private HashMap<String, String> headers = new HashMap<>();

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getIncomingHttpVersion() {
        return incomingHttpVersion;
    }

    public HttpVersion getCompatibleHttpVersion() {
        return compatibleHttpVersion;
    }

    public void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod method : HttpMethod.values()) {
            if (methodName.equals(method.name())) {
                this.method = HttpMethod.valueOf(methodName);
                return;
            }
        }
        throw new HttpParsingException(HttpStatusCode.NOT_IMPLEMENTED);
    }

    public void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.isEmpty()) {
            throw new HttpParsingException(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

        this.requestTarget = requestTarget;
    }

    public void setHttpVersion(String originalHttpVersion) throws NotSupportHttpVersionException, HttpParsingException {
        this.incomingHttpVersion = originalHttpVersion;
        this.compatibleHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.compatibleHttpVersion == null) {
            throw new HttpParsingException(
                    HttpStatusCode.VERSION_NOT_SUPPORTED
            );
        }
    }

    public void addHeader(String headerName, String headerValue){
        headers.put(headerName.toLowerCase(),headerValue);
    }

}
