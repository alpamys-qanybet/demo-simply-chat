package info.simply.chat.room;

import info.simply.chat.Message;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MessageWrapper {
    private long id;
    private String content;
    private long roomId;
    private long userId;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public long getRoomId() {
        return roomId;
    }
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public static MessageWrapper wrap(Message msg){
        MessageWrapper wrapper = new MessageWrapper();

        try {
            wrapper.setId(msg.getId());
            wrapper.setContent(msg.getContent());
            wrapper.setRoomId(msg.getRoom().getId());
            wrapper.setUserId(msg.getUser().getId());
        }
        catch (Exception e) {
//			e.printStackTrace();
        }
        return wrapper;
    }

    public static List<MessageWrapper> wrap(List<Message> msgs){
        List<MessageWrapper> list = new ArrayList<>();
        for (Message msg: msgs)
            list.add(wrap(msg));

        return list;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", content:'" + content + '\'' +
                ", roomId:" + roomId +
                ", userId:" + userId +
                '}';
    }
}

