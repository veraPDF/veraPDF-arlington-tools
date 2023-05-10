package org.verapdf.arlington;

import java.util.*;

public class MultiObject extends Object {

	private JavaGeneration javaGeneration;

	private boolean pageContainsStructContentItemsProperty = false;
	private boolean imageIsStructContentItemProperty = false;
	private final Set<String> extensionProperties = new HashSet<>();
	private final Set<String> arraySizeProperties = new HashSet<>();
	private final Set<String> keysStringProperties = new HashSet<>();
	private final Set<String> entriesStringProperties = new HashSet<>();
	private final Set<String> containsEntriesProperties = new HashSet<>();
	private final Map<String, Type> entriesValuesProperties = new HashMap<>();
	private final Map<String, String> entryNameToArlingtonObjectMap = new HashMap<>();

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
			for (Entry entry : object.getEntries()) {
				MultiEntry multiEntry = (MultiEntry)getEntry(entry.getName());
				if (multiEntry == null) {
					multiEntry = new MultiEntry(entry.getName());
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

	public Set<String> getExtensionProperties() {
		return extensionProperties;
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
}
