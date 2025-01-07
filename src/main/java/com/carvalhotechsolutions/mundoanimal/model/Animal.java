package com.carvalhotechsolutions.mundoanimal.model;

import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_animais")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecieAnimal especie;

    @Column(nullable = false)
    private String raca;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false)
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente dono;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EspecieAnimal getEspecie() {
        return especie;
    }

    public void setEspecie(EspecieAnimal especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Cliente getDono() {
        return dono;
    }

    public void setDono(Cliente dono) {
        this.dono = dono;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
