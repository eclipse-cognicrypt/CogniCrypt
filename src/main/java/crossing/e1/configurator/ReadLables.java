package crossing.e1.configurator;

import java.io.FileInputStream;
import java.util.Properties;

import crossing.e1.configurator.utilities.Utilities;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;

/**
 * @author Ram
 *
 */

public class ReadLables {
	Properties prop = new Properties();
	public ReadLables(String path) {
		try {

		String configFile = Utilities.getAbsolutePath(path);
		
		prop.load(new FileInputStream(configFile));
		
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Properties getProperties() {
		return prop;
	}

}