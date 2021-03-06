package exceptions;

public class OrdinalAlreadyExistException extends MainException {
    private String message;

    public OrdinalAlreadyExistException(String type, int ordinal) {
        this.message = "Entity with type: " + type +
                       " and with ordinal: " + ordinal +
                       " alredy exist!\n Please, use another ordinal";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

