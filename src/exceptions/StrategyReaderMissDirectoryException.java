package exceptions;

public class StrategyReaderMissDirectoryException extends Exception {
    private String message;

    StrategyReaderMissDirectoryException(String directoryName) {
        this.message = "Can't found required directory: " + directoryName;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

