package de.cognicrypt.order.editor.config;

public class CryslFile { //represents a CryslFile object from config.xml
	
	private String rule;
    private String path;
    
    public String getRule() {
		return rule;
	}
    
    public String getRuleName() {
		return rule.substring(0, rule.length()-6);
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
