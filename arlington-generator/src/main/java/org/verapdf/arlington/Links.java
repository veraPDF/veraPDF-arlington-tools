package org.verapdf.arlington;

import java.util.*;
import java.util.stream.Collectors;

public class Links {

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

	public static List<List<PDFVersion>> getVersions(String objectName, String entryName) {
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
			return "Entry";
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
}
