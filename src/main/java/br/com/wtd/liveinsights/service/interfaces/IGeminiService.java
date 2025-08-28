package br.com.wtd.liveinsights.service.interfaces;

public interface IGeminiService {
    String callLLM(String prompt) throws Exception;
}
