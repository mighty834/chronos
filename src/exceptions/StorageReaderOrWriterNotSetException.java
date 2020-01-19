package exceptions;

public class StorageReaderOrWriterNotSetException extends MainException {
    private String message;

    public StorageReaderOrWriterNotSetException(String entity) {
        this.message = "You must set " + entity + " before use pull or push methods";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

