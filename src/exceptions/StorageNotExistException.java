package exceptions;

public class StorageNotExistException extends Exception {
    private String message;

    StorageNotExistException() {
        this.message = "Storage directory not found!\n" +
                       "Please create 'storage' directory in root path";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

