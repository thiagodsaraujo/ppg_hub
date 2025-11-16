# 📋 Levantamento de Requisitos - Módulo de Bancas de Defesa

**Data:** 2024-11-16
**Versão:** 1.0
**Projeto:** PPG Hub

---

## 🎯 Visão Geral

Implementar módulo completo para gestão de bancas de defesa de dissertações e teses, permitindo cadastro, agendamento, alteração e consulta de bancas, com integração aos dados já existentes no sistema (Docentes, Programas, Instituições).

---

## 📊 Análise dos Dados Fornecidos

### Campos do Formulário Original

| Campo | Tipo | Obrigatório | Observação |
|-------|------|-------------|------------|
| ORDEM | Integer | Sim | Número sequencial da banca |
| NOME_DISCENTE | String | Sim | Nome completo do aluno |
| MATRICULA | String | Sim | Matrícula única do discente |
| TITULO_DA_DISSERTACAO | Text | Sim | Título do trabalho |
| NOME_ORIENTADOR | String | Sim | Orientador principal |
| PROFESSOR_INTERNO_TITULAR | String | Sim | Membro interno da banca |
| PROFESSOR_EXTERNO_TITULAR | String | Sim | Membro externo da banca |
| LOCAL_DO_PROFESSOR_EXTERNO | String | Sim | Instituição do externo |
| NOME_SUPLENTE | String | Não | Membro suplente |
| TIPO_SUPLENTE | Enum | Condicional | INTERNO ou EXTERNO |
| LOCAL_DO_SUPLENTE | String | Condicional | Se externo |
| DATA_DE_DEFESA | Date | Sim | Data da defesa |
| DATA_DE_ENVIO | Date | Sim | Data de envio do formulário |
| HORARIO_EM_EXTENSO_QUALIFICACAO | String | Sim | Ex: "14h às 16h" |
| LOCAL | String | Sim | Sala/Link da defesa |
| FORMA_REUNIAO | Enum | Sim | PRESENCIAL/VIRTUAL/HIBRIDA |
| PUBLICA_OU_SECRETA | Enum | Sim | PUBLICA/SECRETA |

---

## 🔍 Análise de Domínio

### Entidades Existentes no Sistema

1. **InstituicaoEntity** ✅
   - Usada para vincular discentes e professores externos

2. **ProgramaEntity** ✅
   - Programa de pós-graduação do discente

3. **DocenteEntity** ✅
   - Orientadores e professores internos

### Novas Entidades Necessárias

1. **DiscenteEntity** 🆕
   - Alunos de pós-graduação

2. **BancaEntity** 🆕
   - Registro da banca de defesa

3. **MembroBancaEntity** 🆕
   - Composição da banca (titulares e suplentes)

4. **ProfessorExternoEntity** 🆕
   - Professores de outras instituições (não cadastrados como Docente)

---

## 📝 Requisitos Funcionais

### RF01 - Gestão de Discentes

**RF01.1** - Cadastrar discente
- Nome completo
- Matrícula (única no programa)
- CPF (único no sistema)
- Email institucional
- Programa de pós-graduação
- Nível (MESTRADO/DOUTORADO)
- Orientador principal
- Co-orientador (opcional)
- Data de ingresso
- Data prevista de defesa
- Status (ATIVO, DEFENDEU, DESISTENTE, JUBILADO)

**RF01.2** - Buscar discente
- Por matrícula
- Por CPF
- Por nome
- Por programa
- Por orientador

**RF01.3** - Atualizar dados do discente

**RF01.4** - Vincular orientador/co-orientador

---

### RF02 - Gestão de Professores Externos

**RF02.1** - Cadastrar professor externo
- Nome completo
- Email
- ORCID (opcional)
- Lattes ID (opcional)
- Instituição de origem
- Titulação
- Área de atuação

**RF02.2** - Buscar professor externo
- Por nome
- Por instituição
- Por ORCID
- Por email

**RF02.3** - Converter professor externo em Docente
- Caso o professor externo se torne docente da instituição

**RF02.4** - Vincular automaticamente se já existe
- Verificar por ORCID, Lattes ou email
- Sugerir cadastro existente

---

### RF03 - Gestão de Bancas

