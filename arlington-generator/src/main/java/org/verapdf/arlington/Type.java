package org.verapdf.arlington;

import org.verapdf.arlington.json.JSONEntry;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum()
public enum Type {
	@XmlEnumValue("array") ARRAY("array", "Array", "COS_ARRAY", "COSArray", true, false,
			null, null,  null),
	@XmlEnumValue("bitmask") BITMASK("bitmask", "Bitmask", "COS_INTEGER", "COSInteger", false, true,
			"Integer", "Long",  "getInteger"),
	@XmlEnumValue("boolean") BOOLEAN("boolean", "Boolean", "COS_BOOLEAN", "COSBoolean", false, true,
			"Boolean", "Boolean",  "getBoolean"),
	@XmlEnumValue("date") DATE("date", "Date", "COS_STRING", null, false, true,
			"String", "String",  "getString"),
	@XmlEnumValue("dictionary") DICTIONARY("dictionary", "Dictionary", "COS_DICT", "COSDictionary", true, false,
			null, null,  null),
	@XmlEnumValue("integer") INTEGER("integer", "Integer", "COS_INTEGER", "COSInteger", false, true,
			"Integer", "Long",  "getInteger"),
	@XmlEnumValue("matrix") MATRIX("matrix", "Matrix", "COS_ARRAY", "COSArray", false, true,
			null, null,  null),
	@XmlEnumValue("name") NAME("name", "Name", "COS_NAME", "COSName", false, true,
			"String", "String",  "getString"),
	@XmlEnumValue("name-tree") NAME_TREE("name-tree", "NameTree", "COS_DICT", "COSDictionary", true, false,
			null, null,  null),
	@XmlEnumValue("null") NULL("null", "Null", "COS_NULL", null, false, true,
			null, null,  null),
	@XmlEnumValue("number") NUMBER("number", "Number", "COS_REAL", "COSReal", false, true,
			"Decimal", "Double",  "getReal"),
	@XmlEnumValue("number-tree") NUMBER_TREE("number-tree", "NumberTree", "COS_DICT", "COSDictionary", true, false,
			null, null,  null),
	@XmlEnumValue("rectangle") RECTANGLE("rectangle", "Rectangle", "COS_ARRAY", "COSArray", false, true,
			null, null,  null),
	@XmlEnumValue("stream") STREAM("stream", "Stream", "COS_STREAM", "COSStream", true, false,
			null, null,  null),
	@XmlEnumValue("string") STRING("string", "String", "COS_STRING", "COSString", false, true,
			"String", "String",  "getString"),
	@XmlEnumValue("string-ascii") STRING_ASCII("string-ascii", "StringAscii", "COS_STRING", "COSString", false, true,
			"String", "String",  "getString"),
	@XmlEnumValue("string-byte") STRING_BYTE("string-byte", "StringByte", "COS_STRING", "COSString", false, true,
			"String", "String",  "getString"),
	@XmlEnumValue("string-text") STRING_TEXT("string-text", "StringText", "COS_STRING", "COSString", false, true,
			"String", "String",  "getString"),
	@XmlEnumValue("entry") ENTRY("entry", "Entry", null, null, true, false,
			null, null,  null),
	@XmlEnumValue("subArray") SUB_ARRAY("subArray", "SubArray", null, null, true, false,
			null, null,  null);

	private final String tsvType;
	private final String type;
	private final String cosObjectType;
	private final String parserClassName;
	private final Boolean isLinkType;
	private final Boolean isPropertyType;
	private final String modelType;
	private final String javaType;
	private final String parserMethod;

	Type(String tsvType, String type, String cosObjectType, String parserClassName, Boolean isLinkType, Boolean isPropertyType,
		 String modelType, String javaType, String parserMethod) {
		this.tsvType = tsvType;
		this.type = type;
		this.cosObjectType = cosObjectType;
		this.parserClassName = parserClassName;
		this.isLinkType = isLinkType;
		this.isPropertyType = isPropertyType;
		this.modelType = modelType;
		this.javaType = javaType;
		this.parserMethod = parserMethod;
	}
	
	public String getType() {
		return type;
	}

	public String getCosObjectType() {
		return cosObjectType != null ? "COSObjType." + cosObjectType : null;
	}

	public String getParserClassName() {
		return parserClassName;
	}

	public Boolean isLinkType() {
		return isLinkType;
	}

	public Boolean isPropertyType() {
		return isPropertyType;
	}

	public String getModelType() {
		return modelType;
	}

	public String getJavaType() {
		return javaType;
	}

	public String getParserMethod() {
		return "." + parserMethod + "()";
	}

	public boolean isActive() {
		return getCosObjectType() != null;
	}

	public String getSeparator() {
		if (Type.STRING.getJavaType().equals(getJavaType())) {
			return "\"";
		}
		return "";
	}

	public String getValueWithSeparator(String value) {
		return getSeparator() + value + getSeparator();
	}

	public String getJavaPostfix() {
		if (this == Type.NUMBER) {
			return "D";
		}
		if (this == Type.INTEGER || this == Type.BITMASK) {
			return "L";
		}
		return "";
	}

	public static Type getType(String string) {
		if (string == null) {
			return null;
		}
		for (Type type : values()) {
			if (string.equals(type.getTsvType())) {
				return type;
			}
		}
		return null;
	}

	public String getTsvType() {
		return tsvType;
	}

	public String getCreationCOSObject(String obj) {
		if (this == Type.STRING || this == Type.STRING_TEXT || this == Type.STRING_ASCII || this == Type.STRING_BYTE) {
			return getParserClassName() + ".construct(" + obj + ".getBytes())";
		} else if (this == Type.NAME || this == Type.BOOLEAN || this == Type.INTEGER || this == Type.NUMBER ||
				this == Type.BITMASK) {
			return getParserClassName() + ".construct(" + obj + ")";
		} else if (this == Type.MATRIX || this == Type.RECTANGLE) {
			String[] values = JSONEntry.getArrayFromString(obj);
			return getParserClassName() + ".construct(" + values.length + ", new double[]{" + String.join(",", values) + "})";
		}
		return obj;
	}
}
