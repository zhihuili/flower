/**
 * 
 */
package com.ly.train.flower.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author leeyazhou
 *
 */
public class CollectionUtil {


  public static boolean isEmpty( Collection<?> collection) {
    return (collection == null || collection.isEmpty());
  }

  public static boolean isEmpty( Map<?, ?> map) {
    return (map == null || map.isEmpty());
  }

  @SuppressWarnings("rawtypes")
  public static List arrayToList( Object source) {
    return Arrays.asList(ObjectUtil.toObjectArray(source));
  }

  @SuppressWarnings("unchecked")
  public static <E> void mergeArrayIntoCollection( Object array, Collection<E> collection) {
    Object[] arr = ObjectUtil.toObjectArray(array);
    for (Object elem : arr) {
      collection.add((E) elem);
    }
  }

  @SuppressWarnings("unchecked")
  public static <K, V> void mergePropertiesIntoMap( Properties props, Map<K, V> map) {
    if (props != null) {
      for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
        String key = (String) en.nextElement();
        Object value = props.get(key);
        if (value == null) {
          // Allow for defaults fallback or potentially overridden accessor...
          value = props.getProperty(key);
        }
        map.put((K) key, (V) value);
      }
    }
  }


  public static boolean contains( Iterator<?> iterator, Object element) {
    if (iterator != null) {
      while (iterator.hasNext()) {
        Object candidate = iterator.next();
        if (ObjectUtil.nullSafeEquals(candidate, element)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean contains( Enumeration<?> enumeration, Object element) {
    if (enumeration != null) {
      while (enumeration.hasMoreElements()) {
        Object candidate = enumeration.nextElement();
        if (ObjectUtil.nullSafeEquals(candidate, element)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean containsInstance( Collection<?> collection, Object element) {
    if (collection != null) {
      for (Object candidate : collection) {
        if (candidate == element) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
    if (isEmpty(source) || isEmpty(candidates)) {
      return false;
    }
    for (Object candidate : candidates) {
      if (source.contains(candidate)) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  
  public static <E> E findFirstMatch(Collection<?> source, Collection<E> candidates) {
    if (isEmpty(source) || isEmpty(candidates)) {
      return null;
    }
    for (Object candidate : candidates) {
      if (source.contains(candidate)) {
        return (E) candidate;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  
  public static <T> T findValueOfType(Collection<?> collection,  Class<T> type) {
    if (isEmpty(collection)) {
      return null;
    }
    T value = null;
    for (Object element : collection) {
      if (type == null || type.isInstance(element)) {
        if (value != null) {
          // More than one value found... no clear single value.
          return null;
        }
        value = (T) element;
      }
    }
    return value;
  }

  
  public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
    if (isEmpty(collection) || ObjectUtil.isEmpty(types)) {
      return null;
    }
    for (Class<?> type : types) {
      Object value = findValueOfType(collection, type);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  public static boolean hasUniqueObject(Collection<?> collection) {
    if (isEmpty(collection)) {
      return false;
    }
    boolean hasCandidate = false;
    Object candidate = null;
    for (Object elem : collection) {
      if (!hasCandidate) {
        hasCandidate = true;
        candidate = elem;
      } else if (candidate != elem) {
        return false;
      }
    }
    return true;
  }

  
  public static Class<?> findCommonElementType(Collection<?> collection) {
    if (isEmpty(collection)) {
      return null;
    }
    Class<?> candidate = null;
    for (Object val : collection) {
      if (val != null) {
        if (candidate == null) {
          candidate = val.getClass();
        } else if (candidate != val.getClass()) {
          return null;
        }
      }
    }
    return candidate;
  }

  
  public static <T> T lastElement( Set<T> set) {
    if (isEmpty(set)) {
      return null;
    }
    if (set instanceof SortedSet) {
      return ((SortedSet<T>) set).last();
    }

    // Full iteration necessary...
    Iterator<T> it = set.iterator();
    T last = null;
    while (it.hasNext()) {
      last = it.next();
    }
    return last;
  }

  
  public static <T> T lastElement( List<T> list) {
    if (isEmpty(list)) {
      return null;
    }
    return list.get(list.size() - 1);
  }

  public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
    ArrayList<A> elements = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      elements.add(enumeration.nextElement());
    }
    return elements.toArray(array);
  }



}
