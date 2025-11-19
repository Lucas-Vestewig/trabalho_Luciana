package TrabalhoFInal.exceptions;

public class MidiaJaExisteException extends RuntimeException {
    public MidiaJaExisteException(String mensagem) {
        super("Midia ja existe: " + mensagem);
    }
    
}
