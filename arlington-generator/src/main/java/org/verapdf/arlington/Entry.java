package org.verapdf.arlington;

import javafx.util.Pair;
import org.verapdf.arlington.json.JSONEntry;

import java.util.*;
import java.util.stream.Collectors;

public class Entry implements Comparable<Entry> {
	private String name;
	private MultiEntry multiEntry;
	private List<Type> types;
	private List<String> typesPredicates;
	private String sinceString;
	private PDFVersion deprecatedVersion;
	private String requiredString;
	private Map<Type, String> indirectReference;
	private boolean inheritable;
	private Pair<Type, String> defaultValue;
	private Map<Type, List<String>> possibleValues;
	private Map<Type, String> specialCases;
	private Map<Type, List<String>> links;

	public Entry() {
		this.types = new LinkedList<>();
		this.typesPredicates = new LinkedList<>();
		this.indirectReference = new HashMap<>();
		this.possibleValues = new HashMap<>();
		this.specialCases = new HashMap<>();
		this.links = new HashMap<>();
	}

	public Entry(Entry entry) {
		this.name = entry.getName();
		this.types = new LinkedList<>(entry.types);
		this.typesPredicates = new LinkedList<>(entry.typesPredicates);
		this.deprecatedVersion = entry.deprecatedVersion;
		this.sinceString = entry.sinceString;
		this.requiredString = entry.requiredString;
		this.indirectReference = new HashMap<>(entry.indirectReference);
		this.inheritable = entry.inheritable;
		this.defaultValue = entry.defaultValue;
		this.possibleValues = new HashMap<>();
		for (Map.Entry<Type, List<String>> e : entry.possibleValues.entrySet()) {
			possibleValues.put(e.getKey(), new LinkedList<>(e.getValue()));
		}
		this.specialCases = new HashMap<>(entry.specialCases);
		this.links = new HashMap<>();
		for (Map.Entry<Type, List<String>> e : entry.links.entrySet()) {
			links.put(e.getKey(), new LinkedList<>(e.getValue()));
		}
	}

	public static Entry getEntryFromJSON(JSONEntry jsonEntry) {
		Entry entry = new Entry();
		entry.name = jsonEntry.getName();
		entry.types = jsonEntry.getTypes();
		entry.typesPredicates = jsonEntry.getTypesPredicates();
		entry.sinceString = jsonEntry.getSinceString();
		entry.deprecatedVersion = jsonEntry.getDeprecatedVersion();
		entry.requiredString = jsonEntry.getRequired();
		entry.inheritable = jsonEntry.getInheritable();
		entry.defaultValue = jsonEntry.getDefaultValue();
		entry.indirectReference = new HashMap<>();
		entry.possibleValues = new HashMap<>();
		entry.specialCases = new HashMap<>();
		entry.links = new HashMap<>();
		for (int i = 0; i < entry.types.size(); i++) {
			String indirectReference = jsonEntry.getIndirectReference(i);
			if (indirectReference != null) {
				entry.indirectReference.put(entry.types.get(i), indirectReference);
			}
			List<String> possibleValues = jsonEntry.getPossibleValues(i);
			if (possibleValues != null) {
				entry.possibleValues.put(entry.types.get(i), possibleValues);
			}
			String specialCase = jsonEntry.getSpecialCase(i);
			if (specialCase != null) {
				entry.specialCases.put(entry.types.get(i), specialCase);
			}
			List<String> links = jsonEntry.getLinks(i);
			if (links != null) {
				entry.links.put(entry.types.get(i), links);
			}
		}
		return entry;
	}

	public String getName() {
		return name;
	}

	public String getRequired() {
		return requiredString;
	}

	public void setRequired(String requiredString) {
		this.requiredString = requiredString;
	}

	public Boolean isRequired() {
		return Constants.TRUE.equals(getRequired());
	}

	public String getIndirectReference(Type type) {
		return indirectReference.get(type);
	}

