package info.simply.chat.user;

import info.simply.chat.Role;
import info.simply.chat.core.GenericWrapper;
import info.simply.chat.core.RoleWrapper;
import info.simply.chat.core.SecurityBean;
import info.simply.chat.room.RoomWrapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/secure/users")
@Produces({ MediaType.APPLICATION_JSON})
//@Consumes({ MediaType.APPLICATION_JSON})
@RequestScoped
public class UserRest {

	@Context
	HttpServletRequest request;

	@Context
	HttpServletResponse response;

	@Inject
	SecurityBean securityBean;

	@Inject
	UserBean userBean;

	@GET
	@Path("/")
	public List<UserWrapper> get() {
		return UserWrapper.wrap( userBean.get() );
	}

	@GET
	@Path("/{id}")
	public UserWrapper get(@PathParam("id") Long id) {
		return UserWrapper.wrap( userBean.get(id) );
	}


	@POST
	@Path("/")
	@Transactional
	public UserWrapper add(UserWrapper userWrapper) throws IOException {
		if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
			return UserWrapper.wrap( userBean.add(userWrapper) );
		}
		else {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return null;
		}
	}

	@PUT
	@Path("/{id}")
	@Transactional
	public UserWrapper edit(@PathParam("id") Long id, UserWrapper userWrapper) throws IOException {
		String login = request.getUserPrincipal().getName();
		if (securityBean.hasRole(login, Role.Name.ADMIN) || id == securityBean.getIdByLogin(login)) {
//		if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
			return UserWrapper.wrap( userBean.edit(id, userWrapper) );
		}
		else {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return null;
		}
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public GenericWrapper delete(@PathParam("id") Long id) throws IOException {
		if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
			return GenericWrapper.wrap(userBean.delete(id));
		}
		else {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return GenericWrapper.wrap(false);
		}
	}

	@GET
	@Path("/current")
	public UserWrapper getCurrent() {
		return UserWrapper.wrap( userBean.getUserByLogin(request.getUserPrincipal().getName()) );
	}

	@GET
	@Path("/login/{login}")
	public UserWrapper getUserByLogin(@PathParam("login") String login) {
		return UserWrapper.wrap( userBean.getUserByLogin(login) );
	}

	@PUT
	@Path("/password/change")
	@Transactional
	public GenericWrapper changePassword(UserWrapper userWrapper) throws IOException {
		Long id = securityBean.getIdByLogin(request.getUserPrincipal().getName());
		return GenericWrapper.wrap(userBean.changePassword(id, userWrapper));
	}

	@GET
	@Path("/{id}/password/reset")
	@Transactional
	public GenericWrapper resetPassword(@PathParam("id") Long id) throws IOException {
		if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
			return GenericWrapper.wrap(userBean.resetPassword(id));
		}
		else {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return GenericWrapper.wrap(false);
		}
	}

	@GET
	@Path("/{id}/roles")
	public List<RoleWrapper> getRoles(@PathParam("id") Long id) throws IOException {
		String login = request.getUserPrincipal().getName();
//		if (securityBean.hasRole(login, Role.Name.ADMIN) || id == securityBean.getIdByLogin(login)) {
			return RoleWrapper.wrap( securityBean.getRoles(id) );
//		}
//		else {
//			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
//			return null;
//		}
	}

	@POST
	@Path("/{id}/roles")
	@Transactional
	public GenericWrapper addRole(@PathParam("id") Long id, RoleWrapper wrapper) throws IOException {
		if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
			return GenericWrapper.wrap(securityBean.addRole(id, wrapper));
		}
		else {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return GenericWrapper.wrap(false);
		}
	}

	@DELETE
	@Path("/{id}/roles/{role}")
	@Transactional
	public GenericWrapper deleteRole(@PathParam("id") Long id, @PathParam("role") String role) throws IOException {
		if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
			return GenericWrapper.wrap(securityBean.removeRole(id, role));
		}
		else {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return GenericWrapper.wrap(false);
		}
	}

	@GET
	@Path("/logout")
	public GenericWrapper logout() {
		try {
			request.logout();
			return GenericWrapper.wrap(true);
		}
		catch (Exception e) {
//			e.printStackTrace();
			return GenericWrapper.wrap(false);
		}
	}

	@GET
	@Path("/{id}/rooms")
	public List<RoomWrapper> getRooms(@PathParam("id") Long id) throws IOException {
		return RoomWrapper.wrap(userBean.getRooms(id));
	}
}