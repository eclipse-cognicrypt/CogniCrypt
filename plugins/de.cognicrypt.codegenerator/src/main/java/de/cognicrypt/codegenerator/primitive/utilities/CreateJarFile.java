package de.cognicrypt.codegenerator.primitive.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.jarpackager.IJarExportRunnable;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

public class CreateJarFile {

	public static int BUFFER_SIZE = 10240;
	private Manifest manifest = new Manifest();

	public void createJarArchive(File archiveFile, File[] tobeJared) {
		try {
			byte buffer[] = new byte[BUFFER_SIZE];
			// Open archive file
			FileOutputStream stream = new FileOutputStream(archiveFile);
			JarOutputStream out = new JarOutputStream(stream, this.manifest);

			for (int i = 0; i < tobeJared.length; i++) {
				if (tobeJared[i] == null || !tobeJared[i].exists() || tobeJared[i].isDirectory())
					continue; // Just in case...
				System.out.println("Adding " + tobeJared[i].getName());

				// Add archive entry
				JarEntry jarAdd = new JarEntry(tobeJared[i].getName());
				jarAdd.setTime(tobeJared[i].lastModified());
				out.putNextEntry(jarAdd);

				// Write file to archive
				FileInputStream in = new FileInputStream(tobeJared[i]);
				while (true) {
					int nRead = in.read(buffer, 0, buffer.length);
					if (nRead <= 0)
						break;
					out.write(buffer, 0, nRead);
				}
				in.close();
			}

			out.close();
			stream.close();
			System.out.println("Adding completed OK");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: " + ex.getMessage());
		}
	}

	//Write the Manifest for the Jar file 
	public void createManifest(String owner, String[] classPaths) {
		Attributes global = this.manifest.getMainAttributes();
		global.put(Attributes.Name.MANIFEST_VERSION, "1.0.0");
		global.put(Attributes.Name.SPECIFICATION_TITLE, "Custom Provider");
		global.put(Attributes.Name.CONTENT_TYPE, "JCE Provider");
		global.put(new Attributes.Name("Created-By"), owner);
		for (String classPath : classPaths) {
			global.put(new Attributes.Name("Name"), classPath);
		}
	}

	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	public Manifest getManifest() {
		return this.manifest;
	}
}
