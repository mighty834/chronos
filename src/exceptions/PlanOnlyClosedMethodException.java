package exceptions;

public class PlanOnlyClosedMethodException extends MainException {
    private String message;

    public PlanOnlyClosedMethodException() {
        this.message = "You can't use this method for not closed plans";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

