package bridge;
import java.util.ArrayList;
import entities.*;
import exceptions.*;

public abstract class Storage {
    private static ArrayList<DailyPlan> dailyPlans = new ArrayList<>();
    private static ArrayList<WeeklyPlan> weeklyPlans = new ArrayList<>();
    private static ArrayList<Target> targets = new ArrayList<>();
    private static ArrayList<Crunch> crunches = new ArrayList<>();
    private static IAbstractReader reader;
    private static IAbstractWriter writer;

    public static void setReader(String type) {
        switch (type) {
            case "strategy": Storage.reader = new StrategyReader();
            break;
            default:
    
    public static void addPlan(AbstractPlan plan) {
        if (plan instanceof DailyPlan) dailyPlans.set(plan.getOrdinal() - 1, plan);
        if (plan instanceof WeeklyPlan) weeklyPlans.set(plan.getOrdinal() - 1, plan);
    }

    public static void setPlans(ArrayList<AbstractPlan> list)
    throws StorageNotPossibleSetEmptyListException {
        if (list.length > 0) {
            if (list.get(0) instanceof DailyPlan) dailyPlans = list;
            if (list.get(0) instanceof WeeklyPlan) weeklyPlans = list;
        } else {
            throw new StorageNotPossibleSetEmptyListException("plans");
        }
    }

    public static AbstractPlan getPlan(String type, int index)
    throws StorageUnexistingTypeException {
        switch (type) {
            case DailyPlan.getTypeName(): return dailyPlans.get(index);
            break;
            case WeeklyPlan.getTypeName(): return weeklyPlans.get(index);
            break;
            default: throw new StorageUnexistingTypeException(type);
        }
    }

    public static ArrayList<AbstractPlan> getAllPlans(String type)
    throws StorageUnexistingTypeException {
        switch (type) {
            case DailyPlan.getTypeName(): return dailyPlans;
            break;
            case WeeklyPlan.getTypeName(): return weeklyPlans;
            break;
            default: throw new StorageUnexistingTypeException(type);
        }
    }

    public static void addAim(AbstractAim aim) {
       if (aim instanceof Target) targets.set(aim.getOrdinal() - 1, aim);
       if (aim instanceof Crunch) crunches.set(aim.getOrdinal() - 1, aim);
    }

    public static void setAims(ArrayList<AbstractAim> list) {
        if (list.length > 0) {
            if (list.get(0) instanceof Target) targets = list;
            if (list.get(0) instanceof Crunch) crunches = list;
        } else {
            throw new StorageNotPossibleSetEmptyListException("aims");
        }
    }

    public static AbstractAim getAim(String type, int index)
    throws StorageUnexistingTypeException {
       switch (type) {
           case Target.getTypeName(): return targets.get(index);
           break;
           case Crunch.getTypeName(): return crunches.get(index);
           break;
           default: throw new StorageUnexistingTypeException(type);
       }
    }

    public static ArrayList<AbstractAim> getAllAims(String type)
    throws StorageUnexistingTypeException {
        switch (type) {
            case Target.getTypeName(): return targets;
            break;
            case Crunch.getTypeName(): return crunches;
            break;
            default: throw new StorageUnexistingTypeException(type);
        }
    }

    public static void clear(String ... types) throws StorageUnexistingTypeException {
        if (types.length == 0) {
            dailyPlans = new ArrayList<>();
            weeklyPlans = new ArrayList<>();
            targets = new ArrayList<>();
            crunches = new ArrayList<>();
        } else {
            for (String type: types) {
                switch (type) {
                    case DailyPlan.getTypeName(): dailyPlans = new ArrayList<>();
                    break;
                    case WeeklyPlan.getTypeName(): weeklyPlans = new ArrayList<>();
                    break;
                    case Target.getTypeName(): targets = new ArrayList<>();
                    break;
                    case Crunch.getTypeName(): crunches = new ArrayList<>();
                    break;
                    default: throw new StorageUnexistingTypeException(type);
                }
            }
        }
    }
}

