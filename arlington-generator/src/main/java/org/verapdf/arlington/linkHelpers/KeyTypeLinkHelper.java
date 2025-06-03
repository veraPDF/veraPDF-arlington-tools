package org.verapdf.arlington.linkHelpers;

import org.verapdf.arlington.Type;

public class KeyTypeLinkHelper extends LinkHelper {

	private final String keyName;
	private final Type[] types;
	private final boolean isDefault;

	public KeyTypeLinkHelper(String objectName, String keyName, Type[] types, boolean isDefault) {
		super(objectName);
		this.keyName = keyName;
		this.types = types;
		this.isDefault = isDefault;
	}

	public Type[] getTypes() {
		return types;
	}

	public String getKeyName() {
		return keyName;
	}

	public boolean isDefault() {
		return isDefault;
	}
}