	public void setIndirectReference(Type type, String indirect) {
		indirectReference.put(type, indirect);
	}

	public Boolean isIndirectReference(Type type) {
		return Constants.TRUE.equals(getIndirectReference(type)) ||
				(PredicatesParser.MUST_BE_INDIRECT_PREDICATE + "()").equals(getIndirectReference(type));
	}

	public Boolean isDirectReference(Type type) {
		return (PredicatesParser.MUST_BE_DIRECT_PREDICATE + "()").equals(getIndirectReference(type));
	}

	public PDFVersion getDeprecatedVersion() {
		return deprecatedVersion;
	}

	public Integer getNumberWithStar() {
		try {
			return Integer.parseInt(getName().substring(0, getName().length() - 1));
		} catch (NumberFormatException ignored) {
		}
		return null;
	}

	public Integer getNumber() {
		try {
			return Integer.parseInt(getName());
		} catch (NumberFormatException ignored) {
		}
		return null;
	}

	public Boolean isNumber() {
		return isNumber(getName());
	}

	public static Boolean isNumber(String entryName) {
		return entryName.matches(Constants.NUMBER_REGEX);
	}

	public Boolean isStar() {
		return Constants.STAR.equals(getName());
	}

	public Boolean isNumberWithStar() {
		return getName().matches(Constants.NUMBER_WITH_STAR_REGEX);
	}

	public List<String> getPossibleValues(Type type) {
		List<String> result = possibleValues.get(type);
		return result != null ? result : Collections.emptyList();
	}

	public Map<Type, List<String>> getPossibleValues() {
		return possibleValues;
	}

	public List<String> getLinks(Type type) {
		List<String> result = links.get(type);
		return result != null ? result : Collections.emptyList();
	}

	public Set<String> getLinksWithoutPredicatesSet(Type type) {
		return getLinks(type).stream().map(link -> link.contains(PredicatesParser.PREDICATE_PREFIX) ?
				PredicatesParser.getPredicateLastArgument(link) : link).collect(Collectors.toSet());
	}

	public List<String> getLinksWithoutPredicatesList(Type type) {
		return getLinksWithoutPredicatesList(getLinks(type));
	}

	public List<String> getLinksWithoutPredicatesList(List<String> links) {
		return links.stream().map(link -> link.contains(PredicatesParser.PREDICATE_PREFIX) ?
				PredicatesParser.getPredicateLastArgument(link) : link).collect(Collectors.toList());
	}

	public Map<Type,List<String>> getLinks() {
		return links;
	}

	public List<Type> getTypes() {
		return types;
	}

	public List<String> getTypesPredicates() {
		return typesPredicates;
	}

	public SortedSet<Type> getUniqActiveTypes() {
		return getTypes().stream().filter(Type::isActive).collect(Collectors.toCollection(TreeSet::new));
	}

	public SortedSet<Type> getUniqLinkTypes() {
		return getTypes().stream().filter(Type::isLinkType).collect(Collectors.toCollection(TreeSet::new));
	}

	public Set<Type> getUniqPropertyTypes() {
		return getTypes().stream().filter(Type::isPropertyType).collect(Collectors.toSet());
	}

	public String getSpecialCase(Type type) {
		return specialCases.get(type);
	}

	public Map<Type, String> getSpecialCases() {
		return specialCases;
	}

	private static boolean isCorrectEntryName(String entryName) {
		if (Constants.reservedVeraPDFNames.contains(entryName)) {
			return false;
		}
		if (Constants.CURRENT_ENTRY.equals(entryName)) {
			return true;
		}
		if (entryName.substring(0, 1).matches(Constants.NUMBER_REGEX)) {
			return false;
		}
		return true;
	}

	public String getCorrectEntryName() {
		return getCorrectEntryName(getName());
	}

