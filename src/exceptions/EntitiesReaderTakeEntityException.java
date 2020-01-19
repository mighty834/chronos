package exceptions;

public class EntitiesReaderTakeEntityException extends MainException {
    private String message;

    public EntitiesReaderTakeEntityException(String name) {
        this.message = "Impossible name for entity: " + name;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

