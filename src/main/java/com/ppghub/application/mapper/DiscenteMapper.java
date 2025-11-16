package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.DiscenteCreateRequest;
import com.ppghub.application.dto.request.DiscenteUpdateRequest;
import com.ppghub.application.dto.response.DiscenteResponse;
import com.ppghub.infrastructure.persistence.entity.DiscenteEntity;
import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre DiscenteEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DiscenteMapper {

    /**
     * Converte CreateRequest para Entity
     */
    @Mapping(target = "programa", source = "programaId", qualifiedByName = "idToPrograma")
    @Mapping(target = "orientador", source = "orientadorId", qualifiedByName = "idToDocente")
    @Mapping(target = "statusMatricula", source = "statusMatricula", qualifiedByName = "stringToStatusMatricula")
    @Mapping(target = "nivelFormacao", source = "nivelFormacao", qualifiedByName = "stringToNivelFormacao")
    DiscenteEntity toEntity(DiscenteCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "programaId", source = "programa.id")
    @Mapping(target = "programaNome", source = "programa.nome")
    @Mapping(target = "programaSigla", source = "programa.sigla")
    @Mapping(target = "orientadorId", source = "orientador.id")
    @Mapping(target = "orientadorNome", source = "orientador.nomeCompleto")
    @Mapping(target = "statusMatricula", source = "statusMatricula", qualifiedByName = "statusMatriculaToString")
    @Mapping(target = "nivelFormacao", source = "nivelFormacao", qualifiedByName = "nivelFormacaoToString")
    DiscenteResponse toResponse(DiscenteEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<DiscenteResponse> toResponseList(List<DiscenteEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "programa", ignore = true) // Não atualizar programa via update
    @Mapping(target = "orientador", ignore = true) // Atualizar via service
    @Mapping(target = "statusMatricula", source = "statusMatricula", qualifiedByName = "stringToStatusMatricula")
    void updateEntityFromRequest(DiscenteUpdateRequest request, @MappingTarget DiscenteEntity entity);

    /**
     * Mapeia ID para ProgramaEntity
     */
    @Named("idToPrograma")
    default ProgramaEntity idToPrograma(Long programaId) {
        if (programaId == null) {
            return null;
        }
        ProgramaEntity programa = new ProgramaEntity();
        programa.setId(programaId);
        return programa;
    }

    /**
     * Mapeia ID para DocenteEntity
     */
    @Named("idToDocente")
    default DocenteEntity idToDocente(Long docenteId) {
        if (docenteId == null) {
            return null;
        }
        DocenteEntity docente = new DocenteEntity();
        docente.setId(docenteId);
        return docente;
    }

    /**
     * Converte String para Enum StatusMatricula
     */
    @Named("stringToStatusMatricula")
    default DiscenteEntity.StatusMatricula stringToStatusMatricula(String status) {
        if (status == null) {
            return null;
        }
        return DiscenteEntity.StatusMatricula.valueOf(status);
    }

    /**
     * Converte Enum StatusMatricula para String
     */
    @Named("statusMatriculaToString")
    default String statusMatriculaToString(DiscenteEntity.StatusMatricula status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    /**
     * Converte String para Enum NivelFormacao
     */
    @Named("stringToNivelFormacao")
    default DiscenteEntity.NivelFormacao stringToNivelFormacao(String nivel) {
        if (nivel == null) {
            return null;
        }
        return DiscenteEntity.NivelFormacao.valueOf(nivel);
    }

    /**
     * Converte Enum NivelFormacao para String
     */
    @Named("nivelFormacaoToString")
    default String nivelFormacaoToString(DiscenteEntity.NivelFormacao nivel) {
        if (nivel == null) {
            return null;
        }
        return nivel.name();
    }
}
