/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class ListItModel (Model of MVC)
 * 				Model class containing all the data management (communicates with DB)
 */

package com.android.listit.vos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.android.listit.Item;
import com.android.listit.SavedItem;

public class ListItModel extends SimpleObservable<ListItModel>
{
	private DataBaseHelper	iDBHelper;

	private static final int SUCCESS = 0;
	
	// All message ids sent to the observers 
	public static final int MESSAGE_LIST_CREATED = 1;
	public static final int MESSAGE_LISTS_LOADED = 2;
	public static final int MESSAGE_ITEM_DELETED = 3;
	public static final int MESSAGE_LIST_DELETED = 4;
	public static final int MESSAGE_ITEMS_LOADED = 5;
	public static final int MESSAGE_CHECKED_UPDATED = 6;
	public static final int MESSAGE_ITEM_EDITED = 7;
	public static final int MESSAGE_ROW_POS_UPDATED = 8;
	public static final int MESSAGE_LIST_EDITED = 9;
	public static final int MESSAGE_LIST_POS_UPDATED = 10;
	
	public ListItModel() 
	{	
	}
	
	public int CreateList(Context aContext, String aListName)
	{
		if( TextUtils.isEmpty(aListName) )
			return -1;
		
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1, errorCode2 = -1;
		
		if( iDBHelper != null )
		{				
			errorCode1 = iDBHelper.createTable( aListName );
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'I', aListName, getDateString(),null);
			
			if( errorCode1 == SUCCESS && errorCode2==SUCCESS )
			{
				this.notifyObservers(MESSAGE_LIST_CREATED, aListName);
			}
		}
		
