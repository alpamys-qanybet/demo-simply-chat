package info.simply.chat.user;

import info.simply.chat.Role;
import info.simply.chat.Room;
import info.simply.chat.User;
import info.simply.chat.core.GenericWrapper;
import info.simply.chat.core.PasswordManager;
import info.simply.chat.core.SecurityBean;
import info.simply.chat.websocket.monitoring.WsBean;
import info.simply.chat.websocket.monitoring.WsMonitoringBean;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

//@RequestScoped
@ApplicationScoped
public class UserBean {
	@PersistenceContext
	EntityManager em;

	@Inject
	SecurityBean securityBean;
	
	@Inject
	PasswordManager passwordManager;

	@Inject
	WsMonitoringBean wsMonitoringBean;

	public List<User> get() {
		
		try {
			return em.createQuery("select u from User u").getResultList();
		}
		catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	public User get(Long id) {
		try {
			return (User) em.find(User.class, id);
		}
		catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}


	public User add(UserWrapper userWrapper) {
		return null;
	}

	@Transactional
	public GenericWrapper add(String login, String name, String password, boolean isAdmin) {
		try {
			User user = new User();
			user.setLogin(login);
			user.setName(name);

			if (password == null)
				password = passwordManager.generate();
			user.setPassword(securityBean.hash(password));

			em.persist(user);

			em.createNativeQuery("INSERT INTO sc_user_group(group_id_, user_id_) " +
					"VALUES ('authenticated', ?);")
					.setParameter(1, user.getLogin())
					.executeUpdate();

			if (isAdmin) {
				securityBean.addRole(user.getId(), Role.Name.ADMIN.toString());
				return null;
			}
			else {
				securityBean.addRole(user.getId(), Role.Name.GUEST.toString());
				try {
					JSONObject jsonObj = new JSONObject();

					jsonObj.put("event", WsBean.Event.ADD_USER.toString());
					jsonObj.put("user", new JSONObject(UserWrapper.wrap(user).toString()));
					wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
				}
				catch (JSONException e){
					e.printStackTrace();
				}
				return GenericWrapper.wrap(password);
			}
		}
		catch (Exception e) {
			return GenericWrapper.wrap(false);
		}
	}
	
	public User edit(Long id, UserWrapper userWrapper) {
		try {
			
			User user = (User) em.find(User.class, id);
			user.setName(userWrapper.getName());
			
			user = em.merge(user);

			try {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put("event", WsBean.Event.EDIT_USER.toString());
				jsonObj.put("user", new JSONObject(UserWrapper.wrap(user).toString()));
				wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
			}
			catch (JSONException e){
				e.printStackTrace();
			}

			return user;
		}
		catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	public boolean delete(Long id) {
		try {
			User user = (User) em.find(User.class, id);
			em.createNativeQuery("DELETE FROM sc_user_group " +
								 "WHERE user_id_ = '" + user.getLogin() + "';")
				.executeUpdate();

			try {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put("event", WsBean.Event.REMOVE_USER.toString());
				jsonObj.put("userId", user.getId());
				wsMonitoringBean.sendMessageOverSocket(jsonObj.toString(), "1");
			}
			catch (JSONException e){
				e.printStackTrace();
			}
			
			em.remove(user);
			
			return true;
		}
		catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
	}
	
	public User getUserByLogin(String login) {
		try {
			return (User) em.createQuery("select u from User u where u.login = :login")
							.setParameter("login", login)
							.getSingleResult();
		}
		catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	public boolean changePassword(Long id, UserWrapper userWrapper) {
		try {
			User user = (User) em.find(User.class, id);
			
			if (user.getPassword().equals( securityBean.hash(userWrapper.getOld()) )) {
				user.setPassword(securityBean.hash(userWrapper.getPassword()));
				em.merge(user);

				return true;
			}
			
			return false;
		}
		catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
	}
	
	public boolean resetPassword(Long id) {
		try {
			User user = (User) em.find(User.class, id);
			
			String password = passwordManager.generate();
			user.setPassword(securityBean.hash(password));
			
			em.merge(user);
			return true;
		}
		catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
	}

	public List<Room> getRooms(Long id) {
		User user = get(id);
		if (user == null)
			return new ArrayList<>();

		return user.getRooms();
	}

//	public List<User> getAvailable() {
//		try {
//			return em.createQuery("select u from User u join u.roles r " +
//					"where r.name = :role " +
//					"and u.line is null")
//					.setParameter("role", Role.Name.OPERATOR.toString())
//					.getResultList();
//		}
//		catch (Exception e) {
////            e.printStackTrace();
//			return null;
//		}
//	}
}
