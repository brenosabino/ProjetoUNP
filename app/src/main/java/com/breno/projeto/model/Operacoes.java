package com.breno.projeto.model;

public class Operacoes {

    private int id;
    private String corretora;
    private String compra;

    public Operacoes(){}

    public Operacoes(String corretora, String compra) {
        super();
        this.corretora = corretora;
        this.compra = compra;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCorretora() {
        return corretora;
    }
    public void setCorretora(String corretora) {
        this.corretora = corretora;
    }
    public String getCompra() {
        return compra;
    }
    public void setCompra(String compra) {
        this.compra = compra;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Operação [id=" + id + ", Corretora = " + corretora + ", Valor=" + compra
                + "]";
    }

}
