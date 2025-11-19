package TrabalhoFInal.exceptions;

public class LocalVazioException extends MidiaException{
    public LocalVazioException(String message) {
        super(message + " do arquivo não pode ser nulo");
    }
}
