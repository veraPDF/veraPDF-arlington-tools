package org.verapdf.arlington;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.verapdf.arlington.json.ItemDeserializer;
import org.verapdf.arlington.json.JSONEntry;
import org.verapdf.arlington.json.JSONValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

	private static final SortedSet<String> objectNames = new TreeSet<>();
	private static final Map<String, MultiObject> objectIdMap = new HashMap<>();
	private static final Map<PDFVersion, Set<String>> activeObjectNames = new HashMap<>();

	public static void main(String[] args) throws IOException {
		ModelGeneration.addPackageAndImportsToModel();
		ModelGeneration.addAObject();
		JavaGeneration gfaObjectGeneration = new JavaGeneration(new PrintWriter(new FileWriter(Main.folder + "GFAObject.java")));
		gfaObjectGeneration.addGFAObject();
		for (PDFVersion version : PDFVersion.values()) {
			createObjectIdMapFromJSON(version);
			ProfileGeneration.startProfile(version, version.getProfileWriter());
			objectNames.addAll(version.getObjectIdMap().keySet());
		}
		addMergedWidgetAnnotFields();
		findParents();
		addDocument();
		addXRefStreamToFileTrailer();
		addStarObjects();
		generate();
		ModelGeneration.close();
		for (PDFVersion version : PDFVersion.values()) {
			ProfileGeneration.endProfile(version.getProfileWriter());
			version.getProfileWriter().close();
		}
	}
	
	private static void addMergedWidgetAnnotFields() {
		for (PDFVersion version : PDFVersion.values()) {
			Object widgetAnnot = version.getObjectIdMap().get("AnnotWidget");
			if (widgetAnnot == null) {
				continue;
			}
			Set<Object> newObjects = new HashSet<>();
			for (Object object : version.getObjectIdMap().values()) {
				if (Object.isField(object.getObjectName())) {
					SortedSet<Entry> entries = new TreeSet<>();
					for (Entry entry : object.getEntries()) {
						if (!"Kids".equals(entry.getName())) {
							entries.add(new Entry(entry));
						}
					}
					for (Entry entry : widgetAnnot.getEntries()) {
						if (!"Kids".equals(entry.getName())) {
							entries.add(new Entry(entry));
						}
					}
					String newObjectName = "AnnotWidget" + object.getObjectName();
					newObjects.add(new Object(newObjectName, entries));
					objectNames.add(newObjectName);
				}
			}
			for (Object object : newObjects) {
				version.getObjectIdMap().put(object.getObjectName(), object);
			}
		}
		for (PDFVersion version : PDFVersion.values()) {
			for (Object object : version.getObjectIdMap().values()) {
				for (Entry entry : object.getEntries()) {
					for (Type type : Type.values()) {
						List<String> links = entry.getLinksWithoutPredicatesList(type);
						for (String link : links) {
							if (Object.isField(link)) {
								entry.getLinks().get(type).add("AnnotWidget" + link);
							}
						}						
					}
				}
			}
		}
		for (PDFVersion version : PDFVersion.values()) {
			for (Object object : version.getObjectIdMap().values()) {
				for (Entry entry : object.getEntries()) {
					for (Type type : Type.values()) {
						List<String> links = entry.getLinksWithoutPredicatesList(type);
						for (String link : links) {
							if (link.equals("AnnotWidget")) {
								entry.getLinks().get(type).addAll(Constants.widgetAnnotFieldsNames);
							}
						}
					}
				}
			}
		}
	}

	public static void createObjectIdMapFromJSON(PDFVersion version) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream jsonFileInputStream = new FileInputStream("arlington" + version.getString() + ".json");
		TypeReference<HashMap<String, Map<String, JSONEntry>>> typeRef
				= new TypeReference<HashMap<String, Map<String, JSONEntry>>>() {};
		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(JSONValue.class, new ItemDeserializer());
		objectMapper.registerModule(module);
		Map<String, Map<String, JSONEntry>> jsonMap = objectMapper.readValue(new InputStreamReader(jsonFileInputStream,
				StandardCharsets.UTF_8), typeRef);
		for (Map.Entry<String, Map<String, JSONEntry>> mapEntry : jsonMap.entrySet()) {
			SortedSet<Entry> entries = new TreeSet<>();
			for (Map.Entry<String, JSONEntry> mapMapEntry : mapEntry.getValue().entrySet()) {
				mapMapEntry.getValue().setName(mapMapEntry.getKey());
				entries.add(Entry.getEntryFromJSON(mapMapEntry.getValue()));
			}
			version.getObjectIdMap().put(mapEntry.getKey(), new Object(mapEntry.getKey(), entries));
		}
		jsonFileInputStream.close();
	}

	private static void generate() throws IOException {
		for (String objectName : objectNames) {
			objectIdMap.put(objectName, new MultiObject(objectName));
		}
		for (PDFVersion version : PDFVersion.values()) {
			findActiveObjects(version);
		}
		for (String objectName : objectNames) {
			MultiObject multiObject = objectIdMap.get(objectName);
			Rules.addRules(multiObject);
		}
		for (String objectName : objectNames) {
			generate(objectName);
		}
	}

	private static void findActiveObjects(PDFVersion version) {
		Stack<String> currentObjectNames = new Stack<>();
		currentObjectNames.add(Constants.DOCUMENT);
		Set<String> activeObjectNames = new HashSet<>();
		activeObjectNames.add(Constants.DOCUMENT);
		while (!currentObjectNames.isEmpty()) {
			String objectName = currentObjectNames.pop();
			Object object = version.getObjectIdMap().get(objectName);
			if (object == null) {
				continue;
			}
			for (Entry entry : object.getEntries()) {
				for (Type type : entry.getTypes()) {
					for (String link : entry.getLinksWithoutPredicatesList(type)) {
						if (!activeObjectNames.contains(link) && !currentObjectNames.contains(link)) {
							activeObjectNames.add(link);
							currentObjectNames.add(link);
						}
					}
				}
			}
		}
		Main.activeObjectNames.put(version, activeObjectNames);
	}

	private static void generate(String objectName) throws IOException {
		MultiObject multiObject = objectIdMap.get(objectName);
		PrintWriter javaWriter = new PrintWriter(new FileWriter(folder + Object.getJavaClassName(objectName) + ".java"));
		multiObject.setJavaGeneration(new JavaGeneration(javaWriter));
		multiObject.getJavaGeneration().addPackageAndImportsToClass(objectName);
		multiObject.getJavaGeneration().addClassStart(multiObject);
		multiObject.getJavaGeneration().addSize(multiObject);
		ModelGeneration.addTypeToModel(Object.getModelType(objectName), Object.getModelType(Constants.OBJECT));
		Links.addLinks(multiObject);
		Properties.addProperties(multiObject);
		ModelGeneration.addEndType();
		javaWriter.println("}");
		javaWriter.close();
	}

	public static void addPackage(PrintWriter writer, String packageName) {
		writer.println(Constants.PACKAGE + " " + packageName + ";");
	}

	public static void addImport(PrintWriter writer, String importName) {
		writer.println(Constants.IMPORT + " " + importName + ";");
	}

	private static void addDocument() {
		for (PDFVersion version : PDFVersion.values()) {
			version.getObjectIdMap().put(Constants.DOCUMENT, new Object(Constants.DOCUMENT, new TreeSet<>()));
			objectNames.add(Constants.DOCUMENT);
		}

		addLinearizationDictionary();
		addStreamObjects();
		addXRefStreamToDocument();
		addFileTrailer();
	}
	
	private static void addXRefStreamToFileTrailer() {
		Entry entry = new Entry();
		entry.setName(Constants.XREF_STREAM);
		entry.getTypes().add(Type.STREAM);
		List<String> links = new LinkedList<>();
		links.add(Constants.XREF_STREAM);
		entry.getLinks().put(Type.STREAM, links);
		entry.getTypesPredicates().add("");
		entry.setRequired("");
		for (PDFVersion version : PDFVersion.values()) {
			if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
				Object object = version.getObjectIdMap().get(Constants.FILE_TRAILER);
				object.addEntry(entry);
			}
		}
	}

	private static void addFileTrailer() {
		Entry entry = new Entry();
		entry.setName(Constants.FILE_TRAILER);
		entry.getTypes().add(Type.DICTIONARY);
		List<String> links = new LinkedList<>();
		links.add(Constants.FILE_TRAILER);
		entry.getLinks().put(Type.DICTIONARY, links);
		entry.getTypesPredicates().add("");
		entry.setRequired("");
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
			object.addEntry(entry);
		}
	}

	private static void addXRefStreamToDocument() {
		Entry entry = new Entry();
		entry.setName(Constants.XREF_STREAM);
		entry.getTypes().add(Type.STREAM);
		List<String> links = new LinkedList<>();
		links.add(Constants.XREF_STREAM);
		entry.getLinks().put(Type.STREAM, links);
		entry.getTypesPredicates().add("");
		entry.setRequired("");
		for (PDFVersion version : PDFVersion.values()) {
			if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
				Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
				object.addEntry(entry);
			}
		}
	}

	private static void addLinearizationDictionary() {
		Entry entry = new Entry();
		entry.setName(Constants.LINEARIZATION_PARAMETER_DICTIONARY);
		entry.getTypes().add(Type.DICTIONARY);
		List<String> links = new LinkedList<>();
		links.add(Constants.LINEARIZATION_PARAMETER_DICTIONARY);
		entry.getLinks().put(Type.DICTIONARY, links);
		entry.getTypesPredicates().add("");
		entry.setRequired("");
		for (PDFVersion version : PDFVersion.values()) {
			if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {
				Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
				object.addEntry(entry);
			}
		}
	}

	private static void addStreamObjects() {
		Entry entry = new Entry();
		entry.setName(Constants.OBJECT_STREAMS);
		entry.getTypes().add(Type.ARRAY);
		List<String> links = new LinkedList<>();
		links.add(Constants.ARRAY_OF_OBJECT_STREAMS);
		entry.getLinks().put(Type.ARRAY, links);
		entry.getTypesPredicates().add("");
		entry.setRequired("");
		for (PDFVersion version : PDFVersion.values()) {
			if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
				Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
				object.addEntry(entry);
			}
		}
		objectNames.add(Constants.ARRAY_OF_OBJECT_STREAMS);
		for (PDFVersion version : PDFVersion.values()) {
			if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
				Set<String> possibleParents = new HashSet<>();
				possibleParents.add(Constants.DOCUMENT);
				Entry starEntry = new Entry();
				starEntry.setName(Constants.STAR);
				starEntry.getTypes().add(Type.STREAM);
				List<String> starLinks = new LinkedList<>();
				starLinks.add(Constants.OBJECT_STREAM);
				starEntry.getLinks().put(Type.STREAM, starLinks);
				starEntry.getTypesPredicates().add("");
				starEntry.setRequired("");
				SortedSet<Entry> entries = new TreeSet<>();
				entries.add(starEntry);
				Object object = new Object(Constants.ARRAY_OF_OBJECT_STREAMS, entries, possibleParents);
				version.getObjectIdMap().put(Constants.ARRAY_OF_OBJECT_STREAMS, object);
			}
		}
	}

	private static void addStarObjects() {
		for (PDFVersion version : PDFVersion.values()) {
			Set<String> newObjectsNames = new HashSet<>();
			for (String objectName : objectNames) {
				Object object = version.getObjectIdMap().get(objectName);
				if (object == null) {
					continue;
				}
				for (Entry entry : object.getEntries()) {
					if (entry.getUniqLinkTypes().contains(Type.NAME_TREE)) {
						addTreeObject(object, entry, version, Type.NAME_TREE);
						newObjectsNames.add(object.getId() + Type.NAME_TREE.getType() + entry.getName());
					}
					if (entry.getUniqLinkTypes().contains(Type.NUMBER_TREE)) {
						addTreeObject(object, entry, version, Type.NUMBER_TREE);
						newObjectsNames.add(object.getId() + Type.NUMBER_TREE.getType() + entry.getName());
					}
				}
				if (object.isArray()) {
					addSubArrayObject(object, version, newObjectsNames);
				}
			}
			objectNames.addAll(newObjectsNames);
		}
		for (PDFVersion version : PDFVersion.values()) {
			Set<String> newObjectsNames = new HashSet<>();
			for (String objectName : objectNames) {
				Object object = version.getObjectIdMap().get(objectName);
				if (object == null) {
					continue;
				}
				for (Entry entry : object.getEntries()) {
					if (entry.isStar()) {
						addStarEntryObject(object, entry, version);
						newObjectsNames.add(object.getId() + Type.ENTRY.getType());
					}
				}
			}
			objectNames.addAll(newObjectsNames);
		}
	}

	private static void findParents() {
		for (PDFVersion version : PDFVersion.values()) {
			for (String objectName : objectNames) {
				Object object = version.getObjectIdMap().get(objectName);
				if (object == null) {
					continue;
				}
				for (Entry entry : object.getEntries()) {
					for (Type linkType : entry.getUniqLinkTypes()) {
						for (String link : entry.getLinks(linkType)) {
							Object childObject = version.getObjectIdMap().get(link);
							if (childObject == null) {
								continue;
							}
							childObject.getPossibleParents().add(objectName);
						}
					}
				}
			}
		}
	}

	private static void addTreeObject(Object object, Entry entry, PDFVersion version, Type type) {
		String newObjectName = object.getId() + type.getType() + entry.getName();
		Entry newEntry = new Entry();
		newEntry.setName(Constants.STAR);
		for (String link : entry.getLinks(type)) {
			Object currentObject = version.getObjectIdMap().get(link);
			Type currentType = Type.DICTIONARY;
			if (currentObject.isArray()) {
				currentType = Type.ARRAY;
			} else if (currentObject.isStream()) {
				currentType = Type.STREAM;
			}
			newEntry.getTypes().add(currentType);
			newEntry.getTypesPredicates().add(currentType.getType());
			newEntry.setIndirectReference(currentType, entry.getIndirectReference(type));
			List<String> links = newEntry.getLinks(currentType);
			if (links.isEmpty()) {
				links = new LinkedList<>();
				links.add(link);
				newEntry.getLinks().put(currentType, links);
			} else {
				links.add(link);
			}
		}
		entry.getLinks().clear();
		List<String> links = new LinkedList<>();
		links.add(newObjectName);
		entry.getLinks().put(type, links);
		SortedSet<Entry> entries = new TreeSet<>();
		entries.add(newEntry);
		version.getObjectIdMap().put(newObjectName, new Object(newObjectName, entries,
				object.getPossibleParents()));
	}

	private static void addStarEntryObject(Object object, Entry entry, PDFVersion version) {
		String newObjectName = Object.getObjectEntryName(object.getId());
		Entry newEntry = new Entry(entry);
		newEntry.setName(Constants.CURRENT_ENTRY);
		entry.setRequired(Constants.FALSE);
		List<String> links = new LinkedList<>();
		links.add(newObjectName);
		entry.getLinks().clear();
		entry.getTypes().clear();
		entry.getTypesPredicates().clear();
		entry.getPossibleValues().clear();
		entry.getIndirectReference().clear();
		entry.getLinks().put(Type.ENTRY, links);
		entry.getTypes().add(Type.ENTRY);
		SortedSet<Entry> entries = new TreeSet<>();
		entries.add(newEntry);
		version.getObjectIdMap().put(newObjectName, new Object(newObjectName, entries, object.getPossibleParents()));
	}

	private static void addSubArrayObject(Object object, PDFVersion version, Set<String> newObjectsNames) {
		List<? extends Entry> numberStarEntries = object.getNumberStarEntries();
		if (!numberStarEntries.isEmpty()) {
			String newObjectName = object.getId() + Type.SUB_ARRAY.getType();
			newObjectsNames.add(newObjectName);
			List<Integer> numbers = new LinkedList<>();
			for (Entry entry : numberStarEntries) {
				numbers.add(entry.getNumberWithStar());
			}
			Collections.sort(numbers);
			SortedSet<Entry> entries = new TreeSet<>();
			for (int i = 0; i < numbers.size(); i++) {
				Entry originalEntry = numberStarEntries.get(i);
				Entry newEntry = new Entry();
				newEntry.setName(Integer.toString(i));
				for (Map.Entry<Type, String> entry : originalEntry.getIndirectReference().entrySet()) {
					newEntry.getIndirectReference().put(entry.getKey(), replace(entry.getValue(), numbers.get(0), numbers.size()));
				}
				newEntry.getTypes().addAll(originalEntry.getTypes());
				newEntry.getTypesPredicates().addAll(originalEntry.getTypesPredicates());
				for (Map.Entry<Type, List<String>> entry : originalEntry.getPossibleValues().entrySet()) {
					List<String> possibleValues = new LinkedList<>();
					for (String possibleValue : entry.getValue()) {
						possibleValues.add(replace(possibleValue, numbers.get(0), numbers.size()));
					}
					newEntry.getPossibleValues().put(entry.getKey(), possibleValues);
				}
				for (Map.Entry<Type, List<String>> entry : originalEntry.getLinks().entrySet()) {
					List<String> links = new LinkedList<>();
					for (String link : entry.getValue()) {
						links.add(replace(link, numbers.get(0), numbers.size()));
					}
					newEntry.getLinks().put(entry.getKey(), links);
				}
				for (Map.Entry<Type, String> entry : originalEntry.getIndirectReference().entrySet()) {
					newEntry.getIndirectReference().put(entry.getKey(), replace(entry.getValue(), numbers.get(0), numbers.size()));
				}
				for (Map.Entry<Type, String> entry : originalEntry.getSpecialCases().entrySet()) {
					newEntry.getSpecialCases().put(entry.getKey(), replace(entry.getValue(), numbers.get(0), numbers.size()));
				}
				newEntry.setRequired(replace(originalEntry.getRequired(), numbers.get(0), numbers.size()));
				entries.add(newEntry);
			}
			Entry newEntry = new Entry();
			newEntry.setName(Constants.SUB_ARRAYS);
			List<String> links = new LinkedList<>();
			links.add(newObjectName);
			newEntry.setRequired(Constants.FALSE);
			newEntry.getLinks().put(Type.SUB_ARRAY, links);
			newEntry.getTypes().add(Type.SUB_ARRAY);
			newEntry.getTypesPredicates().add(Type.SUB_ARRAY.getType());
			object.addEntry(newEntry);
			version.getObjectIdMap().put(newObjectName, new Object(newObjectName, entries, object.getPossibleParents()));
		}
	}

	private static String replace(String string, int startNumber, int numbers) {
		String newString = string;
		for (int i = 0; i < numbers; i++) {
			newString = newString.replace((i + startNumber) + Constants.STAR, Integer.toString(i));
		}
		return newString;
	}

	public static String getString(PDFVersion version, Object object, Entry entry, Type type) {
		return "Version " + version.getString() + " object " + object.getId() + " entry " + entry.getName() +
				(type != null ?  (" type " + type.getType()) : "");
	}

	public static String getString(PDFVersion version, Object object, Entry entry) {
		return getString(version, object, entry, null);
	}

	public static Map<PDFVersion, Set<String>> getActiveObjectNames() {
		return activeObjectNames;
	}
}

//add extra context not to all items, only for * entries?

//improve message of 11,16 rule

//extension possible values rule, links other cases rules

