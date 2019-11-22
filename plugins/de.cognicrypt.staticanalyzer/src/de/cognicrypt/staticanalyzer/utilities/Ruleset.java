package de.cognicrypt.staticanalyzer.utilities;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class Ruleset {
	private String url;
	private String folderName;
	private CCombo versions;
	private String selectedVersion = "";
	private boolean isChecked = false;
	private TableItem rulesRow;
	
	public Ruleset(Preferences subPrefs) throws BackingStoreException {
		String[] keys = subPrefs.keys();
		for (String key : keys) {
			switch (key) {
				case "SelectedVersion":
					this.selectedVersion = subPrefs.get(key, "");
					break;
				case "CheckboxState":
					this.isChecked = subPrefs.getBoolean(key, false);
					break;
				case "FolderName":
					this.folderName = subPrefs.get(key, "");
					break;
				case "Url":
					this.url = subPrefs.get(key, "");
					break;
				default:
					break;
			}
		}
	}

	public Ruleset(String url) {
		this.folderName = url.substring(url.lastIndexOf('/') + 1);
		this.url = url;
	}

	public Ruleset(String url, boolean checked) {
		this.folderName = url.substring(url.lastIndexOf('/') + 1);
		this.url = url;
		this.isChecked = checked;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public CCombo getVersions() {
		return versions;
	}

	public void setVersions(CCombo versions) {
		this.versions = versions;
	}

	public String getSelectedVersion() {
		return selectedVersion;
	}

	public void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public TableItem getRulesRow() {
		return rulesRow;
	}

	public void setRulesRow(TableItem rulesRow) {
		this.rulesRow = rulesRow;
	}
}

