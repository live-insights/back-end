package br.com.wtd.liveinsights.service;

import br.com.wtd.liveinsights.service.interfaces.IGroqService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class GroqService implements IGroqService {

    private final Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("LLM_API_KEY");
    private final String SYSTEM_PROMPT = dotenv.get("LLM_PROMPT", "");

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String callLLM(String prompt) throws Exception {
        Map<String, Object> payload = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", prompt)
                )
        );

        String body = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
