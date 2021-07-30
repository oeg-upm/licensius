package oeg.rdflicense2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vroddon
 */
public class LicenseEntry {
    public String licenseid = "";
    public String title = "";
    public String source = "";
    public List<String> mapping = new ArrayList();
    
    public LicenseEntry()
    {
        
    }
    public LicenseEntry(String _id)
    {
        licenseid = _id;
    }
    public static void main(String args[])
    {
        System.out.println("hola mundo");
        TripleStore.startup();
        System.out.println("bye");
    }
    
}
