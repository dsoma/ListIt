package com.android.listit;

public class SavedItem 
{
    private String iItemDesc = "";
    private String iDate = "";
    //private int    iId = -1; 
    
    public SavedItem() 
    {	 
    }
    
    public SavedItem( int aId, String aItemDesc ) 
    {
    	iItemDesc = aItemDesc;
    	//iId = aId;
    }
    
    public SavedItem( int aId, String aItemDesc, String aDate ) 
    {
	      iItemDesc = aItemDesc;
	      iDate = aDate;
	      //iId = aId;
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
    
    /*public int getId()
    {
    	return iId;
    }*/
}