**RF03.1** - Criar nova banca
- Tipo (QUALIFICACAO/DEFESA_MESTRADO/DEFESA_DOUTORADO)
- Discente (obrigatório)
- Título do trabalho (obrigatório)
- Data e horário da defesa
- Local (presencial) ou Link (virtual)
- Forma de reunião (PRESENCIAL/VIRTUAL/HIBRIDA)
- Visibilidade (PUBLICA/SECRETA)
- Programa (herdado do discente)
- Status (AGENDADA/REALIZADA/CANCELADA/REAGENDADA)

**RF03.2** - Adicionar membros da banca
- Orientador (automático, mas pode não participar)
- Titulares internos (1 a N)
- Titulares externos (1 a N)
- Suplentes internos (0 a N)
- Suplentes externos (0 a N)
- Papel de cada membro (PRESIDENTE/TITULAR/SUPLENTE)

**RF03.3** - Buscar bancas
- Por discente
- Por orientador
- Por data
- Por programa
- Por status
- Por tipo

**RF03.4** - Atualizar banca
- Reagendar data/horário
- Alterar local
- Substituir membros
- Registrar mudança de status

**RF03.5** - Registrar resultado da defesa
- Data de realização
- Resultado (APROVADO/APROVADO_COM_CORRECOES/REPROVADO)
- Observações
- Presença dos membros
- Ata da defesa (arquivo PDF)

**RF03.6** - Cancelar banca
- Motivo do cancelamento
- Data do cancelamento
- Manter histórico

**RF03.7** - Listar próximas bancas
- Bancas agendadas nos próximos 30 dias
- Filtro por programa

**RF03.8** - Gerar documentos
- Ata de defesa (template)
- Convite para membros
- Declaração de participação
- Relatório de bancas realizadas

---

### RF04 - Validações e Regras de Negócio

**RF04.1** - Validar composição da banca
- ✅ Mínimo de 3 membros titulares
- ✅ Máximo de 5 membros titulares
- ✅ Pelo menos 1 membro externo titular
- ✅ Orientador pode ser membro (geralmente presidente)
- ✅ Não pode haver duplicação de membros
- ⚠️ Suplentes são opcionais mas recomendados

**RF04.2** - Validar agendamento
- ✅ Data da defesa deve ser futura (no cadastro)
- ✅ Não pode haver conflito de horário para o mesmo local
- ✅ Discente não pode ter outra banca agendada
- ⚠️ Alertar se membro tem outra banca no mesmo horário

**RF04.3** - Validar pré-requisitos
- ✅ Discente deve estar ativo
- ✅ Discente deve ter cumprido créditos mínimos (informação externa)
- ⚠️ Alertar se prazo máximo está próximo

**RF04.4** - Validar professor externo
- ✅ Deve ter instituição vinculada
- ✅ Não pode ser da mesma instituição do programa
- ✅ Deve ter titulação mínima (Doutor)

---

### RF05 - Notificações

**RF05.1** - Notificar membros da banca
- Email de convite
- Confirmação de presença
- Lembrete 7 dias antes
- Lembrete 1 dia antes

**RF05.2** - Notificar discente
- Confirmação de agendamento
- Alterações na banca
- Resultado da defesa

**RF05.3** - Notificar coordenação
- Novas bancas agendadas
- Bancas próximas sem confirmação
- Alterações de última hora

---

## 🔒 Requisitos Não Funcionais

### RNF01 - Segurança

**RNF01.1** - Autenticação e autorização
- Apenas coordenadores podem criar/editar bancas
- Discentes podem visualizar apenas suas bancas
- Professores podem visualizar bancas onde participam
- Bancas secretas só visíveis para envolvidos

**RNF01.2** - Proteção de dados
- Dados pessoais de acordo com LGPD
- Histórico de alterações (audit log)
- Documentos armazenados com criptografia

### RNF02 - Desempenho

**RNF02.1** - Tempos de resposta
- Listagem de bancas: < 500ms
- Criação de banca: < 1s
- Busca de discente/professor: < 300ms

**RNF02.2** - Concorrência
- Suportar 50 usuários simultâneos
- Lock otimista para evitar conflitos

### RNF03 - Usabilidade

**RNF03.1** - Interface
- Formulário wizard para criar banca (passo a passo)
- Autocompletar para busca de pessoas
- Sugestões de horários disponíveis
- Validação em tempo real

**RNF03.2** - Acessibilidade
- Compatível com leitores de tela
- Navegação por teclado
- Contraste adequado

