package info.simply.chat.room;

import info.simply.chat.Room;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RoomWrapper {

    private long id;
    private String name;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static RoomWrapper wrap(Room room){
        RoomWrapper wrapper = new RoomWrapper();

        try {
            wrapper.setId(room.getId());
            wrapper.setName(room.getName());
        }
        catch (Exception e) {
//			e.printStackTrace();
        }
        return wrapper;
    }

    public static List<RoomWrapper> wrap(List<Room> rooms){
        List<RoomWrapper> list = new ArrayList<>();
        for (Room room: rooms)
            list.add(wrap(room));

        return list;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                '}';
    }
}
