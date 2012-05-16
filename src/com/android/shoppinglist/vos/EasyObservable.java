package com.android.shoppinglist.vos;


public interface EasyObservable<T> {
	
	void addObserver(ModelObserver<T> listener);
	void removeObserver(ModelObserver<T> listener);
	
}