package bridge;
import java.io.*;
import java.util.*;
import entities.*;
import exceptions.*;

class StrategyWriter implements IAbstractWriter {
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String WRITER_TYPE = "strategy";
    public static final String DONE_PATH = "done/";
    public static final String FROZEN_PATH = "frozen/";
    public static final String REJECTED_PATH = "rejected/";
    public static final String PATH = "./strategy/";
    private File strategy;

    StrategyWriter() {
        this.strategy = new File(PATH);
        if (!this.strategy.isDirectory()) {
            this.strategy.createDirectory(PATH);
            new File(PATH + DONE_PATH).createDirectory(PATH + DONE_PATH);
            new File(PATH + FROZEN_PATH).createDirectory(PATH + FROZEN_PATH);
            new File(PATH + REJECTED_PATH).createDirectory(PATH + REJECTED_PATH);
        } else {
            if (!new File(PATH + DONE_PATH).isDirectory()) {
                new File(PATH + DONE_PATH).createDirectory(PATH + DONE_PATH);
            }
            if (!new File(PATH + FROZEN_PATH).isDirectory()) {
                new File(PATH + FROZEN_PATH).createDirectory(PATH + FROZEN_PATH);
            }
            if (!new File(PATH + REJECTED_PATH).isDirectory()) {
                new File(PATH + REJECTED_PATH).createDirectory(PATH + REJECTED_PATH);
            }
        }
    }

    private String renderAim(AbstractAim aim) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String result = "# DoD\n\n";

        for (AbstractAim.DodPoint dodPoint: aim.getDod()) {
            result += (dodPoint.isDone()) ? "- [x] " : "- [ ] ";
            result += dodPoint.getTheses() + "\n";
        }

        result += "\n# Deadline\n\n";
        result += format.format(aim.getDeadline()) + "\n\n";

        if (aim.getDescription() != null) result += aim.getDescription() + "\n\n";

        result += "# History\n\n";

        for (AbstractAim.HistoryPoint historyPoint: aim.getHistory()) {
            result += "* " + historyPoint.getStatus() + " " + format.format(historyPoint.getDate());
        }

        if (aim.getPostmortem() != null) {
            AbstractAim.Postmortem postmortem = aim.getPostmortem();
            result += "\n\n# Postmortem " + format.format(postmortem.getDate()) + "\n\n";
            result += postmortem.getConclusion();

            for (String cause: postmortem.getCauses()) {
                result += "* " + cause + "\n";
            }
        }

        return result;
    }

    private String renderPlan(AbstractPlan plan) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String result = "# " + format.format(plan.getDate()) + "\n";
        if (plan.isClosed()) result += "closed ";
        if (plan.isRetry()) result += "retry ";

        result += "\n\n";

        ArrayList<String> descriptions = new ArrayList<>(); 
        for (AbstractPlan.Task task: plan.getTasks()) {
            result += (task.isDone()) ? "- [x] " : "- [ ] ";
            if (task.getDescription() != null) {
                for (int i = 0; i <= descriptions.size(); i++) result += "*";
                destinations.add(task.getDescription());
            }
            result += task.getTheses() + " ";

            String types = ""
            if (plan.isWork()) types += "work";
            if (plan.isFun()) types += (types.length() > 0) ? ",fun" : "fun";
            if (plan.isRoutine()) types += (types.length() > 0) ? ",routine" : "routine";
            if (plan.isGrowth()) types += (types.length() > 0) ? ",growth" : "growth";

            if (types.length() > 0) result += "{" + types + "}";

            result += task.getEstimateVolume() + "h";

            if (task.getRealVolume() != 0) result += " " + task.getRealVolume() + "h";

            result += "\n";
        }

        if (plan.getSummary() != null) {
            result += "\n\n# Summary\n\n";
            result += plan.getSummary();
        }

        return result;
    }

    private void createDaily(AbstractPlan plan) {
        String destination = (plan.isDone()) ? PATH + DONE_PATH : PATH;
        String name = "daily_" + plan.getOrdinal();
        String inner = this.renderPlan(plan);

        File daily = new File(destination + name);
        if (!daily.exists(destination + name)) daily.createNewFile();
            
        try (
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(daily)    
            )
        ) {
            writer.write(inner);
        }
        catch (IOException exception) {
            System.out.println("Problem with writer, exception is: " + exception);
        }
    }

    private void createWeekly(AbstractPlan plan) {
        String destination = (plan.isDone()) ? PATH + DONE_PATH : PATH;
        String name = "weekly_" + plan.getOrdinal();
        String inner = this.renderPlan(plan);

        File weekly = new File(destination + name);
        if (!weekly.exists(destination + name)) weekly.createNewFile();

        try (
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(weekly)
            )
        ) {
            writer.write(inner);
        }
        catch (IOException exception) {
            System.out.println("Problem with writer, exception is: " + exception);
        }
    }

    private void createTarget(AbstractAim aim) {
        String destination;
        switch (aim.getStatus()) {
            case "DRAFT":
            case "START":
            case "UNFREEZE":
            case "MODIFY": destination = PATH;
            break;
            case "CLOSE": destination = PATH + DONE_PATH;
            break;
            case "FREEZE": destination = PATH + FROZEN_PATH;
            break;
            case "REJECT": destination = PATH + REJECTED_PATH;
            break;
            default: System.out.println("Problem with writer when target creating!");
        }
        String name = "target_" + aim.getOrdinal();
        String inner = this.renderAim(aim);

        File target = new File(destination + name);
        if (!target.exists(destination + name)) target.createNewFile();

        try (
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(target)    
            )
        ) {
            writer.write(inner);
        }
        catch (IOException exception) {
            System.out.println("Problem with writer when target creating, exception is: " + exception);
        }
    }

    private void createCrunch(AbstractAim aim) {
        String destination;
        switch (aim.getStatus()) {
            case "DRAFT":
            case "START":
            case "UNFREEZE":
            case "MODIFY": destination = PATH;
            break;
            case "CLOSE": destination = PATH + DONE_PATH;
            break;
            case "FREEZE": destination = PATH + FROZEN_PATH;
            break;
            case "REJECT": destination = PATH + REJECTED_PATH;
            break;
            default: System.out.println("Problem with writer when crunch creating!");
        }
        String name = "crunch_" + aim.getOrdinal();
        String inner = this.renderAim(aim);

        File crunch = new File(destination + name);
        if (!crunch.exists(destination + name)) crunch.createNewFile();

        try (
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(crunch);    
            )
        ) {
            writer.write(inner);
        }
        catch (IOException exception) {
            System.out.println("Problem with writer when crunch creating, exception is: " + exception);
        }
    }

    public void pushEntities() {
        for (AbstractPlan plan: Storage.dailyPlans) {
            this.createDaily(plan);
        }
        for (AbstractPlan plan: Storage.weeklyPlans) {
            this.createWeekly(plan);
        }
        for (AbstractAim aim: Storage.targets) {
            this.createTarget(aim);
        }
        for (AbstractAim aim: Storage.crunches) {
            this.createCrunch(aim);
        }
    }
}

