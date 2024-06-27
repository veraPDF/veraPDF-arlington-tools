package org.verapdf.arlington;

import org.verapdf.arlington.linkHelpers.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Links {

	private static final Logger LOGGER = Logger.getLogger(Links.class.getCanonicalName());

	private static Map<String, String> getLinksEntries(Object object) {
		Map<String, String> entries = new HashMap<>();
		for (Entry entry : object.getEntries()) {
			if (entry.isNumberWithStar()) {
				continue;
			}
			Set<Type> types = entry.getUniqLinkTypes();
			if (types.isEmpty()) {
				continue;
			}
			String linkType = Constants.OBJECT;
			if (types.size() == 1) {
				List<String> links = entry.getLinks(types.iterator().next());
				if (links.size() == 1) {
					linkType = Object.getModelType(links.iterator().next());
				}
			}
			entries.put(entry.getName(), linkType);
		}
		return entries;
	}

	public static void addLinks(MultiObject multiObject) {
		SortedMap<String, String> commonEntries = new TreeMap<>();
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(multiObject.getId());
			if (object == null) {
				continue;
			}
			Map<String, String> entries = getLinksEntries(object);
			for (Map.Entry<String, String> mapEntry : entries.entrySet()) {
				if (commonEntries.containsKey(mapEntry.getKey())) {
					if (!Objects.equals(commonEntries.get(mapEntry.getKey()), mapEntry.getValue())) {
						commonEntries.put(mapEntry.getKey(), Constants.OBJECT);
					}
				} else {
					commonEntries.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
		}
		if (commonEntries.isEmpty()) {
			return;
		}
		for (Map.Entry<String, String> mapEntry : commonEntries.entrySet()) {
			ModelGeneration.addLink(getLinkName(mapEntry.getKey()), mapEntry.getValue(),
					Constants.STAR.equals(mapEntry.getKey()) ? Constants.STAR : "?");
		}
		multiObject.getJavaGeneration().addgetLinkedObjectsMethod(commonEntries);
		for (Map.Entry<String, String> mapEntry : commonEntries.entrySet()) {
			generateLinkGetters(mapEntry, multiObject);
		}
	}

	public static void generateLinkGetters(Map.Entry<String, String> mapEntry, MultiObject multiObject) {
		String entryName = mapEntry.getKey();
		List<List<PDFVersion>> versions = getLinkVersions(multiObject.getId(), entryName);
		multiObject.getJavaGeneration().addCommonGetLink(multiObject.getObjectName(), entryName, mapEntry.getValue(), versions);
		for (List<PDFVersion> versionsList : versions) {
			PDFVersion version = versionsList.get(0);
			Object object = version.getObjectIdMap().get(multiObject.getId());
			if (object == null) {
				continue;
			}
			Entry entry = object.getEntry(entryName);
			if (entry == null) {
				continue;
			}
			if (entry.isStar()) {
				object.getJavaGeneration().addMultiLink(object, entry, mapEntry.getValue(), version);
			} else if (Constants.SUB_ARRAYS.equals(entryName)) {
				object.getJavaGeneration().addSubArrayLink(object, entry, mapEntry.getValue(), version);
			} else {
				object.getJavaGeneration().addOneLink(object, entry, mapEntry.getValue(), version);
			}
			for (Type type : entry.getUniqLinkTypes()) {
				Set<String> correctLinks = entry.getLinksWithoutPredicatesSet(type);
				if (correctLinks.size() <= 1) {
					continue;
				}
				Map<String, LinkHelper> map = LinkHelper.getMap(correctLinks);
				if (map == null) {
					continue;
				}
				addGetter(map, object, entry, type, version, 0, "");
			}
		}
	}

	public static void addGetter(Map<String, LinkHelper> map, Object object, Entry entry,
								  Type type, PDFVersion version, Integer index, String methodNamePostfix) {
		LinkHelper linkHelper = map.values().iterator().next();
		if (linkHelper instanceof DifferentKeysValuesLinkHelper)  {
			object.getJavaGeneration().addLinkGetterByKeyValues(map, object, entry, type, version, index, methodNamePostfix);
		} else if (linkHelper instanceof SizeLinkHelper) {
			object.getJavaGeneration().addLinkGetterBySize(map, object, entry, type, version);
		} else if (linkHelper instanceof DifferentKeysLinkHelper) {
			object.getJavaGeneration().addLinkGetterByDifferentKeys(map, object, entry, type, version, index, methodNamePostfix);
		} else if (linkHelper instanceof KeyNameLinkHelper) {
			object.getJavaGeneration().addLinkGetterByKeyName(map, object, entry, type, version);
		}
	}

	public static List<List<PDFVersion>> getLinkVersions(String objectName, String entryName) {
		List<List<PDFVersion>> versions = new LinkedList<>();
		for (PDFVersion version : PDFVersion.values()) {
			Object object = version.getObjectIdMap().get(objectName);
			if (object == null) {
				continue;
			}
			Entry entry = object.getEntry(entryName);
			if (entry == null) {
				continue;
			}
			boolean flag = false;
			for (List<PDFVersion> versionsList : versions) {
				PDFVersion currentVersion = versionsList.get(0);
				Object object2 = currentVersion.getObjectIdMap().get(objectName);
				Entry entry2 = object2.getEntry(entryName);
				if (sameLinks(object, object2, entry, entry2)) {
					versionsList.add(version);
					flag = true;
				}
			}
			if (!flag) {
				List<PDFVersion> versionsList = new LinkedList<>();
				versionsList.add(version);
				versions.add(versionsList);
			}
		}
		return versions;
	}

	public static String getLinkName(String entryName) {
		if (Constants.STAR.equals(entryName)) {
			return "Entries";
		}
		if (Constants.CURRENT_ENTRY.equals(entryName)) {
			return Type.ENTRY.getType();
		}
		return Entry.getCorrectEntryName(entryName);
	}

	private static boolean sameLinks(Object object1, Object object2, Entry entry1, Entry entry2) {
		if (!entry1.getUniqLinkTypes().equals(entry2.getUniqLinkTypes())) {
			return false;
		}
		for (Type type : entry1.getUniqLinkTypes()) {
			if (!entry1.getLinks(type).equals(entry2.getLinks(type))) {
				return false;
			}
		}
		if (object1.isDictionary() && Constants.STAR.equals(entry1.getName())) {
			if (!object1.getEntriesNames().equals(object2.getEntriesNames())) {
				return false;
			}
		}
		return true;
	}

	public static SortedMap<String, Set<String>> getDifferentKeysLinksMap(List<String> correctLinks,
																		  Map<String, LinkHelper> map) {
		SortedMap<String, Set<String>> resultMap = new TreeMap<>();
		for (String link : correctLinks) {
			DifferentKeysLinkHelper helper = (DifferentKeysLinkHelper)map.get(link);
			for (String keyName : helper.getKeyNames()) {
				if (resultMap.containsKey(keyName)) {
					resultMap.get(keyName).add(link);
				} else {
					Set<String> valuesMap = new HashSet<>();
					valuesMap.add(link);
					resultMap.put(keyName, valuesMap);
				}
			}
		}
		return resultMap;
	}

	public static SortedMap<String, Set<String>> getDifferentKeysValuesLinksMap(List<String> correctLinks,
																				Map<String, LinkHelper> map) {
		SortedMap<String, Set<String>> resultMap = new TreeMap<>();
		for (String link : correctLinks) {
			DifferentKeysValuesLinkHelper helper = (DifferentKeysValuesLinkHelper)map.get(link);
			for (String keyValue : helper.getKey().getKeyValues()) {
				if (resultMap.containsKey(keyValue)) {
					resultMap.get(keyValue).add(link);
				} else {
					Set<String> valuesMap = new HashSet<>();
					valuesMap.add(link);
					resultMap.put(keyValue, valuesMap);
				}
			}
		}
		return resultMap;
	}

	public static SortedMap<Integer, String> getSizeLinksMap(PDFVersion version, Object object, Entry entry, Type type,
															 List<String> correctLinks, Map<String, LinkHelper> map) {
		SortedMap<Integer, String> resultMap = new TreeMap<>();
		for (String link : correctLinks) {
			if (link.contains(PredicatesParser.PREDICATE_PREFIX)) {
				LOGGER.log(Level.WARNING, Main.getString(version, object, entry, type) + " link contains predicate");
			}
			SizeLinkHelper helper = (SizeLinkHelper)map.get(link);
			if (helper != null) {
				resultMap.put(helper.getSize(), link);
			}
		}
		return resultMap;
	}
}
