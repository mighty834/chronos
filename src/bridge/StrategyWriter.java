package bridge;
import java.io.*;
import java.util.*;
import java.time.*;
import java.text.*;
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
            this.strategy.mkdir();
            new File(PATH + DONE_PATH).mkdir();
            new File(PATH + FROZEN_PATH).mkdir();
            new File(PATH + REJECTED_PATH).mkdir();
        } else {
            if (!new File(PATH + DONE_PATH).isDirectory()) {
                new File(PATH + DONE_PATH).mkdir();
            }
            if (!new File(PATH + FROZEN_PATH).isDirectory()) {
                new File(PATH + FROZEN_PATH).mkdir();
            }
            if (!new File(PATH + REJECTED_PATH).isDirectory()) {
                new File(PATH + REJECTED_PATH).mkdir();
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
        result += format.format(aim.getDeadLine()) + "\n\n";

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

    private String renderPlan(AbstractPlan plan)
    throws PlanOnlyClosedMethodException, OpenTaskEstimationDiffException {
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
                descriptions.add(task.getDescription());
            }
            result += task.getTheses() + " ";

            String types = "";
            if (task.isWork()) types += "work";
            if (task.isFun()) types += (types.length() > 0) ? ",fun" : "fun";
            if (task.isRoutine()) types += (types.length() > 0) ? ",routine" : "routine";
            if (task.isGrowth()) types += (types.length() > 0) ? ",growth" : "growth";

            if (types.length() > 0) result += "{" + types + "} ";

            result += task.getEstimateVolume() + "h";

            if (task.getRealVolume() != 0) result += " " + task.getRealVolume() + "h";

            result += "\n";
        }

        if (plan.getSummary() != null) {
            result += "\n# Summary\n\n";
            result += plan.getSummary();
        }

        if (plan.isClosed()) {
            result += "\n\n";
            result += "`p.time: " + plan.getTotalTime() + "h`\n";
            result += "`rank: " + (int)Math.round(plan.getRank()) + "%`\n";
            result += "`diff: " + plan.getTotalEstimationDiff() + "`\n";
        }

        return result;
    }

    private void createDaily(AbstractPlan plan)
    throws IOException, PlanOnlyClosedMethodException, OpenTaskEstimationDiffException {
        String destination = (plan.isClosed()) ? PATH + DONE_PATH : PATH;
        String name = "daily_" + plan.getOrdinal() + ".md";
        String inner = this.renderPlan(plan);

        File daily = new File(destination + name);
        if (!daily.exists()) daily.createNewFile();
            
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

    private void createWeekly(AbstractPlan plan)
    throws IOException, PlanOnlyClosedMethodException, OpenTaskEstimationDiffException {
        String destination = (plan.isClosed()) ? PATH + DONE_PATH : PATH;
        String name = "weekly_" + plan.getOrdinal() + ".md";
        String inner = this.renderPlan(plan);

        File weekly = new File(destination + name);
        if (!weekly.exists()) weekly.createNewFile();

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

    private void createTarget(AbstractAim aim) throws IOException {
        String destination = "";
        switch (aim.getStatus().toString()) {
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
        String name = "target_" + aim.getOrdinal() + ".md";
        String inner = this.renderAim(aim);

        File target = new File(destination + name);
        if (!target.exists()) target.createNewFile();

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

    private void createCrunch(AbstractAim aim) throws IOException {
        String destination = "";
        switch (aim.getStatus().toString()) {
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
        String name = "crunch_" + aim.getOrdinal() + ".md";
        String inner = this.renderAim(aim);

        File crunch = new File(destination + name);
        if (!crunch.exists()) crunch.createNewFile();

        try (
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(crunch)    
            )
        ) {
            writer.write(inner);
        }
        catch (IOException exception) {
            System.out.println("Problem with writer when crunch creating, exception is: " + exception);
        }
    }

    public String getWriterType() {
        return WRITER_TYPE;
    }

    public void pushEntities()
    throws IOException, PlanOnlyClosedMethodException, OpenTaskEstimationDiffException {
        for (AbstractPlan plan: Storage.getDailyPlans()) {
            this.createDaily(plan);
        }
        for (AbstractPlan plan: Storage.getWeeklyPlans()) {
            this.createWeekly(plan);
        }
        for (AbstractAim aim: Storage.getTargets()) {
            this.createTarget(aim);
        }
        for (AbstractAim aim: Storage.getCrunches()) {
            this.createCrunch(aim);
        }
    }
}

