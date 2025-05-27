package org.verapdf.arlington;

import javafx.util.Pair;

import java.util.Map;

public class Properties {

	static void addProperties(MultiObject multiObject) {
		for (MultiEntry multiEntry : multiObject.getEntries()) {
			if (multiEntry.getContainsProperty()) {
				addContains(multiObject, multiEntry.getName());
			}
			if (!multiEntry.isStar() && !multiEntry.isNumberWithStar() && !Constants.SUB_ARRAYS.equals(multiEntry.getName())) {
				multiObject.getJavaGeneration().getEntryCOSObject(multiObject, multiEntry.getName());
			}
			if (multiEntry.getIndirectProperty()) {
				addIndirect(multiObject, multiEntry);
			}
			if (!multiEntry.getHasTypeProperties().isEmpty()) {
				addEntryType(multiObject, multiEntry.getName());
			}
			for (Type type : multiEntry.getHasTypeProperties()) {
				addHasType(multiObject, multiEntry.getName(), type);
			}
			for (Type type : multiEntry.getTypeValueProperties()) {
				addgetValue(multiObject, multiEntry.getName(), type);
			}
			for (Integer number : multiEntry.getArraySortAscendingProperties()) {
				addArraySortAscending(multiObject, multiEntry, number);
			}
			if (multiEntry.getArraySizeProperty()) {
				addArraySize(multiObject, multiEntry.getName());
			}
			if (multiEntry.getStringSizeProperty()) {
				addStringSize(multiObject, multiEntry);
			}
			if (multiEntry.getStreamSizeProperty()) {
				addStreamSize(multiObject, multiEntry);
			}
			if (multiEntry.getRectHeightProperty()) {
				addRectHeight(multiObject, multiEntry);
			}
			if (multiEntry.getRectWidthProperty()) {
				addRectWidth(multiObject, multiEntry);
			}
			if (multiEntry.getFieldNameProperty()) {
				addFieldName(multiObject, multiEntry);
			}
			if (multiEntry.getHexStringProperty()) {
				addHexString(multiObject, multiEntry);
			}
			if (multiEntry.getEntriesStringProperty()) {
				addEntriesString(multiObject, multiEntry.getName());
			}
			if (multiEntry.getHasCycleProperty()) {
				addHasCycle(multiObject, multiEntry);
			}
		}
		for (String entry : multiObject.getComplexObjectProperties()) {
			multiObject.getJavaGeneration().getComplexCOSObject(multiObject, entry);
		}
		if (multiObject.getPageContainsStructContentItemsProperty()) {
			addPageContainsStructContentItems(multiObject);
		}
//		if (Constants.PAGE_OBJECT.equals(multiObject.getObjectName())) {
//			multiObject.getJavaGeneration().addProcessAFKeysMethod();
//		}
//		if ("EmbeddedFileParameter".equals(multiObject.getObjectName())) {
//			multiObject.getJavaGeneration().addIsAssociatedFile();
//		}
		if (multiObject.getImageIsStructContentItemProperty()) {
			addImageIsStructContentItem(multiObject);
		}
		for (String entryName : multiObject.getArraySizeProperties()) {
			addArraySize(multiObject, entryName);
		}
		for (Pair<String, String> entryNames : multiObject.getIsInArrayProperties()) {
			addIsInArray(multiObject, entryNames);
		}
		for (Pair<String, String> entryNames : multiObject.getIsNameTreeIndexProperties()) {
			addIsNameTreeIndex(multiObject, entryNames);
		}
		for (Pair<String, String> entryNames : multiObject.getIsNameTreeValueProperties()) {
			addIsNameTreeValue(multiObject, entryNames);
		}
		for (Pair<String, String> entryNames : multiObject.getIsNumberTreeIndexProperties()) {
			addIsNumberTreeIndex(multiObject, entryNames);
		}
		for (Pair<String, String> entryNames : multiObject.getIsNumberTreeValueProperties()) {
			addIsNumberTreeValue(multiObject, entryNames);
		}
		for (String entryName : multiObject.getKeysStringProperties()) {
			addKeysString(multiObject, entryName);
		}
		for (Map.Entry<String, Type> entry : multiObject.getEntriesValuesProperties().entrySet()) {
			addgetValue(multiObject, entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, Type> entry : multiObject.getEntriesHasTypeProperties().entrySet()) {
			addHasType(multiObject, entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, String> entry : multiObject.getFindNMValueInArrayProperties().entrySet()) {
			addFindNMValueInArray(multiObject, entry.getKey(), entry.getValue());
		}
		for (String entryName : multiObject.getContainsEntriesProperties()) {
			addContains(multiObject, entryName);
		}
		for (String entryName : multiObject.getEntriesStringProperties()) {
			addEntriesString(multiObject, entryName);
		}
	}

	private static void addPageContainsStructContentItems(MultiObject multiObject) {
		ModelGeneration.addProperty(Constants.PAGE_CONTAINS_STRUCT_CONTENT_ITEMS, Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addPageContainsStructContentItemsMethod();
	}

	private static void addImageIsStructContentItem(MultiObject multiObject) {
		ModelGeneration.addProperty(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM, Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addImageIsStructContentItemMethod();
	}

	private static void addArraySize(MultiObject multiObject, String entryName) {
		ModelGeneration.addProperty(Entry.getArrayLengthPropertyName(entryName), Type.INTEGER.getModelType());
		multiObject.getJavaGeneration().addArrayLengthMethod(multiObject, entryName);
	}

	private static void addStringSize(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getStringLengthPropertyName(), Type.INTEGER.getModelType());
		multiObject.getJavaGeneration().addStringLengthMethod(entry);
	}

	private static void addStreamSize(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getStreamLengthPropertyName(), Type.INTEGER.getModelType());
		multiObject.getJavaGeneration().addStreamLengthMethod(entry);
	}

	private static void addRectHeight(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getRectHeightPropertyName(), Type.NUMBER.getModelType());
		multiObject.getJavaGeneration().addRectHeightMethod(entry);
	}

	private static void addRectWidth(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getRectWidthPropertyName(), Type.NUMBER.getModelType());
		multiObject.getJavaGeneration().addRectWidthMethod(entry);
	}

