package com.listit.controllers;

public interface OnControllerObserver 
{
	public static final int MESSAGE_GET_LIST_NAME = 1;
	public static final int MESSAGE_ADD_ITEM_TO_VIEW = 2;
	public static final int MESSAGE_EDIT_LIST_NAME = 3;
	public static final int MESSAGE_HANDLE_DUPLICATE_LIST_NAME = 4;
	public static final int MESSAGE_HANDLE_DUPLICATE_EDIT_LIST = 5;
	public static final int MESSAGE_CLEAR_LIST = 6;
	public static final int MESSAGE_UPDATE_TITLE = 7;
	
	void ControllerCallback(int aMessageId, Object aData);
	void DisplayMessage(int aMessageId);
}