### RNF04 - Integridade

**RNF04.1** - Consistência de dados
- Transações ACID
- Constraints no banco de dados
- Validação em múltiplas camadas

**RNF04.2** - Auditoria
- Log de todas as alterações
- Quem, quando, o que foi alterado
- Retenção de 5 anos

### RNF05 - Escalabilidade

**RNF05.1** - Volume de dados
- Suportar 10.000 discentes
- Suportar 50.000 bancas
- Suportar 1.000 professores externos

### RNF06 - Manutenibilidade

**RNF06.1** - Código
- Cobertura de testes > 80%
- Documentação de API (OpenAPI)
- Logs estruturados

---

## 🎭 Casos de Uso Principais

### UC01 - Cadastrar Banca (Fluxo Principal)

**Ator:** Coordenador do Programa

**Pré-condições:**
- Usuário autenticado como coordenador
- Discente cadastrado no sistema
- Orientador cadastrado

**Fluxo:**
1. Coordenador acessa "Cadastrar Nova Banca"
2. Sistema exibe formulário wizard (Etapa 1: Informações Básicas)
3. Coordenador seleciona:
   - Tipo de banca
   - Discente (autocomplete)
   - Sistema preenche automaticamente: programa, orientador
4. Coordenador preenche:
   - Título do trabalho
   - Data e horário
   - Local/Link
   - Forma de reunião
   - Visibilidade
5. Sistema valida disponibilidade de data/local
6. Sistema avança para Etapa 2: Composição da Banca
7. Coordenador adiciona membros titulares:
   - Busca professor interno (autocomplete de Docentes)
   - Adiciona como titular
   - Busca professor externo
8. **[Cenário A]** Professor externo já cadastrado:
   - Sistema encontra e sugere
   - Coordenador confirma
9. **[Cenário B]** Professor externo NÃO cadastrado:
   - Sistema não encontra
   - Coordenador clica "Cadastrar Novo Professor Externo"
   - Sistema exibe mini-formulário inline
   - Coordenador preenche: nome, email, instituição, titulação
   - Sistema cria ProfessorExterno e vincula à banca
10. Coordenador adiciona suplentes (opcional)
11. Sistema valida composição (mín 3, máx 5, pelo menos 1 externo)
12. Sistema avança para Etapa 3: Revisão
13. Coordenador revisa todos os dados
14. Coordenador confirma
15. Sistema cria banca com status AGENDADA
16. Sistema envia emails de notificação
17. Sistema exibe confirmação com número da banca

**Pós-condições:**
- Banca criada no banco de dados
- Emails enviados aos membros
- Banca aparece no calendário

---

### UC02 - Reagendar Banca

**Ator:** Coordenador

**Fluxo:**
1. Coordenador busca banca
2. Clica "Reagendar"
3. Sistema exibe modal com nova data/horário
4. Coordenador preenche novos dados
5. Sistema valida disponibilidade
6. Coordenador confirma
7. Sistema atualiza status para REAGENDADA
8. Sistema registra histórico
9. Sistema envia emails de notificação

---

### UC03 - Substituir Membro da Banca

**Ator:** Coordenador

**Fluxo:**
1. Coordenador acessa banca
2. Clica em "Substituir" no membro
3. Sistema exibe busca de novo membro
4. Coordenador seleciona substituto
5. Sistema valida (não duplicar, mesma categoria)
6. Sistema inativa membro antigo
7. Sistema adiciona novo membro
8. Sistema envia notificações

---

### UC04 - Registrar Resultado da Defesa

**Ator:** Coordenador ou Secretaria

**Fluxo:**
1. Acessa banca realizada
2. Clica "Registrar Resultado"
3. Preenche:
   - Resultado (APROVADO/APROVADO_COM_CORRECOES/REPROVADO)
   - Prazo para correções (se aplicável)
   - Observações
   - Presença dos membros
4. Faz upload da ata assinada (PDF)
5. Sistema valida PDF
6. Sistema atualiza status para REALIZADA
7. Sistema notifica discente
8. Sistema gera declarações para membros

---

## ⚠️ Cenários de Exceção e Tratamento

### Cenário 1: Professor Externo Não Cadastrado

**Problema:** Ao cadastrar banca, professor externo não existe no sistema.

