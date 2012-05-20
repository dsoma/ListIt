// File: ListItActivity.java
// Author: Ramya Machina
// App: ListIt

package com.android.listit;

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

import com.android.listit.controllers.OnControllerObserver;
import com.android.listit.controllers.ListItController;
import com.android.listit.vos.ModelObserver;
import com.android.listit.vos.ListItModel;

public class ListItActivity extends TabActivity 
 						    implements OnTabChangeListener,
 						  		  	   ModelObserver<ListItModel>,
 						  			   OnControllerObserver
{
    private TabHost 				iTabHost;
	private ListItModel 			iListItModel;
	private ListItController 		iListItController;
	private String 					iCurrentListName;
	private TouchListView 			iItemListView;
	private AutoCompleteTextView 	iItemText;
	private EditText 				iQuantityText;
	private TouchListView 			iSavedListView;
	private String 					iDuplicateListName;
	private CheckBox 				iCheckBox;
	
	private ArrayAdapter<SavedItem> 	iSavedListAdapter;
	private ArrayList<SavedItem> 		iSavedLists;
	private String[]		 			iSuggestedWordList;
	private ArrayAdapter<String> 		iSuggestedItemAdapter;
	private ArrayAdapter<Item> 			iItemAdapter;
	private ArrayList<Item> 			iItems; 
	
	public  DialogInterface.OnClickListener	iListNameDialogListener;
	
	/* All tab ids */
	private static final int TabId_ItemList = 0;
	private static final int TabId_SavedList = 1;
	
	@Override
	@SuppressWarnings("unchecked")
    public void onCreate(Bundle aSavedInstanceState) 
    {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.main);
        
        // Create the model and register the observer
        iListItModel = new ListItModel();
        iListItModel.addObserver(this);
        
        // Create a controller and add the model. 
        iListItController = new ListItController(iListItModel);
        
        // Register the view with the controller.
        iListItController.addHandler(this);
        iListItController.setCurrentView(this);
        
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

	/*	Method:		Next editior action listener for landscape mode Item list input
		parameter:	
		returns :	void
	*/
	
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

	/*	Method:		Done editior action listener for landscape mode Item list input
		parameter:	
		returns :	void
	*/
	
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
	
	public void onChange(ListItModel model) 
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
   
	 /*	Method:		Save the data of textview so that it retains its value on activity pause / close
		parameter:	Bundle
		returns :	void
	*/
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
  
   /*	Method:		Restore saved values on application close / orientation change
		parameter:	Bundle
		returns :	void
	*/
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
   	
   /*	Method:		On click listener for Add button
		parameter:	
		returns :	void
	*/
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
	        		Toast.makeText(getApplicationContext(), getString(R.string.add_button_toast), Toast.LENGTH_SHORT).show();
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
		        	
					iListItController.handleMessage(ListItController.MESSAGE_ADD_ITEM, arguments);
					iItemText.requestFocus();
				}
			}
		};
		
        addButton.setOnClickListener(addButtonOnClickListener);
   	}
   	
   /*	Method:		on Click listener for an Item
		parameter:	
		returns :	void
	*/
   	private void SetupItemListView()
   	{
   		// When item is tapped, toggle checked properties of CheckBox and Model.
        iItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
        	public void onItemClick( AdapterView<?> aParent, View aItem, 
                                     int aPosition, long aId) 
        	{
        		Item item = iItemAdapter.getItem( aPosition );
        		ShowEditItemListDialog(item, aPosition+1);
        	}
        });
   	}
   	
	/*	Method:		Shows edit dialog for an item
		parameter:	Item, Item to be edited
					int, position of the item
		returns :	void
	*/
   	private void ShowEditItemListDialog(final Item aItem, final int aRowId)
   	{
   		final String itemName = aItem.getName();
		final String quantity = aItem.getQuantity();
		if(!(TextUtils.isEmpty(itemName)))
		{
			LayoutInflater factory = LayoutInflater.from(ListItActivity.this);            
	        final View textEntryView = factory.inflate(R.layout.edit_dialog, null);

	        AlertDialog.Builder alert = new AlertDialog.Builder(ListItActivity.this);
	        final AutoCompleteTextView inputItem = (AutoCompleteTextView) textEntryView.findViewById(R.id.itemEditText);
	        inputItem.setAdapter(iSuggestedItemAdapter);
	        
	        final EditText inputQty = (EditText) textEntryView.findViewById(R.id.qtyEditText);
	        	 
	        alert.setTitle(getString(R.string.edit_item)); 
	        
	        inputItem.setText(itemName);
	        inputItem.setSelection(itemName.length());
	        inputQty.setText(quantity);
	        
	        // Set an EditText view to get user input  
	        alert.setView(textEntryView); 
	       
	        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
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
			        	Item oldData = new Item(aRowId,itemName, quantity);
			        	arguments.add(oldData);
			        	Item newData = new Item(aRowId, iNewItemName, iNewQty);
			        	arguments.add(newData);
			        	iListItController.handleMessage(ListItController.MESSAGE_EDIT_ITEM, 
								arguments);	
		        	}
		        	else
		        	{
		        		ShowEditItemListDialog(aItem,aRowId);
		        		iNewItemName = "";
		        		iNewQty = "";
		        		Toast.makeText(getApplicationContext(), getString(R.string.add_button_toast), Toast.LENGTH_SHORT).show();
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
  }
	/*	Method:		Item List Change Observer for displaying the last item in the list to the use always
		parameter:
		returns :	void
	*/
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
   	
   	/*	Method:		Drag and drop listener for Item list and List Set
		parameter:
		returns :	void
	*/
   	private void SetupListDrag()
   	{
   		iItemListView.setDropListener(onDrop);
   		iSavedListView.setDropListener(onDrop);
   	}

	/*	Method:		Update the item and List set on position on drag and drop
		parameter:
		returns :	void
	*/
   	
   	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		public void drop(int from, int to) {
			if(from!=to)
			{
				if(iTabHost.getCurrentTab() ==  TabId_ItemList)
				{
					ArrayList<Object> arguments= new ArrayList<Object>();
					arguments.add(getApplicationContext());
		        	arguments.add(iCurrentListName);
					arguments.add(from);
					arguments.add(to);
					
					iListItController.handleMessage(ListItController.MESSAGE_UPDATE_ITEM_POS, 
							arguments);	
					
					/*Item item=iItemAdapter.getItem(from);				
					iItemAdapter.remove(item);
					iItemAdapter.insert(item, to);*/
					
				}
				else
				{
					SavedItem List=iSavedListAdapter.getItem(from);	
					
					ArrayList<Object> arguments= new ArrayList<Object>();
					arguments.add(getApplicationContext());
					arguments.add(List.getName());
					arguments.add(from);
					arguments.add(to);
					
					iListItController.handleMessage(ListItController.MESSAGE_UPDATE_LIST_POS, 
							arguments);	
								
					iSavedListAdapter.remove(List);
					iSavedListAdapter.insert(List, to);
				}
					
			}
		}
	};
	/*	Method:		Update the item List on position change
		parameter:	ArrayList<Item>, new Item List
		returns :	void
	*/
	
	public void UpdateItemPosition(ArrayList<Item> aListItem)
	{
		iItems.clear();
		iItems.addAll(aListItem);
		
		iItemAdapter.notifyDataSetChanged();
	}
	
	/*	Method:		Tap on List set
		parameter:	
		returns :	void
	*/
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
	        	
				iListItController.handleMessage(ListItController.MESSAGE_LOAD_ITEM, 
														arguments);				
							
   			}
   		});
   	}
   	
   	/*	Method:		Long press on any List Set
		parameter:	
		returns :	void
	*/
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
   	
   /*	Method:		New List button click
		parameter:	
		returns :	void
	*/
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
	        	iListItController.handleMessage(ListItController.MESSAGE_ADD_ITEM, 
	        												arguments);					
			}
		};
		newButton.setOnClickListener(newButtonOnClickListener);
        
   	}
	/*	Method:		View modification callback on any item list edit
		parameter:	ArrayList<Item>, Old item and New item deatils.
		returns :	void
	*/
   	public void EditItemList(ArrayList<Item> aItemNames)
	{
   		Item oldItemContent = aItemNames.get(0);
   		Item newItemContent = aItemNames.get(1);
   		
   		String oldItemName = oldItemContent.getName().toString();
		String oldQuantity = oldItemContent.getQuantity().toString();
		int rowId = oldItemContent.getRowId();
		
		String newItemName = newItemContent.getName().toString();
		String newItemQty = newItemContent.getQuantity().toString();
		
		if(!TextUtils.isEmpty(oldItemName) || !TextUtils.isEmpty(newItemName))
		{	
			for(int i=0;i<iItems.size();i++)
			{			
				Item item = iItems.get(i);				
				String itemName = item.getName().toString();
				String quantity = item.getQuantity().toString();
				
				if(itemName.contentEquals(oldItemName)&& quantity.contentEquals(oldQuantity)
					&& ((i+1)==rowId)	)
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
   
	/*	Method:		View modification callback on List set edit
		parameter:	ArrayList<String>, Old list Set and New list set deatils.
		returns :	void
	*/
   public void EditListNames(ArrayList<String> aListNames)
   {
	   String oldListName = aListNames.get(0);
  		String newListName = aListNames.get(1);
  		String date = aListNames.get(2);
		
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
					listName.setDate(date);
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
   	
  	/*	Method:		Called by framework on Tab Change.
		parameter:	String, tab Name.
		returns :	void
	*/
	public void onTabChanged(String aTabName) 
	{	
		if( iTabHost.getCurrentTab() ==  TabId_SavedList )
		{
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(iTabHost.getApplicationWindowToken(), 0);

			iListItController.handleMessage(ListItController.MESSAGE_LOAD_LIST, 
					 							  getApplicationContext());
		}
	}
	
	/*	Method:		Displays List Set name input dialog.
		parameter:	
		returns :	void
	*/
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
		        	iListItController.handleMessage(ListItController.MESSAGE_SET_LIST_NAME, 
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

	/*	Method:		Displays List Set edit dialog.
		parameter:	String, List set Name to be edited.
		returns :	void
	*/
	
	public void ShowEditListNameDialog(final String aListName)
	{
		LayoutInflater factory = LayoutInflater.from(ListItActivity.this);            
        final View textEntryView = factory.inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(ListItActivity.this);
        final EditText inputItem = (EditText) textEntryView.findViewById(R.id.saveEditText);
	        	        	 
        alert.setTitle(getString(R.string.edit_list)); 
        inputItem.setText(aListName);
        inputItem.setSelection(aListName.length());
        
        // Set an EditText view to get user input  
        alert.setView(textEntryView); 	
        
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
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
		        	iListItController.handleMessage(ListItController.MESSAGE_EDIT_LIST, 
							arguments);	
	        	}
	        	else
	        	{
	        		ShowEditListNameDialog(aListName);
	        		iNewListName = "";
	        		Toast.makeText(getApplicationContext(), getString(R.string.new_list_toast), Toast.LENGTH_SHORT).show();
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
 
	/*	Method:		call back function from Model.
		parameter:	int, messageId.
		returns :	void
	*/
	public void ModelCallback(final int aMessageId) 
   	{
		switch( aMessageId )
		{
			case ListItModel.MESSAGE_LIST_CREATED:
			{
				updateTitle(iCurrentListName);
				break;
			}
			case ListItModel.MESSAGE_CHECKED_UPDATED:
			{
				break;
			}
			case ListItModel.MESSAGE_LIST_POS_UPDATED:
			{
				break;
			}
		}
	}
	/*	Method:		call back function from OnControllerObserver.
		parameter:	int, messageId.
		returns :	void
	*/
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

	/*	Method:		Displayes messages from OnControllerObserver.
		parameter:	int, messageId.
					String, Message to be displayed
		returns :	void
	*/
   	public void DisplayMessage(int aMessageId, String aStr)
   	{
   		switch(aMessageId)
		{
			case ListItController.DUPLICATE_LIST:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.duplicate_list), 
						       Toast.LENGTH_SHORT).show();
				iDuplicateListName = aStr;
				
				break;
			}
		}
   	}

	/*	Method:		Loads List Set adapter.
		parameter:	ArrayList<SavedItem>, Lists to be deplayed in the list set.
		returns :	void
	*/
	public void HandleLists(ArrayList<SavedItem> aListNames)
	{
		iSavedLists.clear();
		iSavedLists.addAll(aListNames);
		
		iSavedListAdapter.notifyDataSetChanged();
	}

	/*	Method:		Loads Item list adapter.
		parameter:	ArrayList<Item>, Items to be deplayed in the list.
		returns :	void
	*/
	
	public void LoadItemsList(ArrayList<Item> aItemNames)
	{
		iItems.clear();
		iItems.addAll(aItemNames);
		
		iItemAdapter.notifyDataSetChanged();
		iTabHost.setCurrentTab(TabId_ItemList);
		updateTitle(iCurrentListName);
	}

	/*	Method:		Delete a item from database.
		parameter:	String, Item to be deleted
					int, position of the item
		returns :	void
	*/
	
	public void deleteItem(final String listItem, int itemPos)
	{
		if( iTabHost.getCurrentTab() ==  TabId_ItemList)
		{
			ArrayList<Object> arguments = new ArrayList<Object>();
			
        	arguments.add(getApplicationContext());
        	arguments.add(iCurrentListName);
        	arguments.add(listItem);
        	arguments.add(itemPos);
        	
			iListItController.handleMessage(ListItController.MESSAGE_DELETE_ITEM, 
													arguments);
		}
		
	}

	/*	Method:		Observer callback to delete an item from item list adapter.
		parameter:	ArrayList<Object>, with item to be deleted and position.
		returns :	void
	*/
	
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
	/*	Method:		Delete the list set from database.
		parameter:	String
		returns :	void
	*/
	
	public void DeleteList(String aList)
	{
		if( iTabHost.getCurrentTab() ==  TabId_SavedList)
		{
			ArrayList<Object> arguments = new ArrayList<Object>();
			
        	arguments.add(getApplicationContext());
        	arguments.add(aList);
        	
			iListItController.handleMessage(ListItController.MESSAGE_DELETE_LIST, 
													arguments);
		}
		
	}

	/*	Method:		Observer callback function to update the list set adapter on delete button press.
		parameter:	String
		returns :	void
	*/
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

	/*	Method:		Updates the title Depending on the list created
		parameter:	String
		returns :	void
	*/
	private void updateTitle(String aTitle)
	{
		if(!TextUtils.isEmpty(aTitle))
			setTitle(getApplicationContext().getString(R.string.app_name)+" : " + aTitle);
		else
			setTitle(getApplicationContext().getString(R.string.app_name));
	}

	/*	Method:		Observer callback to get the input from user
		parameter:	NULL
		returns :	void
	*/
	
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
	/* Method to update the checkBox in Item List
		parameter: Item contains the row details checked
		returns : void*/
	
	public void UpdateCheckBox(Item aItem)
	{
		ArrayList<Object> arguments = new ArrayList<Object>();
		
    	arguments.add(getApplicationContext());
    	arguments.add(iCurrentListName);
    	arguments.add(aItem);
    	
		iListItController.handleMessage(ListItController.MESSAGE_UPDATE_CHECKED, 
												arguments);
	}


	
public Object onRetainNonConfigurationInstance() {
	ArrayList<Object> arguments = new ArrayList<Object>();
	arguments.add(iItems);
	arguments.add(iSavedLists);
	 
    return arguments ;
  }


}

