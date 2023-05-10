package org.verapdf.arlington.linkHelpers;

import org.verapdf.arlington.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Key {
	private final String keyName;
	private final Type type;
	private final Integer bit;
	private final boolean isParent;
	private final boolean isDefault;
	private final Set<String> keyValues;

	public Key(String keyName, Type type, String ... keyValues) {
		this(keyName, type, false, false, keyValues);
	}

	public Key(String keyName, Type type, Integer bit, String ... keyValues) {
		this(keyName, type, bit, false, false, keyValues);
	}

	public Key(String keyName, Type type, boolean isDefault, boolean isParent, String ... keyValues) {
		this(keyName, type, null, isDefault, isParent, keyValues);
	}

	public Key(String keyName, Type type, Integer bit, boolean isDefault, boolean isParent, String ... keyValues) {
		this.keyName = keyName;
		this.type = type;
		this.bit = bit;
		this.keyValues = new HashSet<>();
		this.keyValues.addAll(Arrays.asList(keyValues));
		this.isDefault = isDefault;
		this.isParent = isParent;
	}

	public String getKeyName() {
		return keyName;
	}

	public Type getType() {
		return type;
	}

	public Set<String> getKeyValues() {
		return keyValues;
	}

	public boolean isParent() {
		return isParent;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public Integer getBit() {
		return bit;
	}

}
