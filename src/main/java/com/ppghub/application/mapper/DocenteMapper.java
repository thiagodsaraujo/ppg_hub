package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.DocenteCreateRequest;
import com.ppghub.application.dto.response.DocenteResponse;
import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre DocenteEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DocenteMapper {

    /**
     * Converte CreateRequest para Entity
     */
    @Mapping(target = "instituicao.id", source = "instituicaoId")
    DocenteEntity toEntity(DocenteCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "instituicaoId", source = "instituicao.id")
    @Mapping(target = "instituicaoNome", source = "instituicao.nome")
    @Mapping(target = "instituicaoSigla", source = "instituicao.sigla")
    DocenteResponse toResponse(DocenteEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<DocenteResponse> toResponseList(List<DocenteEntity> entities);

    /**
     * Atualiza Entity existente com dados do Request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instituicao", ignore = true) // Não atualizar instituição via update
    void updateEntityFromRequest(DocenteCreateRequest request, @MappingTarget DocenteEntity entity);

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
