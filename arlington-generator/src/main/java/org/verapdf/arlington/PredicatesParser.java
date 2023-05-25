package org.verapdf.arlington;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PredicatesParser {

	private static final Logger LOGGER = Logger.getLogger(PredicatesParser.class.getCanonicalName());

	private static final String ENTRY_REGEX = "['A-Za-z:.@\\*\\d\\-_]+";

	public static final String PREDICATE_PREFIX = "fn:";
	public static final String DEPRECATED_PREDICATE = "fn:Deprecated";
	public static final String EVAL_PREDICATE = "fn:Eval";
	public static final String EXTENSION_PREDICATE = "fn:Extension";
	public static final String IGNORE_PREDICATE = "fn:Ignore";
	public static final String MUST_BE_DIRECT_PREDICATE = "fn:MustBeDirect";
	public static final String MUST_BE_INDIRECT_PREDICATE = "fn:MustBeIndirect";
	public static final String REQUIRED_VALUE_PREDICATE = "fn:RequiredValue";

	public enum Operator {
		OR("||", 2),
		AND("&&", 3),
		NON_EQUALS("!=", 4),
		EQUALS("==", 4),
		GREATER(">", 5),
		EQUAL_GREATER(">=", 5),
		LESS("<", 5),
		EQUAL_LESS("<=", 5),
		ADD("+", 6),
		SUB("-", 6),
		MOD("mod", 7);

		String operator;
		int precedence;
		boolean hasLeftAssociativity;

		Operator(String operator, int precedence) {
			this.operator = operator;
			this.precedence = precedence;
			this.hasLeftAssociativity = true;
		}

		public String getOperator() {
			return this.operator;
		}

		public int getPrecedence() {
			return this.precedence;
		}

		public boolean isHasLeftAssociativity() {
			return hasLeftAssociativity;
		}

		public static Operator getOperator(String string) {
			if (string == null) {
				return null;
			}
			for (Operator operator : values()) {
				if (string.equals(operator.getOperator())) {
					return operator;
				}
			}
			return null;
		}
	}

	private void executeOperator(String operatorName, boolean isOriginal) {
		Part secondArgument = output.pop();
		Part firstArgument = output.pop();
		switch (operatorName) {
			case "||":
				or(firstArgument, secondArgument, isOriginal);
				break;
			case "&&":
				and(firstArgument, secondArgument, isOriginal);
				break;
			case "mod":
				mod(firstArgument, secondArgument);
				break;
			case "==":
				equal(firstArgument, secondArgument);
				break;
			case "!=":
				nonEqual(firstArgument, secondArgument);
				break;
			case ">":
			case ">=":
			case "<":
			case "<=":
			case "+":
			case "-":
				output.add(getNewPart(firstArgument, operatorName, secondArgument));
				break;
			default:
				output.add(operatorName + firstArgument.getString() + secondArgument.getString());
		}
	}

	private static boolean possibleNegative(String original) {
		if (!original.contains(" ")) {
			return false;
		}
		if (original.contains("||") && original.contains("&&")) {
			return false;
		}
		if (original.contains(".split")) {
			return false;
		}
		return true;
	}

	private static Part getNegativePart(Part original) {
		return new Part(getNegativeString(original.getString()), original.getUndefinedEntries());
	}

	private static String getNegativeString(String original) {
		return original
				.replace(" < ","$")
				.replace(" >= "," < ")
				.replace("$"," >= ")
				.replace(" <= ","$")
				.replace(" > "," <= ")
				.replace("$"," > ")
				.replace("!=","$")
				.replace("==","!=")
				.replace("$","==")
				.replace("||","$")
				.replace("&&","||")
				.replace("$","&&");
	}

	private String getCurrentFunction() {
		for (int i = operators.size() - 1; i >= 0; i--) {
			if (operators.get(i).startsWith(PREDICATE_PREFIX)) {
				return operators.get(i);
			}
		}
		return null;
	}

	private void mod(Part argument1, Part argument2) {
		output.add(getNewPart(argument1, "%", argument2));
	}

	private void equal(Part firstArgument, Part secondArgument) {
		if (("false".equals(firstArgument.getString()) && "true".equals(secondArgument.getString())) || ("true".equals(firstArgument.getString()) && "false".equals(secondArgument.getString()))) {
			output.add("false");
			return;
		}
		if (("false".equals(firstArgument.getString()) && "false".equals(secondArgument.getString())) || ("true".equals(firstArgument.getString()) && "true".equals(secondArgument.getString()))) {
			output.add("true");
			return;
		}
		if ("true".equals(secondArgument.getString()) && firstArgument.getString().contains(" ")) {
			output.add(firstArgument);
			return;
		}
		if ("true".equals(firstArgument.getString()) && secondArgument.getString().contains(" ")) {
			output.add(secondArgument);
			return;
		}
		if ("false".equals(firstArgument.getString()) && possibleNegative(secondArgument.getString())) {
			output.add(getNegativePart(secondArgument));
			return;
		}
		if ("false".equals(secondArgument.getString()) && possibleNegative(firstArgument.getString())) {
			output.add(getNegativePart(firstArgument));
			return;
		}
		if (isProfile) {
			output.add(getNewPart(firstArgument, "==", secondArgument));
		} else {
			output.add(getNewPart("Objects.equals(", firstArgument, ",", secondArgument, ")"));
		}
	}

	private void nonEqual(Part firstArgument, Part secondArgument) {
		if (("false".equals(firstArgument.getString()) && "true".equals(secondArgument.getString())) || ("true".equals(firstArgument.getString()) && "false".equals(secondArgument.getString()))) {
			output.add("true");
			return;
		}
		if (("false".equals(firstArgument.getString()) && "false".equals(secondArgument.getString())) || ("true".equals(firstArgument.getString()) && "true".equals(secondArgument.getString()))) {
			output.add("false");
			return;
		}
		if ("false".equals(secondArgument.getString()) && firstArgument.getString().contains(" ")) {
			output.add(firstArgument);
			return;
		}
		if ("false".equals(firstArgument.getString()) && secondArgument.getString().contains(" ")) {
			output.add(secondArgument);
			return;
		}
		if ("true".equals(firstArgument.getString()) && possibleNegative(secondArgument.getString())) {
			output.add(getNegativePart(secondArgument));
			return;
		}
		if ("true".equals(secondArgument.getString()) && possibleNegative(firstArgument.getString())) {
			output.add(getNegativePart(firstArgument));
			return;
		}
		if (isProfile) {
			output.add(getNewPart(firstArgument, "!=", secondArgument));
		} else {
			output.add(getNewPart("!Objects.equals(", firstArgument, ",", secondArgument, ")"));
		}
	}

	public static boolean containsBrackets(String str) {
		int counter = 0;
		for (int i = 0; i < str.length() - 1; i++) {
			if (str.charAt(i) == '(') {
				counter++;
			} else if (str.charAt(i) == ')') {
				counter--;
			}
			if (counter <= 0) {
				return false;
			}
		}
		return true;
	}

	private void bitClear() {
		checkBit(0);
	}

	private void bitSet() {
		checkBit(1);
	}

	private void bitsClear() {
		checkBits(0);
	}

	private void bitsSet() {
		checkBits(1);
	}

	private void eval() {
		output.push(getNewPart(arguments));
	}

	private void fileSize() {
		output.push(getPropertyOrMethodName(Constants.FILE_SIZE));
	}

	private void ignore() {
		output.push(IGNORE_PREDICATE + getNewPart(arguments).getString());
	}

	private String split(String stringName, boolean equals, List<String> values) {
		if (isProfile) {
			return ProfileGeneration.split(stringName, equals, values);
		} else {
			return JavaGeneration.split(stringName, equals, values);
		}
	}

	private void isEncryptedWrapped() {
		output.push(getPropertyOrMethodName(Constants.IS_ENCRYPTED_WRAPPER));
	}

	private void isPDFTagged() {
		output.push(getPropertyOrMethodName(Constants.IS_PDF_TAGGED));
	}

	private void not() {
		methods("(", getNewPart(arguments), ")", "!=", "true");
	}

	private void notStandard14Font() {
		output.push(getPropertyOrMethodName(Constants.NOT_STANDARD_14_FONT));
	}

	private void numberOfPages() {
		output.push(getPropertyOrMethodName(Constants.NUMBER_OF_PAGES));
	}

	private String addQuotes(String argument) {
		if (argument.matches(ENTRY_REGEX) && !argument.startsWith("@") && !argument.startsWith(PREDICATE_PREFIX) && !argument.contains("::") &&
				!argument.matches("-?" + Constants.NUMBER_REGEX) && !Constants.STAR.equals(argument) &&
				!argument.matches("-?" + Constants.DOUBLE_REGEX) && !Constants.TRUE.equals(argument) &&
				!Constants.FALSE.equals(argument)) {
			return "\"" + removeQuotes(argument) + "\"";
		}
		return argument;
	}

	public Part getNewPart(java.lang.Object ... parts) {
		List<Part> list = new LinkedList<>();
		for (java.lang.Object object : parts) {
			if (object instanceof String) {
				list.add(new Part((String)object));
			} else {
				list.add((Part)object);
			}
		}
		return getNewPart(list);
	}

	public String getEntryName(String name) {
		String entryName = removeQuotes(name);
		if (entryName.startsWith("@")) {
			entryName = entryName.substring(1);
		}
		if (Constants.STAR.equals(entryName) && Constants.CURRENT_ENTRY.equals(entry.getName())) {
			return Constants.CURRENT_ENTRY;
		}
		return entryName;
	}

	public static String getPredicateLastArgument(String predicate) {
		int index = predicate.lastIndexOf(",");
		int lastIndex = predicate.lastIndexOf(")");
		while (predicate.charAt(lastIndex) == ')') {
			lastIndex--;
		}
		return predicate.substring(index + 1, lastIndex + 1).trim();
	}

	public static String getPredicateFirstArgument(String predicate) {
		int index = predicate.indexOf("(");
		int lastIndex = predicate.lastIndexOf(",");
		return predicate.substring(index + 1, lastIndex).trim();
	}

	private String getPropertyOrMethodName(String propertyName) {
		return isProfile ? propertyName : JavaGeneration.getMethodName(propertyName) + "()";
	}

	public static String removeQuotes(String argument) {
		if ((argument.startsWith("\"") && argument.endsWith("\"")) || (argument.startsWith("'") && argument.endsWith("'"))) {
			return argument.substring(1, argument.length() - 1);
		}
		return argument;
	}

	public boolean isDefault() {
		return "DefaultValue".equals(columnName);
	}

	public static class Part {
		private final String part;
		private Map<String, Type> undefinedEntries = new HashMap<>();

		public Part(String part) {
			this.part = part;
		}

		public Part(String part, String entry, Type type) {
			this.part = part;
			this.undefinedEntries.put(entry, type);
		}

		public Part(String part, Map<String, Type> undefinedEntries) {
			this.part = part;
			this.undefinedEntries = new HashMap<>(undefinedEntries);
		}

		public String getString() {
			return part;
		}

		public boolean isUndefined() {
			return !undefinedEntries.isEmpty();
		}

		public Map<String, Type> getUndefinedEntries() {
			return undefinedEntries;
		}
	}
}
