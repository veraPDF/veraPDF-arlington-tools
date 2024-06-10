package org.verapdf.arlington.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.verapdf.arlington.Constants;
import org.verapdf.arlington.PredicatesParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ItemDeserializer extends StdDeserializer<JSONValue> {

	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String FUNCTION_NAME = "FUNC_NAME";
	private static final String KEY_VALUE = "KEY_VALUE";
	private static final String KEY_PATH = "KEY_PATH";
	private static final String PDF_PATH = "PDF_PATH";

	public ItemDeserializer() {
		this(null);
	}

	public ItemDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public JSONValue deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		if (node.isDouble()) {
			return new JSONValue(node.asDouble());
		}
		if (node.isInt()) {
			return new JSONValue(node.asInt());
		}
		if (node.isTextual()) {
			return new JSONValue(node.asText());
		}
		if (node.isBoolean()) {
			return new JSONValue(node.asBoolean());
		}
		if (node.isArray()) {
			if (isFunction(node)) {
				return new JSONValue(getString(node, false));
			} else {
				List<String> result = new LinkedList<>();
				for (Iterator<JsonNode> it = node.elements(); it.hasNext();) {
					JsonNode child = it.next();
					result.add(child.asText());
				}
				return new JSONValue(result);
			}
		}
		if (node.isContainerNode()) {
			return null;
		}
		return new JSONValue(new ArrayList<>());
	}

	public boolean isFunction(JsonNode node) {
		if (node.isArray()) {
			for (Iterator<JsonNode> it = node.elements(); it.hasNext();) {
				JsonNode child = it.next();
				if (child.isArray() && isFunction(child)) {
					return true;
				}
				if (child.isContainerNode()) {
					JsonNode type = child.get(TYPE);
					if (type != null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getString(JsonNode node, boolean isArguments) {
		if (!node.isArray()) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		boolean hasOneElement = true;
		Iterator<JsonNode> iterator = node.elements();
		if (iterator.hasNext()) {
			JsonNode firstChild = iterator.next();
			if (iterator.hasNext()) {
				hasOneElement = false;
				if (firstChild.get(TYPE) != null && FUNCTION_NAME.equals(firstChild.get(TYPE).asText())) {
					JsonNode secondChild = iterator.next();
					if (secondChild.isArray() && !iterator.hasNext()) {
						hasOneElement = true;
					}
				}
			}
		}
		if (isArguments) {
			hasOneElement = true;
		}
		if (!hasOneElement) {
			stringBuilder.append("(");
		}
		boolean previousChildIsFunction = false;
		boolean previousChildIsToken = false;
		boolean previousChildIsPath = false;
		boolean previousChildIsValue = false;
		boolean isFirstElement = true;
		boolean previousChildIsNumberKeyValue = false;
		for (iterator = node.elements(); iterator.hasNext();) {
			boolean isFunction = false;
			boolean isToken = false;
			boolean isKeyPath = false;
			boolean isNumberKeyValue = false;
			boolean isValue = false;
			String stringValue;
			JsonNode child = iterator.next();
			if (child.isArray()) {
				stringValue = getString(child, previousChildIsFunction);
			} else if (child.isContainerNode()) {
				JsonNode value = child.get(VALUE);
				stringValue = value != null ? value.asText() : child.asText();
				JsonNode type = child.get(TYPE);
				if (type != null) {
					if (FUNCTION_NAME.equals(type.asText())) {
						isFunction = true;
					} else if (!KEY_VALUE.equals(type.asText())) {
						isToken = true;
					}
					if (KEY_VALUE.equals(type.asText())) {
						isValue = true;
						if (value.asText().matches("@" + Constants.NUMBER_REGEX)) {
							isNumberKeyValue = true;
						}
					}
					if (KEY_PATH.equals(type.asText()) || PDF_PATH.equals(type.asText())) {
						isKeyPath = true;
					}
				}
			} else {
				stringValue = child.asText();
				if (Constants.STAR.equals(child.asText()) && previousChildIsNumberKeyValue) {
					removeTrailingSpace(stringBuilder);
				}
			}
			if (isArguments && (!isToken || (isKeyPath && previousChildIsValue)) && !previousChildIsToken && !isFirstElement &&
					(!Constants.STAR.equals(child.asText()) || !previousChildIsNumberKeyValue)) {
				removeTrailingSpace(stringBuilder);
				stringBuilder.append(", ");
			}
			if (!stringBuilder.toString().endsWith(" ") && PredicatesParser.isOperator(stringValue)) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(stringValue);
			if (PredicatesParser.isOperator(stringValue)) {
				stringBuilder.append(" ");
			} else if (!isKeyPath && !previousChildIsPath && !isFunction && iterator.hasNext()) {
				stringBuilder.append(" ");
			}
			previousChildIsPath = isKeyPath;
			previousChildIsNumberKeyValue = isNumberKeyValue;
			previousChildIsValue = isValue;
			previousChildIsFunction = isFunction;
			previousChildIsToken = isToken;
			isFirstElement = false;
		}
		if (isArguments || !hasOneElement) {
			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}

	private static void removeTrailingSpace(StringBuilder stringBuilder) {
		if (stringBuilder.length() > 0 && stringBuilder.toString().charAt(stringBuilder.length() - 1) == ' ') {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
	}
}