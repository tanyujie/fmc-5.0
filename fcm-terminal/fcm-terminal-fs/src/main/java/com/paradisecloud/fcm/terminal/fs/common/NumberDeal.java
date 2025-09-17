package com.paradisecloud.fcm.terminal.fs.common;

import java.util.regex.Pattern;

public abstract class NumberDeal {
	
	private static final NumberDeal INSTANCE = new NumberDeal() 
	{

	};

	public static NumberDeal getInstance() 
	{
		return INSTANCE;
	}
	
	public Boolean isNumberic(String numStr) 
	{
		String regex="^[1-9]+[0-9]*$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(numStr).matches();
	}
}
