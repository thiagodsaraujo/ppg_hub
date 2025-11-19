# Sprint 4.2 - Dashboards e Relatórios - IMPLEMENTADO

## Resumo da Implementação

Este documento descreve a implementação completa do **Sprint 4.2 - Dashboards e Relatórios** para a FASE 4 do PPG Hub.

Data de implementação: **18/11/2025**

---

## Arquivos Criados

### 1. Migration SQL
**Arquivo:** `src/main/resources/db/migration/V6__create_materialized_views.sql`

**Conteúdo:**
- 3 Views Materializadas:
  - `academic.mv_programa_stats` - Estatísticas consolidadas do programa
  - `academic.mv_producao_docente` - Métricas de produtividade docente
  - `academic.mv_evasao_conclusao` - Análise de evasão e conclusão por coorte

- Função PostgreSQL:
  - `academic.refresh_materialized_views()` - Atualiza todas as views concorrentemente

- Índices únicos para permitir REFRESH CONCURRENTLY
- Comentários completos em todas as colunas
- Grants de permissão para ppg_user
- Refresh inicial das views

**Tamanho:** 11KB

---

### 2. DTOs (4 classes)

#### 2.1. ProgramaStatsDTO
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/dto/ProgramaStatsDTO.java`

**Atributos:**
- Informações do programa (ID, nome, sigla)
- Métricas de docentes (total, permanentes)
- Métricas de discentes (total, mestrandos, doutorandos, ativos, titulados)
- Métricas de disciplinas (total, ofertas ativas)
- Média de notas

**Métodos auxiliares:**
- `calcularTaxaDocentesPermanentes()`
- `calcularTaxaDiscentesAtivos()`
- `calcularRelacaoOrientandoPorDocente()`

#### 2.2. ProducaoDocenteDTO
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/dto/ProducaoDocenteDTO.java`

**Atributos:**
- Dados do docente (ID, nome, e-mail, categoria)
- Métricas de orientação (total, ativos, titulados, evadidos)
- Disciplinas ministradas
- Bancas (total, qualificação, defesa)
- Métricas bibliométricas (publicações, citações, H-index, i10-index)

**Métodos auxiliares:**
- `calcularTaxaSucessoOrientacao()`
- `calcularTaxaEvasaoOrientacao()`
- `calcularMediaCitacoesPorPublicacao()`
- `isAltamenteProdutivo()`

#### 2.3. EvasaoConclusaoDTO
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/dto/EvasaoConclusaoDTO.java`

**Atributos:**
- Dados da coorte (programa, tipo de curso, ano de ingresso)
- Contadores (ingressantes, titulados, evadidos, cursando, trancados)
- Taxas (conclusão, evasão, cursando)
- Tempo médio de titulação

**Métodos auxiliares:**
- `isDentroDoPrazo(int anoAtual)`
- `calcularTaxaRetencao()`
- `isEvasaoCritica()`
- `isConclusaoBaixa()`
- `calcularProjecaoTitulados(double taxaSucessoEsperada)`

#### 2.4. DashboardResponseDTO
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/dto/DashboardResponseDTO.java`

**Atributos:**
- `estatisticas` - ProgramaStatsDTO
- `topDocentes` - List<ProducaoDocenteDTO>
- `evasaoPorAno` - List<EvasaoConclusaoDTO>
- `graficos` - Map<String, Object> (dados para visualizações)

**Métodos auxiliares:**
- `empty()` - Cria dashboard vazio
- `hasData()` - Verifica se há dados
- `getTotalDocentesRanking()`
- `getTotalCoortesAnalisadas()`

---

### 3. Repositories (3 interfaces)

#### 3.1. ProgramaStatsRepository
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/repository/ProgramaStatsRepository.java`

**Métodos:**
- `findByProgramaId(Long programaId)` - Estatísticas de um programa
- `findAllStats()` - Estatísticas de todos os programas
- `findBySigla(String sigla)` - Busca por sigla
- `findTopProgramasByDiscentesAtivos(int limit)` - Top programas por discentes ativos

#### 3.2. ProducaoDocenteRepository
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/repository/ProducaoDocenteRepository.java`

