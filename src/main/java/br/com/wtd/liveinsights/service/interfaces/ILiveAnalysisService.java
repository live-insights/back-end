package br.com.wtd.liveinsights.service.interfaces;

public interface ILiveAnalysisService {
    void configureAnalysis(Long userId, String liveId);
    boolean startAnalysis(String liveID);
    void executeAnalysis();
    void stopAnalysis();
    boolean isRunning();
}
