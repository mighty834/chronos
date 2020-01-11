package exceptions;

public class EntitiesReaderTakeEntityException extends Exception {
    private String message;

    EntitiesReaderTakeEntityException(String name) {
        this.message = "Impossible name for entity: " + name;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

