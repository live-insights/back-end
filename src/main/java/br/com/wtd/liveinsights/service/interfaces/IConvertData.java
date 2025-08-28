package br.com.wtd.liveinsights.service.interfaces;

public interface IConvertData {
    <T> T getData(String json, Class<T> classe);
}
