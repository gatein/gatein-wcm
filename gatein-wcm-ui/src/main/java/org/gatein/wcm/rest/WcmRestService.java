package org.gatein.wcm.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/wcm")
public class WcmRestService {

    @GET
    @Path("/{param}")
    public Response printMessage(@PathParam("param") String msg) {
        String result = "GateIn WCM REST example : " + msg;

        return Response.status(200).entity(result).build();
    }

}
