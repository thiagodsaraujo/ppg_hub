package com.ppghub.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de Instituição.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstituicaoCreateRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    @NotBlank(message = "Sigla é obrigatória")
    @Size(max = 50, message = "Sigla deve ter no máximo 50 caracteres")
    private String sigla;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 50)
    private String tipo; // PUBLICA, PRIVADA, ESPECIAL

    @Size(max = 50)
    private String categoria; // FEDERAL, ESTADUAL, MUNICIPAL, PARTICULAR, COMUNITARIA

    @Size(max = 18)
    private String cnpj;

    // Endereço
    @Size(max = 255)
    private String logradouro;

    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @Size(max = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
    private String estado;

    @Size(max = 9)
    private String cep;

    @Size(max = 100)
    private String pais;

    // Contato
    @Size(max = 20)
    private String telefone;

    @Email(message = "Email inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 500)
    private String website;

    // OpenAlex Integration (opcional no create)
    @Size(max = 50)
    private String openalexInstitutionId;

    @Size(max = 50)
    private String rorId;
}
