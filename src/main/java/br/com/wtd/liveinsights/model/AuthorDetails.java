package br.com.wtd.liveinsights.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AuthorDetails {
    private String channelId;
    @Column(length = 512)
    private String channelUrl;
    private String userDisplayName;
    @Column(length = 512)
    private String userProfileImageUrl;

    public AuthorDetails(String channelId, String channelUrl, String userDisplayName, String userProfileImageUrl) {
        this.channelId = channelId;
        this.channelUrl = channelUrl;
        this.userDisplayName = userDisplayName;
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public AuthorDetails(){}

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }
}
