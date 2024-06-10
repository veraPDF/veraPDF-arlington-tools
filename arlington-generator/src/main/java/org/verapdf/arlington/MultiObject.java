package org.verapdf.arlington;

import javafx.util.Pair;

import java.util.*;

public class MultiObject extends Object {

	private JavaGeneration javaGeneration;

	private boolean pageContainsStructContentItemsProperty = false;
	private boolean imageIsStructContentItemProperty = false;
	private final Set<String> arraySizeProperties = new TreeSet<>();
	private final Set<Pair<String, String>> isInArrayProperties = new HashSet<>();
	private final Set<Pair<String, String>> isNameTreeIndexProperties = new HashSet<>();
	private final Set<Pair<String, String>> isNameTreeValueProperties = new HashSet<>();
	private final Set<Pair<String, String>> isNumberTreeIndexProperties = new HashSet<>();
	private final Set<Pair<String, String>> isNumberTreeValueProperties = new HashSet<>();
	private final Set<String> keysStringProperties = new TreeSet<>();
	private final Set<String> entriesStringProperties = new TreeSet<>();
	private final Set<String> containsEntriesProperties = new TreeSet<>();
	private final Map<String, Type> entriesValuesProperties = new TreeMap<>();
	private final Map<String, Type> entriesHasTypeProperties = new TreeMap<>();
	private final Map<String, String> entryNameToArlingtonObjectMap = new HashMap<>();
	private final Set<String> complexObjectProperties = new TreeSet<>();

	private final SortedSet<MultiEntry> entries;

	public MultiObject() {
		this.entries = new TreeSet<>();
	}

	public MultiObject(String id) {
		this.id = id;
		this.entries = new TreeSet<>();
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(id);
			if (object == null) {
				continue;
			}
			object.setMultiObject(this);
			getPossibleParents().addAll(object.getPossibleParents());
			for (Entry entry : object.getEntries()) {
				MultiEntry multiEntry = (MultiEntry)getEntry(entry.getName());
				if (multiEntry == null) {
					multiEntry = new MultiEntry(entry.getName());
				}
				multiEntry.getTypes().addAll(entry.getTypes());
				for (Map.Entry<Type,List<String>> en : entry.getLinks().entrySet()) {
					List<String> types = multiEntry.getLinks().computeIfAbsent(en.getKey(), k -> new LinkedList<>());
					for (String type : en.getValue()) {
						if (!types.contains(type)) {
							types.add(type);
						}
					}
				}
				entry.setMultiEntry(multiEntry);
				entries.add(multiEntry);
			}
		}
	}

	@Override
	public Set<MultiEntry> getEntries() {
		return entries;
	}

	@Override
	public Set<String> getArraySizeProperties() {
		return arraySizeProperties;
	}

	@Override
	public Set<Pair<String, String>> getIsInArrayProperties() {
		return isInArrayProperties;
	}

	@Override
	public Set<Pair<String, String>> getIsNameTreeIndexProperties() {
		return isNameTreeIndexProperties;
	}

	@Override
	public Set<Pair<String, String>> getIsNameTreeValueProperties() {
		return isNumberTreeValueProperties;
	}

	@Override
	public Set<Pair<String, String>> getIsNumberTreeIndexProperties() {
		return isNumberTreeIndexProperties;
	}

	@Override
	public Set<Pair<String, String>> getIsNumberTreeValueProperties() {
		return isNameTreeValueProperties;
	}

	@Override
	public Set<String> getKeysStringProperties() {
		return keysStringProperties;
	}

	@Override
	public Set<String> getEntriesStringProperties() {
		return entriesStringProperties;
	}

	@Override
	public Map<String, Type> getEntriesValuesProperties() {
		return entriesValuesProperties;
	}

	@Override
	public Map<String, Type> getEntriesHasTypeProperties() {
		return entriesHasTypeProperties;
	}

	@Override
	public Set<String> getContainsEntriesProperties() {
		return containsEntriesProperties;
	}

	@Override
	public void setPageContainsStructContentItemsProperty(boolean pageContainsStructContentItemsProperty) {
		this.pageContainsStructContentItemsProperty = pageContainsStructContentItemsProperty;
	}

	public boolean getPageContainsStructContentItemsProperty() {
		return pageContainsStructContentItemsProperty;
	}

	@Override
	public Map<String, String> getEntryNameToArlingtonObjectMap() {
		return entryNameToArlingtonObjectMap;
	}

	public boolean getImageIsStructContentItemProperty() {
		return imageIsStructContentItemProperty;
	}

	@Override
	public void setImageIsStructContentItemProperty(boolean imageIsStructContentItemProperty) {
		this.imageIsStructContentItemProperty = imageIsStructContentItemProperty;
	}

	@Override
	public JavaGeneration getJavaGeneration() {
		return javaGeneration;
	}

	public void setJavaGeneration(JavaGeneration javaGeneration) {
		this.javaGeneration = javaGeneration;
	}

	@Override
	public Set<String> getComplexObjectProperties() {
		return complexObjectProperties;
	}
}
