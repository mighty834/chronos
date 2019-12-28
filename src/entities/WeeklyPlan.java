package entities;
import exceptions.*;

class WeeklyPlan extends AbstractPlan {
    private static final int maxPossibleTime = 84;

    WeeklyPlan(int futureWeek) {
        this.date = new Date(new Date().getTime() + (futureWeek * 7 * 86400000));
    }

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