	public static String getCorrectEntryName(String entryName) {
		String correctEntryName = entryName.replaceAll(":","").replaceAll("\\.","").replaceAll("@","");
		if (isCorrectEntryName(correctEntryName)) {
			return correctEntryName;
		}
		return "entry" + correctEntryName;
	}

	public String getContainsPropertyName() {
		return getContainsPropertyName(getName());
	}

	public static String getContainsPropertyName(String entryName) {
		return getCorrectEntryName("contains" + entryName).replace(Constants.STAR, "Any");
	}

	public String getHasTypePropertyName(Type type) {
		return getHasTypePropertyName(getName(), type);
	}

	public static String getHasTypePropertyName(String entryName, Type type) {
		return getCorrectEntryName(entryName) + "HasType" + type.getType();
	}

	public static String getEntryTypePropertyName(String entryName) {
		return getCorrectEntryName(entryName) + "Type";
	}

	public String getTypeValuePropertyName(Type type) {
		return getTypeValuePropertyName(getName(), type);
	}

	public String getArrayLengthPropertyName() {
		return getArrayLengthPropertyName(getName());
	}

	public static String getArrayLengthPropertyName(String entryName) {
		return getCorrectEntryName(entryName) + "ArraySize";
	}

	public String getRectWidthPropertyName() {
		return getRectWidthPropertyName(getName());
	}

	public static String getRectWidthPropertyName(String entryName) {
		return getCorrectEntryName(entryName) + "RectWidth";
	}

	public String getRectHeightPropertyName() {
		return getRectHeightPropertyName(getName());
	}

	public static String getRectHeightPropertyName(String entryName) {
		return getCorrectEntryName(entryName) + "RectHeight";
	}

	public String getStringLengthPropertyName() {
		return getCorrectEntryName(getName()) + "StringSize";
	}

	public String getStreamLengthPropertyName() {
		return getCorrectEntryName(getName()) + "StreamLength";
	}

	public String getIndirectPropertyName() {
		return getIndirectPropertyName(getName());
	}

	public static String getIndirectPropertyName(String entryName) {
		return "is" + getCorrectEntryName(entryName) + "Indirect";
	}

	public String getHasCyclePropertyName() {
		return getCorrectEntryName(getName()) + "hasCycle";
	}

	public String getArraySortAscendingPropertyName(int number) {
		return getArraySortAscendingPropertyName(getName(), String.valueOf(number));
	}

	public static String getArraySortAscendingPropertyName(String entryName, String number) {
		return "is" + getCorrectEntryName(entryName) + "ArraySortAscending" + number;
	}

	public static String getTypeValuePropertyName(String entryName, Type type) {
		return getCorrectEntryName(entryName) + type.getType() + "Value";
	}

	public static String getValuePropertyName(String entryName) {
		return getCorrectEntryName(entryName).replace("*", "Any") + "Value";
	}

	public static String getTypeDefaultValuePropertyName(String entryName, Type type) {
		return getCorrectEntryName(entryName) + type.getType() + "DefaultValue";
	}

	public static String getDefaultValuePropertyName(String entryName) {
		return getCorrectEntryName(entryName) + "DefaultValue";
	}

	public String getIsHexStringPropertyName() {
		return getIsHexStringPropertyName(getName());
	}

	public String getEntriesStringPropertyName() {
		return getEntriesStringPropertyName(getName());
	}

	public static String getIsFieldNamePropertyName(String entryName) {
		return getCorrectEntryName(entryName) + "IsFieldName";
	}

	public static String getEntriesStringPropertyName(String name) {
		return getCorrectEntryName(name) + "EntriesString";
	}

	public String getEntryIsIndexInNameTreePropertyName(String entryName) {
		return getCorrectEntryName(getName() + "EntryIsIndexInNameTree" + entryName);
	}

	public String getEntryIsValueInNameTreePropertyName(String entryName) {
		return getCorrectEntryName(getName() + "EntryIsValueInNameTree" + entryName);
	}


