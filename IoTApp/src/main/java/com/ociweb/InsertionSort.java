package com.ociweb;

public class InsertionSort {

	public static void sort(int[] arr, int j, int key) {
		while (j>=0 && arr[j] > key)
        {
            arr[j+1] = arr[j];
            j = j-1;
        }
        arr[j+1] = key;
	}
	
	
}
