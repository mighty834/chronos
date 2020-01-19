package exceptions;

public class AimRejectNotHavePostmortem extends MainException {
    private String message;

    public AimRejectNotHavePostmortem() {
        this.message = "Rejecting aim without DRAFT status must have postmortem";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

