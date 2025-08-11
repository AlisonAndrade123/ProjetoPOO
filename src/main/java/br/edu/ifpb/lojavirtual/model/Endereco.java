package br.edu.ifpb.lojavirtual.model;

public class Endereco {
    private final String rua;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;

    public Endereco(String rua, String numero, String complemento, String bairro, String cidade, String estado, String cep) {
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }

    @Override
    public String toString() {
        String comp = (complemento == null || complemento.trim().isEmpty()) ? "" : " - " + complemento;
        return String.format("%s, %s%s - %s\n%s-%s, CEP: %s",
                rua, numero, comp, bairro, cidade, estado, cep);
    }
}