package info.simply.chat.user;

import info.simply.chat.Role;
import info.simply.chat.User;
import info.simply.chat.core.SecurityBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class UserWrapper {

	private long id;
	private String login;
	private String name;
	private String password;
	private String old;
	private boolean admin;

	@Inject
	SecurityBean securityBean;

	public UserWrapper(){}

	public UserWrapper(String login, String name, String password) {
		setLogin(login);
		setName(name);
		setPassword(password);
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOld() {
		return old;
	}
	public void setOld(String old) {
		this.old = old;
	}

	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public static UserWrapper wrap(User user){
		UserWrapper wrapper = new UserWrapper();

		try {
			wrapper.setId(user.getId());
		}
		catch(NullPointerException npe) {
//			npe.printStackTrace();
		}

		try {
			wrapper.setLogin(user.getLogin());
			wrapper.setName(user.getName());
//			wrapper.setPassword(user.getPassword());
			boolean admin = false;
			for ( Role r: user.getRoles() )
				if (r.getName().equals(Role.Name.ADMIN.toString()))
					admin = true;

			wrapper.setAdmin(admin);
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		return wrapper;
	}
	
	public static List<UserWrapper> wrap(List<User> users){
		List<UserWrapper> list = new ArrayList<UserWrapper>();
		for (User user: users)
			list.add(wrap(user));
		
		return list;
	}

	@Override
	public String toString() {
		return "{" +
				"id:" + id +
				", login:'" + login + '\'' +
				", name:'" + name + '\'' +
				", admin:" + admin +
				'}';
	}
}
