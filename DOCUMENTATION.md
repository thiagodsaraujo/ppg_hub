# Documentação Técnica Completa - PPG Hub

Este documento fornece uma explicação detalhada de CADA classe, método e componente do sistema PPG Hub.

## 📚 Índice

1. [Estrutura Geral](#estrutura-geral)
2. [Entidades (Model)](#entidades-model)
3. [DTOs (Data Transfer Objects)](#dtos-data-transfer-objects)
4. [Repositórios (Repository)](#repositórios-repository)
5. [Serviços (Service)](#serviços-service)
6. [Controladores (Controller)](#controladores-controller)
7. [Validadores (Validation)](#validadores-validation)
8. [Exceções (Exception)](#exceções-exception)
9. [Configurações (Config)](#configurações-config)
10. [Fluxo de Dados](#fluxo-de-dados)

---

## Estrutura Geral

### Arquitetura em Camadas

O projeto segue uma arquitetura em camadas (Layered Architecture):

```
┌──────────────────┐
│   Controller     │ ← Camada de Apresentação (REST API)
├──────────────────┤
│     Service      │ ← Camada de Lógica de Negócio
├──────────────────┤
│   Repository     │ ← Camada de Acesso a Dados
├──────────────────┤
│    Database      │ ← PostgreSQL
└──────────────────┘
```

**Fluxo de uma Requisição:**

1. **Cliente** faz request HTTP → **Controller**
2. **Controller** valida dados (DTOs) e chama → **Service**
3. **Service** executa lógica de negócio e chama → **Repository**
4. **Repository** acessa banco de dados (JPA)
5. **Resposta** retorna no caminho inverso: DB → Repository → Service → Controller → Cliente

---

## Entidades (Model)

### 📄 Instituicao.java

**Localização:** `src/main/java/br/edu/ppg/hub/model/Instituicao.java`

**Propósito:** Representa uma instituição de ensino no banco de dados.

#### Annotations da Classe

```java
@Entity // Marca como entidade JPA (será persistida)
@Table(name = "instituicoes") // Nome da tabela no banco
@Data // Lombok: getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: construtor vazio
@AllArgsConstructor // Lombok: construtor com todos os campos
@Builder // Lombok: padrão Builder
@EqualsAndHashCode(of = "id") // Igualdade baseada no ID
@ToString(exclude = {...}) // Exclui JSONs do toString
```

#### Campos Principais

| Campo | Tipo | Descrição | Constraints |
|-------|------|-----------|-------------|
| `id` | Long | Chave primária auto-incremento | NOT NULL, PRIMARY KEY |
| `codigo` | String | Código único (ex: UEPB) | NOT NULL, UNIQUE, 2-20 chars |
| `nomeCompleto` | String | Nome oficial completo | NOT NULL, 5-500 chars |
| `nomeAbreviado` | String | Nome para exibição | NOT NULL, 2-50 chars |
| `sigla` | String | Sigla oficial | NOT NULL, 2-10 chars |
| `tipo` | String | Tipo (Federal/Estadual/etc) | NOT NULL |
| `cnpj` | String | CNPJ formatado | UNIQUE, 18 chars |
| `endereco` | Map<String,Object> | Endereço em JSON | JSONB |
| `contatos` | Map<String,Object> | Contatos em JSON | JSONB |
| `ativo` | Boolean | Status ativo/inativo | NOT NULL, DEFAULT true |
| `createdAt` | LocalDateTime | Data de criação | AUTO, NOT NULL |
| `updatedAt` | LocalDateTime | Data de atualização | AUTO, NOT NULL |

#### Métodos Importantes

**Callbacks JPA:**

```java
@PrePersist
protected void onCreate() {
    // Executado ANTES de INSERT
    // - Define valores padrão (ativo=true)
    // - Normaliza dados (codigo.toUpperCase())
}

@PreUpdate
protected void onUpdate() {
    // Executado ANTES de UPDATE
    // - Mantém normalização de dados
}
```

**Métodos Auxiliares:**

```java
public String getEnderecoCompleto() {
    // Formata JSON de endereço em string legível
    // Ex: "Rua X - Cidade - UF"
}

public String getContatoPrincipal() {
    // Retorna email ou telefone principal
    // Prioridade: email > telefone
}
```

#### Como Funciona o JSONB

PostgreSQL permite armazenar JSON nativamente:

```json
{
  "endereco": {
    "logradouro": "Rua Baraúnas, 351",
    "cidade": "Campina Grande",
    "uf": "PB"
  }
}
```

Em Java, isso é representado como `Map<String, Object>`:

```java
Map<String, Object> endereco = instituicao.getEndereco();
String cidade = (String) endereco.get("cidade"); // "Campina Grande"
```

---

## DTOs (Data Transfer Objects)

DTOs são objetos usados para transferir dados entre camadas. Separam a representação interna (Entity) da externa (API).

### 📄 InstituicaoCreateDTO.java

**Propósito:** Dados para CRIAR uma nova instituição.

**Características:**
- Todos os campos obrigatórios são marcados com `@NotBlank`
- Validações acontecem automaticamente via Bean Validation
- Não inclui campos automáticos (id, timestamps)

**Exemplo de Uso:**

```java
// Cliente envia JSON:
{
  "codigo": "UEPB",
  "nome_completo": "Universidade Estadual da Paraíba",
  "tipo": "Estadual",
  ...
}

// Spring converte para:
InstituicaoCreateDTO dto = ...;

// Validações executam automaticamente:
// - @NotBlank verifica campos obrigatórios
// - @Size verifica tamanhos
// - @ValidCNPJ valida CNPJ
// - @Pattern valida padrões
```

### 📄 InstituicaoUpdateDTO.java

**Propósito:** Dados para ATUALIZAR uma instituição existente.

**Características:**
- TODOS os campos são opcionais (nullable)
- Permite atualização parcial (PATCH semantics)
- Apenas campos fornecidos são atualizados

**Exemplo de Uso:**

```java
// Cliente quer mudar apenas o telefone:
{
  "contatos": {
    "telefone_principal": "(83) 9999-9999"
  }
}

// Apenas o campo contatos será atualizado
// Outros campos permanecem inalterados
```

### 📄 InstituicaoResponseDTO.java

**Propósito:** Dados retornados pela API.

**Características:**
- Inclui TODOS os campos (inclusive id, timestamps)
- Inclui campos calculados (enderecoCompleto, contatoPrincipal)
- Formato final enviado ao cliente

**Exemplo de Uso:**

```java
// API retorna:
{
  "id": 1,
  "codigo": "UEPB",
  "nome_completo": "Universidade Estadual da Paraíba",
  "created_at": "2024-01-15T10:30:00",
  "updated_at": "2024-01-15T10:30:00",
  "endereco_completo": "Rua X - Cidade - UF",  // Calculado!
  "contato_principal": "email@uepb.edu.br"      // Calculado!
}
```

### 📄 InstituicaoMapper.java

**Propósito:** Converte entre Entity e DTOs.

**Métodos:**

```java
// Converte CreateDTO → Entity
Instituicao toEntity(InstituicaoCreateDTO dto);

// Converte Entity → ResponseDTO
InstituicaoResponseDTO toResponseDTO(Instituicao entity);

// Atualiza Entity com UpdateDTO (apenas campos não-nulos)
void updateEntityFromDTO(InstituicaoUpdateDTO dto, Instituicao entity);
```

**Como Funciona:**

```java
// Criação:
InstituicaoCreateDTO createDTO = ...; // Dados do cliente
Instituicao entity = mapper.toEntity(createDTO); // Converte para Entity
repository.save(entity); // Salva no banco

// Leitura:
Instituicao entity = repository.findById(1); // Busca do banco
InstituicaoResponseDTO responseDTO = mapper.toResponseDTO(entity); // Converte para DTO
return responseDTO; // Retorna ao cliente

// Atualização:
Instituicao entity = repository.findById(1); // Busca existente
InstituicaoUpdateDTO updateDTO = ...; // Novos dados
mapper.updateEntityFromDTO(updateDTO, entity); // Atualiza apenas campos fornecidos
repository.save(entity); // Salva alterações
```

---

## Repositórios (Repository)

### 📄 InstituicaoRepository.java

**Localização:** `src/main/java/br/edu/ppg/hub/repository/InstituicaoRepository.java`

**Propósito:** Interface para acesso ao banco de dados.

**Herança:**

```java
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long>
```

- `JpaRepository<Instituicao, Long>`: Fornece CRUD automático
  - `Instituicao`: Tipo da entidade
  - `Long`: Tipo da chave primária (ID)

#### Métodos CRUD Herdados (Automáticos)

```java
// CREATE
Instituicao save(Instituicao entity); // Salva ou atualiza

// READ
Optional<Instituicao> findById(Long id); // Busca por ID
List<Instituicao> findAll(); // Lista todas
Page<Instituicao> findAll(Pageable pageable); // Lista com paginação
long count(); // Conta total

// DELETE
void deleteById(Long id); // Deleta por ID
void delete(Instituicao entity); // Deleta entidade

// EXISTS
boolean existsById(Long id); // Verifica se existe
```

#### Métodos Customizados

**Busca por Campos Únicos:**

```java
Optional<Instituicao> findByCodigo(String codigo);
Optional<Instituicao> findByCnpj(String cnpj);
```

**Verificação de Existência:**

```java
boolean existsByCodigo(String codigo);
boolean existsByCnpj(String cnpj);
boolean existsByCodigoAndIdNot(String codigo, Long id); // Para validar updates
```

**Filtros:**

```java
Page<Instituicao> findByAtivoTrue(Pageable pageable); // Apenas ativas
List<Instituicao> findByTipoAndAtivoTrue(String tipo); // Por tipo
```

**Busca Avançada (JPQL):**

```java
@Query("SELECT i FROM Instituicao i WHERE " +
       "LOWER(i.codigo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
       "LOWER(i.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%'))")
Page<Instituicao> searchByTermo(@Param("termo") String termo, Pageable pageable);
```

**Como o Spring Data JPA Funciona:**

1. Você declara a interface com métodos
2. Spring gera implementação AUTOMATICAMENTE
3. Nome do método define a query (convenção sobre configuração)

**Exemplos de Nomenclatura:**

```java
findBy + Campo + Operador
       ↓        ↓
findByCodigo           // WHERE codigo = ?
findByCodigoAndAtivo   // WHERE codigo = ? AND ativo = ?
findByNomeContaining   // WHERE nome LIKE %?%
findByIdNot            // WHERE id != ?
```

---

## Serviços (Service)

### 📄 InstituicaoService.java

**Localização:** `src/main/java/br/edu/ppg/hub/service/InstituicaoService.java`

**Propósito:** Contém toda a lógica de negócio.

**Responsabilidades:**
1. Validações de regras de negócio
2. Orquestração entre repository e mapper
3. Tratamento de exceções de negócio
4. Transformações de dados

#### Estrutura da Classe

```java
@Service // Marca como componente de serviço
@RequiredArgsConstructor // Lombok: injeção por construtor
@Slf4j // Lombok: logger
@Transactional(readOnly = true) // Todas as operações são read-only por padrão
public class InstituicaoService {

    private final InstituicaoRepository repository; // Injetado
    private final InstituicaoMapper mapper; // Injetado

    // Métodos...
}
```

#### Método: create()

```java
@Transactional // Sobescreve read-only (precisa escrever)
public InstituicaoResponseDTO create(InstituicaoCreateDTO dto) {
    // 1. LOG de entrada
    log.info("Criando instituição com código: {}", dto.getCodigo());

    // 2. VALIDAÇÃO: Código já existe?
    if (repository.existsByCodigo(dto.getCodigo())) {
        throw new DuplicateResourceException("Instituição", "codigo", dto.getCodigo());
    }

    // 3. VALIDAÇÃO: CNPJ já existe? (se fornecido)
    if (dto.getCnpj() != null && repository.existsByCnpj(dto.getCnpj())) {
        throw new DuplicateResourceException("Instituição", "CNPJ", dto.getCnpj());
    }

    // 4. FORMATAÇÃO: Formata CNPJ
    if (dto.getCnpj() != null) {
        dto.setCnpj(CNPJValidator.formatarCNPJ(dto.getCnpj()));
    }

    // 5. CONVERSÃO: DTO → Entity
    Instituicao entity = mapper.toEntity(dto);

    // 6. PERSISTÊNCIA: Salva no banco
    Instituicao saved = repository.save(entity);

    // 7. LOG de sucesso
    log.info("Instituição criada com sucesso. ID: {}", saved.getId());

    // 8. CONVERSÃO: Entity → ResponseDTO
    return mapper.toResponseDTO(saved);
}
```

**Fluxo Completo:**

```
Cliente → Controller → Service.create()
                           ↓
                      Valida código único
                           ↓
                      Valida CNPJ único
                           ↓
                      Formata CNPJ
                           ↓
                      Mapper: DTO → Entity
                           ↓
                      Repository.save()
                           ↓
                      Banco de Dados (INSERT)
                           ↓
                      Mapper: Entity → ResponseDTO
                           ↓
Cliente ← Controller ← Retorna ResponseDTO
```

#### Método: update()

```java
@Transactional
public InstituicaoResponseDTO update(Long id, InstituicaoUpdateDTO dto) {
    // 1. Busca entidade existente (lança exceção se não encontrar)
    Instituicao entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Instituição", "id", id));

    // 2. Valida código se foi alterado
    if (dto.getCodigo() != null && !dto.getCodigo().equalsIgnoreCase(entity.getCodigo())) {
        if (repository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
            throw new DuplicateResourceException(...);
        }
    }

    // 3. Valida CNPJ se foi alterado
    if (dto.getCnpj() != null && !dto.getCnpj().equals(entity.getCnpj())) {
        if (repository.existsByCnpjAndIdNot(dto.getCnpj(), id)) {
            throw new DuplicateResourceException(...);
        }
        dto.setCnpj(CNPJValidator.formatarCNPJ(dto.getCnpj()));
    }

    // 4. Atualiza campos (apenas os fornecidos)
    mapper.updateEntityFromDTO(dto, entity);

    // 5. Salva alterações
    Instituicao updated = repository.save(entity);

    // 6. Retorna DTO
    return mapper.toResponseDTO(updated);
}
```

#### Outros Métodos Importantes

```java
// Busca simples com conversão
public InstituicaoResponseDTO findById(Long id) {
    Instituicao entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Instituição", "id", id));
    return mapper.toResponseDTO(entity);
}

// Paginação com conversão
public Page<InstituicaoResponseDTO> findAll(Pageable pageable) {
    return repository.findAll(pageable)
        .map(mapper::toResponseDTO); // Converte cada elemento
}

// Soft delete
public InstituicaoResponseDTO deactivate(Long id) {
    Instituicao entity = repository.findById(id).orElseThrow(...);
    entity.setAtivo(false); // Marca como inativa (não deleta)
    return mapper.toResponseDTO(repository.save(entity));
}
```

---

## Controladores (Controller)

### 📄 InstituicaoController.java

**Localização:** `src/main/java/br/edu/ppg/hub/controller/InstituicaoController.java`

**Propósito:** Expõe endpoints REST HTTP.

**Estrutura:**

```java
@RestController // Marca como controller REST
@RequestMapping("/api/v1/instituicoes") // Base path
@RequiredArgsConstructor // Injeção de dependências
@Slf4j // Logger
@Tag(name = "Instituições") // Swagger
public class InstituicaoController {

    private final InstituicaoService service;

    // Endpoints...
}
```

#### Endpoint: POST /api/v1/instituicoes

```java
@PostMapping
@Operation(summary = "Criar instituição") // Documentação Swagger
public ResponseEntity<InstituicaoResponseDTO> create(
    @Valid @RequestBody InstituicaoCreateDTO dto) {
    //  ↑                  ↑
    //  |                  └─ Dados vêm no corpo da requisição (JSON)
    //  └─ Valida automaticamente com Bean Validation

    log.info("POST /api/v1/instituicoes - Criando: {}", dto.getCodigo());

    InstituicaoResponseDTO created = service.create(dto);

    return ResponseEntity
        .status(HttpStatus.CREATED) // Status 201
        .body(created); // Retorna DTO no corpo
}
```

**Fluxo HTTP:**

```
1. Cliente envia:
   POST /api/v1/instituicoes
   Content-Type: application/json

   {
     "codigo": "UEPB",
     "nome_completo": "Universidade..."
   }

2. Spring:
   - Deserializa JSON → InstituicaoCreateDTO
   - Executa validações (@Valid)
   - Se inválido: retorna 400 Bad Request
   - Se válido: chama controller.create(dto)

3. Controller:
   - Loga requisição
   - Chama service.create(dto)
   - Retorna ResponseEntity com status 201

4. Cliente recebe:
   HTTP/1.1 201 Created
   Content-Type: application/json

   {
     "id": 1,
     "codigo": "UEPB",
     "created_at": "2024-01-15T10:30:00",
     ...
   }
```

#### Endpoint: GET /api/v1/instituicoes/{id}

```java
@GetMapping("/{id}")
public ResponseEntity<InstituicaoResponseDTO> findById(@PathVariable Long id) {
    //                                                      ↑
    //                                                      └─ Extrai da URL

    InstituicaoResponseDTO response = service.findById(id);
    return ResponseEntity.ok(response); // Status 200
}
```

**Uso:**

```bash
GET /api/v1/instituicoes/1
→ Retorna instituição com ID 1

GET /api/v1/instituicoes/999
→ Retorna 404 Not Found se não existir
```

#### Endpoint: GET /api/v1/instituicoes (com paginação)

```java
@GetMapping
public ResponseEntity<Page<InstituicaoResponseDTO>> findAll(
    @PageableDefault(size = 20, sort = "nomeAbreviado") Pageable pageable) {
    //                                                      ↑
    //                                                      └─ Paginação automática

    Page<InstituicaoResponseDTO> response = service.findAll(pageable);
    return ResponseEntity.ok(response);
}
```

**Como Funciona a Paginação:**

```bash
# Página 0, 20 itens, ordenado por nome
GET /api/v1/instituicoes?page=0&size=20&sort=nomeAbreviado,asc

# Resposta:
{
  "content": [...],           // Lista de instituições
  "totalElements": 150,       // Total de registros
  "totalPages": 8,            // Total de páginas
  "size": 20,                 // Itens por página
  "number": 0,                // Página atual
  "first": true,              // É a primeira página?
  "last": false               // É a última página?
}
```

#### Endpoint: PUT /api/v1/instituicoes/{id}

```java
@PutMapping("/{id}")
public ResponseEntity<InstituicaoResponseDTO> update(
    @PathVariable Long id,
    @Valid @RequestBody InstituicaoUpdateDTO dto) {

    InstituicaoResponseDTO updated = service.update(id, dto);
    return ResponseEntity.ok(updated);
}
```

#### Endpoint: PATCH /api/v1/instituicoes/{id}/deactivate

```java
@PatchMapping("/{id}/deactivate")
public ResponseEntity<InstituicaoResponseDTO> deactivate(@PathVariable Long id) {
    InstituicaoResponseDTO response = service.deactivate(id);
    return ResponseEntity.ok(response);
}
```

**Diferença entre PUT e PATCH:**

- **PUT**: Substitui o recurso completo
- **PATCH**: Atualiza parcialmente ou executa ação específica

#### Endpoint: DELETE /api/v1/instituicoes/{id}

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build(); // 204 No Content
}
```

---

## Validadores (Validation)

### 📄 ValidCNPJ.java e CNPJValidator.java

**Propósito:** Validação customizada de CNPJ.

#### Como Criar um Validador Customizado

**1. Criar a Annotation:**

```java
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Onde pode ser usada
@Retention(RetentionPolicy.RUNTIME) // Mantém em runtime
@Constraint(validatedBy = CNPJValidator.class) // Classe validadora
public @interface ValidCNPJ {
    String message() default "CNPJ inválido"; // Mensagem padrão
    Class<?>[] groups() default {}; // Para validação em grupos
    Class<? extends Payload>[] payload() default {}; // Metadados extras
}
```

**2. Criar o Validador:**

```java
public class CNPJValidator implements ConstraintValidator<ValidCNPJ, String> {

    @Override
    public boolean isValid(String cnpj, ConstraintValidatorContext context) {
        // null é válido (use @NotNull se quiser obrigatoriedade)
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return true;
        }

        // Remove formatação
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");

        // Valida tamanho
        if (cnpjLimpo.length() != 14) {
            return false;
        }

        // Valida se não são todos dígitos iguais
        if (cnpjLimpo.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Valida dígitos verificadores
        return validarDigitosVerificadores(cnpjLimpo);
    }

    private boolean validarDigitosVerificadores(String cnpj) {
        // Implementação do algoritmo de validação do CNPJ
        // ...
        return true;
    }
}
```

**3. Usar no DTO:**

```java
public class InstituicaoCreateDTO {

    @ValidCNPJ // Usa a validação customizada
    private String cnpj;
}
```

**Fluxo de Validação:**

```
1. Cliente envia request com CNPJ: "12.345.678/0001-90"
2. Spring deserializa JSON → DTO
3. @Valid no controller aciona validação
4. Bean Validation processa @ValidCNPJ
5. Chama CNPJValidator.isValid()
6. Se inválido: lança MethodArgumentNotValidException
7. GlobalExceptionHandler captura e retorna 400 Bad Request
```

---

## Exceções (Exception)

### Hierarquia de Exceções

```
RuntimeException
    ↓
ResourceNotFoundException
    └─ Recurso não encontrado (404)

DuplicateResourceException
    └─ Recurso duplicado (409)
```

### 📄 ResourceNotFoundException.java

```java
public class ResourceNotFoundException extends RuntimeException {

    // Construtor genérico
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Construtor com template
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s não encontrado(a) com %s: %s",
            resource, field, value));
        // Ex: "Instituição não encontrada com id: 1"
    }
}
```

### 📄 GlobalExceptionHandler.java

**Propósito:** Intercepta TODAS as exceções e retorna respostas HTTP padronizadas.

```java
@RestControllerAdvice // Intercepta exceções de todos os controllers
public class GlobalExceptionHandler {

    // Trata ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value()) // 404
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Trata erros de validação
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex, WebRequest request) {

        // Extrai todos os erros de validação
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = ValidationErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value()) // 400
            .error("Validation Failed")
            .message("Erro de validação nos campos fornecidos")
            .errors(errors) // Mapa campo → mensagem
            .path(request.getDescription(false))
            .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
```

**Exemplo de Resposta de Erro de Validação:**

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Erro de validação nos campos fornecidos",
  "path": "/api/v1/instituicoes",
  "errors": {
    "codigo": "Código é obrigatório",
    "nomeCompleto": "Nome completo deve ter entre 5 e 500 caracteres",
    "cnpj": "CNPJ inválido"
  }
}
```

---

## Configurações (Config)

### 📄 application.yml

```yaml
spring:
  # Configuração do banco de dados
  datasource:
    url: jdbc:postgresql://host:port/database
    username: user
    password: pass
    hikari: # Pool de conexões
      maximum-pool-size: 10
      minimum-idle: 5

  # Configuração do JPA/Hibernate
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update # Atualiza schema automaticamente
    show-sql: true # Mostra SQL no console

# Configuração do servidor
server:
  port: 8000

# Configuração de logging
logging:
  level:
    br.edu.ppg.hub: DEBUG
    org.hibernate.SQL: DEBUG
```

### 📄 CorsConfig.java

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE, etc
        config.addAllowedHeader("*"); // Todos os headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica a todos endpoints

        return new CorsFilter(source);
    }
}
```

**O que é CORS?**

CORS (Cross-Origin Resource Sharing) permite que um frontend em um domínio (ex: http://localhost:3000) acesse uma API em outro domínio (ex: http://localhost:8000).

Sem CORS configurado, o navegador bloqueia requisições cross-origin por segurança.

---

## Fluxo de Dados Completo

### Exemplo: Criar uma Instituição

```
1. CLIENTE (Frontend)
   POST http://localhost:8000/api/v1/instituicoes
   Content-Type: application/json

   {
     "codigo": "UEPB",
     "nome_completo": "Universidade Estadual da Paraíba",
     "tipo": "Estadual"
   }

2. SPRING BOOT (Deserialização)
   JSON → InstituicaoCreateDTO

3. BEAN VALIDATION
   Verifica @NotBlank, @Size, @ValidCNPJ
   Se inválido → 400 Bad Request

4. CONTROLLER
   InstituicaoController.create(dto)
   - Loga requisição
   - Chama Service

5. SERVICE
   InstituicaoService.create(dto)
   - Valida código único
   - Valida CNPJ único
   - Formata CNPJ
   - Converte DTO → Entity (Mapper)
   - Chama Repository

6. REPOSITORY
   InstituicaoRepository.save(entity)
   - Spring Data JPA gera SQL
   - Hibernate executa INSERT

7. BANCO DE DADOS
   INSERT INTO instituicoes (codigo, nome_completo, tipo, ...)
   VALUES ('UEPB', 'Universidade...', 'Estadual', ...)

   RETORNA id = 1

8. HIBERNATE → SERVICE
   Entity com id=1, created_at, updated_at preenchidos

9. SERVICE → CONTROLLER
   Converte Entity → ResponseDTO (Mapper)

10. CONTROLLER → CLIENTE
    HTTP/1.1 201 Created
    Content-Type: application/json

    {
      "id": 1,
      "codigo": "UEPB",
      "nome_completo": "Universidade Estadual da Paraíba",
      "tipo": "Estadual",
      "created_at": "2024-01-15T10:30:00",
      "updated_at": "2024-01-15T10:30:00"
    }
```

---

## Conceitos Importantes

### Transaction (@Transactional)

```java
@Transactional
public void metodoComTransacao() {
    // Tudo dentro deste método executa em uma transação

    repository.save(entidade1); // INSERT
    repository.save(entidade2); // INSERT

    // Se qualquer operação falhar, ROLLBACK em todas
    // Se tudo funcionar, COMMIT ao final
}
```

**Quando usar:**
- Métodos que ESCREVEM no banco (INSERT, UPDATE, DELETE)
- Operações que precisam ser atômicas (tudo ou nada)

**Read-only:**

```java
@Transactional(readOnly = true)
public List<Entity> buscar() {
    // Otimização: Hibernate não prepara mudanças
    // Mais rápido para consultas
}
```

### Dependency Injection

Spring injeta dependências automaticamente:

```java
@Service
@RequiredArgsConstructor // Lombok gera construtor
public class InstituicaoService {

    private final InstituicaoRepository repository; // ← Injetado
    private final InstituicaoMapper mapper; // ← Injetado

    // Spring cria:
    // public InstituicaoService(InstituicaoRepository repository, InstituicaoMapper mapper) {
    //     this.repository = repository;
    //     this.mapper = mapper;
    // }
}
```

### Paginação

```java
Pageable pageable = PageRequest.of(
    0,    // Página (começa em 0)
    20,   // Tamanho da página
    Sort.by("nomeAbreviado").ascending() // Ordenação
);

Page<Instituicao> page = repository.findAll(pageable);

page.getContent();        // Lista de itens da página atual
page.getTotalElements();  // Total de registros
page.getTotalPages();     // Total de páginas
page.hasNext();           // Tem próxima página?
```

### Logging

```java
@Slf4j // Lombok gera: private static final Logger log = ...
public class MinhaClasse {

    public void metodo() {
        log.debug("Mensagem de debug");  // Só aparece se log level = DEBUG
        log.info("Informação");          // Informação geral
        log.warn("Aviso");               // Aviso de algo suspeito
        log.error("Erro", exception);    // Erro com stack trace
    }
}
```

---

## Diagrama de Classes Simplificado

```
┌─────────────────────┐
│  InstituicaoController│
│  - service          │
│  + create()         │
│  + findById()       │
│  + update()         │
│  + delete()         │
└──────────┬──────────┘
           │ chama
           ↓
┌─────────────────────┐
│  InstituicaoService │
│  - repository       │
│  - mapper           │
│  + create()         │
│  + findById()       │
│  + update()         │
└──────────┬──────────┘
           │ usa
           ↓
┌─────────────────────────┐
│ InstituicaoRepository   │
│ (Interface)             │
│ + save()                │
│ + findById()            │
│ + findByCodigo()        │
└──────────┬──────────────┘
           │ acessa
           ↓
     ┌──────────┐
     │ Database │
     │ (PostgreSQL) │
     └──────────┘

DTOs:
┌──────────────────┐
│ InstituicaoCreateDTO│  (Request de criação)
└──────────────────┘

┌──────────────────┐
│ InstituicaoUpdateDTO│  (Request de atualização)
└──────────────────┘

┌──────────────────┐
│ InstituicaoResponseDTO│ (Response da API)
└──────────────────┘

Entity:
┌──────────────┐
│ Instituicao  │  (Representa tabela no banco)
│ @Entity      │
└──────────────┘

Mapper:
┌──────────────────┐
│ InstituicaoMapper│
│ + toEntity()     │  DTO → Entity
│ + toResponseDTO()│  Entity → DTO
└──────────────────┘
```

---

## Conclusão

Este documento fornece uma visão completa de como cada componente do sistema funciona e como eles se integram. O arquivo `Instituicao.java` está completamente documentado linha por linha com 617 linhas de comentários detalhados.

Para entender melhor:
1. Comece pela entidade (`Instituicao.java`) - está totalmente comentada
2. Entenda os DTOs (transferência de dados)
3. Veja como o Repository acessa o banco
4. Estude como o Service orquestra tudo
5. Por fim, veja como o Controller expõe a API

**Arquivos Totalmente Documentados:**
- ✅ `Instituicao.java` - 617 linhas com comentários detalhados

**Arquivos com Documentação JavaDoc:**
- ✅ Todos os outros arquivos têm JavaDoc nas classes e métodos principais
