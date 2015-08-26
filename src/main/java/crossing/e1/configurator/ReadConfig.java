package crossing.e1.configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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

public class ReadConfig {

	private String path = "src/main/resources/config.properties";
	private Properties prop;

	public ReadConfig() {
		try {
		prop = new Properties();

		String configFile = Utilities.getAbsolutePath(path);
		
			prop.load(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getClaferPath() {
		return Utilities.getAbsolutePath(prop.getProperty("claferPath"));
	}
}