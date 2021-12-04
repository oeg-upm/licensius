package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.rdflicense2.LicenseEntry;
import oeg.rdflicense2.TripleStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This should be a content negotiation service for a predetermined mapping license.
 * @author vroddon
 */
@Controller
@Api(tags = "RDFLicense", value = "rdflicense")
@ApiOperation(value = "Provide one particular mapping for licenses.")
@RequestMapping("/rdflicense")
public class RDFLicense {
    
    private final String base = "https://raw.githubusercontent.com/w3c/odrl/master/bp/license/rdflicense/";

    @CrossOrigin
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET)
    @ApiOperation(value = "Redirects the request to the rdflicense mapping. Perhaps this should be made random.")
    @ResponseBody
    public String rdflicense(@PathVariable String id, HttpServletRequest req, HttpServletResponse resp)   {
        try{
        if (!id.contains(".ttl"))
            id+=".ttl";
        resp.sendRedirect(base + id);
/*        System.out.println(base+id);
        req.getRequestDispatcher("http://elmundo.es").forward(req, resp);
*/
        return id;
        }catch(Exception e)
        {
            e.printStackTrace();
            return "error";
        }
    }    
}
