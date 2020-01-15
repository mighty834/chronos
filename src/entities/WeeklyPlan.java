package entities;
import java.util.*;
import exceptions.*;
import bridge.Storage;

public class WeeklyPlan extends AbstractPlan {
    public static final int maxPossibleTime = 84;
    public static final String PLAN_TYPE = "weekly";

    WeeklyPlan(int ordinal, int futureWeek) throws OrdinalAlreadyExistException {
        this.date = new Date(new Date().getTime() + (futureWeek * 7 * 86400000));
        this.ordinal = ordinal;
        this.status = false;
        this.pushToStorage();
    }

    public String getPlanType() {
        return PLAN_TYPE;
    }

    public int getWeekNumber() {
        Calendar calendar = new Calendar.getInstance();
        calendar.setTime(this.date);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

