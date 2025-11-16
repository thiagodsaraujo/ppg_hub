package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.InstituicaoCreateRequest;
import com.ppghub.application.dto.response.InstituicaoResponse;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre InstituicaoEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface InstituicaoMapper {

    /**
     * Converte CreateRequest para Entity
     */
    InstituicaoEntity toEntity(InstituicaoCreateRequest request);

    /**
     * Converte Entity para Response
     */
    InstituicaoResponse toResponse(InstituicaoEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<InstituicaoResponse> toResponseList(List<InstituicaoEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(InstituicaoCreateRequest request, @MappingTarget InstituicaoEntity entity);
}
