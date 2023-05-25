package org.verapdf.arlington;

import java.util.logging.Logger;

public class Rules {

	static void addRules(MultiObject multiObject) {
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(multiObject.getId());
			if (object == null) {
				continue;
			}
			if (object.isArray()) {
				addRuleAboutArraySize(version, object);
			}
			if (!object.isEntry() && !object.isArray()) {
				containsExtraEntries(version, object);
				containsFutureEntries(version, object);
			}
			for (Entry entry : object.getEntries()) {
				checkLinks(version, object, entry);
				if (entry.isStar() || entry.isNumberWithStar()) {
					continue;
				}
				hasWrongType(version, object, entry);
				typesPredicates(version, object, entry);
				requiredEntry(version, object, entry);
				if (!object.isArray()) {
					deprecatedEntry(version, object, entry);
				}
				for (Type type : entry.getUniqActiveTypes()) {
					indirectAndDirect(version, object, entry, type);
					possibleValuesOfEntry(version, object, entry, type);
					checkSpecialCase(version, object, entry, type);
				}
			}
		}
	}

	public static String getClause(Object object) {
		return getClause(object, null);
	}

	public static String getClause(Object object, Entry entry) {
		return getClause(object, entry, null);
	}

	public static String getClause(Object object, Entry entry, Type type) {
		return getClause(object, entry, type, null);
	}

	public static String getClause(Object object, Entry entry, Type type, String value) {
		StringBuilder clause = new StringBuilder(object.getId());
		if (entry != null) {
			if (!Constants.CURRENT_ENTRY.equals(entry.getName())) {
				clause.append("-").append(entry.getName());
			}
			if (type != null) {
				clause.append("-").append(type.getType());
				if (value != null) {
					clause.append("-").append(PredicatesParser.removeQuotes(value));
				}
			}
		}
		return clause.toString();
	}

}
