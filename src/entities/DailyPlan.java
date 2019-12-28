package entities;
import exceptions.*;

class DailyPlan extends AbstractPlan {
    private static final int maxPossibleTime = 12;

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

