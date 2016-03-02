/**
 * Copyright 2015 Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Ram
 *
 */

package crossing.e1.configurator.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import crossing.e1.configurator.Activator;

/**
 * An utility class which reads the configuration file .
 *
 * @author Ram
 *
 */
public class ReadConfig {

	private final String path = "src/main/resources/config.properties";
	private Properties prop;

	public ReadConfig() {
		try {
			this.prop = new Properties();
			final String configFile = Utilities.getAbsolutePath(this.path);
			this.prop.load(new FileInputStream(configFile));
		} catch (final IOException e) {
			Activator.getDefault().logError(e);
		}

	}

	/**
	 * Used only when path has to be read
	 *
	 * @param key
	 * @return absolute path
	 */
	public String getPathFromConfig(final String key) {
		return Utilities.getAbsolutePath(this.prop.getProperty(key));
	}

	public String getValue(final String key) {
		return this.prop.getProperty(key);
	}
}