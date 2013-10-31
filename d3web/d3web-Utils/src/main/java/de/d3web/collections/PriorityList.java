package de.d3web.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import de.d3web.utils.EqualsUtils;

/**
 * Class to manage prioritized items in a list-like manner. Each item in the
 * list is added with a priority. Even when adding items without such a
 * priority, a well-defined priority is assumed (see various method
 * documentations for more details).
 * <p>
 * The implementation of this PriorityList is focused on access optimization.
 * Therefore all access methods to single items priorities or sub-lists of
 * elements of the same priority are returning in constant O(1) calculation
 * complexity. On the other hand insert operations are taking up to O(n)
 * complexity.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 31.10.2013
 */
public class PriorityList<P extends Comparable<P>, E> extends AbstractList<E> implements List<E> {

	public final P defaultPriority;

	private static class Entry<P, E> {

		private final P priority;
		private final E element;

		public Entry(P priority, E item) {
			this.priority = priority;
			this.element = item;
		}
	}

	public static class Group<P, E> {

		private final P priority;
		private final List<E> elements;

		public Group(P priority, List<E> elements) {
			this.priority = priority;
			this.elements = elements;
		}

		public P getPriority() {
			return priority;
		}

		public List<E> getElements() {
			return elements;
		}

		@Override
		public String toString() {
			return priority + "=" + elements;
		}
	}

	private final List<Entry<P, E>> items = new ArrayList<Entry<P, E>>();

	private transient Map<P, List<E>> cachedPriorityMap = null;

	public PriorityList(P defaultPriority) {
		this.defaultPriority = defaultPriority;
	}

	public PriorityList(P defaultPriority, Collection<? extends E> elements) {
		this(defaultPriority);
		addAll(elements);
	}

	/*
	 * methods to provide the list character
	 */

	@Override
	public E get(int index) {
		return items.get(index).element;
	}

	@Override
	public int size() {
		return items.size();
	}

	/*
	 * methods to make the list modifiable
	 */

	@Override
	public E set(int index, E element) {
		Entry<P, E> oldItem = items.get(index);
		Entry<P, E> newItem = new Entry<P, E>(oldItem.priority, element);
		items.set(index, newItem);
		invalidateCaches();
		return oldItem.element;
	}

	@Override
	public void add(int index, E element) {
		P priority;
		if (index < size()) {
			// use the priority of the item that is at the specific index
			priority = items.get(index).priority;
		}
		else if (index > 0) {
			// if there is no such item try the item before
			priority = items.get(index - 1).priority;
		}
		else {
			// otherwise (empty list) use default priority
			priority = defaultPriority;
		}
		items.add(index, new Entry<P, E>(priority, element));
		invalidateCaches();
	}

	@Override
	public E remove(int index) {
		Entry<P, E> removed = items.remove(index);
		invalidateCaches();
		return removed.element;
	}

	/*
	 * methods to access priority-like access
	 */

	private void invalidateCaches() {
		this.cachedPriorityMap = null;
	}

	/**
	 * Appends the specified item to the elements. The position of the new item
	 * is based on the priority. If there are already items with the same
	 * priority, the specified element will become the last of these elements.
	 * 
	 * @created 31.10.2013
	 * @param priority the priority of the element
	 * @param element the element to be added
	 */
	public void add(P priority, E element) {
		// iterate from list end until we reached the first position
		// or the previous position will not have a less or same priority
		int index = items.size();
		while (index > 0 && items.get(index - 1).priority.compareTo(priority) > 0)
			index--;

		// then insert item at position found
		items.add(index, new Entry<P, E>(priority, element));
		invalidateCaches();
	}

	/**
	 * Returns all the prioritized items grouped by their priorities as a map.
	 * The items of the individual lists contains all items of this
	 * {@link PriorityList} of a specific priority in the order as they are
	 * available in this list.
	 * 
	 * @created 31.10.2013
	 * @return the items of this list grouped by their priorities
	 */
	public Map<P, List<E>> getPriorityMap() {
		if (cachedPriorityMap == null) {
			if (isEmpty()) {
				cachedPriorityMap = Collections.emptyMap();
			}
			else {
				cachedPriorityMap = new LinkedHashMap<P, List<E>>();
				P currentPrio = null;
				List<E> currentList = null;
				for (Entry<P, E> item : this.items) {
					if (currentPrio == null || !item.priority.equals(currentPrio)) {
						currentPrio = item.priority;
						currentList = new LinkedList<E>();
						cachedPriorityMap.put(currentPrio,
								Collections.unmodifiableList(currentList));
					}
					currentList.add(item.element);
				}
			}
		}
		return cachedPriorityMap;
	}

