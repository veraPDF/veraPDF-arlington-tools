package org.verapdf.arlington;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PredicatesParserDescription extends PredicatesParser {

	private static final Logger LOGGER = Logger.getLogger(PredicatesParserDescription.class.getCanonicalName());

	public PredicatesParserDescription(Object object, Entry entry, PDFVersion version, Type type, String columnName) {
		super(object, entry, version, type, columnName, true);
		isDescription = true;
	}

	@Override
	public String parse(String str) {
		try {
			str = str.replace("fn:IsPresent(AP::N::*)", "fn:IsDictionary(AP::N)")
					.replace("fn:IsPresent(AP::R::*)", "fn:IsDictionary(AP::R)")
					.replace("fn:IsPresent(AP::D::*)", "fn:IsDictionary(AP::D)");
			String result = parseString(str);
			if (result == null) {
				return str;
			}
			return result;
		} catch (RuntimeException e) {
			LOGGER.log(Level.WARNING, getString() + ": " + str + ". Error: " + e.getMessage());
		}
		return str;
	}

	@Override
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
			case "==":
				equal(firstArgument, secondArgument);
				break;
			case "!=":
				nonEqual(firstArgument, secondArgument);
				break;
			default:
				output.add(firstArgument.getString() + " " + operatorName + " " + secondArgument.getString());
		}
	}

	@Override
	protected void executeFunction(String functionName) {
		arguments = new LinkedList<>();
		while (!"(".equals(output.peek().getString())) {
			arguments.add(0, output.pop());
		}
		output.pop(); //pop "("
		Part token = !output.isEmpty() ? output.pop() : new Part("");//pop possible functionName
		switch (functionName) {
			case BEFORE_VERSION_PREDICATE:
				beforeVersion();
				break;
			case IS_PDF_VERSION_PREDICATE:
				isPDFVersion();
				break;
			case SINCE_VERSION_PREDICATE:
				sinceVersion();
				break;
			default:
				if (functionName.startsWith(PREDICATE_PREFIX)) {
					String argumentsString = "(" + getArgumentsString() + ")";
					output.push(functionName + argumentsString);
				} else {
					String argumentsString = PredicatesParser.addBrackets(getArgumentsString());
					if (!token.getString().isEmpty()) {
						output.push(token);
					}
					if (!functionName.isEmpty()) {
						operators.push(functionName);
					}
					output.push(argumentsString);
				}
				break;
		}
	}

	private String getArgumentsString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (arguments.isEmpty()) {
			return "";
		}
		if (arguments.size() == 1) {
			return arguments.get(0).getString();
		}
		for (Part argument : arguments) {
			if (Constants.STAR.equals(argument.getString())) {
				stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
			}
			stringBuilder.append(argument.getString());
			if (!Constants.STAR.equals(argument.getString())) {
				stringBuilder.append(",");
			}
			stringBuilder.append(" ");
		}
		stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
		return stringBuilder.toString();
	}
}

