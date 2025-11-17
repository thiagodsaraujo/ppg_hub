# 🎓 Design: Refatoração das Regras de Composição de Bancas

**Data:** 2025-11-17
**Status:** 📋 Proposta para Aprovação
**Autor:** Claude Code

---

## 📊 1. Análise do Problema Atual

### 1.1 Situação Identificada

A estrutura atual **já distingue tipos de banca** via enum `TipoBanca`:
- `QUALIFICACAO_MESTRADO` / `QUALIFICACAO_DOUTORADO`
- `DEFESA_MESTRADO` / `DEFESA_DOUTORADO` / `DEFESA_DOUTORADO_DIRETO`

**Porém**, as validações são **genéricas** e não respeitam as diferenças entre tipos:

```java
// BancaService.java - Validação atual (INCORRETA)
private static final int MIN_MEMBROS_TITULARES = 3;
private static final int MAX_MEMBROS_TITULARES = 5;
private static final int MIN_MEMBROS_EXTERNOS = 1; // ❌ Aplica para TODOS os tipos
```

### 1.2 Problemas Identificados

❌ **Problema 1**: Qualificação aceita bancas com apenas internos, mas validação exige externos
❌ **Problema 2**: Defesa deveria exigir pelo menos 1 externo, mas regra não é específica
❌ **Problema 3**: Não há diferenciação clara entre regras de Mestrado vs Doutorado
❌ **Problema 4**: Validação está misturada com lógica de serviço (baixa coesão)

---

## 🎯 2. Regras de Negócio Completas

### 2.1 Bancas de Qualificação (Mestrado e Doutorado)

| Regra | Descrição | Validação |
|-------|-----------|-----------|
| **Composição Mínima** | 3 membros titulares | `titulares.size() >= 3` |
| **Composição Máxima** | 5 membros titulares | `titulares.size() <= 5` |
| **Membros Externos** | **SEM restrição obrigatória** | ✅ Pode ter 0, 1 ou 2 externos |
| **Exemplos Válidos** | - 3 internos<br>- 2 internos + 1 externo<br>- 1 interno + 2 externos<br>- 2 externos + 1 interno | Flexível |
| **Presidente** | Obrigatório 1 presidente | `membros.where(funcao=PRESIDENTE).count() == 1` |
| **Orientador** | Pode participar mas não conta como titular | `orientadorParticipa` flag |

### 2.2 Bancas de Defesa (Mestrado e Doutorado)

| Regra | Descrição | Validação |
|-------|-----------|-----------|
| **Composição Mínima** | 3 membros titulares | `titulares.size() >= 3` |
| **Composição Máxima** | 5 membros titulares | `titulares.size() <= 5` |
| **Membros Externos** | **OBRIGATÓRIO pelo menos 1** | `externos.size() >= 1` ✅ |
| **Máximo Externos** | Até 2 membros externos | `externos.size() <= 2` |
| **Exemplos Válidos** | - 2 internos + 1 externo ✅<br>- 1 interno + 2 externos ✅<br>- 3 externos ❌ (max 2)<br>- 3 internos ❌ (min 1 externo) | Rigoroso |
| **Presidente** | Obrigatório 1 presidente | `membros.where(funcao=PRESIDENTE).count() == 1` |
| **Orientador** | Pode participar mas não conta como titular | `orientadorParticipa` flag |

### 2.3 Regras Comuns (Todas as Bancas)

| Regra | Descrição |
|-------|-----------|
| **Suplentes** | Opcional, mas recomendado ter suplentes |
| **Status Convites** | Todos titulares devem estar CONFIRMADOS antes da realização |
| **Conflito de Horário** | Não pode haver 2 bancas no mesmo horário para o mesmo discente |
| **Presidente Único** | Exatamente 1 presidente por banca |

---

## 🏗️ 3. Arquitetura Proposta: Strategy Pattern + DDD

### 3.1 Visão Geral

