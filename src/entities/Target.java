package entities;
import exceptions.*;
import java.util.ArrayList;
    
public class Target extends AbstractAim {
    public Target(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
    }

    public static final String AIM_TYPE = "target";

    public String getAimType() {
        return AIM_TYPE;
    }

    public void takeAffectedEntities() {}
    public void addAffectedEntity(AbstractAim aim) {}
    public ArrayList<AbstractAim> getAffectedEntities() { return null; }
    public void clearAffectedEntities() {} 
}

