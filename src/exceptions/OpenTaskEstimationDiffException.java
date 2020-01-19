package exceptions;

public class OpenTaskEstimationDiffException extends MainException {
    private String message;

    public OpenTaskEstimationDiffException() {
        this.message = "Sorry, but you can't get estimation diff from open task!\n" +
                       "Please, first close this task.";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

