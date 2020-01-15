package exceptions;

public class StorageNotPossibleSetEmptyListException extends Exception {
    private String message;

    public StorageNotPossibleSetEmptyListException(String type) {
        this.message = "Not possible set empty list of " + type;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

