// File: ListItActivity.java
// Author: Ramya Machina
// App: ListIt

package com.android.listit;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.listit.controllers.ListItController;
import com.android.listit.controllers.OnControllerObserver;
import com.android.listit.vos.ListItModel;
import com.android.listit.vos.ModelObserver;

public class ListItActivity extends TabActivity 
 						    implements OnTabChangeListener,
 						  		  	   ModelObserver<ListItModel>,
 						  			   OnControllerObserver
{
    private TabHost 					iTabHost;
	private ListItModel 				iListItModel;
	private ListItController 			iListItController;
	private TouchListView 				iItemListView;
	private AutoCompleteTextView 		iItemText;
	private EditText 					iQuantityText;
	private TouchListView 				iSavedListView;
	private SaveData					iSavedData;
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
	
	/* All Dialog ids */
	public static final int DIALOG_NONE = 0; 
	public static final int DIALOG_EDIT_ITEM = 1; 
	public static final int DIALOG_EDIT_LISTNAME = 2; 
	public static final int DIALOG_NEW_LIST = 3; 
	
	/* All the ids of the messages that the view could display on the screen */
	public static final int DUPLICATE_LIST = 1;
	
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
    	
    	iItemListView  = (TouchListView) findViewById(R.id.itemListView);
    	iSavedListView = (TouchListView) findViewById(R.id.savedListView);
    	
    	iSuggestedWordList = getResources().getStringArray(R.array.suggested_word_list);
        iSuggestedItemAdapter = new ArrayAdapter<String>(this, 
		                                                R.layout.suggested_item_list_view, 
		                                                iSuggestedWordList);
        
        iItemText = (AutoCompleteTextView) findViewById(R.id.editTextItem);
        iItemText.setOnEditorActionListener(new NextOnEditorActionListener());
        iItemText.setAdapter(iSuggestedItemAdapter);
        
        iQuantityText = (EditText) findViewById(R.id.editTextQty);
        iQuantityText.setOnEditorActionListener(new DoneOnEditorActionListener());
        
        // If it is the creation because of non-configuration changes, 
    	// then let us create everything from the beginning. 
    	// Otherwise just reload the saved data. 
    	
        if (getLastNonConfigurationInstance() == null)
        {
        	// Set our custom array adapter as the ListView's adapter.
        	iItems = new ArrayList<Item>();
            iItemAdapter = new ItemListAdapter(this, iItems);
            iItemListView.setAdapter( iItemAdapter );  
            
         	// Set our custom array adapter as the SavedListView's adapter.
        	iSavedLists  = new ArrayList<SavedItem>();
        	iSavedListAdapter = new SavedListAdapter(this, iSavedLists);
        	iSavedListView.setAdapter( iSavedListAdapter ); 
        	
        	iSavedData = new SaveData();
        	iSavedData.iCurrentListName = "";
        }
        else
        {
        	final ArrayList<Object> savedData = (ArrayList<Object>) getLastNonConfigurationInstance();
        	
        	iItems = (ArrayList<Item>) savedData.get(0);
        	iItemAdapter = new ItemListAdapter(this, iItems);
            
            iItemListView.setAdapter( iItemAdapter ); 
        	iItemAdapter.notifyDataSetChanged();
        	
        	iSavedLists = (ArrayList<SavedItem>) savedData.get(1);        	
        	iSavedListAdapter = new SavedListAdapter(this, iSavedLists);
        	
            iSavedListView.setAdapter( iSavedListAdapter ); 
            iSavedListAdapter.notifyDataSetChanged();
            
            iSavedData = (SaveData) savedData.get(2);      
            
            iItemText.setText( iSavedData.iItemEditBoxStr );
            iQuantityText.setText( iSavedData.iQuantityEditBoxStr );
        }   
        
        updateTitle( iSavedData.iCurrentListName );
    }

	/*	Method:		Next editor action listener for landscape mode Item list input
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
	        	arguments.add(iSavedData.iCurrentListName);
	        	
	        	String itemString = iItemText.getText().toString();
	        	String qtyString = iQuantityText.getText().toString().trim(); 
	        	
	        	if( !ValidateItem(itemString) )
	        	{
	        		Toast.makeText(getApplicationContext(), getString(R.string.add_button_toast), Toast.LENGTH_SHORT).show();
	        	}
	        	else if( !ValidateQuantity(qtyString) )
        		{
        			Toast.makeText(getApplicationContext(),getString(R.string.invalid_qty_toast), 
        						   Toast.LENGTH_SHORT).show();
    				iQuantityText.requestFocus();
    				return;
        		}
        		else
        		{
        			qtyString = StripOffZeros(qtyString);
        			
	        		arguments.add(itemString);
		        	arguments.add(qtyString);
		        	arguments.add(false);
		        	arguments.add(iItems.size());
		        	
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
   		iItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
        	public void onItemClick( AdapterView<?> aParent, View aItem, 
                                     int aPosition, long aId) 
        	{
        		Item i = iItemAdapter.getItem( aPosition );
        		
        		if( iSavedData != null ) 
        		{
	        		iSavedData.iCurrentDialogId = DIALOG_EDIT_ITEM;
	        		iSavedData.iCurrentItemPosition = aPosition;
	        		iSavedData.iItemDialogEditStr = i.getDesc();
	        		iSavedData.iQuantityDialogEditStr = i.getQuantity();
        		}
        		
        		showDialog(DIALOG_EDIT_ITEM);
        	}
        });
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
   	
   	protected Dialog onCreateDialog(int aDialogId) 
   	{
   		Dialog dialog;
   		
   		switch( aDialogId )
   		{
   			case DIALOG_EDIT_ITEM: 
   			{
   				dialog = CreateEditItemDialog();
   				break;
   			}
   			case DIALOG_NEW_LIST:
   			{
   				dialog = CreateListNameInputDialog();
   				break;
   			}
   			case DIALOG_EDIT_LISTNAME:
   			{
   				dialog = CreateListNameEditDialog();
   				break;
   			}
	   		default:
	   		{
	   	        dialog = null;
	   	        break;
	   		}
   		}
   		
   		if(dialog != null)
   			dialog.setOwnerActivity(this);
   		
   		return dialog;
   	}
   	
   	protected void onPrepareDialog(int aDialogId, Dialog aDialog)
   	{
   		if( iSavedData == null )
   			return;
   		
   		iSavedData.iCurrentDialogId = aDialogId;
   		iSavedData.LoadDialogData( (AlertDialog) aDialog ); 
		
		if( aDialogId == DIALOG_EDIT_ITEM )
		{
			final AutoCompleteTextView inputItem = (AutoCompleteTextView) aDialog.findViewById(R.id.itemEditText);
			inputItem.setAdapter(iSuggestedItemAdapter);
		}
   	}
   	
   	private Dialog CreateEditItemDialog()
   	{
   		Context mContext = getApplicationContext();
   		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
   		View layout = inflater.inflate(R.layout.edit_dialog,
   		                               (ViewGroup) findViewById(R.id.edit_dialog_root));

   		// Builder will create the dialog with all custom settings and returns. 
   		
   		return new AlertDialog.Builder(ListItActivity.this)
   			   .setView(layout)
   			   .setTitle(getString(R.string.edit_item)) 
   			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
		   		{ 
		   			public void onClick(DialogInterface dialog, int whichButton) 
		   			{ 
						AlertDialog alertDialog = (AlertDialog) dialog;
						final AutoCompleteTextView inputItem = (AutoCompleteTextView) alertDialog.findViewById(R.id.itemEditText);
						final EditText inputQty = (EditText) alertDialog.findViewById(R.id.qtyEditText);
				  
						String iNewItemName = inputItem.getText().toString();
					 	String iNewQty      = inputQty.getText().toString().trim();
					 	boolean validItem   = ValidateItem(iNewItemName);
					 	boolean validQty    = ValidateQuantity(iNewQty);
		 	
					 	// Valid case
					 	if( validItem && validQty )
					 	{
					 		ArrayList<Object> arguments = new ArrayList<Object>();
					      	arguments.add(getApplicationContext());
					      	arguments.add(iSavedData.iCurrentListName);
					      	
					      	Item oldData = iItems.get( iSavedData.iCurrentItemPosition );
					      	arguments.add(oldData);
					      	
					      	iNewQty = StripOffZeros(iNewQty);
					      	
					      	Item newData = new Item( oldData.getRowId(), iNewItemName, iNewQty );
					      	arguments.add(newData);
					      	
					      	iListItController.handleMessage(ListItController.MESSAGE_EDIT_ITEM, 
															arguments);	
					      	iSavedData.ClearDialogData();
					      	return;
					 	}
		 	
					 	// Invalid case.
					 	String  message = "";
					 	
					 	if( !validItem )
					 	{
					 		message = getString(R.string.add_button_toast);
					 	}
					 	else if( !validQty )
					 	{
					 		message = getString(R.string.invalid_qty_toast);
					 	}
			
					 	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
					 	
					 	removeDialog(DIALOG_EDIT_ITEM);
					 	showDialog(DIALOG_EDIT_ITEM);
					}
		   		})
		   		.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
				{ 
					public void onClick(DialogInterface dialog, int whichButton) 
					{ 
						iSavedData.ClearDialogData();
					}
				})
				.create();
   	}
   	
   	/*	Method:		Update the item and List set on position on drag and drop
		parameter:
		returns :	void
	*/
   	private TouchListView.DropListener onDrop = new TouchListView.DropListener() 
   	{
		public void drop(int from, int to) 
		{
			if( from != to )
			{
				if(iTabHost.getCurrentTab() == TabId_ItemList)
				{
					ArrayList<Object> arguments= new ArrayList<Object>();
					arguments.add(getApplicationContext());
		        	arguments.add(iSavedData.iCurrentListName);
					arguments.add(from);
					arguments.add(to);
					
					iListItController.handleMessage(ListItController.MESSAGE_UPDATE_ITEM_POS, 
							arguments);
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
   				if( aPosition < 0 || aPosition >= iSavedLists.size() )
   					return;
   				
   				iSavedData.iCurrentListName = iSavedListAdapter.getItem(aPosition).getName();
   											
				ArrayList<Object> arguments = new ArrayList<Object>();
				
	        	arguments.add(getApplicationContext());
	        	arguments.add(iSavedData.iCurrentListName);
	        	
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
   		iSavedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
   		{
			public boolean onItemLongClick(AdapterView<?> aParent, View aItem,
										   int aPosition, long aId) 
			{
				if( aPosition < 0 || aPosition >= iSavedLists.size() )
   					return false;
				
				SavedItem list = iSavedListAdapter.getItem(aPosition);
				final String listName = list.getName();
   				
				ShowEditListNameDialog(aPosition, listName);
				return true;
			}
		});
   	}
   	
   /*	Method:		New List button click
		parameter:	
		returns :	void
	*/
   	private void SetupNewListButton()
   	{
   		Button newButton = (Button) findViewById(R.id.buttonNew);
   		
        View.OnClickListener newButtonOnClickListener = new OnClickListener() 
        {	
			public void onClick(View v) 
			{
				ArrayList<Object> arguments = new ArrayList<Object>();
				iListItController.handleMessage(ListItController.MESSAGE_ADD_LIST, 
	        									arguments);					
			}
		};
		
		newButton.setOnClickListener(newButtonOnClickListener);
   	}
   	
	/*	Method:		View modification callback on any item list edit
		parameter:	ArrayList<Item>, Old item and New item details.
		returns :	void
	*/
   	public void EditItemList(ArrayList<Item> aItemNames)
	{	
   		Item oldItemContent = aItemNames.get(0);
   		Item newItemContent = aItemNames.get(1);
   		
   		String oldItemName = oldItemContent.getName().toString();
		int position = oldItemContent.getRowId() - 1;
		
		String newItemName = newItemContent.getName().toString();
		String newItemQty = newItemContent.getQuantity().toString();
		
		if( !TextUtils.isEmpty(oldItemName) && 
			!TextUtils.isEmpty(newItemName) && 
			position >= 0 && position < iItems.size() )
		{	
			iItems.get( position ).setName( newItemName );
			iItems.get( position ).setQty( newItemQty );
			iItemAdapter.notifyDataSetChanged();
		}
	}
   
	/*	Method:		View modification callback from the model to edit the list name
		parameter:	ArrayList<String>, Old list Set and New list set details.
		returns :	void
	*/
   public void EditListNames(ArrayList<String> aListNames)
   {
	   if( aListNames.size() <= 0 )
	   {
		   return;
	   }
	   
	    String oldListName = aListNames.get(0);
  		String newListName = aListNames.get(1);
  		String date = aListNames.get(2);
  		
  		int position = iSavedData.iCurrentItemPosition;
		
		if( !TextUtils.isEmpty(oldListName) && 
			!TextUtils.isEmpty(newListName) && 
			position >= 0 && position < iSavedLists.size() )
		{	
			SavedItem listName = iSavedLists.get( position );
			listName.setName(newListName.toString());
			listName.setDate(date);
			iSavedLists.set(position, listName);
				
		}
		if(oldListName.contentEquals(iSavedData.iCurrentListName))
		{
			updateTitle(newListName);
			iSavedData.iCurrentListName = newListName;
		}
		
		iSavedListAdapter.notifyDataSetChanged();
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
		if( iSavedData != null ) 
		{
    		iSavedData.iCurrentDialogId = DIALOG_NEW_LIST;
    		iSavedData.iListNameDialogEditStr = "";
    	}
		
		showDialog(DIALOG_NEW_LIST);
	}
	
	private Dialog CreateListNameInputDialog()
	{
		Context mContext = getApplicationContext();
   		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
   		View layout = inflater.inflate(R.layout.custom_dialog,
   		                               (ViewGroup) findViewById(R.id.root));

   		// Builder will create the dialog with all custom settings and returns. 
   		
   		return new AlertDialog.Builder(ListItActivity.this)
	    .setView(layout)
	    .setTitle(getString(R.string.list_name_display)) 
	    .setMessage(getString(R.string.give_name))
	    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
	    { 
	        public void onClick(DialogInterface dialog, int whichButton) 
	        { 
	        	AlertDialog alertDialog = (AlertDialog) dialog;
	        	final EditText input = (EditText) alertDialog.findViewById(R.id.saveEditText);
	        	
	        	String inputStr = input.getText().toString().trim();
	        	
	        	iSavedData.iListNameDialogEditStr = "";
	        	
	        	if( ValidateItem(inputStr) )
	        	{
	        		ArrayList<Object> arguments = new ArrayList<Object>();
		        	arguments.add(getApplicationContext());
		        	arguments.add(inputStr);
		        	
		        	String qtyTextStr = iQuantityText.getText().toString().trim();
		        	
		        	if( ValidateItem(iItemText.getText().toString()) && 
		        		ValidateQuantity(qtyTextStr) )
		        	{
			        	arguments.add(iItemText.getText().toString());
			        	arguments.add(qtyTextStr);
			        	arguments.add(false);
			        	arguments.add(iItems.size());
		        	}
		        	
		        	// If the list name is found to be duplicate, then we should not 
		        	// clear the data as we need to use them to re-display the dialog. 
		        	
		        	if( iListItController.handleMessage(ListItController.MESSAGE_SET_LIST_NAME, 
		        									arguments) )
		        	{
		        		iSavedData.iCurrentListName = inputStr;
		        		iSavedData.ClearDialogData();
		        	}
	        	}
	        	else
	        	{
	        		// Empty string is input; so re-display the dialog. 
	        		Toast.makeText(getApplicationContext(), getString(R.string.give_name), Toast.LENGTH_SHORT).show();
	        		
	        		removeDialog(DIALOG_NEW_LIST);
	        		showDialog(DIALOG_NEW_LIST);
	        	}
	        } 
	    })
   	    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
   	    { 
   		   public void onClick(DialogInterface dialog, int whichButton) 
   		   { 
   			   iSavedData.ClearDialogData();
   		   } 
   	    })
	    .create();
	}
	
	private Dialog CreateListNameEditDialog()
	{
		Context mContext = getApplicationContext();
   		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
   		View layout = inflater.inflate(R.layout.custom_dialog,
   		                               (ViewGroup) findViewById(R.id.root));

   		// Builder will create the dialog with all custom settings and returns. 
   		
   	    return new AlertDialog.Builder(ListItActivity.this)
	    .setView(layout)
	    .setTitle(getString(R.string.edit_list)) 
	    .setMessage(getString(R.string.give_name))
	    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
	    { 
	        public void onClick(DialogInterface dialog, int whichButton) 
	        { 
	        	AlertDialog alertDialog = (AlertDialog) dialog;
	        	final EditText input = (EditText) alertDialog.findViewById(R.id.saveEditText);
	        	
	        	String newListName = input.getText().toString().trim();
	        	
	        	iSavedData.iListNameDialogEditStr = iSavedData.iEditingListOldName;
        		
	        	if( ValidateItem(newListName) )
	        	{
		        	ArrayList<Object> arguments = new ArrayList<Object>();
		        	arguments.add(getApplicationContext());
		        	arguments.add(iSavedData.iEditingListOldName);
		        	arguments.add(newListName);
		        	
		        	// If the list name is found to be duplicate, then we should not 
		        	// clear the data as we need to use them to re-display the dialog. 
		        	
		        	if( iListItController.handleMessage(ListItController.MESSAGE_EDIT_LIST, 
												    arguments) )	
		        	{
		        		iSavedData.ClearDialogData();
		        	}
	        	}
	        	else
	        	{
	        		// Empty string is input; so re-display the dialog. 
	        		Toast.makeText(getApplicationContext(), getString(R.string.new_list_toast), Toast.LENGTH_SHORT).show();
	        		
	        		removeDialog(DIALOG_EDIT_LISTNAME);
	        		showDialog(DIALOG_EDIT_LISTNAME);
	        	}
	        } 
	    })
   	    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
   	    { 
   		    public void onClick(DialogInterface dialog, int whichButton) 
   		    { 
   			    iSavedData.ClearDialogData();
   		    } 
   	    })
	    .create();
	}

	/*	Method:		Displays List Set edit dialog.
		parameter:	String, List set Name to be edited.
		returns :	void
	*/
	
	public void ShowEditListNameDialog(int aPosition, final String aListName)
	{
		if( iSavedData != null )
		{
			iSavedData.iEditingListOldName = aListName;
			iSavedData.iListNameDialogEditStr = aListName;
			iSavedData.iCurrentDialogId = DIALOG_EDIT_LISTNAME;
			iSavedData.iCurrentItemPosition = aPosition;
		}
		
		showDialog(DIALOG_EDIT_LISTNAME);
	}
	
	/*	Method:		call back function from Model.
		parameter:	int, messageId.
		returns :	void
	*/
	public void ModelCallback(final int aMessageId, final Object aMessageData) 
   	{
		// NOTE: All the callback messages from the model are
		// handled in SimpleObserveable.java. Only some messages
		// which are trivial / do not have anything to be sent as data 
		// are handled here. 
		
		switch( aMessageId )
		{
			case ListItModel.MESSAGE_LIST_CREATED:
			{
				if(aMessageData != null)
					updateTitle((String) aMessageData);
				else
					updateTitle("");
				
				break;
			}
			case ListItModel.MESSAGE_LIST_POS_UPDATED:
			{
				break;
			}
		}
	}
	
	/*	Method:		Call back function from OnControllerObserver.
		parameter:	int, messageId.
		returns :	void
	*/
   	public void ControllerCallback(int aMessageId, Object aData)
   	{
   		switch(aMessageId)
		{
			case MESSAGE_GET_LIST_NAME:
			{
				ShowGetListNameDialog();
				break;
			}
			case MESSAGE_ADD_ITEM_TO_VIEW:
			{
				if( aData != null )
					AddItemInView( (Item) aData );
				
				break;
			}
			case MESSAGE_EDIT_LIST_NAME:
			{
				ShowEditListNameDialog(iSavedData.iCurrentItemPosition, 
						               iSavedListAdapter.getItem(iSavedData.iCurrentItemPosition).getName());
				break;
			}
			case MESSAGE_HANDLE_DUPLICATE_LIST_NAME:
			{
				// Duplicate name is input, so re-display the dialog. 
				DisplayMessage(DUPLICATE_LIST);
        		removeDialog(DIALOG_NEW_LIST);
        		showDialog(DIALOG_NEW_LIST);
				break;
			}
			case MESSAGE_HANDLE_DUPLICATE_EDIT_LIST:
			{
				// Duplicate name is input, so re-display the dialog. 
				DisplayMessage(DUPLICATE_LIST);
        		removeDialog(DIALOG_EDIT_LISTNAME);
        		showDialog(DIALOG_EDIT_LISTNAME);
				break;
			}
			case MESSAGE_CLEAR_LIST:
			{
				if( iItems.size() > 0 )
				{
					iItems.clear();
					iItemAdapter.notifyDataSetChanged();
				}
				break;
			}
			case MESSAGE_UPDATE_TITLE:
			{
				if( aData != null )
					updateTitle( (String) aData );
				break;
			}
		}
   	}
   	
   	public void UpdateItemChecked(Item aItem)
   	{
   		iItems.get( aItem.getRowId() - 1 ).setChecked( aItem.isChecked() );
		iItemAdapter.notifyDataSetChanged();
   	}

	/*	Method:		Displays messages from OnControllerObserver.
		parameter:	int, messageId.
					String, Message to be displayed
		returns :	void
	*/
   	public void DisplayMessage(int aMessageId)
   	{
   		switch(aMessageId)
		{
			case DUPLICATE_LIST:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.duplicate_list), 
						       Toast.LENGTH_SHORT).show();
				break;
			}
		}
   	}

	/*	Method:		Loads List Set adapter.
		parameter:	ArrayList<SavedItem>, Lists to be displayed in the list set.
		returns :	void
	*/
	public void HandleLists(ArrayList<SavedItem> aListNames)
	{
		iSavedLists.clear();
		iSavedLists.addAll(aListNames);
		
		iSavedListAdapter.notifyDataSetChanged();
	}

	/*	Method:		Loads Item list adapter.
		parameter:	ArrayList<Item>, Items to be displayed in the list.
		returns :	void
	*/
	
	public void LoadItemsList(ArrayList<Item> aItemNames)
	{
		iItems.clear();
		iItems.addAll(aItemNames);
		
		iItemAdapter.notifyDataSetChanged();
		iTabHost.setCurrentTab(TabId_ItemList);
		updateTitle(iSavedData.iCurrentListName);
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
        	arguments.add(iSavedData.iCurrentListName);
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
			//list is deleted which is open in first tab 
			if(iSavedData.iCurrentListName.contentEquals(aListName)) 
			{
				iItems.clear();
				iItemAdapter.notifyDataSetChanged();
				
				iSavedData.iCurrentListName = "";
				updateTitle(iSavedData.iCurrentListName);
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
			this.setTitle(getApplicationContext().getString(R.string.app_name)+" : " + aTitle);
		else
			setTitle(getApplicationContext().getString(R.string.app_name));
	}

	public void AddItemInView(Item aNewItem)
	{
		iItems.add(aNewItem);
		iItemAdapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), getString(R.string.added_to_list) /*+" "+ iSavedData.iCurrentListName*/, 
			           Toast.LENGTH_SHORT).show();
		
		// Now reset the editors
		iItemText.setText("");
        iQuantityText.setText(""); 
	}

	/* Method to update the checkBox in Item List
		parameter: Item contains the row details checked
		returns : void*/
	
	public void UpdateCheckBox(Item aItem)
	{
		ArrayList<Object> arguments = new ArrayList<Object>();
		
    	arguments.add(getApplicationContext());
    	arguments.add(iSavedData.iCurrentListName);
    	arguments.add(aItem);
    	
		iListItController.handleMessage(ListItController.MESSAGE_UPDATE_CHECKED, 
										arguments);
	}
	
	public Object onRetainNonConfigurationInstance() 
	{
		final ArrayList<Object> savedData = new ArrayList<Object>();
		savedData.add( iItems );
		savedData.add( iSavedLists );
		
		if( iSavedData != null )
		{
			iSavedData.iItemEditBoxStr = ((AutoCompleteTextView) findViewById(R.id.editTextItem)).getText().toString();
			iSavedData.iQuantityEditBoxStr = ((EditText) findViewById(R.id.editTextQty)).getText().toString();
			iSavedData.SaveDialogData();
		
			savedData.add( iSavedData );
		}
	    return savedData;
	}
	
	private String StripOffZeros( String aValue ) 
	{
		if( aValue.length() > 0 )
		{
			Double d = Double.parseDouble(aValue);
			String s = Double.toString( d );
			if( s.contains("E") )
			{
				s = ConvertExponentDoubleToString(s);
			}
			return s;
		}
		
		return aValue;
	}
	
	private boolean ValidateQuantity( String aQuantityStr )
	{
		if( aQuantityStr.length() > 0 )
		{
			// If the number contains more than 2 dots, then invalid. 
			String[] splitWords =  aQuantityStr.split("\\.");
			
			if(splitWords.length > 2)
			{
				return false;
			}
			
			double dValue = 0;
			
			// Parse the double, if invalid, catch the exception and return invalid. 
			try {
				dValue = Double.parseDouble(aQuantityStr);
			}
			catch(Exception e) {
				return false;
			}
			
			// If -ve, then it doesn't make sense in our case, so invalid. 
			if( dValue < 0 )
			{
				return false;
			}
		}
		
		return true;
	}
	
	private boolean ValidateItem(String aItemText)
	{
		if( TextUtils.isEmpty(aItemText.trim()) )
			return false;
		return true;
	}
	
	private String ConvertExponentDoubleToString(String aNumber)
	{
		String resultStr = "";
		
		String[] parts = aNumber.split("E");
		String[] leftParts = (parts[0]).split("\\.");
		String num = "";
		if( leftParts.length > 0 )
			num = leftParts[0] + leftParts[1];
		
		Integer exp = Integer.parseInt( parts[1] );
		int s = Integer.signum( exp.intValue() );
		if( s < 0 ) 
		{
			resultStr = "0.";
			exp = exp * -1;
			for(int i = 1; i < exp.intValue(); i++)
			{
				resultStr += "0";
			}
			resultStr += num;
		}
		
		return resultStr;
	}
}


