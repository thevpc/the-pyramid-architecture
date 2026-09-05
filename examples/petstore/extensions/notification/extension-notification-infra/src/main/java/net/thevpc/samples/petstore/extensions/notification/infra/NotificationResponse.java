package net.thevpc.samples.petstore.extensions.notification.infra;

public class NotificationResponse {
    private String id;
    private NotificationStatus status;
    private String driverName;
    private String message;

    public NotificationResponse() {
    }

    public NotificationResponse(String id, NotificationStatus status, String driverName, String message) {
        this.id = id;
        this.status = status;
        this.driverName = driverName;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
