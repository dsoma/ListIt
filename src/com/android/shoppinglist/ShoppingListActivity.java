// File: ShoppingListActivity.java

package com.android.shoppinglist;

import java.util.ArrayList;
import java.lang.Object;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView.OnEditorActionListener;

import com.android.shoppinglist.controllers.OnControllerObserver;
import com.android.shoppinglist.controllers.ShoppingListController;
import com.android.shoppinglist.vos.ModelObserver;
import com.android.shoppinglist.vos.ShoppingListModel;

public class ShoppingListActivity extends TabActivity 
 						  		  implements OnTabChangeListener,
 						  		  			 ModelObserver<ShoppingListModel>,
 						  			 		 OnControllerObserver
{
    private TabHost 				iTabHost;
	private ShoppingListModel 		iShoppingListModel;
	private ShoppingListController 	iShoppingListController;
	private String 					iCurrentListName;
	private TouchListView 			iItemListView;
	private ArrayAdapter<Item> 		iItemAdapter;
	private ArrayList<Item> 		iItems; 
	private AutoCompleteTextView 	iItemText;
	private EditText 				iQuantityText;
	private TouchListView 			iSavedListView;
	private String 					iDuplicateListName;
	private CheckBox 				iCheckBox;
	
	private ArrayAdapter<SavedItem> 	iSavedListAdapter;
	private ArrayList<SavedItem> 		iSavedLists;
	private String[]		 			iSuggestedWordList;
	private ArrayAdapter<String> 		iSuggestedItemAdapter;
	
	public  DialogInterface.OnClickListener	iListNameDialogListener;
	
	/* All tab ids */
	private static final int TabId_ItemList = 0;
	private static final int TabId_SavedList = 1;
	
	@Override
    public void onCreate(Bundle aSavedInstanceState) 
    {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.main);
        
        // Create the model and register the observer
        iShoppingListModel = new ShoppingListModel();
        iShoppingListModel.addObserver(this);
        
        // Create a controller and add the model. 
        iShoppingListController = new ShoppingListController(iShoppingListModel);
        
        // Register the view with the controller.
        iShoppingListController.addHandler(this);
        iShoppingListController.setCurrentView(this);
        
        iCurrentListName = "";
        iDuplicateListName="";
        
        iTabHost = getTabHost();
    	iTabHost.setOnTabChangedListener(this);
        
        // Add first view to tab host
        TabSpec firstTab = iTabHost.newTabSpec(getString(R.string.list_tab_name)); 
    	firstTab.setIndicator(getString(R.string.list_tab_name), 
    			              getResources().getDrawable(R.drawable.ic_tab_item_list_selected) ); //icon
    	firstTab.setContent(R.id.first_content);    //View
    	iTabHost.addTab(firstTab);
    	
    	// Add second view to tab host         
    	TabSpec secondTab = iTabHost.newTabSpec(getString(R.string.list_set_tab_name));
    	secondTab.setIndicator(getString(R.string.list_set_tab_name),
    						   getResources().getDrawable(R.drawable.ic_tab_list_set_unselected) ); //icon
    	secondTab.setContent(R.id.second_content);    //View
    	iTabHost.addTab(secondTab);
    	
    	// Set our custom array adapter as the ListView's adapter.
    	iItems = new ArrayList<Item>();
        iItemAdapter = new ItemListAdapter(this, iItems);
        
        iItemListView = (TouchListView) findViewById(R.id.itemListView);
        iItemListView.setAdapter( iItemAdapter );  
        
        // Set our custom array adapter as the SavedListView's adapter.
    	iSavedLists  = new ArrayList<SavedItem>();
    	iSavedListAdapter = new SavedListAdapter(this, iSavedLists);
    	
    	iSavedListView = (TouchListView) findViewById(R.id.savedListView);
        iSavedListView.setAdapter( iSavedListAdapter ); 
        
        iItemText = (AutoCompleteTextView) findViewById(R.id.editTextItem);
        iItemText.setOnEditorActionListener(new NextOnEditorActionListener());
        iSuggestedWordList = getResources().getStringArray(R.array.suggested_word_list);
        iSuggestedItemAdapter = new ArrayAdapter<String>(this, 
		                                                R.layout.suggested_item_list_view, 
		                                                iSuggestedWordList);
        iItemText.setAdapter(iSuggestedItemAdapter);
        
        iQuantityText = (EditText) findViewById(R.id.editTextQty);
        iQuantityText.setOnEditorActionListener(new DoneOnEditorActionListener());
        
        iCheckBox = (CheckBox)findViewById(R.id.checkBox);
        
        // populate the array adapter objects
        if (getLastNonConfigurationInstance() != null)
        {
        	ArrayList<Object> arguments= (ArrayList<Object>)getLastNonConfigurationInstance() ;
        	iItems = (ArrayList<Item>)arguments.get(0);
        	iItemAdapter = new ItemListAdapter(this, iItems);
            
            iItemListView.setAdapter( iItemAdapter ); 
        	iItemAdapter.notifyDataSetChanged();
        	
        	iSavedLists = (ArrayList<SavedItem>)arguments.get(1);        	
        	iSavedListAdapter = new SavedListAdapter(this, iSavedLists);
        	
            iSavedListView.setAdapter( iSavedListAdapter ); 
            iSavedListAdapter.notifyDataSetChanged();
        }       
    }
	
	class NextOnEditorActionListener implements OnEditorActionListener {
	   
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        if (actionId == EditorInfo.IME_ACTION_NEXT) {
	            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	            iQuantityText.requestFocus();
	            return true;	
	        }
	        return false;
	    }
	}
	
	class DoneOnEditorActionListener implements OnEditorActionListener {
		   
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        if (actionId == EditorInfo.IME_ACTION_DONE) {
	            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	            return true;	
	        }
	        return false;
	    }
	}
	
	public void onChange(ShoppingListModel model) 
   	{
	}
   	
   	protected void onStart() 
   	{
    	super.onStart();
    }
   	
   	public void onResume()
   	{
   		super.onResume();
   		
   		SetupAddButton();
   		SetupItemListView();
   		SetupItemListChangeObserver();
   		SetupListDrag();
   		SetupSavedListOnClick();
   		SetupSavedListOnLongPress();
   		SetupNewListButton();
   		
   	}
   	public void onPause()
   	{
   		super.onPause();
   		
   	}
   	
   	public void onStop()
   	{
   		super.onStop();
   	}
   	
   	public void onDestroy()
   	{
   		super.onDestroy();
   	}
   	// Save the data of textview so that it retains its value on activity pause / close
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
	       
      iItemText = (AutoCompleteTextView) findViewById(R.id.editTextItem);
      iSuggestedWordList = getResources().getStringArray(R.array.suggested_word_list);
      iSuggestedItemAdapter = new ArrayAdapter<String>(this, 
		                                                R.layout.suggested_item_list_view, 
		                                                iSuggestedWordList);
      iItemText.setAdapter(iSuggestedItemAdapter);
      String item = iItemText.getText().toString();
      
      iQuantityText = (EditText) findViewById(R.id.editTextQty);
      String qty = iQuantityText.getText().toString();
      
      iCheckBox = (CheckBox)findViewById(R.id.checkBox);
      boolean check;
      if(iCheckBox!=null)
    	  check = iCheckBox.isChecked();
      else 
    	  check = false;
      
      savedInstanceState.putString("Item", item);      
      savedInstanceState.putString("Qty", qty);      
      savedInstanceState.putBoolean("CheckBox", check); 
      savedInstanceState.putString("ListName", iCurrentListName);
      
	  super.onSaveInstanceState(savedInstanceState);
  }
  
  //Restore saved values
  protected void onRestoreInstanceState(Bundle savedInstanceState) 
  {
	    super.onRestoreInstanceState(savedInstanceState); 
	    
	 // Restore UI state from the savedInstanceState.
        if (savedInstanceState != null)
        {
          String strValue = savedInstanceState.getString("Item");
          if (strValue != null)
          {
        	  AutoCompleteTextView oControl = (AutoCompleteTextView)findViewById(R.id.editTextItem);
            oControl.setText(strValue);
          }
          
          strValue = savedInstanceState.getString("Qty");
          if (strValue != null)
          {
            EditText oControl = (EditText)findViewById(R.id.editTextQty);
            oControl.setText(strValue);
          }
          
          CheckBox chkTandC = (CheckBox)findViewById(R.id.checkBox);
          if(chkTandC!=null)
        	  chkTandC.setChecked(savedInstanceState.getBoolean("CheckBox"));
          
          strValue = savedInstanceState.getString("ListName");
          iCurrentListName = strValue;
          updateTitle(iCurrentListName);          
          
        }	  
  }
   	
  // Add button click
   	private void SetupAddButton()
   	{
   		// Add Button implementation        
        Button addButton = (Button) findViewById(R.id.buttonAdd);
        View.OnClickListener addButtonOnClickListener =	new OnClickListener() 
        {
			public void onClick(View v) 
			{
				ArrayList<Object> arguments = new ArrayList<Object>();
				
	        	arguments.add(getApplicationContext());
	        	arguments.add(iCurrentListName);
	        	
	        	String itemString = iItemText.getText().toString().trim();
	        	String qtyString = iQuantityText.getText().toString(); 
	        	
	        	if(TextUtils.isEmpty(itemString))
	        	{
	        		Toast.makeText(getApplicationContext(), "Enter the Item Name", Toast.LENGTH_SHORT).show();
	        	}
	        	else
	        	{
	        		if(qtyString.length()!=0)
	        		{
	        			String[] splitWords =  qtyString.split("\\.");
	        			if(splitWords.length > 2)
	        			{
	        				Toast.makeText(getApplicationContext(),"Invalid Quantity", Toast.LENGTH_SHORT).show();
	        				iQuantityText.requestFocus();
	        			}
	        			else if(splitWords.length==1)
	        			{
	        				String repString = splitWords[0].replaceFirst("^0+(?!$)", "");
	        				qtyString = repString;
	        			}
	        			else
	        			{
	        				String repString = splitWords[0].replaceFirst("^0+(?!$)", "");
	        				int decimalValue = Integer.parseInt(splitWords[1]);
	        				if(decimalValue==0)
	        					qtyString = repString;
	        				else
	        					qtyString = repString+"."+ Integer.toString(decimalValue);

	        			}
	        		}
	        			
		        			
	        		
		        		Item i = new Item();
		        		
			        	arguments.add(itemString);
			        	arguments.add(qtyString);
			        	arguments.add(i.isChecked());
		        	
					iShoppingListController.handleMessage(ShoppingListController.MESSAGE_ADD_ITEM, arguments);
					iItemText.requestFocus();
				}
			}
		};
		
        addButton.setOnClickListener(addButtonOnClickListener);
   	}
   	
   	//Item List View Click
   	private void SetupItemListView()
   	{
   		// When item is tapped, toggle checked properties of CheckBox and Model.
        iItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
        	public void onItemClick( AdapterView<?> aParent, View aItem, 
                                     int aPosition, long aId) 
        	{
        		Item item = iItemAdapter.getItem( aPosition );
        		ShowEditItemListDialog(item);
        	}
        });
   	}
   	
   	private void ShowEditItemListDialog(final Item aItem)
   	{
   		final String itemName = aItem.getName();
		final String quantity = aItem.getQuantity();
		if(!(TextUtils.isEmpty(itemName)))
		{
			LayoutInflater factory = LayoutInflater.from(ShoppingListActivity.this);            
	        final View textEntryView = factory.inflate(R.layout.edit_dialog, null);

	        AlertDialog.Builder alert = new AlertDialog.Builder(ShoppingListActivity.this);
	        final AutoCompleteTextView inputItem = (AutoCompleteTextView) textEntryView.findViewById(R.id.itemEditText);
	        inputItem.setAdapter(iSuggestedItemAdapter);
	        
	        final EditText inputQty = (EditText) textEntryView.findViewById(R.id.qtyEditText);
	        	 
	        alert.setTitle("Edit Item"); 
	        
	        inputItem.setText(itemName);
	        inputItem.setSelection(itemName.length());
	        inputQty.setText(quantity);
	        
	        // Set an EditText view to get user input  
	        alert.setView(textEntryView); 
	       
	        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	        { 
		        public void onClick(DialogInterface dialog, int whichButton) 
		        { 
		        	String iNewItemName = 	inputItem.getText().toString().trim();
		        	String iNewQty = 		inputQty.getText().toString();
		        	if(!TextUtils.isEmpty(iNewItemName))
		        	{
			        	ArrayList<Object> arguments = new ArrayList<Object>();
			        	arguments.add(getApplicationContext());
			        	arguments.add(iCurrentListName);
			        	Item oldData = new Item(itemName, quantity);
			        	arguments.add(oldData);
			        	Item newData = new Item(iNewItemName, iNewQty);
			        	arguments.add(newData);
			        	iShoppingListController.handleMessage(ShoppingListController.MESSAGE_EDIT_ITEM, 
								arguments);	
		        	}
		        	else
		        	{
		        		ShowEditItemListDialog(aItem);
		        		iNewItemName = "";
		        		iNewQty = "";
		        		Toast.makeText(getApplicationContext(), "Enter the Item Name ", Toast.LENGTH_SHORT).show();
		        	}
		        } 
	        });  
	        
	        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
	          public void onClick(DialogInterface dialog, int whichButton) { 
	            // Canceled. 
	          } 
	        }); 
	        
	        alert.show(); 
			}
  }
   	// Item List Change Observer for displaying the last item in the list to the use always.
   	private void SetupItemListChangeObserver()
   	{
   		iItemAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                iItemListView.setSelection(iItemAdapter.getCount() - 1);    
            }
        });	
   	}
   	
   	// List Item Drag and Drop listener
   	private void SetupListDrag()
   	{
   		iItemListView.setDropListener(onDrop);
   		iSavedListView.setDropListener(onDrop);
   	}
   	
   	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		public void drop(int from, int to) {
			if(from!=to)
			{
				if(iTabHost.getCurrentTab() ==  TabId_ItemList)
				{
					Item item=iItemAdapter.getItem(from);				
					iItemAdapter.remove(item);
					iItemAdapter.insert(item, to);
					
					ArrayList<Object> arguments= new ArrayList<Object>();
					arguments.add(getApplicationContext());
		        	arguments.add(iCurrentListName);
					arguments.add(from);
					arguments.add(to);
					
					iShoppingListController.handleMessage(ShoppingListController.MESSAGE_UPDATE_ITEM_POS, 
							arguments);	
				}
				else
				{
					SavedItem List=iSavedListAdapter.getItem(from);				
					iSavedListAdapter.remove(List);
					iSavedListAdapter.insert(List, to);
					
					ArrayList<Object> arguments= new ArrayList<Object>();
					arguments.add(getApplicationContext());
					arguments.add(List.getName());
					arguments.add(from);
					arguments.add(to);
					
					iShoppingListController.handleMessage(ShoppingListController.MESSAGE_UPDATE_LIST_POS, 
							arguments);	
				}
					
			}
		}
	};
	
	// SavedList on click
   	private void SetupSavedListOnClick()
   	{
   		iSavedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
   		{
   			public void onItemClick( AdapterView<?> aParent, View aItem, 
   									int aPosition,long aId ) 
   			{
   				// TODO Auto-generated method stub
   				SavedItem listName = iSavedListAdapter.getItem(aPosition);
   				iCurrentListName = listName.getName();
   											
				ArrayList<Object> arguments = new ArrayList<Object>();
				
	        	arguments.add(getApplicationContext());
	        	arguments.add(iCurrentListName);
	        	
				iShoppingListController.handleMessage(ShoppingListController.MESSAGE_LOAD_ITEM, 
														arguments);				
							
   			}
   		});
   	}
   	
   	//Saved List on Long Press 
   	private void SetupSavedListOnLongPress()
   	{
   		iSavedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> aParent, View aItem,
					int aPosition, long aId) {
				// TODO Auto-generated method stub
				
				SavedItem list = iSavedListAdapter.getItem(aPosition);
				final String listName = list.getName();
   				
				ShowEditListNameDialog(listName);
				return false;
			}
		});
   		
   	}
   	
   	//New button click
   	private void SetupNewListButton()
   	{
   		// New Button implementation        
        Button newButton = (Button) findViewById(R.id.buttonNew);
        View.OnClickListener newButtonOnClickListener = new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(! TextUtils.isEmpty(iItems.toString()))
				{
					iItems.clear();
					iItemAdapter.notifyDataSetChanged();
					iCurrentListName = "";
					updateTitle(iCurrentListName);
				}
					
				
				ArrayList<Object> arguments = new ArrayList<Object>();
				
	        	arguments.add(getApplicationContext());
	        	arguments.add(iCurrentListName);
	        	
	        	if(!TextUtils.isEmpty(iItemText.getText().toString()))
	        	{
	        		Item i = new Item();
	        		
		        	arguments.add(iItemText.getText().toString());
		        	arguments.add(iQuantityText.getText().toString());
		        	arguments.add(i.isChecked());
	        	}
	        	iShoppingListController.handleMessage(ShoppingListController.MESSAGE_ADD_ITEM, 
	        												arguments);					
			}
		};
		newButton.setOnClickListener(newButtonOnClickListener);
        
   	}
   	
   	// call back for view modification on Editing the list
   	public void EditItemList(ArrayList<Item> aItemNames)
	{
   		Item oldItemContent = aItemNames.get(0);
   		Item newItemContent = aItemNames.get(1);
   		
   		String oldItemName = oldItemContent.getName().toString();
		String oldQuantity = oldItemContent.getQuantity().toString();
		
		String newItemName = newItemContent.getName().toString();
		String newItemQty = newItemContent.getQuantity().toString();
		
		if(!TextUtils.isEmpty(oldItemName) || !TextUtils.isEmpty(newItemName))
		{	
			for(int i=0;i<iItems.size();i++)
			{			
				Item item = iItems.get(i);				
				String itemName = item.getName().toString();
				String quantity = item.getQuantity().toString();
				
				if(itemName.contentEquals(oldItemName)&& quantity.contentEquals(oldQuantity))
				{
					item.setName(newItemName);
					item.setQty(newItemQty);
					iItems.set(i, item);
					break;
				}
			}
			iItemAdapter.notifyDataSetChanged();
		}
	}
   	
   	// Call back for view modification on editing the Saved list
   public void EditListNames(ArrayList<String> aListNames)
   {
	   String oldListName = aListNames.get(0);
  		String newListName = aListNames.get(1);
		
		if(!TextUtils.isEmpty(oldListName) || !TextUtils.isEmpty(newListName))
		{	
			for(int i=0;i<iSavedLists.size();i++)
			{			
				//String name = iSavedLists.get(i);	
				SavedItem listName = iSavedLists.get(i);
   				String name = listName.getName();
								
				if(name.contentEquals(oldListName))
				{
					listName.setName(newListName.toString());
					iSavedLists.set(i, listName);
					break;
				}
			}
			if(oldListName.contentEquals(iCurrentListName))
			{
				updateTitle(newListName);
			}
			iSavedListAdapter.notifyDataSetChanged();
		}
   }
   	
   // On tab change
	public void onTabChanged(String aTabName) 
	{	
		if( iTabHost.getCurrentTab() ==  TabId_SavedList )
		{
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(iTabHost.getApplicationWindowToken(), 0);

			iShoppingListController.handleMessage(ShoppingListController.MESSAGE_LOAD_LIST, 
					 							  getApplicationContext());
		}
	}
	
	// List Name dialog
	private void ShowGetListNameDialog()
	{
		LayoutInflater factory = LayoutInflater.from(this);            
        final View textEntryView = factory.inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = (EditText) textEntryView.findViewById(R.id.saveEditText);
        	 
        alert.setTitle(getString(R.string.list_name_display)); 
        alert.setMessage(getString(R.string.give_name)); 
        
        // Set an EditText view to get user input  
        alert.setView(textEntryView); 
               
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
        { 
	        public void onClick(DialogInterface dialog, int whichButton) 
	        { 
	        	iCurrentListName = input.getText().toString();
	        	if(!TextUtils.isEmpty(iCurrentListName) && (iCurrentListName.trim().length()>0))
	        	{
		        	Item i = new Item();
		        			        		        	
		        	ArrayList<Object> arguments = new ArrayList<Object>();
		        	arguments.add(getApplicationContext());
		        	arguments.add(iCurrentListName);
		        	
		        	if(!TextUtils.isEmpty(iItemText.getText().toString()))
		        	{
			        	arguments.add(iItemText.getText().toString());
			        	arguments.add(iQuantityText.getText().toString());
			        	arguments.add(i.isChecked());
		        	}
		        	iShoppingListController.handleMessage(ShoppingListController.MESSAGE_SET_LIST_NAME, 
		        										  arguments);	
	        	}
	        	else
	        	{
	        		ShowGetListNameDialog();
	        		iCurrentListName = "";
	        		Toast.makeText(getApplicationContext(), getString(R.string.give_name), Toast.LENGTH_SHORT).show();
	        	}
	        } 
        });  
        
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
            // Canceled. 
          } 
        }); 
        
        alert.show(); 	
	}
	
	public void ShowEditListNameDialog(final String aListName)
	{
		LayoutInflater factory = LayoutInflater.from(ShoppingListActivity.this);            
        final View textEntryView = factory.inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(ShoppingListActivity.this);
        final EditText inputItem = (EditText) textEntryView.findViewById(R.id.saveEditText);
	        	        	 
        alert.setTitle("Edit List"); 
        inputItem.setText(aListName);
        inputItem.setSelection(aListName.length());
        
        // Set an EditText view to get user input  
        alert.setView(textEntryView); 	
        
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() 
        { 
	        public void onClick(DialogInterface dialog, int whichButton) 
	        { 
	        	String iNewListName = 	inputItem.getText().toString().trim();
	        	if(!TextUtils.isEmpty(iNewListName))
	        	{
		        	ArrayList<Object> arguments = new ArrayList<Object>();
		        	arguments.add(getApplicationContext());
		        	arguments.add(aListName);
		        	arguments.add(iNewListName);
		        	iShoppingListController.handleMessage(ShoppingListController.MESSAGE_EDIT_LIST, 
							arguments);	
	        	}
	        	else
	        	{
	        		ShowEditListNameDialog(aListName);
	        		iNewListName = "";
	        		Toast.makeText(getApplicationContext(), "Enter the List Name", Toast.LENGTH_SHORT).show();
	        	}
	        } 
        });  
        
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
            // Canceled. 
          } 
        }); 
        
        alert.show(); 
	}

	// Callback from the model. 
	public void ModelCallback(final int aMessageId) 
   	{
		switch( aMessageId )
		{
			case ShoppingListModel.MESSAGE_LIST_CREATED:
			{
				updateTitle(iCurrentListName);
				break;
			}
			case ShoppingListModel.MESSAGE_CHECKED_UPDATED:
			{
				break;
			}
			case ShoppingListModel.MESSAGE_ROW_POS_UPDATED:
			{
				break;
			}
			case ShoppingListModel.MESSAGE_LIST_POS_UPDATED:
			{
				break;
			}
		}
	}
	
	// From OnControllerObserver
   	public void ControllerCallback(int aMessageId)
   	{
   		switch(aMessageId)
		{
			case  MESSAGE_GET_LIST_NAME:
			{
				ShowGetListNameDialog();
				break;
			}
			case  MESSAGE_GET_ITEM:
			{	        	
				GetNewItemFromUser();
				break;
			}
			case MESSAGE_EDIT_LIST_NAME:
				ShowEditListNameDialog(iDuplicateListName);
				break;
		}
   	}
   	
   	// From OnControllerObserver
   	public void DisplayMessage(int aMessageId, String aStr)
   	{
   		switch(aMessageId)
		{
			case ShoppingListController.DUPLICATE_LIST:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.duplicate_list), 
						       Toast.LENGTH_SHORT).show();
				iDuplicateListName = aStr;
				
				break;
			}
		}
   	}

	
	public void HandleLists(ArrayList<SavedItem> aListNames)
	{
		iSavedLists.clear();
		iSavedLists.addAll(aListNames);
		
		iSavedListAdapter.notifyDataSetChanged();
	}
	
	public void LoadItemsList(ArrayList<Item> aItemNames)
	{
		iItems.clear();
		iItems.addAll(aItemNames);
		
		iItemAdapter.notifyDataSetChanged();
		iTabHost.setCurrentTab(TabId_ItemList);
		updateTitle(iCurrentListName);
	}
	
	public void deleteItem(final String listItem, int itemPos)
	{
		if( iTabHost.getCurrentTab() ==  TabId_ItemList)
		{
			ArrayList<Object> arguments = new ArrayList<Object>();
			
        	arguments.add(getApplicationContext());
        	arguments.add(iCurrentListName);
        	arguments.add(listItem);
        	arguments.add(itemPos);
        	
			iShoppingListController.handleMessage(ShoppingListController.MESSAGE_DELETE_ITEM, 
													arguments);
		}
		
	}
	
	public void DeleteItemFromList(ArrayList<Object> aListItem)
	{
		ArrayList<Object> arguments = (ArrayList<Object>) aListItem;
		String listItem = (String)arguments.get(0);
		int 	itemPos = (Integer)arguments.get(1);
		
		if(!TextUtils.isEmpty(listItem))
		{	
			for(int i=0;i<iItems.size();i++)
			{			
				Item item = iItems.get(i);				
				String itemName = item.getName().toString();
				
				if(itemName.contentEquals(listItem)&& (itemPos==(i+1)))
				{
					iItems.remove(i);
					iItemAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	
	public void DeleteList(String aList)
	{
		if( iTabHost.getCurrentTab() ==  TabId_SavedList)
		{
			ArrayList<Object> arguments = new ArrayList<Object>();
			
        	arguments.add(getApplicationContext());
        	arguments.add(aList);
        	
			iShoppingListController.handleMessage(ShoppingListController.MESSAGE_DELETE_LIST, 
													arguments);
		}
		
	}
	public void DeleteSavedList(String aListName)
	{
		if(!TextUtils.isEmpty(aListName))
		{	
			for(int i=0;i<iSavedLists.size();i++)
			{	
				SavedItem lName = iSavedLists.get(i);	
				String listName = lName.getName();
								
				if(listName.contentEquals(aListName))
				{
					iSavedLists.remove(i);
					iSavedListAdapter.notifyDataSetChanged();
				}
			}
			if(iCurrentListName.contentEquals(aListName))//list is deleted which is open in first tab 
			{
				iItems.clear();
				iItemAdapter.notifyDataSetChanged();
				
				iCurrentListName = "";
				updateTitle(iCurrentListName);
			}
		}
	}
	private void updateTitle(String aTitle)
	{
		if(!TextUtils.isEmpty(aTitle))
			setTitle(getApplicationContext().getString(R.string.app_name)+" : " + aTitle);
		else
			setTitle(getApplicationContext().getString(R.string.app_name));
	}
	
	private void GetNewItemFromUser()
	{
		// If Item is not entered, do not add an item entry.
		// Force the user to add an item text. 
		String qtyString = iQuantityText.getText().toString();
		
		if( TextUtils.isEmpty(iItemText.getText().toString()) )
		{
			Toast.makeText(getApplicationContext(), getString(R.string.itemname), Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			if(qtyString.length()!=0)
			{
				String[] splitWords =  qtyString.split("\\.");
				if(splitWords.length > 2)
				{
					Toast.makeText(getApplicationContext(),"Invalid Quantity", Toast.LENGTH_SHORT).show();
					iQuantityText.requestFocus();
				}
				else if(splitWords.length==1)
    			{
    				String repString = splitWords[0].replaceFirst("^0+(?!$)", "");
    				qtyString = repString;
    			}
    			else
    			{
    				String repString = splitWords[0].replaceFirst("^0+(?!$)", "");
    				int decimalValue = Integer.parseInt(splitWords[1]);
    				if(decimalValue==0)
    					qtyString = repString;
    				else
    					qtyString = repString+"."+ Integer.toString(decimalValue);
    			}
				
		}
		
		
		Item i = new Item( iItemText.getText().toString(), 
							qtyString.toString());		
		iItems.add(i);
		iItemAdapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), getString(R.string.added_to_list) +" "+ iCurrentListName, Toast.LENGTH_SHORT).show();
		
		// Now reset the editors
		iItemText.setText("");
        iQuantityText.setText("");
		}
	}
	
	public void UpdateCheckBox(Item aItem)
	{
		ArrayList<Object> arguments = new ArrayList<Object>();
		
    	arguments.add(getApplicationContext());
    	arguments.add(iCurrentListName);
    	arguments.add(aItem);
    	
		iShoppingListController.handleMessage(ShoppingListController.MESSAGE_UPDATE_CHECKED, 
												arguments);
	}
	
public Object onRetainNonConfigurationInstance() {
	ArrayList<Object> arguments = new ArrayList<Object>();
	arguments.add(iItems);
	arguments.add(iSavedLists);
	 
    return arguments ;
  }


}

