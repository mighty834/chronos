package entities;
import exceptions.*;

class Crunch extends AbstractAim {
    private ArrayList<AbstructAim> allTargets;

    @Override
    public void freeze() {
        System.out.println("You can't freeze crunch entity!");
    }
}

