package TrabalhoFInal.models.midias;

import TrabalhoFInal.enums.ArquivosSuportados;
import TrabalhoFInal.exceptions.MenorIgualAZeroException;
import TrabalhoFInal.exceptions.NomeVazioException;

import java.io.*;

public class Filme extends Midia {

    private String idioma;
    private File arquivo;
    private long minutos;

    public Filme(String titulo, Categoria categoria, ArquivosSuportados arquivosSuportados, String idioma) {
        super(titulo, categoria, arquivosSuportados);
        setIdioma(idioma);

    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        if (idioma == null || idioma.trim().isEmpty()) {
            throw new NomeVazioException("Idioma ");
        }
        this.idioma = idioma;
    }

    public long getMinutos() {
        return minutos;
    }

    public void setMinutos(double minutos) {
        if (minutos <= 0) {
            throw new MenorIgualAZeroException("Minutos ");
        }
        this.minutos = (long) minutos;
    }
    public double getTamanhoArquivo() {
        return arquivo.length();
    }

    @Override
    public double duracao() throws MenorIgualAZeroException {
        // ✅ SEMPRE usa o atributo minutos - não tenta ler arquivo
        if (minutos <= 0) {
            throw new MenorIgualAZeroException("Duração do filme deve ser maior que zero");
        }
        return minutos;
    }

    @Override
    public double tamanhoArquivo() {
        if (arquivo != null && arquivo.exists()) {
            return arquivo.length() / (1024.0 * 1024.0); //conversao para mb
        }
        return 0;
    }

    @Override
    public ArquivosSuportados arquivosSuportados() {
        if (getLocal() != null) { //se houver um arquivo ele vai filtrar todos os arquivos que terminam com mp4 e mkv e irao ser buscados
            if (arquivo != null && getLocal().toLowerCase().endsWith(".mp4")) return ArquivosSuportados.MP4;
            if (arquivo != null && getLocal().toLowerCase().endsWith(".mkv")) return ArquivosSuportados.MKV;
        }
        return super.getArquivosSuportados(); //retorna o valor do atributo
    }

    //aprimorar
    @Override
    public void exibir() {
        System.out.println("Filme " + super.getTitulo() +
                "Categoria: " + getCategoria() +
                "\nIdioma: " + getIdioma() +
                "\nDuração: " + duracao()); // apenas para testes, mas acredito que seja isso
    }
    @Override
    public String toTPOO() {
        StringBuilder sb = new StringBuilder();
        sb.append("TIPO:FILME\n");
        sb.append("TITULO:").append(getTitulo() != null ? getTitulo() : "").append("\n");
        sb.append("CATEGORIA:").append(getCategoria() != null ? getCategoria().getTitulo() : "").append("\n");
        sb.append("DURACAO:").append(getMinutos()).append(" minutos\n");
        sb.append("LOCAL:").append(getLocal() != null ? getLocal() : "").append("\n");
        sb.append("IDIOMA:").append(getIdioma() != null ? getIdioma() : "").append("\n");
        sb.append("FORMATOS_SUPORTADOS:MP4,MKV\n");
        sb.append("TAMANHO_ARQUIVO:").append(String.format("%.2f", tamanhoArquivo())).append(" MB\n");
        return sb.toString();
    }

    @Override
    public String toCSV() {
        return "FILME;" +
                getTitulo() + ";" +
                getCategoria().getTitulo() + ";" +
                arquivosSuportados() + ";" +
                getIdioma() + ";" +
                getLocal() + ";" +
                duracao() + ";" +
                tamanhoArquivo();
    }
}
