package com.android.shoppinglist;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/** Custom adapter for displaying an array of objects. */
public class SavedListAdapter extends ArrayAdapter<SavedItem> 
{
	private LayoutInflater 	iInflater;
	private ShoppingListActivity iActivity;
  
	public SavedListAdapter( ShoppingListActivity aActivity, ArrayList<SavedItem> aListNames ) 
	{
	    super( aActivity, R.layout.rowbuttonlayout, R.id.listTextView, aListNames );
	    
	    // Cache the LayoutInflate to avoid asking for a new one each time.
	    iInflater = LayoutInflater.from(aActivity) ;
	    iActivity = aActivity;    	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final SavedItem listName = (SavedItem) getItem( position ); 
		
	    // The child views in each row.
		Button 			delButton;
	    ImageView 		dragView;	    
	    final TextView listNameView; 
	    final TextView dateView;
	    
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
		            Button delBtn = (Button) v;
		            SavedItem listItem = (SavedItem)delBtn.getTag();
		            confirmDelete(listItem.getName().toString());
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

	    delButton.setTag(listName);
	    delButton.setClickable(true);
	    
	    listNameView.setText( listName.getName() );
	    dateView.setText(listName.getDate());
	    dragView.setTag(listName);
	    
	    return convertView;
	}
  
 private void confirmDelete(final String aList)
 {    
      AlertDialog.Builder alert = new AlertDialog.Builder(iActivity);
      	 
      alert.setTitle(getContext().getString(R.string.delete)); 
      alert.setMessage(getContext().getString(R.string.sure)); 
      
       alert.setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() 
       { 
	      public void onClick(DialogInterface dialog, int whichButton) 
	      {
	    	  if(iActivity!=null) 
	    	  {
	    		  iActivity.DeleteList(aList);   	
	    		  Toast.makeText(iActivity, getContext().getString(R.string.deleted), Toast.LENGTH_SHORT).show(); 
	    	  }
      } 
      }); 
      alert.setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() 
      { 
        public void onClick(DialogInterface dialog, int whichButton) 
        { 
          // Canceled. 
        } 
      }); 
      alert.show(); 	
	  
  }
  
}

