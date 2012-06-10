/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class ListItController (Controller of MVC)
 * 				Contains the business logic flow of the app. 
 */

package com.android.listit.controllers;

import java.util.ArrayList;

import android.content.Context;

import com.android.listit.Item;
import com.android.listit.vos.ListItModel;

public class ListItController extends Controller
{
	/* All the messages that the view sends to the controller */
	public static final int MESSAGE_ADD_ITEM = 1;
	public static final int MESSAGE_ADD_LIST = 2;
	public static final int MESSAGE_SET_LIST_NAME = 3;
	public static final int MESSAGE_LOAD_LIST = 4;
	public static final int MESSAGE_DELETE_ITEM = 5;
	public static final int MESSAGE_DELETE_LIST = 6;
	public static final int MESSAGE_LOAD_ITEM = 7;
	public static final int MESSAGE_UPDATE_CHECKED = 8;
	public static final int MESSAGE_EDIT_ITEM = 9;
	public static final int MESSAGE_UPDATE_ITEM_POS = 10;
	public static final int MESSAGE_EDIT_LIST = 11;
	public static final int MESSAGE_UPDATE_LIST_POS = 12;
	public static final int MESSAGE_GET_LIST_COUNT = 13;
	
	private ListItModel				iModel;
	private OnControllerObserver	iCurrentView;
	
	public ListItController( ListItModel aModel )
	{
		iModel = aModel;
	}
	
	public void setCurrentView( OnControllerObserver aView )
	{
		iCurrentView = aView;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	/* Heart of the engine - State machine should be handled here */
	public boolean handleMessage(int aMessage, Object aData) 
	{
		switch(aMessage)
		{
			case  MESSAGE_ADD_ITEM:
			{
				// If there is no list, then create a list first. 
				// Otherwise, just add an item. 
				
				if(((ArrayList<Object>)aData).get(1)=="")
				{
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_GET_LIST_NAME, null);
				}
				else
				{
					ArrayList<Object> arguments = (ArrayList<Object>)aData;
					if((arguments.size())>2)
					{
						Item i = new Item((Integer) arguments.get(5), (String) arguments.get(2), 
								          (String) arguments.get(3), (Boolean) arguments.get(4));
						
						iModel.InsertItem((Context) arguments.get(0), (String) arguments.get(1),i);
						
						iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_ADD_ITEM_TO_VIEW, i);
					}
				}
				
				break;
			}
			
			case MESSAGE_ADD_LIST:
			{
				iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_GET_LIST_NAME, null);
				break;
			}
			
			case MESSAGE_SET_LIST_NAME:
			{
				boolean status = true;
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				String listName = (String) arguments.get(1);
				int duplicateListFound = iModel.CheckDuplicateLists((Context) arguments.get(0), listName);
				
				// Unique name, so create a list and prompt for new items. 
				if (duplicateListFound == -1)
				{
					iModel.CreateList( (Context) arguments.get(0), (String) arguments.get(1));
					
					// Notify to clear the current list items first. 
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_CLEAR_LIST, null);
					
					// Notify to update the title of the current list to this new list
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_UPDATE_TITLE, listName);
					
					// If the item was sent along with the message, add it to the database. 
					if( arguments.size() > 2 )
					{
						Item i = new Item((Integer) arguments.get(5), (String) arguments.get(2), 
										  (String) arguments.get(3), (Boolean) arguments.get(4));
						
						iModel.InsertItem((Context) arguments.get(0), (String) arguments.get(1), i);
						
						// Notify to update the UI by adding new item to the array. 
						iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_ADD_ITEM_TO_VIEW, i);	
					}
					else
					{
						// Notify to update the UI by adding new item to the array. 
						iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_ADD_ITEM_TO_VIEW, null);
					}
					
					status = true;
				}
				// Duplicate name, so notify the observer to handle it. 
				else
				{
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_HANDLE_DUPLICATE_LIST_NAME, listName);
					status = false;
				}
				
				return status;
			}
			case MESSAGE_LOAD_LIST:
			{
				iModel.LoadSavedLists( (Context) aData );
				break;
			}
			case MESSAGE_DELETE_ITEM:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.DeleteItem((Context) arguments.get(0), (String) arguments.get(1), (String) arguments.get(2), (Integer) arguments.get(3));
				break;
			}
			case MESSAGE_DELETE_LIST:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.DeleteList((Context) arguments.get(0), 
						 		  (Integer) arguments.get(1),
						          (String)  arguments.get(2));
				
				break;
			}
			case MESSAGE_LOAD_ITEM:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.LoadSavedListItems( (Context) arguments.get(0),(String) arguments.get(1) );
				
				break;
			}
			
			case MESSAGE_UPDATE_CHECKED:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.UpdateCheckBox( (Context) arguments.get(0),(String) arguments.get(1),(Item)arguments.get(2));
				
				break;
			}
			case MESSAGE_EDIT_ITEM:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
								
				iModel.EditItem( (Context) arguments.get(0),(String) arguments.get(1),(Item)arguments.get(2), (Item)arguments.get(3));
				
				break;
			}
			case MESSAGE_EDIT_LIST:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				Context c = (Context) arguments.get(0);
				String oldListName = (String) arguments.get(1);
				String newListName = (String) arguments.get(2);
				int position = (Integer)arguments.get(3);
				
				int duplicateListFound = iModel.CheckDuplicateLists(c, newListName);
				
				boolean status = true;
				
				// If edit a list with the same name and Press OK, discard the message 'Duplicate'.
				// Dialog should not reappear.
				
				if (position == duplicateListFound)
					return status;
				
				// If unique name is provided, change the name in the database. 
				if (duplicateListFound == -1 )
				{
					iModel.EditList( c, oldListName, newListName );
				}
				// If duplicate name is provided, notify the observer to handle it. 
				else
				{
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_HANDLE_DUPLICATE_EDIT_LIST, newListName);
					status = false;
				}
				
				return status;
			}
			case MESSAGE_UPDATE_ITEM_POS:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.UpdateItemPosition( (Context) arguments.get(0),(String) arguments.get(1),(Integer)arguments.get(2), (Integer)arguments.get(3));
				
				break;
			}
			case MESSAGE_UPDATE_LIST_POS:
			{
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.UpdateListPosition( (Context) arguments.get(0),(String)arguments.get(1), (Integer)arguments.get(2), (Integer)arguments.get(3));
				
				break;
			}
		}
		return true;
	}
	
	public int GetListCount(Context aContext)
	{
		if( iModel == null || aContext == null )
			return 0;
		
		return iModel.GetListCount( (Context) aContext );
	}
}