```
┌─────────────────────────────────────────────────────────────┐
│                      BancaService                           │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ create(request) {                                     │  │
│  │   validator = factory.getValidator(tipoBanca)         │  │
│  │   validator.validarComposicao(membros)  ←─────────┐   │  │
│  │ }                                                   │   │  │
│  └───────────────────────────────────────────────────┬─┘   │  │
└──────────────────────────────────────────────────────┼─────┘  │
                                                       │        │
                        ┌──────────────────────────────┘        │
                        │                                       │
                        ▼                                       │
        ┌───────────────────────────────┐                      │
        │ BancaValidatorFactory         │                      │
        │ ───────────────────────────── │                      │
        │ + getValidator(TipoBanca)     │                      │
        │   returns: Validator          │                      │
        └───────────────┬───────────────┘                      │
                        │                                       │
        ┌───────────────┴───────────────┐                      │
        │                               │                      │
        ▼                               ▼                      │
┌───────────────────┐         ┌───────────────────┐            │
│ DefesaValidator   │         │ QualificValidator │            │
│ ───────────────── │         │ ───────────────── │            │
│ validarExternos() │         │ validarExternos() │            │
│   >= 1 && <= 2    │         │   (sem restrição) │            │
└───────────────────┘         └───────────────────┘            │
        │                               │                      │
        └───────────────┬───────────────┘                      │
                        │ implements                           │
                        ▼                                       │
        ┌───────────────────────────────┐                      │
        │ BancaComposicaoValidator      │                      │
        │ ───────────────────────────── │                      │
        │ + validarComposicao(membros)  │◄─────────────────────┘
        │ + validarTitulares()          │
        │ + validarPresidente()         │
        │ + validarExternos()           │
        └───────────────────────────────┘
```

### 3.2 Benefícios da Arquitetura

✅ **Separação de Responsabilidades**: Validação isolada do serviço
✅ **Open/Closed Principle**: Fácil adicionar novos tipos sem modificar código existente
✅ **Testabilidade**: Cada validador testável independentemente
✅ **Type Safety**: Compile-time checking com Strategy Pattern
✅ **Domain-Driven Design**: Regras de negócio explícitas no domínio
✅ **Manutenibilidade**: Regras centralizadas e documentadas

---

## 💻 4. Estrutura de Classes Proposta

### 4.1 Árvore de Arquivos

```
src/main/java/com/ppghub/domain/
├── model/
│   └── ComposicaoBanca.java                    # Value Object com métricas
├── service/
│   ├── BancaService.java                       # Service principal (modificado)
│   └── banca/
│       ├── validator/
│       │   ├── BancaComposicaoValidator.java   # Interface (Strategy)
│       │   ├── DefesaComposicaoValidator.java  # Implementação Defesa
│       │   ├── QualificacaoComposicaoValidator.java  # Implementação Qualificação
│       │   └── BancaValidatorFactory.java      # Factory Pattern
│       └── BancaComposicaoService.java         # Domain Service (análise)

src/test/java/com/ppghub/domain/service/banca/validator/
├── DefesaComposicaoValidatorTest.java
├── QualificacaoComposicaoValidatorTest.java
└── BancaValidatorFactoryTest.java
```

### 4.2 Código das Classes

#### 4.2.1 Value Object: ComposicaoBanca

```java
package com.ppghub.domain.model;

import com.ppghub.infrastructure.persistence.entity.MembroBancaEntity;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Value Object que representa a composição de uma banca.
 * Encapsula métricas e análise da composição de membros.
 */
@Value
@Builder
public class ComposicaoBanca {
    List<MembroBancaEntity> membros;

    public List<MembroBancaEntity> getTitulares() {
        return membros.stream()
                .filter(MembroBancaEntity::isTitular)
                .collect(Collectors.toList());
    }

    public List<MembroBancaEntity> getSuplentes() {
        return membros.stream()
                .filter(MembroBancaEntity::isSuplente)
                .collect(Collectors.toList());
    }

    public List<MembroBancaEntity> getExternos() {
        return membros.stream()
                .filter(MembroBancaEntity::isExterno)
                .collect(Collectors.toList());
    }

    public List<MembroBancaEntity> getInternos() {
        return membros.stream()
                .filter(MembroBancaEntity::isInterno)
                .collect(Collectors.toList());
    }

    public List<MembroBancaEntity> getPresidentes() {
        return membros.stream()
                .filter(m -> m.getFuncao() == MembroBancaEntity.Funcao.PRESIDENTE)
                .collect(Collectors.toList());
    }

    public int getNumeroTitulares() {
        return getTitulares().size();
    }

    public int getNumeroExternos() {
        return getExternos().size();
    }

    public int getNumeroInternos() {
        return getInternos().size();
    }

    public boolean temPresidente() {
        return !getPresidentes().isEmpty();
    }
}
```

