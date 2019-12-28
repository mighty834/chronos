package exceptions.*;

public class OpanTaskEstimationDiffException extends Exception {
    private String message;

    OpenTaskEstimationDiffException() {
        this.message = "Sorry, but you can't get estimation diff from open task!\n" +
                       "Please, first close this task.";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

