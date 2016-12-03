package info.simply.chat.room;

import info.simply.chat.Role;
import info.simply.chat.User;
import info.simply.chat.core.GenericWrapper;
import info.simply.chat.core.SecurityBean;
import info.simply.chat.user.UserBean;
import info.simply.chat.user.UserWrapper;

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

@Path("/secure/rooms")
@Produces({ MediaType.APPLICATION_JSON})
@RequestScoped
public class RoomRest {
    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    @Inject
    SecurityBean securityBean;

    @Inject
    RoomBean roomBean;

    @Inject
    UserBean userBean;

    @GET
    @Path("/")
    public List<RoomWrapper> get() {
        return RoomWrapper.wrap(roomBean.get());
    }

    @GET
    @Path("/{id}")
    public RoomWrapper get(@PathParam("id") Long id) {
        return RoomWrapper.wrap(roomBean.get(id));
    }

    @POST
    @Path("/")
    @Transactional
    public RoomWrapper add(RoomWrapper wrapper) throws IOException {
        if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
            return RoomWrapper.wrap( roomBean.add(wrapper) );
        }
        else {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return null;
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public RoomWrapper edit(@PathParam("id") Long id, RoomWrapper wrapper) throws IOException {
        if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
            return RoomWrapper.wrap( roomBean.edit(id, wrapper) );
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
            return GenericWrapper.wrap(roomBean.delete(id));
        }
        else {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return GenericWrapper.wrap(false);
        }
    }

    @GET
    @Path("/{id}/users")
    public List<UserWrapper> getUsers(@PathParam("id") Long id) throws IOException {
        return UserWrapper.wrap(roomBean.getUsers(id));
    }

    @POST
    @Path("/{id}/users")
    @Transactional
    public GenericWrapper addUser(@PathParam("id") Long id, UserWrapper userWrapper) throws IOException {
//        if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
        return GenericWrapper.wrap(roomBean.addUser(id, userWrapper));
    }

    @DELETE
    @Path("/{id}/users/{userId}")
    @Transactional
    public GenericWrapper deleteUser(@PathParam("id") Long id, @PathParam("userId") Long userId) throws IOException {
//        if (securityBean.hasRole(request.getUserPrincipal().getName(), Role.Name.ADMIN)) {
        return GenericWrapper.wrap(roomBean.removeUser(id, userId));
    }

    @POST
    @Path("/{id}/users/{userId}/message")
    @Transactional
    public GenericWrapper addMessage(@PathParam("id") Long id, @PathParam("userId") Long userId, MessageWrapper messageWrapper) throws IOException {
        if ( userBean.getUserByLogin(request.getUserPrincipal().getName()).getId() == userId) {
            return GenericWrapper.wrap(roomBean.addMessage(messageWrapper));
        }
        else {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return GenericWrapper.wrap(false);
        }
    }
}