**Solução 1 (Recomendada):**
- Formulário inline para cadastro rápido
- Campos mínimos: nome, email, instituição
- Sistema cria ProfessorExternoEntity
- Pode ser enriquecido depois via integração OpenAlex/ORCID

**Solução 2 (Alternativa):**
- Permitir cadastro com apenas nome + instituição (texto livre)
- Campo "raw_data" JSON para armazenar
- Flag "requires_validation" = true
- Processo de enriquecimento posterior

**Código:**
```java
@Service
public class ProfessorExternoService {

    public ProfessorExternoEntity findOrCreate(ProfessorExternoRequest request) {
        // Tentar encontrar por ORCID
        if (request.getOrcid() != null) {
            Optional<ProfessorExternoEntity> found =
                repository.findByOrcid(request.getOrcid());
            if (found.isPresent()) return found.get();
        }

        // Tentar encontrar por email
        if (request.getEmail() != null) {
            Optional<ProfessorExternoEntity> found =
                repository.findByEmail(request.getEmail());
            if (found.isPresent()) return found.get();
        }

        // Criar novo
        return create(request);
    }
}
```

---

### Cenário 2: Conflito de Horário

**Problema:** Local já está reservado no horário solicitado.

**Solução:**
- Validação antes de confirmar
- Sugerir horários alternativos
- Permitir override com justificativa (admin)

**Código:**
```java
public void validarDisponibilidadeLocal(LocalDateTime dataHora, String local) {
    boolean ocupado = bancaRepository.existsByDataHoraAndLocal(dataHora, local);
    if (ocupado) {
        throw new ConflictException("Local já reservado para este horário");
    }
}
```

---

### Cenário 3: Membro com Múltiplas Bancas no Mesmo Dia

**Problema:** Professor tem 3 bancas no mesmo dia.

**Solução:**
- Emitir WARNING (não ERROR)
- Permitir continuar
- Destacar na interface
- Notificar professor

---

### Cenário 4: Orientador Não é Membro da Banca

**Problema:** Em alguns programas, orientador não participa da banca.

**Solução:**
- Orientador NÃO é obrigatório na banca
- Campo "orientador_participa" = true/false
- Se true, adicionar automaticamente como presidente
- Se false, deixar de fora

---

### Cenário 5: Discente Sem Orientador Cadastrado

**Problema:** Discente está sem orientador no sistema.

**Solução:**
- Permitir selecionar orientador na criação da banca
- Atualizar registro do discente automaticamente
- Validar se orientador é do programa

---

### Cenário 6: Banca Cancelada de Última Hora

**Problema:** Banca precisa ser cancelada faltando 1 dia.

**Solução:**
- Status CANCELADA
- Motivo obrigatório
- Notificação urgente para todos
- Manter histórico completo
- Permitir reagendamento rápido (copiar dados)

---

### Cenário 7: Documento da Ata Corrompido

**Problema:** PDF enviado está corrompido ou inválido.

**Solução:**
- Validação de tipo de arquivo (PDF only)
- Validação de tamanho (max 10MB)
- Tentativa de leitura do PDF
- Armazenar hash para integridade
- Permitir reenvio

---

### Cenário 8: Professor Externo de Instituição Não Cadastrada

**Problema:** Instituição do professor externo não existe no banco.

**Solução 1:**
- Campo texto livre "nome_instituicao_externa"
- Flag "instituicao_validada" = false
- Processo de cadastro posterior da instituição

**Solução 2 (Melhor):**
- Permitir cadastro rápido de instituição no formulário
- Mini-wizard inline
- Validar depois via ROR/OpenAlex

---

### Cenário 9: Dados Duplicados (Mesma Banca Cadastrada 2x)

**Problema:** Erro humano ao cadastrar.

**Solução:**
- Unique constraint: (discente_id + tipo + data_defesa)
- Validação: "Já existe banca de [tipo] para este discente em [data]"
- Sugerir edição ao invés de novo cadastro

---

### Cenário 10: Alteração de Composição Após Envio de Convites

**Problema:** Membro desiste, precisa substituir.

**Solução:**
- Status do membro: CONVIDADO/CONFIRMADO/SUBSTITUIDO/CANCELADO
- Histórico de mudanças
- Notificar todos sobre a alteração
- Manter registro do membro anterior

---

## 🗄️ Modelo de Dados Proposto

