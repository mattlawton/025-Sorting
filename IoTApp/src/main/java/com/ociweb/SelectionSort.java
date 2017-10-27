package com.ociweb;

public class SelectionSort {

	public static int findMinIndex(int[] arr, int step) {
		int n = arr.length;
		// Find the minimum element in unsorted array
		int min_idx = step;
		for (int j = step + 1; j < n; j++)
			if (arr[j] < arr[min_idx])
				min_idx = j;

		return min_idx;
	}

	public static void swap(int[] arr, int target, int step) {
		int temp = arr[target];
		arr[target] = arr[step];
		arr[step] = temp;
	}
}
