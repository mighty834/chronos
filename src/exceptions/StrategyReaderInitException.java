package exceptions;

public class StrategyReaderInitException extends MainException {
    private String message;

    public StrategyReaderInitException() {
        this.message = "Can't found strategy directory";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