### DiscenteEntity

```java
@Entity
@Table(name = "discentes")
public class DiscenteEntity extends BaseEntity {

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String matricula;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaEntity programa;

    @Enumerated(EnumType.STRING)
    private NivelDiscente nivel; // MESTRADO, DOUTORADO

    @ManyToOne
    @JoinColumn(name = "orientador_id")
    private DocenteEntity orientador;

    @ManyToOne
    @JoinColumn(name = "coorientador_id")
    private DocenteEntity coorientador;

    private LocalDate dataIngresso;
    private LocalDate dataPrevisaoDefesa;

    @Enumerated(EnumType.STRING)
    private StatusDiscente status; // ATIVO, DEFENDEU, DESISTENTE, JUBILADO

    private String telefone;
    private String lattesId;
}
```

### ProfessorExternoEntity

```java
@Entity
@Table(name = "professores_externos")
public class ProfessorExternoEntity extends BaseEntity {

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String orcid;

    private String lattesId;

    @ManyToOne
    @JoinColumn(name = "instituicao_id")
    private InstituicaoEntity instituicao;

    // Se instituição não cadastrada
    private String nomeInstituicaoExterna;
    private Boolean instituicaoValidada = false;

    @Enumerated(EnumType.STRING)
    private Titulacao titulacao;

    private String areaAtuacao;

    // Para facilitar migração para Docente
    private Boolean convertidoParaDocente = false;

    @OneToOne
    private DocenteEntity docenteVinculado;
}
```

### BancaEntity

```java
@Entity
@Table(name = "bancas")
public class BancaEntity extends BaseEntity {

    @Column(unique = true)
    private String numeroBanca; // Auto-gerado: "BANCA-2024-001"

    @Enumerated(EnumType.STRING)
    private TipoBanca tipo; // QUALIFICACAO, DEFESA_MESTRADO, DEFESA_DOUTORADO

    @ManyToOne
    @JoinColumn(name = "discente_id", nullable = false)
    private DiscenteEntity discente;

    @ManyToOne
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaEntity programa;

    @Column(nullable = false, columnDefinition = "text")
    private String tituloTrabalho;

    @Column(nullable = false)
    private LocalDateTime dataHoraDefesa;

    private String local;
    private String linkVirtual;

    @Enumerated(EnumType.STRING)
    private FormaReuniao formaReuniao; // PRESENCIAL, VIRTUAL, HIBRIDA

    @Enumerated(EnumType.STRING)
    private VisibilidadeBanca visibilidade; // PUBLICA, SECRETA

    @Enumerated(EnumType.STRING)
    private StatusBanca status; // AGENDADA, REALIZADA, CANCELADA, REAGENDADA

    @OneToMany(mappedBy = "banca", cascade = CascadeType.ALL)
    private List<MembroBancaEntity> membros = new ArrayList<>();

    // Resultado
    @Enumerated(EnumType.STRING)
    private ResultadoBanca resultado; // APROVADO, APROVADO_COM_CORRECOES, REPROVADO

    private LocalDate prazoCorrecoes;

    @Column(columnDefinition = "text")
    private String observacoes;

    // Ata
    private String ataPdfUrl;
    private String ataPdfHash;

    // Auditoria
    private LocalDateTime dataCadastro;
    private LocalDateTime dataReagendamento;
    private LocalDateTime dataCancelamento;

    @Column(columnDefinition = "text")
    private String motivoCancelamento;

    @Column(columnDefinition = "jsonb")
    private String historicoAlteracoes;
}
```

### MembroBancaEntity

