package br.com.wtd.liveinsights.service;

public interface IConvertData {
    <T> T getData(String json, Class<T> classe);
}
