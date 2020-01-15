package exceptions;

public class AimCloseOverdueWithoutPostmortemException extends Exception {
    private String message;

    public AimCloseOverdueWithoutPostmortemException() {
        this.message = "If you close overdue aim, you must set postmortem!";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

