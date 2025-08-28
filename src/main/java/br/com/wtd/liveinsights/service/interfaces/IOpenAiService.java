package br.com.wtd.liveinsights.service.interfaces;

public interface IOpenAiService {
    String callLLM(String prompt) throws Exception;
}
