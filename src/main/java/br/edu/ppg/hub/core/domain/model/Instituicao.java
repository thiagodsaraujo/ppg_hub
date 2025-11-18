package br.edu.ppg.hub.core.domain.model; // Declara o pacote onde a classe está localizada

// === IMPORTS DE BIBLIOTECAS EXTERNAS ===
// Importa suporte para tipos JSON do Hypersistence Utils (permite armazenar JSON no PostgreSQL)
import io.hypersistence.utils.hibernate.type.json.JsonType;

// Importa annotations do Jakarta Persistence (JPA) para mapeamento objeto-relacional
import jakarta.persistence.*; // Importa todas as annotations JPA (* significa "tudo")

// Importa annotations de validação do Bean Validation
import jakarta.validation.constraints.NotBlank; // Valida que String não é nula, vazia ou só espaços
import jakarta.validation.constraints.Size; // Valida tamanho mínimo e máximo de Strings

// Importa annotations do Lombok para reduzir código boilerplate
import lombok.*; // Importa todas as annotations do Lombok

// Importa annotations específicas do Hibernate
import org.hibernate.annotations.CreationTimestamp; // Preenche automaticamente data de criação
import org.hibernate.annotations.Type; // Define tipo customizado de coluna
import org.hibernate.annotations.UpdateTimestamp; // Preenche automaticamente data de atualização

// Importa classes do Java para trabalhar com data/hora e coleções
import java.time.LocalDateTime; // Classe para representar data e hora sem timezone
import java.util.Map; // Interface para coleções chave-valor (usado para JSON)

/**
 * Entidade JPA para Instituição de Ensino.
 *
 * Esta classe representa uma instituição de ensino superior no banco de dados.
 * Cada instância desta classe corresponde a uma linha na tabela "instituicoes".
 *
 * ANNOTATIONS DA CLASSE:
 * - @Entity: Marca a classe como uma entidade JPA (será persistida no banco)
 * - @Table: Configura detalhes da tabela no banco de dados
 * - @Data: Lombok - gera getters, setters, toString, equals e hashCode
 * - @NoArgsConstructor: Lombok - gera construtor sem argumentos
 * - @AllArgsConstructor: Lombok - gera construtor com todos os argumentos
 * - @Builder: Lombok - implementa padrão Builder para criar objetos
 * - @EqualsAndHashCode: Define que equals/hashCode usam apenas o campo "id"
 * - @ToString: Exclui campos JSON do toString para evitar saída muito grande
 *
 * @author PPG Team
 * @version 0.1.0
 */
@Entity // Marca esta classe como uma entidade JPA que será mapeada para o banco
@Table( // Configura a tabela no banco de dados
    name = "instituicoes", // Nome da tabela no banco será "instituicoes"
    uniqueConstraints = { // Define restrições de unicidade
        // Cria constraint única para o campo codigo (não pode haver dois códigos iguais)
        @UniqueConstraint(name = "uk_instituicao_codigo", columnNames = "codigo"),
        // Cria constraint única para o campo cnpj (não pode haver dois CNPJs iguais)
        @UniqueConstraint(name = "uk_instituicao_cnpj", columnNames = "cnpj")
    },
    indexes = { // Define índices para otimizar consultas
        // Cria índice no campo tipo (acelera buscas por tipo de instituição)
        @Index(name = "idx_instituicao_tipo", columnList = "tipo"),
        // Cria índice no campo ativo (acelera filtros por instituições ativas)
        @Index(name = "idx_instituicao_ativo", columnList = "ativo"),
        // Cria índice no nome_abreviado (acelera ordenação e busca por nome)
        @Index(name = "idx_instituicao_nome_abreviado", columnList = "nome_abreviado")
    }
)
@Data // Lombok: Gera automaticamente getters, setters, toString, equals e hashCode
@NoArgsConstructor // Lombok: Gera construtor vazio (necessário para JPA)
@AllArgsConstructor // Lombok: Gera construtor com todos os campos
@Builder // Lombok: Implementa padrão Builder (ex: Instituicao.builder().codigo("UEPB").build())
@EqualsAndHashCode(of = "id") // Define que dois objetos são iguais se tiverem o mesmo ID
@ToString(exclude = {"configuracoes", "endereco", "contatos", "redesSociais"}) // Exclui JSONs do toString
public class Instituicao { // Declaração da classe pública Instituicao