```java
@Entity
@Table(name = "membros_banca")
public class MembroBancaEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "banca_id", nullable = false)
    private BancaEntity banca;

    // Pode ser Docente (interno) OU Professor Externo
    @ManyToOne
    @JoinColumn(name = "docente_id")
    private DocenteEntity docente;

    @ManyToOne
    @JoinColumn(name = "professor_externo_id")
    private ProfessorExternoEntity professorExterno;

    @Enumerated(EnumType.STRING)
    private TipoMembro tipoMembro; // TITULAR, SUPLENTE

    @Enumerated(EnumType.STRING)
    private PapelMembro papelMembro; // PRESIDENTE, MEMBRO, SUPLENTE

    @Enumerated(EnumType.STRING)
    private CategoriaMembro categoria; // INTERNO, EXTERNO

    private Integer ordem; // Ordem na composição

    @Enumerated(EnumType.STRING)
    private StatusMembro status; // CONVIDADO, CONFIRMADO, SUBSTITUIDO, AUSENTE, PRESENTE

    private LocalDateTime dataConvite;
    private LocalDateTime dataConfirmacao;
    private Boolean presente;

    @Column(columnDefinition = "text")
    private String observacoes;

    // Validação: ou docente ou professorExterno, nunca ambos
    @PrePersist
    @PreUpdate
    private void validate() {
        if (docente != null && professorExterno != null) {
            throw new IllegalStateException("Membro não pode ser docente E professor externo simultaneamente");
        }
        if (docente == null && professorExterno == null) {
            throw new IllegalStateException("Membro deve ser docente OU professor externo");
        }
    }
}
```

---

## 🔄 Fluxos de Trabalho

### Fluxo 1: Criação de Banca com Professor Externo Novo

```
[Coordenador] → Cadastrar Banca
    ↓
[Sistema] → Exibe formulário wizard
    ↓
[Etapa 1: Informações Básicas]
    ↓
[Coordenador] → Seleciona discente (autocomplete)
    ↓
[Sistema] → Preenche programa e orientador automaticamente
    ↓
[Coordenador] → Preenche título, data, local, forma
    ↓
[Sistema] → Valida disponibilidade de data/local
    ↓
[Etapa 2: Composição da Banca]
    ↓
[Coordenador] → Busca "Prof. Dr. João Silva" (interno)
    ↓
[Sistema] → Encontra em DocenteEntity → Adiciona
    ↓
[Coordenador] → Busca "Profa. Dra. Maria Santos" (externa)
    ↓
[Sistema] → NÃO encontra
    ↓
[Sistema] → Exibe: "Professor não encontrado. Deseja cadastrar?"
    ↓
[Coordenador] → Clica "Cadastrar Novo"
    ↓
[Sistema] → Mini-formulário inline:
              - Nome: [Profa. Dra. Maria Santos]
              - Email: [maria@externa.edu.br]
              - Instituição: [Buscar ou criar]
              - Titulação: [DOUTORADO]
    ↓
[Coordenador] → Busca instituição "Universidade Federal do Rio"
    ↓
[Sistema] → Encontra → Vincula
    ↓
[Sistema] → Cria ProfessorExternoEntity
    ↓
[Sistema] → Adiciona à banca como TITULAR EXTERNO
    ↓
[Continua até completar composição...]
    ↓
[Etapa 3: Revisão]
    ↓
[Coordenador] → Confirma
    ↓
[Sistema] → Cria BancaEntity (status: AGENDADA)
[Sistema] → Cria MembroBancaEntity para cada membro
[Sistema] → Envia emails de notificação
[Sistema] → Exibe confirmação
```

---

## 📈 Priorização (MVP)

### Fase 1 - MVP (2-3 semanas)
- [ ] Entity: DiscenteEntity
- [ ] Entity: ProfessorExternoEntity
- [ ] Entity: BancaEntity
- [ ] Entity: MembroBancaEntity
- [ ] Repository: Todos
- [ ] Service: DiscenteService (CRUD básico)
- [ ] Service: ProfessorExternoService (CRUD + findOrCreate)
- [ ] Service: BancaService (criar, listar, buscar)
- [ ] Controller: DiscenteController
- [ ] Controller: ProfessorExternoController
- [ ] Controller: BancaController
- [ ] Migrations: Flyway
- [ ] Testes unitários

### Fase 2 - Validações e Regras (1-2 semanas)
- [ ] Validação de composição da banca
- [ ] Validação de conflitos de horário
- [ ] Validação de pré-requisitos
- [ ] Exceptions customizadas
- [ ] Testes de integração

### Fase 3 - Funcionalidades Avançadas (2 semanas)
- [ ] Reagendamento
- [ ] Substituição de membros
- [ ] Registro de resultado
- [ ] Upload de ata
- [ ] Histórico de alterações
- [ ] Auditoria

### Fase 4 - Notificações (1 semana)
- [ ] Template de emails
- [ ] Envio de convites
- [ ] Lembretes automáticos
- [ ] Confirmações

