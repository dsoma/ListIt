/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class SavedListRowView 
 * 				Holds child views for one row in Saved Lists View. . 
 */

package com.android.listit;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SavedListRowView 
{
	private Button 		delButton;
    private TextView 	listName ;
    private TextView 	dateList;
    private ImageView 	imageView;
    
    public SavedListRowView() 
    {
    }
   
    public SavedListRowView( ImageView imageView, TextView aListName,TextView aDate, 
    		     			 Button aDelButton ) 
    {
    	this.imageView = imageView;
    	this.listName = aListName ;
    	this.dateList = aDate;
        this.delButton = aDelButton;
    }
    
    public Button getButton() 
    {
    	return delButton;
    }
    
    public void setButton(Button delButton) 
    {
    	this.delButton = delButton;
    }
    
    public TextView getTextView() 
    {
    	return listName;
    }
    
    public void setTextView(TextView listName) 
    {
    	this.listName = listName;
    }  
    
    public void setDate(TextView aDate) 
    {
    	this.dateList = aDate;
    }  
    
    public TextView getDate() 
    {
    	return dateList;
    }
    public ImageView getImageView() 
    {
    	return imageView;
    }
    public void setImageView(ImageView imageView) 
    {
    	this.imageView = imageView;
    }
}