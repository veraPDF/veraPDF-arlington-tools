package org.verapdf.arlington;

import java.util.HashSet;
import java.util.Set;

public class Constants {
	public static final String PACKAGE = "package";
	public static final String IMPORT = "import";

	//propertiesNames
	public static final String FILE_SIZE = "fileSize";
	public static final String KEY_NAME = "keyName";
	public static final String KEYS_STRING = "keysString";
	public static final String SIZE = "size";
	public static final String NUMBER_OF_PAGES = "numberOfPages";
	public static final String PAGE_CONTAINS_STRUCT_CONTENT_ITEMS = "pageContainsStructContentItems";
	public static final String IMAGE_IS_STRUCT_CONTENT_ITEM = "imageIsStructContentItem";
	public static final String IS_ENCRYPTED_WRAPPER = "isEncryptedWrapper";
	public static final String IS_PDF_TAGGED = "isPDFTagged";
	public static final String NOT_STANDARD_14_FONT = "notStandard14Font";
	public static final String SUB_ARRAYS = "subArrays";
	public static final String HAS_CYCLE = "hasCycle";


	//regex
	public static final String NUMBER_REGEX = "\\d+";
	public static final String DOUBLE_REGEX = "\\d+\\.\\d+";
	public static final String NUMBER_WITH_STAR_REGEX = "\\d+\\*";
	public static final String STAR = "*";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String OBJECT = "Object";
	public static final String NULL = "null";
	public static final String CURRENT_ENTRY = "";

	public static final Set<String> reservedVeraPDFNames = new HashSet<>();

	static {
		reservedVeraPDFNames.add("Properties");
		reservedVeraPDFNames.add("ID");
	}

}
