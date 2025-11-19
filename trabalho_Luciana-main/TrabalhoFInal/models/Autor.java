package TrabalhoFInal.models;

public class Autor extends Pessoa {

    public Autor(String nome) {
        super(nome);
    }

    @Override
    public String toString() {
        return "Autor: " + getNome();
    }
}
