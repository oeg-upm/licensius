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

import oeg.odrlapi.validator.ODRLValidator;

@Path("/validator")
@Api(value = "/validator", description = "Checks the conformance of ODRL Policy expressions with respect to the ODRL Information Model validation requirements.")
@Produces({"application/json"})
public class Validator {

    @POST
    @Path("/")
    @Consumes("text/turtle")
    @Produces("application/json")
    @ApiOperation(value = "validator", notes = "Returns if a policy is valid, not valid or unknown. Checks the conformance of ODRL Policy expressions with respect to the ODRL Information Model validation requirements. ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = ValidatorResponse.class)
        ,
                                @ApiResponse(code = 400, message = "Bad Request")
        ,
                                @ApiResponse(code = 415, message = "Unsupported Media Type")})
    public Response validator(@ApiParam(name = "policy", value = "ODRL policy serialized as RDF Turtle or RDF/XML", required = true) String rdf) {
        try {
            ODRLValidator validator = new ODRLValidator();
            ValidatorResponse vres = validator.validate(rdf);
            return Response.status(vres.status).entity(vres).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal error.").build();
        }
    }

}
