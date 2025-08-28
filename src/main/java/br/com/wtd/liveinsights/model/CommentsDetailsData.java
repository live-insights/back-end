package br.com.wtd.liveinsights.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;

import java.sql.Timestamp;

@AttributeOverrides({
        @AttributeOverride(name = "commentContent", column = @Column(columnDefinition = "TEXT"))
})
@JsonIgnoreProperties(ignoreUnknown = true)
public record CommentsDetailsData(@JsonAlias("liveChatId") String commentLiveId,
                                  @JsonAlias("publishedAt") Timestamp commentTimeStamp,
                                  @JsonAlias("displayMessage") String commentContent) {
}
