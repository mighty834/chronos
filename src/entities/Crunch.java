package entities;
import java.util.*;
import exceptions.*;

class Crunch extends AbstractAim {
    private ArrayList<AbstructAim> allTargets;
    private static final String AIM_TYPE = "crunch";

    public static String getTypeName() {
        return AIM_TYPE;
    }

    public String getAimType() {
        return AIM_TYPE;
    }

    @Override
    public void freeze() {
        System.out.println("You can't freeze crunch entity!");
    }
}

