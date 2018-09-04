package utils;

import android.util.SparseArray;
import android.view.View;

public class AdapterUtils {
	
	@SuppressWarnings("unchecked")
	public static <T extends View> T getHolderItem(View convertView, int resId) {
		SparseArray<View> array = (SparseArray<View>) convertView.getTag();
		if(array == null) {
			array = new SparseArray<View>();
			convertView.setTag(array);
		}
		
		T item = (T)array.get(resId);
		if(item == null) {
			item = (T)convertView.findViewById(resId);
			array.put(resId, item);
		}
		
		return item;
	}
}
