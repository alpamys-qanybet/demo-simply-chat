package info.simply.chat.core;

import info.simply.chat.Role;
import info.simply.chat.User;
import sun.misc.BASE64Encoder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

//@RequestScoped
@ApplicationScoped
public class SecurityBean {

	@PersistenceContext
	EntityManager em;

	public String hash(String input) {
		// please note that we do not use digest, because if we
		// cannot get digest, then the second time we have to call it
		// again, which will fail again
		MessageDigest digest = null;

		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
//			e.printStackTrace();
		}

		if (digest == null)
			return input;

		// now everything is ok, go ahead
		try {
			digest.update(input.getBytes("UTF-8"));
		} catch (java.io.UnsupportedEncodingException ex) {
//			ex.printStackTrace();
		}
		byte[] rawData = digest.digest();

		BASE64Encoder bencoder = new BASE64Encoder();
		return bencoder.encode(rawData);
	}

	public boolean hasRole(String login, Role.Name role) {
		try {
			User user = em.find(User.class, getIdByLogin(login) );

			for ( Role r: user.getRoles() )
				if (r.getName().equals(role.toString()))
					return true;

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	public List<String> getRoles() {
		try {
			List<String> list = new ArrayList<>();

			for (Role.Name role: Role.Name.values())
				list.add(role.toString());

			return list;
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	public List<Role> getRoles(Long userId) {
		try {
			User user = em.find(User.class, userId);

			List<Role> roles = new ArrayList<Role>();

			for (Role role: user.getRoles())
				roles.add(role);

			return roles;
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	public boolean addRole(Long userId, RoleWrapper roleWrapper) {
		return addRole(userId, roleWrapper.getName());
	}

	public boolean addRole(Long userId, String roleName) {
		User user = em.find(User.class, userId);
		Role role = em.find(Role.class, roleName);

		if (!user.getRoles().contains(role)) {
			user.getRoles().add(role);
			em.merge(user);
		}

		return true;
	}

	public boolean removeRole(Long userId, String rolename) {
		User user = em.find(User.class, userId);
		Role role = em.find(Role.class, rolename);

		if (user.getRoles().contains(role)) {
			user.getRoles().remove(role);
			em.merge(user);
		}

		return true;
	}

	public long getIdByLogin(String login) {
		try {
			User user = (User) em.createQuery("select u from User u where u.login = :login")
								.setParameter("login", login)
								.getSingleResult();

			return user.getId();
		}
		catch (Exception e) {
//			e.printStackTrace();
			return -1;
		}
	}
}
