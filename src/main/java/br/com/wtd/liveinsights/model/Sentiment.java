package br.com.wtd.liveinsights.model;

public enum Sentiment {
    NEGATIVO(0),
    NEUTRO(1),
    POSITIVO(2);

    private final int code;

    Sentiment(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Sentiment fromCode(int code) {
        for (Sentiment s : Sentiment.values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Nenhum sentimento encontrado para o código: " + code);
    }
}

