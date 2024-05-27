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
		addPackageAndImportsToClass(Constants.OBJECT);
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
		javaWriter.println("\t\tCOSKey key = baseObject != null ? baseObject.getObjectKey() : null;");
		javaWriter.println("\t\treturn key != null ? (getObjectType() + \" \" + key.toString()) : null;");
		javaWriter.println("\t}");
		javaWriter.println();

		javaWriter.println("\t@Override");
		javaWriter.println("\tpublic String getExtraContext() {");
		javaWriter.println("\t\treturn keyName == null || keyName.isEmpty() ? null : keyName;");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getGetterName(Constants.SIZE));
		javaWriter.println("\t\treturn (long)baseObject.size();");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getGetterName(Constants.KEY_NAME));
		javaWriter.println("\t\treturn this.keyName;");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getGetterName(Constants.KEYS_STRING));
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Constants.KEYS_STRING),
				"new COSObject(this.baseObject)") + ";");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getGetterName(Constants.NUMBER_OF_PAGES));
		javaWriter.println("\t\treturn (long) StaticResources.getDocument().getPages().size();");
		javaWriter.println("\t}");
		javaWriter.println();

		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getGetterName(Constants.FILE_SIZE));
		javaWriter.println("\t\treturn StaticResources.getDocument().getDocument().getFileSize();");
		javaWriter.println("\t}");
		javaWriter.println();

		addIsPDFTagged();
		addGetObjectType();
		addNotStandard14FontMethod();
		addRectHeightMethod();
		addRectWidthMethod();
		addIndirectMethod();
		addImageIsStructContentItemBaseMethod();
		addArrayLengthMethod();
		addArraySortAscendingMethod();
		addKeysStringMethod();
		addEntriesStringMethod();
		addIsEncryptedWrapperMethod();
		addHasCycleMethod();
		addGetInheritable();
		addPageObjectMethod();
		addContainsInheritableValueMethod();
		for (String extensionName : Main.extensionNames) {
			addHasExtensionMethod(extensionName);
		}
		for (Type type : Type.values()) {
			addGetValueMethod(type);
			addHasTypeMethod(type);
		}

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

	public void addIsEncryptedWrapperMethod() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Constants.IS_ENCRYPTED_WRAPPER));
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
	}

	public void addHasCycleMethod() {
		printMethodSignature(false, "public", true, Type.BOOLEAN.getJavaType(),
				Constants.HAS_CYCLE, "COSObject object", "ASAtom entryName");
		javaWriter.println("\t\tif (object == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tSet<COSKey> visitedKeys = new HashSet<>();");
		javaWriter.println("\t\twhile (!object.empty() && object.knownKey(entryName)) {");
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
	}

	public void addPageObjectMethod() {
		printMethodSignature(false, "protected", true, "COSObject",
				getGetterName(Constants.PAGE_OBJECT), "COSObject object");
		javaWriter.println("\t\tLong pageNumber = null;");
		javaWriter.println("\t\tif (object != null && object.getType() == " + Type.STRING_BYTE.getCosObjectType() + ") {");
		javaWriter.println("\t\t\tPDNamesDictionary names = StaticResources.getDocument().getCatalog().getNamesDictionary();");
		javaWriter.println("\t\t\tif (names == null) {");
		javaWriter.println("\t\t\t\treturn null;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tPDNameTreeNode dests = names.getDests();");
		javaWriter.println("\t\t\tif (dests == null) {");
		javaWriter.println("\t\t\t\treturn null;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tobject = dests.getObject(object" + Type.STRING_BYTE.getParserMethod() + ");");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (object != null && object.getType() == " + Type.INTEGER.getCosObjectType() + ") {");
		javaWriter.println("\t\t\tpageNumber = object" + Type.INTEGER.getParserMethod() + ";");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (pageNumber == null || pageNumber >= StaticResources.getDocument().getPages().size()) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn StaticResources.getDocument().getPages().get(pageNumber.intValue()).getObject();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHasTypeMethod(Type type) {
		if (type == Type.ENTRY || type == Type.SUB_ARRAY) {
			return;
		}
		printMethodSignature(false, "public", true, Type.BOOLEAN.getJavaType(),
				getGetterName(Entry.getHasTypePropertyName("", type)), "COSObject object");
		String objectName = "object";
		if (type == Type.NUMBER) {
			javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType().isNumber();");
		} else if (type == Type.DATE) {
			javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType() == " +
					type.getCosObjectType() + " && " + objectName + ".getString().matches(PDF_DATE_FORMAT_REGEX);");
		} else if (type == Type.RECTANGLE || type == Type.MATRIX) {
			javaWriter.println("\t\tif (" + objectName + " == null || " + objectName + ".getType() != " +
					type.getCosObjectType() + " || " + objectName + ".size() != " + (type == Type.MATRIX ? 6 : 4) + ") {");
			javaWriter.println("\t\t\treturn false;");
			javaWriter.println("\t\t}");
			javaWriter.println("\t\tfor (COSObject elem : (COSArray)" + objectName + ".getDirectBase()) {");
			javaWriter.println("\t\t\tif (elem == null || (elem.getType() != " + Type.NUMBER.getCosObjectType() +
					" && elem.getType() != " + Type.INTEGER.getCosObjectType() + ")) {");
			javaWriter.println("\t\t\t\treturn false;");
			javaWriter.println("\t\t\t}");
			javaWriter.println("\t\t}");
			javaWriter.println("\t\treturn true;");
		} else if (type == Type.STRING_TEXT) {
			javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType() == " +
					type.getCosObjectType() + " && ((COSString)" + objectName + ".getDirectBase()).isTextString();");
		} else if (type == Type.STRING_ASCII) {
			javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType() == " +
					type.getCosObjectType() + " && ((COSString)" + objectName + ".getDirectBase()).isASCIIString();");
		} else {
			javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType() == " +
					type.getCosObjectType() + ";");
		}
		javaWriter.println("\t}");
		javaWriter.println();
	}


	public void addClassStart(Object object) {
		javaWriter.println("public class " + object.getJavaClassName() + " extends GFAObject implements " +
				object.getModelType() + " {");
		javaWriter.println();
		if (Constants.FONT_FILE_2.equals(object.getId())) {
			javaWriter.println("\tprivate COSBase parentParentObject;");
			javaWriter.println();
			javaWriter.println("\tpublic " + object.getJavaClassName() +
					"(COSBase baseObject, COSBase parentObject, COSBase parentParentObject, String keyName) {");
			javaWriter.println("\t\tsuper(baseObject, parentObject, keyName, \"" + object.getModelType() + "\");");
			javaWriter.println("\t\tthis.parentParentObject = parentParentObject;");
		} else if (object.isEntry()) {
			javaWriter.println("\tprivate COSBase parentParentObject;");
			javaWriter.println("\tprivate String collectionName;");
			javaWriter.println();
			javaWriter.println("\tpublic " + object.getJavaClassName() +
					"(COSBase baseObject, COSBase parentObject, COSBase parentParentObject, String collectionName, String keyName) {");
			javaWriter.println("\t\tsuper(baseObject, parentObject, keyName, \"" + object.getModelType() + "\");");
			javaWriter.println("\t\tthis.parentParentObject = parentParentObject;");
			javaWriter.println("\t\tthis.collectionName = collectionName;");
		} else {
			javaWriter.println("\tpublic " + object.getJavaClassName() + "(COSBase baseObject, COSBase parentObject, String keyName) {");
			javaWriter.println("\t\tsuper(baseObject, parentObject, keyName, \"" + object.getModelType() + "\");");
		}
		if (Constants.FILE_TRAILER.equals(object.getId())) {
		} else if (Constants.OBJECT_REFERENCE.equals(object.getId())) {
			javaWriter.println("\t\tCOSObject obj = this.baseObject.getKey(ASAtom.OBJ);");
			javaWriter.println("\t\tif (obj != null && obj.getKey() != null) {");
			javaWriter.println("\t\t\tGFAObject.getKeysSet().add(obj.getKey());");
			javaWriter.println("\t\t}");
		}
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addSize(Object object) {
		if (object.isNameTree() || object.isNumberTree()) {
			printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
					getGetterName(Constants.SIZE));
			if (object.isNameTree()) {
				javaWriter.println("\t\treturn PDNameTreeNode.create(new COSObject(baseObject)).size();");
			} else {
				javaWriter.println("\t\treturn new PDNumberTreeNode(new COSObject(baseObject)).size();");
			}
			javaWriter.println("\t}");
			javaWriter.println();
		}
	}

	public boolean getDefaultObject(Object multiObject, String entryName) {
		Map<String, List<PDFVersion>> map = MultiEntry.getDefaultValueMap(multiObject, entryName);
		if (map.isEmpty()) {
			return false;
		}
		printMethodSignature(false, "public", false, "COSObject",
				getGetterName(Entry.getDefaultValuePropertyName(entryName)));
		if (map.size() == 1 && map.values().iterator().next().size() == PDFVersion.values().length) {
			javaWriter.println("\t\treturn " + map.keySet().iterator().next() + ";");
		} else {
			javaWriter.println("\t\tswitch (StaticContainers.getFlavour()) {");
			for (Map.Entry<String, List<PDFVersion>> value : map.entrySet()) {
				for (PDFVersion version : value.getValue()) {
					javaWriter.println("\t\t\tcase ARLINGTON" + version.getStringWithUnderScore() + ":");
				}
				String obj = value.getKey();
				javaWriter.println("\t\t\t\treturn " + obj + ";");
			}
			javaWriter.println("\t\t}");
			javaWriter.println("\t\treturn null;");
		}
		javaWriter.println("\t}");
		javaWriter.println();
		return true;
	}

	public void addPackageAndImportsToClass(String objectName) {
		Main.addPackage(javaWriter, "org.verapdf.gf.model.impl.arlington");
		javaWriter.println();
		Main.addImport(javaWriter, "org.verapdf.cos.*");
		Main.addImport(javaWriter, "org.verapdf.model.alayer.*");
		Main.addImport(javaWriter, "org.verapdf.gf.model.impl.containers.StaticContainers");
		if (Constants.OBJECT.equals(objectName)) {
			Main.addImport(javaWriter, "org.verapdf.extensions.ExtensionObjectType");
			Main.addImport(javaWriter, "org.verapdf.model.GenericModelObject");
			Main.addImport(javaWriter, "org.verapdf.pd.PDDocument");
			Main.addImport(javaWriter, "org.verapdf.pd.PDCatalog");
			Main.addImport(javaWriter, "org.verapdf.pd.PDNamesDictionary");
		}
		Main.addImport(javaWriter, "org.verapdf.tools.StaticResources");
		Main.addImport(javaWriter, "java.util.*");
		Main.addImport(javaWriter, "org.verapdf.pd.PDNameTreeNode");
		Main.addImport(javaWriter, "org.verapdf.as.ASAtom");
		Main.addImport(javaWriter, "java.util.stream.Collectors");
		if (!Constants.OBJECT.equals(objectName)) {
			Main.addImport(javaWriter, "org.verapdf.pd.structure.PDNumberTreeNode");
		}
		if (Constants.PAGE_OBJECT.equals(objectName)) {
			Main.addImport(javaWriter, "org.verapdf.model.tools.constants.Operators");
			Main.addImport(javaWriter, "org.verapdf.operator.Operator");
			Main.addImport(javaWriter, "org.verapdf.as.io.ASInputStream");
			Main.addImport(javaWriter, "org.verapdf.parser.PDFStreamParser");
			Main.addImport(javaWriter, "java.io.IOException");
		}
		javaWriter.println();
	}

	public void addLinkGetterByDifferentKeys(Map<String, LinkHelper> map, Object object, Entry entry, Type type,
											 PDFVersion version, int index, String methodNamePostfix) {
		String linkName = Links.getLinkName(entry.getName());
		printMethodSignature(false, "private", false,
				Constants.BASE_MODEL_OBJECT_PATH, getGetterName(linkName + type.getType() + methodNamePostfix +
						version.getStringWithUnderScore()), "COSBase base", "String keyName");
		List<String> links = entry.getLinks(type).stream().filter(link -> map.containsKey(link.contains(PredicatesParser.PREDICATE_PREFIX) ?
				PredicatesParser.getPredicateLastArgument(link) : link)).collect(Collectors.toList());
		SortedMap<String, Set<String>> newMap = Links.getDifferentKeysLinksMap(links, map);
		String string = "base";
		if (((DifferentKeysLinkHelper)map.values().iterator().next()).isChild()) {
			string = "objectBase";
			javaWriter.println("\t\tCOSBase objectBase = COSDictionary.construct().getDirectBase();");
			javaWriter.println("\t\tfor (ASAtom key : base.getKeySet()) {");
			javaWriter.println("\t\t\tCOSObject obj = base.getKey(key);");
			javaWriter.println("\t\t\tif (obj != null && obj.getDirectBase() != null) {");
			javaWriter.println("\t\t\t\tobjectBase = obj.getDirectBase();");
			javaWriter.println("\t\t\t\tbreak;");
			javaWriter.println("\t\t\t}");
			javaWriter.println("\t\t}");
		}
		for (Map.Entry<String, Set<String>> mapEntry : newMap.entrySet()) {
			if (mapEntry.getKey().isEmpty()) {
				continue;
			}
			javaWriter.println("\t\tif (" + string + ".knownKey(" + getASAtomFromString(mapEntry.getKey()) + ")) {");//merge cases
			if (mapEntry.getValue().size() != 1) {
				javaWriter.println("\t\t\treturn " + getMethodCall(getGetterName(linkName + type.getType() + methodNamePostfix +
						mapEntry.getKey() + version.getStringWithUnderScore()), "base", "keyName") + ";");
			} else {
				javaWriter.println("\t\t\treturn " + constructorGFAObject(entry.getName(), mapEntry.getValue().iterator().next(),
						"base", "this.baseObject", "keyName") + ";");
			}
			javaWriter.println("\t\t}");
		}
		if (newMap.get("") != null) {
			javaWriter.println("\t\treturn " + constructorGFAObject(entry.getName(), newMap.get("").iterator().next(),
					"base", "this.baseObject", "keyName") + ";");
		} else {
			javaWriter.println("\t\treturn null;");
		}
		javaWriter.println("\t}");
		javaWriter.println();
		for (Map.Entry<String, Set<String>> mapEntry : newMap.entrySet()) {
			if (mapEntry.getValue().size() != 1) {
				Map<String, LinkHelper> newHelperMap = LinkHelper.getMap(mapEntry.getValue());
				if (newHelperMap != null) {
					addLinkGetterByKeyValues(newHelperMap, object, entry, type, version, index + 1, methodNamePostfix + mapEntry.getKey());
				}
			}
		}
	}

	public void addLinkGetterByKeyValues(Map<String, LinkHelper> map, Object object, Entry entry, Type type,
										 PDFVersion version, int index, String methodNamePostfix) {
		String linkName = Links.getLinkName(entry.getName());
		printMethodSignature(false, "private", false, Constants.BASE_MODEL_OBJECT_PATH,
				getGetterName(linkName + type.getType() + methodNamePostfix + version.getStringWithUnderScore()),
				"COSBase base", "String keyName");
		List<String> links = entry.getLinks(type).stream().filter(link -> map.containsKey(link.contains(PredicatesParser.PREDICATE_PREFIX) ?
				PredicatesParser.getPredicateLastArgument(link) : link)).collect(Collectors.toList());
		List<String> correctLinks = entry.getLinksWithoutPredicatesList(links);
		DifferentKeysValuesLinkHelper helper = (DifferentKeysValuesLinkHelper)map.get(correctLinks.iterator().next());
		Key key = helper.getKey();
		calculateSubtype(object, entry, key);
		javaWriter.println("\t\t" + key.getType().getJavaType() + " subtypeValue = subtype" +
				key.getType().getParserMethod() + ";");
		Set<String> defaultSet = addDefaultCaseLinkGetterByKeyValues(map, version, entry, type, linkName, correctLinks, methodNamePostfix);
		SortedMap<String, Set<String>> newMap = addSwitchLinkGetterByKeyValues(map, version, object, entry, type, key, links,
				correctLinks, linkName, methodNamePostfix);
		javaWriter.println("\t}");
		javaWriter.println();
		for (Map.Entry<String, Set<String>> mapEntry : newMap.entrySet()) {
			if (mapEntry.getValue().size() != 1) {
				Links.addGetter(LinkHelper.getMap(mapEntry.getValue()), object, entry, type, version, index + 1, methodNamePostfix + mapEntry.getKey());
			}
		}
		if (defaultSet.size() > 1) {
			Links.addGetter(LinkHelper.getMap(defaultSet), object, entry, type, version, index + 1, methodNamePostfix + "Default");
		}
	}

	private void calculateSubtype(Object object, Entry entry, Key key) {
		String objectName = key.isParent() ? "this.baseObject" : "base";
		String keyName = key.isParent() && "FDecodeParms".equals(entry.getName()) ? "FFilter" : key.getKeyName();
		if (Constants.ARRAY_OF_DECODE_PARAMS_ENTRY.equals(object.getId())) {
			javaWriter.println("\t\tString name = \"FDecodeParms\".equals(collectionName) ? \"FFilter\" : \"Filter\";");
			javaWriter.println("\t\tCOSObject object = this.parentParentObject.getKey(ASAtom.getASAtom(name));");
			javaWriter.println("\t\tint keyNumber = Integer.parseInt(keyName);");
			javaWriter.println("\t\tif (object == null) { ");
			javaWriter.println("\t\t\treturn null;");
			javaWriter.println("\t\t}");
			javaWriter.println("\t\tCOSObject subtype = object.at(keyNumber);");
		} else if (key.getKeyName().matches("\\d+")) {
			javaWriter.println("\t\tif (" + objectName + ".size() <= " + keyName + ") {");
			javaWriter.println("\t\t\treturn null;");
			javaWriter.println("\t\t}");
			javaWriter.println("\t\tCOSObject subtype = " + objectName + ".at(" + keyName + ");");
		} else {
			javaWriter.println("\t\tCOSObject subtype = " + objectName + ".getKey(" + getASAtomFromString(keyName) + ");");
		}
		if (key.isInherited()) {
			javaWriter.println("\t\tCOSObject parent = " + objectName + ".getKey(" + getASAtomFromString(Constants.PARENT_KEY) + ");");
			javaWriter.println("\t\twhile ((subtype == null || subtype.empty()) && (parent != null && !parent.empty())) {");
			javaWriter.println("\t\t\tsubtype = " + objectName + ".getKey(" + getASAtomFromString(keyName) + ");");
			javaWriter.println("\t\t\tparent = " + objectName + ".getKey(" + getASAtomFromString(Constants.PARENT_KEY) + ");");
			javaWriter.println("\t\t}");
		}
		javaWriter.println("\t\tif (subtype == null) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
	}

	private Set<String> addDefaultCaseLinkGetterByKeyValues(Map<String, LinkHelper> map, PDFVersion version, Entry entry,
															Type type, String linkName, List<String> correctLinks, String methodNamePostfix) {
		javaWriter.println("\t\tif (subtypeValue == null) {");
		Set<String> defaultSet = new HashSet<>();
		for (String link : correctLinks) {//refactoring
			DifferentKeysValuesLinkHelper helper = (DifferentKeysValuesLinkHelper)map.get(link);
			if (helper != null && helper.getKey().isDefault()) {
				defaultSet.add(link);
			}
		}
		if (defaultSet.isEmpty()) {
			javaWriter.println("\t\t\treturn null;");
		} else if (defaultSet.size() == 1) {
			javaWriter.println("\t\t\treturn " + constructorGFAObject(entry.getName(), defaultSet.iterator().next(),
					"base", "this.baseObject", "keyName") + ";");
		} else {
			javaWriter.println("\t\t\treturn " + getMethodCall(getGetterName(linkName + type.getType() + methodNamePostfix + 
					"Default" + version.getStringWithUnderScore()), "base", "keyName") + ";");
		}
		javaWriter.println("\t\t}");
		return defaultSet;
	}

	private SortedMap<String, Set<String>> addSwitchLinkGetterByKeyValues(Map<String, LinkHelper> map, PDFVersion version,
																		  Object object, Entry entry, Type type,
																		  Key key, List<String> links,
																		  List<String> correctLinks, String linkName, 
																		  String methodNamePostfix) {
		if (key.getBit() != null) {
			javaWriter.println("\t\tswitch (subtypeValue.intValue() >> " + (key.getBit() - 1) + ") {");
		} else if (Type.INTEGER == key.getType()) {
			javaWriter.println("\t\tswitch (subtypeValue.intValue()) {");
		} else {
			javaWriter.println("\t\tswitch (subtypeValue) {");
		}
		SortedMap<String, Set<String>> newMap = Links.getDifferentKeysValuesLinksMap(correctLinks, map);
		for (Map.Entry<String, Set<String>> mapEntry : newMap.entrySet()) {
			javaWriter.println("\t\t\tcase " + key.getType().getValueWithSeparator(mapEntry.getKey()) + ":");
			if (mapEntry.getValue().size() != 1) {
				javaWriter.println("\t\t\t\treturn " + getMethodCall(getGetterName(linkName + type.getType() +
						methodNamePostfix + mapEntry.getKey() + version.getStringWithUnderScore()), "base", "keyName") + ";");
			} else {
				String link = mapEntry.getValue().iterator().next();
				int index1 = correctLinks.indexOf(link);
				if (links.get(index1).contains(PredicatesParser.PREDICATE_PREFIX)) {
					linkPredicate(object, entry, type, version, links.get(index1), Type.ENTRY);
				}
				javaWriter.println("\t\t\t\treturn " + constructorGFAObject(entry.getName(), link, "base",
						"this.baseObject", "keyName") + ";");
			}
		}
		javaWriter.println("\t\t\tdefault:");
		javaWriter.println("\t\t\t\treturn null;");
		javaWriter.println("\t\t}");
		return newMap;
	}

	public void addLinkGetterByKeyName(Map<String, LinkHelper> map, Object object, Entry entry, Type type, PDFVersion version) {
		String linkName = Links.getLinkName(entry.getName());
		printMethodSignature(false, "private", false, Constants.BASE_MODEL_OBJECT_PATH,
				getGetterName(linkName + type.getType() + version.getStringWithUnderScore()),
				"COSBase base", "String keyName");
		
		String string = ((KeyNameLinkHelper)map.values().iterator().next()).isCheckCollectionName() ? "collectionName" : "keyName";
		javaWriter.println("\t\tswitch (" + string + ") {");
		String defaultLink = null;
		for (String link : entry.getLinks(type)) {
			if (link.contains(PredicatesParser.PREDICATE_PREFIX)) {
				LOGGER.log(Level.WARNING, Main.getString(version, object, entry, type) + " link contains predicate");
			}
			KeyNameLinkHelper helper = (KeyNameLinkHelper)map.get(link);
			if (helper != null) {
				if (helper.getKeyName() != null) {
					javaWriter.println("\t\t\tcase \"" + helper.getKeyName() + "\":");
					javaWriter.println("\t\t\t\treturn " + constructorGFAObject(entry.getName(), link, "base",
							"this.baseObject", "keyName") + ";");
				} else {
					defaultLink = link;
				}
			}
		}
		javaWriter.println("\t\t\tdefault:");
		if (defaultLink != null) {
			javaWriter.println("\t\t\t\treturn " + constructorGFAObject(entry.getName(), defaultLink, "base",
					"this.baseObject", "keyName") + ";");
		} else {
			javaWriter.println("\t\t\t\treturn null;");
		}
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
	}
	
	public void addEntryTypeMethod(String entryName) {
		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getGetterName(Entry.getEntryTypePropertyName(entryName)));
		String objectName = getObjectByEntryName(entryName);
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Constants.OBJECT_TYPE), objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHasTypeMethod(Object multiObject, String entryName, Type type) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Entry.getHasTypePropertyName(entryName, type)));
		String arlingtonObject = multiObject.getEntryNameToArlingtonObjectMap().get(entryName);
		String objectName = entryName.contains("::") ? getComplexObject(arlingtonObject != null ? 
				entryName.substring(0, entryName.lastIndexOf("::")) : entryName) :
				getObjectByEntryName(entryName);
		if (arlingtonObject != null) {
			String[] objects = entryName.split("::");
			String finalEntryName = objects[objects.length - 1].replaceAll("@","");
			checkComplexObjectLastEntry(objectName, finalEntryName);
			javaWriter.println("\t\treturn " + constructorGFAObject(entryName, arlingtonObject,  objectName +
					".getDirectBase()", null, null) + "." +
					getGetterName(Entry.getHasTypePropertyName(finalEntryName, type)) + "();");
			javaWriter.println("\t}");
			javaWriter.println();
			return;
		}
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getHasTypePropertyName("", type)),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addGetValueMethod(MultiObject multiObject, String entryName, Type type) {
		printMethodSignature(true, "public", false, type.getJavaType(),
				getGetterName(Entry.getTypeValuePropertyName(entryName, type)));
		String arlingtonObject = multiObject.getEntryNameToArlingtonObjectMap().get(entryName);
		String objectName = entryName.contains("::") ? getComplexObject(arlingtonObject != null ?
				entryName.substring(0, entryName.lastIndexOf("::")) : entryName) :
				getObjectByEntryName(entryName);
		if (arlingtonObject != null) {
			String[] objects = entryName.split("::");
			String finalEntryName = objects[objects.length - 1].replaceAll("@","");
			checkComplexObjectLastEntry(objectName, finalEntryName);
			javaWriter.println("\t\treturn " + constructorGFAObject(entryName, arlingtonObject, objectName +
					".getDirectBase()", null, null) + "." +
					getMethodCall(getGetterName(Entry.getTypeValuePropertyName(finalEntryName, type))) + ";");
			javaWriter.println("\t}");
			javaWriter.println();
			return;
		}
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getTypeValuePropertyName("", type)),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	private void checkComplexObjectLastEntry(String objectName, String finalEntryName) {
		if (finalEntryName.matches(Constants.NUMBER_REGEX)) {
			javaWriter.println("\t\tif (" + objectName + " == null || " + objectName +
					".getType() != " + Type.ARRAY.getCosObjectType() + " || " + objectName + ".size() <= " + finalEntryName + ") {");
		} else {
			javaWriter.println("\t\tif (" + objectName + " == null || !" + objectName +
					".getType().isDictionaryBased()) {");
		}
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
	}

	public void addGetValueMethod(Type type) {
		if (type == Type.ENTRY || type == Type.SUB_ARRAY || type == Type.DICTIONARY || type == Type.ARRAY ||
				type == Type.NULL || type == Type.RECTANGLE || type == Type.MATRIX || type == Type.NAME_TREE ||
				type == Type.NUMBER_TREE || type == Type.STREAM) {
			return;
		}
		printMethodSignature(false, "public", true, type.getJavaType(),
				getGetterName(Entry.getTypeValuePropertyName("", type)), "COSObject object");
		String objectName = "object";
		if (type == Type.NUMBER) {
			javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType().isNumber()) {");
		} else if (type == Type.DATE) {
			javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType() == " +
					type.getCosObjectType() + " && " + objectName + ".getString().matches(GFAObject.PDF_DATE_FORMAT_REGEX)) {");
		} else {
			javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType() == " +
					type.getCosObjectType() + ") {");
		}
		if (type == Type.STRING_ASCII) {
			javaWriter.println("\t\t\treturn ((COSString)" + objectName + ".getDirectBase()).getASCIIString();");
		} else {
			javaWriter.println("\t\t\treturn " + objectName + type.getParserMethod() + ";");
		}
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addLinkGetterBySize(Map<String, LinkHelper> map, Object object, Entry entry, Type type, PDFVersion version) {
		String linkName = Links.getLinkName(entry.getName());
		printMethodSignature(false, "private", false,
				Constants.BASE_MODEL_OBJECT_PATH, getGetterName(linkName + type.getType() +
						version.getStringWithUnderScore()), "COSBase base", "String keyName");
		javaWriter.println("\t\tswitch (base.size()) {");
		SortedMap<Integer, String> linksMap = Links.getSizeLinksMap(version, object, entry, type, entry.getLinks(type), map);
		for (Map.Entry<Integer, String> mapEntry : linksMap.entrySet()) {
			javaWriter.println("\t\t\tcase " + mapEntry.getKey() + ":");
			javaWriter.println("\t\t\t\treturn " + constructorGFAObject(entry.getName(), mapEntry.getValue(),
					"base", "this.baseObject", "keyName") + ";");
		}
		javaWriter.println("\t\t\tdefault:");
		javaWriter.println("\t\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addOneLink(Object object, Entry entry, String returnType, PDFVersion version) {
		String linkName = Links.getLinkName(entry.getName());
		String returnObjectType = Constants.OBJECT.equals(returnType) ? Constants.BASE_MODEL_OBJECT_PATH : returnType;
		printMethodSignature(false, "private", false, "List<" + returnObjectType + ">",
				getGetterName(linkName + version.getStringWithUnderScore()));
		String parentObject = getObjectForOneLinkMethod(entry);
		for (Type type : entry.getUniqLinkTypes()) {
			if (Type.ENTRY == type) {
				continue;
			}
			Set<String> links = new HashSet<>(entry.getLinks(type));
			if (links.isEmpty()) {
				continue;
			}
			addObjectToListOneLink(version, object, entry, type, links, linkName, parentObject);
		}
		javaWriter.println("\t\treturn Collections.emptyList();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	private String getObjectForOneLinkMethod(Entry entry) {
		if (Constants.CURRENT_ENTRY.equals(entry.getName())) {
			javaWriter.println("\t\tCOSObject object = " + constructor("COSObject", "this.baseObject") + ";");
			return "this.parentObject";
		}
		javaWriter.println("\t\tCOSObject object = " +
				getMethodCall(getGetterName(Entry.getValuePropertyName(entry.getName()))) + ";");
		javaWriter.println("\t\tif (object == null) {");
		javaWriter.println("\t\t\treturn Collections.emptyList();");
		javaWriter.println("\t\t}");
		return "this.baseObject";
	}

	private void addObjectToListOneLink(PDFVersion version, Object object, Entry entry, Type type, Set<String> links,
										String linkName, String parentObject) {
		javaWriter.println("\t\tif (object.getType() == " + type.getCosObjectType() + ") {");
		String entryName = Constants.CURRENT_ENTRY.equals(entry.getName()) ? "keyName" : "\"" + entry.getName() + "\"";
		if (links.size() == 1) {
			String link = links.iterator().next();
			if (link.contains(PredicatesParser.PREDICATE_PREFIX)) {
				linkPredicate(object, entry, type, version, link, Type.ARRAY);
				link = PredicatesParser.getPredicateLastArgument(link);
			}
			javaWriter.println("\t\t\tList<" + Object.getModelType(link) + "> list = new ArrayList<>(1);");
			javaWriter.println("\t\t\tlist.add(" + constructorGFAObject(entry.getName(), link, "(" +
					type.getParserClassName() + ")object.getDirectBase()", parentObject, entryName) + ");");
		} else {
			Set<String> correctLinks = entry.getLinksWithoutPredicatesSet(type);
			if (LinkHelper.getMap(correctLinks) != null) {
				javaWriter.println("\t\t\t" + Constants.BASE_MODEL_OBJECT_PATH + " result = " +
						getMethodCall(getGetterName(linkName + type.getType() + version.getStringWithUnderScore()),
								"object.getDirectBase()", entryName) + ";");
				javaWriter.println("\t\t\tList<" + Constants.BASE_MODEL_OBJECT_PATH + "> list = new ArrayList<>(1);");
				javaWriter.println("\t\t\tif (result != null) {");
				javaWriter.println("\t\t\t\tlist.add(result);");
				javaWriter.println("\t\t\t}");
			} else {
				javaWriter.println("\t\t\tList<" + Constants.BASE_MODEL_OBJECT_PATH + "> list = Collections.emptyList();");
				LOGGER.log(Level.WARNING, Main.getString(version, object, entry, type) +
						" Several dictionaries/streams " + String.join(",", entry.getLinks(type)));
			}
		}
		javaWriter.println("\t\t\treturn Collections.unmodifiableList(list);");
		javaWriter.println("\t\t}");
	}

	public void addMultiLink(Object object, Entry entry, String returnType, PDFVersion version) {
		String linkName = Links.getLinkName(entry.getName());
		String returnObjectType = Constants.OBJECT.equals(returnType) ? Constants.BASE_MODEL_OBJECT_PATH : returnType;
		printMethodSignature(false, "private", false, "List<" + returnObjectType + ">",
				getGetterName(linkName + version.getStringWithUnderScore()));
		javaWriter.println("\t\tList<" + returnObjectType + "> list = new LinkedList<>();");
		String keyName = getObjectForMultiLinkMethod(object);
		for (Type type : entry.getUniqLinkTypes()) {
			if (Type.ENTRY == type) {
				continue;
			}
			Set<String> links = entry.getLinks(type).stream().filter(s -> !s.contains(PredicatesParser.PREDICATE_PREFIX))
					.collect(Collectors.toSet());
			if (!entry.getLinks(type).stream().filter(s -> s.contains(PredicatesParser.PREDICATE_PREFIX))
					.collect(Collectors.toSet()).isEmpty()) {
				System.out.println(Main.getString(version, object, entry) + " links with predicates");
			}
			if (links.isEmpty()) {
				continue;
			}
			addObjectToListMultiLink(version, object, entry, type, links, linkName, keyName);
		}
		if (entry.getUniqLinkTypes().contains(Type.ENTRY)) {
			javaWriter.println("\t\t\tlist.add(" + constructorGFAObject(entry.getName(), entry.getLinks(Type.ENTRY).get(0),
					"object != null ? object.get() : null", "this.baseObject", keyName) + ");");
		}
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn Collections.unmodifiableList(list);");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	private String getObjectForMultiLinkMethod(Object object) {
		if (object.isArray()) {
			javaWriter.println("\t\tfor (int i = " + (object.getEntries().size() - 1) + "; i < baseObject.size(); i++) {");
			javaWriter.println("\t\t\tCOSObject object = baseObject.at(i);");
			return "String.valueOf(i)";
		}
		if (object.isNameTree()) {
			javaWriter.println("\t\tfor (COSObject object : PDNameTreeNode.create(new COSObject(baseObject))) {");
			return "null";
		}
		if (object.isNumberTree()) {
			javaWriter.println("\t\tfor (COSObject object : new PDNumberTreeNode(new COSObject(baseObject))) {");
			return "null";
		}
		javaWriter.println("\t\tfor (ASAtom key : baseObject.getKeySet()) {");
		if (object.getEntries().size() > 1) {
			StringBuilder condition = new StringBuilder();
			for (Entry currentEntry : object.getEntries()) {
				if (!currentEntry.isStar()) {
					condition.append("\"").append(currentEntry.getName()).append("\".equals(key.getValue()) || ");
				}
			}
			condition.delete(condition.length() - 4, condition.length());
			javaWriter.println("\t\t\tif (" + condition + ") {");
			javaWriter.println("\t\t\t\tcontinue;");
			javaWriter.println("\t\t\t}");
		}
		javaWriter.println("\t\t\tCOSObject object = this.baseObject.getKey(key);");
		return "key.getValue()";
	}

	private void addObjectToListMultiLink(PDFVersion version, Object object, Entry entry, Type type, Set<String> links,
										  String linkName, String keyName) {
		javaWriter.println("\t\t\tif (object.getType() == " + type.getCosObjectType() + ") {");
		if (links.size() == 1) {
			String link = links.iterator().next();
			javaWriter.println("\t\t\t\tlist.add(" + constructorGFAObject(entry.getName(), link,
					"(" + type.getParserClassName() + ")object.getDirectBase()",
					"this.parentObject", keyName) + ");");
		} else {
			if (LinkHelper.getMap(links) != null) {
				javaWriter.println("\t\t\t\t" + Constants.BASE_MODEL_OBJECT_PATH + " result = " +
						getMethodCall(getGetterName(linkName + type.getType() +
								version.getStringWithUnderScore()), "object.getDirectBase()", keyName) + ";");
				javaWriter.println("\t\t\t\tif (result != null) {");
				javaWriter.println("\t\t\t\t\tlist.add(result);");
				javaWriter.println("\t\t\t\t}");
			} else {
				javaWriter.println("\t\t\t\t//todo");
				LOGGER.log(Level.WARNING, Main.getString(version, object, entry, type) +
						" Several dictionaries/streams/arrays " + String.join(",", entry.getLinks(type)));
			}
		}
		javaWriter.println("\t\t\t\tcontinue;");
		javaWriter.println("\t\t\t}");
	}

	public void addGetInheritable(String objectId, String entryName) {
		if (Entry.isInheritable(objectId, entryName) && !Constants.STRUCTURE_ATTRIBUTE_DICTIONARY.equals(objectId)) {
			javaWriter.println("\t\tif (object == null || object.empty()) {");
			javaWriter.println("\t\t\tobject = " + getMethodCall(getGetterName(Constants.INHERITABLE_VALUE),
					getASAtomFromString(entryName)) + ";");
			javaWriter.println("\t\t}");
		}
	}

	public void addGetInheritable() {
		printMethodSignature(false, "public", false, "COSObject",
				getGetterName(Constants.INHERITABLE_VALUE),"ASAtom key");
		javaWriter.println("\t\tCOSObject keyObject = null;");
		javaWriter.println("\t\tCOSObject currentObject = this.baseObject.getKey(" + getASAtomFromString(Constants.PARENT_KEY) + ");");
		javaWriter.println("\t\twhile ((keyObject == null || keyObject.empty()) && (currentObject != null && !currentObject.empty())) {");
		javaWriter.println("\t\t\tkeyObject = currentObject.getKey(key);");
		javaWriter.println("\t\t\tcurrentObject = currentObject.getKey(" + getASAtomFromString(Constants.PARENT_KEY) + ");");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn keyObject;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addArraySortAscendingMethod() {
		printMethodSignature(false, "public", true, Type.BOOLEAN.getJavaType(),
				getGetterName(Entry.getArraySortAscendingPropertyName("", "")),
				"COSObject object", "int number");
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.ARRAY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tLong previousNumber = null;");
		javaWriter.println("\t\tfor (int i = 0; i < object.size(); i += number) {");
		javaWriter.println("\t\t\tCOSObject elem = object.at(i);");
		javaWriter.println("\t\t\tif (elem == null || elem.getType() != " + Type.INTEGER.getCosObjectType() + ") {");
		javaWriter.println("\t\t\t\treturn false;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tif (previousNumber != null && previousNumber > elem.getInteger()) {");
		javaWriter.println("\t\t\t\treturn false;");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t\tpreviousNumber = elem.getInteger();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn true;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addArraySortAscendingMethod(Entry entry, int number) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(entry.getArraySortAscendingPropertyName(number)));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn " +
				getMethodCall(getGetterName(Entry.getArraySortAscendingPropertyName("", "")),
						objectName, String.valueOf(number)) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	private void linkPredicate(Object object, Entry entry, Type type, PDFVersion version, String link, Type returnType) {
		String newLink = link;
		if (newLink == null || !newLink.contains(PredicatesParser.PREDICATE_PREFIX)) {
			return;
		}
		newLink = new PredicatesParser(object, entry, version, type, Constants.LINKS_COLUMN,
				false).parse("(" + newLink + ") == false");
		if (newLink == null) {
			return;
		}
		newLink = PredicatesParser.removeBrackets(newLink);
		if (Type.ARRAY.equals(returnType)) {
			javaWriter.println("\t\t\tif (" + newLink + ") {");
			javaWriter.println("\t\t\t\treturn Collections.emptyList();");
			javaWriter.println("\t\t\t}");
		} else {
			javaWriter.println("\t\t\t\tif (" + newLink + ") {");
			javaWriter.println("\t\t\t\t\treturn null;");
			javaWriter.println("\t\t\t\t}");
		}
	}

	public void addSubArrayLink(Object object, Entry entry, String returnType, PDFVersion version) {
		String linkName = entry.getCorrectEntryName();
		printMethodSignature(false, "private", false, "List<" + returnType + ">",
				getGetterName(linkName + version.getStringWithUnderScore()));
		javaWriter.println("\t\tList<" + returnType + "> list = new LinkedList<>();");
		List<Integer> numbers = new LinkedList<>();
		int requiredNumbers = 0;
		for (Entry numberEntry : object.getNumberStarEntries()) {
			if (numberEntry.isRequired()) {
				requiredNumbers++;
			}
			numbers.add(numberEntry.getNumberWithStar());
		}
		Collections.sort(numbers);
		javaWriter.println("\t\tCOSObject array = COSArray.construct();");
		javaWriter.println("\t\tfor (int i = " + numbers.get(0) + "; i < baseObject.size(); i++) {");
		javaWriter.println("\t\t\tCOSObject child = baseObject.at(i);");
		for (int i = requiredNumbers; i < numbers.size(); i++) {
			Entry number = object.getEntry(numbers.get(i) + "*");
			javaWriter.println("\t\t\tif (array.size() == " + i + " && child.getType() != " +
					number.getTypes().iterator().next().getCosObjectType() + ") {");
			javaWriter.println("\t\t\t\tlist.add(" + constructorGFAObject(entry.getName(),
					entry.getLinks(Type.SUB_ARRAY).iterator().next(), "array.getDirectBase()",
					"this.parentObject", "null") + ");");
			javaWriter.println("\t\t\t\tarray = COSArray.construct();");
			javaWriter.println("\t\t\t}");
		}
		javaWriter.println("\t\t\tarray.add(child);");
		javaWriter.println("\t\t\tif (array.size() == " + numbers.size() + ") {");
		javaWriter.println("\t\t\t\tlist.add(" + constructorGFAObject(entry.getName(),
				entry.getLinks(Type.SUB_ARRAY).iterator().next(), "array.getDirectBase()",
				"this.parentObject", "null") + ");");
		javaWriter.println("\t\t\t\tarray = COSArray.construct();");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (array.size() > 0) {");
		javaWriter.println("\t\t\tlist.add(" + constructorGFAObject(entry.getName(),
				entry.getLinks(Type.SUB_ARRAY).iterator().next(), "array.getDirectBase()",
				"this.parentObject", "null") + ");");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn Collections.unmodifiableList(list);");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addContainsMethod(Object object, String entryName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Entry.getContainsPropertyName(entryName)));
		int index = entryName.lastIndexOf("::");
		String objectName = index != -1 ? getComplexObject(entryName.substring(0, index)) : "this.baseObject";
		String finalEntryName = index != -1 ? entryName.substring(index + 2) : entryName;
		if (finalEntryName.endsWith(Constants.TREE_NODE)) {
			finalEntryName = finalEntryName.substring(0, finalEntryName.length() - 8);
		}
		if (Constants.FILE_TRAILER.equals(object.getId()) && Constants.XREF_STREAM.equals(entryName)) {
			javaWriter.println("\t\treturn " +
					getMethodCall(getGetterName(Entry.getContainsPropertyName(Constants.XREF_STM))) + ";");
		} else if (Constants.DOCUMENT.equals(object.getId()) && (Constants.OBJECT_STREAMS.equals(entryName) ||
				Constants.LINEARIZATION_PARAMETER_DICTIONARY.equals(entryName) || 
				Constants.XREF_STREAM.equals(entryName) || Constants.FILE_TRAILER.equals(entryName))) {
				javaWriter.println("\t\treturn " +
						getMethodCall(getGetterName(Entry.getValuePropertyName(entryName))) + " != null;");
		} else if (Constants.STAR.equals(finalEntryName)) {
			javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getKeySet() != null && !" + objectName +
					".getKeySet().isEmpty();");
		} else if (Entry.isNumber(finalEntryName)) {
			javaWriter.println("\t\treturn " + objectName + ".size() > " + finalEntryName + ";");
		} else {
			if (index == -1) {
				addContainsInheritableValueMethod(object.getId(), entryName);
			}
			javaWriter.println("\t\treturn " + objectName + ".knownKey(" + getASAtomFromString(finalEntryName) + ");");
		}
		javaWriter.println("\t}");
		javaWriter.println();
	}

	private void addContainsInheritableValueMethod(String objectId, String entryName) {
		if (Entry.isInheritable(objectId, entryName) && !Constants.STRUCTURE_ATTRIBUTE_DICTIONARY.equals(objectId)) {
			javaWriter.println("\t\tif (" + getMethodCall(Constants.IS_CONTAINS_INHERITABLE_VALUE, getASAtomFromString(entryName)) + ") {");
			javaWriter.println("\t\t\treturn true;");
			javaWriter.println("\t\t}");
		}
	}

	private void addContainsInheritableValueMethod() {
		printMethodSignature(false, "public", false, Type.BOOLEAN.getJavaType(),
				Constants.IS_CONTAINS_INHERITABLE_VALUE, "ASAtom key");
		javaWriter.println("\t\tCOSObject currentObject = new COSObject(this.baseObject);");
		javaWriter.println("\t\twhile (currentObject != null && !currentObject.empty() && !currentObject.knownKey(key)) {");
		javaWriter.println("\t\t\tcurrentObject = currentObject.getKey(" + getASAtomFromString(Constants.PARENT_KEY) + ");");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn currentObject != null && !currentObject.empty() && currentObject.knownKey(key);");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addIndirectMethod() {
		printMethodSignature(false, "public", true, Type.BOOLEAN.getJavaType(),
				getGetterName(Entry.getIndirectPropertyName("")), "COSObject object");
		javaWriter.println("\t\treturn object != null && object.get() != null && object.get().isIndirect();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addIndirectMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(entry.getIndirectPropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getIndirectPropertyName("")),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addEntryIsIndexInNameTreeMethod(Object object, Entry entry, String nameTreeEntryName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(entry.getEntryIsIndexInNameTreePropertyName(nameTreeEntryName)));
		String entryName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (" + entryName + " == null || " + entryName + ".getType() != " + Type.STRING.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		String nameTreeFinalEntry = getComplexObject(nameTreeEntryName);
		javaWriter.println("\t\tif (" + nameTreeFinalEntry + " == null || " + nameTreeFinalEntry + ".getType() != " +
				Type.DICTIONARY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tPDNameTreeNode nameTreeNode = PDNameTreeNode.create(" + nameTreeFinalEntry + ");");
		javaWriter.println("\t\treturn nameTreeNode.containsKey(" + entryName + ".getString());");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addEntryIsValueInNameTreeMethod(Object object, Entry entry, String nameTreeEntryName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(entry.getEntryIsValueInNameTreePropertyName(nameTreeEntryName)));
		String entryName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (" + entryName + " == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		String nameTreeFinalEntry = getComplexObject(nameTreeEntryName);
		javaWriter.println("\t\tif (" + nameTreeFinalEntry + " == null || " + nameTreeFinalEntry + ".getType() != " +
				Type.DICTIONARY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tPDNameTreeNode nameTreeNode = PDNameTreeNode.create(" + nameTreeFinalEntry + ");");
		javaWriter.println("\t\treturn nameTreeNode.containsValue(object);");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addgetLinkedObjectsMethod(SortedMap<String, String> entries) {
		printMethodSignature(true, "public", false, "List<? extends " +
						Constants.BASE_MODEL_OBJECT_PATH + ">", "getLinkedObjects", "String link");
		javaWriter.println("\t\tswitch (link) {");
		for (String entry : entries.keySet()) {
			String linkName = Links.getLinkName(entry);
			javaWriter.println("\t\t\tcase \"" + linkName + "\":");
			javaWriter.println("\t\t\t\treturn " + getGetterName(linkName) + "();");
		}
		javaWriter.println("\t\t\tdefault:");
		javaWriter.println("\t\t\t\treturn super.getLinkedObjects(link);");
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addPageContainsStructContentItemsMethod() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Constants.PAGE_CONTAINS_STRUCT_CONTENT_ITEMS));
		javaWriter.println("\t\tCOSObject contents = this.baseObject.getKey(ASAtom.CONTENTS);");
		javaWriter.println("\t\tif (contents.getType() == COSObjType.COS_STREAM || contents.getType() == COSObjType.COS_ARRAY) {");
		javaWriter.println("\t\t\ttry (ASInputStream opStream = contents.getDirectBase().getData(COSStream.FilterFlags.DECODE);");
		javaWriter.println("\t\t\t\t PDFStreamParser streamParser = new PDFStreamParser(opStream)) {");
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
		javaWriter.println("\t\t\t\t\t\t\t\tCOSObject resources = getInheritableResources(new COSObject(this.baseObject));");
		javaWriter.println("\t\t\t\t\t\t\t\tCOSObject properties = resources != null ? resources.getKey(ASAtom.PROPERTIES) : null;");
		javaWriter.println("\t\t\t\t\t\t\t\tCOSObject dict = properties != null ? properties.getKey(lastArgument.getName()) : null;");
		javaWriter.println("\t\t\t\t\t\t\t\tif (dict != null && dict.getType() == COSObjType.COS_DICT) {");
		javaWriter.println("\t\t\t\t\t\t\t\t\tlastArgument = dict.getDirectBase();");
		javaWriter.println("\t\t\t\t\t\t\t\t}");
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
		
		javaWriter.println("\tprivate COSObject getInheritableResources(COSObject object) {");
		javaWriter.println("\t\tCOSObject value = object.getKey(ASAtom.RESOURCES);");
		javaWriter.println("\t\tif (value != null && !value.empty()) {");
		javaWriter.println("\t\t\treturn value;");
		javaWriter.println("\t\t} else {");
		javaWriter.println("\t\t\treturn this.baseObject.knownKey(ASAtom.PARENT) ? getInheritableResources(this.baseObject.getKey(ASAtom.PARENT)) : null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addImageIsStructContentItemBaseMethod() {
		printMethodSignature(false, "public", true, Type.BOOLEAN.getJavaType(),
				getGetterName(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM), "COSObject object");
		javaWriter.println("\t\treturn object.isIndirect() && GFAObject.getKeysSet().contains(object.getKey());");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addImageIsStructContentItemMethod() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM));
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM),
				"new COSObject(this.baseObject)") + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addArrayLengthMethod() {
		printMethodSignature(false, "public", true, Type.INTEGER.getJavaType(),
				getGetterName(Entry.getArrayLengthPropertyName("")), "COSObject object");
		String objectName = "object";
		javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType() == " +
				Type.ARRAY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn (long) " + objectName + ".size();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addArrayLengthMethod(Object object, String entryName) {
		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getGetterName(Entry.getArrayLengthPropertyName(entryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getArrayLengthPropertyName("")), objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addStringLengthMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getGetterName(entry.getStringLengthPropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType() == " + 
				Type.STRING.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn (long) " + objectName + ".getString().length();");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addStreamLengthMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.INTEGER.getJavaType(),
				getGetterName(entry.getStreamLengthPropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\tif (" + objectName + " != null && " + objectName + ".getType() == " + 
				Type.STREAM.getCosObjectType() + ") {");
		javaWriter.println("\t\t\tCOSObject length = " + objectName + ".getKey(ASAtom.LENGTH);");
		javaWriter.println("\t\t\tif (length != null && length.getType() == " + Type.INTEGER.getCosObjectType() + ") {");
		javaWriter.println("\t\t\t\treturn length.getInteger();");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addRectWidthMethod() {
		printMethodSignature(false, "public", true, Type.NUMBER.getJavaType(),
				getGetterName(Entry.getRectWidthPropertyName("")), "COSObject object");
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.ARRAY.getCosObjectType() +
				" || object.size() != 4) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject left = object.at(0);");
		javaWriter.println("\t\tCOSObject right = object.at(2);");
		javaWriter.println("\t\tif (left == null || (left.getType() != " + Type.INTEGER.getCosObjectType() +
				" && left.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (right == null || (right.getType() != " + Type.INTEGER.getCosObjectType() +
				" && right.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn right.getReal() - left.getReal();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addRectWidthMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.NUMBER.getJavaType(),
				getGetterName(entry.getRectWidthPropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getRectWidthPropertyName("")),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addIsPDFTagged() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Constants.IS_PDF_TAGGED));
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
	}

	public void addGetObjectType() {
		printMethodSignature(false, "public", false, Type.STRING.getJavaType(),
				getGetterName(Constants.OBJECT_TYPE), "COSObject object");
		for (Type type : Type.values()) {
			if (type == Type.BITMASK || type == Type.DATE || type == Type.MATRIX || type == Type.ENTRY || 
					type == Type.RECTANGLE || type == Type.NAME_TREE || type == Type.NUMBER_TREE || 
					type == Type.STRING_BYTE || type == Type.STRING_ASCII || type == Type.STRING_TEXT ||
					type == Type.SUB_ARRAY) {
				continue;
			}
			javaWriter.println("\t\tif (object.getType() == " + type.getCosObjectType() + ") {");
			javaWriter.println("\t\t\treturn \"" + type.getType() + "\";");
			javaWriter.println("\t\t}");
		}
		javaWriter.println("\t\treturn null;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addNotStandard14FontMethod() {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Constants.NOT_STANDARD_14_FONT));
		javaWriter.println("\t\tCOSObject type = baseObject.getKey(ASAtom.TYPE);");
		javaWriter.println("\t\tif (type == null || type.getType() != " + Type.NAME.getCosObjectType() +
				" || type.getName() != ASAtom.FONT) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject subtype = baseObject.getKey(ASAtom.SUBTYPE);");
		javaWriter.println("\t\tif (subtype == null || subtype.getType() != " + Type.NAME.getCosObjectType() +
				" || subtype.getName() != ASAtom.TYPE1) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject baseFont = baseObject.getKey(ASAtom.BASE_FONT);");
		javaWriter.println("\t\tif (baseFont == null || baseFont.getType() != " + Type.NAME.getCosObjectType() + ") {");
		javaWriter.println("\t\t\treturn true;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn !standardFonts.contains(baseFont.getString());");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addRectHeightMethod() {
		printMethodSignature(false, "public", true, Type.NUMBER.getJavaType(),
				getGetterName(Entry.getRectHeightPropertyName("")), "COSObject object");
		javaWriter.println("\t\tif (object == null || object.getType() != " + Type.ARRAY.getCosObjectType() +
				" || object.size() != 4) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tCOSObject bottom = object.at(1);");
		javaWriter.println("\t\tCOSObject top = object.at(3);");
		javaWriter.println("\t\tif (bottom == null || (bottom.getType() != " + Type.INTEGER.getCosObjectType() +
				" && bottom.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (top == null || (top.getType() != " + Type.INTEGER.getCosObjectType() +
				" && top.getType() != " + Type.NUMBER.getCosObjectType() + ")) {");
		javaWriter.println("\t\t\treturn null;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn top.getReal() - bottom.getReal();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addRectHeightMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.NUMBER.getJavaType(),
				getGetterName(entry.getRectHeightPropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getRectHeightPropertyName("")),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHexStringMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(entry.getIsHexStringPropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType() == " + 
				Type.STRING.getCosObjectType() + " && ((COSString)" + objectName + ".getDirectBase()).isHexadecimal();");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addFieldNameMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Entry.getIsFieldNamePropertyName(entry.getName())));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn " + objectName + " != null && " + objectName + ".getType() == " + 
				Type.STRING.getCosObjectType() + " && !" + objectName + ".getString().contains(\".\");");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHasCycleMethod(Entry entry) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(entry.getHasCyclePropertyName()));
		String objectName = getObjectByEntryName(entry.getName());
		javaWriter.println("\t\treturn GFAObject." + getMethodCall(Constants.HAS_CYCLE, objectName, 
				getASAtomFromString(entry.getName())) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addHasExtensionMethod(String extensionName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Object.getHasExtensionPropertyName(extensionName)));
		javaWriter.println("\t\treturn StaticContainers.getEnabledExtensions().contains(ExtensionObjectType." + extensionName + ");");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addEntriesStringMethod() {
		printMethodSignature(false, "public", true, Type.STRING.getJavaType(),
				getGetterName(Entry.getEntriesStringPropertyName("")), "COSObject object");
		String objectName = "object";
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

	public void addEntriesStringMethod(Object object, String entryName) {//works only for name entries
		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getGetterName(Entry.getEntriesStringPropertyName(entryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getEntriesStringPropertyName("")),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addKeysStringMethod() {
		printMethodSignature(false, "public", true, Type.STRING.getJavaType(),
				getGetterName(Entry.getKeysStringPropertyName("")), "COSObject object");
		String objectName = "object";
		javaWriter.println("\t\tSet<ASAtom> set = " + objectName + ".getKeySet();");
		javaWriter.println("\t\treturn set == null ? \"\" : set.stream()");
		javaWriter.println("\t\t\t\t.map(ASAtom::getValue)");
		javaWriter.println("\t\t\t\t.collect(Collectors.joining(\"&\"));");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addKeysStringMethod(Object object, String entryName) {
		printMethodSignature(true, "public", false, Type.STRING.getJavaType(),
				getGetterName(Entry.getKeysStringPropertyName(entryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		javaWriter.println("\t\treturn " + getMethodCall(getGetterName(Entry.getKeysStringPropertyName("")),
				objectName) + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addIsInArrayMethod(String entryName, String arrayEntryName) {
		printMethodSignature(true, "public", false, Type.BOOLEAN.getJavaType(),
				getGetterName(Object.getIsInArrayPropertyName(entryName, arrayEntryName)));
		String objectName = entryName.contains("::") ? getComplexObject(entryName) : getObjectByEntryName(entryName);
		String arrayObjectName = arrayEntryName.contains("::") ? getComplexObject(arrayEntryName) : getObjectByEntryName(arrayEntryName);
		javaWriter.println("\t\tif (" + objectName + ".getKey() == null) {");
		javaWriter.println("\t\t\treturn false;");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\tif (" + arrayObjectName + " != null && " + arrayObjectName + ".getType() == " + Type.ARRAY.getCosObjectType() + ") {");
		javaWriter.println("\t\t\tfor (COSObject object : (COSArray)" + arrayObjectName + ".getDirectBase()) {");
		javaWriter.println("\t\t\t\tif (Objects.equals(object.getKey(), " + objectName + ".getKey())) {");
		javaWriter.println("\t\t\t\t\treturn true;");
		javaWriter.println("\t\t\t\t}");
		javaWriter.println("\t\t\t}");
		javaWriter.println("\t\t}");
		javaWriter.println("\t\treturn false;");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	private String getComplexObject(String entryName) {
		if (Constants.PARENT.equals(entryName)) {
			return "this.parentObject";
		}
		String correctEntryName = Entry.getCorrectEntryName(entryName).replace(Constants.STAR, "Any");
		javaWriter.println("\t\tCOSObject " + correctEntryName + " = " + 
				getMethodCall(getGetterName(Entry.getValuePropertyName(entryName))) + ";");
		return correctEntryName;
	}
	
	private String getComplexObject(Object object, String entryName) {
		List<String> entriesNames = new ArrayList<>(Arrays.asList(entryName.split("::")));
		Pair<Integer, String> pair = calculateInitialObjectName(object, entriesNames);
		int i = pair.getKey();
		String currentObjectName = pair.getValue();
		for (; i < entriesNames.size(); i++) {
			String newEntryName = entriesNames.get(i);
			if (newEntryName.startsWith("@")) {
				newEntryName = newEntryName.substring(1);
			}
			if (newEntryName.matches(Constants.NUMBER_REGEX)) {
				javaWriter.println("\t\tif (" + currentObjectName + " == null || " + currentObjectName +
						".getType() != " + Type.ARRAY.getCosObjectType() + " || " + 
						currentObjectName + ".size() <= " + newEntryName + ") {");
			} else {
				javaWriter.println("\t\tif (" + currentObjectName + " == null || !" + currentObjectName +
						".getType().isDictionaryBased()) {");
			}
			javaWriter.println("\t\t\treturn null;");
			javaWriter.println("\t\t}");
			String newCorrectEntryName = Entry.getCorrectEntryName(newEntryName);
			if (Constants.CURRENT_ENTRY.equals(newEntryName)) {
				newCorrectEntryName = "baseObject";
				javaWriter.println("\t\tCOSObject " + newCorrectEntryName + " = new COSObject(" + currentObjectName + ");");
			} else if (newEntryName.matches(Constants.NUMBER_REGEX)) {
				javaWriter.println("\t\tCOSObject " + newCorrectEntryName + " = " + currentObjectName + ".at(" + newEntryName + ");");
			} else {
				javaWriter.println("\t\tCOSObject " + newCorrectEntryName + " = " + currentObjectName + ".getKey(" +
						getASAtomFromString(newEntryName) + ");");
			}
			currentObjectName = newCorrectEntryName;
		}
		return currentObjectName;
	}

	private Pair<Integer, String> calculateInitialObjectName(Object object, List<String> entriesNames) {
		int i = 0;
		String currentObjectName = "this.baseObject";
		if (Constants.TRAILER.equals(entriesNames.get(0))) {
			javaWriter.println("\t\tCOSObject trailer = StaticResources.getDocument().getDocument().getTrailer().getObject();");
			currentObjectName = Constants.TRAILER;
			i = 1;
			if (entriesNames.size() > 1 && Constants.CATALOG.equals(entriesNames.get(1))) {
				entriesNames.set(1, Constants.ROOT);
			}
		} else if (Constants.PAGE.equals(entriesNames.get(0))) {
			String objectName = getObjectByEntryName(entriesNames.get(1));
			javaWriter.println("\t\tCOSObject page = " + getMethodCall(getGetterName(Constants.PAGE_OBJECT), objectName) + ";");
			currentObjectName = Constants.PAGE;
			i = 2;
		} else if (Constants.PARENT.equals(entriesNames.get(0))) {
			if (entriesNames.size() > 1 && Constants.PARENT.equals(entriesNames.get(1))) {
				currentObjectName = "this.parentParentObject";
				i = 2;
			} else {
				currentObjectName = object.isEntry() ? "this.parentParentObject" : "this.parentObject";
				i = 1;
			}
		} else if (Constants.STAR.equals(entriesNames.get(0))) {
			entriesNames.set(0, Constants.CURRENT_ENTRY);
		}
		return new Pair<>(i, currentObjectName);
	}

	public String getObjectByEntryName(String entryName) {
		String correctEntryName = Entry.getCorrectEntryName(entryName);
		if (correctEntryName.isEmpty()) {
			correctEntryName = "entry";
		}
		javaWriter.println("\t\tCOSObject " + correctEntryName + " = " + 
				getMethodCall(getGetterName(Entry.getValuePropertyName(entryName))) + ";");
		return correctEntryName;
	}

	public void getEntryCOSObject(Object multiObject, String entryName) {
		boolean addDefault = multiObject.getJavaGeneration().getDefaultObject(multiObject, entryName);
		printMethodSignature(false, "public", false, "COSObject",
				getGetterName(Entry.getValuePropertyName(entryName)));
		if (entryName.endsWith(Constants.TREE_NODE)) {
			entryName = entryName.substring(0, entryName.length() - 8);
		}
		if (Constants.FILE_TRAILER.equals(multiObject.getId()) && Constants.XREF_STREAM.equals(entryName)) {
			javaWriter.println("\t\tLong offset = " +
					getMethodCall(getGetterName(Entry.getTypeValuePropertyName(Constants.XREF_STM, Type.INTEGER))) + ";");
			javaWriter.println("\t\tCOSObject object = offset != null ? " +
					"StaticResources.getDocument().getDocument().getObject(offset) : null;");
		} else if (Constants.DOCUMENT.equals(multiObject.getId()) && Constants.LINEARIZATION_PARAMETER_DICTIONARY.equals(entryName)) {
				javaWriter.println("\t\tCOSObject object = StaticResources.getDocument().getDocument().getLinearizationDictionary();");
		} else if (Constants.DOCUMENT.equals(multiObject.getId()) && Constants.OBJECT_STREAMS.equals(entryName)) {
			javaWriter.println("\t\tList<COSObject> objectStreamsList = StaticResources.getDocument().getDocument().getObjectStreamsList();");
			javaWriter.println("\t\tCOSObject object = objectStreamsList.isEmpty() ? null : new COSObject(new COSArray(objectStreamsList));");
		} else if (Constants.DOCUMENT.equals(multiObject.getId()) && Constants.XREF_STREAM.equals(entryName)) {
			javaWriter.println("\t\tCOSObject object = StaticResources.getDocument().getDocument().getLastXRefStream();");
		} else if (Constants.DOCUMENT.equals(multiObject.getId()) && Constants.FILE_TRAILER.equals(entryName)) {
			javaWriter.println("\t\tif (StaticResources.getDocument().getDocument().getLastXRefStream() != null) {");
			javaWriter.println("\t\t\treturn null;");
			javaWriter.println("\t\t}");
			javaWriter.println("\t\tCOSObject object = StaticResources.getDocument().getDocument().getTrailer().getObject();");
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
			javaWriter.println("\t\t\t" + objectName + " = " +
					getMethodCall(getGetterName(Entry.getDefaultValuePropertyName(entryName))) + ";");
			javaWriter.println("\t\t}");
		}
		javaWriter.println("\t\treturn " + objectName + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}
	
	public void getComplexCOSObject(MultiObject object, String entry) {
		if (Constants.PARENT.equals(entry)) {
			return;
		}
		if (object.getEntriesNames().contains(entry)) {
			return;
		}
		printMethodSignature(false, "public", false, "COSObject", getGetterName(Entry.getValuePropertyName(entry)));
		String objectName = getComplexObject(object, entry);
		javaWriter.println("\t\treturn " + objectName + ";");
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public void addCommonGetLink(String entryName, String returnType, List<List<PDFVersion>> versions) {
		String linkName = Links.getLinkName(entryName);
		String returnObjectType = Constants.OBJECT.equals(returnType) ? Constants.BASE_MODEL_OBJECT_PATH : returnType;
		printMethodSignature(false, "private", false, "List<" + returnObjectType + ">",
				getGetterName(linkName));
		if (versions.size() == 1 && versions.get(0).size() == PDFVersion.values().length) {
			javaWriter.println("\t\treturn " + getMethodCall(getGetterName(linkName +
					versions.get(0).get(0).getStringWithUnderScore())) + ";");
		} else {
			javaWriter.println("\t\tswitch (StaticContainers.getFlavour()) {");
			for (List<PDFVersion> versionsList : versions) {
				for (PDFVersion version : versionsList) {
					javaWriter.println("\t\t\tcase ARLINGTON" + version.getStringWithUnderScore() + ":");
				}
				String versionString = versionsList.get(0).getStringWithUnderScore();
				javaWriter.println("\t\t\t\treturn " + getMethodCall(getGetterName(linkName + versionString)) + ";");
			}
			javaWriter.println("\t\t\tdefault:");
			javaWriter.println("\t\t\t\treturn Collections.emptyList();");
			javaWriter.println("\t\t}");
		}
		javaWriter.println("\t}");
		javaWriter.println();
	}

	public static String getGetterName(String propertyName) {
		return "get" + propertyName;
	}

	public static String getMethodCall(String methodName, String ... arguments) {
		StringBuilder str = new StringBuilder(methodName);
		str.append("(");
		if (arguments.length != 0) {
			for (int i = 0; i < arguments.length - 1; i++) {
				str.append(arguments[i]).append(", ");
			}
			str.append(arguments[arguments.length - 1]);
		}
		str.append(")");
		return str.toString();
	}

	public void printMethodSignature(boolean isOverride, String accessModifier, boolean isStatic,
									 String returnType, String methodName, String ... arguments) {
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
				javaWriter.print(arguments[i] + ", ");
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
			string.append(equals ? "" : "!").append("Objects.equals(elem, ").append(value).append(")").append(" ")
					.append(equals ? "||" : "&&").append(" ");
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

	public static String constructorGFAObject(String entryName, String arlingtonObjectName, String objectName,
											  String parentObject, String keyName) {
		List<String> arguments = new LinkedList<>();
		arguments.add(objectName);
		arguments.add(parentObject);
		if (Constants.FONT_FILE_2.equals(arlingtonObjectName)) {
			arguments.add("this.parentObject");
		} else if (Constants.STAR.equals(entryName)) {
			arguments.add("this.parentObject");
			arguments.add("keyName");
		}
		arguments.add(keyName);
		return constructor(Object.getJavaClassName(arlingtonObjectName), arguments);
	}
}
