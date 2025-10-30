package br.com.wtd.liveinsights.model;

public enum Interaction {
    PERGUNTA(3),
    ELOGIO(4),
    CRITICA(5),
    SUGESTAO(6),
    PIADA(7),
    RECLAMACAO(8),
    REACAO_EMOCIONAL(9),
    OFENSIVO_PRECONCEITUOSO(10);

    private final int code;

    Interaction(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Interaction fromCode(int code) {
        for (Interaction s : Interaction.values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Nenhuma interação encontrada para o código: " + code);
    }
}

