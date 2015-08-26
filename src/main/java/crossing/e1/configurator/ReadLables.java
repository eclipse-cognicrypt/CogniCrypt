package crossing.e1.configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.clafer.javascript.Javascript;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

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