**Métodos:**
- `findByDocenteId(Long docenteId)` - Métricas de um docente
- `findByProgramaId(Long programaId)` - Todos os docentes de um programa
- `findTopDocentesByHIndex(Long programaId, int limit)` - Top N por H-index
- `findByProgramaIdAndCategoria(Long programaId, String categoria)` - Filtro por categoria
- `findTopDocentesByOrientandosAtivos(Long programaId, int limit)` - Top por orientandos

#### 3.3. EvasaoConclusaoRepository
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/repository/EvasaoConclusaoRepository.java`

**Métodos:**
- `findByProgramaId(Long programaId)` - Análise completa de um programa
- `findByProgramaIdAndTipoCurso(Long programaId, String tipoCurso)` - Filtro por tipo
- `findByProgramaIdAndPeriodo(Long programaId, Integer anoInicio, Integer anoFim)` - Período específico
- `findCoortesComEvasaoCritica(Long programaId)` - Coortes com evasão > 20%
- `findCoortesComConclusaoBaixa(Long programaId)` - Coortes com conclusão < 60%
- `findEstatisticasAgregadas(Long programaId)` - Estatísticas agregadas

---

### 4. Services (4 classes)

#### 4.1. PdfReportService
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/service/PdfReportService.java`

**Tecnologia:** iText 8

**Métodos:**
- `generateProgramaStatsPDF(ProgramaStatsDTO, OutputStream)` - Estatísticas em PDF
- `generateProducaoDocentePDF(List<ProducaoDocenteDTO>, String, OutputStream)` - Produção em PDF
- `generateEvasaoConclusaoPDF(List<EvasaoConclusaoDTO>, String, OutputStream)` - Evasão em PDF
- `generateDashboardPDF(ProgramaStatsDTO, List, List, OutputStream)` - Dashboard completo em PDF

**Recursos:**
- Cabeçalhos e rodapés formatados
- Tabelas com cores e espaçamento
- Células com destaque para valores críticos
- Timestamp de geração

#### 4.2. ExcelReportService
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/service/ExcelReportService.java`

**Tecnologia:** Apache POI (XSSF)

**Métodos:**
- `generateProgramaStatsExcel(ProgramaStatsDTO, OutputStream)` - Estatísticas em Excel
- `generateProducaoDocenteExcel(List<ProducaoDocenteDTO>, String, OutputStream)` - Produção em Excel
- `generateEvasaoConclusaoExcel(List<EvasaoConclusaoDTO>, String, OutputStream)` - Evasão em Excel

**Recursos:**
- Estilos customizados (header, título, labels)
- Células com cores para destaque (críticos em vermelho/rosa)
- Auto-size de colunas
- Múltiplas seções por planilha
- Merged regions para títulos

#### 4.3. CsvReportService
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/service/CsvReportService.java`

**Padrão:** RFC 4180

**Métodos:**
- `generateProgramaStatsCSV(ProgramaStatsDTO, OutputStream)` - Estatísticas em CSV
- `generateProducaoDocenteCSV(List<ProducaoDocenteDTO>, String, OutputStream)` - Produção em CSV
- `generateEvasaoConclusaoCSV(List<EvasaoConclusaoDTO>, String, OutputStream)` - Evasão em CSV

**Recursos:**
- Encoding UTF-8 com BOM (compatibilidade Excel)
- Escape correto de valores (RFC 4180)
- Separador: vírgula
- Line break: CRLF (Windows)

#### 4.4. ReportService
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/service/ReportService.java`

**Função:** Orquestrador principal

**Métodos de Busca:**
- `getDashboardPrograma(Long programaId)` - Dashboard completo
- `getProgramaStats(Long programaId)` - Estatísticas
- `getTopDocentes(Long programaId, int limit)` - Top N docentes
- `getEvasaoConclusao(Long programaId)` - Análise de evasão
- `getEvasaoPorPeriodo(Long programaId, int anoInicio, int anoFim)` - Por período

**Métodos de Export:**
- `exportProgramaStatsPDF(Long programaId, OutputStream)` - PDF de estatísticas
- `exportProducaoDocenteExcel(Long programaId, OutputStream)` - Excel de produção
- `exportEvasaoCSV(Long programaId, OutputStream)` - CSV de evasão
- `exportDashboardPDF(Long programaId, OutputStream)` - PDF completo

**Métodos de Manutenção:**
- `refreshMaterializedViews()` - Atualiza views materializadas

**Recursos:**
- Conversão de Map para DTOs
- Preparação de dados para gráficos
- Exception handling robusto
- Logs detalhados

---

### 5. Controller REST

#### ReportController
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/controller/ReportController.java`

