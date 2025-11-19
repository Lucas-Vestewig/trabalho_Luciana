package TrabalhoFInal.models;

public class Artista extends Pessoa {

    public Artista(String nome) {
        super(nome);
    }

    @Override
    public String toString() {
        return "Artista: " + getNome();
    }

}
