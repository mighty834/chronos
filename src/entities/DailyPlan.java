package entities;
import java.util.*;
import java.text.*;
import exceptions.*;
import bridge.Storage;

public class DailyPlan extends AbstractPlan {
    public static final int maxPossibleTime = 12;
    public static final String PLAN_TYPE = "daily";

    DailyPlan(int ordinal, int futureDay) throws OrdinalAlreadyExistException {
        this.date = new Date(new Date().getTime() + (futureDay * 86400000));
        this.ordinal = ordinal;
        this.status = false;
        this.pushToStorage();
    }

    public String getPlanType() {
        return PLAN_TYPE;
    }

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

