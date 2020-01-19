package exceptions;

public class AimPostmortemWithoutCauseException extends MainException {
    private String message;

    public AimPostmortemWithoutCauseException() {
        this.message = "Postmortem must have least one cause!";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

