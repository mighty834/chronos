package exceptions;

public class StrategyReaderInitException extends Exception {
    private String message;

    StrategyReaderInitException() {
        this.message = "Can't found strategy directory";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