#### 4.2.2 Interface Strategy: BancaComposicaoValidator

```java
package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;

/**
 * Strategy para validação de composição de bancas.
 * Cada tipo de banca (Defesa, Qualificação) tem regras específicas.
 */
public interface BancaComposicaoValidator {

    /**
     * Valida a composição completa da banca.
     * Lança BusinessRuleException se houver violação.
     *
     * @param composicao Composição da banca a validar
     * @throws com.ppghub.domain.exception.BusinessRuleException se inválida
     */
    void validarComposicao(ComposicaoBanca composicao);

    /**
     * Retorna o tipo de banca que este validador suporta.
     */
    BancaEntity.TipoBanca[] getTiposSuportados();
}
```

#### 4.2.3 Implementação: DefesaComposicaoValidator

```java
package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validador de composição para Bancas de Defesa (Mestrado/Doutorado).
 *
 * Regras específicas:
 * - Mínimo 3 titulares, máximo 5
 * - OBRIGATÓRIO pelo menos 1 membro externo
 * - Máximo 2 membros externos
 * - Exatamente 1 presidente
 */
@Component
@Slf4j
public class DefesaComposicaoValidator implements BancaComposicaoValidator {

    private static final int MIN_TITULARES = 3;
    private static final int MAX_TITULARES = 5;
    private static final int MIN_EXTERNOS = 1;  // ✅ Obrigatório para defesa
    private static final int MAX_EXTERNOS = 2;

    @Override
    public void validarComposicao(ComposicaoBanca composicao) {
        log.debug("Validando composição de banca de DEFESA");

        validarNumeroTitulares(composicao);
        validarPresidente(composicao);
        validarMembrosExternos(composicao);  // ✅ Regra específica de defesa
    }

    private void validarNumeroTitulares(ComposicaoBanca composicao) {
        int numTitulares = composicao.getNumeroTitulares();

        if (numTitulares < MIN_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter no mínimo %d membros titulares. Atual: %d",
                    MIN_TITULARES, numTitulares)
            );
        }

        if (numTitulares > MAX_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter no máximo %d membros titulares. Atual: %d",
                    MAX_TITULARES, numTitulares)
            );
        }
    }

    private void validarPresidente(ComposicaoBanca composicao) {
        int numPresidentes = composicao.getPresidentes().size();

        if (numPresidentes == 0) {
            throw new BusinessRuleException("Banca de defesa deve ter exatamente 1 presidente");
        }

        if (numPresidentes > 1) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter apenas 1 presidente. Atual: %d", numPresidentes)
            );
        }
    }

    private void validarMembrosExternos(ComposicaoBanca composicao) {
        int numExternos = composicao.getNumeroExternos();

        // ✅ REGRA ESPECÍFICA DE DEFESA: Obrigatório pelo menos 1 externo
        if (numExternos < MIN_EXTERNOS) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter no mínimo %d membro externo. Atual: %d",
                    MIN_EXTERNOS, numExternos)
            );
        }

        if (numExternos > MAX_EXTERNOS) {
            throw new BusinessRuleException(
                String.format("Banca de defesa deve ter no máximo %d membros externos. Atual: %d",
                    MAX_EXTERNOS, numExternos)
            );
        }

        log.debug("Composição de defesa válida: {} externos de {} titulares",
            numExternos, composicao.getNumeroTitulares());
    }

    @Override
    public BancaEntity.TipoBanca[] getTiposSuportados() {
        return new BancaEntity.TipoBanca[] {
            BancaEntity.TipoBanca.DEFESA_MESTRADO,
            BancaEntity.TipoBanca.DEFESA_DOUTORADO,
            BancaEntity.TipoBanca.DEFESA_DOUTORADO_DIRETO
        };
    }
}
```

#### 4.2.4 Implementação: QualificacaoComposicaoValidator

