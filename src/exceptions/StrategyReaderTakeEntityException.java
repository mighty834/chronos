package exceptions;

public class StrategyReaderTakeEntityException extends Exception {
    private String message;

    StrategyReaderTakeEntityException(String name) {
        this.message = "Impossible name for entity: " + name;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

