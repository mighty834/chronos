package bridge;
import java.util.ArrayList;
import entities.*;
import exceptions.*;

public abstract class Storage {
    private static ArrayList<AbstractPlan> dailyPlans = new ArrayList<>();
    private static ArrayList<AbstractPlan> weeklyPlans = new ArrayList<>();
    private static ArrayList<AbstractAim> targets = new ArrayList<>();
    private static ArrayList<AbstractAim> crunches = new ArrayList<>();
    private static IAbstractReader reader;
    private static IAbstractWriter writer;

    public static void setReader(String type)
    throws StorageWrongReaderOrWriterTypeException, StrategyReaderInitException {
        switch (type) {
            case StrategyReader.READER_TYPE: Storage.reader = new StrategyReader();
            break;
            default: throw new StorageWrongReaderOrWriterTypeException(type);
        }
    }

    public static void setWriter(String type) throws StorageWrongReaderOrWriterTypeException {
        switch (type) {
            case StrategyWriter.WRITER_TYPE: Storage.writer = new StrategyWriter();
            break;
            default: throw new StorageWrongReaderOrWriterTypeException(type);
        }
    }
    
    public static void addPlan(AbstractPlan plan) {
        if (plan instanceof DailyPlan) dailyPlans.set(plan.getOrdinal() - 1, plan);
        if (plan instanceof WeeklyPlan) weeklyPlans.set(plan.getOrdinal() - 1, plan);
    }

    public static void setPlans(ArrayList<AbstractPlan> list)
    throws StorageNotPossibleSetEmptyListException {
        if (list.size() > 0) {
            if (list.get(0) instanceof DailyPlan) dailyPlans = list;
            if (list.get(0) instanceof WeeklyPlan) weeklyPlans = list;
        } else {
            throw new StorageNotPossibleSetEmptyListException("plans");
        }
    }

    public static AbstractPlan getPlan(String type, int index)
    throws StorageUnexistingTypeException {
        AbstractPlan result;
        switch (type) {
            case DailyPlan.PLAN_TYPE: result = dailyPlans.get(index);
            break;
            case WeeklyPlan.PLAN_TYPE: result = weeklyPlans.get(index);
            break;
            default: throw new StorageUnexistingTypeException(type);
        }

        return result;
    }

    public static ArrayList<AbstractPlan> getAllPlans(String type)
    throws StorageUnexistingTypeException {
        ArrayList<AbstractPlan> result;
        switch (type) {
            case DailyPlan.PLAN_TYPE: result = dailyPlans;
            break;
            case WeeklyPlan.PLAN_TYPE: result = weeklyPlans;
            break;
            default: throw new StorageUnexistingTypeException(type);
        }

        return result;
    }

    public static void addAim(AbstractAim aim) {
       if (aim instanceof Target) targets.set(aim.getOrdinal() - 1, aim);
       if (aim instanceof Crunch) crunches.set(aim.getOrdinal() - 1, aim);
    }

    public static void setAims(ArrayList<AbstractAim> list)
    throws StorageNotPossibleSetEmptyListException {
        if (list.size() > 0) {
            if (list.get(0) instanceof Target) targets = list;
            if (list.get(0) instanceof Crunch) crunches = list;
        } else {
            throw new StorageNotPossibleSetEmptyListException("aims");
        }
    }

    public static AbstractAim getAim(String type, int index)
    throws StorageUnexistingTypeException {
        AbstractAim result;
        switch (type) {
            case Target.AIM_TYPE: result = targets.get(index);
            break;
            case Crunch.AIM_TYPE: result = crunches.get(index);
            break;
            default: throw new StorageUnexistingTypeException(type);
        }

        return result;
    }

    public static ArrayList<AbstractAim> getAllAims(String type)
    throws StorageUnexistingTypeException {
        ArrayList<AbstractAim> result;
        switch (type) {
            case Target.AIM_TYPE: result = targets;
            break;
            case Crunch.AIM_TYPE: result = crunches;
            break;
            default: throw new StorageUnexistingTypeException(type);
        }

        return result;
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
                    case DailyPlan.PLAN_TYPE: dailyPlans = new ArrayList<>();
                    break;
                    case WeeklyPlan.PLAN_TYPE: weeklyPlans = new ArrayList<>();
                    break;
                    case Target.AIM_TYPE: targets = new ArrayList<>();
                    break;
                    case Crunch.AIM_TYPE: crunches = new ArrayList<>();
                    break;
                    default: throw new StorageUnexistingTypeException(type);
                }
            }
        }
    }
}

