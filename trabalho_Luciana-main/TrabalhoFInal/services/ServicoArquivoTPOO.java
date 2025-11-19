package TrabalhoFInal.services;

import TrabalhoFInal.exceptions.MidiaNaoEncontradaException;
import TrabalhoFInal.models.midias.Midia;

import javax.swing.*;
import java.io.*;

public class ServicoArquivoTPOO {

    private File diretorioDestino;

    public ServicoArquivoTPOO() {

        // Apenas define um diretório padrão
        diretorioDestino = new File("arquivos_tpoo");
        diretorioDestino.mkdirs();
    }

    public void salvarMidia(Midia midia) {
        if (midia == null) {
            throw new MidiaNaoEncontradaException("Mídia não pode ser nula!");
        }

        // ⭐ SE AINDA NÃO ESCOLHEU DIRETÓRIO, PERGUNTA AGORA
        if (diretorioDestino == null || diretorioDestino.getName().equals("arquivos_tpoo")) {
            escolherDiretorio();
        }

        String nomeArquivo = midia.getTitulo().replaceAll("[^a-zA-Z0-9-_ ]", "") + ".tpoo";
        File arquivoTPOO = new File(diretorioDestino, nomeArquivo);

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoTPOO))) {
            writer.write(midia.toTPOO());
            System.out.println("✅ Arquivo .tpoo salvo em: " + arquivoTPOO.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar arquivo TPOO: " + e.getMessage());
        }
    }

    // ⭐ MÉTODO SEPARADO PARA ESCOLHER DIRETÓRIO (só quando necessário)
    private void escolherDiretorio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Escolha o diretório onde os arquivos .tpoo serão salvos");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int resultado = chooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            diretorioDestino = chooser.getSelectedFile();
            System.out.println("📁 Diretório escolhido: " + diretorioDestino.getAbsolutePath());
        } else {
            // Mantém o padrão se usuário cancelar
            diretorioDestino = new File("arquivos_tpoo");
            System.out.println("⚠ Usando pasta padrão 'arquivos_tpoo'.");
        }
        diretorioDestino.mkdirs();
    }

    public void removerArquivoTPOO(Midia midia) {
        // ... método permanece igual
    }
}