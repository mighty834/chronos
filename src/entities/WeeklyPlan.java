package entities;
import java.util.*;
import exceptions.*;
import bridge.Storage;

public class WeeklyPlan extends AbstractPlan {
    public static final int maxPossibleTime = 84;
    public static final String PLAN_TYPE = "weekly";

    public WeeklyPlan(int ordinal) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal);
    }

    public WeeklyPlan(int ordinal, Date date) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal, date);
    }

    public WeeklyPlan(int ordinal, String date) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        super(ordinal, date);
    }

    public WeeklyPlan(int ordinal, int futureWeek) throws OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        this.date = new Date(new Date().getTime() + (futureWeek * 7 * 86400000));
        this.ordinal = ordinal;
        this.status = false;
        this.pushToStorage();
    }

    public String getPlanType() {
        return PLAN_TYPE;
    }

    public int getWeekNumber() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.date);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

