package org.verapdf.arlington.linkHelpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DifferentKeysLinkHelper extends LinkHelper {

	private final List<String> keyNames;//empty string if default

	public DifferentKeysLinkHelper(String objectName, String ... keyNames) {
		super(objectName);
		this.keyNames = keyNames == null ? Collections.singletonList(null) : new LinkedList<>(Arrays.asList(keyNames));
	}

	public List<String> getKeyNames() {
		return keyNames;
	}
}
