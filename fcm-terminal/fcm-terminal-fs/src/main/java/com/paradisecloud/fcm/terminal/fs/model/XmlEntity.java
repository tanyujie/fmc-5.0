package com.paradisecloud.fcm.terminal.fs.model;

public class XmlEntity {
	private String xmlStr;
	private String fileName;
	private Long id;
	
	public String getXmlStr() {
		return xmlStr;
	}
	
	public void setXmlStr(String xmlStr) {
		this.xmlStr = xmlStr;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getId() {
		return id;
	}

	public void setDeptId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "XmlEntity [xmlStr=" + xmlStr + ", fileName=" + fileName + ", id=" + id + "]";
	}

	public XmlEntity(String xmlStr, String fileName, Long id) {
		super();
		this.xmlStr = xmlStr;
		this.fileName = fileName;
		this.id = id;
	}

	public XmlEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
}
