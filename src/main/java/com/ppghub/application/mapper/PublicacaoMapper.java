package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.PublicacaoCreateRequest;
import com.ppghub.application.dto.response.AutoriaResponse;
import com.ppghub.application.dto.response.PublicacaoResponse;
import com.ppghub.infrastructure.persistence.entity.AutoriaEntity;
import com.ppghub.infrastructure.persistence.entity.PublicacaoEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre PublicacaoEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PublicacaoMapper {

    /**
     * Converte CreateRequest para Entity
     */
    PublicacaoEntity toEntity(PublicacaoCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "autores", source = "autorias")
    PublicacaoResponse toResponse(PublicacaoEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<PublicacaoResponse> toResponseList(List<PublicacaoEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "autorias", ignore = true) // Autorias gerenciadas separadamente
    void updateEntityFromRequest(PublicacaoCreateRequest request, @MappingTarget PublicacaoEntity entity);

    /**
     * Converte AutoriaEntity para AutoriaResponse
     */
    @Mapping(target = "publicacaoId", source = "publicacao.id")
    @Mapping(target = "docenteId", source = "docente.id")
    @Mapping(target = "docenteNome", source = "docente.nomeCompleto")
    AutoriaResponse toAutoriaResponse(AutoriaEntity entity);

    /**
     * Converte lista de autorias
     */
    List<AutoriaResponse> toAutoriaResponseList(List<AutoriaEntity> entities);
}
