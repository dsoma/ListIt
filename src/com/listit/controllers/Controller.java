package com.listit.controllers;

import com.listit.controllers.OnControllerObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {
	
	private final List<OnControllerObserver> iHandlers = new ArrayList<OnControllerObserver>();
	
	public Controller() {
		
	}
	
	public void dispose() {}
	
	abstract public boolean handleMessage(int what, Object data);

	public boolean handleMessage(int what) {
		return handleMessage(what, null);
	}
	
	public final void addHandler(OnControllerObserver handler) {
		iHandlers.add(handler);
	}

	public final void removeHandler(OnControllerObserver handler) {
		iHandlers.remove(handler);
	}
	
	protected final void notifyHandlers(int what, int arg1, int arg2, Object obj) 
	{
		if (!iHandlers.isEmpty()) {
			/*for (OnControllerObserver handler : iHandlers) {
				Message msg = Message.obtain(handler, what, arg1, arg2, obj);
				msg.sendToTarget();
			}*/
		}
	}
	
}
