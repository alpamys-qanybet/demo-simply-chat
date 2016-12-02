package info.simply.chat.core;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/secure")
@Produces({ MediaType.APPLICATION_JSON})
//@Consumes({ MediaType.APPLICATION_JSON})
@RequestScoped
public class SecureRest {
	@Context
	HttpServletRequest request;
	
	@Context
	HttpServletResponse response;
	
	@Inject
	SecurityBean securityBean;
	
	@GET
	@Path("/")
	@Produces("text/plain")
	public String check() {
		return "Hello world";
	}
	
	@GET
	@Path("/service")
	@Produces("text/plain")
	/*public void service(@QueryParam("url") String url) throws IOException {
		response.sendRedirect(url); // "http://localhost/es/ui/app"
	}*/
	public String service() throws IOException {
		return "success";
	}
	
	@GET
	@Path("/roles")
	public List<GenericWrapper> getRoles() throws IOException {
		return GenericWrapper.wrap(securityBean.getRoles());
//		return securityBean.getRoles();
	}
}
