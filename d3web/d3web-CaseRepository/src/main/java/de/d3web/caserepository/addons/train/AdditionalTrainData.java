/*
 * Created on 06.10.2003
 */
package de.d3web.caserepository.addons.train;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.d3web.caserepository.addons.IAdditionalTrainData;

/**
 * 06.10.2003 17:01:40
 * @author hoernlein
 */
public class AdditionalTrainData implements IAdditionalTrainData {
	
	public static class TargetAudience {
		
		/*
		 * Student im vorklinischen Semester
		 */
		public final static TargetAudience MED_SVKS = new TargetAudience("med_svks", TargetAudienceType.MEDICAL);
		
		/*
		 * Student im klinischen Semester
		 */
		public final static TargetAudience MED_SKS = new TargetAudience("med_sks", TargetAudienceType.MEDICAL);
		
		/*
		 * PJ (Arzt im praktischen Jahr) 
		 */
		public final static TargetAudience MED_PJ = new TargetAudience("med_pj", TargetAudienceType.MEDICAL);
		
		/*
		 * AIP (Arzt im Praktikum)
		 */
		public final static TargetAudience MED_AIP = new TargetAudience("med_aip", TargetAudienceType.MEDICAL);
		
		/*
		 * Arzt in Weiterbildung
		 */
		public final static TargetAudience MED_AIW = new TargetAudience("med_aiw", TargetAudienceType.MEDICAL);
		
		/*
		 * Facharzt
		 */
		public final static TargetAudience MED_FA = new TargetAudience("med_fa", TargetAudienceType.MEDICAL);
		
		/*
		 * Pflegeberuf
		 */
		public final static TargetAudience MED_PB = new TargetAudience("med_pb", TargetAudienceType.MEDICAL);
		
		/*
		 * Andere Heilberufe
		 */
		public final static TargetAudience MED_OTHER = new TargetAudience("med_other", TargetAudienceType.MEDICAL);
		
		private String name;
		private TargetAudienceType type;
		private TargetAudience(String name, TargetAudienceType type) {
			this.name = name;
			this.type = type;
		}

		public static TargetAudience getTargetAudience(String name) {
			if (MED_SVKS.getName().equals(name))
				return MED_SVKS;
			else if (MED_SKS.getName().equals(name))
				return MED_SKS;
			else if (MED_PJ.getName().equals(name))
				return MED_PJ;
			else if (MED_AIP.getName().equals(name))
				return MED_AIP;
			else if (MED_AIW.getName().equals(name))
				return MED_AIW;
			else if (MED_FA.getName().equals(name))
				return MED_FA;
			else if (MED_PB.getName().equals(name))
				return MED_PB;
			else if (MED_OTHER.getName().equals(name))
				return MED_OTHER;
			else
				return null;
		}

