package TrabalhoFInal.services;

import TrabalhoFInal.exceptions.ArquivoNaoPodeSerNuloException;

public interface SerializacaoTPOO {

    /**
     * Converte o objeto para o formato .tpoo (salvar)
     * Retorna uma string no formato:
     * chave1:valor1
     * chave2:valor2
     */
    String toTPOO() throws ArquivoNaoPodeSerNuloException;

}