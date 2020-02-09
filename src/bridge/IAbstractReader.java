package bridge;
import exceptions.*;
import java.text.ParseException;

public interface IAbstractReader {
    public void loadEntities() throws EntitiesReaderTakeEntityException,
    EntitySetStateException, AimPostmortemWithoutCauseException, ParseException,
    OrdinalAlreadyExistException, StorageUnexistingTypeException,
    StrategyLoadValidatorException, AimStartLostPropertiesException,
    AimNotAllowedActionFlow;

    public String getReaderType();
}

