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

        Storage.pull();
        Storage.push();
    }
}

