package crossing.e1.primitiveintegration.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.core.internal.resources.Folder;
import org.junit.Test;


public class testghv {

	@Test
	public void test() {
		File dir = new File("C:\\Program Files\\Java\\");
		
		   System.out.println(lastFileModified("C:\\Program Files\\Java\\"));
		
		
	}
	
	
	public static File lastFileModified(String dir) {
	    File fl = new File(dir);
	    FileFilter fileFilter = new WildcardFileFilter("jdk*");
	    File[] files = fl.listFiles(fileFilter);
	    long lastMod = Long.MIN_VALUE;
	    File choice = null;
	    for (File file : files) {
	        if (file.lastModified() > lastMod ) {
	            choice = file;
	            lastMod = file.lastModified();
	        }
	    }
	    return choice;
	}

}
