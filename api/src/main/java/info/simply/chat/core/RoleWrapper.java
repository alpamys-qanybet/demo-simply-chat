package info.simply.chat.core;

import info.simply.chat.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleWrapper {
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public static RoleWrapper wrap(Role role){
		try {
			RoleWrapper wrapper = new RoleWrapper();
			wrapper.setName(role.getName());
			return wrapper;
		}
		catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	public static List<RoleWrapper> wrap(List<Role> roles){
		List<RoleWrapper> list = new ArrayList<RoleWrapper>();
		for (Role role: roles)
			list.add(wrap(role));

		return list;
	}
}