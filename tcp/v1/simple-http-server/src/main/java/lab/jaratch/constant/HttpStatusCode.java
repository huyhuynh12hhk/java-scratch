package lab.jaratch.constant;

// https://datatracker.ietf.org/doc/html/rfc7231#section-6.1
public enum HttpStatusCode {
    // Client error
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    METHOD_NOT_ALLOW(405, "Method Not Allow"),
    NOT_FOUND(404, "Bad Request"),
    // Server Error
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    VERSION_NOT_SUPPORTED(505, "Version Not Supported"),
    // Successful
    OK(200, "Ok"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),


    ;
    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }


}
