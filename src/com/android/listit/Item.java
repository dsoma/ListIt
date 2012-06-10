/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class Item.
 * 				This class acts as a data structure to hold the Items in the list. 
 * 
 */
package com.android.listit;

public class Item 
{
    private String iItemDesc = "";
    private String iQuantity = "";
    private boolean iChecked = false;
    private int iRowId; // Row Id is 1-based and not 0-based. 
    
    public Item() 
    {	 
    }
    
    public Item( int aRowId, String aItemDesc, String aQuantity, boolean aChecked ) 
    {
    	  iRowId = aRowId;
	      iItemDesc = aItemDesc;
	      iQuantity = aQuantity;
	      iChecked = aChecked;
	}
    
    public Item( int aRowId, String aItemDesc, String aQuantity) 
    {
    	  iRowId = aRowId;
	      iItemDesc = aItemDesc;
	      iQuantity = aQuantity;
	}
    
    public Item( Item aItem )
    {
    	iRowId    = aItem.iRowId;
    	iItemDesc = aItem.iItemDesc;
    	iQuantity = aItem.iQuantity;
    	iChecked  = aItem.iChecked;
    }
    
    public int getRowId() 
	{
    	return iRowId;
    }
    
    public void setRowId(int aRowId) 
	{
    	iRowId = aRowId;
    }
    
	public String getDesc() 
	{
    	return iItemDesc;
    }
    
    public String getQuantity() 
    {
	      return iQuantity;
	}
    
    public void setName(String aItemDesc) 
    {
    	iItemDesc = aItemDesc;
    }
    
    public String getName() 
    {
	      return iItemDesc;
	}
    
    public void setQty(String aQuantity) 
    {
    	iQuantity = aQuantity;
	}
    
    public boolean isChecked() 
    {
    	return iChecked;
    }
    
    public void setChecked(boolean checked) 
    {
    	iChecked = checked;
    }
    
    public String toStringName() 
    {
    	return iItemDesc; 
    }
    
    public String toStringQty() 
    {
	      return iQuantity; 
	}
    
    public void toggleChecked() 
    {
    	iChecked = !iChecked;
    }
  
}
