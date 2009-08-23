package de.d3web.caserepository;

import org.w3c.dom.Node;

import de.d3web.persistence.xml.loader.PropertiesUtilities;

/**
 * Implementation of Interface IMetaData
 * @author: praktikum00s
 */
public class MetaDataImpl implements IMetaData {
	
	public final static class Codec extends PropertiesUtilities.PropertyCodec {
		
		public Codec(Class clazz) {
			super(clazz);
		}

		/* (non-Javadoc)
		 * @see de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec#encode(java.lang.Object)
		 */
		public String encode(Object o) {
			return ((IMetaData) o).getXMLCode();
		}

		/* (non-Javadoc)
		 * @see de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec#decode(org.w3c.dom.Node)
		 */
		public Object decode(Node n) {
			throw new UnsupportedOperationException("the MetaData.Codec should not be used for decoding.");
		}
		
	}
	
	private String account = "guest@guest";
	private Long processingTime = null;

	/**
	 * @return processing time for the case in seconds
	 */
	public long getProcessingTime() {
		return processingTime == null
			? -1
			: processingTime.longValue();
	}

	/**
	 * @param time (processing time for the case in seconds)
	 */
	public void setProcessingTime(long time) {
		processingTime = new Long(time);
	}

	/**
	 * Returns the account.
	 * @return String
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * Sets the account.
	 * @param account The account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof MetaDataImpl))
			return false;
		if (o == this)
			return true;
			
		MetaDataImpl other = (MetaDataImpl) o;
		return getAccount().equals(other.getAccount())
			&& getProcessingTime() == other.getProcessingTime();
	}

	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Metadata>\n");

		if (getProcessingTime() != -1)
			sb.append("<ProcessingTime value=\"" + getProcessingTime() + "\"/>\n");
		
		if (getAccount() != null)
			sb.append("<Account>" + getAccount() + "</Account>\n");
		
		sb.append("</Metadata>\n");
		return sb.toString();
	}
	
	
	public Object clone() {
		MetaDataImpl md = new MetaDataImpl();
		md.setAccount(new String(getAccount()));
		md.setProcessingTime(getProcessingTime());
		return md;
	}

}