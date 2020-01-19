package bridge;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.ParseException;
import exceptions.*;
import entities.*;

public class StrategyReader implements IAbstractReader {
    public static final String READER_TYPE = "strategy";
    public static final String PATH = "./strategy/";
    private File strategy;

    private String getTypeFromName(String name) {
        return name.substring(0, name.indexOf("_"));
    }

    private int getOrdinalFromName(String name) {
        return Integer.parseInt(
            name.substring(name.indexOf("_") + 1, name.indexOf("."))        
        );
    }

    //TODO Maybe possible use RegExp for parsing
    private ArrayList<String> parseAim(File aim) {
        ArrayList<String> stateSetterParams = new ArrayList<>();
        String phase = "";
        String description = "";

        try (
            BufferedReader reader = new BufferedReader(
                new FileReader(aim)    
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("# DoD") != -1) phase = "dod";
                else if (line.indexOf("# Deadline") != -1) phase = "deadline";
                else if (line.indexOf("# Description") != -1) phase = "description";
                else if (line.indexOf("# History") != -1) phase = "history";
                else if (line.indexOf("# Postmortem") != -1) phase = "postmortem";

                switch (phase) {
                    case "dod": {
                        if ((line.length() > 0) && (line.indexOf("# DoD") == -1)) {
                            stateSetterParams.add("dod | " + line);
                        }
                    }
                    break;
                    case "deadline": {
                        if ((line.length() > 0) && (line.indexOf("# Deadline") == -1)) {
                            stateSetterParams.add("deadline | " + line);
                        }
                    }
                    break;
                    case "description": description += line;
                    break;
                    case "history": {
                        if ((line.length() > 0) && (line.indexOf("# History") == -1)) {
                            stateSetterParams.add("history | " + line);
                        }
                    }
                    break;
                    case "postmortem": {
                        if (line.length() > 0) stateSetterParams.add("postmortem | " + line);
                    }
                    break;
                    default: System.out.println("Wrong title in aim entity file!");
                }
            }

            if (description != null) {
                stateSetterParams.add("description | " + description);
            }
        }
        catch (IOException exception) {
            System.out.println("Problem with parsing some aim: " + exception);
        }

        return stateSetterParams;
    }

    //TODO About RegExp too
    private ArrayList<String> parsePlan(File plan) {
        ArrayList<String> stateSetterParams = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<Integer> taskNums = new ArrayList<>();
        int lineNum = 0;
        int taskNum = 0;
        int summaryLineNum = 0;

        try (
            BufferedReader reader = new BufferedReader(
                new FileReader(plan)    
            )        
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;

                if (lineNum == 1) {
                    stateSetterParams.add(line.substring(line.length() - 10, line.length()));
                }
                else if (lineNum == 2) {
                    stateSetterParams.add(line);
                }
                else if (line.indexOf("- [") != -1) {
                    taskNum++;

                    if (line.indexOf("*") != -1) taskNums.add(taskNum);
                    stateSetterParams.add(line);
                }
                else if (line.indexOf("\\*") != -1) {
                    descriptions.add(line.substring(line.lastIndexOf("*") + 2));
                }
                else if (line.indexOf("# Summary") != -1) {
                    summaryLineNum = lineNum;
                }
                else if ((summaryLineNum != 0) && (summaryLineNum + 2 == lineNum)) {
                    stateSetterParams.add("summary | " + line);
                }
            }

            for (int i = 0; i < descriptions.size(); i++) {
                stateSetterParams.set(
                    taskNums.get(i) + 2,
                    stateSetterParams.get(taskNums.get(i) + 2) + " | " + descriptions.get(i)
                );
            }
        }
        catch (IOException exception) {
            System.out.println("Problem with parsing some plan: " + exception);
        }

        return stateSetterParams;
    }

    private void createDaily(File daily) throws EntitySetStateException,
    OrdinalAlreadyExistException, StorageUnexistingTypeException {
        AbstractPlan plan = new DailyPlan(
            this.getOrdinalFromName(daily.getName())        
        );

        plan.setState(this.parsePlan(daily));
    }

    private void createWeekly(File weekly) throws EntitySetStateException,
    OrdinalAlreadyExistException, StorageUnexistingTypeException {
        WeeklyPlan plan = new WeeklyPlan(
            this.getOrdinalFromName(weekly.getName())        
        );

        plan.setState(this.parsePlan(weekly));
    }

    private void createTarget(File target) throws EntitySetStateException,
    AimPostmortemWithoutCauseException, ParseException, OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        Target aim = new Target(
            this.getOrdinalFromName(target.getName())
        );

        aim.setState(this.parseAim(target));
    }

    private void createCrunch(File crunch) throws EntitySetStateException,
    AimPostmortemWithoutCauseException, ParseException, OrdinalAlreadyExistException,
    StorageUnexistingTypeException {
        Crunch aim = new Crunch(
            this.getOrdinalFromName(crunch.getName())        
        );

        aim.setState(this.parseAim(crunch));
    }

    StrategyReader() throws StrategyReaderInitException {
        this.strategy = new File(PATH);
        if (!this.strategy.isDirectory()) {
            throw new StrategyReaderInitException();
        }
    }

    public String getReaderType() {
        return READER_TYPE;
    }

    public void loadEntities() throws EntitiesReaderTakeEntityException,
    EntitySetStateException, AimPostmortemWithoutCauseException, ParseException,
    OrdinalAlreadyExistException, StorageUnexistingTypeException {
        File[] files = this.strategy.listFiles();

        for (File file: files) {
            if (!file.isDirectory()) {
                switch (this.getTypeFromName(file.getName())) {
                    case DailyPlan.PLAN_TYPE: this.createDaily(file);
                    break;
                    case WeeklyPlan.PLAN_TYPE: this.createWeekly(file);
                    break;
                    case Target.AIM_TYPE: this.createTarget(file);
                    break;
                    case Crunch.AIM_TYPE: this.createCrunch(file);
                    break;
                    default: throw new EntitiesReaderTakeEntityException(file.getName());
                }
            } else {
                File[] innerFiles = file.listFiles();

                for (File innerFile: innerFiles) {
                    switch (this.getTypeFromName(innerFile.getName())) {
                        case DailyPlan.PLAN_TYPE: this.createDaily(innerFile);
                        break;
                        case WeeklyPlan.PLAN_TYPE: this.createWeekly(innerFile);
                        break;
                        case Target.AIM_TYPE: this.createTarget(innerFile);
                        break;
                        case Crunch.AIM_TYPE: this.createCrunch(innerFile);
                        break;
                        default: throw new EntitiesReaderTakeEntityException(innerFile.getName());
                    }
                }
            }
        }
    }
}

