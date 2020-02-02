package entities;
import java.util.*;
import exceptions.*;

public class Crunch extends AbstractAim {
    private ArrayList<AbstractAim> frozenTargets;
    public static final String AIM_TYPE = "crunch";
    
    public Crunch(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
        this.frozenTargets = null;
    }

    public void addFrozenTarget(AbstractAim target) {
        if (this.frozenTargets == null) this.frozenTargets = new ArrayList<>();
        this.frozenTargets.add(target);
    }

    public ArrayList<AbstractAim> getFrozenTargets() {
        return this.frozenTargets;
    }

    public void clearFrozenTargets() {
        this.frozenTargets = null;
    }

    public void takeAffectedEntities() {
        for (int ordinal: this.affectedEntitiesOrdinals) {
            this.addFrozenTarget(Storage.getAim(Target.AIM_TYPE, ordinal));
        }
    }

    public String getAimType() {
        return AIM_TYPE;
    }
}

