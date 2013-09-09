package de.d3web.utils;

import java.util.regex.Pattern;

public enum OS {
	WINDOWS("^Windows"),
	MAC_OS("^Mac OS"),
	UNIX("^(?:AIX|Digital Unix|Epoc32|FreeBSD|HP UX|IRIX|Linux|NetBSD|OpenBSD|Solaris|SunOS)"),
	OTHER(".*"); // take the rest

	private final Pattern pattern;
	private static final OS currentOS;

	static {
		OS matchesOS = OTHER;
		for (OS os : OS.values()) {
			if (os.pattern.matcher(System.getProperty("os.name")).find()) {
				matchesOS = os;
				break;
			}
		}
		currentOS = matchesOS;
	}

	private OS(String regex) {
		this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	public boolean isCurrentOS() {
		return this.equals(currentOS);
	}

	public static OS getCurrentOS() {
		return currentOS;
	}
}