package br.com.wtd.liveinsights.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommentsInfoData(@JsonAlias("id") String commentId,
                               @JsonAlias("snippet") CommentsDetailsData commentsDetail,
                               @JsonAlias("authorDetails") AuthorDetailsData authorDetails) {

}
