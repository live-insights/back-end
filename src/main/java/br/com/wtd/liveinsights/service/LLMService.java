package br.com.wtd.liveinsights.service;

import br.com.wtd.liveinsights.model.CommentsInfo;
import br.com.wtd.liveinsights.model.Interaction;
import br.com.wtd.liveinsights.model.Sentiment;
import br.com.wtd.liveinsights.repository.CommentsRepository;
import br.com.wtd.liveinsights.service.interfaces.IGeminiService;
import br.com.wtd.liveinsights.service.interfaces.IGroqService;
import br.com.wtd.liveinsights.service.interfaces.ILLMService;
import br.com.wtd.liveinsights.service.interfaces.IOpenAiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMService implements ILLMService {

    private final CommentsRepository repository;
    private final IOpenAiService openAiService;
    private final IGeminiService geminiService;
    private final IGroqService grokService;

    private final String llmProvider;
    private final int batchSize;
    private static final ObjectMapper mapper = new ObjectMapper();

    public LLMService(
            CommentsRepository repository,
            IOpenAiService openAiService,
            IGeminiService geminiService,
            IGroqService grokService
    ) {
        this.repository = repository;
        this.openAiService = openAiService;
        this.geminiService = geminiService;
        this.grokService = grokService;

        Dotenv dotenv = Dotenv.load();
        this.llmProvider = dotenv.get("LLM_PROVIDER", "GROQ").toUpperCase();
        this.batchSize = Integer.parseInt(dotenv.get("LLM_BATCH_SIZE", "30"));
    }

    @Override
    public void analyzeCommentsBatch(List<CommentsInfo> comments) {
        List<List<CommentsInfo>> batches = partition(comments, batchSize);

        for (List<CommentsInfo> batch : batches) {
            try {
                String prompt = buildPrompt(batch);

                String response = switch (llmProvider) {
                    case "GEMINI" -> geminiService.callLLM(prompt);
                    case "OPENAI" -> openAiService.callLLM(prompt);
                    case "GROQ" -> grokService.callLLM(prompt);
                    default -> throw new UnsupportedOperationException("Unsupported LLM_PROVIDER: " + llmProvider);
                };

                parseAndApplyResponse(response, batch);
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

    private void parseAndApplyResponse(String json, List<CommentsInfo> batch) throws Exception {
        JsonNode root = mapper.readTree(json);
        String content = switch (llmProvider) {
            case "OPENAI" -> root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();
            case "GROQ" -> root
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();
            default -> throw new UnsupportedOperationException("Unsupported LLM_PROVIDER: " + llmProvider);
        };

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
