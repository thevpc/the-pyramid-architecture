package net.thevpc.samples.petstore.drivers.notification.memory.infra;

import java.time.Instant;

public class NotificationLog {
    private String id;
    private String recipient;
    private String subject;
    private String body;
    private String channel;
    private Instant timestamp;

    public NotificationLog() {
    }

    public NotificationLog(String id, String recipient, String subject, String body, String channel, Instant timestamp) {
        this.id = id;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.channel = channel;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
