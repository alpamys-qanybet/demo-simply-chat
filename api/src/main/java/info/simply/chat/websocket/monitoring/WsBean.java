package info.simply.chat.websocket.monitoring;

import info.simply.chat.Room;
import info.simply.chat.User;
import info.simply.chat.room.MessageWrapper;
import info.simply.chat.room.RoomBean;
import info.simply.chat.room.RoomWrapper;
import info.simply.chat.user.UserBean;
import info.simply.chat.user.UserWrapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class WsBean {
    @PersistenceContext
    EntityManager em;

    @Inject
    RoomBean roomBean;

    @Inject
    UserBean userBean;

    public enum Event {
        LAUNCH_INFO,
        ADD_ROOM, EDIT_ROOM, REMOVE_ROOM,
        ADD_USER, EDIT_USER, REMOVE_USER,
        ADD_USER_TO_ROOM, REMOVE_USER_FROM_ROOM, JOIN_ROOM, LEAVE_ROOM,
        MESSAGE_ROOM,
        ON_WS_OPEN, ON_WS_CLOSE
    }

    public String messageOnLaunch() throws JSONException {
        JSONObject json = new JSONObject();

        JSONArray jsonRooms = new JSONArray();
        for (Room room: roomBean.get()) {
            JSONObject jsonRoom = new JSONObject();
            jsonRoom.put("room", new JSONObject(RoomWrapper.wrap(room).toString()));
            jsonRoom.put("users", new JSONArray(UserWrapper.wrap(room.getUsers()).toString()));
            jsonRoom.put("messages", new JSONArray(MessageWrapper.wrap(room.getMessages()).toString()));
            jsonRooms.put(jsonRoom);
        }

        JSONArray jsonUsers = new JSONArray(UserWrapper.wrap(userBean.get()).toString());

        json.put("event", Event.LAUNCH_INFO.toString());
        json.put("rooms", jsonRooms);
        json.put("users", jsonUsers);

        return json.toString();
    }
}
