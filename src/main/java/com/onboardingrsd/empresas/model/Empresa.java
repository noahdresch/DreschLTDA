package com.onboardingrsd.empresas.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entidade de domínio Empresa — mapeada à tabela {@code empresa}.
 */
@Entity
@Table(name = "empresa", uniqueConstraints = {
        @UniqueConstraint(name = "uk_empresa_cnpj", columnNames = "cnpj")
})
public class Empresa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Razão Social é obrigatória")
    @Size(max = 120, message = "Razão Social deve ter no máximo {max} caracteres")
    @Column(name = "razao_social", nullable = false, length = 120)
    private String razaoSocial;

    @NotBlank(message = "Nome Fantasia é obrigatório")
    @Size(max = 120, message = "Nome Fantasia deve ter no máximo {max} caracteres")
    @Column(name = "nome_fantasia", nullable = false, length = 120)
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter exatamente 14 dígitos (somente números)")
    @Column(name = "cnpj", nullable = false, length = 14)
    private String cnpj;

    @NotBlank(message = "Ramo de Atividade é obrigatório")
    @Size(max = 80, message = "Ramo de Atividade deve ter no máximo {max} caracteres")
    @Column(name = "ramo_atividade", nullable = false, length = 80)
    private String ramoAtividade;

    @NotNull(message = "Data de Fundação é obrigatória")
    @Column(name = "data_fundacao", nullable = false)
    private LocalDate dataFundacao;

    @NotNull(message = "Tipo da Empresa é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empresa", nullable = false, length = 30)
    private TipoEmpresa tipoEmpresa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRamoAtividade() {
        return ramoAtividade;
    }

    public void setRamoAtividade(String ramoAtividade) {
        this.ramoAtividade = ramoAtividade;
    }

    public LocalDate getDataFundacao() {
        return dataFundacao;
    }

    public void setDataFundacao(LocalDate dataFundacao) {
        this.dataFundacao = dataFundacao;
    }

    public TipoEmpresa getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Empresa empresa)) {
            return false;
        }
        return id != null && Objects.equals(id, empresa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Empresa{id=" + id + ", razaoSocial='" + razaoSocial + "', cnpj='" + cnpj + "'}";
    }
}
