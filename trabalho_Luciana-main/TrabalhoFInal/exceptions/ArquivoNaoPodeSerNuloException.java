package TrabalhoFInal.exceptions;


public class ArquivoNaoPodeSerNuloException extends MidiaException {
    public ArquivoNaoPodeSerNuloException(String message) {
        super(message + " não pode ser nulo");
    }
}
