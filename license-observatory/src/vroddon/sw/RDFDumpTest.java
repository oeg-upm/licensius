package vroddon.sw;

import java.util.List;

/**
 * Main class to make tests
 * @author Victor
 */
public class RDFDumpTest {
    
    public static void main(String[] args) {
        RDFDump dump = new RDFDump("E:\\data\\linghub.nt");
        int ntriples=dump.countTriples();
        System.out.println(ntriples);
        List<String> predicates=Licenser.getRightsPredicates();
        dump.filterByPredicates(predicates, "E:\\data\\linghub.rights.nt");
        System.out.println("Output file has been generated");
    }
    
}
