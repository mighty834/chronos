package exceptions;

public class StrategyReaderMissDirectoryException extends MainException {
    private String message;

    public StrategyReaderMissDirectoryException(String directoryName) {
        this.message = "Can't found required directory: " + directoryName;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

