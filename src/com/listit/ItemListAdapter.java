/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class ItemListAdapter 
 * 				Custom adapter for displaying an array of objects.
 * 
 */
package com.listit;

import java.util.List;

import com.listit.R;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends ArrayAdapter<Item> 
{ 
	private LayoutInflater inflater;
	private ListItActivity iActivity;
	
	/* Colors Used - Although used inside resources, setTextColor()
	 * doesn't work properly with the resource color. So sync up every time 
	 * you change the color in the resource
	 */
	
	public static final int backgroundColor = 0x002C89A0;
	public static final int whiteColor 	    = 0xFFFFFFFF;
	public static final int foregroundColor = whiteColor - backgroundColor; //0xFFD45700;
	public static final int strikeoutColor  = 0xFF676767;
	
	public ItemListAdapter( ListItActivity aActivity, List<Item> ItemList ) 
	{
	    super( aActivity, R.layout.simplerow, R.id.rowTextView, ItemList );
	    
	    // Cache the LayoutInflate to avoid asking for a new one each time.
	    iActivity = aActivity; 
	    inflater = LayoutInflater.from(iActivity);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
	    // Item to display
		final Item item = new Item( (Item) getItem( position ) );
		
		// NOTE: position indexing in database is 1-based. But adapter array indexing is 0-based. 
	    item.setRowId( position + 1 );

	    // The child views in each row.
	    CheckBox checkBox;
	    Button delButton;
	    ImageView dragView;
	    
	    final TextView textView; 
	    final TextView qtyView;
    
	    // Create a new row view
	    if ( convertView == null ) 
	    {
	    	convertView = inflater.inflate(R.layout.simplerow, null);
	    	
	    	// Find the child views.
	    	dragView  = (ImageView) convertView.findViewById(R.id.icon);
	    	checkBox  = (CheckBox) convertView.findViewById( R.id.checkBox );
	    	textView  = (TextView) convertView.findViewById( R.id.rowTextView );	    	
	    	qtyView   = (TextView) convertView.findViewById( R.id.qtyTextView );	    	
	    	delButton = (Button)   convertView.findViewById( R.id.deleteButton);     
      
	    	// Optimization: Tag the row with it's child views, so we don't have to 
	    	// call findViewById() later when we reuse the row.	
	    	convertView.setTag( new ItemListRowView(dragView, checkBox, textView, qtyView,delButton) );
	    	
    	  	//CheckBox on click listener
	  		checkBox.setOnClickListener( new View.OnClickListener() 
	  		{
	  			public void onClick(View v) 
	  			{
		            CheckBox cb = (CheckBox) v;
		            Item item = (Item) cb.getTag();
		            item.setChecked( cb.isChecked() );
		            
		            //update database and view
		            iActivity.UpdateCheckBox(item);
	  			}
	  		});    	  	   
      
    	  	// If DeleteButton is clicked, update the model it is tagged with.
    	  	delButton.setOnClickListener( new View.OnClickListener() 
    	  	{
    	  		public void onClick(View v) 
    	  		{
    	  			Button delBtn = (Button) v;
		        	Item i = (Item) delBtn.getTag();
		        	String listItem = i.getDesc().toString();     
		        	int rowId = i.getRowId();
		        	
		        	confirmDelete(listItem, rowId);        	
		        }
		    });   
    	  	
    	  	// If itemText or qtyText is pressed, then user intends to edit the item. 
    	  	textView.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					TextView textView = (TextView) v;
	    			if( textView != null && textView.getTag() != null )
	    			{
	    				int itemPosition = ((Item) textView.getTag()).getRowId();
	    				iActivity.onItemClick(itemPosition - 1);
	    			}
				}
			});
	    	
	    	qtyView.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					TextView qtyView = (TextView) v;
	    			if( qtyView != null && qtyView.getTag() != null )
	    			{
	    				int itemPosition = ((Item) qtyView.getTag()).getRowId();
	    				iActivity.onItemClick(itemPosition - 1);
	    			}
				}
			});
	    }
	    
	    // Reuse existing row view
	    else 
	    {
	    	// Because we use a ViewHolder, we avoid having to call findViewById().
	    	ItemListRowView viewHolder = (ItemListRowView) convertView.getTag();
	    	
	    	dragView =  viewHolder.getDragView();
	    	checkBox =  viewHolder.getCheckBox() ;	    	
	    	textView =  viewHolder.getTextView() ;
	    	qtyView =   viewHolder.getQtyView();
	    	delButton = viewHolder.getButton();
	    }
	      
	    // Tag the CheckBox with the item it is displaying, so that we can
	    // access the item in onClick() when the CheckBox is toggled.
	    checkBox.setTag( item ); 
	    
	    // Display model data
	    checkBox.setChecked( item.isChecked() );
	    strikeOut(textView, qtyView, item.isChecked());
	    
	    // delete button
	    delButton.setTag(item);
	    delButton.setClickable(true);
    
	    textView.setText( item.getDesc() ); 
	    textView.setTag(item);
	    
	    qtyView.setText( item.getQuantity() );
	    qtyView.setTag(item);
	    
	    dragView.setTag(item);
	    
	    return convertView;
	}
	
	private void confirmDelete(final String listItem, int itemPos)
	{
		 iActivity.deleteItem(listItem, itemPos);   	
	}
	
	public void strikeOut(TextView aTV, TextView aQV, boolean aShouldStrikeOut)
	{
		if( aShouldStrikeOut )
		{
			aTV.setTextColor(strikeoutColor);
	    	aQV.setTextColor(strikeoutColor);
	    	aTV.setPaintFlags(aTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	    	aQV.setPaintFlags(aQV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		else
		{
			aTV.setTextColor(foregroundColor);
	    	aQV.setTextColor(foregroundColor);
	    	aTV.setPaintFlags(aTV.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
	    	aQV.setPaintFlags(aQV.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
		}
	}
}

