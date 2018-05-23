package oeg.odrlapi.rest.server.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/evaluator")
@Api(value = "/evaluator", description = "Evaluates an ODRL policy")
@Produces({"application/json"})
public class Evaluator {

    @POST
    @Path("/")
    @Consumes("text/turtle")
    @Produces("application/json")
    @ApiOperation(value = "evaluate", notes = "Given a set of policies, it will return a map with each policy and its evaluation state. A system that determines whether the Rules of an ODRL Policy expression have meet their intended action performance.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = EvaluatorResponse.class)
        , 
            @ApiResponse(code = 415, message = "Unsupported Media Type")
        ,
            @ApiResponse(code = 400, message = "Bad Request. One or more of the policies is not valid")})
    public Response evaluate(@ApiParam(name = "policy", value = "ODRL policy(es) serialized as RDF Turtle", required = true) String turtle) {
        EvaluatorResponse res = new EvaluatorResponse();
        res.results.put("http://example.com/policy:1012", "valid");
        return Response.status(200).entity(res).build();
    }

    @PUT
    @Path("/")
    @Consumes("text/turtle")
    @Produces("application/json")
    @ApiOperation(value = "inform", notes = "Informs whether a constraint has been satisfied or a duty has been fulfilled.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = EvaluatorResponse.class)
        , 
        @ApiResponse(code = 415, message = "Unsupported Media Type")
        ,
        @ApiResponse(code = 400, message = "Bad Request. One or more of the policies is not valid")})
    public Response inform(@ApiParam(name = "element", value = "URI of a constraint or duty", required = true) String element, @ApiParam(name = "satisfied", value = "true if the constraint has been satisfied or the duty has been fulfilled, false otherwise", required = true) String satisfied) {
        EvaluatorResponse res = new EvaluatorResponse();
        res.results.put("12Dsssss3s", "ok");
        return Response.status(201).entity(res).build();
    }

}
