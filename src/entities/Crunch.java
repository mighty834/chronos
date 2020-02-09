package entities;
import java.util.*;
import exceptions.*;
import bridge.Storage;

public class Crunch extends AbstractAim {
    private ArrayList<AbstractAim> frozenTargets;
    public static final String AIM_TYPE = "crunch";
    
    public Crunch(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
        this.frozenTargets = null;
    }

    public void addAffectedEntity(AbstractAim target) {
        if (this.frozenTargets == null) this.frozenTargets = new ArrayList<>();
        this.frozenTargets.add(target);
    }

    public ArrayList<AbstractAim> getAffectedEntities() {
        return this.frozenTargets;
    }

    public void clearAffectedEntities() {
        this.frozenTargets = null;
        this.affectedEntitiesOrdinals = null;
    }

    public void takeAffectedEntities() throws StorageUnexistingTypeException {
        if (this.affectedEntitiesOrdinals != null) {
            for (int ordinal: this.affectedEntitiesOrdinals) {
                this.addAffectedEntity(Storage.getAim(Target.AIM_TYPE, ordinal - 1));
            }
        }
    }

    public String getAimType() {
        return AIM_TYPE;
    }
}

