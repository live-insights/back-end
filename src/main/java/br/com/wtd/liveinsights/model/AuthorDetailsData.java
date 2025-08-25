package br.com.wtd.liveinsights.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorDetailsData(@JsonAlias("channelId") String channelId,
                                @JsonAlias("channelUrl") String channelUrl,
                                @JsonAlias("displayName") String userDisplayName,
                                @JsonAlias("profileImageUrl") String userProfileImageUrl) {
}
