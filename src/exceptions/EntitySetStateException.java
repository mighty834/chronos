package exceptions;

public class EntitySetStateException extends Exception {
    private String message;

    EntitySetStateException(String entityType) {
        this.message = "Only readers can directly set state of entities!\n" +
                       "Problem in entity: " + entityType;
    }

    @Override
    public Srting toString() {
        return this.message;
    }
}

