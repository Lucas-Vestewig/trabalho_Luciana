package TrabalhoFInal.exceptions;

public class MidiaNaoEncontradaException extends MidiaException {
    public MidiaNaoEncontradaException(String mensagem) {
        super("Midia nao encontrada: " + mensagem);
    }
    
}
