package TrabalhoFInal.exceptions;

public class MenorIgualAZeroException extends MidiaException {
    public MenorIgualAZeroException(String message) {
        super(message + " deve ser maior que zero!");
    }
}
