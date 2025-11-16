package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.ProfessorExternoCreateRequest;
import com.ppghub.application.dto.request.ProfessorExternoUpdateRequest;
import com.ppghub.application.dto.response.ProfessorExternoResponse;
import com.ppghub.infrastructure.persistence.entity.ProfessorExternoEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre ProfessorExternoEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProfessorExternoMapper {

    /**
     * Converte CreateRequest para Entity
     */
    ProfessorExternoEntity toEntity(ProfessorExternoCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "numeroParticipacoes", expression = "java(entity.getNumeroParticipacoes())")
    ProfessorExternoResponse toResponse(ProfessorExternoEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<ProfessorExternoResponse> toResponseList(List<ProfessorExternoEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProfessorExternoUpdateRequest request, @MappingTarget ProfessorExternoEntity entity);
}