### Fase 5 - Relatórios (1 semana)
- [ ] Listagem de bancas por período
- [ ] Relatório por programa
- [ ] Exportação para Excel/PDF
- [ ] Dashboard de bancas

---

## ✅ Critérios de Aceitação

### CA01 - Cadastro de Banca
- ✅ Deve permitir criar banca com discente, título e data
- ✅ Deve validar composição mínima (3 membros)
- ✅ Deve permitir adicionar professor externo não cadastrado
- ✅ Deve enviar email para todos os membros
- ✅ Deve gerar número único da banca

### CA02 - Busca de Professores
- ✅ Autocomplete deve retornar resultados em < 300ms
- ✅ Deve buscar em Docentes E Professores Externos
- ✅ Deve sugerir cadastrados com dados similares (fuzzy match)

### CA03 - Validações
- ✅ Não deve permitir data passada
- ✅ Não deve permitir duplicação de membros
- ✅ Deve alertar sobre conflitos de horário
- ✅ Deve validar email ao cadastrar professor externo

---

## 🎨 Wireframes (Conceitual)

### Tela 1: Listagem de Bancas
```
╔══════════════════════════════════════════════════════════╗
║  PPG Hub - Bancas de Defesa                              ║
╠══════════════════════════════════════════════════════════╣
║  [+ Nova Banca]  [Filtros ▼]  [Exportar]                ║
╠══════════════════════════════════════════════════════════╣
║  Próximas Bancas                                         ║
║  ┌────────────────────────────────────────────────────┐  ║
║  │ 📅 15/12/2024 14h - Mestrado                       │  ║
║  │ João da Silva                                      │  ║
║  │ "Aplicações de IA em Saúde"                       │  ║
║  │ 👥 3 titulares + 1 suplente                       │  ║
║  │ [Ver] [Editar] [Cancelar]                         │  ║
║  └────────────────────────────────────────────────────┘  ║
║  ┌────────────────────────────────────────────────────┐  ║
║  │ 📅 20/12/2024 10h - Doutorado                      │  ║
║  │ ...                                                │  ║
║  └────────────────────────────────────────────────────┘  ║
╚══════════════════════════════════════════════════════════╝
```

### Tela 2: Wizard - Etapa 2 (Composição)
```
╔══════════════════════════════════════════════════════════╗
║  Nova Banca - Etapa 2: Composição                        ║
╠══════════════════════════════════════════════════════════╣
║  Membros Titulares (3-5):                                ║
║  ┌────────────────────────────────────────────────────┐  ║
║  │ 1. 👤 Prof. Dr. Pedro Santos (INTERNO) ✓          │  ║
║  │    Presidente                                      │  ║
║  └────────────────────────────────────────────────────┘  ║
║  ┌────────────────────────────────────────────────────┐  ║
║  │ 2. [Buscar professor...              ] 🔍         │  ║
║  │    Categoria: ○ Interno  ● Externo                │  ║
║  └────────────────────────────────────────────────────┘  ║
║                                                          ║
║  Suplentes (opcional):                                   ║
║  [+ Adicionar suplente]                                  ║
║                                                          ║
║  [← Voltar]              [Próximo: Revisão →]           ║
╚══════════════════════════════════════════════════════════╝
```

---

## 📊 Métricas de Sucesso

- **Tempo médio de cadastro:** < 5 minutos
- **Taxa de erro no cadastro:** < 5%
- **Aprovação dos coordenadores:** > 85%
- **Redução de emails manuais:** > 70%
- **Cobertura de testes:** > 80%

---

## 🚨 Riscos e Mitigações

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Resistência dos coordenadores | Média | Alto | Treinamento e suporte |
| Dados incompletos de professores externos | Alta | Médio | Permitir cadastro mínimo |
| Conflitos de horário não detectados | Baixa | Médio | Validação rigorosa |
| Erros na composição da banca | Média | Alto | Validações múltiplas |
| Performance com muitas bancas | Baixa | Médio | Indexação e cache |

---

## 📚 Referências

- Regimento de Pós-Graduação (cada programa)
- CAPES - Normas de Avaliação
- LGPD - Lei Geral de Proteção de Dados
- OpenAlex API Documentation

---

**Aprovação:**

- [ ] Coordenação PPG
- [ ] Desenvolvimento
- [ ] Infraestrutura
- [ ] Segurança da Informação

**Data de Aprovação:** ___/___/______