**Base Path:** `/api/v1/relatorios`

**Endpoints:**

| Método | Endpoint | Descrição | Permissão | Content-Type |
|--------|----------|-----------|-----------|--------------|
| GET | `/dashboard/programa/{id}` | Dashboard JSON completo | COORDENADOR, ADMIN, DOCENTE | application/json |
| GET | `/programa/{id}/stats.pdf` | Estatísticas em PDF | COORDENADOR, ADMIN, DOCENTE | application/pdf |
| GET | `/programa/{id}/producao.xlsx` | Produção docente em Excel | COORDENADOR, ADMIN, DOCENTE | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet |
| GET | `/programa/{id}/evasao.csv` | Evasão/conclusão em CSV | COORDENADOR, ADMIN, DOCENTE | text/csv |
| GET | `/programa/{id}/dashboard.pdf` | Dashboard completo em PDF | COORDENADOR, ADMIN | application/pdf |
| POST | `/refresh-views` | Atualizar views materializadas | ADMIN | text/plain |

**Recursos:**
- Content-Disposition headers corretos para download
- Nomes de arquivo com timestamp
- Exception handling
- Documentação OpenAPI completa
- Logs de requisições

---

### 6. Job Agendado

#### RefreshViewsJob
**Arquivo:** `src/main/java/br/edu/ppg/hub/integration/reports/job/RefreshViewsJob.java`

**Agendamento:** Todo dia às 01:00 AM (cron: "0 0 1 * * *")

**Função:** Atualização automática das views materializadas

**Métodos:**
- `refreshMaterializedViews()` - Execução agendada
- `executeManualRefresh()` - Refresh manual
- `testJob()` - Teste do job
- `notifyError(Exception)` - Notificação de erros (stub)

**Recursos:**
- Logs detalhados com timestamps
- Medição de tempo de execução
- Exception handling
- Estrutura para notificações (e-mail, Slack)

---

## Dependências Adicionadas no pom.xml

```xml
<!-- iText 8 (para PDF) -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>kernel</artifactId>
    <version>8.0.2</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>layout</artifactId>
    <version>8.0.2</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>io</artifactId>
    <version>8.0.2</version>
</dependency>
```

**Observação:** Apache POI já estava presente no pom.xml.

---

## Padrões Implementados

### Clean Architecture
- Separação em camadas (controller, service, repository, dto)
- Inversão de dependências
- Injeção via @RequiredArgsConstructor

### Boas Práticas
- ✅ @Slf4j para logging
- ✅ @RequiredArgsConstructor (imutabilidade)
- ✅ JavaDoc completo em todas as classes e métodos
- ✅ Try-catch em operações I/O
- ✅ @PreAuthorize para segurança
- ✅ OpenAPI documentation
- ✅ Content-Type corretos
- ✅ Headers de download corretos
- ✅ Exception handling robusto
- ✅ Transactional adequado
- ✅ Métodos auxiliares bem nomeados

### Segurança
- Autenticação JWT obrigatória
- Autorização baseada em roles
- Endpoint de refresh restrito a ADMIN

---

## Estrutura de Arquivos Final

```
src/main/java/br/edu/ppg/hub/integration/reports/
├── controller/
│   └── ReportController.java (1 arquivo)
├── dto/
│   ├── DashboardResponseDTO.java
│   ├── EvasaoConclusaoDTO.java
│   ├── ProducaoDocenteDTO.java
│   └── ProgramaStatsDTO.java (4 arquivos)
├── job/
│   └── RefreshViewsJob.java (1 arquivo)
├── repository/
│   ├── EvasaoConclusaoRepository.java
│   ├── ProducaoDocenteRepository.java
│   └── ProgramaStatsRepository.java (3 arquivos)
└── service/
    ├── CsvReportService.java
    ├── ExcelReportService.java
    ├── PdfReportService.java
    └── ReportService.java (4 arquivos)

src/main/resources/db/migration/
└── V6__create_materialized_views.sql (1 arquivo)

Total: 13 arquivos Java + 1 SQL
```

