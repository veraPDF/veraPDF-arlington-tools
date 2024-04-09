package org.verapdf.arlington;

import java.util.*;

public class MultiObject extends Object {

	private JavaGeneration javaGeneration;

	private boolean pageContainsStructContentItemsProperty = false;
	private boolean imageIsStructContentItemProperty = false;
	private final Set<String> extensionProperties = new TreeSet<>();
	private final Set<String> arraySizeProperties = new TreeSet<>();
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

	public Set<String> getArraySizeProperties() {
		return arraySizeProperties;
	}

	public Set<String> getKeysStringProperties() {
		return keysStringProperties;
	}

	public Set<String> getEntriesStringProperties() {
		return entriesStringProperties;
	}

	public Map<String, Type> getEntriesValuesProperties() {
		return entriesValuesProperties;
	}

	public Map<String, Type> getEntriesHasTypeProperties() {
		return entriesHasTypeProperties;
	}

	public Set<String> getContainsEntriesProperties() {
		return containsEntriesProperties;
	}

	public void setPageContainsStructContentItemsProperty(boolean pageContainsStructContentItemsProperty) {
		this.pageContainsStructContentItemsProperty = pageContainsStructContentItemsProperty;
	}

	public boolean getPageContainsStructContentItemsProperty() {
		return pageContainsStructContentItemsProperty;
	}

	public Map<String, String> getEntryNameToArlingtonObjectMap() {
		return entryNameToArlingtonObjectMap;
	}

	public boolean getImageIsStructContentItemProperty() {
		return imageIsStructContentItemProperty;
	}

	public void setImageIsStructContentItemProperty(boolean imageIsStructContentItemProperty) {
		this.imageIsStructContentItemProperty = imageIsStructContentItemProperty;
	}

	public JavaGeneration getJavaGeneration() {
		return javaGeneration;
	}

	public void setJavaGeneration(JavaGeneration javaGeneration) {
		this.javaGeneration = javaGeneration;
	}

	public Set<String> getComplexObjectProperties() {
		return complexObjectProperties;
	}
}
