package ldconditional.test;

import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.LDRConfig;
import ldconditional.Main;

/**
 *
 * @author vroddon
 */
public class MainTest {


    public static void main(String[] args) throws Exception {
        Main.initLogger();
        LDRConfig.Load();
        ConditionalDatasets.loadDatasets();
        testServer();
    }

    public static void testDatasets()
    {
        for(ConditionalDataset cd : ConditionalDatasets.datasets)
            System.out.println(cd.toString()+"\n===========================\n");
    }

    public static void testServer()
    {
        try{
   //         Main.initServer();
   //         System.out.println("Server is running");
            TestClient2.browse("http://localhost/geo");
        }catch(Exception e)
        {

        }
    }


}
