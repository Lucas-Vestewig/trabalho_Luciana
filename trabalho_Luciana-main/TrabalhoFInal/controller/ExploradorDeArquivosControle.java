package TrabalhoFInal.controller;

import TrabalhoFInal.services.ServicoArquivoTPOO;

import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ExploradorDeArquivosControle {

    private JFileChooser fileChooser;
    private ServicoArquivoTPOO servicoTPOO;

    public ExploradorDeArquivosControle(ServicoArquivoTPOO servicoTPOO) {
        this.servicoTPOO = servicoTPOO;

        this.fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o arquivo de Mídia");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Documents"));
    }


}
