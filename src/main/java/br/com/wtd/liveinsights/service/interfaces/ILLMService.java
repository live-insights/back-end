package br.com.wtd.liveinsights.service.interfaces;

import br.com.wtd.liveinsights.model.CommentsInfo;

import java.util.List;

public interface ILLMService {
    void analyzeCommentsBatch(List<CommentsInfo> comments);
}
