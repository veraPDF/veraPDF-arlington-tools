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

	public static final SortedSet<String> objectNames = new TreeSet<>();
	public static final SortedSet<String> extensionNames = new TreeSet<>();
	public static final Map<String, MultiObject> objectIdMap = new HashMap<>();
	private static final Map<PDFVersion, Set<String>> activeObjectNames = new HashMap<>();
	private static final String VALIDATION_RESULT_FOLDER = "result_validation/";

	public static void main(String[] args) throws IOException {
		ModelGeneration.addPackageAndImportsToModel();
		for (PDFVersion version : PDFVersion.values()) {
			createObjectIdMapFromJSON(version);
			ProfileGeneration.startProfile(version, version.getProfileWriter());
			objectNames.addAll(version.getObjectIdMap().keySet());
		}
		ObjectCreation.addMergedWidgetAnnotFields();
		ObjectCreation.addNameAndNumberTreeEntries();
		ObjectCreation.addDocument();
		ObjectCreation.addXRefStreamToFileTrailer();
		ObjectCreation.addStarObjects();
		findParents();
		generate();
		ModelGeneration.close();
		for (PDFVersion version : PDFVersion.values()) {
			ProfileGeneration.endProfile(version.getProfileWriter());
			version.getProfileWriter().close();
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
		JavaGeneration gfaObjectGeneration = new JavaGeneration(new PrintWriter(new FileWriter(Main.VALIDATION_RESULT_FOLDER + "GFAObject.java")));
		gfaObjectGeneration.addGFAObject();
		ModelGeneration.addAObject();
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
		PrintWriter javaWriter = new PrintWriter(new FileWriter(VALIDATION_RESULT_FOLDER + Object.getJavaClassName(objectName) + ".java"));
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

