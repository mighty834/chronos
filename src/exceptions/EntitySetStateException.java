package exceptions;

public class EntitySetStateException extends Exception {
    private String message;

    public EntitySetStateException(String entityType) {
        this.message = "Only readers can directly set state of entities!\n" +
                       "Problem in entity: " + entityType;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

