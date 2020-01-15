package exceptions;

public class StorageUnexistingTypeException extends Exception {
    private String message;

    public StorageUnexistingTypeException(String type) {
        this.message = "Unreal get entity from storage with requested type\n" +
                       "Requested type: " + type;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

