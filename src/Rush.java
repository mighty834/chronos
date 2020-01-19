import java.io.IOException;
import java.text.ParseException;
import entities.*;
import bridge.*;
import exceptions.*;

class Rush {
    public static void main(String[] args)
    throws MainException, IOException, ParseException {
        Storage.setReader("strategy");
        Storage.setWriter("strategy");

        Storage.pull("strategy");

        for (AbstractPlan plan: Storage.getDailyPlans()) {
            System.out.println("PLAN: " + plan.getDate().toString());
            for (AbstractPlan.Task task: plan.getTasks()) {
                System.out.println("Theses is: " + task.getTheses());
                System.out.println("Status is: " + task.isDone());
                System.out.println("\n________________________________________________\n");
            }
        }

        Storage.push();
    }
}

