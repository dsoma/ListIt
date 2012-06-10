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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
//import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
 						  			   OnControllerObserver, 
									   TextWatcher
 						  			   
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
	//private String[]		 			iSuggestedWordList;
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
	public static final int DIALOG_DELETE_LIST = 4;
	
	/* All the ids of the messages that the view could display on the screen */
	public static final int DUPLICATE_LIST = 1;
	public static final int ENTER_ITEM = 2;
	public static final int ADD_TO_LIST = 3;
	public static final int INVALID_QUANTITY = 4;
	public static final int NAME_A_LIST = 5;
	public static final int LIST_NAME = 6;
	public static final int NONE = 7;
	
	/* I know, we shouldn't create it here. I have a replica of this in strings.xml. 
	 * However, specifying such a big list in strings.xml is not working on some old phones. 
	 * Hence going for this option. We are not supporting any other languages other than English. 
	 * So it should be okay as of now. 
	 */
	public static final String[] iSuggestedWordList = 
	{
		"Air Freshener",
		"Ajwain",
        "All Purpose Flour",
        "Almonds",
        "Almonds Sliced",
        "Aluminum Foil",
        "Amaranth",
        "Amchur Powder",
        "Anise",
        "Apple",
        "Apple Cider Vinegar",
        "Applesauce",
        "Apricot",
        "Artichoke",
        "Arugula",
        "Asafoetida",
        "Asparagus", 
        "Avocado",
        "Baby Food", 
        "Bacon",
        "Bacon Sausage",
        "Bag",
        "Bagel",
        "Baking Powder",
        "Baking Soda",
        "Balsamic Vinegar",
        "Bamboo Shoots",
        "Banana", 
        "Banana Chips",
        "Bangle",
        "Bar Soaps",
        "Barley",
        "Basil",
        "Basmati Rice",
        "Bathroom Tissues",
        "Batteries",
        "Bay leaf",
        "BBQ Sauce", 
        "Beans",
        "Beef",
        "Beer",
        "Beets",
        "Bell Pepper",
        "Belt",
        "Berries",
        "Besan",
        "Biriyani Masala",
        "Bisi Bele Baath Masala",
        "Black beans",
        "Blackberry",
        "Black-eyed Peas",
        "Black Pepper", 
        "Black Salt",
        "Bleach",
        "Bleu Cheese",
        "Blueberry",
        "Blush",
        "Blush Brush",
        "Bok Choy",
        "Body Lotion",
        "Body Spray",
        "Body Wash",
        "Bouillon cubes",
        "Bottle",
        "Bracelet",
        "Brazil Nuts",
        "Bread", 
        "Bread Crumbs",   
        "Bread Toast",   
        "Broccoli",
        "Broth",
        "Brussels Sprouts",
        "Brownie",
        "Brownie Mix",
        "Brown Rice",
        "Buckwheat",
        "Bulgar",
        "Bun",
        "Burger",
        "Burrito",
        "Butter",
        "Cabbage",
        "Cake",
        "Cake Icing",
        "Cake Mix",
        "Candles",
        "Candy",
        "Canola Oil",
        "Cantelope",
        "Cardamom",
        "Cardamom Powder",
        "Carrot",
        "Cashews",
        "Cashew Roasted",
        "Cassava",
        "Catfish",
        "Cat Food",
        "Cauliflower",
        "Cayenne Pepper",
        "Celery",
        "Celery Seed",
        "Cereal",
        "Cereal Bar",
        "Chaat Masala",
        "Chai",
        "Chain",
        "Chakli",
        "Champagne",
        "Channa Dal",
        "Chard",
        "Cheddar Cheese",
        "Cheese",
        "Cherries",
        "Chestnut",
        "Chicken",
        "Chicken Breast",
        "Chickpeas",
        "Chickpea Flour",
        "Chilli",
        "Chilli Powder",
        "Chip dip",
        "Chives",
        "Chocolate Chips",
        "Cilantro",
        "Cinnamon",
        "Cleanser",
        "Clove",
        "Club soda",
        "Cocoa",
        "Coconut",
        "Coconut Chutney",
        "Coconut Flakes",
        "Coconut Milk",
        "Coconut Oil",
        "Coffee",
        "Coffee Filter",
        "Collard greens",
        "Comb",
        "Concealer",
        "Concealer Brush",
        "Conditioner",
        "Cookies",
        "Coriander",
        "Coriander Chutney",
        "Coriander Seeds",
        "Coriander Powder",
        "Corn",
        "Corn Chips",
        "Corn Flour",
        "Cornmeal",
        "Cosmetic Set",
        "Cosmetic Case",
        "Cosmetic Pencils",
        "Cottage Cheese",
        "Couscous",
        "Crab",
        "Crackers",
        "Cranberries",
        "Cream Cheese",
        "Croissant",
        "Cucumber",
        "Cumin Seeds",
        "Cumin Powder",
        "Curry Leaf",
        "Curry Powder",
        "Daikon",
        "Dates",
        "Diapers",
        "Dill",
        "Dishwasher Detergent",
        "Dog Food",
        "Donut",
        "Dosa Instant Mix",
        "Drum Stick",
        "Dry Fruit",
        "Earrings",
        "Edamame",
        "Egg",
        "Eggplant",
        "Electrol",
        "Endive",
        "English Muffin",
        "Eyelash Curler",
        "Eyelash Brush",
        "Eyeliner",
        "Eye Lashes",
        "Eye Pencil",
        "Eye Shadow",
        "Fabric Refresher",
        "Fabric Softener",
        "Face Wash",
        "Facial Tissue",
        "Fava Beans",
        "Fennel",
        "Fennel Seeds",
        "Fenugreek Seeds",
        "Feta Cheese",
        "Figs",
        "Fish",
        "Fish Sticks",
        "Flax Seeds",
        "Flour",
        "Food Color - Red",
        "Food Color - Yellow",
        "Food Color - Orange",
        "Foundation Makeup",
        "Foundation Sponge",
        "Fries",
        "Fruits",
        "Fruit Juice",
        "Garam Masala",
        "Garlic",
        "Garlic Paste",
        "Gel",
        "Ghee",
        "Gin",
        "Ginger",
        "Ginger Paste",
        "Ginger and Garlic Paste",
        "Gloves",
        "Glucose",
        "Goat Cheese",
        "Granola Bar",
        "Granola Mix",
        "Grapefruit",
        "Grapes",
        "Gravy",
        "Greek Yogurt",
        "Green Chillies",
        "Ground Beef",
        "Groundnut",
        "Guava",
        "Gulab Jamoon Mix",
        "Gum",
        "Hair Clip",
        "Hair Color",
        "Hair Dryer",
        "Hair Oil",
        "Hair Spray",
        "Half and Half",
        "Ham",
        "Hand Bag",
        "Hand Lotion",
        "Hand Soap",
        "Harissa",
        "Hat",
        "Hing",
        "Honey",
        "Honeydew Melon",
        "Hot Dogs",
        "Hot Peppers",
        "Hot Sauce",
        "Instant Coffee Powder",
        "Ice Cream",
        "Jackfruit",
        "Jaggery",
        "Jaljeera Masala",
        "Jam",
        "Jasmin Rice",
        "Jelly",
        "Jicama",
        "Juice",
        "Jewelry",
        "Kabuli Chana",
        "Kala Jeera",
        "Kale",
        "Ketchup",
        "Kheer Mix",
        "Kidney Beans",
        "Kiwi",
        "Kohlrabi",
        "Kumquats",
        "Laundry Detergent",
        "Lavender",
        "Leeks",
        "Lemon",
        "Lemongrass",
        "Lentils",
        "Lettuce",
        "Light Bulbs",
        "Lima Beans",
        "Lime",
        "Lip Balm",
        "Lipgloss",
        "Lipstick",
        "Lip Pencil",
        "Lip Shine",
        "Liquid Dish Detergent",
        "Lychee",
        "Lobster",        
        "Macadamia",
        "Maida",
        "Makeup Brushes",
        "Makeup Palette",
        "Makeup Kit",
        "Mango",
        "Manicure Set",
        "Maple Syrup",
        "Marjoram",
        "Margarine",
        "Massoor Dal",
        "Mascara",
        "Mayonnaise",
        "Melon",
        "Methi",
        "Millet",
        "Milk",
        "Mint",
        "Mint Chutney",
        "Mirror",
        "Moisturizer",
        "Moisture Cream",
        "Moong Beans",
        "Moong Dal",
        "Mozarella Cheese",
        "Murukku",
        "Mushroom",
        "Muskmelon",
        "Mussels",
        "Mustard",
        "Mustard Oil",
        "Nail Color",
        "Nail Color Remover",
        "Nail Polish",
        "Nail Polish Remover",
        "Napkins",
        "Navy Beans",
        "Necklace",
        "Nectarine",
        "Nopale",
        "Nutmeg",
        "Nuts",
        "Oatmeal",
        "Okra",
        "Olives",
        "Olive Oil",
        "Onion",
        "Orange",
        "Oregano",
        "Oysters",
        "Packaged Meal",
        "Pancake",
        "Paneer",
        "Panipuri Masala",
        "Papad",
        "Papaya",
        "Papper Towels",
        "Paprika",
        "Parmesan Cheese",
        "Parsley",
        "Parsnip",
        "Pasta",
        "Pasta Sauce",
        "Pastries",
        "Pav Bhaji Masala",
        "Peach",
        "Peanuts",
        "Peanut Butter",
        "Pear",
        "Peas",
        "Pecans",
        "Pencil Sharpener",
        "Pepper",
        "Peppermint",
        "Perfume",
        "Pendant",
        "Pickle",
        "Pineapple",
        "Pine Nuts",
        "Pinto Beans",
        "Pita Bread",
        "Pistachios",
        "Pizza",
        "Plantains",
        "Plastic Wraps",
        "Plum",
        "Poha",
        "Pomegranate",
        "Ponzu",
        "Popcorn",
        "Poppy Seed",
        "Popsicle",        
        "Pork",
        "Potato",
        "Potato Chips",
        "Pouch",
        "Powder",
        "Powder Brush",
        "Pretzels",
        "Primer",
        "Protein Bar",
        "Provolone Cheese",
        "Pulav Masala",
        "Puliyogare Mix",
        "Puliyogare Paste",
        "Pumpkin",
        "Pumpkin Seeds",
        "Purse",
        "Quince",
        "Quinoa",
        "Radish",
        "Radicchio",
        "Ragi Flour",
        "Rasam Powder",
        "Rajma",
        "Raspberries",
        "Rava",
        "Rava Idli Mix",
        "Razor",
        "Red Beans",
        "Red Chilli",
        "Kabuli Chilli Powder",
        "Red Pepper",
        "Red Wine",
        "Red Wine Vinegar",
        "Relish",
        "Rhubarb",
        "Ribbons",
        "Rice",
        "Rice Milk",
        "Rice Vinegar",
        "Ricotta Cheese",
        "Rosemary",
        "Rum",
        "Rusk",
        "Sabudaana",
        "Saffron",
        "Sake",
        "Salad Dressing",
        "Salmon",
        "Salsa",
        "Salt",
        "Sambar Powder",
        "Samosa",
        "Samosa Chutney",
        "Scissors",
        "Scrub Sponges",
        "Seeds",
        "Sesame",
        "Sesame Oil",
        "Shadow Palette",
        "Shallots",
        "Shampoo",
        "Shaving Kit",
        "Shoes",
        "Shrimp",
        "Shredded Coconut",
        "Sliced Bread",
        "Socks",
        "Soda",
        "Soda pop",
        "Sonamassori Rice",
        "Sooji Flour",
        "Sorbet",
        "Sorghum",
        "Soy Creamer",
        "Soy Butter",
        "Soy Flour",
        "Soy Margarine",
        "Soy Milk",
        "Soy Sauce",
        "Soy Yogurt",
        "Soup",
        "Sour Cream",
        "Spaghetti",
        "Spearmint",
        "Spinach",
        "Split Peas",
        "Sprouts",
        "Squash",
        "Steak Sauce",
        "Straightener",
        "Strawberries",
        "Storage Bags",
        "Sugar",
        "Sunglasses",
        "Sunscreen Lotion",
        "Sweet Potato",
        "Swiss Cheese",
        "Syrup",
        "Taco",
        "Tahini",
        "Tamarind",
        "Tandoori Chicken Mix",
        "Tapioca",
        "Taro",
        "Tarragon",
        "Tea",
        "Tea Bags",
        "Tempeh",
        "Thyme",
        "Tilapia",
        "Tinned Meats",
        "Tofu",
        "Tomatillo",
        "Tomato",
        "Tomato Sauce",
        "Toner",
        "Toor Dal",
        "Tortilla",
        "Toothbrush",
        "Toothpaste",
        "Toys",
        "Trail Mix",
        "Trash Bags",
        "Tuna",
        "Turkey",
        "Turmeric",
        "Turnips",
        "Tweezer",
        "Upma Mix",
        "Urad Dal",
        "Vada Mix",
        "Vangibaath Mix",
        "Vanilla",
        "Vanilla Extract",
        "Vegetables",
        "Vegetable Oil",
        "Vinegar",
        "Vodka",
        "Waffle",
        "Walnuts",
        "Wallets",
        "Watch",
        "Water",
        "Watermelon",
        "Wheat Flour",
        "Whey Protein",
        "Whip Cream",
        "Whiskey",
        "White Beans",
        "White Rice",
        "White Wine",
        "Wine",
        "Worcestershire Sauce",
        "Yeast",
        "Yogurt",
        "Zucchini"
	};
	
	/*	Method:		onCreate method of an activity
		parameter:	
		returns :	void
	 */
	
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
    	
    	//iSuggestedWordList = getResources().getStringArray(R.array.suggested_word_list);
        iSuggestedItemAdapter = new ArrayAdapter<String>(this, 
		                                                R.layout.suggested_item_list_view, 
		                                                iSuggestedWordList);
        
        iItemText = (AutoCompleteTextView) findViewById(R.id.editTextItem);
        iItemText.setOnEditorActionListener(new NextOnEditorActionListener());
        iItemText.setAdapter(iSuggestedItemAdapter);
        
        iQuantityText = (EditText) findViewById(R.id.editTextQty);
        iQuantityText.setOnEditorActionListener(new DoneOnEditorActionListener());
 		iQuantityText.addTextChangedListener(this);
        
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
        	
        	// If there are saved lists, then show the list view. 
        	// Otherwise, just open the empty list view. 
        	if( GetListCount() > 0 )
        	{
        		iTabHost.setCurrentTab(TabId_SavedList);
        	}
        	else
        	{
        		iTabHost.setCurrentTab(TabId_ItemList);
        	}
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
        
        /*DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);*/
        
        return;
    }

	
	
	public void onChange(ListItModel model) 
   	{
	
	}
	/*	Method:		onStart method of an activity 
	 				called when the activity is about to become visible
		parameter:	
		returns :	void
	 */
   	
   	protected void onStart() 
   	{
    	super.onStart();
    }
   	/*	Method:		onResume method of an activity
   	 				called when the activity has become visible
		parameter:	
		returns :	void
   	 */
   	
   	public void onResume()
   	{
   		super.onResume();
   		
   		SetupAddButton();
   		SetupListDrag();
   		SetupNewListButton();
   	}
   	
   	private int GetListCount()
   	{
   		if( iListItController == null )
   			return 0;
   		
   		return iListItController.GetListCount(getApplicationContext());
   	}
   	   
    /*	Method:		Handles Add button click event.
					Checks for emptiness of an item edit Text and validates quantity.
					If everything OK, updates the database.Otherwise, display toasts
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
	        		DisplayMessage(ENTER_ITEM);
	        		//Toast.makeText(getApplicationContext(), getString(R.string.add_button_toast), Toast.LENGTH_SHORT).show();
	        	}
	        	else if( !ValidateQuantity(qtyString) )
        		{
	        		DisplayMessage(INVALID_QUANTITY);
        			//Toast.makeText(getApplicationContext(),getString(R.string.invalid_qty_toast), 
        			//			   Toast.LENGTH_SHORT).show();
    				iQuantityText.requestFocus();
    				return;
        		}
        		else
        		{
        			qtyString = StripOffZeros(qtyString);
        			
	        		arguments.add(itemString);
		        	arguments.add(qtyString);
		        	arguments.add(false);
		        	
		        	// Item hasn't yet been added to the list. 
		        	// So size() would return one less. 
		        	arguments.add(iItems.size() + 1);
		        	
					iListItController.handleMessage(ListItController.MESSAGE_ADD_ITEM, arguments);
					iItemText.requestFocus();
				}
			}
		};
		
        addButton.setOnClickListener(addButtonOnClickListener);
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
	
	/*	Method:		Handles click event on any List in a List Set.
					Opens the list of items in a list reading from database.
		parameter:	
		returns :	void
	*/
   	public void onSavedListClick( String aName )
   	{
   		if( TextUtils.isEmpty(aName) || 
   	   		iSavedLists == null || iSavedLists.isEmpty() )
   		{
   			return;
   		}
   		
   		int position = getListPosition(aName);
   		
		if( position < 0 || position >= iSavedLists.size() || iSavedData == null )
		{
			return;
		}
		
		iSavedData.iCurrentListName = iSavedLists.get(position).getName();
									
		ArrayList<Object> arguments = new ArrayList<Object>();
	
		arguments.add(getApplicationContext());
		arguments.add(iSavedData.iCurrentListName);
	
		iListItController.handleMessage(ListItController.MESSAGE_LOAD_ITEM, 
										arguments);	
	}
   	
   	/*	Method:		Handles long press event on any List in a List Set.
					Edits a list displaying dialog.
					Updates the database with new edited name.
		parameter:	
		returns :	void
	*/
   	public boolean onSavedListLongClick(String aName) 
	{
   		if( TextUtils.isEmpty(aName) || 
   			iSavedLists == null || iSavedLists.isEmpty() )
   		{
   			return false;
   		}
   		
   		int position = getListPosition(aName);
   		
		if( position < 0 || position >= iSavedLists.size() )
			return false;
		
		SavedItem list = iSavedLists.get(position);
		
		if( list == null )
			return false;
		
		final String listName = list.getName();
		
		ShowEditListNameDialog(position, listName);
		
		return true;
	}
   	
   	/*	Method:		Handles click event on any item in a List.
					Opens a dialog to edit the item.
		parameter:	
		returns :	void
   	*/

   	public void onItemClick( int aPosition )
   	{
   		if( aPosition < 0 || aPosition >= iItems.size() )
   			return;

   		Item i = iItemAdapter.getItem(aPosition);

   		if( iSavedData != null ) 
   		{
   			iSavedData.iCurrentDialogId = DIALOG_EDIT_ITEM;
   			iSavedData.iCurrentItemPosition = aPosition;
   			iSavedData.iItemDialogEditStr = i.getDesc();
   			iSavedData.iQuantityDialogEditStr = i.getQuantity();
   		}

		showDialog(DIALOG_EDIT_ITEM);
   	}
   	
    /*	Method:		Handles click event on New List button.
					Creates a new List for adding items.
					
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
   	
	/*	Method:		Callback on editing any item in a list.
					Model callback to modify the View on database modification.
		parameter:	ArrayList<Item>, Old item and New item details.
		returns :	void
	*/
   	public void EditItemList(ArrayList<Item> aItemNames)
	{	
   		if( aItemNames == null || aItemNames.size() <= 0 )
   			return;
   		
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
   
	/*	Method:		Callback on editing a list from List Set.
					Model callback to modify the View on database modification.
		parameter:	ArrayList<String>, Old list Set and New list set details.
		returns :	void
	*/
   public void EditListNames(ArrayList<String> aListNames)
   {
	   if( aListNames.size() <= 0 || iSavedData == null )
	   {
		   return;
	   }
	   
	    String oldListName = aListNames.get(0);
  		String newListName = aListNames.get(1);
  		String date = aListNames.get(2);
  		
  		int position = iSavedData.iCurrentItemPosition;
		
		if( !TextUtils.isEmpty(oldListName) && 
			!TextUtils.isEmpty(newListName) && 
			iSavedLists != null &&
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
   	
  	/*	Method:		Called by Android framework on Tab Change.
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
	
	/*	Method:		Displays Dialog on creation of New List.
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
	
	/*	Method:		Handels edit dialog for edition of a list name..
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
	
	/*	Method:		Callback for creating dialogs.
					Invoked when showDialog() is called. 
		parameter:
		returns :	Dialog
	*/
   	
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
   			case DIALOG_DELETE_LIST:
   			{
   				dialog = CreateDeleteListDialog();
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
	
	/*	Method:		Provides an opportunity to prepare a managed dialog before it is being shown.
		parameter:
		returns :	void
	*/
   	
   	protected void onPrepareDialog(int aDialogId, Dialog aDialog)
   	{
   		if( iSavedData == null )
   			return;
   		
   		if( aDialogId == DIALOG_EDIT_ITEM )
		{
			final AutoCompleteTextView inputItem = (AutoCompleteTextView) aDialog.findViewById(R.id.itemEditText);
			inputItem.setAdapter(iSuggestedItemAdapter);
			
			EditText qtyTextView = (EditText) aDialog.findViewById(R.id.qtyEditText);
			qtyTextView.addTextChangedListener(this);
		}
   		
   		iSavedData.iCurrentDialogId = aDialogId;
   		iSavedData.LoadDialogData( (AlertDialog) aDialog );
   	}
	
	/*	Method:		Invokes custom dialog for editing an Item.
					Called from CreateDialog().
					Updates the database with new edited item.
		parameter:
		returns :	void
	*/
   	
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
					      	
					      	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					      	imm.hideSoftInputFromWindow(inputItem.getWindowToken(), 0);
					      	
					      	return;
					 	}
		 	
					 	// Invalid case.
					 	int messageId = (!validItem) ? ENTER_ITEM : (( !validQty ) ? INVALID_QUANTITY : NONE);
					 	DisplayMessage(messageId);
					 	
					 	removeDialog(DIALOG_EDIT_ITEM);
					 	showDialog(DIALOG_EDIT_ITEM);
					}
		   		})
		   		.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
				{ 
					public void onClick(DialogInterface dialog, int whichButton) 
					{ 
						iSavedData.ClearDialogData();
						
						AlertDialog alertDialog = (AlertDialog) dialog;
						final AutoCompleteTextView inputItem = (AutoCompleteTextView) alertDialog.findViewById(R.id.itemEditText);
						
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				      	imm.hideSoftInputFromWindow(inputItem.getWindowToken(), 0);
					}
				})
				.create();
   	}
	
	/*	Method:		Invokes custom dialog for creation of a new list.
					Called from CreateDialog().
					Updates the database with newly created list name.
		parameter:
		returns :	void
	*/
	
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
			        	
			        	// Item hasn't yet been added to the list. 
			        	// So size() would return one less. 
			        	arguments.add(iItems.size() + 1);
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
	        		DisplayMessage(NAME_A_LIST);
	        		//Toast.makeText(getApplicationContext(), getString(R.string.give_name), Toast.LENGTH_SHORT).show();
	        		
	        		removeDialog(DIALOG_NEW_LIST);
	        		showDialog(DIALOG_NEW_LIST);
	        	}
	        	
	        	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		      	imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	        } 
	    })
   	    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
   	    { 
   		   public void onClick(DialogInterface dialog, int whichButton) 
   		   { 
   			    iSavedData.ClearDialogData();
   			   
	   			AlertDialog alertDialog = (AlertDialog) dialog;
	        	final EditText input = (EditText) alertDialog.findViewById(R.id.saveEditText);
        	
	   		    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
   		   } 
   	    })
	    .create();
	}
	
	/*	Method:		Invokes custom dialog for editing a list from list set.
					Called from CreateDialog().
		
	*/
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
		        	arguments.add(iSavedData.iCurrentItemPosition);
		        	
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
	        		DisplayMessage(LIST_NAME);
	        		
	        		removeDialog(DIALOG_EDIT_LISTNAME);
	        		showDialog(DIALOG_EDIT_LISTNAME);
	        	}
	        	
	        	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	        } 
	    })
   	    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
   	    { 
   		    public void onClick(DialogInterface dialog, int whichButton) 
   		    { 
   			    iSavedData.ClearDialogData();
   			    
   			    AlertDialog alertDialog = (AlertDialog) dialog;
	        	final EditText input = (EditText) alertDialog.findViewById(R.id.saveEditText);
     	
	   		    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
   		    } 
   	    })
	    .create();
	}
	
	private Dialog CreateDeleteListDialog()
	{
		// Builder will create the dialog with all custom settings and returns. 
   		
   	    return new AlertDialog.Builder(ListItActivity.this)
	    .setTitle(getString(R.string.delete)) 
	    .setMessage(getString(R.string.sure))
        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() 
        { 
        	public void onClick(DialogInterface dialog, int whichButton) 
        	{
        		Context c = getApplicationContext();
        		DeleteList(iSavedData.iCurrentItemPosition);   	
        		Toast.makeText(c, getString(R.string.deleted), Toast.LENGTH_SHORT).show(); 
        		iSavedData.ClearDialogData();
        	}	 
        }) 
	    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() 
	    { 
	        public void onClick(DialogInterface dialog, int whichButton) 
	        { 
	        	iSavedData.ClearDialogData();
	        } 
	    })
	    .create();
	}
	
	/*	Method:		Drop listener for an Item or a list.
					Updates the database with new position of an item / list
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
				}	
			}
		}
	};
	
	/*	Method:		Handles the checkbox click.
					Invoked by the view adapter since the click event is captured in the view.
					Updates the database on check / uncheck.
		
		returns :	void
	*/
	
	public void UpdateCheckBox(Item aItem)
	{
		ArrayList<Object> arguments = new ArrayList<Object>();
		
    	arguments.add(getApplicationContext());
    	arguments.add(iSavedData.iCurrentListName);
    	arguments.add(aItem);
    	
		iListItController.handleMessage(ListItController.MESSAGE_UPDATE_CHECKED, 
										arguments);
	}
	
	/*	Method:		Deletes a list from database.
		parameter:	String
		returns :	void
	*/
	public void DeleteList(int aListPosition)
	{
		if( iSavedData == null || 
			aListPosition == -1 || 
			aListPosition >= iSavedLists.size() || 
			iSavedLists.get(aListPosition) == null )
		{
			return;
		}
		
		if( iTabHost.getCurrentTab() ==  TabId_SavedList)
		{
			ArrayList<Object> arguments = new ArrayList<Object>();
			
        	arguments.add(getApplicationContext());
        	arguments.add(aListPosition);
        	arguments.add(iSavedLists.get(aListPosition).getName());
        	
			iListItController.handleMessage(ListItController.MESSAGE_DELETE_LIST, 
											arguments);
		}
	}
	
	/*	Method:		Deletes an item from database.
					Called when delete button of an item is pressed.
					Called from view adapter class since the click event belongs to a particular item.
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
	
	/*	Method:		Deletes an item from database.
					Called when delete button of an item is pressed.
					Called from view adapter class since the click event belongs to a particular item.
		parameter:	String, Item to be deleted
					int, position of the item
		returns :	void
	*/
	
	public void deleteListConfirmDialog(String aListName)
	{	
		iSavedData.iCurrentItemPosition = getListPosition( aListName );
		showDialog(DIALOG_DELETE_LIST);
	}
	
	private int getListPosition(String aListName)
	{
		if( TextUtils.isEmpty(aListName) )
			return -1; 
		
		for(int i = 0; i < iSavedLists.size(); i++)
		{
			SavedItem listItem = iSavedLists.get(i);
			if( listItem != null && listItem.getName() == aListName )
			{
				return i;
			}
		}
		
		return -1;
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
	
	/*	Method:		Callbacks to update UI.
					Called from Model to update the view.
		parameter:	int, messageId.
		returns :	void
	*/
	@SuppressWarnings("unchecked")
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
				if(aMessageData != null)
					HandleLists((ArrayList<SavedItem>) aMessageData);
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
				else
					DisplayMessage(ENTER_ITEM);
				
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
	
	/*	Method:		Displays Toasts on the screen depending on the messageId.
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
			case ENTER_ITEM:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.itemname), 
					       Toast.LENGTH_SHORT).show();
				break;
			}
			case ADD_TO_LIST:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.added_to_list) /*+" "+ iSavedData.iCurrentListName*/, 
				           Toast.LENGTH_SHORT).show();
				break;
			}
			case INVALID_QUANTITY:
			{
				Toast.makeText(getApplicationContext(),getString(R.string.invalid_qty_toast), 
						   Toast.LENGTH_SHORT).show();
				break;
			}
			case NAME_A_LIST:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.give_name), Toast.LENGTH_SHORT).show();
				break;
			}
			case LIST_NAME:
			{
				Toast.makeText(getApplicationContext(), getString(R.string.new_list_toast), Toast.LENGTH_SHORT).show();
				break;
			}
		}
   	}

	/*	Method:		Loads all List Sets on tab change.
					Called from Model to update the View after reading the list names from database.
		parameter:	ArrayList<SavedItem>, Lists to be displayed in the list set.
		returns :	void
	*/
	public void HandleLists(ArrayList<SavedItem> aListNames)
	{
		iSavedLists.clear();
		iSavedLists.addAll(aListNames);
		
		iSavedListAdapter.notifyDataSetChanged();
	}

	/*	Method:		Loads all items from a list on clicking any list from list set.
					Called from Model to update the View after reading the item names from database.
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

	/*	Method:		Observer callback to delete an item from item list adapter.
					Invoked by Model to modify the view.
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

	/*	Method:		Observer callback function to update the list set adapter on pressing delete button of a list.
					Invoked by Model to update the view. 
		parameter:	String
		returns :	void
	*/
	public void DeleteSavedList(ArrayList<Object> aData)
	{
		if( aData == null || aData.isEmpty() )
			return;
		
		String aListName  = (String) aData.get(0);
		int aListPosition = (Integer) aData.get(1);
		
		if( TextUtils.isEmpty(aListName) || 
			aListPosition < 0 || aListPosition >= iSavedLists.size() )
		{	
			return;
		}
		
		SavedItem lName = iSavedLists.get(aListPosition);
		
		if( lName == null )
			return;
		
		String listName = lName.getName();
		
		if( listName.contentEquals(aListName) )
		{
			iSavedLists.remove(aListPosition);
			iSavedListAdapter.notifyDataSetChanged();
		}
		
		// If the list is deleted which is open in first tab 
		
		if(iSavedData.iCurrentListName.contentEquals(aListName)) 
		{
			iItems.clear();
			iItemAdapter.notifyDataSetChanged();
			
			iSavedData.iCurrentListName = "";
			updateTitle(iSavedData.iCurrentListName);
		}
	}

	/*	Method:		Callback method to add an item to the list.
					Model callback to update the View/UI.
		
		returns :	void
	*/
	public void AddItemInView(Item aNewItem)
	{
		iItems.add(aNewItem);
		iItemAdapter.notifyDataSetChanged();
		DisplayMessage(ADD_TO_LIST);
		//Toast.makeText(getApplicationContext(), getString(R.string.added_to_list) /*+" "+ iSavedData.iCurrentListName*/, 
			          // Toast.LENGTH_SHORT).show();
		
		// Now reset the editors
		iItemText.setText("");
        iQuantityText.setText(""); 
        
        // We should show the recently added item (which is at the end) always. 
        // So make list view to scroll to the end. 
        
        iItemListView.setSelection(iItemAdapter.getCount() - 1); 
	}
	
	/*	Method:		Updates an item with its new position.
					Model callback to update UI after modification in the database.
					
		parameter:	ArrayList<Item>, all the items from list
		returns :	void
	*/
	
	public void UpdateItemPosition(ArrayList<Item> aListItem)
	{
		iItems.clear();
		iItems.addAll(aListItem);
		
		iItemAdapter.notifyDataSetChanged();
	}
	
	/*	Method:		Updates an item on clicking the check box.
					Called from Model to update the View once the database modifications are success.
		parameter:
		returns :	void
	*/
   	
   	public void UpdateItemChecked(Item aItem)
   	{
   		iItems.get( aItem.getRowId() - 1 ).setChecked( aItem.isChecked() );
		iItemAdapter.notifyDataSetChanged();
   	}
	
	/*	Method:		Retains the value of an object on runtime configuration change.
		
		returns :	Object
	*/
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
	
	/*	Method:		Stripoff extra zeros in quantity field
		
		returns :	String
	*/
	
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
	
	/*	Method:		Validates the quantity field by removing unwanted zeros.
		
		returns :	boolean
	*/
	
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
	
	/*	Method:		Validates Item text for emptyness.
		
		returns :	boolean
	*/
	
	private boolean ValidateItem(String aItemText)
	{
		if( TextUtils.isEmpty(aItemText.trim()) )
			return false;
		return true;
	}
	
	/*	Method:		Converts exponent value to double in quantity field.
		returns :	String
	*/
	
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
	
	@Override
	public void afterTextChanged(Editable s) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) 
	{
		// This implementation is done to avoid the truncation of digits
		// when specified number to digits are allowed in an edit text.
		
		EditText textView = null;
		
		if( iQuantityText.isFocused() )
		{
			textView = iQuantityText;
		}
		else if( iSavedData.iCurrentDialog != null && 
				 iSavedData.iCurrentDialogId == DIALOG_EDIT_ITEM )
		{
			AlertDialog d = iSavedData.iCurrentDialog;
			textView = (EditText) d.findViewById(R.id.qtyEditText);
		}
		else
		{
			return;
		}			
		
		if( textView == null || s.length() == 0 ||
			start < 0 || start >= textView.getText().length())
			return;
		
		if(textView.isFocused() && start == 0 && s.charAt(start) =='.' )
		{				
			textView.setText("0.");
			textView.setSelection(count+1);
		}
	}
	
	/*	Nested class for Next editor action listener for landscape mode of an item	 */

	class NextOnEditorActionListener implements OnEditorActionListener {
	   
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        if (actionId == EditorInfo.IME_ACTION_NEXT) {
	        	// Hide the softkey pad
	            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	            
	            // Move the focus to Quantity edit text
	            iQuantityText.requestFocus();
	            return true;	
	        }
	        
	        return false;
	    }
	}

	/*	Nested class for Done editor action listener for landscape mode of an item */

	class DoneOnEditorActionListener implements OnEditorActionListener {
		   
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        if (actionId == EditorInfo.IME_ACTION_DONE) {
	        	// Hide the softkey pad
	            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	            
	            return true;	
	        	}
	        	return false;
	    	}
	    
	}	
	
	/*	Method:		onPause method of an activity
					called when another activity is taking focus

		returns :	void
	*/

   	public void onPause()
   	{
   		super.onPause();
   	}
	
	/*	Method:		onStop method of an activity
					called when the activity is no longer visible

		returns :	void
	*/
   	
   	public void onStop()
   	{
   		super.onStop();
   	}
	
	/*	Method:		onDestroy method of an activity
					called when the activity is about to be destroyed

		returns :	void
	*/
   	
   	public void onDestroy()
   	{
   		super.onDestroy();
   	}

}


