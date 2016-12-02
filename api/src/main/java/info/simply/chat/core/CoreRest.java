package info.simply.chat.core;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces({ MediaType.APPLICATION_JSON})
//@Consumes({ MediaType.APPLICATION_JSON})
public class CoreRest {
    @Context
    HttpServletRequest request;

    @GET
    @Path("/")
    @Produces("text/plain")
    public String check() {
        return "Hello world";
    }

    @GET
    @Path("/displayjsonp")
    @Produces("text/plain")
    public String displayJsonp() {
        return "jsonpresp('{\"Manufacturer\":\"BMW\"}')";
    }

    @GET
    @Path("/authorized")
    @Produces("text/plain")
    public boolean authorized() {
        return request.getUserPrincipal() != null;
    }
}
