package TrabalhoFInal.models.midias;

import TrabalhoFInal.exceptions.MenorIgualAZeroException;

public class Categoria {

    private String tituloCategoria;

    public Categoria(String tituloCategoria) {
        setTitulo(tituloCategoria);
    }

    public String getTitulo() {
        return tituloCategoria;
    }
    public void setTitulo(String tituloCategoria) throws MenorIgualAZeroException {
        if (tituloCategoria == null || tituloCategoria.isBlank()) {
            throw new MenorIgualAZeroException("Nome do arquivo ");
        }
        this.tituloCategoria = tituloCategoria;
    }

}
