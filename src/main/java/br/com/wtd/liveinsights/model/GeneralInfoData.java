package br.com.wtd.liveinsights.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneralInfoData(@JsonAlias("totalResults") int totalResults,
                              @JsonAlias("nextPageToken") String nextPageToken,
                              @JsonAlias("items") List<CommentsInfoData> commentsInfo) {
}