```java
package com.ppghub.domain.service.banca.validator;

import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validador de composição para Bancas de Qualificação (Mestrado/Doutorado).
 *
 * Regras específicas:
 * - Mínimo 3 titulares, máximo 5
 * - SEM restrição obrigatória de membros externos (pode ter 0, 1 ou 2)
 * - Exatamente 1 presidente
 */
@Component
@Slf4j
public class QualificacaoComposicaoValidator implements BancaComposicaoValidator {

    private static final int MIN_TITULARES = 3;
    private static final int MAX_TITULARES = 5;
    // ✅ Qualificação: SEM restrição mínima de externos

    @Override
    public void validarComposicao(ComposicaoBanca composicao) {
        log.debug("Validando composição de banca de QUALIFICAÇÃO");

        validarNumeroTitulares(composicao);
        validarPresidente(composicao);
        // ✅ Não valida externos - qualificação é flexível
    }

    private void validarNumeroTitulares(ComposicaoBanca composicao) {
        int numTitulares = composicao.getNumeroTitulares();

        if (numTitulares < MIN_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de qualificação deve ter no mínimo %d membros titulares. Atual: %d",
                    MIN_TITULARES, numTitulares)
            );
        }

        if (numTitulares > MAX_TITULARES) {
            throw new BusinessRuleException(
                String.format("Banca de qualificação deve ter no máximo %d membros titulares. Atual: %d",
                    MAX_TITULARES, numTitulares)
            );
        }
    }

    private void validarPresidente(ComposicaoBanca composicao) {
        int numPresidentes = composicao.getPresidentes().size();

        if (numPresidentes == 0) {
            throw new BusinessRuleException("Banca de qualificação deve ter exatamente 1 presidente");
        }

        if (numPresidentes > 1) {
            throw new BusinessRuleException(
                String.format("Banca de qualificação deve ter apenas 1 presidente. Atual: %d", numPresidentes)
            );
        }
    }

    @Override
    public BancaEntity.TipoBanca[] getTiposSuportados() {
        return new BancaEntity.TipoBanca[] {
            BancaEntity.TipoBanca.QUALIFICACAO_MESTRADO,
            BancaEntity.TipoBanca.QUALIFICACAO_DOUTORADO
        };
    }
}
```

#### 4.2.5 Factory Pattern: BancaValidatorFactory

```java
package com.ppghub.domain.service.banca.validator;

import com.ppghub.infrastructure.persistence.entity.BancaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Factory para obter o validador correto baseado no tipo de banca.
 * Implementa Strategy Pattern para seleção dinâmica de validador.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BancaValidatorFactory {

    private final List<BancaComposicaoValidator> validators;

    /**
     * Retorna o validador apropriado para o tipo de banca.
     *
     * @param tipoBanca Tipo da banca (DEFESA, QUALIFICACAO, etc.)
     * @return Validador correspondente
     * @throws IllegalArgumentException se tipo não suportado
     */
    public BancaComposicaoValidator getValidator(BancaEntity.TipoBanca tipoBanca) {
        log.debug("Buscando validador para tipo de banca: {}", tipoBanca);

        return validators.stream()
                .filter(validator -> Arrays.asList(validator.getTiposSuportados()).contains(tipoBanca))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Nenhum validador encontrado para tipo de banca: " + tipoBanca
                ));
    }
}
```

#### 4.2.6 Modificação no BancaService

```java
// BancaService.java - Método create() modificado

@Transactional
@CacheEvict(value = CacheConfig.BANCAS_CACHE, allEntries = true)
public BancaResponse create(BancaCreateRequest request) {
    log.info("Criando nova banca para discente ID: {}", request.getDiscenteId());

    // Validações de existência...
    DiscenteEntity discente = discenteRepository.findById(request.getDiscenteId())
            .orElseThrow(() -> new EntityNotFoundException("Discente", request.getDiscenteId()));

    ProgramaEntity programa = programaRepository.findById(request.getProgramaId())
            .orElseThrow(() -> new EntityNotFoundException("Programa", request.getProgramaId()));

    // Criar entidade
    BancaEntity entity = mapper.toEntity(request);
    entity.setDiscente(discente);
    entity.setPrograma(programa);

    // ✅ NOVA VALIDAÇÃO ESTRATÉGICA
    if (!entity.getMembros().isEmpty()) {
        BancaComposicaoValidator validator = validatorFactory.getValidator(entity.getTipoBanca());
        ComposicaoBanca composicao = ComposicaoBanca.builder()
                .membros(entity.getMembros())
                .build();
        validator.validarComposicao(composicao);
    }

    BancaEntity saved = repository.save(entity);
    log.info("Banca criada com sucesso: ID {}", saved.getId());

    return mapper.toResponse(saved);
}
```

