package org.verapdf.arlington;

import org.verapdf.arlington.json.JSONEntry;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Rules {

	private static final Logger LOGGER = Logger.getLogger(Rules.class.getCanonicalName());

	//descriptions and error messages
	private static final String SPECIAL_CASE_DESCRIPTION = "%s shall satisfy special case: %s";
	private static final String SPECIAL_CASE_ERROR_MESSAGE = "%s does not satisfy special case: %s";

	private static final String REQUIRED_CONDITION_DESCRIPTION = "%s is required, when %s";
	private static final String REQUIRED_DESCRIPTION = "%s is required";
	private static final String REQUIRED_ERROR_MESSAGE = "%s is missing";

	private static final String DEPRECATED_ENTRY_DESCRIPTION = "%s is deprecated since PDF %s";
	private static final String DEPRECATED_ENTRY_ERROR_MESSAGE = "%s is present";

	private static final String SINCE_DESCRIPTION = "%s can only be present, if satisfy predicate %s";
	private static final String SINCE_ERROR_MESSAGE = "%s is present";

	private static final String DEPRECATED_TYPE_DESCRIPTION = "%s should not have deprecated type %s";
	private static final String DEPRECATED_TYPE_ERROR_MESSAGE = "%s is present";

	private static final String POSSIBLE_VALUE_CONDITION_DESCRIPTION = "%s shall satisfy possible value predicate: %s";
	private static final String POSSIBLE_VALUE_ERROR_CONDITION_MESSAGE = "%s does not satisfy possible value predicate: %s";

	private static final String DIRECT_CONDITION_DESCRIPTION = "If %s satisfies condition %s, it shall be direct";
	private static final String DIRECT_DESCRIPTION = "%s shall be direct";
	private static final String DIRECT_ERROR_MESSAGE = "%s is indirect";

	private static final String INDIRECT_CONDITION_DESCRIPTION = "If %s satisfies condition %s, it shall be indirect";
	private static final String INDIRECT_DESCRIPTION = "%s shall be indirect";
	private static final String INDIRECT_ERROR_MESSAGE = "%s is direct";

	private static final String EXTRA_ENTRIES_DESCRIPTION = "%s shall not contain entries except %s";
	private static final String EXTRA_ENTRIES_ERROR_MESSAGE = "%s contains entry(ies) %s";

	private static final String FUTURE_ENTRIES_DESCRIPTION = "%s shall not contain entries %s in PDF %s. These entries appear in later pdf versions";
	private static final String FUTURE_ENTRIES_ERROR_MESSAGE = "%s contains entry(ies) %s";

	private static final String REQUIRED_VALUE_DESCRIPTION = "%s shall have %s value %s, if this object satisfies condition %s";
	private static final String REQUIRED_VALUE_ERROR_MESSAGE = "%s does not have value %s";

	private static final String VALUE_ONLY_WHEN_DESCRIPTION = "%s may have %s value %s, only if this object satisfies condition %s";
	private static final String VALUE_ONLY_WHEN_ERROR_MESSAGE = "%s has value %s, but not satisfy condition %s";

	private static final String POSSIBLE_VALUE_DESCRIPTION = "%s shall have value %s";
	private static final String POSSIBLE_VALUES_DESCRIPTION = "%s shall have one of values: %s";
	private static final String POSSIBLE_VALUE_ERROR_MESSAGE = "%s has incorrect value %s instead of %s";
	private static final String ARRAY_POSSIBLE_VALUE_ERROR_MESSAGE = "%s has incorrect value instead of %s";

	private static final String DEPRECATED_VALUE_DESCRIPTION = "%s should not have deprecated value %s";
	private static final String DEPRECATED_VALUES_DESCRIPTION = "%s should not have one of deprecated values: %s";
	private static final String DEPRECATED_VALUE_ERROR_MESSAGE = "%s has deprecated value %s";

	private static final String ARRAY_SIZE_DESCRIPTION = "%s shall contain %d * n + %d elements";
	private static final String ARRAY_SIZE_ERROR_MESSAGE = "%s contains %s element(s)";
	private static final String CERTAIN_ARRAY_SIZE_DESCRIPTION = "%s shall contain exactly %d elements";
	private static final String CERTAIN_ARRAY_SIZE_ONE_ELEMENT_DESCRIPTION = "%s shall contain exactly %d element";
	private static final String CERTAIN_ARRAY_SIZE_ERROR_MESSAGE = "%s contains %s element(s) instead of %s";
	private static final String INTERVAL_ARRAY_SIZE_DESCRIPTION = "%s shall contain %d to %d elements";
	private static final String MINIMUM_ARRAY_SIZE_DESCRIPTION = "%s shall contain at least %d elements";
	private static final String MINIMUM_ARRAY_SIZE_ONE_ELEMENT_DESCRIPTION = "%s shall contain at least %d element";
	private static final String MULTIPLY_ARRAY_SIZE_DESCRIPTION = "%s shall contain %d * n elements";

	private static final String POSSIBLE_TYPE_DESCRIPTION = "%s shall have type %s";
	private static final String POSSIBLE_TYPE_ERROR_MESSAGE = "%s is not of type %s";
	private static final String POSSIBLE_TYPES_DESCRIPTION = "%s shall have one of types: %s";
	private static final String POSSIBLE_TYPES_ERROR_MESSAGE = "%s is not one of types: %s";

	private static final String LINK_DESCRIPTION = "%s shall be object %s";
	private static final String LINK_ERROR_MESSAGE = "%s is not object %s";
	private static final String LINKS_DESCRIPTION = "%s shall be one of objects %s";
	private static final String LINKS_ERROR_MESSAGE = "%s is not one of objects %s";

	static void addRules(MultiObject multiObject) {
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(multiObject.getId());
			if (object == null || !Main.getActiveObjectNames().get(version).contains(multiObject.getId())) {
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
				checkSince(version, object, entry);
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

	private static void checkLinks(PDFVersion version, Object object, Entry entry) {
		if (entry.isNumberWithStar()) {
			return;
		}
		SortedSet<Type> linkTypes = entry.getUniqLinkTypes();
		if (linkTypes.isEmpty()) {
			return;
		}
		if (entry.isStar()) {
			if (!object.isNumberTree() && !object.isNameTree()) {
				return;
			}
			List<String> links = new LinkedList<>();
			for (List<String> currentLinks : entry.getLinks().values()) {
				links.addAll(currentLinks);
			}
			String objectsString = String.join(", ", links);
			StringBuilder test = new StringBuilder();
			test.append("Entries_size == ");
			if (object.isArray() || object.isNumberTree() || object.isNameTree() || (object.getEntries().size() == 1 &&
					object.getEntries().iterator().next().isStar())) {
				test.append("size");
				if (object.getEntries().size() > 1) {
					test.append(" - ").append(object.getEntries().size() - 1);
				}
			} else {
				List<String> values = new LinkedList<>();
				for (Entry currentEntry : object.getEntries()) {
					if (!currentEntry.isStar()) {
						values.add("'" + currentEntry.getName() + "'");
					}
				}
				test.append("(keysString == '' ? 0 : ").append(ProfileGeneration.split("keysString",
						false, values)).append(")");
			}
			ProfileGeneration.writeRule(version, 18, object.getModelType(), getClause(object, entry, null),
					test.toString(),
					String.format(links.size() == 1 ? LINK_DESCRIPTION : LINKS_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry), objectsString),
					String.format(links.size() == 1 ? LINK_ERROR_MESSAGE : LINKS_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry), objectsString),
					Constants.KEY_NAME);
		} else {
			for (Type type : linkTypes) {
				List<String> links = entry.getLinks(type);
				if (links.isEmpty()) {
					continue;
				}
				if (type == Type.SUB_ARRAY) {
					continue;
				}
				String objectsString = String.join(", ", links);
				entry.addHasTypeProperty(type);
				String linkName = Links.getLinkName(entry.getName());
				ProfileGeneration.writeRule(version, 17, object.getModelType(), getClause(object, entry, type),
						entry.getHasTypePropertyName(type) + " != true || " + linkName + "_size == 1",
						String.format(links.size() == 1 ? LINK_DESCRIPTION : LINKS_DESCRIPTION,
								ProfileGeneration.getErrorMessageStart(true, object, entry, type), objectsString),
						String.format(links.size() == 1 ? LINK_ERROR_MESSAGE : LINKS_ERROR_MESSAGE,
								ProfileGeneration.getErrorMessageStart(false, object, entry, type), objectsString),
						Constants.KEY_NAME);
			}
		}
	}

	private static void checkSpecialCase(PDFVersion version, Object object, Entry entry, Type type) {
		String specialCase = entry.getSpecialCase(type);
		if (specialCase == null || specialCase.isEmpty()) {
			return;
		}
		String newSpecialCase = new PredicatesParser(object, entry, version, type, Constants.SPECIAL_CASE_COLUMN).parse(specialCase);
		if (newSpecialCase == null || Constants.TRUE.equals(newSpecialCase)) {
			return;
		}
		if (entry.getDefaultValue() != null) {
			System.out.println(Main.getString(version, object, entry, type) + " default + specialCase");
		}
		StringBuilder test = new StringBuilder();
		if (Constants.STRUCTURE_ATTRIBUTE_DICTIONARY.equals(object.getId())) {
			test.append(entry.getContainsPropertyName()).append(" != true || ");
			entry.setContainsProperty(true);
		}
		test.append(entry.getHasTypePropertyName(type)).append(" != true || ");
		entry.addHasTypeProperty(type);
		test.append(PredicatesParser.addBrackets(newSpecialCase));
		ProfileGeneration.writeRule(version, 9, object.getModelType(), getClause(object, entry, type), test.toString(),
				String.format(SPECIAL_CASE_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry, type), specialCase),
				String.format(SPECIAL_CASE_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry, type), specialCase),
				Constants.KEY_NAME);
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
				String.format(currentTypes.size() != 1 ? POSSIBLE_TYPES_DESCRIPTION : POSSIBLE_TYPE_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry), typesString),
				String.format(currentTypes.size() != 1 ? POSSIBLE_TYPES_ERROR_MESSAGE : POSSIBLE_TYPE_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry), typesString),
				Constants.KEY_NAME);
	}

	private static void containsExtraEntries(PDFVersion version, Object object) {
		if (Constants.STREAM.equals(object.getId())) {
			return;
		}
		if (object.getEntries().stream().noneMatch(Entry::isStar)) {
			StringBuilder test = new StringBuilder();
			StringBuilder errorArgument = new StringBuilder();
			StringBuilder keysString = new StringBuilder();
			errorArgument.append(Constants.KEYS_STRING + ".split('&').filter(elem => ");
			List<String> entries = new LinkedList<>();
			Set<String> entryNamesSet = object.getEntriesNames();
			if (Constants.FILE_TRAILER.equals(object.getId())) {
				entryNamesSet.remove(Constants.XREF_STREAM);
			}
			for (String entryName : object.getMultiObject().getEntriesNames()) {
				if (Constants.FILE_TRAILER.equals(object.getId()) && Constants.XREF_STREAM.equals(entryName)) {
					continue;
				}
				if (entryNamesSet.contains(entryName)) {
					keysString.append(entryName).append(", ");
				}
				entries.add("'" + entryName + "'");
				errorArgument.append("elem != '").append(entryName).append("' && ");
			}
			if (!keysString.toString().isEmpty()) {
				test.append(Constants.KEYS_STRING + " == '' || ");
				test.append(ProfileGeneration.split(Constants.KEYS_STRING, false, entries));
				test.delete(test.length() - 8, test.length());
				errorArgument.delete(errorArgument.length() - 4, errorArgument.length());
				String checkSecondAndThirdClassNames = " && /" + Constants.SECOND_OR_THIRD_CLASS_NAME_REGEX + "/.test(elem) == false";
				test.append(checkSecondAndThirdClassNames);
				errorArgument.append(checkSecondAndThirdClassNames);
				test.append(").length");
				test.append(" == 0");
				keysString.deleteCharAt(keysString.length() - 1);
				keysString.deleteCharAt(keysString.length() - 1);
				errorArgument.append(").toString()");
				ProfileGeneration.writeRule(version, 1, object.getModelType(), getClause(object), test.toString(),
						String.format(EXTRA_ENTRIES_DESCRIPTION, object.getId(), keysString),
						String.format(EXTRA_ENTRIES_ERROR_MESSAGE, object.getId(), "%1"),
						errorArgument.toString());
			}
		}
	}

	private static void containsFutureEntries(PDFVersion version, Object object) {
		if (Constants.STREAM.equals(object.getId())) {
			return;
		}
		if (object.getEntries().stream().noneMatch(Entry::isStar)) {
			StringBuilder test = new StringBuilder();
			StringBuilder errorArgument = new StringBuilder();
			StringBuilder keysString = new StringBuilder();
			errorArgument.append(Constants.KEYS_STRING + ".split('&').filter(elem => ");
			List<String> entries = new LinkedList<>();
			Set<String> entryNamesSet = object.getEntriesNames();
			for (String entryName : object.getMultiObject().getEntriesNames()) {
				if (Constants.FILE_TRAILER.equals(object.getId()) && Constants.XREF_STREAM.equals(entryName)) {
					continue;
				}
				if (!entryNamesSet.contains(entryName)) {
					keysString.append(entryName).append(", ");
					entries.add("'" + entryName + "'");
					errorArgument.append("elem == '").append(entryName).append("' || ");
				}
			}
			if (!keysString.toString().isEmpty()) {
				test.append(Constants.KEYS_STRING + " == '' || ");
				test.append(ProfileGeneration.split(Constants.KEYS_STRING, true, entries)).append(" == 0");
				keysString.deleteCharAt(keysString.length() - 1);
				keysString.deleteCharAt(keysString.length() - 1);
				errorArgument.delete(errorArgument.length() - 4, errorArgument.length());
				errorArgument.append(").toString()");
				ProfileGeneration.writeRule(version, 22, object.getModelType(), getClause(object), test.toString(),
						String.format(FUTURE_ENTRIES_DESCRIPTION, object.getId(), keysString, version.getString()),
						String.format(FUTURE_ENTRIES_ERROR_MESSAGE, object.getId(), "%1"),
						errorArgument.toString());
			}
		}
	}

	private static void addRuleAboutArraySize(PDFVersion version, Object object) {
		Set<Integer> numbersWithoutStar = new TreeSet<>();
		Set<Integer> numbersWithStar = new TreeSet<>();
		boolean containsStar = false;
		for (Entry entry : object.getEntries()) {
			String name = entry.getName();
			if (entry.isNumber()) {
				numbersWithoutStar.add(entry.getNumber());
			} else if (entry.isStar()) {
				containsStar = true;
			} else if (entry.isNumberWithStar()) {
				numbersWithStar.add(entry.getNumberWithStar());
			} else if (!Constants.SUB_ARRAYS.equals(name)) {
				LOGGER.log(Level.SEVERE, Main.getString(version, object, entry) + " contains wrong entry");
			}
		}
		int numberOfRequiredElements = 0;
		for (Integer number : numbersWithoutStar) {
			if (object.getEntry(number).isRequired()) {
				numberOfRequiredElements++;
			} else {
				break;
			}
		}
		int numberOfRequiredElementsWithStar = 0;
		for (Integer number : numbersWithStar) {
			if (object.getEntry(number + Constants.STAR).isRequired()) {
				numberOfRequiredElementsWithStar++;
			} else {
				break;
			}
		}
		if (numbersWithStar.isEmpty() && !containsStar && numberOfRequiredElements == numbersWithoutStar.size()) {
			ProfileGeneration.writeRule(version, 2, object.getModelType(), getClause(object),
					Constants.SIZE + " == " + numberOfRequiredElements,
					String.format(numberOfRequiredElements != 1 ?
									CERTAIN_ARRAY_SIZE_DESCRIPTION : CERTAIN_ARRAY_SIZE_ONE_ELEMENT_DESCRIPTION,
							object.getId(), numberOfRequiredElements),
					String.format(CERTAIN_ARRAY_SIZE_ERROR_MESSAGE, object.getId(), "%1", numberOfRequiredElements),
					Constants.SIZE);
		} else if (numberOfRequiredElementsWithStar > 0 && numberOfRequiredElementsWithStar == numbersWithStar.size() &&
				!containsStar) {
			ProfileGeneration.writeRule(version, 3, object.getModelType(), getClause(object),
					Constants.SIZE + " > 0 && " + Constants.SIZE + " % " + numberOfRequiredElementsWithStar + " == " +
							numberOfRequiredElements,
					String.format(numberOfRequiredElements > 0 ? ARRAY_SIZE_DESCRIPTION : MULTIPLY_ARRAY_SIZE_DESCRIPTION,
							object.getId(), numberOfRequiredElementsWithStar, numberOfRequiredElements),
					String.format(ARRAY_SIZE_ERROR_MESSAGE, object.getId(), "%1"),
					Constants.SIZE);
		} else if (!containsStar && numbersWithStar.isEmpty() && numberOfRequiredElements < numbersWithoutStar.size()) {
			ProfileGeneration.writeRule(version, 24, object.getModelType(), getClause(object),
					Constants.SIZE + " >= " + numberOfRequiredElements + " && " + Constants.SIZE + " <= " +
							numbersWithoutStar.size(),
					String.format(INTERVAL_ARRAY_SIZE_DESCRIPTION,
							object.getId(), numberOfRequiredElements, numbersWithoutStar.size()),
					String.format(ARRAY_SIZE_ERROR_MESSAGE, object.getId(), "%1"),
					Constants.SIZE);
		} else if ((!numbersWithStar.isEmpty() || containsStar || numberOfRequiredElements < numbersWithoutStar.size()) &&
				numberOfRequiredElements > 0) {
			ProfileGeneration.writeRule(version, 4, object.getModelType(), getClause(object),
					Constants.SIZE + " >= " + numberOfRequiredElements,
					String.format(numberOfRequiredElements == 1 ?
									MINIMUM_ARRAY_SIZE_ONE_ELEMENT_DESCRIPTION : MINIMUM_ARRAY_SIZE_DESCRIPTION,
							object.getId(), numberOfRequiredElements),
					String.format(ARRAY_SIZE_ERROR_MESSAGE, object.getId(), "%1"),
					Constants.SIZE);
		}
	}

	private static void indirectAndDirect(PDFVersion version, Object object, Entry entry, Type type) {
		boolean isIndirect = entry.isIndirectReference(type);
		boolean isDirect = entry.isDirectReference(type);
		String indirectReference = entry.getIndirectReference(type);
		if (!isIndirect && !isDirect && indirectReference != null &&
				indirectReference.contains(PredicatesParser.PREDICATE_PREFIX)) {
			String predicate = indirectReference.substring(0, indirectReference.indexOf("("));
			if (PredicatesParser.MUST_BE_INDIRECT_PREDICATE.equals(predicate) ||
					PredicatesParser.MUST_BE_DIRECT_PREDICATE.equals(predicate)) {
				indirectReference = indirectReference.substring(predicate.length() + 1, indirectReference.length() - 1);
				String result = new PredicatesParser(object, entry, version, type,
						Constants.INDIRECT_REFERENCE_COLUMN).parse(indirectReference);
				if (Constants.TRUE.equals(result)) {
					if (PredicatesParser.MUST_BE_INDIRECT_PREDICATE.equals(predicate)) {
						isIndirect = true;
					} else {
						isDirect = true;
					}
				}
			}
		}
		if (isIndirect || isDirect) {
			StringBuilder test = new StringBuilder();
			String propertyHasType = entry.getHasTypePropertyName(type);
			test.append(propertyHasType).append(" != " + Constants.TRUE + " || ");
			entry.addHasTypeProperty(type);
			test.append(entry.getIndirectPropertyName()).append(" == ").append(isIndirect);
			entry.setIndirectProperty(true);
			ProfileGeneration.writeRule(version, 10, object.getModelType(), getClause(object, entry, type),
					test.toString(),
					String.format(isIndirect ? INDIRECT_DESCRIPTION : DIRECT_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry, type)),
					String.format(isIndirect ? INDIRECT_ERROR_MESSAGE : DIRECT_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry, type)),
					Constants.KEY_NAME);
		} else if (entry.getIndirectReference(type) != null &&
				entry.getIndirectReference(type).contains(PredicatesParser.PREDICATE_PREFIX)) {
			StringBuilder test = new StringBuilder();
			String propertyHasType = entry.getHasTypePropertyName(type);
			test.append(propertyHasType).append(" != " + Constants.TRUE + " || ");
			entry.addHasTypeProperty(type);
			String predicate = new PredicatesParser(object, entry, version, type,
					Constants.INDIRECT_REFERENCE_COLUMN).parse(entry.getIndirectReference(type));
			if (predicate == null || Constants.TRUE.equals(predicate)) {
				return;
			}
			test.append(predicate);
			if (entry.mustBeIndirect(type)) {
				ProfileGeneration.writeRule(version, 12, object.getModelType(), getClause(object, entry, type),
						test.toString(),
						String.format(INDIRECT_CONDITION_DESCRIPTION,
								ProfileGeneration.getErrorMessagePart(true, object, entry, type),
								indirectReference),
						String.format(INDIRECT_ERROR_MESSAGE,
								ProfileGeneration.getErrorMessageStart(false, object, entry, type)),
						Constants.KEY_NAME);
			} else if (entry.mustBeDirect(type)) {
				ProfileGeneration.writeRule(version, 16, object.getModelType(), getClause(object, entry, type),
						test.toString(),
						String.format(DIRECT_CONDITION_DESCRIPTION,
								ProfileGeneration.getErrorMessagePart(true, object, entry, type),
								indirectReference),
						String.format(DIRECT_ERROR_MESSAGE,
								ProfileGeneration.getErrorMessageStart(false, object, entry, type)),
						Constants.KEY_NAME);
			}
		}
	}

	private static void possibleValuesOfEntry(PDFVersion version, Object object, Entry entry, Type type) {
		if ((type == Type.RECTANGLE || type == Type.MATRIX || type == Type.ARRAY) &&
				entry.getPossibleValues(type) != null && !entry.getPossibleValues(type).isEmpty()) {
			possibleValuesOfArray(version, object, entry, type);
		}
		for (String value : entry.getPossibleValues(type)) {
			if (value.contains(PredicatesParser.EVAL_PREDICATE)) {
				possibleValuePredicate(object, entry, version, type, value);
			}
		}
		if (!type.isPropertyType()) {
			return;
		}
		StringBuilder test = new StringBuilder();
		String propertyName = entry.getTypeValuePropertyName(type);
		String propertyHasType = entry.getHasTypePropertyName(type);
		test.append(propertyHasType).append(" != " + Constants.TRUE + " || ");
		entry.addHasTypeProperty(type);
		Set<String> possibleValues = new HashSet<>();
		Set<String> deprecatedValues = new HashSet<>();
		calculatePossibleAndDeprecatedValues(test, version, object, entry, type, propertyName, possibleValues, deprecatedValues);
		if (!deprecatedValues.isEmpty()) {
			deprecatedValues(deprecatedValues, version, object, entry, type);
		}
		if (possibleValues.contains(Constants.STAR) || possibleValues.contains("\"" + Constants.STAR + "\"")) {
			return;
		}
		String valuesString = String.join(", ", possibleValues);
		if (!possibleValues.isEmpty()) {
			test.delete(test.length() - 4, test.length());
			ProfileGeneration.writeRule(version, 6, object.getModelType(), getClause(object, entry, type), test.toString(),
					String.format(possibleValues.size() == 1 ? POSSIBLE_VALUE_DESCRIPTION : POSSIBLE_VALUES_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry, type), valuesString),
					String.format(POSSIBLE_VALUE_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry, type), "%2", valuesString),
					Constants.KEY_NAME, propertyName);
		}
	}

	private static void calculatePossibleAndDeprecatedValues(StringBuilder test, PDFVersion version, Object object,
													  Entry entry, Type type, String propertyName,
													  Set<String> possibleValues, Set<String> deprecatedValues) {
		for (String propertyValue : entry.getPossibleValues(type)) {
			if (!propertyValue.contains(PredicatesParser.PREDICATE_PREFIX)) {
				propertyValue = PredicatesParser.removeQuotes(propertyValue);
				test.append(propertyName).append(" == ").append(type.getValueWithSeparator(propertyValue)).append(" || ");
				possibleValues.add(propertyValue);
				entry.addTypeValueProperty(type);
			} else if (!propertyValue.contains(PredicatesParser.EVAL_PREDICATE)) {
				String possibleValue = null;
				if (propertyValue.contains(PredicatesParser.REQUIRED_VALUE_PREDICATE)) {
					if (propertyValue.startsWith(PredicatesParser.REQUIRED_VALUE_PREDICATE)) {
						requiredValue(object, entry, version, type, propertyValue);
						possibleValue = PredicatesParser.getPredicateLastArgument(propertyValue);
					}
				} else if (propertyValue.contains(PredicatesParser.VALUE_ONLY_WHEN_PREDICATE)) {
					if (propertyValue.startsWith(PredicatesParser.VALUE_ONLY_WHEN_PREDICATE)) {
						valueOnlyWhen(object, entry, version, type, propertyValue);
						possibleValue = PredicatesParser.getPredicateFirstArgument(propertyValue);
					}
				} else if (propertyValue.contains(PredicatesParser.EXTENSION_PREDICATE)) {
					if (propertyValue.startsWith(PredicatesParser.EXTENSION_PREDICATE)) {
						possibleValue = type.getSeparator() + PredicatesParser.getPredicateLastArgument(propertyValue) +
								type.getSeparator();
					}
				} else if (propertyValue.contains(PredicatesParser.DEPRECATED_PREDICATE)) {
					if (propertyValue.startsWith(PredicatesParser.DEPRECATED_PREDICATE)) {
						possibleValue = type.getSeparator() + PredicatesParser.getPredicateLastArgument(propertyValue) +
								type.getSeparator();
						if (possibleValue.contains(PredicatesParser.PREDICATE_PREFIX)) {
							possibleValue = null;
						} else if (isDeprecatedValue(object, entry, version, type, propertyValue)) {
							deprecatedValues.add(PredicatesParser.removeQuotes(possibleValue));
						}
					}
				} else {
					possibleValue = new PredicatesParser(object, entry, version, type,
							Constants.POSSIBLE_VALUES_COLUMN).parse(propertyValue);
				}
				if (possibleValue == null) {
					continue;
				}
				if (Constants.TRUE.equals(possibleValue)) {
					continue;
				}
				test.append(propertyName).append(" == ").append(possibleValue).append(" || ");
				possibleValue = PredicatesParser.removeQuotes(possibleValue);
				possibleValues.add(possibleValue);
				entry.addTypeValueProperty(type);
			}
		}
	}

	private static void possibleValuesOfArray(PDFVersion version, Object object, Entry entry, Type type) {
		StringBuilder test = new StringBuilder();
		Set<String> possibleValues = new HashSet<>();
		test.append(entry.getHasTypePropertyName(type)).append(" != " + Constants.TRUE + " || ");
		entry.addHasTypeProperty(type);
		for (String value : entry.getPossibleValues(type)) {
			if (value.contains(PredicatesParser.PREDICATE_PREFIX)) {
				continue;
			}
			possibleValues.add(value);
			String[] values = JSONEntry.getArrayFromString(value);
			entry.setArraySizeProperty(true);
			test.append("(").append(entry.getArrayLengthPropertyName()).append(" == ").append(values.length);
			for (int i = 0; i < values.length; i++) {
				String elementValue = values[i];
				if (!elementValue.matches(Constants.NUMBER_REGEX)) {
					LOGGER.log(Level.WARNING, Main.getString(version, object, entry, type) +
							" possible array value contains non-integer element(s)");
					return;
				}
				Type currentType = Type.INTEGER;
				String currentEntryName = entry.getName() + "::" + i;
				object.getEntriesValuesProperties().put(currentEntryName, currentType);
				test.append(" && ").append(Entry.getTypeValuePropertyName(currentEntryName, currentType)).append(" == ")
						.append(elementValue);
			}
			test.append(") || ");
		}
		if (possibleValues.isEmpty()) {
			return;
		}
		test.delete(test.length() - 4, test.length());
		String valuesString = String.join(", ", possibleValues);
		ProfileGeneration.writeRule(version, 20, object.getModelType(), entry.getName(), test.toString(),
				String.format(possibleValues.size() == 1 ? POSSIBLE_VALUE_DESCRIPTION : POSSIBLE_VALUES_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry, type), valuesString),
				String.format(ARRAY_POSSIBLE_VALUE_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry, type), valuesString),
				Constants.KEY_NAME);
	}

	private static void requiredValue(Object object, Entry entry, PDFVersion version, Type type, String propertyValue,
									  String value) {
		String test = new PredicatesParser(object, entry, version, type, Constants.REQUIRED_VALUE_COLUMN).parse(propertyValue);
		if (test == null || Constants.TRUE.equals(test)) {
			return;
		}
		test = PredicatesParser.removeBrackets(test);
		ProfileGeneration.writeRule(version, 15, object.getModelType(), getClause(object, entry, type, value), test,
				String.format(REQUIRED_VALUE_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry), type.getType(),
						value, PredicatesParser.getPredicateFirstArgument(propertyValue)),
				String.format(REQUIRED_VALUE_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry), value),
				Constants.KEY_NAME);
	}

	private static void deprecatedValues(Set<String> deprecatedValues, PDFVersion version, Object object, Entry entry, Type type) {
		StringBuilder deprecatedTest = new StringBuilder();
		for (String deprecatedValue : deprecatedValues) {
			deprecatedTest.append(entry.getTypeValuePropertyName(type)).append(" != ").append(type.getValueWithSeparator(deprecatedValue)).append(" && ");
		}
		deprecatedTest.delete(deprecatedTest.length() - 4, deprecatedTest.length());
		String valuesString = String.join(", ", deprecatedValues);
		entry.addTypeValueProperty(type);
		if (deprecatedValues.size() == 1) {
			ProfileGeneration.writeRule(version, 19, object.getModelType(), getClause(object, entry, type),
					deprecatedTest.toString(),
					String.format(DEPRECATED_VALUE_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry, type), valuesString),
					String.format(DEPRECATED_VALUE_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry, type),
							deprecatedValues.iterator().next()),
					Constants.KEY_NAME);
		} else {
			ProfileGeneration.writeRule(version, 19, object.getModelType(), getClause(object, entry, type),
					deprecatedTest.toString(),
					String.format(DEPRECATED_VALUES_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry, type), valuesString),
					String.format(DEPRECATED_VALUE_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry, type), "%2"),
					Constants.KEY_NAME, entry.getTypeValuePropertyName(type));
		}
	}

	private static void valueOnlyWhen(Object object, Entry entry, PDFVersion version, Type type, String propertyValue,
									  String value) {
		String test = new PredicatesParser(object, entry, version, type, Constants.POSSIBLE_VALUES_COLUMN).parse(propertyValue);
		if (test == null) {
			return;
		}
		test = PredicatesParser.removeBrackets(test);
		String condition = PredicatesParser.getPredicateLastArgument(propertyValue, false);//todo fix
		ProfileGeneration.writeRule(version, 13, object.getModelType(), getClause(object, entry, type, value), test,
				String.format(VALUE_ONLY_WHEN_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry), type.getType(),
						value, condition),
				String.format(VALUE_ONLY_WHEN_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry), value, condition),
				Constants.KEY_NAME);
	}

	private static boolean isDeprecatedValue(Object object, Entry entry, PDFVersion version, Type type, String propertyValue) {
		String info = new PredicatesParser(object, entry, version, type, Constants.POSSIBLE_VALUES_COLUMN).parse(propertyValue);
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
		String predicate = new PredicatesParser(object, entry, version, type, Constants.POSSIBLE_VALUES_COLUMN).parse(value);
		if (predicate == null) {
			return;
		}
		test.append(predicate);
		ProfileGeneration.writeRule(version, 14, object.getModelType(), getClause(object, entry, type), test.toString(),
				String.format(POSSIBLE_VALUE_CONDITION_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry, type), value),
				String.format(POSSIBLE_VALUE_ERROR_CONDITION_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry, type), value),
				Constants.KEY_NAME);
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
				String.format(DEPRECATED_TYPE_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry), type.getType()),
				String.format(DEPRECATED_TYPE_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry, type)),
				Constants.KEY_NAME);
	}

	private static void requiredEntry(PDFVersion version, Object object, Entry entry) {
		String propertyName = Entry.getContainsPropertyName(entry.getName());
		if (entry.isRequired() && !Constants.CURRENT_ENTRY.equals(entry.getName()) && !object.isArray()) {
			ProfileGeneration.writeRule(version, 7, object.getModelType(), getClause(object, entry),
					propertyName + " == true",
					String.format(REQUIRED_DESCRIPTION, ProfileGeneration.getErrorMessageStart(true, object, entry)),
					String.format(REQUIRED_ERROR_MESSAGE, ProfileGeneration.getErrorMessageStart(false, object, entry)),
					Constants.KEY_NAME);
			entry.setContainsProperty(true);
		} else if (!Constants.CURRENT_ENTRY.equals(entry.getName()) && entry.getRequired().contains(PredicatesParser.PREDICATE_PREFIX)) {
			String test = new PredicatesParser(object, entry, version, null, Constants.REQUIRED_COLUMN).parse(entry.getRequired());
			if (test == null || Constants.TRUE.equals(test)) {
				return;
			}
			test = PredicatesParser.removeBrackets(test);
			ProfileGeneration.writeRule(version, 11, object.getModelType(), getClause(object, entry), test,
					String.format(Constants.TRUE.equals(result) ? REQUIRED_DESCRIPTION : REQUIRED_CONDITION_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry), entry.getRequired()),
					String.format(REQUIRED_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry)),
					Constants.KEY_NAME);
		}
	}

	private static void checkSince(PDFVersion version, Object object, Entry entry) {
		if (entry.getSinceString() == null || !entry.getSinceString().contains(PredicatesParser.PREDICATE_PREFIX)) {
			return;
		}
		String result = new PredicatesParser(object, entry, version, null, Constants.SINCE_COLUMN).parse(entry.getSinceString());
		if (result == null || Constants.TRUE.equals(result)) {
			return;
		}
		result = PredicatesParser.removeBrackets(result);
		StringBuilder test = new StringBuilder();
		if (!object.isEntry()) {
			test.append(Entry.getContainsPropertyName(entry.getName())).append(" == false || ");
			entry.setContainsProperty(true);
		}
		test.append(result);
		ProfileGeneration.writeRule(version, 23, object.getModelType(), getClause(object, entry), test.toString(),
				String.format(SINCE_DESCRIPTION,
						ProfileGeneration.getErrorMessageStart(true, object, entry), entry.getSinceString()),
				String.format(SINCE_ERROR_MESSAGE,
						ProfileGeneration.getErrorMessageStart(false, object, entry)),
				Constants.KEY_NAME);
	}

	private static void deprecatedEntry(PDFVersion version, Object object, Entry entry) {
		String propertyName = Entry.getContainsPropertyName(entry.getName());
		if (entry.getDeprecatedVersion() != null && PDFVersion.compare(entry.getDeprecatedVersion(), version) < 0) {
			String test = Constants.CURRENT_ENTRY.equals(entry.getName()) ? "false" : propertyName + " == false";
			ProfileGeneration.writeRule(version, 5, object.getModelType(), getClause(object, entry), test,
					String.format(DEPRECATED_ENTRY_DESCRIPTION,
							ProfileGeneration.getErrorMessageStart(true, object, entry),
							entry.getDeprecatedVersion().getString()),
					String.format(DEPRECATED_ENTRY_ERROR_MESSAGE,
							ProfileGeneration.getErrorMessageStart(false, object, entry)),
					Constants.KEY_NAME);
			if (!Constants.CURRENT_ENTRY.equals(entry.getName())) {
				entry.setContainsProperty(true);
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
