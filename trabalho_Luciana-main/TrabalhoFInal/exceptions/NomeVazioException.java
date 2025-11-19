package TrabalhoFInal.exceptions;

public class NomeVazioException extends MidiaException {
    public NomeVazioException(String message) {
        super(message + " não pode ser vazio");
    }
}