---

## Exemplos de Uso

### 1. Obter Dashboard JSON

```bash
GET /api/v1/relatorios/dashboard/programa/1
Authorization: Bearer {JWT_TOKEN}
```

**Resposta:**
```json
{
  "estatisticas": {
    "programaId": 1,
    "programaNome": "Programa de Pós-Graduação em Ciência da Computação",
    "programaSigla": "PPGCC",
    "totalDocentes": 25,
    "docentesPermanentes": 20,
    "totalDiscentes": 80,
    "mestrandos": 50,
    "doutorandos": 30,
    "discentesAtivos": 65,
    "titulados": 120,
    "totalDisciplinas": 35,
    "ofertasAtivas": 12,
    "mediaNotas": 8.5
  },
  "topDocentes": [...],
  "evasaoPorAno": [...],
  "graficos": {...}
}
```

### 2. Download de PDF

```bash
GET /api/v1/relatorios/programa/1/stats.pdf
Authorization: Bearer {JWT_TOKEN}
```

**Resposta:**
- Content-Type: application/pdf
- Content-Disposition: attachment; filename="estatisticas_programa_20251118_233000.pdf"

### 3. Atualizar Views (Admin)

```bash
POST /api/v1/relatorios/refresh-views
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Resposta:**
```
Views materializadas atualizadas com sucesso
```

---

## Features Implementadas

### Dashboards
- ✅ Dashboard JSON completo com estatísticas, rankings e análises
- ✅ Dados estruturados para gráficos (pizza, barras, linha)
- ✅ Top 10 docentes por H-index
- ✅ Análise de evasão dos últimos 5 anos

### Relatórios PDF
- ✅ Estatísticas do programa
- ✅ Produção docente
- ✅ Evasão e conclusão
- ✅ Dashboard completo
- ✅ Formatação profissional com cores e tabelas

### Relatórios Excel
- ✅ Estatísticas com múltiplas seções
- ✅ Produção docente com ranking
- ✅ Evasão com destaque para valores críticos
- ✅ Estilos customizados
- ✅ Auto-size de colunas

### Relatórios CSV
- ✅ Estatísticas em formato chave-valor
- ✅ Produção docente completa
- ✅ Evasão com todas as métricas
- ✅ UTF-8 com BOM para Excel
- ✅ RFC 4180 compliant

### Views Materializadas
- ✅ 3 views otimizadas com índices
- ✅ Refresh concorrente (não bloqueia leituras)
- ✅ Função PostgreSQL para refresh automático
- ✅ Job agendado diariamente às 01:00
- ✅ Endpoint manual para refresh (Admin)

---

## Métricas da Implementação

- **Arquivos criados:** 14 (13 Java + 1 SQL)
- **Linhas de código (aprox.):** ~3.500 LOC
- **DTOs:** 4
- **Repositories:** 3
- **Services:** 4
- **Controllers:** 1
- **Jobs:** 1
- **Views materializadas:** 3
- **Endpoints REST:** 6
- **Métodos de export:** 10 (PDF, Excel, CSV)

---

## Próximos Passos (Sugestões)

1. **Testes:**
   - Testes unitários para services
   - Testes de integração para repositories
   - Testes de API para endpoints

2. **Melhorias:**
   - Cache dos resultados de dashboard
   - Paginação nos endpoints JSON
   - Filtros adicionais (data, tipo de curso, etc)
   - Gráficos renderizados no PDF (charts)

3. **Notificações:**
   - Implementar envio de e-mail em caso de erro no job
   - Slack webhook para alertas

4. **Documentação:**
   - Criar exemplos de uso com curl
   - Adicionar screenshots dos relatórios
   - Tutorial de como interpretar as métricas

---

## Conclusão

✅ **Sprint 4.2 - Dashboards e Relatórios COMPLETO**

Todos os requisitos foram implementados com alta qualidade:
- Clean Architecture
- Documentação completa
- Exception handling robusto
- Segurança adequada
- Múltiplos formatos de export
- Views materializadas otimizadas
- Job de atualização automática

O módulo está pronto para uso em produção após testes adequados.

---

**Implementado por:** Claude Code
**Data:** 18/11/2025
**Versão PPG Hub:** 0.1.0
**FASE 4 - Sprint 4.2**
