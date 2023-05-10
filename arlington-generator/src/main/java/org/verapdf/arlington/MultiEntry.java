package org.verapdf.arlington;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
}
