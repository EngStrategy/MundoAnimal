package com.carvalhotechsolutions.mundoanimal.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_servicos")
public class Servico {

    @Id
    @GeneratedValue(GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false, unique = false)
    private String nomeServico;

    @Column(nullable = false)
    private BigDecimal valorServico;

    private String descricao;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
