package org.verapdf.arlington;

import java.util.HashSet;
import java.util.Set;

public class Constants {
	public static final String PACKAGE = "package";
	public static final String IMPORT = "import";

	//arlington objects and entries
	public static final String STREAM = "Stream";
	public static final String STRUCTURE_ATTRIBUTE_DICTIONARY = "StructureAttributesDict";
	public static final String FONT_FILE_2 = "FontFile2";
	public static final String FILE_TRAILER = "FileTrailer";
	public static final String DOCUMENT = "Document";
	public static final String OBJECT_REFERENCE = "ObjectReference";
	public static final String OBJECT_STREAM = "ObjectStream";
	public static final String OBJECT_STREAMS = "ObjectStreams";
	public static final String ARRAY_OF_OBJECT_STREAMS = "ArrayOfObjectStreams";
	public static final String NAME_TREE_NODE = "NameTreeNode";
	public static final String NUMBER_TREE_NODE = "NumberTreeNode";
	public static final String NAME_TREE_NODES_ARRAY = NAME_TREE_NODE + "s" + "Array";
	public static final String NUMBER_TREE_NODES_ARRAY = NUMBER_TREE_NODE + "s" + "Array";
	public static final String NAME_TREE_NODE_LIMITS_ARRAY = NAME_TREE_NODE + "Limits" + "Array";
	public static final String NUMBER_TREE_NODE_LIMITS_ARRAY = NUMBER_TREE_NODE + "Limits" + "Array";
	public static final String NAME_TREE_NODE_NAMES_ARRAY = NAME_TREE_NODE + "Names" + "Array";
	public static final String NUMBER_TREE_NODE_NUMS_ARRAY = NUMBER_TREE_NODE + "Nums" + "Array";
	public static String ANNOT_WIDGET = "AnnotWidget";
	public static String ADD_ACTION_WIDGET_ANNOTATION_FORM_FIELD = "AddActionWidgetAnnotationFormField";
	public static final String TREE_NODE = "TreeNode";
	public static final String XREF_STREAM = "XRefStream";
	public static final String XREF_STM = "XRefStm";
	public static final String LINEARIZATION_PARAMETER_DICTIONARY = "LinearizationParameterDict";
	public static final String ARRAY_OF_DECODE_PARAMS_ENTRY = "ArrayOfDecodeParamsEntry";
	public static final String PAGE_OBJECT = "PageObject";
	public static final String PARENT_KEY = "Parent";
	public static final String PARENT = "parent";
	public static final String TRAILER = "trailer";
	public static final String CATALOG = "Catalog";
	public static final String ROOT = "Root";
	public static final String PAGE = "page";

	//column names
	public static final String SINCE_COLUMN = "Since";
	public static final String REQUIRED_COLUMN = "Required";
	public static final String DEFAULT_VALUE_COLUMN = "DefaultValue";
	public static final String POSSIBLE_VALUES_COLUMN = "PossibleValues";
	public static final String EXTENSION_VALUE_COLUMN = "ExtensionValue";
	public static final String REQUIRED_VALUE_COLUMN = "RequiredValue";
	public static final String SPECIAL_CASE_COLUMN = "SpecialCase";
	public static final String INDIRECT_REFERENCE_COLUMN = "IndirectReference";
	public static final String LINKS_COLUMN = "Links";
	public static final String VALUE = "Value";

	//propertiesNames
	public static final String FILE_SIZE = "fileSize";
	public static final String KEY_NAME = "keyName";
	public static final String KEYS_STRING = "keysString";
	public static final String SIZE = "size";
	public static final String NUMBER_OF_PAGES = "numberOfPages";
	public static final String PAGE_CONTAINS_STRUCT_CONTENT_ITEMS = "pageContainsStructContentItems";
	public static final String IMAGE_IS_STRUCT_CONTENT_ITEM = "imageIsStructContentItem";
	public static final String IS_ASSOCIATED_FILE = "isAssociatedFile";
	public static final String IS_ENCRYPTED_WRAPPER = "isEncryptedWrapper";
	public static final String IS_PDF_TAGGED = "isPDFTagged";
	public static final String OBJECT_TYPE = "ObjectType";
	public static final String NOT_STANDARD_14_FONT = "notStandard14Font";
	public static final String SUB_ARRAYS = "subArrays";
	public static final String HAS_CYCLE = "hasCycle";
	public static final String INHERITABLE_VALUE = "InheritableValue";
	public static final String IS_CONTAINS_INHERITABLE_VALUE = "isContainsInheritableValue";


	//regex
	public static final String NUMBER_REGEX = "\\d+";
	public static final String DOUBLE_REGEX = "\\d+\\.\\d+";
	public static final String NUMBER_WITH_STAR_REGEX = "\\d+\\*";
	public static final String SECOND_OR_THIRD_CLASS_NAME_REGEX = "^(XX|(.{4}[_:]))";

	public static final String STAR = "*";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String UNDEFINED = "undefined";
	public static final String OBJECT = "Object";
	public static final String NULL = "null";
	public static final String CURRENT_ENTRY = "";

	//verapdf
	public static final String BASE_MODEL_OBJECT_PATH = "org.verapdf.model.baselayer.Object";

	public static final Set<String> reservedVeraPDFNames = new HashSet<>();

	static {
		reservedVeraPDFNames.add("Properties");
		reservedVeraPDFNames.add("ID");
	}

	public static final Set<String> widgetAnnotFieldsNames = new HashSet<>();

	static {
		widgetAnnotFieldsNames.add("AnnotWidgetField");
		widgetAnnotFieldsNames.add("AnnotWidgetFieldSig");
		widgetAnnotFieldsNames.add("AnnotWidgetFieldTx");
		widgetAnnotFieldsNames.add("AnnotWidgetFieldBtnCheckbox");
		widgetAnnotFieldsNames.add("AnnotWidgetFieldBtnRadio");
		widgetAnnotFieldsNames.add("AnnotWidgetFieldBtnPush");
		widgetAnnotFieldsNames.add("AnnotWidgetFieldChoice");
	}
}
