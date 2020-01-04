package bridge;
import java.io.*;
import java.util.*;
import exceptions.*;
import entities.*;

class StrategyReader implements IAbstractReader {
    private static final String READER_TYPE = "strategy";
    private static final String PATH = "./strategy/";
    private File strategy;

    private int getOrdinalFromName(String name) {
        String temp;

        temp = name.substring(4);
        return Integer.parseInt(
            temp.substring(0, temp.indexOf("_"))
        );
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
                switch (file.getName().charAt(0)) {
                    case 'd': this.parseDaily(file);
                    break;
                    case 'w': this.parseWeekly(file);
                    break;
                    case 't': this.parseTarget(file);
                    break;
                    case 'c': this.parseCrunch(file);
                    break;
                    default: throw new StrategyReaderTakeEntityException(file.getName());
                }
            } else {
                File[] innerFiles = file.listFiles();

                for (File innerFile: innerFiles) {
                    switch (innerFile.getName().charAt(0)) {
                        case 'd': this.parseDaily(innerFile);
                        break;
                        case 'w': this.parseWeekly(innerFile);
                        break;
                        case 't': this.parseTarget(innerFile);
                        break;
                        case 'c': this.parseCrunch(innerFile);
                        break;
                        default: throw new StrategyReaderTakeEntityException(innerFile.getName());
                    }
                }
            }
        }
    }

    

    public void parseDaily(File daily) {
        String setterConfig;
        int ordinal = this.getOrdinalFromName(daily.getName());
        Date date;
        BufferedReader reader = new BufferedReader(
            new FileReader(daily);        
        );

        ordinal = Integer.parseInt(
            daily.getName().substring(4).substring(0, )        
        );

        Stirng line;
        while (line = reader.readLine() != -1) {
            
        }

        DailyPlan plan = new DailyPlan()
    }

    public static String getReaderType() {
        return READER_TYPE;
    }


