package de.cognicrypt.utils;

public class JavaVersion implements Comparable<JavaVersion> {
	/**
	 * This class is used to compare different java version strings.
	 * Ex: If a and b are JavaVersion objects, then
	 * 			returns -1 (a<b)
	 * 			returns 0  (a=b)
	 * 			returns 1  (a>b)
	 * */

    private String version;

    public final String get() {
        return this.version;
    }

    public JavaVersion(String version) {
        if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        this.version = version;
    }

    @Override public int compareTo(JavaVersion that) {
        if(that == null)
            return 1;
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.min(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override public boolean equals(Object that) {
        if(this == that)
            return true;
        if(that == null)
            return false;
        if(this.getClass() != that.getClass())
            return false;
        return this.compareTo((JavaVersion) that) == 0;
    }

}
