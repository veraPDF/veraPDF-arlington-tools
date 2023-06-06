package org.verapdf.arlington;

import java.io.*;
import java.util.*;

public class Main {

	private static final SortedSet<String> objectNames = new TreeSet<>();
	private static final Map<String, MultiObject> objectIdMap = new HashMap<>();

	private static void generate() throws IOException {
		for (String objectName : objectNames) {
			objectIdMap.put(objectName, new MultiObject(objectName));
		}
		for (String objectName : objectNames) {
			MultiObject multiObject = objectIdMap.get(objectName);
			Rules.addRules(multiObject);
		}
		for (String objectName : objectNames) {
			generate(objectName);
		}
	}

	private static void generate(String objectName) throws IOException {
		MultiObject multiObject = objectIdMap.get(objectName);
		PrintWriter javaWriter = new PrintWriter(new FileWriter(folder + Object.getJavaClassName(objectName) + ".java"));
		multiObject.setJavaGeneration(new JavaGeneration(javaWriter));
		multiObject.getJavaGeneration().addPackageAndImportsToClass();
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

	private static String replace(String string, int startNumber, int numbers) {
		String newString = string;
		for (int i = 0; i < numbers; i++) {
			newString = newString.replace((i + startNumber) + Constants.STAR, Integer.toString(i));
		}
		return newString;
	}

	public static String getString(PDFVersion version, Object object, Entry entry, Type type) {
		return "Version " + version.getString() + " object " + object.getId() + " entry " + entry.getName() + (type != null ?  (" type " + type.getType()) : "");
	}

	public static String getString(PDFVersion version, Object object, Entry entry) {
		return getString(version, object, entry, null);
	}
}
