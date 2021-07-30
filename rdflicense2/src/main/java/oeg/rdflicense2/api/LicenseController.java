package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oeg.jodrlapi.odrlmodel.Policy;
import oeg.rdflicense2.LicenseEntry;
import oeg.rdflicense2.TripleStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author vroddon
 */
@Controller
@Api(tags = "License", value = "License")
public class LicenseController {
    
    @CrossOrigin
    @RequestMapping(
            value = "/license",
            produces= "application/json;charset=UTF-8",
            method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getLicenses(@RequestParam(required = false) String id)  {
        String s = "";
        Iterator it = TripleStore.map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();        
        }
        
        List<LicenseEntry> valores = new ArrayList<LicenseEntry>(TripleStore.map.values());
        
        return new ResponseEntity<>( valores,HttpStatus.OK);
//        return new ResponseEntity<>( TripleStore.policies,HttpStatus.OK);
    }    
    
    
}
