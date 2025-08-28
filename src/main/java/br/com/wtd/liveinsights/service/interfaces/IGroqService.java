package br.com.wtd.liveinsights.service.interfaces;

public interface IGroqService {
    String callLLM(String prompt) throws Exception;
}