    // ==================== CAMPO IDENTIFICADOR ====================

    /**
     * Identificador único da instituição no banco de dados.
     * É a chave primária (Primary Key) da tabela.
     * Gerado automaticamente pelo banco usando auto-incremento.
     */
    @Id // Marca este campo como chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Valor gerado automaticamente pelo banco (auto-increment)
    private Long id; // Tipo Long para suportar IDs grandes (pode ir até 9.223.372.036.854.775.807)

    // ==================== CAMPOS OBRIGATÓRIOS ====================

    /**
     * Código único da instituição (ex: UEPB, UFCG, USP).
     *
     * Este código é usado como identificador alfanumérico da instituição.
     * É único no sistema (não pode haver duas instituições com mesmo código).
     * Sempre convertido para maiúsculo antes de salvar.
     *
     * VALIDAÇÕES:
     * - Não pode ser nulo ou vazio (@NotBlank)
     * - Deve ter entre 2 e 20 caracteres (@Size)
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - UNIQUE (unique = true)
     * - VARCHAR(20) (length = 20)
     */
    @Column( // Define configurações da coluna no banco
        name = "codigo", // Nome da coluna no banco será "codigo"
        nullable = false, // Não aceita valores nulos (NOT NULL no SQL)
        unique = true, // Valores devem ser únicos (UNIQUE no SQL)
        length = 20 // Tamanho máximo da string (VARCHAR(20) no SQL)
    )
    @NotBlank(message = "Código é obrigatório") // Validação: não pode ser null, vazio ou só espaços
    @Size(min = 2, max = 20, message = "Código deve ter entre 2 e 20 caracteres") // Validação de tamanho
    private String codigo; // Campo privado do tipo String para armazenar o código

    /**
     * Nome oficial completo da instituição.
     *
     * Exemplo: "Universidade Estadual da Paraíba"
     *
     * Este é o nome jurídico/oficial da instituição, usado em documentos formais.
     *
     * VALIDAÇÕES:
     * - Não pode ser nulo ou vazio (@NotBlank)
     * - Deve ter entre 5 e 500 caracteres (@Size)
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - VARCHAR(500) (length = 500)
     */
    @Column(name = "nome_completo", nullable = false, length = 500) // Coluna obrigatória de até 500 caracteres
    @NotBlank(message = "Nome completo é obrigatório") // Validação: não pode ser vazio
    @Size(min = 5, max = 500, message = "Nome completo deve ter entre 5 e 500 caracteres") // Valida tamanho
    private String nomeCompleto; // Armazena o nome oficial completo

    /**
     * Nome abreviado para exibição em interfaces.
     *
     * Exemplo: "UEPB" ou "UEPB - Campus I"
     *
     * Usado em listagens, menus e onde o nome completo seria muito longo.
     *
     * VALIDAÇÕES:
     * - Não pode ser nulo ou vazio (@NotBlank)
     * - Deve ter entre 2 e 50 caracteres (@Size)
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - VARCHAR(50) (length = 50)
     * - Possui índice para otimizar buscas e ordenação
     */
    @Column(name = "nome_abreviado", nullable = false, length = 50) // Coluna obrigatória de até 50 caracteres
    @NotBlank(message = "Nome abreviado é obrigatório") // Validação: não pode ser vazio
    @Size(min = 2, max = 50, message = "Nome abreviado deve ter entre 2 e 50 caracteres") // Valida tamanho
    private String nomeAbreviado; // Armazena o nome abreviado

    /**
     * Sigla oficial da instituição.
     *
     * Exemplo: "UEPB", "UFCG", "USP"
     *
     * Geralmente são as iniciais do nome da instituição.
     *
     * VALIDAÇÕES:
     * - Não pode ser nulo ou vazio (@NotBlank)
     * - Deve ter entre 2 e 10 caracteres (@Size)
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - VARCHAR(10) (length = 10)
     */
    @Column(name = "sigla", nullable = false, length = 10) // Coluna obrigatória de até 10 caracteres
    @NotBlank(message = "Sigla é obrigatória") // Validação: não pode ser vazio
    @Size(min = 2, max = 10, message = "Sigla deve ter entre 2 e 10 caracteres") // Valida tamanho
    private String sigla; // Armazena a sigla

