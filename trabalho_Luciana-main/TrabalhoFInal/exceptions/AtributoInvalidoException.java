package TrabalhoFInal.exceptions;

public class AtributoInvalidoException extends MidiaException {
    public AtributoInvalidoException(String message) {
        super("Atributo inválido: " + message);
    }
}
