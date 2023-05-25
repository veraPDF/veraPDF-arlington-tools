package org.verapdf.arlington;

import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;

public class JavaGeneration {

	private static final Logger LOGGER = Logger.getLogger(JavaGeneration.class.getCanonicalName());

	private final PrintWriter javaWriter;

	public JavaGeneration(PrintWriter javaWriter) {
		this.javaWriter = javaWriter;
	}

	public static String getMethodName(String propertyName) {
		return "get" + propertyName;
	}

	public void printMethodSignature(boolean isOverride, String accessModifier,
											boolean isStatic, String returnType, String methodName, String ... arguments) {
		if (isOverride) {
			javaWriter.println("\t@Override");
		}
		javaWriter.print("\t" + accessModifier + " ");
		if (isStatic) {
			javaWriter.print("static ");
		}
		javaWriter.print(returnType + " " + methodName + "(");
		if (arguments.length != 0) {
			for (int i = 0; i < arguments.length - 1; i++) {
				javaWriter.print(arguments[i] + ",");
			}
			javaWriter.print(arguments[arguments.length - 1]);
		}
		javaWriter.println(") {");
	}

	public static String getASAtomFromString(String string) {
		return "ASAtom.getASAtom(\"" + string + "\")";
	}

	public static String split(String stringName, boolean equals, List<String> values) {
		StringBuilder string = new StringBuilder();
		string.append("Arrays.stream(");
		string.append(stringName);
		string.append(".split(\"&\")).filter(elem -> ");
		for (String value : values) {
			string.append(equals ? "" : "!").append("Objects.equals(elem, ").append(value).append(")").append(" ").append(equals ? "||" : "&&").append(" ");
		}
		string.delete(string.length() - 4, string.length());
		string.append(").count()");
		return string.toString();
	}

	public static String constructor(String objectName, String ... arguments) {
		return constructor(objectName, new LinkedList<>(Arrays.asList(arguments)));
	}

	public static String constructor(String objectName, List<String> arguments) {
		StringBuilder result = new StringBuilder();
		result.append("new ").append(objectName).append("(");
		for (String argument : arguments) {
			result.append(argument).append(", ");
		}
		result.delete(result.length() - 2, result.length());
		result.append(")");
		return result.toString();
	}

	public static String constructorGFAObject(String entryName, String arlingtonObjectName, String objectName, String parentObject, String keyName) {
		List<String> arguments = new LinkedList<>();
		arguments.add(objectName);
		arguments.add(parentObject);
		if ("FontFile2".equals(arlingtonObjectName)) {
			arguments.add("this.parentObject");
		} else if (Constants.STAR.equals(entryName)) {
			arguments.add("this.parentObject");
			arguments.add("keyName");
		}
		arguments.add(keyName);
		return constructor(Object.getJavaClassName(arlingtonObjectName), arguments);
	}
}
