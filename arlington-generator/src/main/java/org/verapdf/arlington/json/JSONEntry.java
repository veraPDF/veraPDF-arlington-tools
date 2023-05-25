package org.verapdf.arlington.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.util.Pair;
import org.verapdf.arlington.PDFVersion;
import org.verapdf.arlington.PredicatesParser;
import org.verapdf.arlington.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JSONEntry {
	protected String name;
	@JsonProperty("Type")
	protected List<JSONValue> types;
	@JsonProperty("Link")
	protected List<List<JSONValue>> links;
	@JsonProperty("IndirectReference")
	protected List<JSONValue> indirectReference;
	@JsonProperty("Inheritable")
	protected Boolean inheritable;
	@JsonProperty("Note")
	protected String note;
	@JsonProperty("PossibleValues")
	protected List<List<JSONValue>> possibleValues;
	@JsonProperty("Required")
	protected List<JSONValue> requiredList;
	@JsonProperty("SinceVersion")
	protected JSONValue sinceVersion;
	@JsonProperty("DeprecatedIn")
	protected JSONValue deprecatedValue;
	@JsonProperty("DefaultValue")
	protected List<JSONValue> defaultValue;
	@JsonProperty("SpecialCase")
	protected List<JSONValue> specialCases;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Type> getTypes() {
		List<Type> result = new LinkedList<>();
		for (JSONValue value : types) {
			Type type = Type.getType(value.getValue());
			if (type != null) {
				result.add(type);
			} else {
				String typeString = PredicatesParser.getPredicateLastArgument(value.getValue());
				type = Type.getType(typeString);
				if (type != null) {
					result.add(type);
				}
			}
		}
		return result;
	}

	public List<String> getTypesPredicates() {
		return types.stream().map(JSONValue::getValue).collect(Collectors.toList());
	}

	public String getRequired() {
		return requiredList != null ? requiredList.get(0).getValue() : null;
	}

	public List<String> getPossibleValues(int i) {
		return getLinks(possibleValues, i);
	}

	public String getIndirectReference(int i) {
		if (indirectReference == null) {
			return null;
		}
		if (indirectReference.size() >= i && indirectReference.get(i) != null) {
			return indirectReference.get(i).getValue();
		} else if (indirectReference.size() == 1 && indirectReference.get(0) != null) {
			return indirectReference.get(0).getValue();
		}
		return null;
	}

	public List<String> getLinks(int i) {
		return getLinks(links, i);
	}

	public List<String> getLinks(List<List<JSONValue>> list, int i) {
		if (list == null) {
			return null;
		}
		if (list.size() == 1) {
			if (list.get(i) != null) {
				List<String> result = new LinkedList<>();
				for (JSONValue value : list.get(i)) {
					if (value.getValue() != null) {
						result.add(value.getValue());
					} else {
						result.add("[" + String.join(",", value.getValues()) + "]");
					}
				}
				return result;
			}
			return null;
		} else {
			if (list.get(i) != null) {
				List<String> result = new LinkedList<>();
				for (JSONValue value : list.get(i)) {
					if (value.getValue() != null) {
						result.add(value.getValue());
					} else {
						result.addAll(value.getValues());
					}
				}
				return result;
			}
			return null;
		}
	}

	public Boolean getInheritable() {
		return inheritable;
	}

	public PDFVersion getDeprecatedVersion() {
		return PDFVersion.getPDFVersion(getDeprecatedString());
	}

	public Pair<Type, String> getDefaultValue() {
		if (defaultValue == null) {
			return null;
		}
		for (int i = 0; i < defaultValue.size(); i++) {
			JSONValue value = defaultValue.get(i);
			if (value != null) {
				return new Pair<>(getTypes().get(i), value.getValue());
			}
		}
		return null;
	}

	public String getDeprecatedString() {
		return deprecatedValue != null ? deprecatedValue.getValue() : null;
	}

	public String getSpecialCase(int i) {
		return specialCases != null &&  specialCases.get(i) != null ? specialCases.get(i).getValue() : null;
	}
}