	/**
	 * Returns all the prioritized items grouped by their priorities as a list
	 * of groups. The element-list of the individual groups contains all items
	 * of this {@link PriorityList} of a specific priority in the order as they
	 * are available in this list.
	 * 
	 * @created 31.10.2013
	 * @return the items of this list grouped by their priorities
	 */
	public List<Group<P, E>> getPriorityGroups() {
		Map<P, List<E>> priorityMap = getPriorityMap();
		List<Group<P, E>> result = new ArrayList<PriorityList.Group<P, E>>(priorityMap.size());
		for (java.util.Map.Entry<P, List<E>> entry : priorityMap.entrySet()) {
			result.add(new Group<P, E>(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	/**
	 * This method that allows to iterate over a {@link PriorityList} in groups
	 * of elements with the same priority. The iteration starts with lowest
	 * priority and proceeds stepwise to higher priorities (according to the
	 * priority's natural order).
	 * <p>
	 * This special implementation also handles if the underlying PriorityList
	 * changes. Added or removed items with a priority already passed will be
	 * ignored, while added or removed elements for later priorities that the
	 * current one will be taken into consideration.
	 * 
	 * @created 31.10.2013
	 * @return the iterator to iterate securely over modifying priority lists
	 */
	public Iterator<Group<P, E>> groupIterator() {
		// return iterator
		return new Iterator<Group<P, E>>() {

			private P currentPrio = null;

			@Override
			public boolean hasNext() {
				if (currentPrio == null) return !isEmpty();
				// we have still elements if the current priority has not
				// already reached the maximum priority
				P max = getHighestPriority();
				return (max != null) && currentPrio.compareTo(max) < 0;
			}

			@Override
			public Group<P, E> next() {
				// search first priority that is higher that the current one
				for (P priority : getPriorities()) {
					if (currentPrio == null || priority.compareTo(currentPrio) > 0) {
						currentPrio = priority;
						return new Group<P, E>(priority, getElements(priority));
					}
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns all the elements of this {@link PriorityList} that have the
	 * specified priority. If there are no such objects, an empty list is
	 * returned. The elements of the list remain the order the elements have in
	 * this PriorityList.
	 * 
	 * @created 31.10.2013
	 * @param priority the priority to access the elements for
	 * @return the elements of the specified priority
	 */
	public List<E> getElements(P priority) {
		List<E> list = getPriorityMap().get(priority);
		return (list != null) ? list : Collections.<E> emptyList();
	}

	/**
	 * Returns the priorities used in this PriorityList, this means all
	 * priorities for which currently at least one element is added to this
	 * list. The returned collection contains all priorities in the natural
	 * order of the priority.
	 * 
	 * @created 31.10.2013
	 * @return the ordered used priorities
	 */
	public Collection<P> getPriorities() {
		return Collections.unmodifiableCollection(getPriorityMap().keySet());
	}

	/**
	 * Returns the lowest priority currently used by this PriorityList. The
	 * lowest priority is the one of the first element of this list. It is the
	 * first priority of the used priorities in their natural order. If the list
	 * is empty, null is returned.
	 * 
	 * @created 31.10.2013
	 * @return the lowest used priority
	 */
	public P getLowestPriority() {
		if (isEmpty()) return null;
		return items.get(0).priority;
	}

	/**
	 * Returns the highest priority currently used by this PriorityList. The
	 * highest priority is the one of the last element of this list. It is the
	 * last priority of the used priorities in their natural order. If the list
	 * is empty, null is returned.
	 * 
	 * @created 31.10.2013
	 * @return the lowest used priority
	 */
	public P getHighestPriority() {
		if (isEmpty()) return null;
		return items.get(items.size() - 1).priority;
	}

	/**
	 * Returns the priority the specified element is added with to this
	 * PriorityList. If the element is added multiple times, the lowest priority
	 * of the element is returned. If the element is not in this list, null is
	 * returned.
	 * 
	 * @created 31.10.2013
	 * @return the (lowest) priority of the specified element
	 */
	public P getPriority(E element) {
		for (Entry<P, E> item : this.items) {
			if (EqualsUtils.isSame(item.element, element)) return item.priority;
		}
		return null;
	}

	/**
	 * Returns the default priority of the list.
	 * 
	 * @created 31.10.2013
	 * @return the default priority
	 */
	public P getDefaultPriority() {
		return defaultPriority;
	}
}
