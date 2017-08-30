package oeg.odrlapi.rest.server.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/validator")
@Api(value = "/validator", description = "Checks the conformance of ODRL Policy expressions with respect to the ODRL Information Model validation requirements")
@Produces({"application/json"})
public class Validator {
   
	@POST
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
        @ApiOperation(value = "validator", notes = "Returns if a policy is valid, not valid or unknown. Checks the conformance of ODRL Policy expressions with respect to the ODRL Information Model validation requirements. ")
        @ApiResponses(value = { @ApiResponse(code = 200, message = "true or false") })
        public Response validator(@ApiParam(name="policy", value = "ODRL policy serialized as RDF Turtle", required = true) String turtle) {
		String result = "unknown";
		return Response.status(201).entity(result).build();
	}
	
}