package oeg.jodrlapi.examples;

import oeg.jodrlapi.odrlmodel.*;

import java.util.List;

/**
 * Lists all the possible actions.
 *
 * @author Victor
 */
public class Example4 {

    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        List<Action> la = Action.getCoreODRLActions();
        for(Action a: la)
        {
            System.out.println(a.title +"\t"+ a.definition);
        }
    }

}
