package com.paradisecloud.fcm.terminal.fs.model;

public class BusiFcmConfig 
{

    private Long id;
    private String defaultPassword;
    private String userLocalIp;
    private String userExternalIp;
    private String internalSipPort;
    private String vertoPortWs;
    private String vertoPortWss;
    private String wssPem;
    private String userPublicIp;
    private Long deptId;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDefaultPassword() {
		return defaultPassword;
	}
	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}
	public String getUserLocalIp() {
		return userLocalIp;
	}
	public void setUserLocalIp(String userLocalIp) {
		this.userLocalIp = userLocalIp;
	}
	public String getUserExternalIp() {
		return userExternalIp;
	}
	public void setUserExternalIp(String userExternalIp) {
		this.userExternalIp = userExternalIp;
	}
	public String getInternalSipPort() {
		return internalSipPort;
	}
	public void setInternalSipPort(String internalSipPort) {
		this.internalSipPort = internalSipPort;
	}
	public String getVertoPortWs() {
		return vertoPortWs;
	}
	public void setVertoPortWs(String vertoPortWs) {
		this.vertoPortWs = vertoPortWs;
	}
	public String getVertoPortWss() {
		return vertoPortWss;
	}
	public void setVertoPortWss(String vertoPortWss) {
		this.vertoPortWss = vertoPortWss;
	}
	public String getWssPem() {
		return wssPem;
	}
	public void setWssPem(String wssPem) {
		this.wssPem = wssPem;
	}
	public String getUserPublicIp() {
		return userPublicIp;
	}
	public void setUserPublicIp(String userPublicIp) {
		this.userPublicIp = userPublicIp;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public BusiFcmConfig(Long id, String defaultPassword, String userLocalIp, String userExternalIp,
			String internalSipPort, String vertoPortWs, String vertoPortWss, String wssPem, String userPublicIp,
			Long deptId) {
		super();
		this.id = id;
		this.defaultPassword = defaultPassword;
		this.userLocalIp = userLocalIp;
		this.userExternalIp = userExternalIp;
		this.internalSipPort = internalSipPort;
		this.vertoPortWs = vertoPortWs;
		this.vertoPortWss = vertoPortWss;
		this.wssPem = wssPem;
		this.userPublicIp = userPublicIp;
		this.deptId = deptId;
	}
	public BusiFcmConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
}
