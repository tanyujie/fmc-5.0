package com.paradisecloud.fcm.terminal.fs.model;

public class NfsConfig {
	
	private String fmeIp;
	private String userName;
	private String password;
	private String port;
	private String recorderNfs;
	private Long id;
	
	public String getFmeIp() {
		return fmeIp;
	}
	
	public void setFmeIp(String fmeIp) {
		this.fmeIp = fmeIp;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getRecorderNfs() {
		return recorderNfs;
	}

	public Long getId() {
		return id;
	}

	public void setDeptId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "NfsConfig [fmeIp=" + fmeIp + ", userName=" + userName + ", password=" + password + ", port=" + port
				+ ", recorderNfs=" + recorderNfs + ", id=" + id + "]";
	}

	public NfsConfig(String fmeIp, String userName, String password, String port, String recorderNfs, Long id) {
		super();
		this.fmeIp = fmeIp;
		this.userName = userName;
		this.password = password;
		this.port = port;
		this.recorderNfs = recorderNfs;
		this.id = id;
	}

	public NfsConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
}
