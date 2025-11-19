package TrabalhoFInal.services;

import java.util.List;

public interface SalvarELerArquivos {
    //aqui tenho que ver como implementar este método ainda

    //salvar arquivo csv
    void salvarArquivos(String caminho, List<String> linhas);

    List<String> lerArquivos(String caminho);

    //ler arquivo csv
}
