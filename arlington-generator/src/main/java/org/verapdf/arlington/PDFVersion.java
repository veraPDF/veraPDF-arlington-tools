package org.verapdf.arlington;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.util.*;

@XmlType
@XmlEnum()
public enum PDFVersion {
	@XmlEnumValue("1.0") VERSION1_0(1, 0),
	@XmlEnumValue("1.1") VERSION1_1(1, 1),
	@XmlEnumValue("1.2") VERSION1_2(1, 2),
	@XmlEnumValue("1.3") VERSION1_3(1, 3),
	@XmlEnumValue("1.4") VERSION1_4(1, 4),
	@XmlEnumValue("1.5") VERSION1_5(1, 5),
	@XmlEnumValue("1.6") VERSION1_6(1, 6),
	@XmlEnumValue("1.7") VERSION1_7(1, 7),
	@XmlEnumValue("2.0") VERSION2_0(2, 0);

	private final int version;
	private final int subversion;
	private PrintWriter profileWriter;
	private final Map<String, Object> objectIdMap = new HashMap<>();

	PDFVersion(int version, int subversion) {
		this.version = version;
		this.subversion = subversion;
		try {
			this.profileWriter = new PrintWriter(new FileWriter("ARLINGTON" + version + "-" + subversion + ".xml"));
		} catch (IOException ignored) {
		}
	}

	public String getString() {
		return version + "." + subversion;
	}

	public String getStringWithUnderScore() {
		return getString().replace(".","_");
	}

	public int getVersion () {
		return version;
	}

	public int getSubversion() {
		return subversion;
	}

	public PrintWriter getProfileWriter() {
		return profileWriter;
	}

 	public Map<String, Object> getObjectIdMap() {
		return objectIdMap;
	}

	public static int compare(PDFVersion version1, PDFVersion version2) {
		if (version1.version != version2.version) {
			return version1.version - version2.version;
		}
		return version1.subversion - version2.subversion;
	}

	public String getSpecification() {
		return (compare(this, PDFVersion.VERSION1_6) > 0 ? "ISO_32000_" : "PDF_") + getStringWithUnderScore();
	}

	public static PDFVersion getPDFVersion(String string) {
		if (string == null) {
			return null;
		}
		for (PDFVersion version : values()) {
			if (string.equals(version.getString())) {
				return version;
			}
		}
		return null;
	}
}
