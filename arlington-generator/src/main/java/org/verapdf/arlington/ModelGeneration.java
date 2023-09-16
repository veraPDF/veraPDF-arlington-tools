package org.verapdf.arlington;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ModelGeneration {

	private static PrintWriter modelWriter;

	static {
		try {
			modelWriter = new PrintWriter(new FileWriter(modelFolder + "ALayer.mdl"));
		} catch (IOException ignored) {
		}
	}


	public static void addPackageAndImportsToModel() {
		Main.addPackage(modelWriter, "org.verapdf.model.alayer");
		modelWriter.println();
		Main.addImport(modelWriter, Constants.BASE_MODEL_OBJECT_PATH);
		modelWriter.println();
	}

	public static void addTypeToModel(String typeName, String parentName) {
		modelWriter.println("type " + typeName + " extends " + parentName + " {");
	}

	public static void addEndType() {
		modelWriter.println("}");
		modelWriter.println();
	}

	public static void close() {
		modelWriter.close();
	}

	public static void addProperty(String propertyName, String propertyType) {
		modelWriter.println("\tproperty " + propertyName + " : " + propertyType + ";");
	}

	public static void addLink(String linkName, String linkType, String modifier) {
		modelWriter.println("\tlink " + linkName + " : " + linkType + modifier + ";");
	}

	public static void addLink(String linkName, String linkType) {
		addLink(linkName, linkType, "?");
	}

	public static void addAObject() {
		ModelGeneration.addTypeToModel(Object.getModelType(Constants.OBJECT), Constants.OBJECT);
		ModelGeneration.addProperty(Constants.SIZE, Type.INTEGER.getModelType());
		ModelGeneration.addProperty(Constants.KEY_NAME, Type.STRING.getModelType());
		ModelGeneration.addProperty(Constants.KEYS_STRING, Type.STRING.getModelType());
		ModelGeneration.addProperty(Constants.NUMBER_OF_PAGES, Type.INTEGER.getModelType());
		ModelGeneration.addProperty(Constants.FILE_SIZE, Type.INTEGER.getModelType());
		ModelGeneration.addProperty(Constants.OBJECT_TYPE, Type.STRING.getModelType());
		ModelGeneration.addProperty(Constants.IS_PDF_TAGGED, Type.BOOLEAN.getModelType());
		ModelGeneration.addProperty(Constants.NOT_STANDARD_14_FONT, Type.BOOLEAN.getModelType());
		ModelGeneration.addProperty(Constants.IS_ENCRYPTED_WRAPPER, Type.BOOLEAN.getModelType());
		ModelGeneration.addEndType();
	}

}
