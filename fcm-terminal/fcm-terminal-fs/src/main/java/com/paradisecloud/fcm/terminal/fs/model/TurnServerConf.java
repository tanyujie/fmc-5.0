package com.paradisecloud.fcm.terminal.fs.model;

public class TurnServerConf {

	private String listeningDevice;
	private String listeningPort;
	private String realm;
	private String minPort;
	private String maxPort;
	private String user;
	private String relayIp;
	private String externalIp;
	private Long id;
	
	public String getListeningDevice() {
		return listeningDevice;
	}
	
	public void setListeningDevice(String listeningDevice) {
		this.listeningDevice = listeningDevice;
	}
	
	public String getListeningPort() {
		return listeningPort;
	}
	
	public void setListeningPort(String listeningPort) {
		this.listeningPort = listeningPort;
	}
	
	public String getRealm() {
		return realm;
	}
	
	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	public String getMinPort() {
		return minPort;
	}
	
	public void setMinPort(String minPort) {
		this.minPort = minPort;
	}
	
	public String getMaxPort() {
		return maxPort;
	}
	
	public void setMaxPort(String maxPort) {
		this.maxPort = maxPort;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getRelayIp() {
		return relayIp;
	}
	
	public void setRelayIp(String relayIp) {
		this.relayIp = relayIp;
	}
	
	public String getExternalIp() {
		return externalIp;
	}
	
	public void setExternalIp(String externalIp) {
		this.externalIp = externalIp;
	}

	public Long getId() {
		return id;
	}

	public void setDeptId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "TurnServerConf [listeningDevice=" + listeningDevice + ", listeningPort=" + listeningPort + ", realm="
				+ realm + ", minPort=" + minPort + ", maxPort=" + maxPort + ", user=" + user + ", relayIp=" + relayIp
				+ ", externalIp=" + externalIp + ", id=" + id + "]";
	}

	public TurnServerConf(String listeningDevice, String listeningPort, String realm, String minPort, String maxPort,
			String user, String relayIp, String externalIp, Long id) {
		super();
		this.listeningDevice = listeningDevice;
		this.listeningPort = listeningPort;
		this.realm = realm;
		this.minPort = minPort;
		this.maxPort = maxPort;
		this.user = user;
		this.relayIp = relayIp;
		this.externalIp = externalIp;
		this.id = id;
	}

	public TurnServerConf() {
		super();
		// TODO Auto-generated constructor stub
	}
}
