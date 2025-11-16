package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.ProgramaCreateRequest;
import com.ppghub.application.dto.response.ProgramaResponse;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre ProgramaEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProgramaMapper {

    /**
     * Converte CreateRequest para Entity
     */
    @Mapping(target = "instituicao.id", source = "instituicaoId")
    ProgramaEntity toEntity(ProgramaCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "instituicaoId", source = "instituicao.id")
    @Mapping(target = "instituicaoNome", source = "instituicao.nome")
    @Mapping(target = "instituicaoSigla", source = "instituicao.sigla")
    ProgramaResponse toResponse(ProgramaEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<ProgramaResponse> toResponseList(List<ProgramaEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instituicao", ignore = true) // Não atualizar instituição via update
    void updateEntityFromRequest(ProgramaCreateRequest request, @MappingTarget ProgramaEntity entity);

    /**
     * Cria InstituicaoEntity apenas com ID
     */
    default InstituicaoEntity map(Long instituicaoId) {
        if (instituicaoId == null) {
            return null;
        }
        InstituicaoEntity instituicao = new InstituicaoEntity();
        instituicao.setId(instituicaoId);
        return instituicao;
    }
}
