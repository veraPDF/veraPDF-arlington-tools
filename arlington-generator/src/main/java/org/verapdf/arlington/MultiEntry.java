package org.verapdf.arlington;

import javafx.util.Pair;

import java.util.*;

public class MultiEntry extends Entry {

	private Boolean containsProperty = false;
	private Boolean indirectProperty = false;
	private Boolean hexStringProperty = false;
	private Boolean fieldNameProperty = false;
	private Boolean arraySizeProperty = false;
	private Boolean stringSizeProperty = false;
	private Boolean streamSizeProperty = false;
	private Boolean rectWidthProperty = false;
	private Boolean rectHeightProperty = false;
	private Boolean entriesStringProperty = false;
	private Boolean hasCycleProperty = false;
	private final Set<String> inNameTreeProperties = new HashSet<>();
	private final SortedSet<Type> typeValueProperties = new TreeSet<>();
	private final SortedSet<Type> hasTypeProperties = new TreeSet<>();
	private final Set<Integer> arraySortAscendingProperties = new HashSet<>();

	public MultiEntry(String name) {
		setName(name);
	}

	public void setContainsProperty(Boolean containsProperty) {
		this.containsProperty = containsProperty;
	}

	public Boolean getContainsProperty() {
		return containsProperty;
	}

	public void addTypeValueProperty(Type type) {
		typeValueProperties.add(type);
	}

	public SortedSet<Type> getTypeValueProperties() {
		return typeValueProperties;
	}

	public void addHasTypeProperty(Type type) {
		hasTypeProperties.add(type);
	}

	public SortedSet<Type> getHasTypeProperties() {
		return hasTypeProperties;
	}

	public Boolean getHexStringProperty() {
		return hexStringProperty;
	}

	public void setHexStringProperty(Boolean hexStringProperty) {
		this.hexStringProperty = hexStringProperty;
	}

	public Boolean getArraySizeProperty() {
		return arraySizeProperty;
	}

	public void setArraySizeProperty(Boolean arraySizeProperty) {
		this.arraySizeProperty = arraySizeProperty;
	}

	public Boolean getStringSizeProperty() {
		return stringSizeProperty;
	}

	public void setStringSizeProperty(Boolean stringSizeProperty) {
		this.stringSizeProperty = stringSizeProperty;
	}

	public Boolean getStreamSizeProperty() {
		return streamSizeProperty;
	}

	public void setStreamSizeProperty(Boolean streamSizeProperty) {
		this.streamSizeProperty = streamSizeProperty;
	}

	public Boolean getRectWidthProperty() {
		return rectWidthProperty;
	}

	public void setRectWidthProperty(Boolean rectWidthProperty) {
		this.rectWidthProperty = rectWidthProperty;
	}

	public Boolean getRectHeightProperty() {
		return rectHeightProperty;
	}

	public void setFieldNameProperty(Boolean fieldNameProperty) {
		this.fieldNameProperty = fieldNameProperty;
	}

	public Boolean getFieldNameProperty() {
		return fieldNameProperty;
	}

	public void setRectHeightProperty(Boolean rectHeightProperty) {
		this.rectHeightProperty = rectHeightProperty;
	}

	public Boolean getIndirectProperty() {
		return indirectProperty;
	}

	public void setIndirectProperty(Boolean indirectProperty) {
		this.indirectProperty = indirectProperty;
	}

	public Set<Integer> getArraySortAscendingProperties() {
		return arraySortAscendingProperties;
	}

	public Boolean getEntriesStringProperty() {
		return entriesStringProperty;
	}

	public void setEntriesStringProperty(Boolean entriesStringProperty) {
		this.entriesStringProperty = entriesStringProperty;
	}

	public Set<String> getInNameTreeProperties() {
		return inNameTreeProperties;
	}

	public Boolean getHasCycleProperty() {
		return hasCycleProperty;
	}

	public void setHasCycleProperty(Boolean hasCycleProperty) {
		this.hasCycleProperty = hasCycleProperty;
	}

	public static Map<String, List<PDFVersion>> getDefaultValueMap(Object multiObject, String entryName) {
		Map<String, List<PDFVersion>> map = new TreeMap<>();
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(multiObject.getId());
			if (object == null) {
				continue;
			}
			Entry entry = object.getEntry(entryName);
			if (entry == null) {
				continue;
			}
			Pair<Type, String> defaultPair = entry.getDefaultValue();
			if (defaultPair == null) {
				continue;
			}
			Type type = defaultPair.getKey();
			String defaultValue = defaultPair.getValue();
			if (defaultValue == null || defaultValue.startsWith("@") || type == Type.ARRAY) {
				continue;
			}
			if (defaultValue.startsWith("@")) {
				defaultValue = JavaGeneration.getGetterName(Entry.getValuePropertyName(entryName)) + "()";
			} else if (!defaultValue.contains(PredicatesParser.PREDICATE_PREFIX)) {
				if (type != Type.MATRIX && type != Type.RECTANGLE) {
					defaultValue = PredicatesParser.removeSquareBrackets(defaultValue);
				}
				defaultValue = PredicatesParser.removeQuotes(defaultValue);
				defaultValue = type.getCreationCOSObject(type.getValueWithSeparator(defaultValue + type.getJavaPostfix()));
			} else {
				defaultValue = new PredicatesParser(object, entry, version, type, Constants.DEFAULT_VALUE_COLUMN,
						false).parse(defaultValue);
				if (defaultValue == null) {
					continue;
				}
			}
			List<PDFVersion> list = map.get(defaultValue);
			if (list != null) {
				list.add(version);
			} else {
				list = new LinkedList<>();
				list.add(version);
				map.put(defaultValue, list);
			}
		}
		return map;
	}
}

