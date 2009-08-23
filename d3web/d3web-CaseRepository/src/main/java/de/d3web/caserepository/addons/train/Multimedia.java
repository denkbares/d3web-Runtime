package de.d3web.caserepository.addons.train;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.caserepository.addons.IMultimedia;

/**
 * Implementation of Interface Multimedia
 * @author: praktikum00s
 */
public class Multimedia implements IMultimedia {
    
    private static class MMIComparator implements Comparator {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            return new Integer(((MultimediaItem) o1).getOrderNumber())
            .compareTo(new Integer(((MultimediaItem) o2).getOrderNumber()));
        }
        
    }
    private static Comparator myComp = new MMIComparator();
    
    /**
     * Sorts List of MultimediaItems according to their order number
     * 
     * @param multimediaItems
     */
    private static void sortListOfMMIs(List multimediaItems) {
        Collections.sort(multimediaItems, myComp);
    }
	
	private List<MultimediaItem> multimediaItems = new LinkedList<MultimediaItem>();
	private MultimediaItem startItem;

	/**
	 * @param item MultimediaItem
	 */
	public void addMultimediaItem(MultimediaItem item) {
		multimediaItems.add(item);
		sortListOfMMIs(multimediaItems);
	}

	/**
	 * @param item MultimediaItem
	 */
	public void removeMultimediaItem(MultimediaItem item) {
		multimediaItems.remove(item);
	}

	/**
	 * @param id String
	 * @return MultimediaItem
	 */
	public MultimediaItem getMultimediaItemFor(String id) {
		Iterator iter = getMultimediaItems().iterator();
		while (iter.hasNext()) {
			MultimediaItem item = (MultimediaItem) iter.next();
			if (item.getId().equals(id))
				return item;
		}
		return null;
	}

	/**
	 * @return Collection
	 */
	public List<MultimediaItem> getMultimediaItems() {
		return Collections.unmodifiableList(multimediaItems);
	}

	/**
	 * @return MultimediaItem
	 */
	public MultimediaItem getStartItem() {
		return startItem;
	}

	/**
	 * @param newStartItem MultimediaItem
	 */
	public void setStartItem(MultimediaItem newStartItem) {
		startItem = newStartItem;
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.caserepository.IMultimedia#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer xmlCode = new StringBuffer();

		String start = "";
		if (getStartItem() != null)
			start = " start=\"" + getStartItem().getId() + "\"";

		xmlCode.append ("<Multimedia" + start + ">\n");
		for (int i=0; i<multimediaItems.size(); i++) {
			MultimediaItem mItem = multimediaItems.get(i);
			xmlCode.append(mItem.getXMLCode());
		}
		xmlCode.append ("</Multimedia>\n");
		return xmlCode.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Multimedia))
			return false;
		if (obj == this)
			return true;
		
		Multimedia other = (Multimedia) obj;
		
		if (!((getStartItem() == null && other.getStartItem() == null)
			|| getStartItem().getId().equals(other.getStartItem().getId())))
			return false;
			
		// [MISC]:aha:we deal with complete equality here because otherwise
		// we'd have to write lots of equals and hashcode
		
		Iterator thisIter = getMultimediaItems().iterator();
		while (thisIter.hasNext()) {
			MultimediaItem thisItem = (MultimediaItem) thisIter.next();
			boolean itemFound = false;
			Iterator otherIter = other.getMultimediaItems().iterator();
			while (otherIter.hasNext() && !itemFound) {
				MultimediaItem otherItem = (MultimediaItem) otherIter.next();
				if (thisItem.getId().equals(otherItem.getId())) {
					itemFound = true;
					
					if (thisItem.getColor() != otherItem.getColor()
						|| ((thisItem.getComment() != null || otherItem.getComment() != null)
							&& !thisItem.getComment().equals(otherItem.getComment()))
						|| thisItem.getHeight() != otherItem.getHeight()
						|| thisItem.getWidth() != otherItem.getWidth()
						|| !thisItem.getMimeType().equals(otherItem.getMimeType())
						|| ((thisItem.getTitle() != null || otherItem.getTitle() != null)
							&& !thisItem.getTitle().equals(otherItem.getTitle()))
						|| ((thisItem.getURL() != null || otherItem.getURL() != null)
							&& !thisItem.getURL().equals(otherItem.getURL())))
						return false;
						
					Iterator thisFIter = thisItem.getFeatures().iterator();
					while (thisFIter.hasNext()) {
						Feature thisFeature = (Feature) thisFIter.next();
						boolean featureFound = false;
						Iterator otherFIter = otherItem.getFeatures().iterator();
						while (otherFIter.hasNext() && !featureFound) {
							Feature otherFeature = (Feature) otherFIter.next();
							boolean sameFeature = false;
							if (thisFeature.getQASet().equals(otherFeature.getQASet())
								&& thisFeature.getWeight()== otherFeature.getWeight()) {
								if (thisFeature.hasAnswerInterval() && otherFeature.hasAnswerInterval()) {
									sameFeature =
										thisFeature.getAnswerIntervalLowerBoundary()
										.equals(otherFeature.getAnswerIntervalLowerBoundary())
										&& thisFeature.getAnswerIntervalUpperBoundary()
											.equals(otherFeature.getAnswerIntervalUpperBoundary());
								} else if (thisFeature.hasAnswerInterval() || otherFeature.hasAnswerInterval()) {
								    /* false is ok */
								} else
								    sameFeature = (
								            (thisFeature.getAnswer() == null && otherFeature.getAnswer() == null)
								    		||
								    		thisFeature.getAnswer().equals(otherFeature.getAnswer())
								    );
							}
							if (sameFeature) {
								if (thisFeature.getRegions().isEmpty() && otherFeature.getRegions().isEmpty())
									featureFound = true;
								else {
									Iterator thisRIter = thisFeature.getRegions().iterator();
									while (thisRIter.hasNext()) {
										Object thisRegion = thisRIter.next();
										boolean regionFound = false;
										Iterator otherRIter = otherFeature.getRegions().iterator();
										while (otherRIter.hasNext() && !regionFound) {
											if (thisRegion.equals(otherRIter.next()))
												regionFound = true;
										}
										if (!regionFound)
											return false;
									}
									featureFound = true;
								}
							}
						}
						if (!featureFound)
							return false;
					}
				}
			}
			if (!itemFound)
				return false;
		}
		return true;
	}

}