	public static String getIsHexStringPropertyName(String entryName) {
		return getCorrectEntryName("is" + entryName) + "HexString";
	}

	public static String getKeysStringPropertyName(String entryName) {
		return getCorrectEntryName("keysString" + entryName);
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Type, String> getIndirectReference() {
		return indirectReference;
	}

	public boolean isIgnored(Type type) {
		String specialCase = getSpecialCase(type);
		return specialCase != null && specialCase.contains("fn:Ignore()");
	}

	public MultiEntry getMultiEntry() {
		return multiEntry;
	}

	public void setMultiEntry(MultiEntry multiEntry) {
		this.multiEntry = multiEntry;
	}

	public void setContainsProperty(Boolean containsProperty) {
		multiEntry.setContainsProperty(containsProperty);
	}

	public void addTypeValueProperty(Type type) {
		multiEntry.addTypeValueProperty(type);
	}

	public void addHasTypeProperty(Type type) {
		multiEntry.addHasTypeProperty(type);
	}

	public void setHexStringProperty(Boolean hexStringProperty) {
		multiEntry.setHexStringProperty(hexStringProperty);
	}

	public void setArraySizeProperty(Boolean arraySizeProperty) {
		multiEntry.setArraySizeProperty(arraySizeProperty);
	}

	public void setStringSizeProperty(Boolean stringSizeProperty) {
		multiEntry.setStringSizeProperty(stringSizeProperty);
	}

	public void setHasCycleProperty(Boolean hasCycleProperty) {
		multiEntry.setHasCycleProperty(hasCycleProperty);
	}

	public void setStreamSizeProperty(Boolean streamSizeProperty) {
		multiEntry.setStreamSizeProperty(streamSizeProperty);
	}

	public void setRectWidthProperty(Boolean rectWidthProperty) {
		multiEntry.setRectWidthProperty(rectWidthProperty);
	}

	public void setFieldNameProperty(Boolean fieldNameProperty) {
		multiEntry.setFieldNameProperty(fieldNameProperty);
	}

	public void setRectHeightProperty(Boolean rectHeightProperty) {
		multiEntry.setRectHeightProperty(rectHeightProperty);
	}

	public void setIndirectProperty(Boolean indirectProperty) {
		multiEntry.setIndirectProperty(indirectProperty);
	}

	public Set<Integer> getArraySortAscendingProperties() {
		return multiEntry.getArraySortAscendingProperties();
	}

	public void setEntriesStringProperty(Boolean entriesStringProperty) {
		multiEntry.setEntriesStringProperty(entriesStringProperty);
	}

	public boolean mustBeDirect(Type type) {
		return getIndirectReference(type).contains(PredicatesParser.MUST_BE_DIRECT_PREDICATE);
	}

	public boolean mustBeIndirect(Type type) {
		return getIndirectReference(type).contains(PredicatesParser.MUST_BE_INDIRECT_PREDICATE);
	}

	public Set<String> getInNameTreeProperties() {
		return multiEntry.getInNameTreeProperties();
	}

	public Set<Type> getTypeValueProperties() {
		return multiEntry.getTypeValueProperties();
	}

	public Pair<Type, String> getDefaultValue() {
		return defaultValue;
	}

	public Boolean getInheritable() {
		return inheritable;
	}

	public String getSinceString() {
		return sinceString;
	}

	public static Boolean isInheritable(String objectName, String entryName) {
		Boolean isInheritable = null;
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(objectName);
			if (object == null) {
				continue;
			}
			Entry entry = object.getEntry(entryName);
			if (entry == null) {
				continue;
			}
			if (entry.getInheritable() != null) {
				isInheritable = entry.getInheritable();
			}
		}
		return isInheritable != null ? isInheritable : false;
	}

	@Override
	public int compareTo(Entry entry) {
		return name.compareTo(entry.getName());
	}
}