    /**
     * Tipo/categoria da instituição.
     *
     * Valores possíveis: "Federal", "Estadual", "Municipal", "Privada"
     *
     * Define a categoria administrativa da instituição.
     *
     * VALIDAÇÕES:
     * - Não pode ser nulo ou vazio (@NotBlank)
     * - Deve ser um dos valores permitidos (validado no DTO)
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - VARCHAR(20) (length = 20)
     * - Possui índice para otimizar consultas por tipo
     */
    @Column(name = "tipo", nullable = false, length = 20) // Coluna obrigatória de até 20 caracteres
    @NotBlank(message = "Tipo é obrigatório") // Validação: não pode ser vazio
    private String tipo; // Armazena o tipo da instituição

    // ==================== CAMPOS OPCIONAIS ====================

    /**
     * CNPJ (Cadastro Nacional de Pessoa Jurídica) da instituição.
     *
     * Exemplo: "12.345.678/0001-90"
     *
     * Identificador fiscal único da instituição no Brasil.
     * Sempre armazenado com formatação (pontos, barra e hífen).
     *
     * VALIDAÇÕES:
     * - Campo opcional (pode ser nulo)
     * - Se fornecido, deve ser válido (validado no DTO com @ValidCNPJ)
     *
     * CONSTRAINTS NO BANCO:
     * - UNIQUE (unique = true) - não pode haver CNPJs duplicados
     * - VARCHAR(18) (length = 18) - comporta formato XX.XXX.XXX/XXXX-XX
     */
    @Column(name = "cnpj", unique = true, length = 18) // Coluna única opcional de até 18 caracteres
    private String cnpj; // Armazena o CNPJ formatado

    /**
     * Natureza jurídica detalhada da instituição.
     *
     * Exemplo: "Autarquia Estadual", "Fundação Pública", "Sociedade Privada"
     *
     * Classificação legal da instituição segundo a Receita Federal.
     *
     * CONSTRAINTS NO BANCO:
     * - Pode ser nulo (campo opcional)
     * - VARCHAR(100) (length = 100)
     */
    @Column(name = "natureza_juridica", length = 100) // Coluna opcional de até 100 caracteres
    private String naturezaJuridica; // Armazena a natureza jurídica

    // ==================== CAMPOS JSON (DADOS ESTRUTURADOS) ====================

    /**
     * Dados de endereço da instituição em formato JSON.
     *
     * Exemplo de estrutura:
     * {
     *   "logradouro": "Rua Baraúnas, 351",
     *   "bairro": "Universitário",
     *   "cidade": "Campina Grande",
     *   "uf": "PB",
     *   "cep": "58429-500",
     *   "complemento": "Campus I"
     * }
     *
     * Armazenado como JSONB no PostgreSQL para permitir consultas eficientes.
     *
     * TIPO NO BANCO:
     * - JSONB (binary JSON - mais eficiente que JSON text)
     * - Permite indexação e consultas dentro do JSON
     *
     * TIPO NO JAVA:
     * - Map<String, Object> - permite chaves String com valores de qualquer tipo
     */
    @Type(JsonType.class) // Define que este campo usa tipo JSON customizado do Hypersistence
    @Column(name = "endereco", columnDefinition = "jsonb") // Define coluna como JSONB no PostgreSQL
    private Map<String, Object> endereco; // Mapa chave-valor para armazenar dados de endereço

    /**
     * Telefones e emails de contato em formato JSON.
     *
     * Exemplo de estrutura:
     * {
     *   "email_principal": "contato@uepb.edu.br",
     *   "telefone_principal": "(83) 3315-3300",
     *   "emails": ["contato@uepb.edu.br", "reitoria@uepb.edu.br"],
     *   "telefones": ["(83) 3315-3300", "(83) 3315-3301"]
     * }
     *
     * Permite armazenar múltiplos contatos de forma flexível.
     *
     * TIPO NO BANCO:
     * - JSONB (binary JSON)
     *
     * TIPO NO JAVA:
     * - Map<String, Object> - permite diferentes tipos de valores
     */
    @Type(JsonType.class) // Define tipo JSON customizado
    @Column(name = "contatos", columnDefinition = "jsonb") // Coluna JSONB no banco
    private Map<String, Object> contatos; // Mapa para armazenar contatos

