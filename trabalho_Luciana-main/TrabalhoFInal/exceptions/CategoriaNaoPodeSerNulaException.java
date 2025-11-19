package TrabalhoFInal.exceptions;

public class CategoriaNaoPodeSerNulaException extends MidiaException {
    public CategoriaNaoPodeSerNulaException(String message) {
        super(message + " não pode ser vazia");
    }
}
