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
	
	/* All the ids of the messages that the view could display on the screen */
	public static final int DUPLICATE_LIST = 1;
	
	/* States that the controller can be in */
	private enum State 
	{
		Idle,
		AddingList,
		AddingItem,
		LoadingList,
		DeletingItem,
		DeletingList,
		LoadingItem,
		UpdatingChecked,
		EditingItem,
		EditingList,
		UpdatingItemPos,
		UpdatingListPos
	}
	
	private ListItModel				iModel;
	private State					iState;
	private OnControllerObserver	iCurrentView;
	
	public ListItController( ListItModel aModel )
	{
		iModel = aModel;
		iState = State.Idle;
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
					iState = State.AddingList;
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_GET_LIST_NAME);
				}
				else
				{
					iState = State.AddingItem;
					
					ArrayList<Object> arguments = (ArrayList<Object>)aData;
					if((arguments.size())>2)
					{
						Item i = new Item((String) arguments.get(2), (String) arguments.get(3), (Boolean) arguments.get(4));
						iModel.InsertItem((Context) arguments.get(0), (String) arguments.get(1),i);
					}
					iState = State.Idle;	
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_GET_ITEM);
				}
				
				break;
			}
			
			case MESSAGE_SET_LIST_NAME:
			{
				if( iState == State.AddingList )
				{
					ArrayList<Object> arguments = (ArrayList<Object>) aData;
					Boolean listFound = iModel.CheckDuplicateLists((Context) arguments.get(0), (String) arguments.get(1));
					if (!listFound)
					{
						iModel.CreateList( (Context) arguments.get(0), (String) arguments.get(1));
						iState = State.AddingItem;	
						if((arguments.size())>3)
						{
							Item i = new Item((String) arguments.get(2), (String) arguments.get(3), (Boolean) arguments.get(4));
							iModel.InsertItem((Context) arguments.get(0), (String) arguments.get(1),i);
						}
						
						iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_GET_ITEM);																									
					}
					else
					{
						iCurrentView.DisplayMessage(DUPLICATE_LIST, null);
						iState = State.AddingList;
						listFound = false;
						iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_GET_LIST_NAME);
					}
				}
				break;
			}
			case MESSAGE_LOAD_LIST:
			{
				iState = State.LoadingList;
				
				iModel.LoadSavedLists( (Context) aData );
				
				iState = State.Idle;
				break;
			}
			case MESSAGE_DELETE_ITEM:
			{
				iState = State.DeletingItem;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.DeleteItem((Context) arguments.get(0), (String) arguments.get(1), (String) arguments.get(2), (Integer) arguments.get(3));
				
				iState = State.Idle;
				break;
			}
			case MESSAGE_DELETE_LIST:
			{
				iState = State.DeletingList;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.DeleteList((Context) arguments.get(0), (String) arguments.get(1));
				
				iState = State.Idle;
				
				break;
			}
			case MESSAGE_LOAD_ITEM:
			{
				iState = State.LoadingItem;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.LoadSavedListItems( (Context) arguments.get(0),(String) arguments.get(1) );
				
				iState = State.Idle;
				
				break;
			}
			
			case MESSAGE_UPDATE_CHECKED:
			{
				iState = State.UpdatingChecked;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.UpdateCheckBox( (Context) arguments.get(0),(String) arguments.get(1),(Item)arguments.get(2));
				
				iState = State.Idle;
				
				break;
			}
			case MESSAGE_EDIT_ITEM:
			{
				iState = State.EditingItem;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
								
				iModel.EditItem( (Context) arguments.get(0),(String) arguments.get(1),(Item)arguments.get(2), (Item)arguments.get(3));
				
				iState = State.Idle;
				
				break;
			}
			case MESSAGE_EDIT_LIST:
			{
				iState = State.EditingList;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				Boolean listFound = iModel.CheckDuplicateLists((Context) arguments.get(0), (String) arguments.get(2));
				
				if (!listFound)
				{
					iModel.EditList( (Context) arguments.get(0),(String) arguments.get(1),(String)arguments.get(2));
				}
				else
				{
					iCurrentView.DisplayMessage(DUPLICATE_LIST,(String) arguments.get(1));
					listFound = false;
					iCurrentView.ControllerCallback(OnControllerObserver.MESSAGE_EDIT_LIST_NAME);
				}
				iState = State.Idle;
				
				break;
			}
			case MESSAGE_UPDATE_ITEM_POS:
			{
				iState = State.UpdatingItemPos;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.UpdateItemPosition( (Context) arguments.get(0),(String) arguments.get(1),(Integer)arguments.get(2), (Integer)arguments.get(3));
				
				iState = State.Idle;
				
				break;
			}
			case MESSAGE_UPDATE_LIST_POS:
			{
				iState = State.UpdatingListPos;
				
				ArrayList<Object> arguments = (ArrayList<Object>) aData;
				
				iModel.UpdateListPosition( (Context) arguments.get(0),(String)arguments.get(1), (Integer)arguments.get(2), (Integer)arguments.get(3));
				
				iState = State.Idle;
				
				break;
			}
		}
		return true;
	}
}
