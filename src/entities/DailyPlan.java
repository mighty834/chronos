package entities;
import java.util.*;
import java.text.*;
import exceptions.*;
import bridge.Storage;

public class DailyPlan extends AbstractPlan {
    public static final int maxPossibleTime = 12;
    public static final String PLAN_TYPE = "daily";

    public DailyPlan(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
    }

    public DailyPlan(int ordinal, Date date) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal, date);
    }

    public DailyPlan(int ordinal, String date) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal, date);
    }

    public DailyPlan(int ordinal, int futureDay) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        this.date = new Date(new Date().getTime() + (futureDay * 86400000));
        this.ordinal = ordinal;
        this.status = false;
        this.tasks = new ArrayList<>();
        this.pushToStorage();
    }

    public String getPlanType() {
        return PLAN_TYPE;
    }

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

