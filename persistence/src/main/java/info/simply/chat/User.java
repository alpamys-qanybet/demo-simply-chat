package info.simply.chat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="sc_user")
public class User implements Serializable {
	private static final long serialVersionUID = 1101244352368046061L;

	private long id;
	private String login;
	private String name;
	private String password;
	private Set<Role> roles = new HashSet<>();
	private List<Room> rooms;
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

	@Column(name="login_", nullable=false, unique=true)
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name="name_", nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="password_", nullable=false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="sc_user_role",
			joinColumns=@JoinColumn(name="user_"),
			inverseJoinColumns=@JoinColumn(name="role_"))
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
	public List<Room> getRooms() {
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	@OneToMany(mappedBy = "user")
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}