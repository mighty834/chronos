package exceptions;
import java.util.ArrayList;

public class AimStartLostPropertiesException extends Exception {
    private String message;

    public AimStartLostPropertiesException(ArrayList<String> properties) {
        this.message = "You can't start Aim like entity without these properties";
        for (String property: properties) {
            this.message += ", " + property;
        }
    }

    @Override
    public String toString() {
        return this.message;
    }
}

