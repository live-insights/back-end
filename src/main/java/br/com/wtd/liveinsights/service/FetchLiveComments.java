package br.com.wtd.liveinsights.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.HttpUrl;

import java.util.Objects;

public class FetchLiveComments {
    private final Dotenv dotenv = Dotenv.load();
    private final String YOUTUBE_API_KEY = dotenv.get("YOUTUBE_API_KEY");
    private final ConsumeApi consume = new ConsumeApi();

    String lastPageToken = "";
    int totalResults = 0;

    public String fetchLiveComments(String activeChatId) throws JsonProcessingException {

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://www.googleapis.com/youtube/v3/liveChat/messages\n"))
                .newBuilder()
                .addQueryParameter("liveChatId", activeChatId)
                .addQueryParameter("part", "snippet,authorDetails")
                .addQueryParameter("key", YOUTUBE_API_KEY)
                .addQueryParameter("pageToken", lastPageToken)
                .addQueryParameter("maxResults", "100")
                .build();

        String json = consume.getData(String.valueOf(url));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        JsonNode nextTokenNode = root.get("nextPageToken");
        if (nextTokenNode != null) {
            lastPageToken = nextTokenNode.asText();
        }

        JsonNode pageInfoNode = root.get("pageInfo");

        if (pageInfoNode != null && pageInfoNode.has("totalResults")) {
            totalResults = pageInfoNode.get("totalResults").asInt();
        }

        System.out.println("Total results: " + totalResults);

        return json;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
