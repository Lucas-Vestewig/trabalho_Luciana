package TrabalhoFInal.models.midias;

import TrabalhoFInal.enums.ArquivosSuportados;
import TrabalhoFInal.exceptions.MenorIgualAZeroException;
import TrabalhoFInal.exceptions.NomeVazioException;
import TrabalhoFInal.models.Artista;

import java.io.*;

public class Musica extends Midia {

    private Artista artista;
    private File arquivo;
    private long segundos;


    public Musica(String titulo, Categoria categoria, ArquivosSuportados arquivosSuportados) {
        super(titulo, categoria, arquivosSuportados); //colocar os atributos necessarios dentro da musica
        setSegundos(segundos);

    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        if (artista.getNome() == null  || artista.getNome() == "") {
            throw new NomeVazioException("Nome do artista ");
        }
        this.artista = artista;
    }
    public double getTamanhoArquivo() {
        return arquivo.length();
    }

    public long getSegundos() {
        return segundos;
    }

    public void setSegundos(double segundos) {
        if (segundos < 0) {
            throw new MenorIgualAZeroException("A quantidade de segundos");
        }
        this.segundos = (long) segundos;
    }

    @Override
    public double duracao() throws MenorIgualAZeroException {
        if (arquivo != null && arquivo.exists()) {
            try (BufferedReader lerArquivo = new BufferedReader(new FileReader(arquivo))) {
                System.out.println(arquivo.getAbsolutePath());

                String linha;
                while ((linha = lerArquivo.readLine()) != null){
                    if (linha.startsWith("Duracao: ")) {
                        String[] linhaArquivo = linha.split(":");
                        if (linhaArquivo.length > 1) {
                            double duracao = Double.parseDouble(linhaArquivo[1]);
                            System.out.println("Duracao: " + duracao);
                            return duracao;
                        }
                    }
                }
                return getSegundos();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Arquivo nao encontrado" + e);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler o arquivo" + e);
            }
        }
        return getSegundos();
    }

    @Override
    public double tamanhoArquivo() {
        if (arquivo != null && arquivo.exists()) {
            return arquivo.length() / (1024.0 * 1024.0);
        }
        return 0;
    }

    @Override
    public ArquivosSuportados arquivosSuportados() {
        if (getLocal() != null){
            if (arquivo != null && getLocal().toLowerCase().endsWith(".mp3")) return ArquivosSuportados.MP3;
        }
        return getArquivosSuportados();
    }

    @Override
    public void exibir() {
        System.out.println("Nome: " + super.getTitulo() + "\n" + "Categoria: " + super.getCategoria() + "\n" + "Artista: " + getArtista().getNome() + "\nDuração: " + duracao());
    }
    @Override
    public String toTPOO() {
        StringBuilder sb = new StringBuilder();
        sb.append("TIPO:MUSICA\n");
        sb.append("TITULO:").append(getTitulo() != null ? getTitulo() : "").append("\n");
        sb.append("CATEGORIA:").append(getCategoria() != null ? getCategoria().getTitulo() : "").append("\n");
        sb.append("DURACAO:").append(getSegundos()).append(" segundos\n");
        sb.append("LOCAL:").append(getLocal() != null ? getLocal() : "").append("\n");
        sb.append("ARTISTA:").append(getArtista() != null ? getArtista().getNome() : "").append("\n");
        sb.append("FORMATOS_SUPORTADOS:MP3\n");
        sb.append("TAMANHO_ARQUIVO:").append(String.format("%.2f", tamanhoArquivo())).append(" MB\n");
        return sb.toString();
    }

    @Override
    public String toCSV(){
        return "MUSICA;" +
                getTitulo() + ";" +
                getCategoria().getTitulo() + ";" +
                arquivosSuportados() + ";" +
                getLocal() + ";" +
                artista.getNome() + ";" + //para não sair @hashcode
                duracao() + ";" +
                tamanhoArquivo();
    }
}