	private static void addFieldName(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(Entry.getIsFieldNamePropertyName(entry.getName()), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addFieldNameMethod(entry);
	}

	private static void addHexString(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getIsHexStringPropertyName(), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addHexStringMethod(entry);
	}

	private static void addHasCycle(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getHasCyclePropertyName(), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addHasCycleMethod(entry);
	}

	private static void addEntriesString(MultiObject multiObject, String entryName) {
		ModelGeneration.addProperty(Entry.getEntriesStringPropertyName(entryName), Type.STRING.getModelType());
		multiObject.getJavaGeneration().addEntriesStringMethod(multiObject, entryName);
	}

	private static void addKeysString(MultiObject multiObject, String entryName) {
		ModelGeneration.addProperty(Entry.getKeysStringPropertyName(entryName), Type.STRING.getModelType());
		multiObject.getJavaGeneration().addKeysStringMethod(multiObject, entryName);
	}

	private static void addIsInArray(MultiObject multiObject, Pair<String, String> entryNames) {
		ModelGeneration.addProperty(Object.getIsInArrayPropertyName(entryNames.getKey(), entryNames.getValue()), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addIsInArrayMethod(entryNames.getKey(), entryNames.getValue());
	}

	private static void addIsNameTreeIndex(MultiObject multiObject, Pair<String, String> entryNames) {
		ModelGeneration.addProperty(Object.getIsNameTreeIndexPropertyName(entryNames.getKey(), entryNames.getValue()), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addIsNameTreeIndexMethod(entryNames.getKey(), entryNames.getValue());
	}

	private static void addIsNameTreeValue(MultiObject multiObject, Pair<String, String> entryNames) {
		ModelGeneration.addProperty(Object.getIsNameTreeValuePropertyName(entryNames.getKey(), entryNames.getValue()), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addIsNameTreeValueMethod(entryNames.getKey(), entryNames.getValue());
	}

	private static void addIsNumberTreeIndex(MultiObject multiObject, Pair<String, String> entryNames) {
		ModelGeneration.addProperty(Object.getIsNumberTreeIndexPropertyName(entryNames.getKey(), entryNames.getValue()), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addIsNumberTreeIndexMethod(entryNames.getKey(), entryNames.getValue());
	}

	private static void addIsNumberTreeValue(MultiObject multiObject, Pair<String, String> entryNames) {
		ModelGeneration.addProperty(Object.getIsNumberTreeValuePropertyName(entryNames.getKey(), entryNames.getValue()), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addIsNumberTreeValueMethod(entryNames.getKey(), entryNames.getValue());
	}

	private static void addHasType(MultiObject multiObject, String entryName, Type type) {
		ModelGeneration.addProperty(Entry.getHasTypePropertyName(entryName, type), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addHasTypeMethod(multiObject, entryName, type);
	}

	private static void addFindNMValueInArray(MultiObject multiObject, String name1, String name2) {
		ModelGeneration.addProperty(Entry.getFindNMValueInArrayPropertyName(name1, name2), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addFindNMValueInArrayMethod(multiObject, name1, name2);
	}

	private static void addEntryType(MultiObject multiObject, String entryName) {
		ModelGeneration.addProperty(Entry.getEntryTypePropertyName(entryName), Type.STRING.getModelType());
		multiObject.getJavaGeneration().addEntryTypeMethod(entryName);
	}
	
	private static void addContains(MultiObject multiObject, String entryName) {
		ModelGeneration.addProperty(Entry.getContainsPropertyName(entryName), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addContainsMethod(multiObject, entryName);
	}

	private static void addIndirect(MultiObject multiObject, Entry entry) {
		ModelGeneration.addProperty(entry.getIndirectPropertyName(), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addIndirectMethod(entry);
	}

	private static void addgetValue(MultiObject multiObject, String entryName, Type type) {
		ModelGeneration.addProperty(Entry.getTypeValuePropertyName(entryName, type), type.getModelType());
		multiObject.getJavaGeneration().addGetValueMethod(multiObject, entryName, type);
	}

	private static void addArraySortAscending(MultiObject multiObject, Entry entry, int number) {
		ModelGeneration.addProperty(entry.getArraySortAscendingPropertyName(number), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addArraySortAscendingMethod(entry, number);
	}
}