    /**
     * URLs das redes sociais da instituição em formato JSON.
     *
     * Exemplo de estrutura:
     * {
     *   "facebook": "https://facebook.com/uepb.oficial",
     *   "instagram": "https://instagram.com/uepb.oficial",
     *   "twitter": "https://twitter.com/uepb_oficial",
     *   "linkedin": "https://linkedin.com/school/uepb"
     * }
     *
     * Armazena links para perfis em redes sociais.
     *
     * TIPO NO BANCO:
     * - JSONB (binary JSON)
     *
     * TIPO NO JAVA:
     * - Map<String, Object> - chaves são nomes de redes, valores são URLs
     */
    @Type(JsonType.class) // Define tipo JSON customizado
    @Column(name = "redes_sociais", columnDefinition = "jsonb") // Coluna JSONB no banco
    private Map<String, Object> redesSociais; // Mapa para armazenar redes sociais

    // ==================== CAMPOS DE TEXTO SIMPLES ====================

    /**
     * URL do logotipo/logo da instituição.
     *
     * Exemplo: "https://uepb.edu.br/assets/logo.png"
     *
     * Link para a imagem do logo usado em interfaces.
     *
     * CONSTRAINTS NO BANCO:
     * - Pode ser nulo (campo opcional)
     * - VARCHAR(500) (length = 500) - comporta URLs longas
     */
    @Column(name = "logo_url", length = 500) // Coluna opcional de até 500 caracteres
    private String logoUrl; // Armazena URL do logotipo

    /**
     * URL do site oficial da instituição.
     *
     * Exemplo: "https://uepb.edu.br"
     *
     * Link para o website institucional.
     *
     * CONSTRAINTS NO BANCO:
     * - Pode ser nulo (campo opcional)
     * - VARCHAR(500) (length = 500)
     */
    @Column(name = "website", length = 500) // Coluna opcional de até 500 caracteres
    private String website; // Armazena URL do website

    // ==================== CAMPOS DE DATA ====================

    /**
     * Data de fundação da instituição.
     *
     * Exemplo: 1987-04-05 (5 de abril de 1987)
     *
     * Registra quando a instituição foi fundada/criada.
     *
     * TIPO NO BANCO:
     * - TIMESTAMP (data e hora)
     *
     * TIPO NO JAVA:
     * - LocalDateTime (data e hora sem timezone)
     */
    @Column(name = "fundacao") // Coluna opcional do tipo TIMESTAMP
    private LocalDateTime fundacao; // Armazena data de fundação

    // ==================== CAMPOS DE INTEGRAÇÃO EXTERNA ====================

    /**
     * ID da instituição na base de dados OpenAlex.
     *
     * Exemplo: "I123456789"
     *
     * OpenAlex é um catálogo aberto de publicações acadêmicas.
     * Este ID permite vincular a instituição com dados bibliométricos.
     *
     * CONSTRAINTS NO BANCO:
     * - Pode ser nulo (campo opcional)
     * - VARCHAR(50) (length = 50)
     */
    @Column(name = "openalex_institution_id", length = 50) // Coluna opcional de até 50 caracteres
    private String openalexInstitutionId; // Armazena ID do OpenAlex

    /**
     * Research Organization Registry ID (ROR).
     *
     * Exemplo: "https://ror.org/01234567"
     *
     * ROR é um registro global de organizações de pesquisa.
     * Fornece identificadores persistentes para instituições acadêmicas.
     *
     * CONSTRAINTS NO BANCO:
     * - Pode ser nulo (campo opcional)
     * - VARCHAR(100) (length = 100)
     */
    @Column(name = "ror_id", length = 100) // Coluna opcional de até 100 caracteres
    private String rorId; // Armazena ROR ID

    // ==================== CAMPOS DE CONTROLE ====================

    /**
     * Indica se a instituição está ativa no sistema.
     *
     * true = Instituição ativa (aparece em listagens, aceita operações)
     * false = Instituição inativa/desativada (soft delete)
     *
     * Usado para implementar "soft delete" - em vez de deletar fisicamente,
     * apenas marcamos como inativa.
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - DEFAULT true (valor padrão é verdadeiro)
     * - Possui índice para otimizar filtros por ativas
     *
     * LOMBOK @Builder.Default:
     * - Define valor padrão ao usar o Builder
     */
    @Column(name = "ativo", nullable = false) // Coluna obrigatória do tipo BOOLEAN
    @Builder.Default // Define valor padrão quando usar Instituicao.builder()
    private Boolean ativo = true; // Valor padrão é true (ativa)

