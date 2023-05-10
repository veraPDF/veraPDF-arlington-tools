package org.verapdf.arlington.linkHelpers;

public class SizeLinkHelper extends LinkHelper {

	private final int size;

	public SizeLinkHelper(String objectName, int size) {
		super(objectName);
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
