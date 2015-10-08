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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class WriteToFileHelper {

	public String getFileNameToBeSaved(Composite container, String name) {

		String pathToReturn = "";
		FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
		String[] names = new String[] { "XML Files", "All Files (*)" };
		String[] extensions = new String[] { "*.xml", "*" };
		String path = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32")) {
			names = new String[] { "XML Files", "All Files (*.*)" };
			extensions = new String[] { "*.xml", "*.*" };
			path = "c:\\";
		}
		dialog.setFilterNames(names);
		dialog.setFilterExtensions(extensions);
		dialog.setFilterPath(path);
		dialog.setFileName(name);
		pathToReturn = dialog.open();
		return pathToReturn;
	}

	public int writeToFile(String content, String path) {
		File file = new File(path);
		FileWriter writer = null;
		PrintWriter printer = null;
		try {
			writer = new FileWriter(file, false);
			printer = new PrintWriter(writer);
			printer.append(content);
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		} finally {
			if (printer != null) {
				printer.close();
			}
		}
		return 0;
	}
}