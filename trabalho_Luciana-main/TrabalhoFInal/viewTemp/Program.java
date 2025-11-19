package TrabalhoFInal.viewTemp;

import TrabalhoFInal.controller.MidiaControle;
import TrabalhoFInal.enums.ArquivosSuportados;
import TrabalhoFInal.exceptions.MidiaJaExisteException;
import TrabalhoFInal.exceptions.MidiaNaoEncontradaException;
import TrabalhoFInal.exceptions.NomeVazioException;
import TrabalhoFInal.models.Artista;
import TrabalhoFInal.models.Autor;
import TrabalhoFInal.models.midias.*;
import TrabalhoFInal.services.ServicoArquivoTPOO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class Program extends JFrame {
    private JTextArea areaTexto;
    private MidiaControle midiaControle;

    public Program() {
        setTitle("Sistema de Mídias - Trabalho Final");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        midiaControle = new MidiaControle();

        criarSistema();
        setVisible(true);
    }

    private void criarSistema() {
        JPanel painel = new JPanel(new BorderLayout());

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(areaTexto);

        JPanel painelBotoes = new JPanel(new GridLayout(0, 2, 5, 5));

        String[] opcoes = {
                "0 - Sair",
                "1 - Incluir mídia",
                "2 - Editar mídia",
                "3 - Excluir mídia",
                "4 - Mover mídia",
                "5 - Renomear arquivo",
                "6 - Listar por formato",
                "7 - Listar por categoria",
                "8 - Ordenar por título",
                "9 - Ordenar por duração",
                "10 - Listar por formato/categoria",
                "11 - Características da mídia"
        };

        for (int i = 0; i < opcoes.length; i++) {
            JButton botao = new JButton(opcoes[i]);
            final int opcao = i;

            botao.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    processarOpcao(opcao);
                }
            });

            painelBotoes.add(botao);
        }

        painel.add(new JLabel("Sistema de Gerenciamento de Mídias", JLabel.CENTER), BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);
        add(painel);
    }

    private void processarOpcao(int opcao) {
        areaTexto.append("\n=== Opção " + opcao + " ===\n");

        switch (opcao) {
            case 0:
                sairPrograma();
                break;
            case 1:
                incluirMidia();
                break;

            case 2:
                editarMidia();
                break;
            case 3:
                excluirMidia();
                break;
            case 4:
                moverMidia();
                break;
            case 5:
                renomearMidia();
                break;
            case 6:
                listarPorFormato();
                break;
            case 7:
                listarPorCategoria();
                break;
            case 8:
                listarOrdenadoPorTitulo();
                break;
            case 9:
                listarOrdenadoPorDuracao();
                break;
            case 10:
                listarPorFormatoECategoria();
                break;
            case 11:
                exibirCaracteristicas();
                break;
            default:
                areaTexto.append("Opção em desenvolvimento...\n");
        }
        areaTexto.append("----------------------------\n");
    }

    private void sairPrograma() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void incluirMidia() {
        try {
            // === PASSO 1: CRIAR UM FORMULÁRIO PARA COLETAR DADOS ===
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

            // Campos do formulário
            JTextField tituloField = new JTextField();
            JTextField categoriaField = new JTextField();
            JComboBox<String> tipoComboBox = new JComboBox<>(new String[]{"FILME", "MUSICA", "LIVRO"});
            JTextField duracaoField = new JTextField();
            JTextField tamanhoField = new JTextField();

            // Adicionar campos ao painel
            panel.add(new JLabel("Título:*"));
            panel.add(tituloField);
            panel.add(new JLabel("Categoria(Genero):*"));
            panel.add(categoriaField);
            panel.add(new JLabel("Tipo de Mídia:*"));
            panel.add(tipoComboBox);
            panel.add(new JLabel("Duração:"));
            panel.add(duracaoField);
            panel.add(new JLabel("Tamanho (MB):"));
            panel.add(tamanhoField);

            // === PASSO 2: MOSTRAR DIALOG E COLETAR DADOS ===
            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Incluir Nova Mídia",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            // === PASSO 3: SE USUÁRIO CLICOU EM OK ===
            if (result == JOptionPane.OK_OPTION) {
                // Coletar valores dos campos
                String titulo = tituloField.getText().trim();
                String categoria = categoriaField.getText().trim();
                String tipo = (String) tipoComboBox.getSelectedItem();
                String duracaoStr = duracaoField.getText().trim();
                String tamanhoStr = tamanhoField.getText().trim();

                // === PASSO 4: VALIDAÇÕES BÁSICAS NO GUI ===
                if (titulo.isEmpty() || categoria.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Título e categoria são obrigatórios!",
                            "Campos Obrigatórios",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // === PASSO 5: CONVERTER TAMANHO ===
                double tamanho = 0.0;
                if (!tamanhoStr.isEmpty()) {
                    try {
                        tamanho = Double.parseDouble(tamanhoStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                                "Tamanho deve ser um número válido!",
                                "Tamanho Inválido",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // === PASSO 6: CRIAR OBJETO MIDIA CONFORME O TIPO ===
                Midia novaMidia = null;

                switch (tipo) {
                    case "FILME":
                        // ✅ PEDIR IDIOMA
                        String idioma = JOptionPane.showInputDialog(this, "Idioma do filme:");

                        if (idioma == null || idioma.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(this,
                                    "Idioma é obrigatório para filmes!",
                                    "Campo Obrigatório",
                                    JOptionPane.WARNING_MESSAGE);
                            areaTexto.append("❌ Erro: Idioma não pode ser vazio\n");
                            return;
                        }

//                        // ✅ PEDIR CAMINHO DO ARQUIVO
//                        String caminhoFilme = JOptionPane.showInputDialog(this,
//                                "Caminho do arquivo do filme (MP4/MKV):");
//
//                        if (caminhoFilme == null || caminhoFilme.trim().isEmpty()) {
//                            JOptionPane.showMessageDialog(this,
//                                    "Caminho do arquivo é obrigatório!",
//                                    "Campo Obrigatório",
//                                    JOptionPane.WARNING_MESSAGE);
//                            areaTexto.append("❌ Erro: Caminho do arquivo não pode ser vazio\n");
//                            return;
//                        }

                        // ✅ CRIAR FILME COM 5 PARÂMETROS
                        novaMidia = new Filme(
                                titulo,
                                new Categoria(categoria),
                                ArquivosSuportados.MP4,
                                idioma.trim()
                        );

                        // ✅ SETAR TAMANHO MANUALMENTE
                        novaMidia.setTamanho(tamanho);

                        if (!duracaoStr.isEmpty()) {
                            ((Filme) novaMidia).setMinutos(Long.parseLong(duracaoStr));
                        }
                        break;

                    case "MUSICA":
                        String artista = JOptionPane.showInputDialog(this, "Artista da música:");

                        if (artista == null || artista.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(this,
                                    "Artista é obrigatório para músicas!",
                                    "Campo Obrigatório",
                                    JOptionPane.WARNING_MESSAGE);
                            areaTexto.append("❌ Erro: Artista não pode ser vazio\n");
                            return;
                        }

                        // ✅ PEDIR CAMINHO DO ARQUIVO
//                        String caminhoMusica = JOptionPane.showInputDialog(this, "Caminho do arquivo MP3:");
//
//                        if (caminhoMusica == null || caminhoMusica.trim().isEmpty()) {
//                            JOptionPane.showMessageDialog(this,
//                                    "Caminho do arquivo é obrigatório!",
//                                    "Campo Obrigatório",
//                                    JOptionPane.WARNING_MESSAGE);
//                            areaTexto.append("❌ Erro: Caminho do arquivo não pode ser vazio\n");
//                            return;
//                        }

                        // ✅ CORREÇÃO: MUSICA COM 4 PARÂMETROS
                        novaMidia = new Musica(
                                titulo,
                                new Categoria(categoria),
                                ArquivosSuportados.MP3
                                 // ✅ 4 parâmetros
                        );

                        // ✅ SETAR TAMANHO MANUALMENTE
                        novaMidia.setTamanho(tamanho);

                        // ✅ SETAR ARTISTA DEPOIS DO CONSTRUTOR
                        ((Musica) novaMidia).setArtista(new Artista(artista.trim()));

                        if (!duracaoStr.isEmpty()) {
                            ((Musica) novaMidia).setSegundos(Long.parseLong(duracaoStr));
                        }
                        break;

                    case "LIVRO":
                        String autor = JOptionPane.showInputDialog(this, "Autor do livro:");

                        if (autor == null || autor.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(this,
                                    "Autor é obrigatório para livros!",
                                    "Campo Obrigatório",
                                    JOptionPane.WARNING_MESSAGE);
                            areaTexto.append("❌ Erro: Autor não pode ser vazio\n");
                            return;
                        }



                        // ✅ CORREÇÃO: LIVRO COM 4 PARÂMETROS
                        novaMidia = new Livro(
                                titulo,
                                new Categoria(categoria),
                                ArquivosSuportados.PDF
                                  // ✅ 4 parâmetros
                        );

                        // ✅ SETAR TAMANHO MANUALMENTE
                        novaMidia.setTamanho(tamanho);

                        // ✅ O autor já está no construtor do Livro (verificar se precisa setar)
                        ((Livro) novaMidia).setAutor(new Autor(autor.trim()));

                        if (!duracaoStr.isEmpty()) {
                            ((Livro) novaMidia).setPaginas(Long.parseLong(duracaoStr));
                        }
                        break;
                }

                // === PASSO 7: CHAMAR O CONTROLLER ===
                midiaControle.addMidia(novaMidia);

                ServicoArquivoTPOO servico = new ServicoArquivoTPOO();
                servico.salvarMidia(novaMidia);

                // === PASSO 8: FEEDBACK DE SUCESSO ===
                areaTexto.append("✅ Mídia adicionada com sucesso!\n");
                areaTexto.append("   Tipo: " + tipo + "\n");
                areaTexto.append("   Título: " + titulo + "\n");
                areaTexto.append("   Categoria: " + categoria + "\n");
                areaTexto.append("   Tamanho: " + String.format("%.2f", tamanho) + " MB\n");

            } else {
                areaTexto.append("❌ Inclusão cancelada pelo usuário.\n");
            }

        } catch (NomeVazioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Título Inválido", JOptionPane.WARNING_MESSAGE);
            areaTexto.append("❌ Erro ao adicionar: " + e.getMessage() + "\n");

        } catch (MidiaJaExisteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Mídia Duplicada", JOptionPane.WARNING_MESSAGE);
            areaTexto.append("❌ Erro ao adicionar: " + e.getMessage() + "\n");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Duração deve ser número válido!", "Número Inválido", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro: Número inválido no campo duração\n");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro inesperado: " + e.getMessage() + "\n");
            e.printStackTrace(); // Para debug
        }
    }

    private void excluirMidia() {
        try {
            // Obter lista de mídias
            List<Midia> midiasDisponiveis = midiaControle.getMidias();

            if (midiasDisponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há mídias cadastradas.", "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
                areaTexto.append("❌ Não há mídias para excluir.\n");
                return;
            }

            // Criar array com títulos para o JComboBox
            String[] titulosMidias = new String[midiasDisponiveis.size()];
            for (int i = 0; i < midiasDisponiveis.size(); i++) {
                Midia midia = midiasDisponiveis.get(i);
                titulosMidias[i] = midia.getTitulo() + " (" + midia.getClass().getSimpleName() + ")";
            }

            // Usuário seleciona qual excluir
            String midiaSelecionada = (String) JOptionPane.showInputDialog(
                    this, "Selecione a mídia para excluir:", "Excluir Mídia",
                    JOptionPane.QUESTION_MESSAGE, null, titulosMidias, titulosMidias[0]
            );

            if (midiaSelecionada == null) {
                areaTexto.append("❌ Exclusão cancelada pelo usuário.\n");
                return;
            }

            // Extrair título
            String titulo = midiaSelecionada.substring(0, midiaSelecionada.lastIndexOf(" ("));

            // Encontrar a mídia
            Midia midiaParaExcluir = null;
            for (Midia midia : midiasDisponiveis) {
                if (midia.getTitulo().equals(titulo)) {
                    midiaParaExcluir = midia;
                    break;
                }
            }

            if (midiaParaExcluir == null) {
                throw new Exception("Mídia não encontrada: " + titulo);
            }

            // Confirmar exclusão
            int confirmacao = JOptionPane.showConfirmDialog(
                    this, "Tem certeza que deseja excluir:\n\"" + titulo + "\"?",
                    "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );

            if (confirmacao == JOptionPane.YES_OPTION) {
                midiaControle.removerMidia(midiaParaExcluir);
                areaTexto.append("✅ Mídia excluída: " + titulo + "\n");
            } else {
                areaTexto.append("❌ Exclusão cancelada.\n");
            }

        } catch (MidiaNaoEncontradaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Mídia Não Encontrada", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro: " + e.getMessage() + "\n");

        } catch (NomeVazioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nome Inválido", JOptionPane.WARNING_MESSAGE);
            areaTexto.append("❌ Erro: " + e.getMessage() + "\n");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro inesperado: " + e.getMessage() + "\n");
        }
    }

    private void listarPorCategoria() {
        try {
            String categoria = JOptionPane.showInputDialog(this, "Digite a categoria:");

            if (categoria != null && !categoria.trim().isEmpty()) {
                areaTexto.append("Buscando por categoria: " + categoria + "\n");

                List<Midia> resultado = midiaControle.listarPorCategoria(categoria);

                if (resultado.isEmpty()) {
                    areaTexto.append("Nenhuma mídia na categoria '" + categoria + "'\n");
                } else {
                    areaTexto.append("=== MÍDIAS - " + categoria.toUpperCase() + " ===\n");
                    for (Midia midia : resultado) {
                        areaTexto.append("• Título: " + midia.getTitulo() + " | " +
                                "Tipo: " + midia.getClass().getSimpleName() + " | " +
                                "Tamanho: " + String.format("%.2f", midia.tamanhoArquivo()) + " MB\n");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarPorFormato() {
        try {
            String[] formatos = {"FILME", "LIVRO", "MUSICA"};
            String formato = (String) JOptionPane.showInputDialog(
                    this, "Escolha o formato:", "Listar por Formato",
                    JOptionPane.QUESTION_MESSAGE, null, formatos, formatos[0]
            );

            if (formato != null) {
                areaTexto.append("Buscando " + formato + "s...\n");

                List<Midia> resultado = midiaControle.listarPorFormato(formato);

                if (resultado.isEmpty()) {
                    areaTexto.append("Nenhum " + formato.toLowerCase() + " encontrado.\n");
                } else {
                    areaTexto.append("=== " + formato.toUpperCase() + "S ===\n");
                    for (int i = 0; i < resultado.size(); i++) {
                        Midia midia = resultado.get(i);
                        areaTexto.append((i + 1) + ". Título: " + midia.getTitulo() + " | " +
                                "Categoria: " + midia.getCategoria().getTitulo() + " | " +
                                "Tamanho: " + String.format("%.2f", midia.tamanhoArquivo()) + " MB\n");
                    }
                    areaTexto.append("Total: " + resultado.size() + " " + formato.toLowerCase() + "(s)\n");
                }
            } else {
                areaTexto.append("Operação cancelada.\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === MÉTODOS IMPLEMENTADOS ===

    private void editarMidia() {
        try {
            List<Midia> midiasDisponiveis = midiaControle.getMidias();

            if (midiasDisponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há mídias cadastradas.", "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
                areaTexto.append("❌ Não há mídias para editar.\n");
                return;
            }

            // Criar array com títulos para o JComboBox
            String[] titulosMidias = new String[midiasDisponiveis.size()];
            for (int i = 0; i < midiasDisponiveis.size(); i++) {
                Midia midia = midiasDisponiveis.get(i);
                titulosMidias[i] = midia.getTitulo() + " (" + midia.getClass().getSimpleName() + ")";
            }

            // Usuário seleciona qual editar
            String midiaSelecionada = (String) JOptionPane.showInputDialog(
                    this, "Selecione a mídia para editar:", "Editar Mídia",
                    JOptionPane.QUESTION_MESSAGE, null, titulosMidias, titulosMidias[0]
            );

            if (midiaSelecionada == null) {
                areaTexto.append("❌ Edição cancelada pelo usuário.\n");
                return;
            }

            // Extrair título
            String titulo = midiaSelecionada.substring(0, midiaSelecionada.lastIndexOf(" ("));

            // Encontrar a mídia
            Midia midiaParaEditar = null;
            for (Midia midia : midiasDisponiveis) {
                if (midia.getTitulo().equals(titulo)) {
                    midiaParaEditar = midia;
                    break;
                }
            }

            if (midiaParaEditar == null) {
                throw new Exception("Mídia não encontrada: " + titulo);
            }

            // Selecionar atributo para editar
            String[] atributos = {"titulo", "categoria", "duracao", "local"};
            if (midiaParaEditar instanceof Filme) {
                atributos = new String[]{"titulo", "categoria", "duracao", "local", "idioma"};
            } else if (midiaParaEditar instanceof Musica) {
                atributos = new String[]{"titulo", "categoria", "duracao", "local", "artista"};
            } else if (midiaParaEditar instanceof Livro) {
                atributos = new String[]{"titulo", "categoria", "duracao", "local", "autor"};
            }

            String atributoSelecionado = (String) JOptionPane.showInputDialog(
                    this, "Selecione o atributo para editar:", "Editar Atributo",
                    JOptionPane.QUESTION_MESSAGE, null, atributos, atributos[0]
            );

            if (atributoSelecionado == null) {
                areaTexto.append("❌ Edição cancelada.\n");
                return;
            }

            // Pedir novo valor
            String novoValor = JOptionPane.showInputDialog(this,
                    "Novo valor para " + atributoSelecionado + ":",
                    midiaParaEditar.getTitulo());

            if (novoValor != null && !novoValor.trim().isEmpty()) {
                midiaControle.editarMidia(midiaParaEditar, atributoSelecionado, novoValor.trim());
                areaTexto.append("✅ Mídia editada: " + titulo + " - " + atributoSelecionado + " = " + novoValor + "\n");
            } else {
                areaTexto.append("❌ Edição cancelada.\n");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro ao editar: " + e.getMessage() + "\n");
        }
    }

    private void moverMidia() {
        try {
            List<Midia> midiasDisponiveis = midiaControle.getMidias();

            if (midiasDisponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há mídias cadastradas.", "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
                areaTexto.append("❌ Não há mídias para mover.\n");
                return;
            }

            // Criar array com títulos para o JComboBox
            String[] titulosMidias = new String[midiasDisponiveis.size()];
            for (int i = 0; i < midiasDisponiveis.size(); i++) {
                Midia midia = midiasDisponiveis.get(i);
                titulosMidias[i] = midia.getTitulo() + " (" + midia.getClass().getSimpleName() + ")";
            }

            // Usuário seleciona qual mover
            String midiaSelecionada = (String) JOptionPane.showInputDialog(
                    this, "Selecione a mídia para mover:", "Mover Mídia",
                    JOptionPane.QUESTION_MESSAGE, null, titulosMidias, titulosMidias[0]
            );

            if (midiaSelecionada == null) {
                areaTexto.append("❌ Movimentação cancelada pelo usuário.\n");
                return;
            }

            // Extrair título
            String titulo = midiaSelecionada.substring(0, midiaSelecionada.lastIndexOf(" ("));

            // Encontrar a mídia
            Midia midiaParaMover = null;
            for (Midia midia : midiasDisponiveis) {
                if (midia.getTitulo().equals(titulo)) {
                    midiaParaMover = midia;
                    break;
                }
            }

            if (midiaParaMover == null) {
                throw new Exception("Mídia não encontrada: " + titulo);
            }

            // ✅ CHAMADA SIMPLES: O JFileChooser está DENTRO do controller
            midiaControle.moverMidia(midiaParaMover, this);
            areaTexto.append("✅ Mídia movida: " + titulo + "\n");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro ao mover: " + e.getMessage() + "\n");
        }
    }

    private void renomearMidia() {
        try {
            List<Midia> midiasDisponiveis = midiaControle.getMidias();

            if (midiasDisponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há mídias cadastradas.", "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
                areaTexto.append("❌ Não há mídias para renomear.\n");
                return;
            }

            // Criar array com títulos para o JComboBox
            String[] titulosMidias = new String[midiasDisponiveis.size()];
            for (int i = 0; i < midiasDisponiveis.size(); i++) {
                Midia midia = midiasDisponiveis.get(i);
                titulosMidias[i] = midia.getTitulo() + " (" + midia.getClass().getSimpleName() + ")";
            }

            // Usuário seleciona qual renomear
            String midiaSelecionada = (String) JOptionPane.showInputDialog(
                    this, "Selecione a mídia para renomear:", "Renomear Mídia",
                    JOptionPane.QUESTION_MESSAGE, null, titulosMidias, titulosMidias[0]
            );

            if (midiaSelecionada == null) {
                areaTexto.append("❌ Renomeação cancelada pelo usuário.\n");
                return;
            }

            // Extrair título
            String tituloAtual = midiaSelecionada.substring(0, midiaSelecionada.lastIndexOf(" ("));

            // Encontrar a mídia
            Midia midiaParaRenomear = null;
            for (Midia midia : midiasDisponiveis) {
                if (midia.getTitulo().equals(tituloAtual)) {
                    midiaParaRenomear = midia;
                    break;
                }
            }

            if (midiaParaRenomear == null) {
                throw new Exception("Mídia não encontrada: " + tituloAtual);
            }

            // Pedir novo título
            String novoTitulo = JOptionPane.showInputDialog(this,
                    "Novo título para a mídia:",
                    tituloAtual);

            if (novoTitulo != null && !novoTitulo.trim().isEmpty()) {
                midiaControle.renomearMidia(midiaParaRenomear, novoTitulo.trim());
                areaTexto.append("✅ Mídia renomeada: " + tituloAtual + " → " + novoTitulo + "\n");
            } else {
                areaTexto.append("❌ Renomeação cancelada.\n");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro ao renomear: " + e.getMessage() + "\n");
        }
    }

    private void listarOrdenadoPorTitulo() {
        try {
            List<Midia> resultado = midiaControle.listarOrdenadoPorTitulo();
            areaTexto.append("=== MÍDIAS ORDENADAS POR TÍTULO ===\n");
            if (resultado.isEmpty()) {
                areaTexto.append("Nenhuma mídia para ordenar.\n");
            } else {
                for (int i = 0; i < resultado.size(); i++) {
                    Midia midia = resultado.get(i);
                    areaTexto.append((i + 1) + ". " + midia.getTitulo() + " (" +
                            midia.getClass().getSimpleName() + ") - " +
                            "Tamanho: " + String.format("%.2f", midia.tamanhoArquivo()) + " MB\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarOrdenadoPorDuracao() {
        try {
            List<Midia> resultado = midiaControle.listarOrdenadoPorDuracao();
            areaTexto.append("=== MÍDIAS ORDENADAS POR DURAÇÃO ===\n");
            if (resultado.isEmpty()) {
                areaTexto.append("Nenhuma mídia para ordenar.\n");
            } else {
                for (int i = 0; i < resultado.size(); i++) {
                    Midia midia = resultado.get(i);
                    areaTexto.append((i + 1) + ". " + midia.getTitulo() + " - " +
                            midia.duracao() + " unidades - " +
                            "Tamanho: " + String.format("%.2f", midia.tamanhoArquivo()) + " MB\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarPorFormatoECategoria() {
        try {
            String[] formatos = {"FILME", "LIVRO", "MUSICA"};
            String formato = (String) JOptionPane.showInputDialog(
                    this, "Escolha o formato:", "Filtrar por Formato",
                    JOptionPane.QUESTION_MESSAGE, null, formatos, formatos[0]
            );

            if (formato == null) {
                areaTexto.append("❌ Operação cancelada.\n");
                return;
            }

            String categoria = JOptionPane.showInputDialog(this, "Digite a categoria:");

            if (categoria != null && !categoria.trim().isEmpty()) {
                areaTexto.append("Buscando " + formato + "s na categoria: " + categoria + "\n");

                List<Midia> resultado = midiaControle.listarOrdenadoPorFormatoECategoria(formato, categoria);

                if (resultado.isEmpty()) {
                    areaTexto.append("Nenhum " + formato.toLowerCase() + " na categoria '" + categoria + "'\n");
                } else {
                    areaTexto.append("=== " + formato.toUpperCase() + "S - " + categoria.toUpperCase() + " ===\n");
                    for (int i = 0; i < resultado.size(); i++) {
                        Midia midia = resultado.get(i);
                        areaTexto.append((i + 1) + ". " + midia.getTitulo() + " | " +
                                "Duração: " + midia.duracao() + " | " +
                                "Tamanho: " + String.format("%.2f", midia.tamanhoArquivo()) + " MB\n");
                    }
                    areaTexto.append("Total: " + resultado.size() + " " + formato.toLowerCase() + "(s)\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirCaracteristicas() {
        try {
            List<Midia> midiasDisponiveis = midiaControle.getMidias();

            if (midiasDisponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há mídias cadastradas.", "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
                areaTexto.append("❌ Não há mídias para exibir.\n");
                return;
            }

            // Criar array com títulos para o JComboBox
            String[] titulosMidias = new String[midiasDisponiveis.size()];
            for (int i = 0; i < midiasDisponiveis.size(); i++) {
                Midia midia = midiasDisponiveis.get(i);
                titulosMidias[i] = midia.getTitulo() + " (" + midia.getClass().getSimpleName() + ")";
            }

            // Usuário seleciona qual exibir
            String midiaSelecionada = (String) JOptionPane.showInputDialog(
                    this, "Selecione a mídia para exibir características:", "Características",
                    JOptionPane.QUESTION_MESSAGE, null, titulosMidias, titulosMidias[0]
            );

            if (midiaSelecionada == null) {
                areaTexto.append("❌ Operação cancelada pelo usuário.\n");
                return;
            }

            // Extrair título
            String titulo = midiaSelecionada.substring(0, midiaSelecionada.lastIndexOf(" ("));

            // Encontrar a mídia
            Midia midiaSelecionadaObj = null;
            for (Midia midia : midiasDisponiveis) {
                if (midia.getTitulo().equals(titulo)) {
                    midiaSelecionadaObj = midia;
                    break;
                }
            }

            if (midiaSelecionadaObj == null) {
                throw new Exception("Mídia não encontrada: " + titulo);
            }

            // Exibir características
            areaTexto.append("=== CARACTERÍSTICAS DE " + titulo.toUpperCase() + " ===\n");
            areaTexto.append("Tipo: " + midiaSelecionadaObj.getClass().getSimpleName() + "\n");
            areaTexto.append("Título: " + midiaSelecionadaObj.getTitulo() + "\n");
            areaTexto.append("Categoria: " + midiaSelecionadaObj.getCategoria().getTitulo() + "\n");
            areaTexto.append("Local: " + midiaSelecionadaObj.getLocal() + "\n");
            areaTexto.append("Tamanho: " + String.format("%.2f", midiaSelecionadaObj.tamanhoArquivo()) + " MB\n");

            // Características específicas
            if (midiaSelecionadaObj instanceof Filme) {
                Filme filme = (Filme) midiaSelecionadaObj;
                areaTexto.append("Idioma: " + filme.getIdioma() + "\n");
                areaTexto.append("Duração: " + midiaSelecionadaObj.duracao() + " minutos\n");
            } else if (midiaSelecionadaObj instanceof Musica) {
                Musica musica = (Musica) midiaSelecionadaObj;
                areaTexto.append("Artista: " + (musica.getArtista() != null ? musica.getArtista().getNome() : "Não definido") + "\n");
                areaTexto.append("Duração: " + midiaSelecionadaObj.duracao() + "segundos\n");
            } else if (midiaSelecionadaObj instanceof Livro) {
                Livro livro = (Livro) midiaSelecionadaObj;
                areaTexto.append("Duração: " + midiaSelecionadaObj.duracao() + " paginas\n");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaTexto.append("❌ Erro ao exibir características: " + e.getMessage() + "\n");
        }
    }

}
