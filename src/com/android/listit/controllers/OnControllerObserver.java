package com.android.listit.controllers;

public interface OnControllerObserver 
{
	public static final int MESSAGE_GET_LIST_NAME = 1;
	public static final int MESSAGE_GET_ITEM = 2;
	public static final int MESSAGE_EDIT_LIST_NAME = 3;
	
	
	void ControllerCallback(int aMessageId);
	void DisplayMessage(int aMessageId,String aStr);
}
