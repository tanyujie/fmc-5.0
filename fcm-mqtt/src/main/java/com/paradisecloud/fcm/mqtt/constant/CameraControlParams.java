package com.paradisecloud.fcm.mqtt.constant;

public interface CameraControlParams {
	
	//本端
	String LOCAL = "local";
	
	//远端
	String FAREND = "farend";
	
	String CAMERA_CONTROL_MODE = "cameraControlMode";
	
	//本端命令
	String PANTILT_CONTROL_CMD  = "pantiltControlCmd";
	
	String SET_PANTILT_UP = "setPantiltUp";
	
	String SET_PANTILT_DOWN = "setPantiltDown";
	
	String SET_PANTILT_LEFT = "setPantiltLeft";
	
	String SET_PANTILT_RIGHT = "setPantiltRight";
	
	String SET_PANTILT_STOP = "setPantiltStop";
	
	String FOCUS_CONTROL_CMD = "focusControlCmd";
	
	String ZOOM_CONTROL_CMD = "zoomControlCmd";
	
	//远端命令
	String PZT = "pzt";
	
	String UP = "up";
	
	String DOWN = "down";
	
	String LEFT = "left";
	
	String RIGHT = "right";
	
	String STOP = "stop";
}
