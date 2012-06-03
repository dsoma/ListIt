package com.android.listit;

import android.app.AlertDialog;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class SaveData 
{
	public int 		iCurrentDialogId;
	public int 		iCurrentItemPosition;
	public String	iCurrentListName;
	public String 	iItemEditBoxStr;
	public String	iQuantityEditBoxStr;
	public String	iItemDialogEditStr;
	public String	iQuantityDialogEditStr;
	public String   iListNameDialogEditStr;
	public String   iEditingListOldName;
	
	public AlertDialog	iCurrentDialog;
	
	public SaveData()
	{
		iCurrentDialogId = ListItActivity.DIALOG_NONE;
		iCurrentItemPosition = -1;
		iCurrentListName = "";
		iItemEditBoxStr = "";
		iQuantityEditBoxStr = "";
		iItemDialogEditStr = "";
		iQuantityDialogEditStr = "";
		iListNameDialogEditStr = "";
		iCurrentDialog = null;
		iEditingListOldName = "";
	}
	
	public void ClearDialogData()
	{
		iCurrentDialogId = ListItActivity.DIALOG_NONE;
		iCurrentItemPosition = -1;
		iItemDialogEditStr = "";
		iQuantityDialogEditStr = "";
		iListNameDialogEditStr = "";
		iCurrentDialog = null;
		iEditingListOldName = "";
	}
	
	public void SaveDialogData()
	{
		if( iCurrentDialog == null )
			return;
		
		switch(iCurrentDialogId)
    	{
    		case ListItActivity.DIALOG_EDIT_ITEM:
    		{
    			iItemDialogEditStr = ((AutoCompleteTextView) iCurrentDialog.findViewById(R.id.itemEditText)).getText().toString();
    			iQuantityDialogEditStr = ((EditText) iCurrentDialog.findViewById(R.id.qtyEditText)).getText().toString();
    			iListNameDialogEditStr = "";
    	    	
    			break;
    		}
    		case ListItActivity.DIALOG_EDIT_LISTNAME:
    		case ListItActivity.DIALOG_NEW_LIST:
    		{
    			iListNameDialogEditStr = ((EditText) iCurrentDialog.findViewById(R.id.saveEditText)).getText().toString();
    	    	
    			break;
    		}
    		default: break;
    	}
	}
	
	public void LoadDialogData(AlertDialog aDialog)
	{
		if( aDialog == null )
			return;
		
		iCurrentDialog = aDialog;
		
		switch(iCurrentDialogId)
		{
			case ListItActivity.DIALOG_EDIT_ITEM:
			{
				final AutoCompleteTextView inputItem = (AutoCompleteTextView) aDialog.findViewById(R.id.itemEditText);
   				final EditText inputQty = (EditText) aDialog.findViewById(R.id.qtyEditText);
   				
   			    inputItem.setText( iItemDialogEditStr );
   			    inputQty.setText( iQuantityDialogEditStr );
				
   			    break;
			}
			case ListItActivity.DIALOG_EDIT_LISTNAME:
			case ListItActivity.DIALOG_NEW_LIST:
			{
				final EditText inputListName = (EditText) aDialog.findViewById(R.id.saveEditText);
   				
				inputListName.setText( iListNameDialogEditStr );
   			    
   			    break;
			}
			default: break;
		}
	}
}
