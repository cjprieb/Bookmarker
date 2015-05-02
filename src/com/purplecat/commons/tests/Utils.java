package com.purplecat.commons.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Utils {
	
	/**
	 * 
	 * @param cls
	 * @param fileName fileName from cls's package; prepend with '/'
	 * @return
	 */
	public static List<String> getFile(Class<?> cls, String fileName) {
		List<String> lines = new LinkedList<String>();
		
		fileName = cls.getPackage().getName().replace('.', '/') + fileName;
		
		ClassLoader classLoader = cls.getClassLoader();
		if ( classLoader == null ) {
			throw new NullPointerException("class loader is null");
		}
		URL url = classLoader.getResource(fileName);
		if ( url == null ) {
			throw new NullPointerException("resource is null for " + fileName);
		}
		if ( url.getFile() == null ) {
			throw new NullPointerException("resource file is null");
		}
		File file = new File(url.getFile());
		
		try (Scanner scanner = new Scanner(file) ) {
			while ( scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lines;
	}
	
	public static int parseInt(String s, int def) {
		int result = def;
		if ( s != null && s.length() > 0 ) {
			try { result = Integer.parseInt(s); }
			catch (NumberFormatException e) { result = def; }
		}
		return result;
	}
	
	public static void copyFiles( File from, File to ) throws IOException {
		IOException error = null;
		if ( !to.getParentFile().exists() ) { to.getParentFile().mkdirs(); }
	    if ( !to.exists() ) { to.createNewFile(); }

        FileChannel in = null;
        FileChannel out = null; 

	    try {
	        in = new FileInputStream( from ).getChannel();
	        out = new FileOutputStream( to ).getChannel();

	        out.transferFrom( in, 0, in.size() );
	    }
	    catch (IOException e) {
	    	error = e;
	    }
	    finally {
	    	if ( in != null ) in.close();
	    	if ( out != null ) out.close();
	    }
	    if ( error != null ) {
	    	throw error;
	    }
	}
}
