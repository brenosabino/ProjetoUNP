package com.breno.projeto.model;

import java.io.Serializable;

public class Corretoras implements Serializable {

    /**
     * POJO
     */
    private static final long serialVersionUID = 1L;
    private String nome;
    private String cpf;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return nome;
    }

}
