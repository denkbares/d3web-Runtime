/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.casegeneration;
public class Stopwatch {
    private long start;
    private long stop;
    
    public void start() {
        start = System.currentTimeMillis(); // start timing
    }
    
    public void stop() {
        stop = System.currentTimeMillis(); // stop timing
    }
    
    public long elapsedTimeMillis() {
        return stop - start;
    }
    
    public String toString() {
    	long elapsed = elapsedTimeMillis();
    	long sec = elapsed / 1000;
    	long min = elapsed / 60000;
        return "Elapsed time: " + min + " min, " + sec + " sec";
//        Long.toString(elapsedTimeMillis()); // print execution time
    }
}
