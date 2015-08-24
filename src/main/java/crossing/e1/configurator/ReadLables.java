package crossing.e1.configurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
		
		InputStream input = null;

		try {

			input = new FileInputStream(path);
			prop.load(input);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public Properties getProperties() {
		return prop;
	}

}