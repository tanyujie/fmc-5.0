package com.paradisecloud.fcm.mqtt.model;

public class BannerParams {
	
	//终端序列号
	private String sn; 
	
	//会议号
	private String conferenceNum; 
	
	//字幕类型  local
	private String overlay; 
	
	//文字大小  （小（small）、中(middle)、大(large)、特大(max)、特小(min)）
	private String textsize; 
	
	//字幕字库default
	private String fontLib; 
	
	//文字位置 top/middle/bottom
	private String pos; 
	
	//文字滚动模式  vertical/horizon/none
	private String roll; 
	
	//偏移方向 纵滚（向上rollup/下rolldown）/横滚(向左rollLeft/右rollRight)
	private String rollDirection; 
	
	//偏移速度 标准/快/慢  standard/high/low
	private String rollSpeed;  
	
	//文字颜色 8种颜色 255:255:255(白)、0:0:0(黑)、红(255:0:0")、绿(0:255:0)、黄(255:255:0)、蓝(0:0:255)、紫(160:0:255)、淡蓝(0:255:255)
	private String textcolor; 
	
	//背景颜色
	private String bgcolor;
	
	//文字内容
	private String text; 
	
	//文字透明度 半/全/不透明 half/all/none  （滚动字幕才有这个属性）
	private String alpha; 
	
	//默认"256:256:256",
	private String outlineColor; 
	
	//自定义custom、无none、低low、中normal、高high、极高ultra
	private String offsetLevelX; 
	
	private String offsetX;
	
	private String offsetLevelY;
	
	private String offsetY;
	
	//默认false
	private Boolean enableWeibeijianti; 
	
	public String getOverlay() {
		return overlay;
	}
	
	public void setOverlay(String overlay) {
		this.overlay = overlay;
	}
	
	public String getTextsize() {
		return textsize;
	}
	
	public void setTextsize(String textsize) {
		this.textsize = textsize;
	}
	
	public String getFontLib() {
		return fontLib;
	}
	
	public void setFontLib(String fontLib) {
		this.fontLib = fontLib;
	}
	
	public String getPos() {
		return pos;
	}
	
	public void setPos(String pos) {
		this.pos = pos;
	}
	
	public String getRoll() {
		return roll;
	}
	public void setRoll(String roll) {
		this.roll = roll;
	}
	
	public String getRollDirection() {
		return rollDirection;
	}
	
	public void setRollDirection(String rollDirection) {
		this.rollDirection = rollDirection;
	}
	
	public String getRollSpeed() {
		return rollSpeed;
	}
	
	public void setRollSpeed(String rollSpeed) {
		this.rollSpeed = rollSpeed;
	}
	
	public String getTextcolor() {
		return textcolor;
	}
	
	public void setTextcolor(String textcolor) {
		this.textcolor = textcolor;
	}
	
	public String getBgcolor() {
		return bgcolor;
	}
	
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getAlpha() {
		return alpha;
	}
	
	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}
	
	public String getOutlineColor() {
		return outlineColor;
	}
	
	public void setOutlineColor(String outlineColor) {
		this.outlineColor = outlineColor;
	}
	
	public String getOffsetLevelX() {
		return offsetLevelX;
	}
	
	public void setOffsetLevelX(String offsetLevelX) {
		this.offsetLevelX = offsetLevelX;
	}
	
	public String getOffsetX() {
		return offsetX;
	}
	
	public void setOffsetX(String offsetX) {
		this.offsetX = offsetX;
	}
	
	public String getOffsetLevelY() {
		return offsetLevelY;
	}
	
	public void setOffsetLevelY(String offsetLevelY) {
		this.offsetLevelY = offsetLevelY;
	}
	
	public String getOffsetY() {
		return offsetY;
	}
	
	public void setOffsetY(String offsetY) {
		this.offsetY = offsetY;
	}
	
	public Boolean getEnableWeibeijianti() {
		return enableWeibeijianti;
	}
	
	public void setEnableWeibeijianti(Boolean enableWeibeijianti) {
		this.enableWeibeijianti = enableWeibeijianti;
	}
	
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public String getConferenceNum() {
		return conferenceNum;
	}

	public void setConferenceNum(String conferenceNum) {
		this.conferenceNum = conferenceNum;
	}
	
	@Override
	public String toString() {
		return "BannerParams [overlay=" + overlay + ", textsize=" + textsize + ", fontLib=" + fontLib + ", pos=" + pos
				+ ", roll=" + roll + ", rollDirection=" + rollDirection + ", rollSpeed=" + rollSpeed + ", textcolor="
				+ textcolor + ", bgcolor=" + bgcolor + ", text=" + text + ", alpha=" + alpha + ", outlineColor="
				+ outlineColor + ", offsetLevelX=" + offsetLevelX + ", offsetX=" + offsetX + ", offsetLevelY="
				+ offsetLevelY + ", offsetY=" + offsetY + ", enableWeibeijianti=" + enableWeibeijianti + ", sn=" + sn + ", conferenceNum=" + conferenceNum + "]";
	}
}
