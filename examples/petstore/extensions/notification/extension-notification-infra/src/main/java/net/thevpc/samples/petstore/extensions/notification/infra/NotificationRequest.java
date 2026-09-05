package net.thevpc.samples.petstore.extensions.notification.infra;

public class NotificationRequest {
    private String recipient;
    private String subject;
    private String body;
    private NotificationChannel channel = NotificationChannel.CONSOLE;

    public NotificationRequest() {
    }

    public NotificationRequest(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    public NotificationRequest(String recipient, String subject, String body, NotificationChannel channel) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.channel = channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }
}
