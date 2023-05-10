package org.verapdf.arlington.linkHelpers;

public class KeyNameLinkHelper extends LinkHelper {

	private final String keyName;

	public KeyNameLinkHelper(String objectName, String keyName) {
		super(objectName);
		this.keyName = keyName;
	}

	public String getKeyName() {
		return keyName;
	}
}
