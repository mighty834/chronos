package exceptions;

public class AimRejectNotHavePostmortem extends Exception {
    private String message;

    AimRejectNotHavePostmortem() {
        this.message = "Rejecting aim without DRAFT status must have postmortem";
    }

    @Override
    public String toString() {
        return this.message;
    }
}