---

## 🧪 5. Estratégia de Testes

### 5.1 Testes Unitários por Validador

#### DefesaComposicaoValidatorTest
```java
@DisplayName("Validador de Composição - Bancas de Defesa")
class DefesaComposicaoValidatorTest {

    @Test
    @DisplayName("Deve aceitar banca com 3 titulares e 1 externo")
    void deveAceitarComposicaoValida_3Titulares1Externo() {
        // Given: 2 internos + 1 externo + 1 presidente
        // When: validarComposicao()
        // Then: Sem exceção
    }

    @Test
    @DisplayName("Deve aceitar banca com 4 titulares e 2 externos")
    void deveAceitarComposicaoValida_4Titulares2Externos() {
        // Given: 2 internos + 2 externos + 1 presidente
        // When: validarComposicao()
        // Then: Sem exceção
    }

    @Test
    @DisplayName("Deve rejeitar banca sem membros externos")
    void deveRejeitarBancaSemExternos() {
        // Given: 3 internos (todos)
        // When: validarComposicao()
        // Then: BusinessRuleException("no mínimo 1 membro externo")
    }

    @Test
    @DisplayName("Deve rejeitar banca com 3 membros externos")
    void deveRejeitarBancaCom3Externos() {
        // Given: 0 internos + 3 externos
        // When: validarComposicao()
        // Then: BusinessRuleException("no máximo 2 membros externos")
    }

    @Test
    @DisplayName("Deve rejeitar banca com menos de 3 titulares")
    void deveRejeitarBancaComMenosDe3Titulares() {
        // Given: 2 titulares
        // When: validarComposicao()
        // Then: BusinessRuleException("no mínimo 3 membros titulares")
    }

    @Test
    @DisplayName("Deve rejeitar banca sem presidente")
    void deveRejeitarBancaSemPresidente() {
        // Given: 3 membros mas nenhum presidente
        // When: validarComposicao()
        // Then: BusinessRuleException("exatamente 1 presidente")
    }
}
```

#### QualificacaoComposicaoValidatorTest
```java
@DisplayName("Validador de Composição - Bancas de Qualificação")
class QualificacaoComposicaoValidatorTest {

    @Test
    @DisplayName("Deve aceitar banca com 3 internos e 0 externos")
    void deveAceitarBancaSomenteInternos() {
        // ✅ Diferença crucial: qualificação aceita 0 externos
        // Given: 3 internos + 0 externos
        // When: validarComposicao()
        // Then: Sem exceção
    }

    @Test
    @DisplayName("Deve aceitar banca com 2 internos e 1 externo")
    void deveAceitarBancaMista() {
        // Given: 2 internos + 1 externo
        // When: validarComposicao()
        // Then: Sem exceção
    }

    @Test
    @DisplayName("Deve aceitar banca com 1 interno e 2 externos")
    void deveAceitarBancaMaisExternos() {
        // Given: 1 interno + 2 externos
        // When: validarComposicao()
        // Then: Sem exceção
    }

    @Test
    @DisplayName("Deve rejeitar banca sem presidente")
    void deveRejeitarBancaSemPresidente() {
        // Given: 3 membros mas nenhum presidente
        // When: validarComposicao()
        // Then: BusinessRuleException("exatamente 1 presidente")
    }
}
```

### 5.2 Matriz de Testes

| Cenário | Defesa | Qualificação |
|---------|--------|--------------|
| 3 internos, 0 externos | ❌ Rejeita | ✅ Aceita |
| 2 internos, 1 externo | ✅ Aceita | ✅ Aceita |
| 1 interno, 2 externos | ✅ Aceita | ✅ Aceita |
| 0 internos, 3 externos | ❌ Rejeita (max 2) | ❌ Rejeita (max 5 total) |
| 2 titulares | ❌ Rejeita (min 3) | ❌ Rejeita (min 3) |
| 6 titulares | ❌ Rejeita (max 5) | ❌ Rejeita (max 5) |
| Sem presidente | ❌ Rejeita | ❌ Rejeita |
| 2 presidentes | ❌ Rejeita | ❌ Rejeita |

