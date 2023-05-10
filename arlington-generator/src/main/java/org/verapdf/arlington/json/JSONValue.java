package org.verapdf.arlington.json;

import java.util.List;

public class JSONValue {

	private String value;
	private List<String> values;

	public JSONValue(String value) {
		this.value = value;
	}

	public JSONValue(Integer value) {
		this.value = value.toString();
	}

	public JSONValue(Boolean value) {
		this.value = value.toString();
	}

	public JSONValue(Double value) {
		this.value = Double.toString(value);
	}

	public JSONValue(List<String> values) {
		this.values = values;
	}

	public String getValue() {
		return value;
	}

	public List<String> getValues() {
		return values;
	}
}
