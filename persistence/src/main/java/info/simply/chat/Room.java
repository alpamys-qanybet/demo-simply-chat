package info.simply.chat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="chat_room")
public class Room implements Serializable {
    private static final long serialVersionUID = 5612866093260148614L;

    private long id;
    private String name;
    private List<User> users;
    private List<Message> messages;

    @Id
    @Column(name="id_")
    @GeneratedValue(strategy=GenerationType.AUTO)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name="name_", nullable=false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch=FetchType.LAZY) //cascade=CascadeType.ALL,
    @JoinTable(name="room_user",
            joinColumns=@JoinColumn(name="room_"),
            inverseJoinColumns=@JoinColumn(name="user_"))
    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    @OneToMany(mappedBy = "room")
    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}