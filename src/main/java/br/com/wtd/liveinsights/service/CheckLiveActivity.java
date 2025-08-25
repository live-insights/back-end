package br.com.wtd.liveinsights.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.Objects;

public class CheckLiveActivity {
    private final Dotenv dotenv = Dotenv.load();
    private final OkHttpClient client = new OkHttpClient();
    private final String YOUTUBE_API_KEY = dotenv.get("YOUTUBE_API_KEY");
    private final ConsumeApi consume = new ConsumeApi();

    public String checkActivity(String liveID) throws Exception {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://www.googleapis.com/youtube/v3/videos"))
                .newBuilder()
                .addQueryParameter("part", "liveStreamingDetails")
                .addQueryParameter("id", liveID)
                .addQueryParameter("key", YOUTUBE_API_KEY)
                .build();

        String json = consume.getData(String.valueOf(url));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        JsonNode items = root.get("items");
        if (items == null || !items.isArray() || items.isEmpty()) {
            throw new Exception("Vídeo não encontrado.");
        }

        JsonNode liveStreamingDetails = items.get(0).get("liveStreamingDetails");

        if (liveStreamingDetails != null && liveStreamingDetails.get("actualEndTime") != null) {
            throw new Exception("Live finalizada.");
        }

        if (liveStreamingDetails == null || liveStreamingDetails.get("activeLiveChatId") == null) {
            throw new Exception("Live Chat ID não encontrado. A live está realmente ativa?");
        }

        return liveStreamingDetails.get("activeLiveChatId").asText();
    }


    public boolean isLiveActive(String liveId) {
        try {
            checkActivity(liveId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

