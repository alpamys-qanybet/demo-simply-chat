package info.simply.chat.room;

import info.simply.chat.Message;
import info.simply.chat.Room;
import info.simply.chat.User;
import info.simply.chat.user.UserWrapper;
import info.simply.chat.websocket.monitoring.WsBean;
import info.simply.chat.websocket.monitoring.WsMonitoringBean;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@RequestScoped
public class RoomBean {
    @PersistenceContext
    EntityManager em;

    @Inject
    WsMonitoringBean wsMonitoringBean;

    public List<Room> get() {

        try {
            return em.createQuery("select r from Room r order by r.name").getResultList();
        }
        catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public Room get(Long id) {
        try {
            return (Room) em.find(Room.class, id);
        }
        catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public Room add(RoomWrapper wrapper) {
        try {
            Room room = new Room();
            room.setName(wrapper.getName());
            em.persist(room);

            try {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("event", WsBean.Event.ADD_ROOM.toString());
                jsonObj.put("room", new JSONObject(RoomWrapper.wrap(room).toString()));
                wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return room;
        }
        catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public Room edit(long id, RoomWrapper wrapper) {
        Room room = (Room) em.find(Room.class, id);

        room.setName(wrapper.getName());
        em.merge(room);

        try {
            JSONObject jsonObj = new JSONObject();

            jsonObj.put("event", WsBean.Event.EDIT_ROOM.toString());
            jsonObj.put("room", new JSONObject(RoomWrapper.wrap(room).toString()));
            wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return room;
    }

    public boolean delete(Long id) {
        try {
            Room room = (Room) em.find(Room.class, id);

            for (User user: room.getUsers()) {
//                removeUser(room.getId(), user.getId());
                user.getRooms().remove(room);
                em.merge(user);
            }

            for (Message msg: room.getMessages())
                em.remove(msg);

            try {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("event", WsBean.Event.REMOVE_ROOM.toString());
                jsonObj.put("roomId", room.getId());
                wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            em.remove(room);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List<User> getUsers(Long id) {
        try {
            Room room = (Room) em.find(Room.class, id);
            return room.getUsers();
        }
        catch (Exception e) {
//            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addUser(Long id, UserWrapper userWrapper) {
        try {
            Room room = (Room) em.find(Room.class, id);
            User user = (User) em.find(User.class, userWrapper.getId());

            if (!room.getUsers().contains(user)) {
                room.getUsers().add(user);
                em.merge(room);
            }

            if (!user.getRooms().contains(room)) {
                user.getRooms().add(room);
                em.merge(user);
            }

            try {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("event", WsBean.Event.ADD_USER_TO_ROOM.toString());
                jsonObj.put("roomId", room.getId());
                jsonObj.put("userId", user.getId());
                wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUser(Long id, Long userId) {
        try {
            Room room = (Room) em.find(Room.class, id);
            User user = (User) em.find(User.class, userId);

            if (room.getUsers().contains(user)) {
                room.getUsers().remove(user);
                em.merge(room);
            }

            if (user.getRooms().contains(room)) {
                user.getRooms().remove(room);
                em.merge(user);
            }

            try {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("event", WsBean.Event.REMOVE_USER_FROM_ROOM.toString());
                jsonObj.put("roomId", room.getId());
                jsonObj.put("userId", user.getId());
                wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    public boolean addMessage(MessageWrapper msgWrapper) {
        try{
            Message msg = new Message();
            msg.setContent(msgWrapper.getContent());

            Room room = (Room) em.find(Room.class, msgWrapper.getRoomId());
            msg.setRoom(room);

            User user = (User) em.find(User.class, msgWrapper.getUserId());
            msg.setUser(user);

            em.persist(msg);

            room.getMessages().add(msg);
            em.merge(room);

            user.getMessages().add(msg);
            em.merge(user);

            try {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("event", WsBean.Event.MESSAGE_ROOM.toString());
                jsonObj.put("message", new JSONObject(MessageWrapper.wrap(msg).toString()));
                wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
