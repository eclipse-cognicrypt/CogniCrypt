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
 * @author Ram Kamath
 *
 */
package crossing.e1.configurator.utilities;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import crossing.e1.configurator.Activator;

/**
 * A helper class which writes string content to a file and it will be saved under project directory
 */
public class FileHelper {

	public static int deleteFile(final String fileName) {
		try {
			Files.delete(Paths.get(fileName));
		} catch (IOException e) {
			Activator.getDefault().logError(e);
			return 1;
		}
		return 0;
	}

}