package bridge;
import exceptions.*;

interface IAbstractReader {
    public void loadEntities() throws EntitiesReaderTakeEntityException;
}

