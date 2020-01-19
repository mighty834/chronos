package exceptions;

public class AimCloseOverdueWithoutPostmortemException extends MainException {
    private String message;

    public AimCloseOverdueWithoutPostmortemException() {
        this.message = "If you close overdue aim, you must set postmortem!";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

