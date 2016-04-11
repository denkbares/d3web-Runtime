package de.d3web.utils;

import java.util.regex.Pattern;

public enum OS {
	WINDOWS("^Windows"),
	MAC_OS("^Mac OS"),
	UNIX("^(?:AIX|Digital Unix|Epoc32|FreeBSD|HP UX|IRIX|Linux|NetBSD|OpenBSD|Solaris|SunOS)"),
	OTHER(".*"); // take the rest

	private final Pattern pattern;
	private static final OS currentOS = findOS(System.getProperty("os.name"));

	private OS(String regex) {
		this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	public static OS findOS(String displayName) {
		OS matchesOS = OTHER;
		for (OS os : OS.values()) {
			if (os.pattern.matcher(displayName).find()) {
				matchesOS = os;
				break;
			}
		}
		return matchesOS;
	}

	public boolean isCurrentOS() {
		return this.equals(getCurrentOS());
	}

	public static OS getCurrentOS() {
		return currentOS;
	}
}