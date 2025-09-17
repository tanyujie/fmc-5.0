package com.paradisecloud.fcm.mqtt.model;

public class OperateStatus {
	
	private Long id;
	private Boolean isAgree;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsAgree() {
		return isAgree;
	}
	public void setIsAgree(Boolean isAgree) {
		this.isAgree = isAgree;
	}
	
	public OperateStatus() {
		super();
		// TODO Auto-generated constructor stub
	}
	public OperateStatus(Long id, Boolean isAgree) {
		super();
		this.id = id;
		this.isAgree = isAgree;
	}
	@Override
	public String toString() {
		return "OperateStatus [id=" + id + ", isAgree=" + isAgree + "]";
	}
}
