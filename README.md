# PPG Hub - Sistema de Gestão para Programas de Pós-Graduação

Sistema completo para gerenciamento de programas de pós-graduação, desenvolvido com **Java Spring Boot**.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM
- **PostgreSQL** - Banco de dados
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI** - Documentação da API (Swagger)
- **MapStruct** - Mapeamento de DTOs
- **Maven** - Gerenciamento de dependências

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🔧 Instalação e Configuração

### 1. Clone o repositório

```bash
git clone <repository-url>
cd ppg_hub
```

### 2. Configure o banco de dados

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE ppg_hub;
```

### 3. Configure as variáveis de ambiente

Edite o arquivo `src/main/resources/application.yml` com suas configurações:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ppg_hub
    username: seu_usuario
    password: sua_senha
```

Ou use variáveis de ambiente:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/ppg_hub
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha
```

### 4. Compile o projeto

```bash
mvn clean install
```

### 5. Execute a aplicação

```bash
mvn spring-boot:run
```

Ou execute o JAR compilado:

```bash
java -jar target/ppg-hub-0.1.0.jar
```

## 📚 Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8000/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8000/api-docs

## 🛠️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── br/edu/ppg/hub/
│   │       ├── config/          # Configurações
│   │       ├── controller/      # Controllers REST
│   │       ├── dto/             # Data Transfer Objects
│   │       │   └── mapper/      # Mappers DTO ↔ Entity
│   │       ├── exception/       # Exceções customizadas
│   │       ├── model/           # Entidades JPA
│   │       ├── repository/      # Repositories Spring Data
│   │       ├── service/         # Lógica de negócio
│   │       ├── validation/      # Validadores customizados
│   │       └── PpgHubApplication.java  # Classe principal
│   └── resources/
│       └── application.yml      # Configurações da aplicação
└── test/                        # Testes unitários e integração
```

## 🔌 Endpoints Principais

### Instituições

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET    | `/api/v1/instituicoes` | Lista todas as instituições |
| GET    | `/api/v1/instituicoes/{id}` | Busca por ID |
| GET    | `/api/v1/instituicoes/codigo/{codigo}` | Busca por código |
| GET    | `/api/v1/instituicoes/cnpj/{cnpj}` | Busca por CNPJ |
| GET    | `/api/v1/instituicoes/ativas` | Lista instituições ativas |
| GET    | `/api/v1/instituicoes/search?termo={termo}` | Busca por termo |
| GET    | `/api/v1/instituicoes/tipo/{tipo}` | Lista por tipo |
| GET    | `/api/v1/instituicoes/stats` | Estatísticas |
| POST   | `/api/v1/instituicoes` | Cria nova instituição |
| PUT    | `/api/v1/instituicoes/{id}` | Atualiza instituição |
| DELETE | `/api/v1/instituicoes/{id}` | Remove instituição |
| PATCH  | `/api/v1/instituicoes/{id}/activate` | Ativa instituição |
| PATCH  | `/api/v1/instituicoes/{id}/deactivate` | Desativa instituição |

## 📝 Exemplo de Uso

### Criar Instituição

```bash
curl -X POST http://localhost:8000/api/v1/instituicoes \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "UEPB",
    "nome_completo": "Universidade Estadual da Paraíba",
    "nome_abreviado": "UEPB",
    "sigla": "UEPB",
    "tipo": "Estadual",
    "cnpj": "12.345.678/0001-90",
    "website": "https://uepb.edu.br",
    "ativo": true
  }'
```

### Listar Instituições

```bash
curl http://localhost:8000/api/v1/instituicoes?page=0&size=20
```

### Buscar por Código

```bash
curl http://localhost:8000/api/v1/instituicoes/codigo/UEPB
```

## 🧪 Testes

Execute os testes com:

```bash
mvn test
```

Para gerar relatório de cobertura:

```bash
mvn test jacoco:report
```

## 🏗️ Build para Produção

```bash
mvn clean package -DskipTests
```

O JAR será gerado em `target/ppg-hub-0.1.0.jar`

## 🐳 Docker

### Build da imagem

```bash
docker build -t ppg-hub:latest .
```

### Executar com Docker Compose

```bash
docker-compose up -d
```

## 🔐 Segurança

- Validação de entrada com Bean Validation
- Validadores customizados (CNPJ, Código)
- Tratamento de exceções globalizado
- CORS configurável
- Prepared statements (proteção contra SQL Injection)

## 📊 Features

- ✅ CRUD completo de instituições
- ✅ Busca avançada e filtros
- ✅ Paginação e ordenação
- ✅ Validação de dados
- ✅ Tratamento de erros
- ✅ Documentação Swagger
- ✅ Suporte a JSON para campos complexos
- ✅ Soft delete (ativar/desativar)
- ✅ Auditoria (created_at, updated_at)
- ✅ Estatísticas e relatórios

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

## 👥 Autores

- PPG Team

## 📧 Contato

- Email: admin@ppg.edu.br

## 🔄 Changelog

### v0.1.0 (2024-01-15)
- Implementação inicial do sistema
- CRUD de instituições
- Documentação Swagger
- Validações customizadas
