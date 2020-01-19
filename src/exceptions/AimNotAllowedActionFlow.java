package exceptions;
import java.util.ArrayList;

public class AimNotAllowedActionFlow extends MainException {
    private String message;

    public AimNotAllowedActionFlow(String status, ArrayList<String> allowed) {
        this.message = "Aim with status " + status + " not allowed for this action flow!\n" +
                       "Possible statuses for this action is";

        for (String allow: allowed) {
            this.message = ", " + allow;
        }
    }

    @Override
    public String toString() {
        return this.message;
    }
}

