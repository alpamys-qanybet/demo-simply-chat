package info.simply.chat;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="sc_message")
public class Message implements Serializable {
    private static final long serialVersionUID = -5786117905672367869L;

    private long id;
    private String content;
    private User user;
    private Room room;

    @Id
    @Column(name="id_")
    @GeneratedValue(strategy=GenerationType.AUTO)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name="content_", nullable=false)
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "user_")
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "room_")
    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }
}
