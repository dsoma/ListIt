package com.android.shoppinglist.vos;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.shoppinglist.Item;
import com.android.shoppinglist.SavedItem;

public class DataBaseHelper extends SQLiteOpenHelper 
{
	
	public SQLiteDatabase 	iDB;
	public String 			iDBPath;
	public String 			itemText;
	
	public static String 		DBName = "shoppingListData";
	public static final int 	version = '1';
	public static Context 		currentContext;
	
	
	public DataBaseHelper(Context context) 
	{
		super(context, DBName, null, version);
		currentContext = context;
		iDBPath = "/data/data/" + context.getPackageName() + "/databases";	
		createMasterTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<SavedItem> queryDBForTables( Context aContext )
	{
		ArrayList<SavedItem> tableNames = new ArrayList<SavedItem>();
		
		if (checkDbExists()) 
		{
			return tableNames;
		} 
		
		try 
		{
			iDB = aContext.openOrCreateDatabase(DBName, 0, null);
			
			//Cursor c = iDB.rawQuery("SELECT NAME FROM SQLITE_MASTER WHERE TYPE = 'table'AND NAME !='android_metadata'", null) ;
			Cursor c = iDB.rawQuery("SELECT name,date FROM MASTER", null) ;
			if (c != null && c.moveToFirst() ) 
			{
    			do 
    			{
    				String tName = c.getString(c.getColumnIndex("name"));
    				String tDate  = c.getString(c.getColumnIndex("date"));
    				SavedItem rowItem = new SavedItem(tName, tDate);
    				tableNames.add(rowItem);
    				
    			} while (c.moveToNext());
    			//c.close();
	    	}			
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "[queryDBForTables] DB error");
        } 
		finally 
		{
			if (iDB != null) 
        		iDB.close();
		}
		
		return tableNames;		
	}
	
	public ArrayList<Item> queryTableForRecords( Context aContext, String aTableName )
	{
		ArrayList<Item> entries = new ArrayList<Item>();
		
		if (checkDbExists()) 
		{
			return entries;
		} 
		try 
		{
			iDB = aContext.openOrCreateDatabase(DBName, 0, null);
			
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT, item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			Cursor c = iDB.rawQuery("SELECT item,quantity,checked FROM '" +
									aTableName+"'", null );
			boolean itemChecked = false;
						
			if (c != null  ) 
			{
				if(c.moveToFirst())
				{
	    			do 
	    			{
	    				String itemName = c.getString(c.getColumnIndex("item"));
	    				String itemQty = c.getString(c.getColumnIndex("quantity"));
	    				//Integer itemChecked = c.getInt(c.getColumnIndex("checked"));
	    				String itemCheck = c.getString(c.getColumnIndex("checked"));
	    				
	    				
	    				if(itemCheck.contentEquals("true"))
	    					itemChecked = true;
	    				else
	    					itemChecked = false;
	    				
	    				Item i = new Item( itemName, itemQty, itemChecked );
	    				
	    				entries.add(i);
	    				
	    			} while (c.moveToNext());
		    	}			
			}
			//c.close();
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "[queryDBForTables] DB error");
        } 
		finally 
		{
			if (iDB != null) 
        		iDB.close();
		}
		