		return -1;
	}
	
	public int InsertItem(Context aContext, String aTableName,Item aItem)
	{
		if( TextUtils.isEmpty(aTableName) )
			return -1;
		
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1, errorCode2 = -1;
		if( iDBHelper != null )
		{				
			errorCode1 = iDBHelper.InsertRecord( aContext, aTableName, aItem);
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'U', aTableName, getDateString(), null);
			
			if( errorCode1 == SUCCESS && errorCode2 ==SUCCESS)
			{
				this.notifyObservers(MESSAGE_LIST_CREATED, aTableName);
			}
		}
		
		return -1;
	}
	
	public int UpdateCheckBox(Context aContext, String aTableName, Item aItem)
	{
		if( TextUtils.isEmpty(aTableName) )
			return -1;
		
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1, errorCode2 = -1;
		if( iDBHelper != null )
		{				
			errorCode1 = iDBHelper.UpdateRecord( aContext, aTableName, aItem);
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'U', aTableName, getDateString(), null);
			
			if( errorCode1 == SUCCESS && errorCode2 == SUCCESS )
			{
				this.notifyObservers(MESSAGE_CHECKED_UPDATED, aItem);
			}
		}
		
		return -1;
	}
	
	public int CheckDuplicateLists(Context aContext, String aListName)
	{
		int listFound = -1;
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		if( iDBHelper != null )
		{
			ArrayList<SavedItem> listNames = iDBHelper.queryDBForTables( aContext );
			
			for(int i=0;i<listNames.size();i++)
			{
				SavedItem itemRow = listNames.get(i);
				String listName = itemRow.getName();
				
				if((listName.toLowerCase().trim()).contentEquals(aListName.toLowerCase().trim()))
				{
					listFound = i;
					break;
				}
			}
				
		}
		return listFound;
	}
	
	public int LoadSavedLists(Context aContext)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		if( iDBHelper != null )
		{
			ArrayList<SavedItem> listNames = iDBHelper.queryDBForTables( aContext );
			
			notifyObservers(MESSAGE_LISTS_LOADED, listNames);
			
			return SUCCESS;
		}
		
		return -1;
	}
	
	public int LoadSavedListItems(Context aContext, String aListName)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		if( iDBHelper != null )
		{
			ArrayList<Item> items = iDBHelper.queryTableForRecords( aContext, aListName );
			
			notifyObservers(MESSAGE_ITEMS_LOADED, items);
			
			return SUCCESS;
		}
		
		return -1;
	}
	
	
	public int DeleteItem(Context aContext, String aListName, String aItem, int aPos)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1, errorCode2 = -1;
		
		if( iDBHelper != null )
		{
			errorCode1 = iDBHelper.deleteRecord( aContext, aListName,aItem, aPos  );
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'U', aListName, getDateString(),null);
			
			if( errorCode1 == SUCCESS && errorCode2 == SUCCESS )
			{
				ArrayList<Object> deletedContent = new ArrayList<Object>();
				deletedContent.add(aItem);
				deletedContent.add(aPos);
				
				notifyObservers(MESSAGE_ITEM_DELETED,deletedContent);
				
				return SUCCESS;
			}
		}
		
		return -1;
	}
	
	public int DeleteList(Context aContext, Integer aListNamePosition, String aName)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1, errorCode2 = -1;
		
		if( iDBHelper != null )
		{
			errorCode1 = iDBHelper.deleteTable( aContext, aName );
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'D', aName, null, null);
			
			if( errorCode1 == SUCCESS && errorCode2 == SUCCESS)
			{
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(aName);
				data.add(aListNamePosition);
				
				notifyObservers(MESSAGE_LIST_DELETED, data);
				
				return SUCCESS;
			}
		}
		
		return -1;
	}
	
	public int EditItem(Context aContext, String aListName, Item aOldItem, Item aNewItem)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode = -1;
		
		if( iDBHelper != null )
		{
			ArrayList<Item> ItemAndQty = iDBHelper.editColumn( aContext, aListName,aOldItem,aNewItem  );
			
			errorCode = iDBHelper.UpdateMasterTable(aContext, 'U', aListName, getDateString(), null);
			
			if(errorCode == SUCCESS)
			{
				notifyObservers(MESSAGE_ITEM_EDITED,ItemAndQty);
				
				return SUCCESS;	
			}
		}
		
		return -1;
	}
	
	public int EditList(Context aContext, String aOldItem, String aNewItem)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode = -1;
		
		if( iDBHelper != null )
		{
			ArrayList<String> ListNames = iDBHelper.editTableName( aContext, aOldItem, aNewItem );

			String modifiedDate = getDateString();
			
			ListNames.add(modifiedDate);
			
			errorCode = iDBHelper.UpdateMasterTable(aContext, 'R', aOldItem, modifiedDate,aNewItem);
				
			if(errorCode == SUCCESS)
			{
				notifyObservers(MESSAGE_LIST_EDITED, ListNames);
				
				return SUCCESS;	
			}
		}
		
		return -1;
	}
	
	public int UpdateItemPosition(Context aContext, String aTableName, int aOldPos, int aNewPos)
	{
		if( TextUtils.isEmpty(aTableName) )
			return -1;
		
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode = -1;
		
		if( iDBHelper != null )
		{				
			ArrayList<Item> itemList = iDBHelper.UpdateTableRow( aContext, aTableName, aOldPos,aNewPos );
			
			errorCode = iDBHelper.UpdateMasterTable(aContext, 'U', aTableName, getDateString(), null);
			
			if( errorCode == SUCCESS)
			{
				notifyObservers(MESSAGE_ROW_POS_UPDATED, itemList);
			}
		}
		
		return errorCode;
	}
	
	public int UpdateListPosition(Context aContext, String aTableName, int aOldPos, int aNewPos)
	{		
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		if( iDBHelper != null )
		{				
			ArrayList<SavedItem> itemList = iDBHelper.UpdateTablePosition( aContext, aTableName, 
																		   aOldPos, aNewPos );
						
			this.notifyObservers(MESSAGE_LIST_POS_UPDATED, itemList);
		}
		
		return 0;
	}
	
	public int GetListCount(Context aContext)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		if( iDBHelper != null )
		{				
			return iDBHelper.GetListCount( aContext );
		}
		
		return 0;
	}
	
	private void OpenDB(Context aContext)
	{
		try 
		{
			iDBHelper = new DataBaseHelper(aContext);
		} 
		catch (SQLiteException se) 
		{	
			// TODO
		} 
	}
	
	private String getDateString()
	{		 
		Date today = Calendar.getInstance(Locale.getDefault()).getTime();

	    // (2) create our date "formatter" (the date format we want)
	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy\nHH:mm");

	    // (3) create a new String using the date format we want
	    String dateString = formatter.format(today);
	    
		return dateString;
	}
}
