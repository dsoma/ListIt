package com.android.shoppinglist;

/* This class acts as a datastructure to hold the Item List Tab*/

public class Item 
{
    private String iItemDesc = "";
    private String iQuantity = "";
    private boolean iChecked = false;
    
    public Item() 
    {	 
    }
    
    public Item( String aItemDesc ) 
    {
    	iItemDesc = aItemDesc;
    }
    
    public Item( String aItemDesc, String aQuantity ) 
    {
	      iItemDesc = aItemDesc;
	      iQuantity = aQuantity;
	}
    
    public Item( String aItemDesc, String aQuantity, int aChecked ) 
    {
	      iItemDesc = aItemDesc;
	      iQuantity = aQuantity;
	      iChecked = (aChecked != 0 ) ? true : false;
	}
    
    public Item( String aItemDesc, String aQuantity, boolean aChecked ) 
    {
	      iItemDesc = aItemDesc;
	      iQuantity = aQuantity;
	      iChecked = aChecked;
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
