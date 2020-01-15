package bridge;
import exceptions.*;

public interface IAbstractReader {
    public void loadEntities() throws EntitiesReaderTakeEntityException;
}

