package org.verapdf.arlington;

import javafx.util.Pair;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PredicatesParser {

	private static final Logger LOGGER = Logger.getLogger(PredicatesParser.class.getCanonicalName());

	private static final String ENTRY_REGEX = "['A-Za-z:.@\\*\\d\\-_]+";

	public static final String PREDICATE_PREFIX = "fn:";
	public static final String BEFORE_VERSION_PREDICATE = "fn:BeforeVersion";
	public static final String DEPRECATED_PREDICATE = "fn:Deprecated";
	public static final String EVAL_PREDICATE = "fn:Eval";
	public static final String EXTENSION_PREDICATE = "fn:Extension";
	public static final String IGNORE_PREDICATE = "fn:Ignore";
	public static final String IS_PDF_VERSION_PREDICATE = "fn:IsPDFVersion";
	public static final String IS_REQUIRED_PREDICATE = "fn:IsRequired";
	public static final String MUST_BE_DIRECT_PREDICATE = "fn:MustBeDirect";
	public static final String MUST_BE_INDIRECT_PREDICATE = "fn:MustBeIndirect";
	public static final String REQUIRED_VALUE_PREDICATE = "fn:RequiredValue";
	public static final String SINCE_VERSION_PREDICATE = "fn:SinceVersion";
	public static final String VALUE_ONLY_WHEN_PREDICATE = "fn:ValueOnlyWhen";

	private boolean isProfile = true;//false if java code
	protected boolean isDescription = false;
	protected final PartStack output = new PartStack();
	protected final Stack<String> operators = new Stack<>();
	List<Part> arguments = new LinkedList<>();
	private Object object = new Object();
	private String columnName;
	private Entry entry = new MultiEntry("name");
	private Type type = Type.INTEGER;
	protected PDFVersion version = PDFVersion.VERSION2_0;
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
		protected void push(String s) {
			push(new Part(s));
		}

		protected void add(String s) {
			add(new Part(s));
		}
	}

	public String parse(String str) {
		try {
			str = str.replace("fn:IsPresent(AP::N::*)", "fn:IsDictionary(AP::N)")
					.replace("fn:IsPresent(AP::R::*)", "fn:IsDictionary(AP::R)")
					.replace("fn:IsPresent(AP::D::*)", "fn:IsDictionary(AP::D)");
			String result = parseString(str);
			if (result == null) {
				return null;
			}
			if (result.contains("@") || Entry.isComplexEntry(result)) {
				LOGGER.log(Level.WARNING, getString() + " result: " + result + " original: " + str);
				return null;
			}
			if (result.contains(PREDICATE_PREFIX)) {
				return null;
			}
			return result;
		} catch (RuntimeException e) {
			LOGGER.log(Level.WARNING, getString() + ": " + str + ". Error: " + e.getMessage());
		}
		return null;
	}

	public String parseString(String str) {
		StringBuilder currentString = new StringBuilder();
		boolean flag = false;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case '(':
					processToken(currentString.toString());
					currentString = new StringBuilder();
					processToken("(");
					break;
				case ')':
					processToken(currentString.toString());
					currentString = new StringBuilder();
					processToken(")");
					break;
				case ',':
					processToken(currentString.toString());
					currentString = new StringBuilder();
					processToken(",");
					break;
				case ' ':
					processToken(currentString.toString());
					currentString = new StringBuilder();
					break;
				default:
					boolean newFlag = String.valueOf(str.charAt(i)).matches(ENTRY_REGEX);
					if (currentString.length() > 0) {
						if (flag && !newFlag) {
							processToken(currentString.toString());
							currentString = new StringBuilder();
						}
						if (!flag && newFlag) {
							processToken(currentString.toString());
							currentString = new StringBuilder();
						}
					}
					currentString.append(str.charAt(i));
					flag = newFlag;
					break;
			}
		}
		processToken(currentString.toString());
		while (!operators.isEmpty()) {
			if ("(".equals(operators.peek())) {
				throw new RuntimeException("This expression is invalid");
			}
			executeOperator(operators.pop(), true);
		}
		Part result = getNewPart(output);
		String res = result.getString();
		if (result.isUndefined()) {
			output.clear();
			result = getDefined(result, true, Collections.EMPTY_MAP);
			res = result.getString();
		}
		if (Constants.UNDEFINED.equals(res)) {
			return null;
		}
		if (isDefault()) {
			res = res + " null";
		}
		return res;
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

	public static boolean isOperator(String string) {
		return "+".equals(string) || "-".equals(string) || "<=".equals(string) || "<".equals(string) ||
				">".equals(string) || ">=".equals(string) || "||".equals(string) || "==".equals(string) ||
				"&&".equals(string) || "!=".equals(string) || "mod".equals(string);
	}

	protected void processToken(String token) {
		processToken(new Part(token), true);
	}

	private void processTokens(java.lang.Object ... tokens) {
		processTokensArray(tokens);
	}

	private void processTokensArray(java.lang.Object[] tokens) {
		for (java.lang.Object token : tokens) {
			if (token instanceof String) {
				processToken(new Part((String)token), false);
			} else {
				processToken((Part)token, false);
			}
		}
	}

	private void processToken(Part part, boolean isOriginal) {
		switch (part.getString()) {
			case "":
				return;
			case "(":
				operators.add(part.getString());
				output.add(part);
				break;
			case ")":
				while (!operators.isEmpty() && !"(".equals(operators.peek())) {
					executeOperator(operators.pop(), isOriginal);
				}
				operators.pop();
				executeFunction(!operators.isEmpty() ? operators.pop() : "");
				break;
			case ",":
				while (!"(".equals(operators.peek())) {
					executeOperator(operators.pop(), isOriginal);
				}
				break;
			default:
				if (part.getString().startsWith(PREDICATE_PREFIX)) {
					operators.push(part.getString());
					output.push(part);
				} else {
					Operator operator = Operator.getOperator(part.getString());
					if (operator != null) {
						while (!operators.isEmpty() && Operator.getOperator(operators.peek()) != null &&
								operator.getPrecedence() <= Operator.getOperator(operators.peek()).getPrecedence() &&
								operator.isHasLeftAssociativity()) {
							executeOperator(operators.pop(), isOriginal);
						}
						operators.push(operator.getOperator());
					} else {
						output.add(isOriginal ? new Part(addQuotes(part.getString())) : part);
					}
				}
		}
	}

	protected void executeOperator(String operatorName, boolean isOriginal) {
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

	private String getBooleanFromVersion(String string) {
		PDFVersion version = PDFVersion.getPDFVersion(string);
		if (version != null) {
			if (PDFVersion.compare(this.version, version) >= 0) {
				 return Constants.TRUE;
			} else {
				return Constants.FALSE;
			}
		}
		return string;
	}

	protected void or(Part firstArgument, Part secondArgument, boolean isOriginal) {
		String firstString = firstArgument.getString();
		String secondString = secondArgument.getString();
		if (Constants.SINCE_COLUMN.equals(columnName)) {
			firstString = getBooleanFromVersion(firstString);
			secondString = getBooleanFromVersion(secondString);
		}
		if (isDefault() && EVAL_PREDICATE.equals(getCurrentFunction())) {
			output.add(getNewPart(firstArgument, secondArgument));
		} else if (Constants.TRUE.equals(firstString) || Constants.TRUE.equals(secondString)) {
			output.add(Constants.TRUE);
		} else if (Constants.FALSE.equals(firstString) || Constants.UNDEFINED.equals(firstString)) {
			output.add(secondArgument);
		} else if (Constants.FALSE.equals(secondString) || Constants.UNDEFINED.equals(secondString)) {
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

	protected void and(Part firstArgument, Part secondArgument, boolean isOriginal) {
		if (Constants.FALSE.equals(firstArgument.getString()) || Constants.FALSE.equals(secondArgument.getString())) {
			output.add(Constants.FALSE);
		} else if (Constants.TRUE.equals(firstArgument.getString()) || Constants.UNDEFINED.equals(firstArgument.getString())) {
			output.add(secondArgument);
		} else if (Constants.TRUE.equals(secondArgument.getString()) || Constants.UNDEFINED.equals(secondArgument.getString())) {
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

	private Part getDefined(Part part, boolean and, Map<String, Type> undefinedEntries) {
		List<java.lang.Object> result = new LinkedList<>();
		result.add("(");
		Map<String, Type> undefinedEntriesNames = new HashMap<>();
		for (Map.Entry<String, Type> undefinedEntry : part.getUndefinedEntries().entrySet()) {
			if (undefinedEntries.containsKey(undefinedEntry.getKey())) {
				undefinedEntriesNames.put(undefinedEntry.getKey(), undefinedEntry.getValue());
				continue;
			}
			if (Entry.isComplexEntry(undefinedEntry.getKey())) {
				object.getEntriesHasTypeProperties().put(undefinedEntry.getKey(), undefinedEntry.getValue());
			} else {
				Entry entry = object.getEntry(undefinedEntry.getKey());
				entry.addHasTypeProperty(undefinedEntry.getValue());
			}
			result.add(Entry.getHasTypePropertyName(undefinedEntry.getKey(), undefinedEntry.getValue()));
			result.add("==");
			if (and) {
				result.add(Constants.FALSE);
				result.add("||");
			} else {
				result.add(Constants.TRUE);
				result.add("&&");
			}
		}
		result.add(new Part(part.getString(), undefinedEntriesNames));
		result.add(")");
		processTokensArray(result.toArray());
		return output.pop();
	}

	private void mod(Part argument1, Part argument2) {
		output.add(getNewPart(argument1, "%", argument2));
	}

	protected void equal(Part firstArgument, Part secondArgument) {
		if (Constants.UNDEFINED.equals(firstArgument.getString()) || Constants.UNDEFINED.equals(secondArgument.getString())) {
			output.add(Constants.UNDEFINED);
			return;
		}
		if ((Constants.FALSE.equals(firstArgument.getString()) && Constants.TRUE.equals(secondArgument.getString())) || (Constants.TRUE.equals(firstArgument.getString()) && Constants.FALSE.equals(secondArgument.getString()))) {
			output.add(Constants.FALSE);
			return;
		}
		if ((Constants.FALSE.equals(firstArgument.getString()) && Constants.FALSE.equals(secondArgument.getString())) || (Constants.TRUE.equals(firstArgument.getString()) && Constants.TRUE.equals(secondArgument.getString()))) {
			output.add(Constants.TRUE);
			return;
		}
		if (Constants.TRUE.equals(secondArgument.getString()) && firstArgument.getString().contains(" ")) {
			output.add(firstArgument);
			return;
		}
		if (Constants.TRUE.equals(firstArgument.getString()) && secondArgument.getString().contains(" ")) {
			output.add(secondArgument);
			return;
		}
		if (Constants.FALSE.equals(firstArgument.getString()) && possibleNegative(secondArgument.getString())) {
			output.add(getNegativePart(secondArgument));
			return;
		}
		if (Constants.FALSE.equals(secondArgument.getString()) && possibleNegative(firstArgument.getString())) {
			output.add(getNegativePart(firstArgument));
			return;
		}
		if (isProfile) {
			output.add(getNewPart(firstArgument, "==", secondArgument));
		} else {
			output.add(getNewPart("Objects.equals(", firstArgument, ",", secondArgument, ")"));
		}
	}

	protected void nonEqual(Part firstArgument, Part secondArgument) {
		if (Constants.UNDEFINED.equals(firstArgument.getString()) || Constants.UNDEFINED.equals(secondArgument.getString())) {
			output.add(Constants.UNDEFINED);
			return;
		}
		if ((Constants.FALSE.equals(firstArgument.getString()) && Constants.TRUE.equals(secondArgument.getString())) ||
				(Constants.TRUE.equals(firstArgument.getString()) && Constants.FALSE.equals(secondArgument.getString()))) {
			output.add(Constants.TRUE);
			return;
		}
		if ((Constants.FALSE.equals(firstArgument.getString()) && Constants.FALSE.equals(secondArgument.getString())) ||
				(Constants.TRUE.equals(firstArgument.getString()) && Constants.TRUE.equals(secondArgument.getString()))) {
			output.add(Constants.FALSE);
			return;
		}
		if (Constants.FALSE.equals(secondArgument.getString()) && firstArgument.getString().contains(" ")) {
			output.add(firstArgument);
			return;
		}
		if (Constants.FALSE.equals(firstArgument.getString()) && secondArgument.getString().contains(" ")) {
			output.add(secondArgument);
			return;
		}
		if (Constants.TRUE.equals(firstArgument.getString()) && possibleNegative(secondArgument.getString())) {
			output.add(getNegativePart(secondArgument));
			return;
		}
		if (Constants.TRUE.equals(secondArgument.getString()) && possibleNegative(firstArgument.getString())) {
			output.add(getNegativePart(firstArgument));
			return;
		}
		if (isProfile) {
			output.add(getNewPart(firstArgument, "!=", secondArgument));
		} else {
			output.add(getNewPart("!Objects.equals(", firstArgument, ",", secondArgument, ")"));
		}
	}

	protected void executeFunction(String functionName) {
		arguments = new LinkedList<>();
		while (!"(".equals(output.peek().getString())) {
			arguments.add(0, output.pop());
		}
		output.pop(); //pop "("
		Part token = !output.isEmpty() ? output.pop() : new Part("");//pop possible functionName
		switch (functionName) {
			case "fn:AlwaysUnencrypted":
				alwaysUnencrypted();
				break;
			case "fn:ArrayLength":
				arrayLength();
				break;
			case "fn:ArraySortAscending":
				arraySortAscending();
				break;
			case BEFORE_VERSION_PREDICATE:
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
//			case "fn:InNameTree":
//				inNameTree();
//				break;
			case "fn:IsAssociatedFile":
				isAssociatedFile();
				break;
			case "fn:IsDictionary":
				isDictionary();
				break;
			case "fn:IsEncryptedWrapper":
				isEncryptedWrapped();
				break;
			case "fn:IsFieldName":
				isFieldName();
				break;
			case "fn:IsHexString":
				isHexString();
				break;
			case "fn:IsInArray":
				isInArray();
				break;
			case "fn:IsNameTreeIndex":
				isNameTreeIndex();
				break;
			case "fn:IsNameTreeValue":
				isNameTreeValue();
				break;
			case "fn:IsNumberTreeIndex":
				isNumberTreeIndex();
				break;
			case "fn:IsNumberTreeValue":
				isNumberTreeValue();
				break;
//			case "fn:IsLastInNumberFormatArray":
//				break;
//			case "fn:IsMeaningful":
//				break;
			case "fn:IsPDFTagged":
				isPDFTagged();
				break;
			case IS_PDF_VERSION_PREDICATE:
				isPDFVersion();
				break;
			case "fn:IsPresent":
				isPresent();
				break;
			case IS_REQUIRED_PREDICATE:
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
			case SINCE_VERSION_PREDICATE:
				sinceVersion();
				break;
			case "fn:StreamLength":
				streamLength();
				break;
			case "fn:StringLength":
				stringLength();
				break;
			case VALUE_ONLY_WHEN_PREDICATE:
				valueOnlyWhen();
				break;
			default:
				StringBuilder result = new StringBuilder();
				if (!token.getString().isEmpty()) {
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
		if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
			return false;
		}
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

	public static boolean containsSquareBrackets(String str) {
		if (str.charAt(0) != '[' || str.charAt(str.length() - 1) != ']') {
			return false;
		}
		int counter = 0;
		for (int i = 0; i < str.length() - 1; i++) {
			if (str.charAt(i) == '[') {
				counter++;
			} else if (str.charAt(i) == ']') {
				counter--;
			}
			if (counter <= 0) {
				return false;
			}
		}
		return true;
	}

	public static String removeSquareBrackets(String str) {
		if (containsSquareBrackets(str)) {
			System.out.println(str);
			return str.substring(1, str.length() - 1);
		}
		return str;
	}
	
	public static String removeBrackets(String str) {
		if (containsBrackets(str)) {
			return str.substring(1, str.length() - 1);
		}
		return str;
	}

	public static String addBrackets(String str) {
		if (containsBrackets(str) || !str.contains(" ") || isSinglePredicate(str)) {
			return str;
		}
		return "(" + str + ")";
	}

	private static boolean isSinglePredicate(String str) {
		return str.startsWith(PREDICATE_PREFIX) && containsBrackets(str.substring(str.indexOf("(")));
	}

	private void alwaysUnencrypted() {
		output.push(Constants.TRUE);//todo
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
		} else if (Entry.isComplexEntry(arrayEntryName)) {
			String argument = getPropertyOrMethodName(Entry.getArrayLengthPropertyName(arrayEntryName));
			output.push(new Part(argument, arrayEntryName, Type.ARRAY));
			object.getArraySizeProperties().add(arrayEntryName);
			object.getComplexObjectProperties().add(arrayEntryName);
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

	protected void beforeVersion() {
		if (arguments.size() < 1) {
			throw new RuntimeException("Invalid number of arguments of beforeVersion");
		}
		PDFVersion version = PDFVersion.getPDFVersion(getEntryName(arguments.get(0).getString()));
		if (isDefault()) {
			if (PDFVersion.compare(this.version, version) < 0) {
				if (arguments.size() == 2) {
					processTokens(Constants.TRUE, "?", type.getCreationCOSObject(arguments.get(1).getString()), ":");
				}
			} else {
				output.push("");
			}
			return;
		}
		if (PDFVersion.compare(this.version, version) < 0) {
			if (arguments.size() == 1) {
				output.push(Constants.TRUE);
			} else {
				output.push(getNewPart(arguments.subList(1, arguments.size())));
			}
		} else {
			if (arguments.size() == 1) {
				output.push(Constants.FALSE);
			} else {
				output.push(Constants.UNDEFINED);
			}
		}
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

	private void defaultValue() {
		if (arguments.size() < 2) {
			throw new RuntimeException("Invalid number of arguments of defaultValue");
		}
		if (!isDefault()) {
			throw new RuntimeException("defaultValue used not in DefaultValue column");
		}
		processTokens(getNewPart(arguments.subList(0, arguments.size() - 1)), "?",
				type.getCreationCOSObject(arguments.get(arguments.size() - 1).getString()), ":");
	}

	private void deprecated() {
		if (arguments.size() < 2) {
			throw new RuntimeException("Invalid number of arguments of " + DEPRECATED_PREDICATE);
		}
		if (isValue()) {
			output.push(removeQuotes(arguments.get(1).getString()));
			return;
		}
		PDFVersion version = PDFVersion.getPDFVersion(getEntryName(arguments.get(0).getString()));
		if (PDFVersion.compare(this.version, version) >= 0) {
			Part part = getNewPart(arguments.subList(1, arguments.size()));
			output.push(new Part(DEPRECATED_PREDICATE.replace(":", "/") + part.getString()));//not need undefined
		} else {
			output.push(getNewPart(arguments.subList(1, arguments.size())));
		}
	}

	private void eval() {
		output.push(getNewPart(arguments));
	}

	private void extension() {
		if (arguments.size() < 1) {
			throw new RuntimeException("Invalid number of arguments of " + EXTENSION_PREDICATE);
		}
		if (isValue()) {
			output.push(removeQuotes(arguments.get(arguments.size() - 1).getString()));
			return;
		}
		String extensionName = removeQuotes(arguments.get(0).getString());
		if (isDefault()) {
			processTokens(getPropertyOrMethodName(Object.getHasExtensionPropertyName(extensionName)), "?",
					type.getCreationCOSObject(arguments.get(1).getString()), ":");
			Main.extensionNames.add(extensionName);
			return;
		}
		if (arguments.size() == 1) {
			output.push("(" + getPropertyOrMethodName(Object.getHasExtensionPropertyName(extensionName)) + " == true)");
			Main.extensionNames.add(extensionName);
		} else if (!isProfile || Constants.SINCE_COLUMN.equals(columnName)) {
			PDFVersion version = PDFVersion.getPDFVersion(getEntryName(arguments.get(1).getString()));
			if (version == null || PDFVersion.compare(this.version, version) >= 0) {
				output.push("(" + getPropertyOrMethodName(Object.getHasExtensionPropertyName(extensionName)) + " == true)");
				Main.extensionNames.add(extensionName);
			} else {
				output.push(Constants.FALSE);
			}
		} else {
			processTokens("(", "(", getPropertyOrMethodName(Object.getHasExtensionPropertyName(extensionName)), "==",
					Constants.TRUE, ")", "&&", getNewPart(arguments.subList(1, arguments.size())), ")");
			Main.extensionNames.add(extensionName);
		}
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
		if (arrayEntry == null && !Entry.isComplexEntry(entryName)) {
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
		if (arrayEntry == null && !Entry.isComplexEntry(entryName)) {
			throw new RuntimeException("Invalid entry name in hasSpotColorants");
		}
		output.push("(" + getPropertyOrMethodName(Entry.getEntriesStringPropertyName(entryName)) + " != null && " +
				split(getPropertyOrMethodName(Entry.getEntriesStringPropertyName(entryName)), false, colorants) + " > 0)");
		object.getEntriesStringProperties().add(entryName);
		if (Entry.isComplexEntry(entryName)) {
			object.getComplexObjectProperties().add(entryName);
		}
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
		if (keyMapEntry == null && !Entry.isComplexEntry(entryName)) {
			throw new RuntimeException("Invalid entry name in inKeyMap");
		}
		output.push("(" + split(getPropertyOrMethodName(Entry.getKeysStringPropertyName(entryName)), true,
				Collections.singletonList(getPropertyOrMethodName(entry.getTypeValuePropertyName(Type.NAME)))) + " > 0)");
		object.getKeysStringProperties().add(entryName);
		if (Entry.isComplexEntry(entryName)) {
			object.getComplexObjectProperties().add(entryName);
		}
		entry.getTypeValueProperties().add(Type.NAME);
	}

	private String split(String stringName, boolean equals, List<String> values) {
		if (isProfile) {
			return ProfileGeneration.split(stringName, equals, values);
		} else {
			return JavaGeneration.split(stringName, equals, values);
		}
	}

	private void isAssociatedFile() {
		output.push(getPropertyOrMethodName(Constants.IS_ASSOCIATED_FILE));
	}

	private void isDictionary() {
		if (arguments.size() != 1) {
			throw new RuntimeException("Invalid number of arguments of isDictionary");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry entry = object.getEntry(entryName);
		if (entry != null) {
			output.push(getPropertyOrMethodName(Entry.getHasTypePropertyName(entryName, Type.DICTIONARY)) + " == true");
			entry.setFieldNameProperty(true);
		} else if (Entry.isComplexEntry(entryName)) {
			output.push(getPropertyOrMethodName(Entry.getHasTypePropertyName(entryName, Type.DICTIONARY)) + " == true");
			object.getEntriesHasTypeProperties().put(entryName, Type.DICTIONARY);
			object.getComplexObjectProperties().add(entryName);
		} else {
			throw new RuntimeException("Invalid argument of isDictionary");
		}
	}

	private void isEncryptedWrapped() {
		output.push(getPropertyOrMethodName(Constants.IS_ENCRYPTED_WRAPPER));
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

	private void isHexString() {
		if (!arguments.isEmpty()) {
			throw new RuntimeException("Invalid number of arguments of isHexString");
		}
		output.push(getPropertyOrMethodName(entry.getIsHexStringPropertyName()) + " == true");
		entry.setHexStringProperty(true);
	}

	private void isInArray() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of isInArray");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		String arrayEntryName = getEntryName(arguments.get(1).getString());
		Entry entry = object.getEntry(entryName);
		Entry arrayEntry = object.getEntry(arrayEntryName);
		//contains("::") create separate methods
		if (entry != null || Entry.isComplexEntry(entryName) || arrayEntry != null || Entry.isComplexEntry(arrayEntryName)) {
			object.getIsInArrayProperties().add(new Pair<>(entryName, arrayEntryName));
			String argument = getPropertyOrMethodName(Object.getIsInArrayPropertyName(entryName, arrayEntryName));
			output.push(argument + " == true");
			if (Entry.isComplexEntry(entryName)) {
				object.getComplexObjectProperties().add(entryName);
			}
			if (Entry.isComplexEntry(arrayEntryName)) {
				object.getComplexObjectProperties().add(arrayEntryName);
			}
		} else {
			throw new RuntimeException("Invalid entry name in isInArray");
		}
	}
	
	private void isNameTreeIndex() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of isNameTreeIndex");
		}
		String treeEntryName = getEntryName(arguments.get(0).getString());
		String entryName = getEntryName(arguments.get(1).getString());
		Entry entry = object.getEntry(entryName);
		Entry treeEntry = object.getEntry(treeEntryName);
		if (entry != null || Entry.isComplexEntry(entryName) || treeEntry != null || Entry.isComplexEntry(treeEntryName)) {
			object.getIsNameTreeIndexProperties().add(new Pair<>(entryName, treeEntryName));
			String argument = getPropertyOrMethodName(Object.getIsNameTreeIndexPropertyName(entryName, treeEntryName));
			output.push(argument + " == true");
			if (Entry.isComplexEntry(entryName)) {
				object.getComplexObjectProperties().add(entryName);
			}
			if (Entry.isComplexEntry(treeEntryName)) {
				object.getComplexObjectProperties().add(treeEntryName);
			}
		} else {
			throw new RuntimeException("Invalid entry name in isNameTreeIndex");
		}
	}

	private void isNameTreeValue() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of isNameTreeValue");
		}
		String treeEntryName = getEntryName(arguments.get(0).getString());
		String entryName = getEntryName(arguments.get(1).getString());
		Entry entry = object.getEntry(entryName);
		Entry treeEntry = object.getEntry(treeEntryName);
		if (entry != null || Entry.isComplexEntry(entryName) || treeEntry != null || Entry.isComplexEntry(treeEntryName)) {
			object.getIsNameTreeValueProperties().add(new Pair<>(entryName, treeEntryName));
			String argument = getPropertyOrMethodName(Object.getIsNameTreeValuePropertyName(entryName, treeEntryName));
			output.push(argument + " == true");
			if (Entry.isComplexEntry(entryName)) {
				object.getComplexObjectProperties().add(entryName);
			}
			if (Entry.isComplexEntry(treeEntryName)) {
				object.getComplexObjectProperties().add(treeEntryName);
			}
		} else {
			throw new RuntimeException("Invalid entry name in isNameTreeValue");
		}
	}

	private void isNumberTreeIndex() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of isNumberTreeIndex");
		}
		String treeEntryName = getEntryName(arguments.get(0).getString());
		String entryName = getEntryName(arguments.get(1).getString());
		Entry entry = object.getEntry(entryName);
		Entry treeEntry = object.getEntry(treeEntryName);
		if (entry != null || Entry.isComplexEntry(entryName) || treeEntry != null || Entry.isComplexEntry(treeEntryName)) {
			object.getIsNumberTreeIndexProperties().add(new Pair<>(entryName, treeEntryName));
			String argument = getPropertyOrMethodName(Object.getIsNumberTreeIndexPropertyName(entryName, treeEntryName));
			output.push(argument + " == true");
			if (Entry.isComplexEntry(entryName)) {
				object.getComplexObjectProperties().add(entryName);
			}
			if (Entry.isComplexEntry(treeEntryName)) {
				object.getComplexObjectProperties().add(treeEntryName);
			}
		} else {
			throw new RuntimeException("Invalid entry name in isNumberTreeIndex");
		}
	}

	private void isNumberTreeValue() {
		if (arguments.size() != 2) {
			throw new RuntimeException("Invalid number of arguments of isNumberTreeValue");
		}
		String treeEntryName = getEntryName(arguments.get(0).getString());
		String entryName = getEntryName(arguments.get(1).getString());
		Entry entry = object.getEntry(entryName);
		Entry treeEntry = object.getEntry(treeEntryName);
		if (entry != null || Entry.isComplexEntry(entryName) || treeEntry != null || Entry.isComplexEntry(treeEntryName)) {
			object.getIsNumberTreeValueProperties().add(new Pair<>(entryName, treeEntryName));
			String argument = getPropertyOrMethodName(Object.getIsNumberTreeValuePropertyName(entryName, treeEntryName));
			output.push(argument + " == true");
			if (Entry.isComplexEntry(entryName)) {
				object.getComplexObjectProperties().add(entryName);
			}
			if (Entry.isComplexEntry(treeEntryName)) {
				object.getComplexObjectProperties().add(treeEntryName);
			}
		} else {
			throw new RuntimeException("Invalid entry name in isNumberTreeValue");
		}
	}

	private void isPresent() {
		if (arguments.size() < 1) {
			throw new RuntimeException("Invalid number of arguments of isPresent");
		}
		String entryName = getEntryName(arguments.get(0).getString());
		Entry entry = object.getEntry(entryName);
		if ((entry != null || entryName.matches(ENTRY_REGEX)) &&
				!arguments.get(0).getString().contains("@")) {
			if (entry == null && !Entry.isComplexEntry(entryName)) {
				output.push("@" + entryName);
				return;
			}
			if (arguments.size() == 1) {
				processTokens("(", getPropertyOrMethodName(Entry.getContainsPropertyName(entryName)), "==", Constants.TRUE, ")");
			} else {
				processTokens("(", "(", getPropertyOrMethodName(Entry.getContainsPropertyName(entryName)), "==", Constants.FALSE,
						")", "||", "(", getNewPart(arguments.subList(1, arguments.size())), ")", "==", Constants.TRUE, ")");
			}
			if (entry != null) {
				entry.setContainsProperty(true);
			} else {
				MultiEntry multiEntry = (MultiEntry) object.getMultiObject().getEntry(entryName);
				if (multiEntry != null) {
					multiEntry.setContainsProperty(true);
				} else {
					object.getContainsEntriesProperties().add(entryName);
					int index = entryName.lastIndexOf("::");
					if (index != -1) {
						object.getComplexObjectProperties().add(entryName.substring(0, index));
					}
				}
			}
		} else {
			if (output.size() > 1 && "fn:Not".equals(output.get(output.size() - 2).getString())) {
				processTokens("(", "(", getNewPart(arguments), ")", "==", Constants.TRUE, "&&",
						getPropertyOrMethodName(this.entry.getContainsPropertyName()), "==", Constants.TRUE, ")");
				this.entry.setContainsProperty(true);
			} else {
				processTokens("(", "(", getNewPart(arguments), ")", "==", Constants.FALSE, "||",
						getPropertyOrMethodName(this.entry.getContainsPropertyName()), "==", Constants.TRUE, ")");
				this.entry.setContainsProperty(true);
			}
		}
	}

	private void isRequired() {
		if (arguments.size() < 1) {
			throw new RuntimeException("Invalid number of arguments of " + IS_REQUIRED_PREDICATE);
		}
		Part part = getNewPart(arguments);
		if (Constants.UNDEFINED.equals(part.getString())) {
			output.push(Constants.UNDEFINED);
			return;
		}
		processTokens("(", getPropertyOrMethodName(entry.getContainsPropertyName()), "==", Constants.TRUE, "||", "(",
				part, ")", "==", Constants.FALSE, ")");
		entry.setContainsProperty(true);
	}

	private void isPDFTagged() {
		output.push(getPropertyOrMethodName(Constants.IS_PDF_TAGGED));
	}

	protected void isPDFVersion() {
		if (arguments.size() < 1) {
			throw new RuntimeException("Invalid number of arguments of isPDFVersion");
		}
		PDFVersion version = PDFVersion.getPDFVersion(getEntryName(arguments.get(0).getString()));
		if (PDFVersion.compare(this.version, version) == 0) {
			if (arguments.size() == 1) {
				output.push(Constants.TRUE);
			} else {
				output.push(getNewPart(arguments.subList(1, arguments.size())));
			}
		} else {
			if (arguments.size() == 1) {
				output.push(Constants.FALSE);
			} else {
				output.push(Constants.UNDEFINED);
			}
		}
	}

	private void mustBeDirect() {
		if (arguments.size() < 1) {
			output.push("(" + getPropertyOrMethodName(entry.getIndirectPropertyName()) + " == false)");
		} else {
			processTokens("(", getPropertyOrMethodName(entry.getIndirectPropertyName()), "==", Constants.FALSE, "||", "(",
					getNewPart(arguments), ")", "==", Constants.FALSE, ")");
		}
		entry.setIndirectProperty(true);
	}

	private void mustBeIndirect() {
		if (arguments.size() < 1) {
			processTokens("(", getPropertyOrMethodName(entry.getIndirectPropertyName()), "==", Constants.TRUE, ")");
		} else {
			processTokens("(", getPropertyOrMethodName(entry.getIndirectPropertyName()), "==", Constants.TRUE, "||", "(",
					getNewPart(arguments), ")", "==", Constants.FALSE, ")");
		}
		entry.setIndirectProperty(true);
	}

	private void noCycle() {
		output.push("(" + getPropertyOrMethodName(entry.getHasCyclePropertyName()) + " == false)");
		entry.setHasCycleProperty(true);
	}

	private void not() {
		processTokens("(", getNewPart(arguments), ")", "!=", Constants.TRUE);
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
		String entryName = getEntryName(arguments.get(0).getString());
		Entry entry = object.getEntry(entryName);
		if (entry == null && !Entry.isComplexEntry(entryName)) {
			throw new RuntimeException("Invalid entryName in pageProperty");
		}
		output.push("page::" + entryName + "::" + getEntryName(arguments.get(1).getString()));
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

	private void requiredValue() {
		if (arguments.size() < 2) {
			throw new RuntimeException("Invalid number of arguments of " + REQUIRED_VALUE_PREDICATE);
		}
		String value = removeQuotes(arguments.get(arguments.size() - 1).getString());
		if (isValue()) {
			output.push(value);
			return;
		}
		entry.addTypeValueProperty(type);
		processTokens("(", "(", getNewPart(arguments.subList(0, arguments.size() - 1)), ")", "==", Constants.FALSE, "||",
				getPropertyOrMethodName(entry.getTypeValuePropertyName(type)), "==", type.getValueWithSeparator(value), ")");
	}

	protected void sinceVersion() {
		if (arguments.size() < 1) {
			throw new RuntimeException("Invalid number of arguments of sinceVersion");
		}
		PDFVersion version = PDFVersion.getPDFVersion(getEntryName(arguments.get(0).getString()));
		if (PDFVersion.compare(this.version, version) >= 0) {
			if (arguments.size() == 1) {
				output.push(Constants.TRUE);
			} else {
				output.push(getNewPart(arguments.subList(1, arguments.size())));
			}
		} else {
			if (arguments.size() == 1) {
				output.push(Constants.FALSE);
			} else {
				output.push(Constants.UNDEFINED);
			}
		}
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

	private void valueOnlyWhen() {
		if (arguments.size() < 2) {
			throw new RuntimeException("Invalid number of arguments of " + VALUE_ONLY_WHEN_PREDICATE);
		}
		String value = removeQuotes(arguments.get(0).getString());
		if (isValue()) {
			output.push(value);
			return;
		}
		entry.addTypeValueProperty(type);
		processTokens("(", "(", getNewPart(arguments.subList(1, arguments.size())), ")", "==", Constants.TRUE, "||",
				getPropertyOrMethodName(entry.getTypeValuePropertyName(type)), "!=", type.getValueWithSeparator(value), ")");
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

	protected String addQuotes(String argument) {
		if (!isDescription && argument.matches(ENTRY_REGEX) && !argument.startsWith("@") && !argument.startsWith(PREDICATE_PREFIX) &&
				!Entry.isComplexEntry(argument) && !argument.matches("-?" + Constants.NUMBER_REGEX) &&
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

	public Part getNewPart(List<Part> parts) {
		StringBuilder result = new StringBuilder();
		Map<String, Type> undefinedEntries = new HashMap<>();
		if (!parts.isEmpty()) {
			for (Part part : parts) {
				if (part.getString().isEmpty()) {
					continue;
				}
				if (result.length() > 0 && (",".equals(part.getString()) || ")".equals(part.getString()))) {
					result.deleteCharAt(result.length() - 1);
				}
				Part argument = processArgument(part.getString());
				result.append(argument.getString());
				undefinedEntries.putAll(argument.getUndefinedEntries());
				if (!part.getString().endsWith("(")) {
					result.append(" ");
				}
				undefinedEntries.putAll(part.getUndefinedEntries());
			}
			result.deleteCharAt(result.length() - 1);
		}
		return new Part(result.toString(), undefinedEntries);
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
		return getPredicateLastArgument(predicate, true);
	}

	public static String getPredicateLastArgument(String predicate, boolean removeBrackets) {
		int index = predicate.lastIndexOf(",");
		int lastIndex = predicate.lastIndexOf(")");
		if (removeBrackets) {
			while (predicate.charAt(lastIndex) == ')') {
				lastIndex--;
			}
		} else {
			lastIndex--;
		}
		return predicate.substring(index + 1, lastIndex + 1).trim();
	}

	public static String getPredicateFirstArgument(String predicate) {
		int index = predicate.indexOf("(");
		int lastIndex = predicate.lastIndexOf(",");
		return predicate.substring(index + 1, lastIndex).trim();
	}

	public static String getPredicateArgument(String string, String predicate) {
		return string.substring(predicate.length() + 1, string.length() - 1);
	}

	private Part processArgument(String argument) {
		if (isDescription || argument.contains(" ")) {
			return new Part(argument);
		}
		if (!argument.startsWith("@")) {
			return processComplexArgument(argument);
		}
		String entryName = argument.substring(1);
		if (Constants.STAR.equals(entryName)) {
			entryName = Constants.CURRENT_ENTRY;
		}
		if (entryName.equals(entry.getName())) {
			if (!type.isPropertyType()) {
				return new Part(argument);
			}
			entry.addTypeValueProperty(type);
			return new Part(getPropertyOrMethodName(entry.getTypeValuePropertyName(type)));
		}
		Entry entry = object.getEntry(entryName);
		if (entry != null && entry.getUniqPropertyTypes().size() != 1) {
			LOGGER.log(Level.WARNING, getString() + " " + entry.getName() + " several property types");
		}
		if (entry == null || entry.getUniqPropertyTypes().size() != 1) {
			return new Part(argument);
		}
		Type type = entry.getUniqPropertyTypes().iterator().next();
		entry.addTypeValueProperty(type);
		String property = getPropertyOrMethodName(entry.getTypeValuePropertyName(type));
		Part part = new Part(property);
		if (type == Type.INTEGER || type == Type.NUMBER) {
			part.getUndefinedEntries().put(entry.getName(), type);
			entry.addHasTypeProperty(type);
		}
		return part;
	}

	private Part processComplexArgument(String argument) {
		if (argument.startsWith("@") || argument.startsWith(PREDICATE_PREFIX) ||
				argument.matches(Constants.NUMBER_REGEX) || Constants.STAR.equals(argument) ||
				argument.matches(Constants.DOUBLE_REGEX) || Constants.TRUE.equals(argument) || Constants.FALSE.equals(argument)) {
			return new Part(argument);
		}
		List<String> array = new ArrayList<>(Arrays.asList(argument.split("::")));
		if (array.size() < 2) {
			return new Part(argument);
		}
		List<Object> currentObjects = new LinkedList<>();
		int index = calculateInitialObjects(array, currentObjects);
		Part part = calculateIntermediateObjects(index, array, currentObjects, argument);
		if (part != null) {
			return part;
		}
		return calculateFinalValue(currentObjects, array, argument);
	}

	private int calculateInitialObjects(List<String> array, List<Object> initialObjects) {
		int index = 0;
		if (Constants.TRAILER.equals(array.get(0))) {
			index += 1;
			if (Constants.CATALOG.equals(array.get(1))) {
				initialObjects.add(Main.objectIdMap.get(Constants.CATALOG));
				index += 1;
			} else {
				initialObjects.add(Main.objectIdMap.get(Constants.FILE_TRAILER));
			}
		} else if (Constants.PAGE.equals(array.get(0))) {
			index += 2;
			initialObjects.add(Main.objectIdMap.get(Constants.PAGE_OBJECT));
		} else if (Constants.CURRENT_ENTRY.equals(entry.getName()) && Constants.STAR.equals(array.get(0))) {
			initialObjects.add(object.getMultiObject());
			index += 1;
		} else {
			initialObjects.add(object.getMultiObject());
			while (Constants.PARENT.equals(array.get(index))) {
				List<Object> futureObjects = new LinkedList<>();
				for (Object currentObject : initialObjects) {
					for (String parentName : currentObject.getPossibleParents()) {
						Object parent = Main.objectIdMap.get(parentName);
						if (parent != null) {
							futureObjects.add(parent);
						}
					}
				}
				initialObjects.clear();
				initialObjects.addAll(futureObjects);
				index += 1;
			}
		}
		return index;
	}

	private Part calculateIntermediateObjects(int index, List<String> array, List<Object> currentObjects, String argument) {
		for (; index < array.size() - 1; index++) {
			List<Object> futureObjects = new LinkedList<>();
			String entryName = array.get(index);
			for (Object currentObject : currentObjects) {
				if (entryName.matches(Constants.NUMBER_REGEX) && !currentObject.getEntriesNames().contains(entryName) &&
						currentObject.getEntriesNames().contains(Constants.STAR)) {
					entryName = Constants.STAR;
				}
				if (Constants.STAR.equals(entryName)) {
					array.add(index + 1, Constants.CURRENT_ENTRY);
				}
				Entry entry = currentObject.getEntry(entryName);
				if (entry == null) {
					continue;
				}
				if (entry.getUniqActiveTypes().size() == 1 && index == array.size() - 2) {
					Type type = entry.getUniqActiveTypes().iterator().next();
					if (type == Type.RECTANGLE || type == Type.MATRIX) {
						Part newPart = calculateFinalValueForMatrixOrRectangle(array, argument);
						if (newPart != null) {
							return newPart;
						}
					}
				}
				for (Type type : entry.getUniqLinkTypes()) {
					for (String linkName : entry.getLinks(type)) {
						Object futureObject = Main.objectIdMap.get(linkName);
						if (futureObject != null) {
							futureObjects.add(futureObject);
						}
					}
				}
			}
			if (!futureObjects.isEmpty() || !Constants.CURRENT_ENTRY.equals(entryName)) {
				currentObjects.clear();
				currentObjects.addAll(futureObjects);
			}
		}
		return null;
	}

	private Part calculateFinalValueForMatrixOrRectangle(List<String> array, String argument) {
		String numberEntryName = array.get(array.size() - 1);
		String collectionName = argument.substring(0, argument.length() - numberEntryName.length() - 2);
		if (numberEntryName.startsWith("@")) {
			numberEntryName = numberEntryName.substring(1);
		}
		if (numberEntryName.matches(Constants.NUMBER_REGEX)) {
			object.getEntriesValuesProperties().put(argument, Type.NUMBER);
			Part part = new Part(getPropertyOrMethodName(Entry.getTypeValuePropertyName(argument,
					Type.NUMBER)));
			if (array.size() > 2) {
				object.getEntriesHasTypeProperties().put(collectionName, type);
				part.getUndefinedEntries().put(collectionName, type);
			}
			object.getComplexObjectProperties().add(argument);
			return part;
		}
		return null;
	}

	private Part calculateFinalValue(List<Object> currentObjects, List<String> array, String argument) {
		String entryName = array.get(array.size() - 1);
		if (!entryName.startsWith("@")) {
			return new Part(argument);
		}
		entryName = entryName.substring(1);
		Set<Type> types = new HashSet<>();
		for (Object currentObject : currentObjects) {
			if (entryName.matches(Constants.NUMBER_REGEX) && !currentObject.getEntriesNames().contains(entryName) &&
					currentObject.getEntriesNames().contains(Constants.STAR)) {
				entryName = Constants.STAR;
			}
			Entry entry = currentObject.getEntry(entryName);
			if (entry != null) {
				types.addAll(entry.getUniqPropertyTypes());
			}
		}
		if (types.size() < 1) {
			LOGGER.log(Level.WARNING, getString() + " " + entryName + " Types not found");
		} else if (types.size() > 1) {
			LOGGER.log(Level.WARNING, getString() + " " + entryName + " Several types found");
		} else {
			Type type = types.iterator().next();
			object.getEntriesValuesProperties().put(argument, type);
			boolean createArlingtonObject = false;
			if (currentObjects.size() == 1) {
				Object currentObject = currentObjects.get(0);
				Entry entry = currentObject.getEntry(entryName);
				if (entry != null) {
					entry.getTypeValueProperties().add(type);
					if (type == Type.NUMBER || type == Type.INTEGER) {
						entry.addHasTypeProperty(type);
					}
					object.getEntryNameToArlingtonObjectMap().put(argument, currentObject.getId());
					createArlingtonObject = true;
				}
			}
			if (Entry.isComplexEntry(argument)) {
				if (createArlingtonObject) {
					object.getComplexObjectProperties().add(argument.substring(0, argument.lastIndexOf("::")));
				} else {
					object.getComplexObjectProperties().add(argument);
				}
			}
			Part part = new Part(getPropertyOrMethodName(Entry.getTypeValuePropertyName(argument, type)));
			if (type == Type.NUMBER || type == Type.INTEGER) {
				object.getEntriesHasTypeProperties().put(argument, type);
				part.getUndefinedEntries().put(argument, type);
			}
			return part;
		}
		return new Part(argument);
	}

	private String getPropertyOrMethodName(String propertyName) {
		return isProfile ? propertyName : JavaGeneration.getMethodCall(JavaGeneration.getGetterName(propertyName));
	}

	public static String removeQuotes(String argument) {
		if ((argument.startsWith("\"") && argument.endsWith("\"")) || (argument.startsWith("'") && argument.endsWith("'"))) {
			return argument.substring(1, argument.length() - 1);
		}
		return argument;
	}

	public boolean isDefault() {
		return Constants.DEFAULT_VALUE_COLUMN.equals(columnName);
	}

	private boolean isValue() {
		return Constants.VALUE.equals(columnName);
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

	protected String getString() {
		return Main.getString(version, object, entry, type) + " column name " + columnName;
	}
}
