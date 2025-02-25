package com.carvalhotechsolutions.mundoanimal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_secretarios")
public class Secretario extends Usuario {

    @Column(nullable = false, unique = true)
    private String telefone;

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


}
