package entities;
import java.util.*;
import exceptions.*;

public class Crunch extends AbstractAim {
    private ArrayList<AbstractAim> allTargets;

    public Crunch(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
    }

    public static final String AIM_TYPE = "crunch";

    public String getAimType() {
        return AIM_TYPE;
    }

    @Override
    public void freeze() {
        System.out.println("You can't freeze crunch entity!");
    }
}

