package TrabalhoFInal.services;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServicoArquivo implements SalvarELerArquivos{
    @Override
    public void salvarArquivos(String caminho, List<String> linhas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) {//aqui abre o arquivo pelo caminho e coeça ler ele
            for (String linha : linhas) {
                bw.write(linha);//escreve a inha
                bw.newLine();//pula para a proxima linha ser escrita
            }
        }catch (Exception e){
            System.out.println("Erro ao salvar arquivos de arquivo"+e.getMessage());//implementar o swing aqui!!!

        }

    }

    @Override
    public List<String> lerArquivos(String caminho) {

        List<String> linhas = new ArrayList<>();
        Path p = resolver(caminho);
        try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {//abre o arquivo par ser lido( new BufferedReader é pra ler em blocos maiores e mais rapido assim)
            String linha;
            while ((linha = br.readLine()) != null){
                linhas.add(linha);
            }
        } catch (FileNotFoundException e) { //caso arquivo não seja encontrado e fazer swig
            throw new RuntimeException(e);
        } catch (IOException e) {// se der erro de ler arquivo, fazer swing
            throw new RuntimeException(e);
        }
        return linhas;
    }
    private Path resolver(String caminho) {
        return Paths.get(System.getProperty("user.dir"), caminho).toAbsolutePath();
    }


}
