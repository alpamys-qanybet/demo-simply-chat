package info.simply.chat.openapi;

import info.simply.chat.core.GenericWrapper;
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

@Path("/openapi")
@Produces({ MediaType.APPLICATION_JSON})
@RequestScoped
public class PublicRest {
    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    @Inject
    UserBean userBean;

    @POST
    @Path("/register")
    @Transactional
    public GenericWrapper register(UserWrapper userWrapper) throws IOException {
        return userBean.add(userWrapper.getLogin(), userWrapper.getName(), null, false);
    }
}