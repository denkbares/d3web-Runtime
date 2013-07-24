/*
 * Copyright (C) 2009 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io.progress;

/**
 * A simple ProgressListener that prints the progress to the console
 * 
 * @author Volker Belli (denkbares GmbH)
 */
public class ConsoleProgressBarListener implements ProgressListener {

	private int lastProgress = -1;
	private static final int SIZE = 50;

	@Override
	public void updateProgress(float percent, String message) {
		int progress = (int) (percent * SIZE);
		if (lastProgress == -1) {
			System.out.print("| 0 % ");
			for (int i = 0; i < (SIZE - 18) / 2; i++) {
				System.out.print("\u00AF");
			}
			System.out.print(" 50 % ");
			for (int i = 0; i < (SIZE - 18) / 2; i++) {
				System.out.print("\u00AF");
			}
			System.out.println(" 100 % |");
			System.out.print("|");
			lastProgress = 0;
		}
		if (lastProgress == progress) return;

		for (int i = lastProgress + 1; i <= progress; i++) {
			System.out.print("=");
		}
		lastProgress = progress;
		if (progress == SIZE) System.out.println("|");
	}

}
