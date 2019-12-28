package entities;
import java.util.*;
import java.text.*;
import exceptions.*;

class DailyPlan extends AbstractPlan {
    private static final int maxPossibleTime = 12;
    private static final String dateFormat = "yyyy.MM.dd";

    DailyPlan(int futureDay) {
        this.date = new Date(new Date().getTime() + (futureDay * 86400000));
    }
    
    DailyPlan(Date date) {
        this.date = date;
    }

    DailyPlan(String date) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);

        try {
            this.date = format.parse(date);
        }
        catch (ParseException exception) {
            System.out.println(
                "Problem with date parsing in DailyPlan entity!\n" +
                exception.getMessage()
            );
        }
    }

    public int getPossibleTime() {
        return maxPossibleTime;
    }
}

