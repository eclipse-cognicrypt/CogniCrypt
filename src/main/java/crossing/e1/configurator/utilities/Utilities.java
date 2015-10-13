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
 * @author Sarah Nadi
 *
 */
package crossing.e1.configurator.utilities;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import crossing.e1.configurator.Activator;

public class Utilities {

	public static String getAbsolutePath(String inputPath){
		String outputFile = null;
		
		try {
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			
			if (bundle == null) {
				//running as application
				String fileName = inputPath.substring(inputPath.lastIndexOf("/") + 1);
				
				outputFile = Utilities.class.getClassLoader().getResource(fileName).getPath();
			} else {
				// running as plugin
				Path originPath = new Path(inputPath);

				URL bundledFileURL = FileLocator.find(bundle, originPath, null);
				
				bundledFileURL = FileLocator.resolve(bundledFileURL);
				
				outputFile = new File(bundledFileURL.getFile()).getPath();
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return outputFile;
	}
	

}
