package com.android.shoppinglist.vos;

import java.util.ArrayList;

import com.android.shoppinglist.Item;
import com.android.shoppinglist.SavedItem;

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
	
	protected void notifyObservers(final int aMessageId, Object aObj) {
		synchronized (observers) {
			for (ModelObserver<T> observer : observers) 
			{
				switch( aMessageId )
				{
					default: observer.ModelCallback(aMessageId); break;
					case ShoppingListModel.MESSAGE_LISTS_LOADED: 
					{
						observer.HandleLists( (ArrayList<SavedItem>) aObj );
						break;
					}
					case ShoppingListModel.MESSAGE_ITEM_DELETED:
					{
						observer.DeleteItemFromList((ArrayList<Object>) aObj);
						break;
					}
					case ShoppingListModel.MESSAGE_LIST_DELETED:
					{
						observer.DeleteSavedList((String) aObj);
						break;
					}
					case ShoppingListModel.MESSAGE_ITEMS_LOADED:
					{
						observer.LoadItemsList((ArrayList<Item>) aObj);
						break;
					}
					case ShoppingListModel.MESSAGE_ITEM_EDITED:
					{
						observer.EditItemList((ArrayList<Item>) aObj);
						break;
					}
					case ShoppingListModel.MESSAGE_LIST_EDITED:
					{
						observer.EditListNames((ArrayList<String>) aObj);
						break;
					}
				}
				
			}
		}
	}
	
}