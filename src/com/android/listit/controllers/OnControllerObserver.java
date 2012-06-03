package com.android.listit.controllers;

public interface OnControllerObserver 
{
	public static final int MESSAGE_GET_LIST_NAME = 1;
	public static final int MESSAGE_GET_ITEM = 2;
	public static final int MESSAGE_EDIT_LIST_NAME = 3;
	public static final int MESSAGE_HANDLE_DUPLICATE_LIST_NAME = 4;
	public static final int MESSAGE_HANDLE_DUPLICATE_EDIT_LIST = 5;
	public static final int MESSAGE_CLEAR_LIST = 6;
	
	void ControllerCallback(int aMessageId);
	void DisplayMessage(int aMessageId);
}
