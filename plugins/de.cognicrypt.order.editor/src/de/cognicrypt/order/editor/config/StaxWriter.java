package de.cognicrypt.order.editor.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.cognicrypt.order.editor.Activator;
import de.cognicrypt.order.editor.Constants;
import de.cognicrypt.utils.Utils;

public class StaxWriter {
    private File f;

    public void setFile(String configFile) {
    	
    	final Bundle bundle = Platform.getBundle(de.cognicrypt.order.editor.Activator.PLUGIN_ID);
		
		 URL entry = null;
		 URL resolvedURL = null;
		 if (bundle == null) {
		 }
		 else {
			 entry = bundle.getEntry(Constants.RELATIVE_STATEMACHINE_CONFIG_DIR);
		 }
		 if (entry == null) {
			}
			try {
				resolvedURL = FileLocator.toFileURL(entry);
			} catch (IOException e8) {
				e8.printStackTrace();
			}
			java.net.URI resolvedURI = null;
			if (!(resolvedURL == null)) {
				try {
					resolvedURI = new java.net.URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}finally {
					
				}}
				else {
					try {
						resolvedURI = FileLocator.resolve(entry).toURI();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
		java.net.URI fileUri = null;
		 
		try {
			fileUri = new java.net.URI(resolvedURI + configFile);
		} catch (URISyntaxException e8) {
			e8.printStackTrace();
		}
		
		File file = new File(fileUri);
		
		if(!file.exists()) {
			 try {
				 file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		 }
		f = file;
    }    

	public void saveConfig() throws Exception {
    	
    	File folderJCA = new File(System.getProperty("user.home") + Constants.RELATIVE_JCA_FOLDER);
    	File folderBC = new File(System.getProperty("user.home") + Constants.RELATIVE_BC_FOLDER);
    	File folderBCJCA = new File(System.getProperty("user.home") + Constants.RELATIVE_BC_JCA_FOLDER);
    	File folderTink = new File(System.getProperty("user.home") + Constants.RELATIVE_TINK_FOLDER);
        
    	File[] listOfFilesJCA = folderJCA.listFiles();
    	File[] listOfFilesBC = folderBC.listFiles();
    	File[] listOfFilesBCJCA = folderBCJCA.listFiles();
    	File[] listOfFilesTink = folderTink.listFiles();
    	File[] all = concatAll(listOfFilesJCA, listOfFilesBC, listOfFilesBCJCA, listOfFilesTink);
    	
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = outputFactory
             .createXMLEventWriter(new FileOutputStream(f)); 
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
 
        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);

        eventWriter.add(eventFactory.createStartElement("",
                "", "folders"));
        
        for (int i = 0; i < all.length; i++) {
			if (all[i].isFile()) {
				if(all[i].getName().contains("crysl")) {

					StartElement folderStartElement = eventFactory.createStartElement("",
			                "", "cryslFile");
			        eventWriter.add(folderStartElement);
					createNode(eventWriter, "rule", all[i].getName());
			        createNode(eventWriter, "path", all[i].getAbsolutePath());
			        eventWriter.add(eventFactory.createEndElement("", "", "cryslFile"));
			        eventWriter.add(end);
				}
			}
        }
        
        eventWriter.add(end);

        eventWriter.add(eventFactory.createEndElement("", "", "folders"));
        eventWriter.add(end);
        
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
    }

    private void createNode(XMLEventWriter eventWriter, String name,
            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);

        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
      
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
    
    public static <T> T[] concatAll(T[] head, T[]... tail) {
    	  int totalLength = head.length;
    	  for (T[] array : tail) {
    	    totalLength += array.length;
    	  }
    	  T[] result = Arrays.copyOf(head, totalLength);
    	  int offset = head.length;
    	  for (T[] array : tail) {
    	    System.arraycopy(array, 0, result, offset, array.length);
    	    offset += array.length;
    	  }
    	  return result;
    }
    
    public File getFile() {
		return f;
	}
}
