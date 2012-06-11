/*
 * Copyright (c) 2012 Ramya Machina. (ramya dot machina at gmail dot com)
 * 
 * Description: Implementation of class ModelObserver 
 * 				Observer Interface to observe the model for data changes. 
 */

package com.listit.vos;

import java.util.ArrayList;

import com.listit.Item;
import com.listit.SavedItem;

public interface ModelObserver<T> 
{
	void onChange(T model);
	void ModelCallback(final int aMessageId, final Object aMessageData);
	void HandleLists(ArrayList<SavedItem> aListNames);
	void DeleteItemFromList(ArrayList<Object> aListItem);
	void DeleteSavedList(ArrayList<Object> aData);
	void LoadItemsList(ArrayList<Item> aObj);
	void EditItemList(ArrayList<Item> aObj);
	void EditListNames(ArrayList<String> aObj);
	void UpdateItemPosition(ArrayList<Item> aObj);
	void UpdateItemChecked(Item aItem);
}
