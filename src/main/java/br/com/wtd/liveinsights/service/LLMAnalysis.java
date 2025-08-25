package br.com.wtd.liveinsights.service;

import br.com.wtd.liveinsights.model.CommentsInfo;
import br.com.wtd.liveinsights.model.Interaction;
import br.com.wtd.liveinsights.model.Sentiment;
import br.com.wtd.liveinsights.repository.CommentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMAnalysis {

    private final Dotenv dotenv = Dotenv.load();
    private final String LLM_API_KEY = dotenv.get("LLM_API_KEY");
    private final String LLM_URL = dotenv.get("LLM_URL");
    private static final ObjectMapper mapper = new ObjectMapper();

    private final CommentsRepository repository;

    private static final int batchSize = 30;

    private static final String SYSTEM_MESSAGE = """
    Você é um analista de comentários de lives. Sua tarefa é classificar cada comentário com dois números:

      - O primeiro número representa o sentimento:
        0 = Negativo
        1 = Neutro
        2 = Positivo

      - O segundo número representa o tipo de interação:
        3 = Pergunta
        4 = Elogio
        5 = Crítica
        6 = Sugestão
        7 = Meme / Piada
        8 = Reclamação
        9 = Reação emocional

      Siga estritamente o padrão abaixo, não adicione nem remova nenhuma informação :
      [ID] "<comentário>" → <sentimento> <tipo>
    
      Exemplo:
      [0] "Muito bom!" → 2 4
    
      Não modifique o ID nem o comentário. Apenas classifique.
      Caso não se encaixe exatamente em nenhuma categoria classifique com a categoria mais próxima e sempre siga o padrão fornecido.
    """;

    public LLMAnalysis(CommentsRepository repository) {
        this.repository = repository;
    }

    public void analyzeCommentsBatch(List<CommentsInfo> comments) {
        List<List<CommentsInfo>> batches = partition(comments, batchSize);

        for (List<CommentsInfo> batch : batches) {
            try {
                String prompt = buildPrompt(batch);
                String responseJson = callLLM(prompt);
                parseAndApplyResponse(responseJson, batch);

                repository.saveAll(batch);
            } catch (Exception e) {
                System.err.println("Erro ao analisar lote de comentários: " + e.getMessage());
            }
        }
    }

    private String buildPrompt(List<CommentsInfo> comments) {
        StringBuilder prompt = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            String text = comments.get(i).getCommentsDetailsData().commentContent();
            prompt.append(String.format("[%d] \"%s\"\n", i, text));
        }
        return prompt.toString();
    }


    private String callLLM(String prompt) throws IOException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(
                                Map.of("text", SYSTEM_MESSAGE + "\n\n" + prompt)
                        ))
                )
        );

        String body = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL + "?key=" + LLM_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private void parseAndApplyResponse(String json, List<CommentsInfo> batch) throws JsonProcessingException {
        JsonNode root = mapper.readTree(json);

        String content = root
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text")
                .asText();

        System.out.println("Resposta bruta do LLM:\n" + content);

        Pattern pattern = Pattern.compile("^\\[(\\d+)]\\s*\"?(.*?)\"?\\s*→\\s*(\\d)\\s+(\\d)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            int sentimentCode = Integer.parseInt(matcher.group(3));
            int interactionCode = Integer.parseInt(matcher.group(4));

            if (index >= 0 && index < batch.size()) {
                CommentsInfo comment = batch.get(index);
                comment.setSentiment(Sentiment.fromCode(sentimentCode));
                comment.setInteraction(Interaction.fromCode(interactionCode));
            }
        }
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
