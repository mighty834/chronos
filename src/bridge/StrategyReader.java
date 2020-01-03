package bridge;
import java.io.*;

class StrategyReader implements IAbstractReader {
    private String strategyPath = "./strategy/";
    private File strategy;

    StrategyReader() {
        
    }

    public void getStrategy() {
        this.strategy = new File(this.strategyPath);

