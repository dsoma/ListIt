 package com.android.shoppinglist.vos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.shoppinglist.Item;
import com.android.shoppinglist.SavedItem;

public class ShoppingListModel extends SimpleObservable<ShoppingListModel>
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
	
	public ShoppingListModel() 
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
				this.notifyObservers(MESSAGE_LIST_CREATED, null);
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
				this.notifyObservers(MESSAGE_LIST_CREATED, null);
			}
		}
		
		return -1;
	}
	
	public int UpdateCheckBox(Context aContext, String aTableName,Item aItem)
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
				this.notifyObservers(MESSAGE_CHECKED_UPDATED, null);
			}
		}
		
		return -1;
	}
	
	public boolean CheckDuplicateLists(Context aContext, String aListName)
	{
		boolean listFound = false;
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
					listFound = true;
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
	
	public int DeleteList(Context aContext, String aListName)
	{
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1, errorCode2 = -1;
		
		if( iDBHelper != null )
		{
			errorCode1 = iDBHelper.deleteTable( aContext, aListName );
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'D', aListName, null, null);
			
			if( errorCode1 == SUCCESS && errorCode2 == SUCCESS)
			{
				notifyObservers(MESSAGE_LIST_DELETED,aListName);
				
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
			
			errorCode = iDBHelper.UpdateMasterTable(aContext, 'R', aOldItem, getDateString(),aNewItem);
				
			if(errorCode == SUCCESS)
			{
				notifyObservers(MESSAGE_LIST_EDITED,ListNames);
				
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
		
		int errorCode1 = -1, errorCode2 = -1;
		
		if( iDBHelper != null )
		{				
			errorCode1 = iDBHelper.UpdateTableRow( aContext, aTableName, aOldPos,aNewPos );
			
			errorCode2 = iDBHelper.UpdateMasterTable(aContext, 'U', aTableName, getDateString(), null);
			
			if( errorCode1 == SUCCESS && errorCode2 == SUCCESS)
			{
				this.notifyObservers(MESSAGE_ROW_POS_UPDATED, null);
			}
		}
		
		return -1;
		
	}
	
	public int UpdateListPosition(Context aContext, String aTableName, int aOldPos, int aNewPos)
	{		
		if( iDBHelper == null )
		{
			OpenDB( aContext );
		}
		
		int errorCode1 = -1;
		
		if( iDBHelper != null )
		{				
			errorCode1 = iDBHelper.UpdateTablePosition( aContext, aTableName, aOldPos,aNewPos );
						
			if( errorCode1 == SUCCESS )
			{
				this.notifyObservers(MESSAGE_LIST_POS_UPDATED, null);
			}
		}
		
		return -1;
		
	}
	private void OpenDB(Context aContext)
	{
		try 
		{
			iDBHelper = new DataBaseHelper(aContext);
		} 
		catch (SQLiteException se) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
	}
	
	private String getDateString()
	{		 
		Date today = Calendar.getInstance(Locale.getDefault()).getTime();

	    // (2) create our date "formatter" (the date format we want)
	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy \n HH:mm");

	    // (3) create a new String using the date format we want
	    String dateString = formatter.format(today);
	    
		return dateString;
	}
}