		return entries;		
	}
	
	public int createTable(String aTableName) 
	{
		if (checkDbExists()) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
					   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");	
		
			
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}
	
	public int InsertRecord(Context aContext, String aTableName, Item aItem) 
	{
		if ( checkDbExists() ) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
				
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			Cursor c = iDB.rawQuery("SELECT row,item,quantity,checked FROM '" +
					aTableName+"'", null );
			
			int rowCount = c.getCount();
			rowCount = rowCount + 1;
			
			iDB.execSQL("INSERT INTO '" + aTableName + "' Values ('"+rowCount +"','"+ 
			            aItem.getName().toString() + "','" + aItem.toStringQty() + "','" + aItem.isChecked() +"');" );
			
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}

	public int UpdateRecord(Context aContext, String aTableName, Item aItem) 
	{
		if ( checkDbExists() ) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
				
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			Cursor c = iDB.rawQuery("SELECT item,quantity,checked FROM '" +
					aTableName+"'", null );
		
			if (c != null  ) 
			{
			if(c.moveToFirst())
			{
				do 
				{
					String itemName = c.getString(c.getColumnIndex("item"));
					//String itemQty = c.getString(c.getColumnIndex("quantity"));
					//String itemChecked = c.getString(c.getColumnIndex("checked"));
					
					if(itemName.contentEquals(aItem.getName().toString()))
					{		
						iDB.execSQL("UPDATE '" + aTableName + "' SET item='"+ 
					           aItem.getName().toString() + "',quantity='" + aItem.toStringQty() + "',checked='" + 
					           aItem.isChecked() +"' WHERE item='"+aItem.getName().toString()+"';" );
					}
				} while (c.moveToNext());
			}
			}
			//c.close();		
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}

	
	public int UpdateTableRow( Context aContext, String aTableName, int aOldPos,int aNewPos ) 
	{
		if ( checkDbExists() ) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		ArrayList<Item> arguments = new ArrayList<Item>();
		int rowId = 0;
		boolean itemChecked=false;
		Item mContent;
		String itemName = null, itemQty = null; 
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
				
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			Cursor c = iDB.rawQuery("SELECT item,quantity,checked FROM '" +
					aTableName+"'", null );
		
			if (c != null  ) 
			{
				if(c.moveToFirst())
				{
						c.moveToPosition(aOldPos);
						 itemName = c.getString(c.getColumnIndex("item"));
						 itemQty = c.getString(c.getColumnIndex("quantity"));
						 String itemCheck = c.getString(c.getColumnIndex("checked"));		    				
		    				
		    				if(itemCheck.contentEquals("true"))
		    					itemChecked = true;
		    				else
		    					itemChecked = false;
						
					 	iDB.execSQL("DELETE FROM '" + aTableName + "' WHERE item='"+itemName+"' AND row='"+ aOldPos+"';");
				
				}
			}
			c = iDB.rawQuery("SELECT item,quantity,checked FROM '" +
					aTableName+"'", null );
			if (c != null  ) 
			{
				if(c.moveToFirst())
				{
						
						do//put it into vector before deleting all the entries from MASTER
						{
							if(rowId==aNewPos)
							{
								mContent = new Item(itemName,itemQty,itemChecked);
								arguments.add(mContent);
								rowId = rowId+1;
							}
							boolean iChecked = false;
							String iName = c.getString(c.getColumnIndex("item"));
							String iQty = c.getString(c.getColumnIndex("quantity"));
							String iCheck = c.getString(c.getColumnIndex("checked"));		    				
		    				
		    				if(iCheck.contentEquals("true"))
		    					iChecked = true;
		    				else
		    					iChecked = false;
							
								mContent = new Item(iName,iQty,iChecked);
								
								arguments.add(mContent);
								rowId = rowId+1;
							
						}while(c.moveToNext());
						
						if(rowId==aNewPos)
						{
							mContent = new Item(itemName,itemQty,itemChecked);
							arguments.add(mContent);
							rowId = rowId+1;
						}
						
						iDB.execSQL("DELETE FROM '"+aTableName+"'");
										
							
							for(int i=0;i<arguments.size();i++)
							{
								Item tObject = arguments.get(i);
								String tName = tObject.getName().toString();
								String tDate = tObject.getQuantity().toString();
								boolean tChecked = tObject.isChecked();
								
								iDB.execSQL("INSERT INTO '" + aTableName + "' Values ('"+(i+1) +"','"+ 
										tName.toString() + "','" + tDate.toString() + "','" + tChecked +"');" );
						 	}
				}
			}
			
			//c.close();
						
					
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}

	public int UpdateTablePosition( Context aContext, String aTableName, int aOldPos,int aNewPos ) 
	{
		if ( checkDbExists() ) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		ArrayList<SavedItem> arguments = new ArrayList<SavedItem>();
		int rowId = 0;
		SavedItem mContent;
		String tableName = null, tableDate = null;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
				
			Cursor c = iDB.rawQuery("SELECT NAME, DATE FROM MASTER", null) ;
		
			if (c != null  ) 
			{
				if(c.moveToFirst())
				{
						c.moveToPosition(aOldPos);
						 tableName = c.getString(c.getColumnIndex("name"));
						 tableDate = c.getString(c.getColumnIndex("date"));
						
						iDB.execSQL("DELETE FROM MASTER WHERE NAME='"+tableName+"';");
				}
			}
			c = iDB.rawQuery("SELECT NAME, DATE FROM MASTER", null) ;
			if (c != null  ) 
			{
				if(c.moveToFirst())
				{
						
						do//put it into vector before deleting all the entries from MASTER
						{
							if(rowId==aNewPos)
							{
								mContent = new SavedItem(tableName,tableDate);
								arguments.add(mContent);
								rowId = rowId+1;
							}
							
								String tName = c.getString(c.getColumnIndex("name"));
								String tDate = c.getString(c.getColumnIndex("date"));
								mContent = new SavedItem(tName,tDate);
								
								arguments.add(mContent);
								rowId = rowId+1;
							
						}while(c.moveToNext());
						
						if(rowId==aNewPos)
						{
							mContent = new SavedItem(tableName,tableDate);
							arguments.add(mContent);
							rowId = rowId+1;
						}
						iDB.execSQL("DELETE FROM 'MASTER'");
										
							
							for(int i=0;i<arguments.size();i++)
							{
								SavedItem tObject = arguments.get(i);
								String tName = tObject.getName().toString();
								String tDate = tObject.getDate().toString();
								
						 		iDB.execSQL("INSERT INTO MASTER Values('"+ 
						 				tName + "','" + tDate + "');" );
						 	}
				}
			}
			
			//c.close();
					
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}
	
	private boolean checkDbExists() 
	{
		SQLiteDatabase checkDB = null;

		try {
			String myPath = iDBPath + DBName;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}
	
	public ArrayList<Item> editColumn(Context aContext, String aTableName, Item aOldItem, Item aNewItem) 
	{
		ArrayList<Item> ItemAndQty = new ArrayList<Item>();
		
		if ( checkDbExists() ) 
		{
			return ItemAndQty;
		} 
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
				
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			Cursor c = iDB.rawQuery("SELECT item,quantity FROM '" +
					aTableName+"'", null );
		
			if (c != null  ) 
			{
			if(c.moveToFirst())
			{
				do 
				{
					String itemName = c.getString(c.getColumnIndex("item"));
					String itemQty = c.getString(c.getColumnIndex("quantity"));
										
					if(itemName.contentEquals(aOldItem.getName().toString()) &&
						itemQty.contentEquals(aOldItem.getQuantity().toString()))
					{		
						iDB.execSQL("UPDATE '" + aTableName + "' SET item='"+ 
								aNewItem.getName().toString() + "',quantity='"+aNewItem.getQuantity().toString()+ "' WHERE item='"+aOldItem.getName().toString()+"' AND quantity='"+
								aOldItem.getQuantity().toString()+"';" );
					}
				} while (c.moveToNext());
			}
			}
					
			ItemAndQty.add(aOldItem);
			ItemAndQty.add(aNewItem);
			//c.close();
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return ItemAndQty;
	}
	
	public ArrayList<String> editTableName(Context aContext, String aOldItem, String aNewItem) 
	{
		ArrayList<String> ListNames = new ArrayList<String>();
		
		if ( checkDbExists() ) 
		{
			return ListNames;
		} 
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			ArrayList<SavedItem> tableName = queryDBForTables(aContext );
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			for(int i=0;i<tableName.size();i++)
			{
				SavedItem rowItem = tableName.get(i);
				String tName = rowItem.getName().toString();
				if(tName.contentEquals(aOldItem))
				{
					iDB.execSQL("ALTER TABLE " + aOldItem + " RENAME TO '"+ 
					aNewItem.toString() + "';" );
					
					ListNames.add(aOldItem);
					ListNames.add(aNewItem);
					break;
				}
			}
			
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return ListNames;
	}
	
	
	public int deleteRecord(Context aContext, String aTableName, String aRecordName, int aPos) 
	{
		if ( checkDbExists() ) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			iDB.execSQL("DELETE FROM '" + aTableName + "' WHERE item='"+aRecordName+"' AND row='"+aPos+"';");
			
			errorCode = 0;
			
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}
	
	public int deleteTable(Context aContext, String aTableName) 
	{
		if ( checkDbExists() ) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			iDB.execSQL("CREATE TABLE IF NOT EXISTS '" + aTableName +
			   "'(row INT,item VARCHAR, quantity VARCHAR, checked VARCHAR);");
			
			iDB.execSQL("DELETE FROM '" + 	aTableName+"'");
			iDB.execSQL("DROP TABLE '" +		aTableName+"'");
			
			errorCode = 0;			
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}
	
	
	//----------------MASTER TABLE----------------------------------------
	public int createMasterTable() 
	{
		if (checkDbExists()) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			iDB.execSQL("CREATE TABLE IF NOT EXISTS MASTER (name VARCHAR, date VARCHAR);");		
			
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}

	public int UpdateMasterTable(Context aContext, final char aCommand, String aTableName, String aDate, String aNewName) 
	{
		if (checkDbExists()) 
		{
			return 0;
		} 
		
		int errorCode = -1;
		
		try 
		{
			iDB = currentContext.openOrCreateDatabase(DBName, 0, null);
			
			iDB.execSQL("CREATE TABLE IF NOT EXISTS MASTER (name VARCHAR, date VARCHAR);");
			
			switch(aCommand){
				case 'U':
				case 'u':
					iDB.execSQL("UPDATE MASTER SET date='"+ aDate.toString() + "' WHERE name='"+aTableName+"';" );
					break;
				case 'D':
				case 'd':
					iDB.execSQL("DELETE FROM MASTER WHERE name='"+aTableName+"';");
					break;
				case 'E':
				case 'e':
				{
					Cursor c = iDB.rawQuery("SELECT name,date FROM MASTER", null );
					if (c != null  ) 
					{
						if(c.moveToFirst())
						{
							c.moveToPosition(Integer.parseInt(aDate));
							String listName = c.getString(c.getColumnIndex("name"));
							String listDate = c.getString(c.getColumnIndex("date"));
							iDB.execSQL("DELETE FROM MASTER WHERE name='"+aTableName+"';");
							
							c.moveToPosition(Integer.parseInt(aNewName));
							iDB.execSQL("INSERT INTO MASTER Values ('"+ 
								listName.toString() + "','" + listDate.toString() + "');" );		
						}
					}
					//c.close();
					break;
				}
				case 'I':
				case 'i':
				{
					iDB.execSQL("INSERT INTO MASTER Values ('"+ 
							aTableName.toString() + "','" + aDate.toString() + "');" );
				}
				break;
				
				case 'R':
				case 'r':
				{
					iDB.execSQL("UPDATE MASTER SET name='"+ aNewName.toString()+"',date='"+ aDate.toString() + "' WHERE name='"+aTableName+"';" );
					break;
				}
			}
			
			errorCode = 0;
		}
		catch (SQLiteException se ) 
		{
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } 
		finally 
		{
        	if (iDB != null) 
        		iDB.close();
        }
		
		return errorCode;
	}

}