		public String getName() { return name; }
		public TargetAudienceType getType() { return type; }
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return this == obj;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return getName().hashCode();
		}

	}
	
	public static class TargetAudienceType {
		
		public final static TargetAudienceType MEDICAL = new TargetAudienceType("medical");
		
		private String name;
		private TargetAudienceType(String myName) {
			name = myName;
		}

		public static TargetAudienceType getTargetAudienceType(String name) {
			if (MEDICAL.getName().equals(name))
				return MEDICAL;
			else
				return null;
		}

		public String getName() {
			return name;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return this == obj;
		}
		
	}

	public static class Duration {
		
		public final static Duration UNDER_THIRTY = new Duration("under_30");
		public final static Duration THIRTY_TO_FORTYFIVE = new Duration("30_to_45");
		public final static Duration FORTYFIVE_TO_SIXTY = new Duration("45_to_60");
		public final static Duration MORE_THAN_SIXTY = new Duration("more_than_60");
		
		private String name;
		private Duration(String myName) {
			name = myName;
		}

		public static Duration getDuration(String name) {
			if (UNDER_THIRTY.getName().equals(name))
				return UNDER_THIRTY;
			else if (THIRTY_TO_FORTYFIVE.getName().equals(name))
				return THIRTY_TO_FORTYFIVE;
			else if (FORTYFIVE_TO_SIXTY.getName().equals(name))
				return FORTYFIVE_TO_SIXTY;
			else if (MORE_THAN_SIXTY.getName().equals(name))
				return MORE_THAN_SIXTY;
			else
				return null;
		}

		public String getName() {
			return name;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return this == obj;
		}

	}
	
	public static class Complexity {
		
		public final static Complexity EASY = new Complexity("easy");
		public final static Complexity MEDIUM = new Complexity("medium");
		public final static Complexity HARD = new Complexity("hard");

		private String name;
		private Complexity(String myName) {
			name = myName;
		}

		public static Complexity getComplexity(String name) {
			if (EASY.getName().equals(name))
				return EASY;
			else if (MEDIUM.getName().equals(name))
				return MEDIUM;
			else if (HARD.getName().equals(name))
				return HARD;
			else
				return null;
		}

		public String getName() {
			return name;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return this == obj;
		}
		
	}

	private String startInfo;
	private String endComment;
	private Complexity complexity;
	private Duration duration;
	private Set targetAudiences = new HashSet();

	public String getStartInfo() { return startInfo; }
	public void setStartInfo(String string) { startInfo = string; }

	public String getEndcomment() { return endComment; }
	public void setEndComment(String endComment) { this.endComment = endComment; }

	public Complexity getComplexity() { return complexity; }
	public void setComplexity(Complexity complexity) { this.complexity = complexity; }

	public Duration getDuration() { return duration; }
	public void setDuration(Duration duration) { this.duration = duration; }

	public Set getTargetAudiences() { return targetAudiences; }
	public void setTargetAudiences(Set targetAudiences) { this.targetAudiences = targetAudiences; }
	public void addTargetAudience(TargetAudience targetAudience) { targetAudiences.add(targetAudience); }
	public void removeTargetAudience(TargetAudience targetAudience) { targetAudiences.remove(targetAudience); }

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<AdditionalTrainData>\n");

		if (getTargetAudiences() != null && !getTargetAudiences().isEmpty()) {
			sb.append("<TargetAudiences>\n");
			Iterator iter = getTargetAudiences().iterator();
			while (iter.hasNext())
				sb.append("<TargetAudience>" + ((TargetAudience) iter.next()).getName() + "</TargetAudience>\n");
			sb.append("</TargetAudiences>\n");
		}
		if (getComplexity() != null)
			sb.append("<Complexity>" + getComplexity().getName() + "</Complexity>\n");
		if (getDuration() != null)
			sb.append("<Duration>" + getDuration().getName() + "</Duration>\n");
		if (getStartInfo() != null)
			sb.append("<Startinfo><![CDATA[" + getStartInfo() + "]]></Startinfo>\n");
		if (getEndcomment() != null)
			sb.append("<Endcomment><![CDATA[" + getEndcomment() + "]]></Endcomment>\n");
		
		sb.append("</AdditionalTrainData>\n");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof AdditionalTrainData))
			return false;
		if (obj == this)
			return true;
			
		AdditionalTrainData other = (AdditionalTrainData) obj;
		return
			((getComplexity() == null && other.getComplexity() == null)
				|| getComplexity().equals(other.getComplexity()))
			&& ((getTargetAudiences() == null && other.getTargetAudiences() == null)
				|| (getTargetAudiences().containsAll(other.getTargetAudiences())
					&& other.getTargetAudiences().containsAll(getTargetAudiences())))
			&& ((getDuration() == null && other.getDuration() == null)
				|| getDuration().equals(other.getDuration()))
			&& ((getStartInfo() == null && other.getStartInfo() == null)
				|| ignoreWSEquals(getStartInfo(), other.getStartInfo()))
			&& ((getEndcomment() == null && other.getEndcomment() == null)
				|| ignoreWSEquals(getEndcomment(), other.getEndcomment()));
	}
	
	private static boolean ignoreWSEquals(String s1, String s2) {
		return s1.replaceAll("\\s", "").equals(s2.replaceAll("\\s", ""));
	}
	
	public Object clone() {
		AdditionalTrainData atd = new AdditionalTrainData();

		atd.setTargetAudiences(new HashSet());
		Iterator iter = getTargetAudiences().iterator();
		while (iter.hasNext())
			atd.addTargetAudience((TargetAudience) iter.next());

		atd.setDuration(getDuration());

		atd.setComplexity(getComplexity());

		atd.setEndComment(new String(getEndcomment()));

		atd.setStartInfo(new String(getStartInfo()));

		return atd;
	}

}
