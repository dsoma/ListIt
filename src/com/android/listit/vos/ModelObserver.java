package com.android.listit.vos;

import java.util.ArrayList;

import com.android.listit.Item;
import com.android.listit.SavedItem;

public interface ModelObserver<T> 
{
	void onChange(T model);
	void ModelCallback(int aMessageId);
	void HandleLists(ArrayList<SavedItem> aListNames);
	void DeleteItemFromList(ArrayList<Object> aListItem);
	void DeleteSavedList(String aListName);
	void LoadItemsList(ArrayList<Item> aObj);
	void EditItemList(ArrayList<Item> aObj);
	void EditListNames(ArrayList<String> aObj);
}
