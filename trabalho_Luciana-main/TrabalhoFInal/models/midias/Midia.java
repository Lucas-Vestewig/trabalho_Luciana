package TrabalhoFInal.models.midias;

import TrabalhoFInal.enums.ArquivosSuportados;
import TrabalhoFInal.exceptions.*;

public abstract class Midia {
    private String titulo;
    private String local;
    private ArquivosSuportados arquivosSuportados;
    private Categoria categoria;
    protected double tamanho;

    public Midia(){
        super();
    }

    public Midia(String titulo, Categoria categoria, ArquivosSuportados arquivosSuportados) {
        setTitulo(titulo);
        setCategoria(categoria);
        setArquivosSuportados(arquivosSuportados);
    }

    public String getTitulo() {
        return titulo;
    }

    //titulo nao pode ser vazio
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new NomeVazioException ("O Título ");
        }
        this.titulo = titulo;
    }

    public String getLocal() {
        return local;
    }

    //path do arquivo
    public void setLocal(String local) {
        if (local == null || local.isBlank()) {
            throw new LocalVazioException("O Local ");
        }
        this.local = local;
    }

    public ArquivosSuportados getArquivosSuportados() {
        return arquivosSuportados;
    }

    public void setArquivosSuportados(ArquivosSuportados arquivosSuportados) {
        if (arquivosSuportados == null) {
            throw new ArquivoNaoPodeSerNuloException("Arquivo Suportado ");
        }
        this.arquivosSuportados = arquivosSuportados;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new CategoriaNaoPodeSerNulaException("Categoria ");
        }
        this.categoria = categoria;
    }

    public double getTamanho() {
        return tamanho;
    }
    public void setTamanho(double tamanho) {
        this.tamanho = tamanho;
    }

    public abstract void exibir(); //aqui iremos exibir os dados dos arquivos

    public abstract String toTPOO() throws ArquivoNaoPodeSerNuloException;

    public abstract double duracao() throws MenorIgualAZeroException;

    public double tamanhoArquivo(){
        return this.tamanho;
    }; //aqui teremos um método de tamanho de arquivo

    public abstract ArquivosSuportados arquivosSuportados(); //aqui iremos definir os arquivos de cada um dos meus tipos de midia

    public abstract String toCSV();
}
