package com.purplecat.commons.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ListUtils {
	
	public static <T> Iterable<T> nullSafe(Iterable<T> list) {
		return(list != null ? list : Collections.<T>emptySet());
	}
	
	public static <T> Iterable<T> createIterator(T[] array) {
		List<T> list = new ArrayList<T>(array.length);
		for ( T item : array ) {
			list.add(item);
		}
		return(list);
	}
	
	public static <T> T getNullSafeElement(T[] array, int index, T def) {
		if ( index >= 0 && index < array.length ) {
			return(array[index]);
		}
		else {
			return(def);
		}
	}
	
	public static <T> int size(Iterable<T> list) {
		int iCount = 0;
		if ( list != null ) {
			Iterator<T> iterator = list.iterator();
			while ( iterator.hasNext() ) {
				iterator.next();
				iCount++;
			}
		}
		return(iCount);
	}
	
	public static <T> boolean contains(Iterable<T> iterable, T checkItem) {
		boolean bContains = false;
		for ( T item : iterable ) {
			if ( item.equals(checkItem) ) {
				bContains = true;
				break;
			}
		}
		return(bContains);
	}

	public static <T> void addAll(Collection<T> collection, Iterable<T> iterable) {
		if ( iterable != null ) {
			for ( T item : iterable ) {
				collection.add(item);
			}
		}
	}

	public static <T> void removeAll(Collection<T> collection, Iterable<T> iterable) {
		if ( iterable != null ) {
			for ( T item : iterable ) {
				collection.remove(item);
			}
		}
	}
	
	public static <R, T> Iterable<R> selectSubList(Iterable<T> collection, IItemSelect<R, T> matcher) {
		List<R> list = new LinkedList<R>();
		
		for ( T item : collection ) {
			if ( matcher.matches(item) ) {
				list.add(matcher.select(item));
			}
		}
		
		return(list);
	}
	
	public static <T> Iterable<T> where(Iterable<T> collection, IItemWhere<T> matcher) {
		List<T> list = new LinkedList<T>();
		
		for ( T item : collection ) {
			if ( matcher.matches(item) ) {
				list.add(item);
			}
		}
		
		return(list);
	}
	
	public static <T> T firstOrNull(Iterable<T> collection) {
		T item = null;
		if ( collection != null ) {
			Iterator<T> iterator = collection.iterator();
			if ( iterator.hasNext() ) {
				item = iterator.next();
			}
		}
		return(item);
	}	

	public static <T> int indexOf(T[] list, T item) {
		int iIndex = -1;
		int iCount = 0;
		for ( T t : list ) {			
			if ( t.equals(item) ) {
				iIndex = iCount;
				break;
			}
			iCount++;
		}
		return(iIndex);
	}

	public static <T> int indexOf(Iterable<T> list, T item) {
		int iIndex = -1;
		int iCount = 0;
		for ( T t : list ) {			
			if ( t.equals(item) ) {
				iIndex = iCount;
				break;
			}
			iCount++;
		}
		return(iIndex);
	}

	public static <T> int indexOf(Iterable<T> list, T item, Comparator<T> comparer) {
		int iIndex = -1;
		int iCount = 0;
		for ( T t : list ) {			
			if ( comparer.compare(t, item) == 0 ) {
				iIndex = iCount;
				break;
			}
			iCount++;
		}
		return(iIndex);
	}	
	
	public static <T> int update(Collection<T> origList, Iterable<T> newList) {		
		Set<T> removeList = new HashSet<T>();
		Set<T> insertList = new HashSet<T>();		
		ListUtils.addAll(removeList, origList);
		
		//If found, remove from removeList
		//If not in removeList, add to insertList
		for ( T cat : newList ) {
			if ( !removeList.remove(cat) ) {
				insertList.add(cat);
			}
		}
		int iNumMoved = 0;		

		//insert any items in insertList
		if ( insertList.size() > 0 ) {
			iNumMoved += insertList.size();
			ListUtils.addAll(origList, insertList);
		}
		
		//remove any items remaining in removeList
		if ( removeList.size() > 0 ) {
			iNumMoved += removeList.size();
			ListUtils.removeAll(origList, removeList);
		}
		return(iNumMoved);
	}
	
	public interface IItemSelect<R, T> {
		public boolean matches(T item);
		public R select(T item);
	}
	
	public interface IItemWhere<T> {
		public boolean matches(T item);
	}

	public static <T> List<T> list(T[] items) {
		List<T> list = new ArrayList<T>();
		for ( T t : items ) { list.add(t); }
		return(list);
	}

	public static <T> List<T> list(Iterable<T> items) {
		List<T> list = new ArrayList<T>();
		for ( T t : items ) { list.add(t); }
		return(list);
	}

	public static int[] toIntegerArray(Collection<Integer> rowList) {
		int[] rows = new int[rowList.size()];
		int i = 0;
		for ( Integer row : rowList ) {
			rows[i] = row;
			i++;
		}
		return(rows);
	}

}
 