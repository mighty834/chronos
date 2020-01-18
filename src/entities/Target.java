package entities;
import exceptions.*;
    
public class Target extends AbstractAim {
    public Target(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
    }

    public static final String AIM_TYPE = "target";

    public String getAimType() {
        return AIM_TYPE;
    }
}

