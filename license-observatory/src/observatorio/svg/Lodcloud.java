package observatorio.svg;

import observatorio.portals.ckan.CKANtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Esta clase representa la nube de datos enlazados.
 * @author vroddon
 */
public class Lodcloud {

    public static void main(String[] args) {
   parse();
    }
    
    /**
     * This method loads a LOD.svg file and generates a LOD2.svg where, for each dataset, a color is stablished based on a property
     */
    public static void parse()
    {
     try {
            BufferedReader br = new BufferedReader(new FileReader("lod.svg"));
            String str = "";
            String sout = "";
            int cambiar=1000;
                String res="";
            while ((str = br.readLine()) != null) {
                String search = "<a target=\"_blank\" xlink:type=\"simple\" xlink:href=\"";
                cambiar++;
                
                if (cambiar==2)
                {
                    int index=str.indexOf("<g");
                    if (index!=-1)
                    {
                        String s1=str.substring(0,index+20);
                        String s2=str.substring(index+26);
                        String color="FFFFFF";
                        
                        if (res.equals("notspecified"))
                                color="FFFFFF";
                        if (res.equals("publicdomain"))
                                color="0000FF";
                        if (res.equals("attribution"))
                                color="0088FF";
                        if (res.equals("sharealike"))
                                color="00FF88";
                        if (res.equals("restrictions"))
                                color="FF8800";
                        if (res.equals("closed"))
                                color="FF0000";
                        if (res.equals("other"))
                                color="888888";
                        str=s1+color+s2;
                    }
                }
                
                if (str.contains(search)) {
                    int index = str.indexOf(search);
                    if (index != -1) {
                        String strt = str.substring(index+51);
                        int index2 = strt.indexOf("\"");
                        if (index2 != -1) {
                            String ds = strt.substring(0, index2);
                            System.out.println(ds);
                            res=CKANtest.mapa.get(ds);
                            if (res==null)
                                res="";
                        //    String color = "00FFFF";
                            cambiar=0;
                         //   System.out.println(res);
                        }
                    }
                }


                sout += str + "\n";
            }

            BufferedWriter out = new BufferedWriter(new FileWriter("lod2.svg"));
            out.write(sout);
            out.close();
        } catch (Exception e) {
            System.err.println("HA HABIDO UN ERROR SE VA HABE UN FOLLON"+e.getMessage());
        }        
    }
    
}
