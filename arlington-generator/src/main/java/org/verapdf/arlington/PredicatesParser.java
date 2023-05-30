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

	private boolean isProfile = true;//false if java code
	private PartStack output = new PartStack();
	private Stack<String> operators = new Stack<>();
	private List<Part> arguments = new LinkedList<>();
	private Object object = new Object();
	private String columnName;
	private Entry entry = new MultiEntry("name");
	private Type type = Type.INTEGER;
	private PDFVersion version = PDFVersion.VERSION2_0;
	private static final List<String> colorants = new LinkedList<>();

	static {
		colorants.add("\"Cyan\"");
		colorants.add("\"Magenta\"");
		colorants.add("\"Yellow\"");
		colorants.add("\"Black\"");
	}

	public PredicatesParser() {
	}

	public PredicatesParser(Object object, Entry entry, PDFVersion version, Type type, String columnName, boolean isProfile) {
		this.object = object;
		this.entry = entry;
		this.version = version;
		this.type = type;
		this.columnName = columnName;
		this.isProfile = isProfile;
	}

	public PredicatesParser(Object object, Entry entry, PDFVersion version, Type type, String columnName) {
		this(object, entry, version, type, columnName, true);
	}

	public static class PartStack extends Stack<Part> {
		private void push(String s) {
			push(new Part(s));
		}

		private void add(String s) {
			add(new Part(s));
		}
	}

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

	private void or(Part firstArgument, Part secondArgument, boolean isOriginal) {
		if (isDefault() && EVAL_PREDICATE.equals(getCurrentFunction())) {
			output.add(getNewPart(firstArgument, secondArgument));
		} else if ("true".equals(firstArgument.getString()) || "true".equals(secondArgument.getString())) {
			output.add("true");
		} else if ("false".equals(firstArgument.getString())) {
			output.add(secondArgument);
		} else if ("false".equals(secondArgument.getString())) {
			output.add(firstArgument);
		} else if (!isOriginal) {
			output.add(getNewPart(firstArgument, "||", secondArgument));
		} else {
			List<Part> parts = new LinkedList<>();
			if (firstArgument.isUndefined()) {
				parts.add(getDefined(firstArgument, false, secondArgument.getUndefinedEntries()));
			} else {
				parts.add(firstArgument);
			}
			parts.add(new Part("||"));
			if (secondArgument.isUndefined()) {
				parts.add(getDefined(secondArgument, false, firstArgument.getUndefinedEntries()));
			} else {
				parts.add(secondArgument);
			}
			output.add(getNewPart(parts));
		}
	}

	private void and(Part firstArgument, Part secondArgument, boolean isOriginal) {
		if ("false".equals(firstArgument.getString()) || "false".equals(secondArgument.getString())) {
			output.add("false");
		} else if ("true".equals(firstArgument.getString())) {
			output.add(secondArgument);
		} else if ("true".equals(secondArgument.getString())) {
			output.add(firstArgument);
		} else if (!isOriginal) {
			output.add(getNewPart(firstArgument, "&&", secondArgument));
		} else {
			List<Part> parts = new LinkedList<>();
			if (firstArgument.isUndefined()) {
				parts.add(getDefined(firstArgument, true, secondArgument.getUndefinedEntries()));
			} else {
				parts.add(firstArgument);
			}
			parts.add(new Part("&&"));
			if (secondArgument.isUndefined()) {
				parts.add(getDefined(secondArgument, true, firstArgument.getUndefinedEntries()));
			} else {
				parts.add(secondArgument);
			}
			output.add(getNewPart(parts));
		}
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

	private void executeFunction(String functionName) {
		arguments = new LinkedList<>();
		while (!"(".equals(output.peek().getString())) {
			arguments.add(0, output.pop());
		}
		output.pop(); //pop "("
		Part token = !output.isEmpty() ? output.pop() : new Part("");//pop possible functionName
		switch (functionName) {
//			case "fn:AlwaysUnencrypted":
//				break;
			case "fn:ArrayLength":
				arrayLength();
				break;
			case "fn:ArraySortAscending":
				arraySortAscending();
				break;
			case "fn:BeforeVersion":
				beforeVersion();
				break;
			case "fn:BitClear":
				bitClear();
				break;
			case "fn:BitSet":
				bitSet();
				break;
			case "fn:BitsClear":
				bitsClear();
				break;
			case "fn:BitsSet":
				bitsSet();
				break;
			case "fn:Contains":
				contains();
				break;
			case "fn:DefaultValue":
				defaultValue();
				break;
			case DEPRECATED_PREDICATE:
				deprecated();
				break;
			case EVAL_PREDICATE:
				eval();
				break;
			case EXTENSION_PREDICATE:
				extension();
				break;
			case "fn:FileSize":
				fileSize();
				break;
//			case "fn:FontHasLatinChars":
//				break;
			case "fn:HasProcessColorants":
				hasProcessColorants();
				break;
			case "fn:HasSpotColorants":
				hasSpotColorants();
				break;
			case IGNORE_PREDICATE:
				ignore();
				break;
			case "fn:ImageIsStructContentItem":
				imageIsStructContentItem();
				break;
//			case "fn:ImplementationDependent":
//				break;
			case "fn:InKeyMap":
				inKeyMap();
				break;
			case "fn:InNameTree":
				inNameTree();
				break;
//			case "fn:IsAssociatedFile":
//				break;
			case "fn:IsEncryptedWrapper":
				isEncryptedWrapped();
				break;
			case "fn:IsFieldName":
				isFieldName();
				break;
			case "fn:IsHexString":
				isHexString();
				break;
//			case "fn:IsLastInNumberFormatArray":
//				break;
//			case "fn:IsMeaningful":
//				break;
			case "fn:IsPDFTagged":
				isPDFTagged();
				break;
			case "fn:IsPDFVersion":
				isPDFVersion();
				break;
			case "fn:IsPresent":
				isPresent();
				break;
			case "fn:IsRequired":
				isRequired();
				break;
//			case "fn:KeyNameIsColorant":
//				break;
			case MUST_BE_DIRECT_PREDICATE:
				mustBeDirect();
				break;
			case MUST_BE_INDIRECT_PREDICATE:
				mustBeIndirect();
				break;
			case "fn:NoCycle":
				noCycle();
				break;
			case "fn:Not":
				not();
				break;
			case "fn:NotStandard14Font":
				notStandard14Font();
				break;
			case "fn:NumberOfPages":
				numberOfPages();
				break;
			case "fn:PageContainsStructContentItems":
				pageContainsStructContentItems();
				break;
			case "fn:PageProperty":
				pageProperty();
				break;
			case "fn:RectHeight":
				rectHeight();
				break;
			case "fn:RectWidth":
				rectWidth();
				break;
			case REQUIRED_VALUE_PREDICATE:
				requiredValue();
				break;
			case "fn:SinceVersion":
				sinceVersion();
				break;
			case "fn:StreamLength":
				streamLength();
				break;
			case "fn:StringLength":
				stringLength();
				break;
			default:
				StringBuilder result = new StringBuilder();
				if (!token.isEmpty()) {
					output.push(token);
				}
				if (!functionName.isEmpty()) {
					operators.push(functionName);
				}
				Part res = getNewPart(arguments);
				boolean addBrackets = !containsBrackets(res.getString()) && res.getString().contains(" ");
				if (addBrackets) {
					result.append("(");
				}
				result.append(res.getString());
				if (addBrackets) {
					result.append(")");
				}
				output.push(new Part(result.toString(), res.getUndefinedEntries()));
				break;
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

	public static String removeBrackets(String str) {
		if (containsBrackets(str)) {
			return str.substring(1, str.length() - 1);
		}
		return str;
	}

	private void arrayLength() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of arrayLength");
		}
		String arrayEntryName = getEntryName(arguments.get(0).getString());
		Entry arrayEntry = object.getEntry(arrayEntryName);
		if (arrayEntry != null) {
			String argument = getPropertyOrMethodName(arrayEntry.getArrayLengthPropertyName());
			Part part = new Part(argument);
			if (!Objects.equals(arrayEntry.getName(), this.entry.getName())) {
				part.getUndefinedEntries().put(arrayEntryName, Type.ARRAY);
			}
			output.push(part);
			arrayEntry.setArraySizeProperty(true);
		} else if (arrayEntryName.contains("::")) {
			String argument = getPropertyOrMethodName(Entry.getArrayLengthPropertyName(arrayEntryName));
			output.push(new Part(argument, arrayEntryName, Type.ARRAY));
			object.getArraySizeProperties().add(arrayEntryName);
		} else {
			throw new RuntimeException("Invalid entry name in arrayLength");
		}
	}

	private void arraySortAscending() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of arraySortAscending");
		}
		Entry arrayEntry = object.getEntry(getEntryName(arguments.get(0).getString()));
		if (arrayEntry == null) {
			throw new RuntimeException("Invalid entry name in arraySortAscending");
		}
		int number;
		try {
			number = Integer.parseInt(arguments.get(1).getString());
		} catch (NumberFormatException ignored) {
			throw new RuntimeException("Invalid number in arraySortAscending");
		}
		arrayEntry.getArraySortAscendingProperties().add(number);
		output.push(getPropertyOrMethodName(arrayEntry.getArraySortAscendingPropertyName(number) + " == true"));
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

	private void contains() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of contains");
		}
		Entry entry = object.getEntry(getEntryName(arguments.get(0).getString()));
		if (entry == null) {
			throw new RuntimeException("Invalid entry in contains");
		} else {
			output.push("(" + getPropertyOrMethodName(entry.getEntriesStringPropertyName()) + " != null && " +
					split(getPropertyOrMethodName(entry.getEntriesStringPropertyName()), true,
							Collections.singletonList(arguments.get(1).getString())) + " > 0)");
			entry.setEntriesStringProperty(true);
		}
	}

	private void deprecated() {
		if (arguments.size() < 2) {
			throw new RuntimeException("Invalid number of arguments of " + DEPRECATED_PREDICATE);
		}
		PDFVersion version = PDFVersion.getPDFVersion(getEntryName(arguments.get(0).getString()));
		if (PDFVersion.compare(this.version, version) >= 0) {
			Part part = getNewPart(arguments.subList(1, arguments.size()));
			output.push(new Part(DEPRECATED_PREDICATE.replace(":", "/") + part.getString(),
					part.getUndefinedEntries()));
		} else {
			output.push(getNewPart(arguments.subList(1, arguments.size())));
		}
	}

	private void eval() {
		output.push(getNewPart(arguments));
	}

	private void fileSize() {
		output.push(getPropertyOrMethodName(Constants.FILE_SIZE));
	}

	private void hasProcessColorants() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of hasProcessColorants");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry arrayEntry = object.getEntry(entryName);
		if (arrayEntry == null && !entryName.contains("::")) {
			throw new RuntimeException("Invalid entry name in hasProcessColorants");
		}
		output.push("(" + getPropertyOrMethodName(Entry.getEntriesStringPropertyName(entryName)) + " != null && " +
				split(getPropertyOrMethodName(Entry.getEntriesStringPropertyName(entryName)), true, colorants) + " > 0)");
		object.getEntriesStringProperties().add(entryName);
	}

	private void hasSpotColorants() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of hasSpotColorants");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry arrayEntry = object.getEntry(entryName);
		if (arrayEntry == null && !entryName.contains("::")) {
			throw new RuntimeException("Invalid entry name in hasSpotColorants");
		}
		output.push("(" + getPropertyOrMethodName(Entry.getEntriesStringPropertyName(entryName)) + " != null && " +
				split(getPropertyOrMethodName(Entry.getEntriesStringPropertyName(entryName)), false, colorants) + " > 0)");
		object.getEntriesStringProperties().add(entryName);
	}

	private void ignore() {
		output.push(IGNORE_PREDICATE + getNewPart(arguments).getString());
	}

	private void imageIsStructContentItem() {
		object.setImageIsStructContentItemProperty(true);
		output.push("(" + getPropertyOrMethodName(Constants.IMAGE_IS_STRUCT_CONTENT_ITEM) + " == true && " +
				Entry.getTypeValuePropertyName("Subtype", Type.NAME) + " == \"Image\")");
	}

	private void inKeyMap() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of inKeyMap");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry keyMapEntry = object.getEntry(entryName);
		if (keyMapEntry == null && !entryName.contains("::")) {
			throw new RuntimeException("Invalid entry name in inKeyMap");
		}
		output.push("(" + split(getPropertyOrMethodName(Entry.getKeysStringPropertyName(entryName)), true,
				Collections.singletonList(getPropertyOrMethodName(entry.getTypeValuePropertyName(Type.NAME)))) + " > 0)");
		object.getKeysStringProperties().add(entryName);
		entry.getTypeValueProperties().add(Type.NAME);
	}

	private String split(String stringName, boolean equals, List<String> values) {
		if (isProfile) {
			return ProfileGeneration.split(stringName, equals, values);
		} else {
			return JavaGeneration.split(stringName, equals, values);
		}
	}

	private void inNameTree() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of inNameTree");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry nameTreeEntry = object.getEntry(entryName);
		if (nameTreeEntry == null && !entryName.contains("::")) {
			throw new RuntimeException("Invalid entry name in inNameTree");
		}
		output.push("(" + getPropertyOrMethodName(entry.getNameTreeContainsStringPropertyName(entryName)) + " == true)");
		entry.getInNameTreeProperties().add(entryName);
	}

	private void isFieldName() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of isFieldName");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry entry = object.getEntry(entryName);
		if (entry != null) {
			output.push(Entry.getIsFieldNamePropertyName(entry.getName()) + " == true");
			entry.setFieldNameProperty(true);
		} else {
			throw new RuntimeException("Invalid argument of isFieldName");
		}
	}

	private void isEncryptedWrapped() {
		output.push(getPropertyOrMethodName(Constants.IS_ENCRYPTED_WRAPPER));
	}

	private void isHexString() {
		if (!arguments.isEmpty()) {
			throw new RuntimeException("Invalid number of arguments of isHexString");
		}
		output.push(getPropertyOrMethodName(entry.getIsHexStringPropertyName()) + " == true");
		entry.setHexStringProperty(true);
	}

	private void isPDFTagged() {
		output.push(getPropertyOrMethodName(Constants.IS_PDF_TAGGED));
	}

	private void noCycle() {
		output.push("(" + getPropertyOrMethodName(entry.getHasCyclePropertyName()) + " == false)");
		entry.setHasCycleProperty(true);
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

	private void pageContainsStructContentItems() {
		object.setPageContainsStructContentItemsProperty(true);
		output.push(getPropertyOrMethodName("(" + Constants.PAGE_CONTAINS_STRUCT_CONTENT_ITEMS) + " == true)");
	}

	private void pageProperty() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of pageProperty");
		}
		output.push("page::" + getEntryName(arguments.get(0).getString()) + "::" + getEntryName(arguments.get(1).getString()));
	}

	private void rectHeight() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of rectHeight");
		}
		Entry rectangleEntry = object.getEntry(getEntryName(arguments.get(0).getString()));
		if (rectangleEntry == null) {
			throw new RuntimeException("Invalid entry name in rectHeight");
		}
		Part part = new Part(getPropertyOrMethodName(rectangleEntry.getRectHeightPropertyName()));
		if (!Objects.equals(rectangleEntry.getName(), this.entry.getName())) {
			part.getUndefinedEntries().put(rectangleEntry.getName(), Type.RECTANGLE);
		}
		output.push(part);
		rectangleEntry.setRectHeightProperty(true);
	}

	private void rectWidth() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of rectWidth");
		}
		Entry rectangleEntry = object.getEntry(getEntryName(arguments.get(0).getString()));
		if (rectangleEntry == null) {
			throw new RuntimeException("Invalid entry name in rectWidth");
		}
		Part part = new Part(getPropertyOrMethodName(rectangleEntry.getRectWidthPropertyName()));
		if (!Objects.equals(rectangleEntry.getName(), this.entry.getName())) {
			part.getUndefinedEntries().put(rectangleEntry.getName(), Type.RECTANGLE);
		}
		output.push(part);
		rectangleEntry.setRectWidthProperty(true);
	}

	private void streamLength() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of streamLength");
		}
		Entry streamEntry = object.getEntry(getEntryName(arguments.get(0).getString()));
		if (streamEntry == null) {
			throw new RuntimeException("Invalid entry name in streamLength");
		}
		Part part = new Part(getPropertyOrMethodName(streamEntry.getStreamLengthPropertyName()));
		if (!Objects.equals(streamEntry.getName(), this.entry.getName())) {
			part.getUndefinedEntries().put(streamEntry.getName(), Type.STREAM);
		}
		output.push(part);
		streamEntry.setStreamSizeProperty(true);
	}

	private void stringLength() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of stringLength");
		}
		Entry stringEntry = object.getEntry(getEntryName(arguments.get(0).getString()));
		if (stringEntry == null) {
			throw new RuntimeException("Invalid entry name in stringLength");
		}
		Part part = new Part(getPropertyOrMethodName(stringEntry.getStringLengthPropertyName()));
		if (!Objects.equals(stringEntry.getName(), this.entry.getName())) {
			part.getUndefinedEntries().put(stringEntry.getName(), Type.STRING);
		}
		output.push(part);
		stringEntry.setStringSizeProperty(true);
	}

	private void checkBit(int bitValue) {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of bitClear/bitSet");
		}
		int bit;
		try {
			bit = Integer.parseInt(arguments.get(0).getString());
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid argument of bitClear/bitSet");
		}
		output.push("(" + getPropertyOrMethodName(entry.getTypeValuePropertyName(Type.BITMASK)) + " >> " + (bit - 1) +
				" & 1) == " + bitValue);
		entry.addTypeValueProperty(Type.BITMASK);
	}

	private void checkBits(int bitsValue) {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of bitsClear/bitsSet");
		}
		int firstBit;
		int secondBit;
		try {
			firstBit = Integer.parseInt(arguments.get(0).getString());
			secondBit = Integer.parseInt(arguments.get(1).getString());
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid argument of bitsClear/bitsSet");
		}
		StringBuilder test = new StringBuilder("(");
		for (int bit = firstBit; bit <= secondBit; bit++) {
			test.append("(").append(getPropertyOrMethodName(entry.getTypeValuePropertyName(Type.BITMASK))).append(" >> ")
					.append(bit - 1).append(" & 1) == ").append(bitsValue).append(" && ");
		}
		test.delete(test.length() - 4, test.length());
		test.append(")");
		output.push(test.toString());
		entry.addTypeValueProperty(Type.BITMASK);
	}

	private String addQuotes(String argument) {
		if (argument.matches(ENTRY_REGEX) && !argument.startsWith("@") && !argument.startsWith(PREDICATE_PREFIX) &&
				!argument.contains("::") && !argument.matches("-?" + Constants.NUMBER_REGEX) &&
				!Constants.STAR.equals(argument) && !argument.matches("-?" + Constants.DOUBLE_REGEX) &&
				!Constants.TRUE.equals(argument) && !Constants.FALSE.equals(argument)) {
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
