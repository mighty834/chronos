package exceptions;
import java.util.ArrayList;

public class AimCloseUndoneWithoutPostmortemException extends Exception {
    private String message;

    public AimCloseUndoneWithoutPostmortemException(ArrayList<String> undone) {
        this.message = "If you have at least one undone dodPoint, you must have postmortem too!\n" +
                       "List of your undone";
        for (String undonePoint: undone) {
            this.message += ", " = undonePoint;
        }
    }

    @Override
    public String toString() {
        return this.message;
    }
}

