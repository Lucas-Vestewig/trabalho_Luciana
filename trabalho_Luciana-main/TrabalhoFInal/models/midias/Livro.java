package TrabalhoFInal.models.midias;

import TrabalhoFInal.enums.ArquivosSuportados;
import TrabalhoFInal.exceptions.MenorIgualAZeroException;
import TrabalhoFInal.exceptions.NomeVazioException;
import TrabalhoFInal.models.Autor;

import java.io.*;

public class Livro extends Midia {

    private Autor autor;
    private File arquivo;
    private long paginas;

    public Livro(String titulo, Categoria categoria, ArquivosSuportados arquivosSuportados)  {
        super(titulo, categoria, arquivosSuportados); //apenas exemplo
        setPaginas(paginas);

    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        if (autor.getNome() == null || autor.getNome() == "") {
            throw new NomeVazioException("Nome do artista");
        }
        this.autor = autor;
    }
    public double getTamanhoArquivo() {
        return arquivo.length();
    }

    public long getPaginas() {
        return paginas;
    }

    public void setPaginas(double paginas) {
        if (paginas < 0) {
            throw new MenorIgualAZeroException("A quantidade de páginas");
        }
        this.paginas = (long) paginas;
    }

    @Override
    public double duracao() throws MenorIgualAZeroException {
        if (arquivo != null && arquivo.exists()) {
            try (BufferedReader lerArquivo = new BufferedReader(new FileReader(arquivo))) {
                System.out.println(arquivo.getAbsolutePath());

                String linha;
                while ((linha = lerArquivo.readLine()) != null){
                    if (linha.startsWith("Paginas: ")) {
                        String[] partes = linha.split(":");
                        if (partes.length > 2) {
                            double duracao = Double.parseDouble(partes[1].trim());
                            System.out.println("Paginas: " + duracao);
                            return duracao;
                        }
                    }
                }
                return getPaginas();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Arquivo nao encontrado" + e);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler o arquivo" + e);
            }
        } else {
            return getPaginas();
        }
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
        if (getLocal() != null) {
            if (arquivo != null && getLocal().toLowerCase().endsWith(".PDF")) return ArquivosSuportados.PDF;
            if (arquivo != null && getLocal().toLowerCase().endsWith(".EPUB")) return ArquivosSuportados.EPUB;
        }
        return getArquivosSuportados();
    }
    @Override
    public String toTPOO() {
        StringBuilder sb = new StringBuilder();
        sb.append("TIPO:LIVRO\n");
        sb.append("TITULO:").append(getTitulo() != null ? getTitulo() : "").append("\n");
        sb.append("CATEGORIA:").append(getCategoria() != null ? getCategoria().getTitulo() : "").append("\n");
        sb.append("DURACAO:").append(getPaginas()).append(" páginas\n");
        sb.append("LOCAL:").append(getLocal() != null ? getLocal() : "").append("\n");
        sb.append("AUTOR:").append(getAutor() != null ? getAutor().getNome() : "").append("\n");
        sb.append("FORMATOS_SUPORTADOS:PDF,EPUB\n");
        sb.append("TAMANHO_ARQUIVO:").append(String.format("%.2f", tamanhoArquivo())).append(" MB\n");
        return sb.toString();
    }

    @Override
    public void exibir() {
        System.out.println("Livro " + super.getTitulo() +
                "Categoria: " + getCategoria() +
                "\nArtista: " + getAutor().getNome() +
                "\nDuração: " + duracao());
    }

    @Override
    public String toCSV(){
        return "Livro;" +
                getTitulo() + ";" +
                getCategoria().getTitulo() + ";" +
                arquivosSuportados() + ";" +
                getLocal() + ";" +
                autor.getNome() + ";" + //para não sair o @hashcode
                duracao() + ";" +
                tamanhoArquivo();

    }
}
