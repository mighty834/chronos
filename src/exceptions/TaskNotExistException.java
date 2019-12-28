package exceptions;

public class TaskNotExistException extends Exception {
    private String message;

    TaskNotExistException(int index) {
        this.message = "Task with index: " + index + ", not exist in this plan!";
    }

    TaskNotExistException(String thesesPart) {
        this.message = "Task with part of text:\n" +
                       "'" + thesesPart + "'" +
                       " not exist in this plan!";
    }

    @Override
    public String toString() {
        return this.message;
    }
}