    /**
     * Configurações específicas da instituição em formato JSON.
     *
     * Exemplo de estrutura:
     * {
     *   "cor_tema": "#0066cc",
     *   "timezone": "America/Sao_Paulo",
     *   "idioma_padrao": "pt-BR",
     *   "permitir_auto_cadastro": true
     * }
     *
     * Armazena configurações personalizáveis de cada instituição.
     * Flexível para adicionar novos campos sem alterar o schema.
     *
     * TIPO NO BANCO:
     * - JSONB (binary JSON)
     *
     * TIPO NO JAVA:
     * - Map<String, Object> - valor padrão é mapa vazio
     *
     * LOMBOK @Builder.Default:
     * - Define Map.of() (mapa vazio imutável) como padrão
     */
    @Type(JsonType.class) // Define tipo JSON customizado
    @Column(name = "configuracoes", columnDefinition = "jsonb") // Coluna JSONB no banco
    @Builder.Default // Define valor padrão no Builder
    private Map<String, Object> configuracoes = Map.of(); // Valor padrão é mapa vazio

    // ==================== CAMPOS DE AUDITORIA (TIMESTAMPS AUTOMÁTICOS) ====================

    /**
     * Data e hora de criação do registro.
     *
     * Preenchido AUTOMATICAMENTE pelo Hibernate na primeira vez que o registro é salvo.
     * Não pode ser alterado depois (updatable = false).
     *
     * Útil para auditoria - saber quando cada instituição foi cadastrada.
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - NOT UPDATABLE (updatable = false)
     * - TIMESTAMP
     *
     * HIBERNATE @CreationTimestamp:
     * - Preenche automaticamente com data/hora atual na criação
     */
    @CreationTimestamp // Hibernate preenche automaticamente na criação
    @Column(name = "created_at", nullable = false, updatable = false) // Obrigatório e não pode ser alterado
    private LocalDateTime createdAt; // Armazena data/hora de criação

    /**
     * Data e hora da última atualização do registro.
     *
     * Preenchido AUTOMATICAMENTE pelo Hibernate toda vez que o registro é alterado.
     * Sempre reflete o momento da última modificação.
     *
     * Útil para auditoria - saber quando cada instituição foi modificada.
     *
     * CONSTRAINTS NO BANCO:
     * - NOT NULL (nullable = false)
     * - TIMESTAMP
     *
     * HIBERNATE @UpdateTimestamp:
     * - Atualiza automaticamente com data/hora atual a cada update
     */
    @UpdateTimestamp // Hibernate atualiza automaticamente em cada alteração
    @Column(name = "updated_at", nullable = false) // Obrigatório
    private LocalDateTime updatedAt; // Armazena data/hora da última atualização

    // ==================== MÉTODOS CALLBACK DO JPA ====================

    /**
     * Callback executado ANTES de persistir (salvar pela primeira vez) a entidade.
     *
     * Annotation @PrePersist indica que este método será chamado automaticamente
     * pelo JPA/Hibernate antes de fazer INSERT no banco.
     *
     * RESPONSABILIDADES:
     * 1. Garante que campo 'ativo' tenha valor padrão true se for nulo
     * 2. Garante que campo 'configuracoes' tenha mapa vazio se for nulo
     * 3. Converte o código para maiúsculo (padronização)
     *
     * QUANDO É CHAMADO:
     * - Antes de entityManager.persist(instituicao)
     * - Antes de repository.save(instituicao) [primeira vez]
     */
    @PrePersist // Annotation que marca método como callback de pré-persistência
    protected void onCreate() { // Método protegido (pode ser chamado por JPA e subclasses)
        // Verifica se o campo 'ativo' está nulo
        if (ativo == null) { // Se ativo for null
            ativo = true; // Define como true (padrão)
        }

        // Verifica se o campo 'configuracoes' está nulo
        if (configuracoes == null) { // Se configuracoes for null
            configuracoes = Map.of(); // Define como mapa vazio imutável
        }

        // Converte código para maiúsculo para padronização
        if (codigo != null) { // Se codigo não for null
            codigo = codigo.toUpperCase(); // Converte para maiúsculo (ex: "uepb" -> "UEPB")
        }
    }

