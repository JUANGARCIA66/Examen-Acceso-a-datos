package com.example.classes;

/**
 * Contacto
 * 
 * usuario
 * nombre
 * telefono
 * edad
 * 
 * @since 2022-01-25
 * @author Amadeo
 */
public class Contacto {

	private String username;
	private String gid;
	private String uid;
	private String command;

	public Contacto(String username, String uid, String gid, String command) {
		this.username = username;
		this.gid = gid;
		this.uid = uid;
		this.command = command;
	}

	@Override
	public String toString() {
		return "Usuario [username=" + username + ", GID=" + gid + ", UID=" + uid +
				", command=" + command + "]";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
