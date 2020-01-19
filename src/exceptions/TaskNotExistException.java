package exceptions;

public class TaskNotExistException extends MainException {
    private String message;

    public TaskNotExistException(int index) {
        this.message = "Task with index: " + index + ", not exist in this plan!";
    }

    public TaskNotExistException(String thesesPart) {
        this.message = "Task with part of text:\n" +
                       "'" + thesesPart + "'" +
                       " not exist in this plan!";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

