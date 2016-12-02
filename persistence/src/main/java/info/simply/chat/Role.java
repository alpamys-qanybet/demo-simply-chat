package info.simply.chat;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sc_role")
public class Role implements Serializable {
	private static final long serialVersionUID = -3765526776758460652L;

	private String name;

	public enum Name {
		ADMIN, GUEST
	}

	@Id
	@Column(name="name_", nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
