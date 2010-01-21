package de.d3web.persistence.tests.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;

public class Butil {

    public static String readBytes(Reader r) {
        int zeichen = 0;
        LinkedList<Integer> ints = new LinkedList<Integer>();
        while (true) {

            try {
                zeichen = r.read();
            } catch (IOException e) {
                break;
            } catch (OutOfMemoryError e1) {
                break;
            }
            
            if (zeichen == -1) {                
                break;
            }
            
            ints.add(zeichen);
        }

        StringBuilder buffi = new StringBuilder();
        for (Integer i : ints) {
            
            if ((i.intValue() == 128) || (i.intValue() == 228)
                    || (i.intValue() == 252) || (i.intValue() == 246)
                    || (i.intValue() == 214) || (i.intValue() == 196)
                    || (i.intValue() == 220) || (i.intValue() == 223)) {
                if (i.intValue() == 128) {
                    buffi.append('€');
                }
                if (i.intValue() == 228) {
                    buffi.append('ä');
                }
                if (i.intValue() == 252) {
                    buffi.append('ü');
                }
                if (i.intValue() == 246) {
                    buffi.append('ö');
                }
                if (i.intValue() == 214) {
                    buffi.append('ü');
                }
                if (i.intValue() == 196) {
                    buffi.append('Ö');
                }
                if (i.intValue() == 220) {
                    buffi.append('Ü');
                }
                if (i.intValue() == 223) {
                    buffi.append('ß');
                }
            } else {
                buffi.append(((char) i.intValue()));
            }
        }
        return buffi.toString();
    }
   
    public static String ReaderToString(Reader r) {
        return readBytes(r).replace('@', '%');
    }

    /**
     * Reads from a Reader with UTF8 encoding. 
     * 
     * @param org
     * @return
     */
	public static String readString(InputStream s) {
		StringBuffer buffi = new StringBuffer();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(s, "UTF8"));
			String b = r.readLine();
			while(b != null) {
				buffi.append(b);
				b = r.readLine();
			}
			return buffi.toString();
		} catch (Exception e) {
			// Nothing
		}
		return null;
	}
}
