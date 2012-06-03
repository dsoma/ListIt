package com.android.listit.vos;

import java.util.ArrayList;

import com.android.listit.Item;
import com.android.listit.SavedItem;

public class SimpleObservable<T> implements EasyObservable<T> {
	
	private final ArrayList<ModelObserver<T>> observers = new ArrayList<ModelObserver<T>>();
	
	
	public void addObserver(ModelObserver<T> observer) {
		synchronized (observers) {
			observers.add(observer);
		}
	}
	public void removeObserver(ModelObserver<T> observer) {
		synchronized (observers) {
			observers.remove(observer);
		}
	}
	
	protected void notifyObservers(final T model) {
		synchronized (observers) {
			for (ModelObserver<T> observer : observers) {
				observer.onChange(model);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void notifyObservers(final int aMessageId, final Object aObj) {
		synchronized (observers) {
			for (ModelObserver<T> observer : observers) 
			{
				switch( aMessageId )
				{
					default: observer.ModelCallback(aMessageId, aObj); break;
					
					case ListItModel.MESSAGE_LISTS_LOADED: 
					{
						observer.HandleLists( (ArrayList<SavedItem>) aObj );
						break;
					}
					case ListItModel.MESSAGE_ITEM_DELETED:
					{
						observer.DeleteItemFromList((ArrayList<Object>) aObj);
						break;
					}
					case ListItModel.MESSAGE_LIST_DELETED:
					{
						observer.DeleteSavedList((String) aObj);
						break;
					}
					case ListItModel.MESSAGE_ITEMS_LOADED:
					{
						observer.LoadItemsList((ArrayList<Item>) aObj);
						break;
					}
					case ListItModel.MESSAGE_ITEM_EDITED:
					{
						observer.EditItemList((ArrayList<Item>) aObj);
						break;
					}
					case ListItModel.MESSAGE_LIST_EDITED:
					{
						observer.EditListNames((ArrayList<String>) aObj);
						break;
					}
					case ListItModel.MESSAGE_ROW_POS_UPDATED:
					{
						observer.UpdateItemPosition((ArrayList<Item>) aObj);
						break;
					}
					case ListItModel.MESSAGE_CHECKED_UPDATED:
					{
						observer.UpdateItemChecked( (Item) aObj );
						break;
					}
					
				}
				
			}
		}
	}
	
}