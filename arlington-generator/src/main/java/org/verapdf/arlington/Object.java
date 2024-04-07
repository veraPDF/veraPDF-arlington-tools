package org.verapdf.arlington;

import java.util.*;
import java.util.stream.Collectors;

public class Object {
//	@XmlAttribute
	protected String id;
//	@XmlElement(name = "ENTRY")
	private final SortedSet<Entry> entries;
	private final Set<String> possibleParents;
	private MultiObject multiObject;

	public Object() {
		this(null, new TreeSet<>());
	}

	public Object(String id, SortedSet<Entry> entries) {
		this(id, entries, new HashSet<>());
	}

	public Object(String id, SortedSet<Entry> entries, Set<String> possibleParents) {
		this.id = id;
		this.entries = entries;
		this.possibleParents = possibleParents;
	}

	public String getId() {
		return id;
	}

	public String getObjectName() {
		return id;
	}

	public Set<? extends Entry> getEntries() {
		return entries;
	}

	public void addEntry(Entry entry) {
		entries.add(entry);
	}

	public List<? extends Entry> getNumberStarEntries() {
		return getEntries().stream().filter(Entry::isNumberWithStar)
				.sorted(Comparator.comparingInt(Entry::getNumberWithStar)).collect(Collectors.toList());
	}

	public Set<String> getEntriesNames() {
		return getEntries().stream().map(Entry::getName).collect(Collectors.toCollection(TreeSet::new));
	}

	public Entry getEntry(String entryName) {
		for (Entry entry : getEntries()) {
			if (entryName.equals(entry.getName())) {
				return entry;
			}
		}
		return null;
	}
	public Entry getEntry(Integer entryName) {
		return getEntry(entryName.toString());
	}

	public String getModelType() {
		return getModelType(getId());
	}
	
	public static String getModelType(String id) {
		return "A" + id;
	}

	public String getJavaClassName() {
		return getJavaClassName(getId());
	}

	public static String getJavaClassName(String id) {
		return "GF" + getModelType(id);
	}

	public boolean isArray() {
		return isArray(getId());
	}

	public static boolean isArray(String id) {
		return id.endsWith("Array") || id.endsWith("ColorSpace") || (id.startsWith("ArrayOf") && !id.endsWith(Type.ENTRY.getType()));
	}

	public boolean isStream() {
		return getEntries().stream().map(Entry::getName).anyMatch(s -> s.equals("DecodeParms"));
	}

	public boolean isNameTree() {
		return isNameTree(getId());
	}

	public static boolean isNameTree(String id) {
		return id.contains("NameTree") && !id.endsWith(Type.ENTRY.getType()) && !id.contains("NameTreeNode");
	}

	public boolean isNumberTree() {
		return isNumberTree(getId());
	}

	public static boolean isNumberTree(String id) {
		return id.contains("NumberTree") && !id.endsWith(Type.ENTRY.getType()) && !id.contains("NumberTreeNode");
	}

	public boolean isSubArray() {
		return isSubArray(id);
	}

	public static boolean isSubArray(String id) {
		return id.endsWith("SubArray");
	}

	public boolean isDictionary() {
		return !isArray() && !isNameTree() && !isNumberTree() && !isSubArray() && !isEntry() && !isStream();
	}

	public boolean isEntry() {
		return getEntries().size() == 1 && Constants.CURRENT_ENTRY.equals(getEntries().iterator().next().getName());
	}

	public static String getObjectEntryName(String objectId) {
		return objectId + Type.ENTRY.getType();
	}

	public Set<String> getPossibleParents() {
		return possibleParents;
	}

	public Set<String> getArraySizeProperties() {
		return multiObject.getArraySizeProperties();
	}

	public static String getHasExtensionPropertyName(String extensionName) {
		return "hasExtension" + extensionName;
	}

	public Set<String> getExtensionProperties() {
		return multiObject.getExtensionProperties();
	}

	public Set<String> getKeysStringProperties() {
		return multiObject.getKeysStringProperties();
	}

	public Set<String> getComplexObjectProperties() {
		return multiObject.getComplexObjectProperties();
	}

	public Set<String> getEntriesStringProperties() {
		return multiObject.getEntriesStringProperties();
	}

	public Map<String, Type> getEntriesValuesProperties() {
		return multiObject.getEntriesValuesProperties();
	}

	public Map<String, Type> getEntriesHasTypeProperties() {
		return multiObject.getEntriesHasTypeProperties();
	}

	public Set<String> getContainsEntriesProperties() {
		return multiObject.getContainsEntriesProperties();
	}

	public void setPageContainsStructContentItemsProperty(boolean pageContainsStructContentItemsProperty) {
		multiObject.setPageContainsStructContentItemsProperty(pageContainsStructContentItemsProperty);
	}

	public void setImageIsStructContentItemProperty(boolean imageIsStructContentItemProperty) {
		multiObject.setImageIsStructContentItemProperty(imageIsStructContentItemProperty);
	}

	public void setMultiObject(MultiObject multiObject) {
		this.multiObject = multiObject;
	}

	public MultiObject getMultiObject() {
		return multiObject;
	}

	public Map<String, String> getEntryNameToArlingtonObjectMap() {
		return multiObject.getEntryNameToArlingtonObjectMap();
	}

	public JavaGeneration getJavaGeneration() {
		return multiObject.getJavaGeneration();
	}
	
	public static boolean isField(String objectName) {
		return objectName.startsWith("Field") && !"FieldMDPTransformParameters".equals(objectName);
	}
}
