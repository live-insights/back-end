package br.com.wtd.liveinsights.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.sql.Timestamp;
@Embeddable
public class CommentsDetails {
    private String commentLiveId;
    private Timestamp commentTimeStamp;

    @Column(length = 1000)
    private String commentContent;

    public CommentsDetails() {}

    public CommentsDetails(String commentLiveId, Timestamp commentTimeStamp, String commentContent) {
        this.commentLiveId = commentLiveId;
        this.commentTimeStamp = commentTimeStamp;
        this.commentContent = commentContent;
    }

    public String getCommentLiveId() {
        return commentLiveId;
    }

    public void setCommentLiveId(String commentLiveId) {
        this.commentLiveId = commentLiveId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Timestamp getCommentTimeStamp() {
        return commentTimeStamp;
    }

    public void setCommentTimeStamp(Timestamp commentTimeStamp) {
        this.commentTimeStamp = commentTimeStamp;
    }
}
