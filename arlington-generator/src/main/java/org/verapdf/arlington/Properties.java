package org.verapdf.arlington;

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
			for (String entryName : multiEntry.getInNameTreeProperties()) {
				addNameTreeContainsString(multiObject, multiEntry, entryName);
			}
		}
		if (multiObject.getPageContainsStructContentItemsProperty()) {
			addPageContainsStructContentItems(multiObject);
		}
		if (multiObject.getImageIsStructContentItemProperty()) {
			addImageIsStructContentItem(multiObject);
		}
		for (String entryName : multiObject.getArraySizeProperties()) {
			addArraySize(multiObject, entryName);
		}
		for (String entryName : multiObject.getKeysStringProperties()) {
			addKeysString(multiObject, entryName);
		}
		for (Map.Entry<String,Type> entry : multiObject.getEntriesValuesProperties().entrySet()) {
			addgetValue(multiObject, entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String,Type> entry : multiObject.getEntriesHasTypeProperties().entrySet()) {
			addHasType(multiObject, entry.getKey(), entry.getValue());
		}
		for (String entryName : multiObject.getContainsEntriesProperties()) {
			addContains(multiObject, entryName);
		}
		for (String entryName : multiObject.getEntriesStringProperties()) {
			addEntriesString(multiObject, entryName);
		}
		for (String extensionName : multiObject.getExtensionProperties()) {
			addHasExtension(multiObject, extensionName);
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
		multiObject.getJavaGeneration().addArrayLengthMethod(entryName);
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
		multiObject.getJavaGeneration().addEntriesStringMethod(entryName);
	}

	private static void addHasExtension(MultiObject multiObject, String extensionName) {
		ModelGeneration.addProperty(Object.getHasExtensionPropertyName(extensionName), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addHasExtensionMethod(multiObject, extensionName);
	}

	private static void addKeysString(MultiObject multiObject, String entryName) {
		ModelGeneration.addProperty(Entry.getKeysStringPropertyName(entryName), Type.STRING.getModelType());
		multiObject.getJavaGeneration().addKeysStringMethod(entryName);
	}

	private static void addHasType(MultiObject multiObject, String entryName, Type type) {
		ModelGeneration.addProperty(Entry.getHasTypePropertyName(entryName, type), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addHasTypeMethod(multiObject, entryName, type);
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

	private static void addNameTreeContainsString(MultiObject multiObject, Entry entry, String entryName) {
		ModelGeneration.addProperty(entry.getNameTreeContainsStringPropertyName(entryName), Type.BOOLEAN.getModelType());
		multiObject.getJavaGeneration().addNameTreeContainsStringMethod(entry, entryName);
	}
}
