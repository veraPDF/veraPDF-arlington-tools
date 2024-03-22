package org.verapdf.arlington.linkHelpers;

public class KeyNameLinkHelper extends LinkHelper {

	private final String keyName;
	private final boolean checkCollectionName;

	public KeyNameLinkHelper(String objectName, String keyName, boolean checkCollectionName) {
		super(objectName);
		this.keyName = keyName;
		this.checkCollectionName = checkCollectionName;
	}

	public String getKeyName() {
		return keyName;
	}

	public boolean isCheckCollectionName() {
		return checkCollectionName;
	}
}
