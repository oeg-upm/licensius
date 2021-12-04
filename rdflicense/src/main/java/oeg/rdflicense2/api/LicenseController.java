package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import oeg.rdflicense2.LicenseEntry;
import oeg.rdflicense2.TripleStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@ApiOperation(value = "Access to licenses in RDF.")
public class LicenseController {
    
    @CrossOrigin
    @RequestMapping(
            value = "/license",
            produces= "application/json;charset=UTF-8",
            method = RequestMethod.GET)
    @ApiOperation(value = "Gets a list of licenses and their different encodings by different authors")
    @ResponseBody
    public ResponseEntity getLicenses(@RequestParam(required = false) String id)  {
        /*Iterator it = TripleStore.policiesIndex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();        
        }*/
        List<LicenseEntry> valores = new ArrayList<LicenseEntry>(TripleStore.policiesIndex.values());
        return new ResponseEntity<>( valores,HttpStatus.OK);
    }    
    
    @CrossOrigin
    @RequestMapping(
            value = "/license/refresh",
            produces= "application/json;charset=UTF-8",
            method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity refresh()  {
        TripleStore.clonegit();
        TripleStore.clear();
        TripleStore.loadlicenses();
        String msg = TripleStore.policies.size() + " licenses loaded";
        return new ResponseEntity<>( msg ,HttpStatus.OK);
    }    
}
