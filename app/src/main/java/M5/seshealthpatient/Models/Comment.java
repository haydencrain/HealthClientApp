package M5.seshealthpatient.Models;

import java.util.Date;

public class Comment {
    private String message;
    private String commenterId;
    private long sentDateTime;

    public Comment() {
        message = "";
        commenterId = "";
        sentDateTime = new Date().getTime();
    }

    public Comment(String message, String commenterId) {
        this.message = message;
        this.commenterId = commenterId;
        this.sentDateTime = new Date().getTime();
    }

    public Comment(String message, String commenterId, long sentDate) {
        this.message = message;
        this.commenterId = commenterId;
        this.sentDateTime = sentDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public long getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(long sentDateTime) {
        this.sentDateTime = sentDateTime;
    }

    public Date getSentDate() {
        return new Date(sentDateTime);
    }
}
