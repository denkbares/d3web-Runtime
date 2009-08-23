/*
 * Created on 10.11.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * ProbabilityList is a List where only objects of type ProbabilityList.Item can be added
 * Each ProbabilityList.Item points to an Object and has a certain probability (0.0 <= x <= 1.0)
 * 
 * normalize()
 * 		normalizes all probabilites so that the sum is 1
 * getObject()
 * 		returns randomly the Object of one of these items according to the probabilities
 * 		of each item, getObject uses a temporary already normalized ProbabilityList
 * 
 * ATTENTION: toArray() & toArray(Object[] a) throw UnsupportedOperationException
 * 
 * 10.11.2003 14:53:36
 * @author hoernlein
 */
public class ProbabilityList implements List<ProbabilityList.Item> {
	
	public static class Item {
		
		private Object o;
		private double probability;
		
		private Item() { /* hide empty constructor */ }

		public Item(Object o, double probability) throws RuntimeException {
			if (o == null || probability < 0 || probability > 1)
				throw new RuntimeException();
			else {
				this.o = o;
				this.probability = probability;
			}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Item))
				return false;
			if (this == obj)
				return true;
				
			Item other = (Item) obj;
			return other.probability == this.probability
				&& other.o.equals(this.o);
		}
		
		public Object getObject() {
			return o;
		}

		public double getProbability() {
			return probability;
		}

		public void setObject(Object p) {
			this.o = p;
		}

		public void setProbability(double probability) {
			this.probability = probability;
		}

	}
	
	private List<Item> list;
	
	public ProbabilityList() {
		this.list = new LinkedList<Item>();
	}
	
	/**
	 * this method randomly chooses one of the PLItems (according to the their probabilities)
	 * and returns its contained Object
	 * to do this the method uses a shuffled normalized temporary ProbabilityList 
	 * 
	 * @return Object
	 */
	public Object getObject() {
		
		ProbabilityList tmp = new ProbabilityList();
		tmp.addAll(list);
		Collections.shuffle(tmp);
		tmp.normalize();
		
		double dice = Math.random();

		double sum = 0;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Item i = (Item) iter.next();
			sum += i.getProbability();
			if (sum > dice)
				return i.getObject();
		}
		
		System.err.println("getObject returns null!");
		return null;
		
	}
	
	/**
	 * this method normalizes the probabilites of all contained PLItems,
	 * so the sum of their probabilites is 1.0
	 * 
	 */
	public void normalize() {
		
		double sum = 0;
		Iterator iter = iterator();
		while (iter.hasNext())
			sum += ((Item) iter.next()).getProbability();
			
		iter = iterator();
		while (iter.hasNext()) {
			Item i = (Item) iter.next();
			i.setProbability(i.getProbability() / sum);
		}
		
	}

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size() {
        return list.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return list.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    public Iterator<Item> iterator() {
        return list.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.List#add(E)
     */
    public boolean add(Item o) {
        return list.add(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return list.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends Item> c) {
        return list.addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends Item> c) {
        return list.addAll(index, c);
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    public void clear() {
        list.clear();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Item get(int index) {
        return list.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, E)
     */
    public Item set(int index, Item element) {
        return list.set(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, E)
     */
    public void add(int index, Item element) {
        list.add(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Item remove(int index) {
        return list.remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    public ListIterator<Item> listIterator() {
        return list.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<Item> listIterator(int index) {
        return list.listIterator(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    public List<Item> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

}
