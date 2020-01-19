package exceptions;

public class StorageWrongReaderOrWriterTypeException extends MainException {
    private String message;

    public StorageWrongReaderOrWriterTypeException(String type) {
        this.message = "You can't set reader or writer with this type: " + type +
                       "\n Possible types is only: 'strategy'";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

