package de.cognicrypt.order.editor.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


import de.cognicrypt.order.editor.parser.StatemachineParser;


public class StaxParser {
	
	static final String CRYSLFILE = "cryslFile";
	static final String RULE = "rule";
    static final String PATH = "path";
    
    @SuppressWarnings({ "unchecked", "null" })
    public List<CryslFile> readConfig(String configFile) {
        List<CryslFile> cryslFileList = new ArrayList<CryslFile>();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            CryslFile cryslFile = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                        String elementName = startElement.getName().getLocalPart();
                        switch (elementName) {
                        case CRYSLFILE:
                        	cryslFile = new CryslFile();
                            break;
                        case RULE:
                            event = eventReader.nextEvent();
                            cryslFile.setRule(event.asCharacters().getData());
                            break;
                        case PATH:
                            event = eventReader.nextEvent();
                            cryslFile.setPath(event.asCharacters().getData());
                            break;
                        }
                }
                    if (event.isEndElement()) {
                        EndElement endElement = event.asEndElement();
                        if (endElement.getName().getLocalPart().equals(CRYSLFILE)) {
                        	cryslFileList.add(cryslFile);
                        }
                    }

            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        StatemachineParser.generate(cryslFileList);
        return cryslFileList;
    }
}

