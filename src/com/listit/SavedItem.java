/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class SavedItem 
 * 				This class acts as a data structure to hold the List Items in the database.  
 */

package com.listit;

public class SavedItem 
{
    private String iItemDesc = "";
    private String iDate = "";
    
    public SavedItem() 
    {	 
    }
    
    public SavedItem( int aId, String aItemDesc ) 
    {
    	iItemDesc = aItemDesc;
    }
    
    public SavedItem( int aId, String aItemDesc, String aDate ) 
    {
	      iItemDesc = aItemDesc;
	      iDate = aDate;
	}
       
    
    public void setName(String aItemDesc) 
    {
    	iItemDesc = aItemDesc;
    }
    
    public String getName() 
    {
	      return iItemDesc;
	}
    
    public void setDate(String aDate) 
    {
    	iDate = aDate;
	}
    public String getDate() 
    {
	      return iDate;
	}  
}
