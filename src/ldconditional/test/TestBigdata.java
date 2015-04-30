package ldconditional.test;

import java.io.IOException;
import oeg.utils.ExternalSort;

/**
 *
 * @author vroddon
 */
public class TestBigdata {
    public static void main(String[] args) {
        String args2[]={"data.nq", "out.nq"};
        try {
            ExternalSort.main(args2);
        } catch (IOException ex) {
        }
    }

}
