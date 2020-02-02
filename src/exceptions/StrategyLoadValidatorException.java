package exceptions;

public class StrategyLoadValidatorException extends MainException {
    private String message;

    public StrategyLoadValidatorException(String type) {
        this.message = "Problem with strategy entities when them loading!\n" +
                       "Type of problem is: " + type;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

