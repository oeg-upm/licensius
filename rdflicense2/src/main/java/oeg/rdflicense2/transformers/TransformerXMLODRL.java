package oeg.rdflicense2.transformers;

import oeg.rdflicense2.TransformationResponse;

/**
 * From Metashare XML to ODRL.
 * 
 * @seeAlso https://docs.google.com/spreadsheets/d/1od-ip9FZ17Y0B8kVh6-yN8lGQh9i4y8KTGeMZNJ--IE/edit#gid=0
 * @author vroddon
 */
public class TransformerXMLODRL {
        public static TransformationResponse transformXML2ODRL(String xml) {
        try {
            
            String ttl = "";
            
            String prefix = "@prefix odrl: <http://www.w3.org/ns/odrl/2/> .\n@prefix dct: <http://purl.org/dc/terms/> .";
            String line0="_:license a odrl:Policy ;";
            ttl+=line0+"\n";
    
            TransformationResponse tr = new TransformationResponse(true, ttl);
            return tr;
           
        }catch(Exception e)
        {
            TransformationResponse tr = new TransformationResponse(false, e.getMessage());
            return tr;
        }
    }
}
