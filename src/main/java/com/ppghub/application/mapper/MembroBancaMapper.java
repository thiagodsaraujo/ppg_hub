package com.ppghub.application.mapper;

import com.ppghub.application.dto.request.MembroBancaCreateRequest;
import com.ppghub.application.dto.response.MembroBancaResponse;
import com.ppghub.infrastructure.persistence.entity.*;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper para conversão entre MembroBancaEntity e DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MembroBancaMapper {

    /**
     * Converte CreateRequest para Entity
     */
    @Mapping(target = "banca", ignore = true) // Banca será definida no service
    @Mapping(target = "docente", source = "docenteId", qualifiedByName = "idToDocente")
    @Mapping(target = "professorExterno", source = "professorExternoId", qualifiedByName = "idToProfessorExterno")
    @Mapping(target = "tipoMembro", source = "tipoMembro", qualifiedByName = "stringToTipoMembro")
    @Mapping(target = "funcao", source = "funcao", qualifiedByName = "stringToFuncao")
    @Mapping(target = "statusConvite", source = "statusConvite", qualifiedByName = "stringToStatusConvite")
    MembroBancaEntity toEntity(MembroBancaCreateRequest request);

    /**
     * Converte Entity para Response
     */
    @Mapping(target = "bancaId", source = "banca.id")
    @Mapping(target = "docenteId", source = "docente.id")
    @Mapping(target = "docenteNome", source = "docente.nomeCompleto")
    @Mapping(target = "docenteInstituicao", source = "docente.instituicao.nome")
    @Mapping(target = "professorExternoId", source = "professorExterno.id")
    @Mapping(target = "professorExternoNome", source = "professorExterno.nome")
    @Mapping(target = "professorExternoInstituicao", source = "professorExterno.instituicaoOrigem")
    @Mapping(target = "tipoMembro", source = "tipoMembro", qualifiedByName = "tipoMembroToString")
    @Mapping(target = "funcao", source = "funcao", qualifiedByName = "funcaoToString")
    @Mapping(target = "statusConvite", source = "statusConvite", qualifiedByName = "statusConviteToString")
    @Mapping(target = "externo", expression = "java(entity.isExterno())")
    @Mapping(target = "confirmado", expression = "java(entity.isConfirmado())")
    MembroBancaResponse toResponse(MembroBancaEntity entity);

    /**
     * Converte lista de Entities para lista de Responses
     */
    List<MembroBancaResponse> toResponseList(List<MembroBancaEntity> entities);

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
     * Mapeia ID para ProfessorExternoEntity
     */
    @Named("idToProfessorExterno")
    default ProfessorExternoEntity idToProfessorExterno(Long professorExternoId) {
        if (professorExternoId == null) {
            return null;
        }
        ProfessorExternoEntity professorExterno = new ProfessorExternoEntity();
        professorExterno.setId(professorExternoId);
        return professorExterno;
    }

    /**
     * Converte String para Enum TipoMembro
     */
    @Named("stringToTipoMembro")
    default MembroBancaEntity.TipoMembro stringToTipoMembro(String tipo) {
        if (tipo == null) {
            return null;
        }
        return MembroBancaEntity.TipoMembro.valueOf(tipo);
    }

    /**
     * Converte Enum TipoMembro para String
     */
    @Named("tipoMembroToString")
    default String tipoMembroToString(MembroBancaEntity.TipoMembro tipo) {
        if (tipo == null) {
            return null;
        }
        return tipo.name();
    }

    /**
     * Converte String para Enum Funcao
     */
    @Named("stringToFuncao")
    default MembroBancaEntity.Funcao stringToFuncao(String funcao) {
        if (funcao == null) {
            return null;
        }
        return MembroBancaEntity.Funcao.valueOf(funcao);
    }

    /**
     * Converte Enum Funcao para String
     */
    @Named("funcaoToString")
    default String funcaoToString(MembroBancaEntity.Funcao funcao) {
        if (funcao == null) {
            return null;
        }
        return funcao.name();
    }

    /**
     * Converte String para Enum StatusConvite
     */
    @Named("stringToStatusConvite")
    default MembroBancaEntity.StatusConvite stringToStatusConvite(String status) {
        if (status == null) {
            return null;
        }
        return MembroBancaEntity.StatusConvite.valueOf(status);
    }

    /**
     * Converte Enum StatusConvite para String
     */
    @Named("statusConviteToString")
    default String statusConviteToString(MembroBancaEntity.StatusConvite status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
