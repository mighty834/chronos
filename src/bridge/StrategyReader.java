package bridge;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import exceptions.*;
import entities.*;

class StrategyReader implements IAbstractReader {
    private static final String READER_TYPE = "strategy";
    private static final String PATH = "./strategy/";
    private File strategy;

    private String getTypeFromName(String name) {
        return name.substring(0, name.indexOf("_"));
    }

    private int getOrdinalFromName(String name) {
        return Integer.parseInt(
            name.substring(name.indexOf("_") + 1, name.indexOf("."))        
        );
    }

    private String parsePlan(File plan) {
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
            while (line = reader.readLine() != null) {
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
                else if (line.indexOf("# Summary") != 0) {
                    summaryLineNum = lineNum;
                }
                else if ((summaryLineNum != 0) && (summaryLineNum + 2 == lineNum)) {
                    stateSetterParams.add(line);
                }
            }

            for (int i = 0; i < descriptions.length; i++) {
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

    StrategyReader() throws StrategyReaderInitException {
        this.strategy = new File(PATH);
        if (!this.strategy.isDirectory()) {
            throw new StrategyReaderInitException();
        }
    }

    public void loadStrategy() throws StrategyReaderTakeEntityException {
        File[] files = this.strategy.listFiles();

        for (File file: files) {
            if (!file.isDirectory()) {
                switch (this.getTypeFromName(file.getName())) {
                    case DailyPlan.getTypeName(): this.createDaily(file);
                    break;
                    case WeeklyPlan.getTypeName(): this.createWeekly(file);
                    break;
                    case Target.getTypeName(): this.createTarget(file);
                    break;
                    case Crunch.getTypeName(): this.createCrunch(file);
                    break;
                    default: throw new StrategyReaderTakeEntityException(file.getName());
                }
            } else {
                File[] innerFiles = file.listFiles();

                for (File innerFile: innerFiles) {
                    switch (this.getTypeFromName(innerFile.getName())) {
                        case DailyPlan.getTypeName(): this.createDaily(innerFile);
                        break;
                        case WeeklyPlan.getTypeName(): this.createWeekly(innerFile);
                        break;
                        case Target.getTypeName(): this.createTarget(innerFile);
                        break;
                        case Crunch.getTypeName(): this.createCrunch(innerFile);
                        break;
                        default: throw new StrategyReaderTakeEntityException(innerFile.getName());
                    }
                }
            }
        }
    }

    public void createDaily(File daily) {
        DailyPlan plan = new DailyPlan(
            this.getOrdinalFromName(daily.getName())        
        );

        plan.setState(this.parsePlan(daily));
    }

    public void createWeekly(file weekly) {
        WeeklyPlan plan = new WeeklyPlan(
            this.getOrdinalFromName(weekly.getName())        
        );

        plan.setState(this.parsePlan(weekly));
    }

    public static String getReaderType() {
        return READER_TYPE;
    }


