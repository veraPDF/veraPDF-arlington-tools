package org.verapdf.arlington.linkHelpers;

public class DifferentKeysValuesLinkHelper extends LinkHelper {

	private final Key key;

	public DifferentKeysValuesLinkHelper(String objectName, Key key) {
		super(objectName);
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
}
