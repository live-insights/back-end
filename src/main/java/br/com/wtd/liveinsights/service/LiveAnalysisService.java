package br.com.wtd.liveinsights.service;

import br.com.wtd.liveinsights.model.*;
import br.com.wtd.liveinsights.repository.CommentsRepository;
import br.com.wtd.liveinsights.repository.LiveRepository;
import br.com.wtd.liveinsights.repository.UserRepository;
import br.com.wtd.liveinsights.service.interfaces.ILiveAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveAnalysisService implements ILiveAnalysisService {

    private String activeChatId;
    private String currentLiveId;

    private User user;
    private Live live;
    private int emptyCount = 0;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LiveRepository liveRepository;

    private final CheckLiveActivity checkLive = new CheckLiveActivity();
    private final FetchLiveComments fetchLiveComments = new FetchLiveComments();
    private final ConvertData converter = new ConvertData();
    private final CommentsRepository repository;
    private final LLMService llmService;

    @Autowired
    public LiveAnalysisService(CommentsRepository repository, LLMService llmService) {
        this.repository = repository;
        this.llmService = llmService;
    }

    public void configureAnalysis(Long userId, String liveId) {
        this.user = userRepository.findById(userId).orElseThrow();
        this.live = liveRepository.findByLiveIdAndUserId(liveId, userId)
                .orElseThrow(() -> new RuntimeException("Live não encontrada para este usuário"));
    }

    public boolean startAnalysis(String liveID) {
        try {
            this.currentLiveId = liveID;
            this.activeChatId = checkLive.checkActivity(liveID);
            live.setStatus(LiveStatus.ATIVO);
            liveRepository.save(live);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao iniciar análise: " + e.getMessage());
            live.setStatus(LiveStatus.FINALIZADO);
            liveRepository.save(live);
            return false;
        }
    }

    public void executeAnalysis() {
        if (activeChatId == null || user == null || live == null) return;

        try {
            String json = fetchLiveComments.fetchLiveComments(activeChatId);
            GeneralInfoData generalInfoData = converter.getData(json, GeneralInfoData.class);

            if (generalInfoData.commentsInfo().isEmpty()) {
                emptyCount++;
                System.out.println("Nenhum comentário recebido. Contador: " + emptyCount);
            } else {
                emptyCount = 0;
                processComments(generalInfoData);
            }

            if (emptyCount >= 3) {
                emptyCount = 0;
                boolean stillActive = checkLive.isLiveActive(currentLiveId);
                System.out.println("Verificando se a live ainda está ativa... Resultado: " + stillActive);

                if (!stillActive) {
                    System.out.println("Live foi finalizada. Encerrando análise.");
                    live.setStatus(LiveStatus.FINALIZADO);
                    liveRepository.save(live);
                    stopAnalysis();
                }
            }

        } catch (Exception e) {
            System.out.println("Erro durante análise: " + e.getMessage());
        }
    }

    private void processComments(GeneralInfoData generalInfoData) {
        List<CommentsInfo> allComments = new ArrayList<>();

        for (CommentsInfoData data : generalInfoData.commentsInfo()) {
            CommentsInfo comment = getCommentsInfo(data);
            comment.setUser(user);
            comment.setLive(live);
            allComments.add(comment);
        }

        llmService.analyzeCommentsBatch(allComments);
    }

    private CommentsInfo getCommentsInfo(CommentsInfoData data) {
        CommentsInfo comment = new CommentsInfo(
                data.commentId(),
                data.commentsDetail(),
                data.authorDetails()
        );

        CommentsDetailsData oldDetails = comment.getCommentsDetailsData();
        CommentsDetailsData newDetails = new CommentsDetailsData(
                activeChatId,
                oldDetails.commentTimeStamp(),
                oldDetails.commentContent()
        );
        comment.setCommentsDetailsData(newDetails);

        comment.setLiveVideoId(currentLiveId);
        return comment;
    }

    public void stopAnalysis() {
        this.activeChatId = null;

        if (live.getStatus() != LiveStatus.FINALIZADO) {
            live.setStatus(LiveStatus.INATIVO);
            liveRepository.save(live);
        }
    }

    public boolean isRunning() {
        return this.activeChatId != null;
    }
}