---

## 📅 6. Plano de Implementação

### Fase 1: Estrutura Base (1-2 horas)
- [ ] Criar `ComposicaoBanca` value object
- [ ] Criar interface `BancaComposicaoValidator`
- [ ] Criar `BancaValidatorFactory`
- [ ] Adicionar injeção da factory no `BancaService`

### Fase 2: Implementação Validadores (2-3 horas)
- [ ] Implementar `DefesaComposicaoValidator`
- [ ] Implementar `QualificacaoComposicaoValidator`
- [ ] Integrar validação no método `create()` do `BancaService`
- [ ] Integrar validação no método `update()` do `BancaService`

### Fase 3: Testes (2-3 horas)
- [ ] Criar `DefesaComposicaoValidatorTest` (8-10 casos de teste)
- [ ] Criar `QualificacaoComposicaoValidatorTest` (8-10 casos de teste)
- [ ] Criar `BancaValidatorFactoryTest` (3-4 casos de teste)
- [ ] Testes de integração em `BancaServiceTest`

### Fase 4: Refatoração e Limpeza (1 hora)
- [ ] Remover constantes genéricas antigas do `BancaService`
- [ ] Atualizar documentação JavaDoc
- [ ] Code review e ajustes finais
- [ ] Commit e push

**Tempo Total Estimado:** 6-9 horas

---

## ⚠️ 7. Riscos e Mitigações

| Risco | Impacto | Probabilidade | Mitigação |
|-------|---------|---------------|-----------|
| Quebra de bancas existentes no banco | Alto | Baixo | Validação apenas em novas criações/atualizações |
| Incompatibilidade com dados legados | Médio | Médio | Adicionar flag `skipValidation` para migração |
| Performance com muitos validadores | Baixo | Baixo | Factory usa cache de validadores |
| Regras de negócio mudam | Médio | Médio | Strategy Pattern facilita extensão |

### Estratégia de Rollback
Se houver problemas:
1. Feature flag para desabilitar novos validadores
2. Reverter para validação genérica antiga
3. Git revert do commit de refatoração

---

## 🎯 8. Benefícios Esperados

### Técnicos
✅ **+40% cobertura de testes** em regras de negócio de bancas
✅ **Eliminação de bugs** relacionados a composição inválida
✅ **Código mais limpo** e fácil de manter
✅ **Facilita auditoria** de regras de negócio

### Negócio
✅ **Conformidade com regulamentos** de pós-graduação
✅ **Redução de retrabalho** manual em bancas inválidas
✅ **Melhor experiência** para coordenadores e secretaria
✅ **Documentação clara** das regras institucionais

---

## 📝 9. Questões para Aprovação

Antes de implementar, preciso confirmar:

### 9.1 Regras de Negócio
- [ ] **Defesa**: Confirma que deve ter **pelo menos 1 externo**? (Sim/Não)
- [ ] **Defesa**: Confirma que deve ter **no máximo 2 externos**? (Sim/Não)
- [ ] **Qualificação**: Confirma que **NÃO há restrição de externos**? (Sim/Não)
- [ ] **Ambas**: Mínimo 3 e máximo 5 titulares está correto? (Sim/Não)
- [ ] Há diferença entre **Mestrado vs Doutorado** nas regras? (Especificar se sim)

### 9.2 Implementação
- [ ] Aprovar arquitetura proposta (Strategy Pattern + Factory)?
- [ ] Aprovar estrutura de pastas (`domain/service/banca/validator`)?
- [ ] Aprovar nomes de classes?
- [ ] Implementar tudo de uma vez ou por fases?

### 9.3 Dados Legados
- [ ] Existem bancas antigas no banco que podem violar as novas regras?
- [ ] Precisa de migração de dados antes de ativar validação?
- [ ] Aplicar validação apenas para novas bancas ou retroativo?

---

## ✅ 10. Próximos Passos

Aguardando sua aprovação e respostas às questões acima para:
1. Implementar a solução completa
2. Criar todos os testes unitários
3. Fazer commit com mensagem descritiva
4. Atualizar documentação

---

**Aguardando seu feedback para prosseguir! 🚀**
