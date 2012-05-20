package com.android.listit.vos;


public interface EasyObservable<T> {
	
	void addObserver(ModelObserver<T> listener);
	void removeObserver(ModelObserver<T> listener);
	
}