    /**
     * Callback executado ANTES de atualizar a entidade.
     *
     * Annotation @PreUpdate indica que este método será chamado automaticamente
     * pelo JPA/Hibernate antes de fazer UPDATE no banco.
     *
     * RESPONSABILIDADES:
     * - Converte o código para maiúsculo se foi alterado (mantém padronização)
     *
     * QUANDO É CHAMADO:
     * - Antes de fazer UPDATE no banco
     * - Após repository.save(instituicao) [quando já existe]
     */
    @PreUpdate // Annotation que marca método como callback de pré-atualização
    protected void onUpdate() { // Método protegido
        // Converte código para maiúsculo se foi modificado
        if (codigo != null) { // Se codigo não for null
            codigo = codigo.toUpperCase(); // Garante que fica maiúsculo mesmo se alguém alterar
        }
    }

    // ==================== MÉTODOS AUXILIARES (COMPUTED PROPERTIES) ====================

    /**
     * Retorna o endereço completo formatado como String legível.
     *
     * Este é um método auxiliar (computed property) que formata o JSON
     * de endereço em uma string amigável para exibição.
     *
     * FORMATO DE SAÍDA:
     * "Rua Baraúnas, 351 - Campina Grande - PB"
     *
     * LÓGICA:
     * 1. Verifica se há endereço
     * 2. Monta string concatenando: logradouro - cidade - uf
     * 3. Só adiciona campos que existem no JSON
     *
     * @return String com endereço formatado ou "" se não houver endereço
     */
    public String getEnderecoCompleto() { // Método público que retorna String
        // Verifica se o mapa de endereço é nulo ou está vazio
        if (endereco == null || endereco.isEmpty()) { // Se não há dados de endereço
            return ""; // Retorna string vazia
        }

        // Cria um StringBuilder para concatenar as partes do endereço
        // StringBuilder é mais eficiente que concatenação com + para múltiplas strings
        StringBuilder sb = new StringBuilder();

        // Verifica se existe a chave "logradouro" no mapa
        if (endereco.containsKey("logradouro")) { // Se tem logradouro
            sb.append(endereco.get("logradouro")); // Adiciona o valor ao StringBuilder
        }

        // Verifica se existe a chave "cidade" no mapa
        if (endereco.containsKey("cidade")) { // Se tem cidade
            if (sb.length() > 0) sb.append(" - "); // Se já tem algo, adiciona separador " - "
            sb.append(endereco.get("cidade")); // Adiciona a cidade
        }

        // Verifica se existe a chave "uf" (Unidade Federativa - estado) no mapa
        if (endereco.containsKey("uf")) { // Se tem UF
            if (sb.length() > 0) sb.append(" - "); // Se já tem algo, adiciona separador
            sb.append(endereco.get("uf")); // Adiciona a UF
        }

        // Converte o StringBuilder para String e retorna
        return sb.toString(); // Retorna string montada (ex: "Rua X - Campina Grande - PB")
    }

    /**
     * Retorna o contato principal (email ou telefone) da instituição.
     *
     * Este método busca no JSON de contatos e retorna o principal.
     *
     * PRIORIDADE:
     * 1. Tenta retornar email_principal
     * 2. Se não houver, tenta retornar telefone_principal
     * 3. Se não houver nenhum, retorna ""
     *
     * LÓGICA:
     * 1. Verifica se há contatos
     * 2. Procura por "email_principal" primeiro
     * 3. Se não achar, procura por "telefone_principal"
     * 4. Se não achar nada, retorna vazio
     *
     * @return String com email ou telefone principal, ou "" se não houver
     */
    public String getContatoPrincipal() { // Método público que retorna String
        // Verifica se o mapa de contatos é nulo ou está vazio
        if (contatos == null || contatos.isEmpty()) { // Se não há contatos
            return ""; // Retorna string vazia
        }

        // Verifica se existe a chave "email_principal" no mapa
        if (contatos.containsKey("email_principal")) { // Se tem email principal
            // Faz cast para String porque o Map é <String, Object>
            return (String) contatos.get("email_principal"); // Retorna o email
        }

        // Se não achou email, verifica se existe "telefone_principal"
        if (contatos.containsKey("telefone_principal")) { // Se tem telefone principal
            // Faz cast para String
            return (String) contatos.get("telefone_principal"); // Retorna o telefone
        }

        // Se não achou nem email nem telefone
        return ""; // Retorna string vazia
    }
}
