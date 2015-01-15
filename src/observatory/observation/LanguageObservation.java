package observatory.observation;

/**
 *
 * @author Victor
 */
public class LanguageObservation {
        int langtags = 0;
        int en = 0;
        int it = 0;
        int fr = 0;
        int es = 0;
        int de = 0;
        int ru = 0;
        int pl = 0;
        int nl = 0;
        int zh = 0;
        int pt = 0;
        int sv = 0;
        int others = 0;
        int nulo = 0;
    

        public String toString()
        {
            String s= "" + langtags + "\t" + en + "\t" + it + "\t" + fr + "\t" + es + "\t" + de + "\t" + ru + "\t" + pl + "\t" + nl + "\t" + zh + "\t" + pt + "\t" + sv + "\t" + others;
            return s;
        }
        
}
