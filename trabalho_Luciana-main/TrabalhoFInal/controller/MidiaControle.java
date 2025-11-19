package TrabalhoFInal.controller;

import TrabalhoFInal.enums.ArquivosSuportados;
import TrabalhoFInal.exceptions.AtributoInvalidoException;
import TrabalhoFInal.exceptions.MenorIgualAZeroException;
import TrabalhoFInal.exceptions.MidiaJaExisteException;
import TrabalhoFInal.exceptions.MidiaNaoEncontradaException;
import TrabalhoFInal.exceptions.NomeVazioException;
import TrabalhoFInal.models.Artista;
import TrabalhoFInal.models.Autor;
import TrabalhoFInal.models.midias.*;
import TrabalhoFInal.services.ServicoArquivoTPOO;
import TrabalhoFInal.viewTemp.Program;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MidiaControle {

    private ServicoArquivoTPOO servicoTPOO;
    private List<Midia> midias;
    private static final String ARQUIVO_CSV = "midias.csv";

    public MidiaControle() {
        this.servicoTPOO = new ServicoArquivoTPOO();
        this.midias = new ArrayList<>();

        // ✅ REMOVIDO: criação de pasta arquivos_tpoo (não é necessária)

        // Carregar dados do CSV ao iniciar
        carregarDadosCSV();
    }

    private void carregarDadosCSV() {
        try {
            List<Midia> midiasCarregadas = lerCSV(ARQUIVO_CSV);
            this.midias.addAll(midiasCarregadas);
            System.out.println("✅ Dados carregados do CSV: " + midiasCarregadas.size() + " mídias");
        } catch (Exception e) {
            System.out.println("ℹ️ Nenhum arquivo CSV encontrado ou erro ao carregar: " + e.getMessage());
        }
    }

    public List<Midia> getMidias() {
        return this.midias;
    }

    public void addMidia(Midia midia) throws MidiaJaExisteException, NomeVazioException {
        if (midia.getTitulo() == null || midia.getTitulo().isBlank()) {
            throw new NomeVazioException("O nome da mídia não pode ser vazio.");
        }

        // Verifica duplicidade
        for (Midia m : midias) {
            if (m.getTitulo().equalsIgnoreCase(midia.getTitulo())) {
                throw new MidiaJaExisteException("Já existe uma mídia com esse nome.");
            }
        }

        midias.add(midia);

        // ✅ CRIA ARQUIVO .TPOO
        servicoTPOO.salvarMidia(midia);

        // Salva no CSV
        salvarCSV();

        System.out.println("✅ Mídia adicionada e arquivos criados: " + midia.getTitulo());
    }

    public void editarMidia(Midia midia, String atributo, String novoValor) {
        if (midia.getTitulo() == null || midia.getTitulo().isBlank()) {
            throw new NomeVazioException("Nome da mídia a editar é inválido.");
        }

        if (atributo == null || atributo.isBlank()) {
            throw new NomeVazioException("Atributo inválido.");
        }

        if (novoValor == null || novoValor.isBlank()) {
            throw new NomeVazioException("Novo valor inválido.");
        }

        for (int i = 0; i < midias.size(); i++) {
            Midia m = midias.get(i);
            if (m.getTitulo().equalsIgnoreCase(midia.getTitulo())) {
                switch (atributo.toLowerCase()) {
                    case "titulo":
                        m.setTitulo(novoValor);
                        break;
                    case "local":
                        m.setLocal(novoValor);
                        break;
                    case "categoria":
                        m.setCategoria(new Categoria(novoValor));
                        break;
                    case "duracao":
                        double duracao = Double.parseDouble(novoValor);
                        if (duracao <= 0) {
                            throw new MenorIgualAZeroException("A duração deve ser maior que zero.");
                        }

                        if (m instanceof Filme f) {
                            f.setMinutos(duracao);
                        }
                        else if (m instanceof Musica mu) {
                            mu.setSegundos(duracao);
                        }
                        else if (m instanceof Livro l) {
                            l.setPaginas(duracao);
                        }
                        break;
                    case "idioma":
                        if (m instanceof Filme f) {
                            f.setIdioma(novoValor);
                        } else {
                            throw new AtributoInvalidoException("Somente filmes possuem idioma.");
                        }
                        break;
                    default:
                        throw new AtributoInvalidoException("Atributo não reconhecido: " + atributo);
                }

                // ✅ ATUALIZA ARQUIVO .TPOO
                servicoTPOO.salvarMidia(m);
                salvarCSV();
                return;
            }
        }

        throw new MidiaNaoEncontradaException("Mídia "+midia.getTitulo() +" não foi encontrada.");
    }

    public void removerMidia(Midia midia){
        if (midia.getTitulo() == null || midia.getTitulo().isBlank()) {
            throw new NomeVazioException("Nome inválido.");
        }

        for (int i = 0; i < midias.size(); i++) {
            Midia m = midias.get(i);

            if (m.getTitulo().equalsIgnoreCase(midia.getTitulo())) {
                // ✅ REMOVE ARQUIVO .TPOO
                servicoTPOO.removerArquivoTPOO(m);

                midias.remove(i);
                salvarCSV();
                return;
            }
        }
        throw new MidiaNaoEncontradaException("Mídia " + midia.getTitulo() + " não foi encontrada.");
    }

    public void moverMidia(Midia midia, Program parentComponent) {
        if (midia == null || midia.getTitulo().isBlank()) {
            throw new NomeVazioException("O nome da mídia é inválido.");
        }

        // ✅ USAR JFileChooser PARA SELECIONAR O NOVO LOCAL
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o novo local para: " + midia.getTitulo());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // ✅ MOSTRAR O LOCAL ATUAL COMO PONTO DE PARTIDA
        if (midia.getLocal() != null && !midia.getLocal().isEmpty()) {
            File localAtual = new File(midia.getLocal());
            if (localAtual.exists()) {
                fileChooser.setSelectedFile(localAtual);
            }
        }

        int resultado = fileChooser.showSaveDialog(parentComponent);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            throw new RuntimeException("Seleção de local cancelada pelo usuário.");
        }

        File novoLocal = fileChooser.getSelectedFile();
        String novaPasta = novoLocal.getAbsolutePath();

        // ✅ ATUALIZAR O LOCAL DA MÍDIA
        for (int i = 0; i < midias.size(); i++) {
            Midia m = midias.get(i);
            if (m.getTitulo().equalsIgnoreCase(midia.getTitulo())) {
                m.setLocal(novaPasta);
                servicoTPOO.salvarMidia(m);
                salvarCSV();
                return;
            }
        }

        throw new MidiaNaoEncontradaException("Mídia " + midia.getTitulo() + " não encontrada.");
    }

    public void renomearMidia(Midia midia, String novoTitulo) {
        if (midia == null || midia.getTitulo().isBlank()) {
            throw new NomeVazioException("O título atual é inválido.");
        }

        if (novoTitulo == null || novoTitulo.isBlank()) {
            throw new NomeVazioException("O novo título é inválido.");
        }

        for (int i = 0; i < midias.size(); i++) {
            Midia m = midias.get(i);
            if (m.getTitulo().equalsIgnoreCase(midia.getTitulo())) {
                for (Midia outra : midias) {
                    if (outra.getTitulo().equalsIgnoreCase(novoTitulo)) {
                        throw new MidiaJaExisteException("Já existe uma mídia com esse nome.");
                    }
                }

                servicoTPOO.removerArquivoTPOO(m);
                m.setTitulo(novoTitulo);
                servicoTPOO.salvarMidia(m);
                salvarCSV();
                return;
            }
        }
        throw new MidiaNaoEncontradaException("Mídia '" + midia.getTitulo() + "' não encontrada.");
    }

    // ✅ MÉTODOS DE FILTRO E ORDENAÇÃO
    public List<Midia> listarPorFormato(String formato) {
        List<Midia> midiaFiltrada = new ArrayList<>();

        for (Midia midia : midias) {
            if (formato.equalsIgnoreCase("FILME") && midia instanceof Filme) {
                midiaFiltrada.add(midia);
            } else if (formato.equalsIgnoreCase("LIVRO") && midia instanceof Livro) {
                midiaFiltrada.add(midia);
            } else if (formato.equalsIgnoreCase("MUSICA") && midia instanceof Musica) {
                midiaFiltrada.add(midia);
            }
        }
        return midiaFiltrada;
    }

    public List<Midia> listarPorCategoria(String categoria) {
        List<Midia> filtradas = new ArrayList<>();

        for (Midia midia : midias) {
            if (midia.getCategoria() != null &&
                    midia.getCategoria().getTitulo().equalsIgnoreCase(categoria)) {
                filtradas.add(midia);
            }
        }
        return filtradas;
    }

    public List<Midia> listarOrdenadoPorFormatoECategoria(String formato, String categoria) {
        List<Midia> filtradas = new ArrayList<>();

        for (Midia midia : midias) {
            boolean formatoMatch = false;
            boolean categoriaMatch = midia.getCategoria() != null &&
                    midia.getCategoria().getTitulo().equalsIgnoreCase(categoria);

            if (formato.equalsIgnoreCase("FILME") && midia instanceof Filme) {
                formatoMatch = true;
            } else if (formato.equalsIgnoreCase("LIVRO") && midia instanceof Livro) {
                formatoMatch = true;
            } else if (formato.equalsIgnoreCase("MUSICA") && midia instanceof Musica) {
                formatoMatch = true;
            }

            if (formatoMatch && categoriaMatch) {
                filtradas.add(midia);
            }
        }
        return filtradas;
    }

    public List<Midia> listarOrdenadoPorTitulo() {
        List<Midia> midiasOrdenadas = new ArrayList<>(midias);
        Collections.sort(midiasOrdenadas,
                Comparator.comparing(midia -> midia.getTitulo().toLowerCase()));
        return midiasOrdenadas;
    }

    public List<Midia> listarOrdenadoPorDuracao() {
        List<Midia> midiasOrdenadasPorDuracao = new ArrayList<>(midias);
        midiasOrdenadasPorDuracao.sort(Comparator.comparing(Midia::duracao));
        return midiasOrdenadasPorDuracao;
    }

    // ✅ MÉTODOS CSV
    public void salvarCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            for (Midia m : midias) {
                writer.write(m.toCSV());
                writer.newLine();
            }
            System.out.println("✅ CSV salvo: " + ARQUIVO_CSV);
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar CSV: " + e.getMessage());
        }
    }

    public List<Midia> lerCSV(String caminho) {
        List<Midia> midiasLidas = new ArrayList<>();
        File arquivo = new File(caminho);

        if (!arquivo.exists()) {
            System.out.println("ℹ️ Arquivo CSV não encontrado: " + caminho);
            return midiasLidas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                try {
                    String[] partes = linha.split(";");
                    if (partes.length < 5) continue;

                    switch (partes[0]) {
                        case "FILME":
                            if (partes.length >= 8) {
                                Filme f = new Filme(
                                        partes[1],
                                        new Categoria(partes[2]),
                                        ArquivosSuportados.valueOf(partes[3]),
                                        partes[4] // idioma
                                );
                                f.setLocal(partes[5]); // local
                                f.setMinutos((long) Double.parseDouble(partes[6]));
                                f.setTamanho(Double.parseDouble(partes[7]));
                                midiasLidas.add(f);
                            }
                            break;

                        case "MUSICA":
                            if (partes.length >= 8) {
                                Musica mu = new Musica(
                                        partes[1],
                                        new Categoria(partes[2]),
                                        ArquivosSuportados.valueOf(partes[3])
                                );
                                mu.setLocal(partes[4]); // local
                                mu.setArtista(new Artista(partes[5])); // artista
                                mu.setSegundos((long) Double.parseDouble(partes[6]));
                                mu.setTamanho(Double.parseDouble(partes[7]));
                                midiasLidas.add(mu);
                            }
                            break;

                        case "LIVRO":
                            if (partes.length >= 8) {
                                Livro l = new Livro(
                                        partes[1],
                                        new Categoria(partes[2]),
                                        ArquivosSuportados.valueOf(partes[3])
                                );
                                l.setLocal(partes[4]); // local
                                l.setAutor(new Autor(partes[5])); // autor
                                l.setPaginas((long) Double.parseDouble(partes[6]));
                                l.setTamanho(Double.parseDouble(partes[7]));
                                midiasLidas.add(l);
                            }
                            break;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erro ao processar linha CSV: " + linha + " - " + e.getMessage());
                }
            }
            System.out.println("✅ CSV carregado: " + midiasLidas.size() + " mídias");
        } catch (IOException e) {
            System.err.println("❌ Erro ao ler CSV: " + e.getMessage());
        }
        return midiasLidas;
    }

    public Midia  listarOrdenadoPorTitulo(String titulo) {
        for (Midia midia : midias) {
            if (midia.getTitulo().equalsIgnoreCase(titulo)) {
                return midia;
            }
        }
        throw new MidiaNaoEncontradaException("Mídia '" + titulo + "' não encontrada.");
    }
}