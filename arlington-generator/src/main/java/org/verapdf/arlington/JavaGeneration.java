package org.verapdf.arlington;

import javafx.util.Pair;
import org.verapdf.arlington.linkHelpers.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JavaGeneration {

	private static final Logger LOGGER = Logger.getLogger(JavaGeneration.class.getCanonicalName());

	private final PrintWriter javaWriter;

	public JavaGeneration(PrintWriter javaWriter) {
		this.javaWriter = javaWriter;
	}

	public void addGFAObject() {
		addPackageAndImportsToClass();
		javaWriter.println("public class GFAObject extends GenericModelObject implements AObject {");
		javaWriter.println();
		javaWriter.println("\tprivate final static List<String> standardFonts = new LinkedList<>();");
		javaWriter.println("\tprivate static final ThreadLocal<Set<COSKey>> keysSet = new ThreadLocal<>();");
		javaWriter.println("\tprotected static final String PDF_DATE_FORMAT_REGEX = \"(D:)?(\\\\d\\\\d){2,7}(([Z+-]\\\\d\\\\d'(\\\\d\\\\d'?)?)?|Z)\";");

		javaWriter.println("\tprotected final COSBase baseObject;");
		javaWriter.println("\tprotected COSBase parentObject;");
		javaWriter.println("\tprotected String keyName;");
		javaWriter.println();

		javaWriter.println("\tpublic GFAObject(COSBase baseObject, COSBase parentObject, String objectType) {");
		javaWriter.println("\t\tsuper(objectType);");
		javaWriter.println("\t\tthis.baseObject = baseObject;");
		javaWriter.println("\t\tthis.parentObject = parentObject;");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\tpublic GFAObject(COSBase baseObject, COSBase parentObject, String keyName, String objectType) {");
		javaWriter.println("\t\tthis(baseObject, parentObject, objectType);");
		javaWriter.println("\t\tthis.keyName = keyName;");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\t@Override");
		javaWriter.println("\tpublic String getID() {");
		javaWriter.println("\t\tCOSKey key = baseObject.getObjectKey();");
		javaWriter.println("\t\treturn key != null ? (getObjectType() + \" \" + key.toString()) : null;");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\t@Override");
		javaWriter.println("\tpublic String getExtraContext() {");
		javaWriter.println("\t\treturn keyName == null || keyName.isEmpty() ? null : keyName;");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getMethodName(Constants.SIZE));
		javaWriter.println("\t\treturn (long)baseObject.size();");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getMethodName(Constants.KEY_NAME));
		javaWriter.println("\t\treturn this.keyName;");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getMethodName(Constants.KEYS_STRING));
		javaWriter.println("\t\treturn this.baseObject.getKeySet().stream()");
		javaWriter.println("\t\t\t\t.map(ASAtom::getValue)");
		javaWriter.println("\t\t\t\t.collect(Collectors.joining(\"&\"));");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getMethodName(Constants.NUMBER_OF_PAGES));
		javaWriter.println("\t\treturn (long) StaticResources.getDocument().getPages().size();");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getMethodName(Constants.FILE_SIZE));
		javaWriter.println("\t\treturn StaticResources.getDocument().getDocument().getFileSize();");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Constants.IS_PDF_TAGGED));
		javaWriter.println("\t\tPDDocument document = StaticResources.getDocument();");
		javaWriter.println("\t\tPDCatalog catalog = document.getCatalog();");
		javaWriter.println("\t\tif (catalog == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject markInfoObject = catalog.getKey(ASAtom.MARK_INFO);");
		javaWriter.println("\t\tif (markInfoObject == null || markInfoObject.empty()) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSBase markInfo = markInfoObject.getDirectBase();");
		javaWriter.println("\t\tif (markInfo.getType() == " + Type.DICTIONARY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn Objects.equals(markInfo.getBooleanKey(ASAtom.MARKED), true);");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn false;");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Constants.NOT_STANDARD_14_FONT));
		javaWriter.println("\t\tCOSObject type = baseObject.getKey(ASAtom.TYPE);");
		javaWriter.println("\t\tif (type == null || type.getType() != " + Type.NAME.getCosObjectType() + " || type.getName() != ASAtom.FONT) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject subtype = baseObject.getKey(ASAtom.SUBTYPE);");
		javaWriter.println("\t\tif (subtype == null || subtype.getType() != " + Type.NAME.getCosObjectType() + " || subtype.getName() != ASAtom.TYPE1) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject baseFont = baseObject.getKey(ASAtom.BASE_FONT);");
		javaWriter.println("\t\tif (baseFont == null || baseFont.getType() != " + Type.NAME.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn true;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn !standardFonts.contains(baseFont.getString());");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Constants.IS_ENCRYPTED_WRAPPER));
		javaWriter.println("\t\tPDDocument document = StaticResources.getDocument();");
		javaWriter.println("\t\tif (document == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tPDCatalog catalog = document.getCatalog();");
		javaWriter.println("\t\tif (catalog == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tPDNamesDictionary names = catalog.getNamesDictionary();");
		javaWriter.println("\t\tif (names == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tPDNameTreeNode embeddedFiles = names.getEmbeddedFiles();");
		javaWriter.println("\t\tif (embeddedFiles == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tfor (COSObject embeddedFile : embeddedFiles) {");
		javaWriter.println("\t\t\tCOSObject relationship = embeddedFile.getKey(ASAtom.AF_RELATIONSHIP);");
		javaWriter.println("\t\t\tif (relationship != null && relationship.getType() == COSObjType.COS_NAME &&");
		javaWriter.println("\t\t\t\t\trelationship.getName() == " + getASAtomFromString("EncryptedPayload") + ") {");
		javaWriter.println("\t\t\t\treturn true;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn false;");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(false, "public", true, Type.BOOLEAN.getJavaType(),
				Constants.HAS_CYCLE, "COSObject object", "ASAtom entryName");
		javaWriter.println("\t\tif (object == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tSet<COSKey> visitedKeys = new HashSet<>();");
		javaWriter.println("\t\twhile (object.knownKey(entryName)) {");
		javaWriter.println("\t\t\tif (object.getKey() != null) {");
		javaWriter.println("\t\t\t\tif (visitedKeys.contains(object.getKey())) {");
		javaWriter.println("\t\t\t\t\treturn true;");
		javaWriter.println("\t\t\t\t}");
		javaWriter.println("\t\t\t\tvisitedKeys.add(object.getKey());");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tobject = object.getKey(entryName);");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn false;");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\tpublic static Set<COSKey> getKeysSet() {");
		javaWriter.println("\t\tif (keysSet.get() == null) {");
		javaWriter.println("\t\t\tkeysSet.set(new HashSet<>());");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn keysSet.get();");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\tpublic static void setKeysSet(Set<COSKey> keysSet) {");
		javaWriter.println("\t\tGFAObject.keysSet.set(keysSet);");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\tpublic static void clearAllContainers() {");
		javaWriter.println("\t\tkeysSet.set(new HashSet<>());");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\tstatic {");
		javaWriter.println("\t\tstandardFonts.add(\"Times-Roman\");");
		javaWriter.println("\t\tstandardFonts.add(\"Helvetica\");");
		javaWriter.println("\t\tstandardFonts.add(\"Courier\");");
		javaWriter.println("\t\tstandardFonts.add(\"Symbol\");");
		javaWriter.println("\t\tstandardFonts.add(\"Times-Bold\");");
		javaWriter.println("\t\tstandardFonts.add(\"Helvetica-Bold\");");
		javaWriter.println("\t\tstandardFonts.add(\"Courier-Bold\");");
		javaWriter.println("\t\tstandardFonts.add(\"ZapfDingbats\");");
		javaWriter.println("\t\tstandardFonts.add(\"Times-Italic\");");
		javaWriter.println("\t\tstandardFonts.add(\"Helvetica-Oblique\");");
		javaWriter.println("\t\tstandardFonts.add(\"Courier-Oblique\");");
		javaWriter.println("\t\tstandardFonts.add(\"Times-BoldItalic\");");
		javaWriter.println("\t\tstandardFonts.add(\"Helvetica-BoldOblique\");");
		javaWriter.println("\t\tstandardFonts.add(\"Courier-BoldOblique\");");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("}");
		javaWriter.println();
		javaWriter.close();
	}

	public void addPackageAndImportsToClass() {
		Main.addPackage(javaWriter, "org.verapdf.gf.model.impl.arlington");
		javaWriter.println();
		Main.addImport(javaWriter, "org.verapdf.cos.*");
		Main.addImport(javaWriter, "org.verapdf.model.GenericModelObject");
		Main.addImport(javaWriter, "org.verapdf.model.alayer.*");
		Main.addImport(javaWriter, "org.verapdf.gf.model.impl.containers.StaticContainers");
		Main.addImport(javaWriter, "org.verapdf.tools.StaticResources");
		Main.addImport(javaWriter, "java.util.*");
		Main.addImport(javaWriter, "org.verapdf.pd.*");
		Main.addImport(javaWriter, "org.verapdf.as.ASAtom");
		Main.addImport(javaWriter, "java.util.stream.Collectors");
		Main.addImport(javaWriter, "org.verapdf.pd.structure.PDNumberTreeNode");
		Main.addImport(javaWriter, "org.verapdf.model.tools.constants.Operators");
		Main.addImport(javaWriter, "org.verapdf.operator.Operator");
		Main.addImport(javaWriter, "org.verapdf.as.io.ASInputStream");
		Main.addImport(javaWriter, "org.verapdf.parser.PDFStreamParser");
		Main.addImport(javaWriter, "org.verapdf.pd.structure.NameTreeIterator");
		Main.addImport(javaWriter, "java.io.IOException");
		javaWriter.println();
	}

	public void addGetterSize(Map<String, LinkHelper> map, Object object, Entry entry, Type type, PDFVersion version) {
		String linkName = Links.getLinkName(entry.getName());
		javaWriter.println("\tprivate org.verapdf.model.baselayer.Object get" + linkName + type.getType() + version.getStringWithUnderScore() + "(COSBase base, String keyName) {");
		javaWriter.println("\t\tswitch (base.size()) {");
		for (String link : entry.getLinks(type)) {
			if (link.contains(PredicatesParser.PREDICATE_PREFIX)) {
				System.out.println("!!! fn");
			}
			SizeLinkHelper helper = (SizeLinkHelper)map.get(link);
			if (helper != null) {
				javaWriter.println("\t\t\tcase " + helper.getSize() + ":");
				javaWriter.println("\t\t\t\treturn " + constructorGFAObject(entry.getName(), link, "base", "this.baseObject", "keyName") + ";");
			}
		}
		javaWriter.println("\t\t\tdefault:");
		javaWriter.println("\t\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addGetInheritable(String objectId, String entryName) {
		if (isInheritable(objectId, entryName) && !Constants.STRUCTURE_ATTRIBUTE_DICTIONARY.equals(objectId)) {
			javaWriter.println("\t\tCOSObject currentObject = this.baseObject.getKey(" + getASAtomFromString("Parent") + ");");
			javaWriter.println("\t\twhile ((object == null || object.empty()) && (currentObject != null && !currentObject.empty())) {");
			javaWriter.println("\t\t\tobject = currentObject.getKey(" + getASAtomFromString(entryName) + ");");
			javaWriter.println("\t\t\tcurrentObject = currentObject.getKey(" + getASAtomFromString("Parent") + ");");
			javaWriter.println("\t\t}");
		}
	}

	public void addArraySortAscendingMethod(Entry entry, int number) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(entry.getArraySortAscendingPropertyName(number)));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.ARRAY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tLong lastNumber = null;");
		javaWriter.println("\t\tfor (int i = 0; i < object.size(); i += " + number + ") {");
		javaWriter.println("\t\t\tCOSObject elem = object.at(i);");
		javaWriter.println("\t\t\tif (elem == null || elem.getType() != " + Type.INTEGER.getCosObjectType() + ") {");
		javaWriter.println("\t\t\t\treturn false;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tif (lastNumber != null && lastNumber > elem.getInteger()) {");
		javaWriter.println("\t\t\t\treturn false;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tlastNumber = elem.getInteger();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn true;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addIndirectMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(entry.getIndirectPropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn object != null && object.get() != null && object.get().isIndirect();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addNameTreeContainsStringMethod(Entry entry, String nameTreeEntryName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(entry.getNameTreeContainsStringPropertyName(nameTreeEntryName)));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.STRING.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		String nameTreeFinalEntry = getComplexObject(nameTreeEntryName);
		javaWriter.println("\t\tif (" + nameTreeFinalEntry + " == null || " + nameTreeFinalEntry + ".getType() != " + Type.DICTIONARY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tPDNameTreeNode nameTreeNode = PDNameTreeNode.create(" + nameTreeFinalEntry + ");");
		javaWriter.println("\t\treturn nameTreeNode.getObject(object.getString()) != null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addgetLinkedObjectsMethod(SortedMap<String, String> entries) {
		printMethodSignature(true, "public", false,
				"List<? extends org.verapdf.model.baselayer.Object>", "getLinkedObjects", "String link");
		javaWriter.println("\t\tswitch (link) {");
		for (String entry : entries.keySet()) {
			String linkName = Links.getLinkName(entry);
			javaWriter.println("\t\t\tcase \"" + linkName + "\":");
			javaWriter.println("\t\t\t\treturn get" + linkName + "();");
		}
		javaWriter.println("\t\t\tdefault:");
		javaWriter.println("\t\t\t\treturn super.getLinkedObjects(link);");
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addPageContainsStructContentItemsMethod() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Constants.PAGE_CONTAINS_STRUCT_CONTENT_ITEMS));
		javaWriter.println("\t\tCOSObject contents = this.baseObject.getKey(ASAtom.CONTENTS);");
		javaWriter.println("\t\tif (contents.getType() == COSObjType.COS_STREAM || contents.getType() == COSObjType.COS_ARRAY) {");
		javaWriter.println("\t\t\ttry (ASInputStream opStream = contents.getDirectBase().getData(COSStream.FilterFlags.DECODE);" +
				" PDFStreamParser streamParser = new PDFStreamParser(opStream)) {");
		javaWriter.println("\t\t\t\tstreamParser.parseTokens();");
		javaWriter.println("\t\t\t\tList<COSBase> arguments = new ArrayList<>();");
		javaWriter.println("\t\t\t\tfor (java.lang.Object rawToken : streamParser.getTokens()) {");
		javaWriter.println("\t\t\t\t\tif (rawToken instanceof COSBase) {");
		javaWriter.println("\t\t\t\t\t\targuments.add((COSBase) rawToken);");
		javaWriter.println("\t\t\t\t\t} else if (rawToken instanceof Operator) {");
		javaWriter.println("\t\t\t\t\t\tString operatorName = ((Operator)rawToken).getOperator();");
		javaWriter.println("\t\t\t\t\t\tif (Operators.BMC.equals(operatorName) || Operators.BDC.equals(operatorName)) {");
		javaWriter.println("\t\t\t\t\t\t\tif (arguments.isEmpty()) {");
		javaWriter.println("\t\t\t\t\t\t\t\tcontinue;");
		javaWriter.println("\t\t\t\t\t\t\t}");
		javaWriter.println("\t\t\t\t\t\t\tCOSBase lastArgument = arguments.get(arguments.size() - 1);");
		javaWriter.println("\t\t\t\t\t\t\tif (lastArgument.getType() == COSObjType.COS_NAME) {");
		javaWriter.println("\t\t\t\t\t\t\t\t//todo check dict from properties");
		javaWriter.println("\t\t\t\t\t\t\t}");
		javaWriter.println("\t\t\t\t\t\t\tif (lastArgument.getType() == COSObjType.COS_DICT) {");
		javaWriter.println("\t\t\t\t\t\t\t\tif (lastArgument.knownKey(ASAtom.MCID)) {");
		javaWriter.println("\t\t\t\t\t\t\t\t\treturn true;");
		javaWriter.println("\t\t\t\t\t\t\t\t}");
		javaWriter.println("\t\t\t\t\t\t\t}");
		javaWriter.println("\t\t\t\t\t\t}");
		javaWriter.println("\t\t\t\t\t\targuments = new ArrayList<>();");
		javaWriter.println("\t\t\t\t\t}");
		javaWriter.println("\t\t\t\t}");
		javaWriter.println("\t\t\t} catch (IOException exception) {");
		javaWriter.println("\t\t\t\treturn false;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn false;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addImageIsStructContentItemMethod() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM));
		javaWriter.println("\t\treturn GFAObject.getKeysSet().contains(this.baseObject.getKey());");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addArrayLengthMethod(String entryName) {
		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getMethodName(Entry.getArrayLengthPropertyName(entryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType() == " + Type.ARRAY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn (long) " + objectName + ".size();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addStringLengthMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getMethodName(entry.getStringLengthPropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (object != null && object.getType() == " + Type.STRING.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn (long) object.getString().length();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addStreamLengthMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getMethodName(entry.getStreamLengthPropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (object != null && object.getType() == " + Type.STREAM.getCosObjectType() + ") {");
		javaWriter.println("\t\t\tCOSObject length = object.getKey(ASAtom.LENGTH);");
		javaWriter.println("\t\t\tif (length != null && length.getType() == " + Type.INTEGER.getCosObjectType() + ") {");
		javaWriter.println("\t\t\t\treturn length.getInteger();");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addRectWidthMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.NUMBER.getJavaType(),
				getMethodName(entry.getRectWidthPropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.ARRAY.getCosObjectType() + " || object.size() != 4) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject left = object.at(0);");
		javaWriter.println("\t\tCOSObject right = object.at(2);");
		javaWriter.println("\t\tif (left == null || (left.getType() != " + Type.INTEGER.getCosObjectType() + " && left.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (right == null || (right.getType() != " + Type.INTEGER.getCosObjectType() + " && right.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn right.getReal() - left.getReal();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addRectHeightMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.NUMBER.getJavaType(),
				getMethodName(entry.getRectHeightPropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.ARRAY.getCosObjectType() + " || object.size() != 4) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject bottom = object.at(1);");
		javaWriter.println("\t\tCOSObject top = object.at(3);");
		javaWriter.println("\t\tif (bottom == null || (bottom.getType() != " + Type.INTEGER.getCosObjectType() + " && bottom.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (top == null || (top.getType() != " + Type.INTEGER.getCosObjectType() + " && top.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn top.getReal() - bottom.getReal();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHexStringMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(entry.getIsHexStringPropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn object != null && object.getType() == " + Type.STRING.getCosObjectType() + " && ((COSString)object.getDirectBase()).isHexadecimal();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addFieldNameMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Entry.getIsFieldNamePropertyName(entry.getName())));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn object != null && object.getType() == " + Type.STRING.getCosObjectType() + " && !object.getString().contains(\".\");");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHasCycleMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(entry.getHasCyclePropertyName()));
		getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn GFAObject." + Constants.HAS_CYCLE + "(object, " + getASAtomFromString(entry.getName()) + ");");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHasExtensionMethod(MultiObject multiObject, String extensionName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getMethodName(Object.getHasExtensionPropertyName(extensionName)));
		javaWriter.println("\t\treturn false;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addEntriesStringMethod(String entryName) {
		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getMethodName(Entry.getEntriesStringPropertyName(entryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		javaWriter.println("\t\tif (" + objectName + " == null) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (" + objectName + ".getType() == " + Type.NAME.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn " + objectName + Type.NAME.getParserMethod() + ";");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (" + objectName + ".getType() != " + Type.ARRAY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tList<String> names = new LinkedList<>();");
		javaWriter.println("\t\tfor (COSObject elem : (COSArray)" + objectName + ".getDirectBase()) {");
		javaWriter.println("\t\t\tif (elem.getType() == " + Type.NAME.getCosObjectType() + ") {");
		javaWriter.println("\t\t\t\tnames.add(elem" + Type.NAME.getParserMethod() + ");");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn String.join(\"&\", names);");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addKeysStringMethod(String entryName) {
		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getMethodName(Entry.getKeysStringPropertyName(entryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		javaWriter.println("\t\treturn " + objectName + ".getKeySet().stream()");
		javaWriter.println("\t\t\t\t.map(ASAtom::getValue)");
		javaWriter.println("\t\t\t\t.collect(Collectors.joining(\"&\"));");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public String getObjectByEntryName(String entryName) {
		javaWriter.println("\t\tCOSObject object = " + getMethodName(Entry.getValuePropertyName(entryName)) + "();");
		return "object";
	}

	public void getEntryCOSObject(Object multiObject, String entryName) {
		boolean addDefault = multiObject.getJavaGeneration().getDefaultObject(multiObject, entryName);
		printMethodSignature(false, "public", false, "COSObject",
				getMethodName(Entry.getValuePropertyName(entryName)));
		if (Constants.FILE_TRAILER.equals(multiObject.getId()) && Constants.XREF_STREAM.equals(entryName)) {
			javaWriter.println("\t\tLong offset = " + getMethodName(Entry.getTypeValuePropertyName(Constants.XREF_STM, Type.INTEGER)) + "();");
			javaWriter.println("\t\tCOSObject object = offset != null ? StaticResources.getDocument().getDocument().getObject(offset) : null;");
		} else if (Constants.CURRENT_ENTRY.equals(entryName)) {
			javaWriter.println("\t\tCOSObject object = new COSObject(this.baseObject);");
		} else if (Entry.isNumber(entryName)) {
			javaWriter.println("\t\tif (this.baseObject.size() <= " + entryName + ") {");
			javaWriter.println("\t\t\treturn null;");
			javaWriter.println("\t\t}");
			javaWriter.println("\t\tCOSObject object = this.baseObject.at(" + entryName + ");");
		} else {
			javaWriter.println("\t\tCOSObject object = this.baseObject.getKey(" + getASAtomFromString(entryName) + ");");
		}
		String objectName = "object";
		addGetInheritable(multiObject.getId(), entryName);
		if (addDefault) {
			javaWriter.println("\t\tif (" + objectName + " == null || " + objectName + ".empty()) {");
			javaWriter.println("\t\t\t" + objectName + " = " + getMethodName(Entry.getDefaultValuePropertyName(entryName)) + "();");
			javaWriter.println("\t\t}");
		}
		javaWriter.println("\t\treturn " + objectName + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addCommonGetLink(String entryName, String returnType, List<List<PDFVersion>> versions) {
		String linkName = Links.getLinkName(entryName);
		String returnObjectType = Constants.OBJECT.equals(returnType) ? "org.verapdf.model.baselayer.Object" : returnType;
		javaWriter.println("\tprivate List<" + returnObjectType + "> get" + linkName + "() {");
		javaWriter.println("\t\tswitch (StaticContainers.getFlavour()) {");
		for (List<PDFVersion> versionsList : versions) {
			for (PDFVersion version : versionsList) {
				javaWriter.println("\t\t\tcase ARLINGTON" + version.getStringWithUnderScore() + ":");
			}
			String versionString = versionsList.get(0).getStringWithUnderScore();
			javaWriter.println("\t\t\t\treturn get" + linkName + versionString + "();");
		}
		javaWriter.println("\t\t\tdefault:");
		javaWriter.println("\t\t\t\treturn Collections.emptyList();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
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
