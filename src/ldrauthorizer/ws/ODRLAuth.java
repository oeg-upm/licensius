package ldrauthorizer.ws;

import java.util.UUID;
import odrlmodel.LDRConfig;

/**
 *
 * @author Victor
 */
public class ODRLAuth {
    

    public static String getToken(String recurso, String userid)
    {
        String UID= UUID.randomUUID().toString();
        String token ="@base <"+LDRConfig.getServer()+"/ldr/> .\n";
        token+="@prefix odrl: <http://w3.org/ns/odrl/2/> .\n@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
        token+="</policy:0000>\n a odrl:Ticket ;\n";
	token+="odrl:permission [\n a odrl:Permission ; \n odrl:action odrl:play ; \n odrl:target ";
        token+="\"" + recurso +"\";\n";
        token+=" odrl:assignee \"" + userid +"\".\n";
        token+="] .\n";    
        return token;
    }
}
