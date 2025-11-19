package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos tipos de disciplinas oferecidas nos programas de pós-graduação.
 * Define as diferentes categorias de disciplinas disponíveis.
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum TipoDisciplina {

    /**
     * Disciplina obrigatória do programa
     */
    OBRIGATORIA("Obrigatória", "Disciplina obrigatória para conclusão do curso"),

    /**
     * Disciplina eletiva do programa
     */
    ELETIVA("Eletiva", "Disciplina optativa de livre escolha"),

    /**
     * Disciplina de seminários
     */
    SEMINARIO("Seminário", "Disciplina de apresentação de trabalhos e discussões"),

    /**
     * Disciplina de tópicos especiais
     */
    TOPICOS_ESPECIAIS("Tópicos Especiais", "Disciplina de conteúdo variável e avançado");

    /**
     * Valor armazenado no banco de dados
     */
    @JsonValue
    private final String valor;

    /**
     * Descrição do tipo de disciplina
     */
    private final String descricao;

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
    public static TipoDisciplina fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Tipo de disciplina não pode ser nulo");
        }

        for (TipoDisciplina tipo : TipoDisciplina.values()) {
            if (tipo.valor.equalsIgnoreCase(valor) || tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Tipo de disciplina inválido: " + valor);
    }
}
