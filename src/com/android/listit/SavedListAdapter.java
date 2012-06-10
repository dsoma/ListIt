package com.android.listit;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/** Custom adapter for displaying an array of objects. */
public class SavedListAdapter extends ArrayAdapter<SavedItem> 
{
	private LayoutInflater 	iInflater;
	private ListItActivity  iActivity;
	
	public SavedListAdapter( ListItActivity aActivity, ArrayList<SavedItem> aListNames ) 
	{
	    super( aActivity, R.layout.rowbuttonlayout, R.id.listTextView, aListNames );
	    
	    // Cache the LayoutInflate to avoid asking for a new one each time.
	    iInflater = LayoutInflater.from(aActivity) ;
	    iActivity = aActivity;    	
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final SavedItem listItem = (SavedItem) getItem( position ); 
		
	    // The child views in each row.
		Button 			delButton;
	    ImageView 		dragView;	    
	    final TextView  listNameView; 
	    final TextView  dateView;
	    
	    // Create a new row view
	    if ( convertView == null ) 
	    {
	    	convertView = iInflater.inflate(R.layout.rowbuttonlayout, null);
	    	
	    	// Find the child views.
	    	listNameView  = (TextView) convertView.findViewById( R.id.listTextView );  
	    	dateView 	  = (TextView) convertView.findViewById( R.id.dateTextView );
	    	delButton     = (Button)   convertView.findViewById( R.id.deleteButtonList);
	    	dragView	  = (ImageView) convertView.findViewById( R.id.icon2);
	    	
      
	    	// Optimization: Tag the row with it's child views, so we don't have to 
	    	// call findViewById() later when we reuse the row.
	    	SavedListRowView sv = new SavedListRowView(dragView, listNameView,dateView, delButton);
	    	convertView.setTag( sv );
	      	
	    	// If DeleteButton is clicked, update the model it is tagged with.
	    	delButton.setOnClickListener( new View.OnClickListener() 
	    	{
	    		public void onClick(View v) 
	    		{
	    			Button deleteButton = (Button) v;
	    			if( deleteButton != null && deleteButton.getTag() != null )
	    			{
	    				String name = ((SavedItem) deleteButton.getTag()).getName();
	    				iActivity.deleteListConfirmDialog( name );
	    			}
	    		}      	
	    	}); 
	    	
	    	// If listNameView or dateView is long pressed, then user intends to edit the item. 
	    	listNameView.setOnLongClickListener( new View.OnLongClickListener() 
	    	{
				@Override
				public boolean onLongClick(View v) 
				{
					TextView listNameView = (TextView) v;
	    			if( listNameView != null && listNameView.getTag() != null )
	    			{
	    				String name = ((SavedItem) listNameView.getTag()).getName();
	    				return iActivity.onSavedListLongClick(name);
	    			}
					return false;
				}
			});
	    	
	    	dateView.setOnLongClickListener( new View.OnLongClickListener() 
	    	{
				@Override
				public boolean onLongClick(View v) 
				{
					TextView dateView = (TextView) v;
	    			if( dateView != null && dateView.getTag() != null )
	    			{
	    				String name = ((SavedItem) dateView.getTag()).getName();
	    				return iActivity.onSavedListLongClick(name);
	    			}
					return false;
				}
			});
	    	
	    	// If listNameView or dateView is pressed, then user intends to load the list. 
	    	listNameView.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					TextView listNameView = (TextView) v;
	    			if( listNameView != null && listNameView.getTag() != null )
	    			{
	    				String name = ((SavedItem) listNameView.getTag()).getName();
	    				iActivity.onSavedListClick(name);
	    			}
				}
			});
	    	
	    	dateView.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					TextView dateView = (TextView) v;
	    			if( dateView != null && dateView.getTag() != null )
	    			{
	    				String name = ((SavedItem) dateView.getTag()).getName();
	    				iActivity.onSavedListClick(name);
	    			}
				}
			});
	    }
	    // Reuse existing row view
	    else 
	    {
	    	// Because we use a ViewHolder, we avoid having to call findViewById().
	    	SavedListRowView rowView = (SavedListRowView) convertView.getTag();
	    	
	    	delButton     = rowView.getButton();
	    	listNameView  = rowView.getTextView();
	    	dateView 	  = rowView.getDate();
	    	dragView =  	rowView.getImageView();
	    }

	    delButton.setTag(listItem);
	    delButton.setClickable(true);
	    
	    listNameView.setText(listItem.getName() );
	    listNameView.setTag(listItem);
	    
	    dateView.setText(listItem.getDate());
	    dateView.setTag(listItem);
	    
	    dragView.setTag(listItem);
	    
	    return convertView;
	}
}

