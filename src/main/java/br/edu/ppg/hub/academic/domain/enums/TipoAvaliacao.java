package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos tipos de avaliação possíveis em disciplinas.
 * Define os diferentes métodos de avaliação que podem ser aplicados.
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum TipoAvaliacao {

    /**
     * Avaliação por prova escrita
     */
    PROVA("Prova", "Avaliação escrita presencial ou online", 10.0),

    /**
     * Avaliação por trabalho acadêmico
     */
    TRABALHO("Trabalho", "Trabalho individual ou em grupo", 10.0),

    /**
     * Avaliação por apresentação de seminário
     */
    SEMINARIO("Seminário", "Apresentação oral de tema específico", 10.0),

    /**
     * Avaliação por desenvolvimento de projeto
     */
    PROJETO("Projeto", "Desenvolvimento de projeto prático ou teórico", 10.0),

    /**
     * Avaliação por produção de artigo
     */
    ARTIGO("Artigo", "Elaboração de artigo científico", 10.0),

    /**
     * Participação em atividades
     */
    PARTICIPACAO("Participação", "Participação ativa nas aulas e discussões", 10.0),

    /**
     * Exercícios e listas
     */
    EXERCICIO("Exercício", "Resolução de exercícios e listas", 10.0);

    /**
     * Valor armazenado no banco de dados
     */
    @JsonValue
    private final String valor;

    /**
     * Descrição do tipo de avaliação
     */
    private final String descricao;

    /**
     * Nota máxima permitida para este tipo de avaliação
     */
    private final Double notaMaxima;

    /**
     * Retorna a representação em string do enum
     *
     * @return valor do enum
     */
    @Override
    public String toString() {
        return this.valor;
    }

    /**
     * Converte uma string para o enum correspondente
     *
     * @param valor string a ser convertida
     * @return enum correspondente
     * @throws IllegalArgumentException se o valor não for válido
     */
    @JsonCreator
    public static TipoAvaliacao fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Tipo de avaliação não pode ser nulo");
        }

        for (TipoAvaliacao tipo : TipoAvaliacao.values()) {
            if (tipo.valor.equalsIgnoreCase(valor) || tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Tipo de avaliação inválido: " + valor);
    }

    /**
     * Valida se uma nota está dentro do limite permitido
     *
     * @param nota nota a ser validada
     * @return true se a nota é válida
     */
    public boolean validarNota(Double nota) {
        return nota != null && nota >= 0 && nota <= this.notaMaxima;
    }
}
