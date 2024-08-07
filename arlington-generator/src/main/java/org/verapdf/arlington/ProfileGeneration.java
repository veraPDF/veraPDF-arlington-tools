package org.verapdf.arlington;

import java.io.PrintWriter;
import java.util.List;

public class ProfileGeneration {

	public static void writeRule(PDFVersion version, int ruleNumber, String object, String clause, String test, String description,
								  String errorMessage, String ... errorArguments) {
		PrintWriter profileWriter = version.getProfileWriter();
		if (isDeferred(test)) {
			profileWriter.println("\t\t<rule object=\"" + object + "\" deferred=\"true\">");
		} else {
			profileWriter.println("\t\t<rule object=\"" + object + "\">");
		}
		profileWriter.println("\t\t\t<id specification=\"" + version.getSpecification() + "\" clause=\"" + clause + "\" testNumber=\"" + ruleNumber + "\"/>");
		profileWriter.println("\t\t\t<description>" + getXMLString(description) + "</description>");
		profileWriter.println("\t\t\t<test>" + getXMLString(test) + "</test>");
		profileWriter.println("\t\t\t<error>");
		profileWriter.println("\t\t\t\t<message>" + getXMLString(errorMessage) + "</message>");
		boolean containsKeyName = errorMessage.contains("%keyName%");
		if (errorArguments.length == 0 && !containsKeyName) {
			profileWriter.println("\t\t\t\t<arguments/>");
		} else {
			profileWriter.println("\t\t\t\t<arguments>");
			for (String errorArgument : errorArguments) {
				profileWriter.println("\t\t\t\t\t<argument>" + getXMLString(errorArgument) + "</argument>");
			}
			if (containsKeyName) {
				profileWriter.println("\t\t\t\t\t<argument name=\"" + Constants.KEY_NAME + "\">" + Constants.KEY_NAME + "</argument>");
			}
			profileWriter.println("\t\t\t\t</arguments>");
		}
		profileWriter.println("\t\t\t</error>");
		profileWriter.println("\t\t\t<references/>");
		profileWriter.println("\t\t</rule>");
	}

	private static boolean isDeferred(String test) {
		return test.contains(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM) || test.contains(Constants.IS_ASSOCIATED_FILE);
	}

	public static void startProfile(PDFVersion version, PrintWriter profileWriter) {
		String profileVersion = "ARLINGTON" + version.getVersion() + "_"  + version.getSubversion();
		profileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		profileWriter.println("<profile xmlns=\"http://www.verapdf.org/ValidationProfile\" flavour=\"" + profileVersion + "\">");
		profileWriter.println("\t<details creator=\"veraPDF Consortium\" created=\"2022-05-23T21:45:28.872+03:00\">");
		profileWriter.println("\t\t<name>Arlington PDF " + version.getString() + " profile</name>");
		profileWriter.println("\t\t<description>Rules against PDF " + version.getString() + " Specification</description>");
		profileWriter.println("\t</details>");
		profileWriter.println("\t<hash></hash>");
		profileWriter.println("\t<rules>");
	}

	public static String getErrorMessageStart(boolean isDescription, Object object, Entry entry, Type type) {
		return getErrorMessagePart(isDescription, object, entry, type, true);
	}

	public static String getErrorMessageStart(boolean isDescription, Object object, Entry entry) {
		return getErrorMessageStart(isDescription, object, entry, null);
	}

	public static String getErrorMessagePart(boolean isDescription, Object object, Entry entry, Type type) {
		return getErrorMessagePart(isDescription, object, entry, type, false);
	}

	public static String getErrorMessagePart(boolean isDescription, Object object, Entry entry) {
		return getErrorMessagePart(isDescription, object, entry, null);
	}

	public static String getErrorMessagePart(boolean isDescription, Object object, Entry entry, Type type, boolean isStart) {
		StringBuilder stringBuilder = new StringBuilder();
		if (Object.getObjectEntryName(Constants.ARRAY_OF_OBJECT_STREAMS).equals(object.getId())) {
			stringBuilder.append(Constants.OBJECT_STREAM);
		} else if ((Constants.FILE_TRAILER.equals(object.getId()) || Constants.DOCUMENT.equals(object.getId())) && 
				Constants.XREF_STREAM.equals(entry.getName())) {
			stringBuilder.append(Constants.XREF_STREAM);
		} else if (Constants.DOCUMENT.equals(object.getId()) && Constants.FILE_TRAILER.equals(entry.getName())) {
			stringBuilder.append(Constants.FILE_TRAILER);			
		} else {
			if (isStart) {
				stringBuilder.append(Type.ENTRY.getType());
			} else {
				stringBuilder.append("entry");
			}
			if (!Constants.CURRENT_ENTRY.equals(entry.getName())) {
				if (!Constants.STAR.equals(entry.getName()) || (!object.isNameTree() && !object.isNumberTree())) {
					stringBuilder.append(" ").append(entry.getName());
				}
			} else if (!isDescription) {
				stringBuilder.append(" ").append("%keyName%");
			}
			if (type != null) {
				stringBuilder.append(" with type ");
				stringBuilder.append(type.getType());
			}
			stringBuilder.append(" in ");
			if (Constants.CURRENT_ENTRY.equals(entry.getName())) {
				stringBuilder.append(object.getId().substring(0, object.getId().length() - 5));//remove ...Entry
			} else {
				stringBuilder.append(object.getId());
			}
		}
		return stringBuilder.toString();
	}

	public static void endProfile(PrintWriter profileWriter) {
		profileWriter.println("\t</rules>");
		profileWriter.println("\t<variables/>");
		profileWriter.println("</profile>");
	}

	public static String getXMLString(String string) {
		return string.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}

	public static String split(String stringName, boolean equals, List<String> values) {
		StringBuilder string = new StringBuilder();
		string.append(stringName);
		string.append(".split('&').filter(elem => ");
		for (String value : values) {
			string.append("elem ").append(equals ? "==" : "!=").append(" ").append(value).append(" ").append(equals ? "||" : "&&").append(" ");
		}
		string.delete(string.length() - 4, string.length());
		string.append(").length");
		return string.toString();
	}
}
