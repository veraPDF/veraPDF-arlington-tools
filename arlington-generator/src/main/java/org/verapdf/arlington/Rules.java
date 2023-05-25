package org.verapdf.arlington;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Rules {

	private static final Logger LOGGER = Logger.getLogger(Rules.class.getCanonicalName());

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

	private static void hasWrongType(PDFVersion version, Object object, Entry entry) {
		SortedSet<Type> currentTypes = new TreeSet<>(entry.getUniqActiveTypes());
		if (currentTypes.isEmpty() || currentTypes.size() < entry.getTypes().size()) {
			return;
		}
		StringBuilder test = new StringBuilder();
		if (object.isArray()) {
			test.append(Constants.SIZE + " <= ").append(entry.getName()).append(" || ");
		} else if (!object.isEntry()) {
			test.append(entry.getContainsPropertyName()).append(" == false || ");
			entry.setContainsProperty(true);
		}
		for (Type type : currentTypes) {
			test.append(entry.getHasTypePropertyName(type)).append(" == true || ");
			entry.addHasTypeProperty(type);
		}
		test.delete(test.length() - 4, test.length());
		String typesString = currentTypes.stream().map(Type::getType).collect(Collectors.joining(", "));
		ProfileGeneration.writeRule(version, 8, object.getModelType(), getClause(object, entry, null), test.toString(),
				ProfileGeneration.getErrorMessageStart(true, object, entry) + " shall have " +
						(currentTypes.size() != 1 ? "one of types:" : "type") + " " + typesString,
				ProfileGeneration.getErrorMessageStart(false, object, entry) + " is not " +
						(currentTypes.size() != 1 ? "one of types:" : "of type") + " " + typesString, Constants.KEY_NAME);
	}

	private static void requiredValue(Object object, Entry entry, PDFVersion version, Type type, String propertyValue) {
		String test = new PredicatesParser(object, entry, version, type, "RequiredValue").parse(propertyValue);
		if (test == null) {
			return;
		}
		if (PredicatesParser.containsBrackets(test)) {
			test = test.substring(1, test.length() - 1);
		}
		String neededValue = PredicatesParser.getPredicateLastArgument(propertyValue);
		ProfileGeneration.writeRule(version, 15, object.getModelType(), getClause(object, entry, type, neededValue), test,
				ProfileGeneration.getErrorMessageStart(true, object, entry) + " shall have " +
						type.getType() + " value " + neededValue + ", if this object satisfies condition " +
						PredicatesParser.getPredicateFirstArgument(propertyValue),
				ProfileGeneration.getErrorMessageStart(false, object, entry) +
						" does not have value " + neededValue, Constants.KEY_NAME);
	}

	private static boolean isDeprecatedValue(Object object, Entry entry, PDFVersion version, Type type, String propertyValue) {
		String info = new PredicatesParser(object, entry, version, type, "PossibleValues").parse(propertyValue);
		if (info == null || !info.startsWith(PredicatesParser.DEPRECATED_PREDICATE.replace(":", "/")) ||
				Objects.equals(PredicatesParser.DEPRECATED_PREDICATE.replace(":", "/") + "false", info)) {
			return false;
		}
		return true;
	}

	private static void possibleValuePredicate(Object object, Entry entry, PDFVersion version, Type type, String value) {
		StringBuilder test = new StringBuilder();
		String propertyHasType = entry.getHasTypePropertyName(type);
		test.append(propertyHasType).append(" != " + Constants.TRUE + " || ");
		entry.addHasTypeProperty(type);
		String predicate = new PredicatesParser(object, entry, version, type, "PossibleValues").parse(value);
		if (predicate == null) {
			return;
		}
		test.append(predicate);
		ProfileGeneration.writeRule(version, 14, object.getModelType(), getClause(object, entry, type), test.toString(),
				ProfileGeneration.getErrorMessageStart(true, object, entry, type) +
						" shall satisfy possible value predicate: " + value,
				ProfileGeneration.getErrorMessageStart(false, object, entry, type) +
						" does not satisfy possible value predicate: " + value, Constants.KEY_NAME);
	}

	private static void typesPredicates(PDFVersion version, Object object, Entry entry) {
		for (int i = 0; i < entry.getTypes().size(); i++) {
			String typeString = entry.getTypesPredicates().get(i);
			if (!typeString.contains(PredicatesParser.PREDICATE_PREFIX)) {
				continue;
			}
			Type type = entry.getTypes().get(i);
			if (typeString.contains(PredicatesParser.DEPRECATED_PREDICATE)) {
				deprecatedType(version, object, entry, type, typeString);
			} else {
				LOGGER.log(Level.INFO, Main.getString(version, object, entry, type) +
						" column Type contains non " + PredicatesParser.DEPRECATED_PREDICATE + " predicate");
			}
		}
	}

	private static void deprecatedType(PDFVersion version, Object object, Entry entry, Type type, String typeString) {
		String stringVersion = PredicatesParser.getPredicateFirstArgument(typeString);
		PDFVersion predicateVersion = PDFVersion.getPDFVersion(stringVersion);
		if (predicateVersion == null || PDFVersion.compare(version, predicateVersion) < 0) {
			return;
		}
		entry.addHasTypeProperty(type);
		ProfileGeneration.writeRule(version, 21, object.getModelType(), getClause(object, entry, type),
				entry.getHasTypePropertyName(type) + " != true",
				ProfileGeneration.getErrorMessageStart(true, object, entry) +
						"should not have deprecated type " + type.getType(),
				ProfileGeneration.getErrorMessageStart(false, object, entry, type) +
						" is present", Constants.KEY_NAME);
	}

	private static void requiredEntry(PDFVersion version, Object object, Entry entry) {
		String propertyName = Entry.getContainsPropertyName(entry.getName());
		if (entry.isRequired() && !Constants.CURRENT_ENTRY.equals(entry.getName()) && !object.isArray()) {
			ProfileGeneration.writeRule(version, 7, object.getModelType(), getClause(object, entry),
					propertyName + " == true",
					ProfileGeneration.getErrorMessageStart(true, object, entry) + " is required",
					ProfileGeneration.getErrorMessageStart(false, object, entry) + " is missing",
					Constants.KEY_NAME);
			entry.setContainsProperty(true);
		} else if (!Constants.CURRENT_ENTRY.equals(entry.getName()) && entry.getRequired().contains(PredicatesParser.PREDICATE_PREFIX)) {
			String test = new PredicatesParser(object, entry, version, null, "Required").parse(entry.getRequired());
			if (test == null || Constants.TRUE.equals(test)) {
				return;
			}
			if (PredicatesParser.containsBrackets(test)) {
				test = test.substring(1, test.length() - 1);
			}
			ProfileGeneration.writeRule(version, 11, object.getModelType(), getClause(object, entry), test,
					ProfileGeneration.getErrorMessageStart(true, object, entry) +
							" is required, when " + entry.getRequired(),
					ProfileGeneration.getErrorMessageStart(false, object, entry) +
							" is missing", Constants.KEY_NAME);
		}
	}

	private static void deprecatedEntry(PDFVersion version, Object object, Entry entry) {
		String propertyName = Entry.getContainsPropertyName(entry.getName());
		if (entry.getDeprecatedVersion() != null && PDFVersion.compare(entry.getDeprecatedVersion(), version) < 0) {
			String test = Constants.CURRENT_ENTRY.equals(entry.getName()) ? "false" : propertyName + " == false";
			ProfileGeneration.writeRule(version, 5, object.getModelType(), getClause(object, entry), test,
					ProfileGeneration.getErrorMessageStart(true, object, entry) +
							" is deprecated since PDF " + entry.getDeprecatedVersion().getString(),
					ProfileGeneration.getErrorMessageStart(false, object, entry) +
							" is present", Constants.KEY_NAME);
			entry.setContainsProperty(true);
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
