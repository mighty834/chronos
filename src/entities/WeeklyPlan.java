package entities;
import exceptions.*;

class WeeklyPlan extends AbstractPlan {
    private static final int maxPossibleTime = 84;

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

