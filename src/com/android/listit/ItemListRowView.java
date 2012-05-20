package com.android.listit;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/** Holds child views for one row. */

public class ItemListRowView 
{
	private CheckBox 	checkBox ;
	private Button 		delButton;
    private TextView 	textView ;
    private TextView 	qtyView;
    private ImageView	imageView;
    
    public ItemListRowView() {}
   
    public ItemListRowView( ImageView imageView,CheckBox checkBox, TextView textView, TextView qtyView,  Button delButton ) {
    	this.checkBox = checkBox ;
    	this.textView = textView ;
        this.qtyView = qtyView;
        this.imageView = imageView;
        this.delButton = delButton ;
    }
    
    public ItemListRowView( TextView textView,Button delButton ) {
    	this.textView = textView ;
        this.delButton = delButton ;
    }
    
    public CheckBox getCheckBox() {
        return checkBox;
	}
	
    public void setCheckBox(CheckBox checkBox) {
	    this.checkBox = checkBox;
    }
      
    public Button getButton() {
    	return delButton;
    }
    
    public ImageView getDragView() {
    	return this.imageView;
    }
    
    public void setButton(Button delButton) {
      this.delButton = delButton;
    }
    public TextView getTextView() {
      return textView;
    }
    public TextView getQtyView() {
        return qtyView;
    }
    
    public void setTextView(TextView textView) {
      this.textView = textView;
    }  
    
    public void setQtyView(TextView qtyView) {
        this.qtyView= qtyView;
    }   
  }