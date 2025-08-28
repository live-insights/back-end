package br.com.wtd.liveinsights.service;

import br.com.wtd.liveinsights.service.interfaces.IGeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService implements IGeminiService {

    private final Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("LLM_API_KEY");
    private final String SYSTEM_MESSAGE = dotenv.get("LLM_PROMPT");

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    private long lastCallTimestamp = 0;
    private static final long MIN_INTERVAL_MS = 500;

    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_BACKOFF_MS = 1000;

    @Override
    public synchronized String callLLM(String prompt) throws Exception {
        long now = System.currentTimeMillis();
        long waitTime = MIN_INTERVAL_MS - (now - lastCallTimestamp);
        if (waitTime > 0) {
            Thread.sleep(waitTime);
        }
        lastCallTimestamp = System.currentTimeMillis();

        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", SYSTEM_MESSAGE + "\n\n" + prompt)
                                )
                        )
                )
        );

        String body = mapper.writeValueAsString(payload);

        int attempt = 0;
        long backoff = INITIAL_BACKOFF_MS;

        while (attempt <= MAX_RETRIES) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?key=" + API_KEY))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            if (statusCode == 200) {
                return response.body();
            } else if (statusCode == 429) {
                Thread.sleep(backoff);
                backoff *= 2;
                attempt++;
            } else {
                throw new RuntimeException("Erro inesperado na API Gemini: HTTP " + statusCode + " - " + response.body());
            }
        }

        throw new RuntimeException("Falha após " + MAX_RETRIES + " tentativas por erro 429 (quota excedida).");
    }
}
