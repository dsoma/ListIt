package com.android.shoppinglist;

public class SavedItem 
{
    private String iItemDesc = "";
    private String iDate = "";
    
    
    public SavedItem() 
    {	 
    }
    
    public SavedItem( String aItemDesc ) 
    {
    	iItemDesc = aItemDesc;
    }
    
    public SavedItem( String aItemDesc, String aDate ) 
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
