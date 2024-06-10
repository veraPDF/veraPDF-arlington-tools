package org.verapdf.arlington;

import java.util.*;

public class ObjectCreation {

    public static void addMergedWidgetAnnotFields() {
        for (PDFVersion version : PDFVersion.values()) {
            Object widgetAnnot = version.getObjectIdMap().get("AnnotWidget");
            if (widgetAnnot == null) {
                continue;
            }
            Set<Object> newObjects = new HashSet<>();
            for (Object object : version.getObjectIdMap().values()) {
                if (Object.isField(object.getObjectName())) {
                    SortedSet<Entry> entries = new TreeSet<>();
                    for (Entry entry : object.getEntries()) {
                        if (!"Kids".equals(entry.getName())) {
                            entries.add(new Entry(entry));
                        }
                    }
                    for (Entry entry : widgetAnnot.getEntries()) {
                        if (!"Kids".equals(entry.getName())) {
                            entries.add(new Entry(entry));
                        }
                    }
                    String newObjectName = Constants.ANNOT_WIDGET + object.getObjectName();
                    newObjects.add(new Object(newObjectName, entries));
                    Main.objectNames.add(newObjectName);
                }
            }
            for (Object object : newObjects) {
                version.getObjectIdMap().put(object.getObjectName(), object);
            }
        }
        for (PDFVersion version : PDFVersion.values()) {
            for (Object object : version.getObjectIdMap().values()) {
                for (Entry entry : object.getEntries()) {
                    for (Type type : Type.values()) {
                        List<String> links = entry.getLinksWithoutPredicatesList(type);
                        for (String link : links) {
                            if (Object.isField(link)) {
                                entry.getLinks().get(type).add(Constants.ANNOT_WIDGET + link);
                            }
                        }
                    }
                }
            }
        }
        for (PDFVersion version : PDFVersion.values()) {
            for (Object object : version.getObjectIdMap().values()) {
                for (Entry entry : object.getEntries()) {
                    for (Type type : Type.values()) {
                        List<String> links = entry.getLinksWithoutPredicatesList(type);
                        for (String link : links) {
                            if (link.equals(Constants.ANNOT_WIDGET)) {
                                entry.getLinks().get(type).addAll(Constants.widgetAnnotFieldsNames);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void addDocument() {
        for (PDFVersion version : PDFVersion.values()) {
            version.getObjectIdMap().put(Constants.DOCUMENT, new Object(Constants.DOCUMENT, new TreeSet<>()));
            Main.objectNames.add(Constants.DOCUMENT);
        }

        addLinearizationDictionary();
        addStreamObjects();
        addXRefStreamToDocument();
        addFileTrailer();
    }

    public static void addXRefStreamToFileTrailer() {
        Entry entry = new Entry();
        entry.setName(Constants.XREF_STREAM);
        entry.getTypes().add(Type.STREAM);
        List<String> links = new LinkedList<>();
        links.add(Constants.XREF_STREAM);
        entry.getLinks().put(Type.STREAM, links);
        entry.getTypesPredicates().add("");
        entry.setRequired("");
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
                Object object = version.getObjectIdMap().get(Constants.FILE_TRAILER);
                object.addEntry(entry);
            }
        }
    }

    private static void addFileTrailer() {
        Entry entry = new Entry();
        entry.setName(Constants.FILE_TRAILER);
        entry.getTypes().add(Type.DICTIONARY);
        List<String> links = new LinkedList<>();
        links.add(Constants.FILE_TRAILER);
        entry.getLinks().put(Type.DICTIONARY, links);
        entry.getTypesPredicates().add("");
        entry.setRequired("");
        for (PDFVersion version : PDFVersion.values()) {
            Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
            object.addEntry(entry);
        }
    }

    private static void addXRefStreamToDocument() {
        Entry entry = new Entry();
        entry.setName(Constants.XREF_STREAM);
        entry.getTypes().add(Type.STREAM);
        List<String> links = new LinkedList<>();
        links.add(Constants.XREF_STREAM);
        entry.getLinks().put(Type.STREAM, links);
        entry.getTypesPredicates().add("");
        entry.setRequired("");
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
                Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
                object.addEntry(entry);
            }
        }
    }

    private static void addLinearizationDictionary() {
        Entry entry = new Entry();
        entry.setName(Constants.LINEARIZATION_PARAMETER_DICTIONARY);
        entry.getTypes().add(Type.DICTIONARY);
        List<String> links = new LinkedList<>();
        links.add(Constants.LINEARIZATION_PARAMETER_DICTIONARY);
        entry.getLinks().put(Type.DICTIONARY, links);
        entry.getTypesPredicates().add("");
        entry.setRequired("");
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {
                Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
                object.addEntry(entry);
            }
        }
    }

    private static void addStreamObjects() {
        Entry entry = new Entry();
        entry.setName(Constants.OBJECT_STREAMS);
        entry.getTypes().add(Type.ARRAY);
        List<String> links = new LinkedList<>();
        links.add(Constants.ARRAY_OF_OBJECT_STREAMS);
        entry.getLinks().put(Type.ARRAY, links);
        entry.getTypesPredicates().add("");
        entry.setRequired("");
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
                Object object = version.getObjectIdMap().get(Constants.DOCUMENT);
                object.addEntry(entry);
            }
        }
        Main.objectNames.add(Constants.ARRAY_OF_OBJECT_STREAMS);
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_5) >= 0) {
                Set<String> possibleParents = new HashSet<>();
                possibleParents.add(Constants.DOCUMENT);
                Entry starEntry = new Entry();
                starEntry.setName(Constants.STAR);
                starEntry.getTypes().add(Type.STREAM);
                List<String> starLinks = new LinkedList<>();
                starLinks.add(Constants.OBJECT_STREAM);
                starEntry.getLinks().put(Type.STREAM, starLinks);
                starEntry.getTypesPredicates().add("");
                starEntry.setRequired("");
                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(starEntry);
                Object object = new Object(Constants.ARRAY_OF_OBJECT_STREAMS, entries, possibleParents);
                version.getObjectIdMap().put(Constants.ARRAY_OF_OBJECT_STREAMS, object);
            }
        }
    }

    public static void addNameAndNumberTreeEntries() {
        createNameTreeNode();
        createNumberTreeNode();
        createNameTreeNodesArray();
        createNumberTreeNodesArray();
        createNameTreeNodeLimitsArray();
        createNumberTreeNodeLimitsArray();
        createNameTreeNodeNamesArray();
        createNumberTreeNodeNumsArray();
    }

    private static void createNameTreeNode() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {

                Entry kids = new Entry();
                kids.setName("Kids");
                kids.getTypes().add(Type.ARRAY);
                List<String> kidsLinks = new LinkedList<>();
                kidsLinks.add(Constants.NAME_TREE_NODES_ARRAY);
                kids.getLinks().put(Type.ARRAY, kidsLinks);
                kids.getTypesPredicates().add("");
                kids.setRequired("");
                kids.setRequired("fn:IsRequired(fn:Not(fn:IsPresent(Names)))");
                kids.getSpecialCases().put(Type.ARRAY, "fn:Not(fn:IsPresent(fn:IsPresent(Names)))");

                Entry names = new Entry();
                names.setName("Names");
                names.getTypes().add(Type.ARRAY);
                List<String> namesLinks = new LinkedList<>();
                namesLinks.add(Constants.NAME_TREE_NODE_NAMES_ARRAY);
                names.getLinks().put(Type.ARRAY, namesLinks);
                names.getTypesPredicates().add("");
                names.setRequired("");
                names.setRequired("fn:IsRequired(fn:Not(fn:IsPresent(Kids)))");
                names.getSpecialCases().put(Type.ARRAY, "fn:Not(fn:IsPresent(fn:IsPresent(Kids)))");

                Entry limits = new Entry();
                limits.setName("Limits");
                limits.getTypes().add(Type.ARRAY);
                List<String> limitsLinks = new LinkedList<>();
                limitsLinks.add(Constants.NAME_TREE_NODE_LIMITS_ARRAY);
                limits.getLinks().put(Type.ARRAY, limitsLinks);
                limits.getTypesPredicates().add("");
                limits.setRequired("");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(kids);
                entries.add(names);
                entries.add(limits);
                Object object = new Object(Constants.NAME_TREE_NODE, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NAME_TREE_NODE, object);
            }
        }
        Main.objectNames.add(Constants.NAME_TREE_NODE);
    }

    private static void createNumberTreeNode() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_3) >= 0) {

                Entry kids = new Entry();
                kids.setName("Kids");
                kids.getTypes().add(Type.ARRAY);
                List<String> kidsLinks = new LinkedList<>();
                kidsLinks.add(Constants.NUMBER_TREE_NODES_ARRAY);
                kids.getLinks().put(Type.ARRAY, kidsLinks);
                kids.getTypesPredicates().add("");
                kids.setRequired("fn:IsRequired(fn:Not(fn:IsPresent(Nums)))");
                kids.getSpecialCases().put(Type.ARRAY, "fn:Not(fn:IsPresent(fn:IsPresent(Nums)))");

                Entry numbers = new Entry();
                numbers.setName("Nums");
                numbers.getTypes().add(Type.ARRAY);
                List<String> numberLinks = new LinkedList<>();
                numberLinks.add(Constants.NUMBER_TREE_NODE_NUMS_ARRAY);
                numbers.getLinks().put(Type.ARRAY, numberLinks);
                numbers.getTypesPredicates().add("");
                numbers.setRequired("");
                numbers.setRequired("fn:IsRequired(fn:Not(fn:IsPresent(Kids)))");
                numbers.getSpecialCases().put(Type.ARRAY, "fn:Not(fn:IsPresent(fn:IsPresent(Kids))) && fn:ArraySortAscending(Nums,2)");

                Entry limits = new Entry();
                limits.setName("Limits");
                limits.getTypes().add(Type.ARRAY);
                List<String> limitsLinksList = new LinkedList<>();
                limitsLinksList.add(Constants.NUMBER_TREE_NODE_LIMITS_ARRAY);
                limits.getLinks().put(Type.ARRAY, limitsLinksList);
                limits.getTypesPredicates().add("");
                limits.setRequired("");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(kids);
                entries.add(numbers);
                entries.add(limits);
                Object object = new Object(Constants.NUMBER_TREE_NODE, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NUMBER_TREE_NODE, object);
            }
        }
        Main.objectNames.add(Constants.NUMBER_TREE_NODE);
    }

    private static void createNameTreeNodesArray() {

        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {

                Entry kids = new Entry();
                kids.setName(Constants.STAR);
                kids.getTypes().add(Type.DICTIONARY);
                List<String> list = new LinkedList<>();
                list.add(Constants.NAME_TREE_NODE);
                kids.getLinks().put(Type.DICTIONARY, list);
                kids.getTypesPredicates().add("");
                kids.setRequired("");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(kids);
                Object object = new Object(Constants.NAME_TREE_NODES_ARRAY, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NAME_TREE_NODES_ARRAY, object);
            }
        }
        Main.objectNames.add(Constants.NAME_TREE_NODES_ARRAY);
    }

    private static void createNumberTreeNodesArray() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_3) >= 0) {

                Entry kids = new Entry();
                kids.setName(Constants.STAR);
                kids.getTypes().add(Type.DICTIONARY);
                List<String> list = new LinkedList<>();
                list.add(Constants.NUMBER_TREE_NODE);
                kids.getLinks().put(Type.DICTIONARY, list);
                kids.getTypesPredicates().add("");
                kids.setRequired("");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(kids);
                Object object = new Object(Constants.NUMBER_TREE_NODES_ARRAY, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NUMBER_TREE_NODES_ARRAY, object);
            }
        }
        Main.objectNames.add(Constants.NUMBER_TREE_NODES_ARRAY);
    }

    private static void createNameTreeNodeLimitsArray() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {

                Entry first = new Entry();
                first.setName("0");
                first.getTypes().add(Type.STRING);
                first.getTypesPredicates().add("");
                first.setRequired("true");

                Entry second = new Entry();
                second.setName("1");
                second.getTypes().add(Type.STRING);
                second.getTypesPredicates().add("");
                second.setRequired("true");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(first);
                entries.add(second);
                Object object = new Object(Constants.NAME_TREE_NODE_LIMITS_ARRAY, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NAME_TREE_NODE_LIMITS_ARRAY, object);
            }
        }
        Main.objectNames.add(Constants.NAME_TREE_NODE_LIMITS_ARRAY);
    }

    private static void createNumberTreeNodeLimitsArray() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_3) >= 0) {

                Entry first = new Entry();
                first.setName("0");
                first.getTypes().add(Type.INTEGER);
                first.getTypesPredicates().add("");
                first.setRequired("true");

                Entry second = new Entry();
                second.setName("1");
                second.getTypes().add(Type.INTEGER);
                second.getTypesPredicates().add("");
                second.setRequired("true");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(first);
                entries.add(second);
                Object object = new Object(Constants.NUMBER_TREE_NODE_LIMITS_ARRAY, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NUMBER_TREE_NODE_LIMITS_ARRAY, object);
            }
        }
        Main.objectNames.add(Constants.NUMBER_TREE_NODE_LIMITS_ARRAY);
    }

    private static void createNameTreeNodeNamesArray() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {

                Entry first = new Entry();
                first.setName("0*");
                first.getTypes().add(Type.STRING);
                first.getTypesPredicates().add("");
                first.setRequired("true");

                Entry second = new Entry();
                second.setName("1*");
                second.setRequired("true");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(first);
                entries.add(second);
                Object object = new Object(Constants.NAME_TREE_NODE_NAMES_ARRAY, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NAME_TREE_NODE_NAMES_ARRAY, object);
            }
        }
        Main.objectNames.add(Constants.NAME_TREE_NODE_NAMES_ARRAY);
    }

    private static void createNumberTreeNodeNumsArray() {
        for (PDFVersion version : PDFVersion.values()) {
            if (PDFVersion.compare(version, PDFVersion.VERSION1_2) >= 0) {

                Entry first = new Entry();
                first.setName("0*");
                first.getTypes().add(Type.INTEGER);
                first.getTypesPredicates().add("");
                first.setRequired("true");

                Entry second = new Entry();
                second.setName("1*");
                second.setRequired("true");

                SortedSet<Entry> entries = new TreeSet<>();
                entries.add(first);
                entries.add(second);
                Object object = new Object(Constants.NUMBER_TREE_NODE_NUMS_ARRAY, entries, new HashSet<>());
                version.getObjectIdMap().put(Constants.NUMBER_TREE_NODE_NUMS_ARRAY, object);
            }
        }
        Main.objectNames.add(Constants.NUMBER_TREE_NODE_NUMS_ARRAY);
    }

    public static void addStarObjects() {
        for (PDFVersion version : PDFVersion.values()) {
            Set<String> newObjectsNames = new HashSet<>();
            for (String objectName : Main.objectNames) {
                Object object = version.getObjectIdMap().get(objectName);
                if (object == null) {
                    continue;
                }
                Set<Entry> newEntries = new HashSet<>();
                for (Entry entry : object.getEntries()) {
                    if (entry.getUniqLinkTypes().contains(Type.NAME_TREE)) {
                        addTreeObject(object, entry, version, Type.NAME_TREE, newEntries);
                        newObjectsNames.add(object.getId() + Type.NAME_TREE.getType() + entry.getName());
                    }
                    if (entry.getUniqLinkTypes().contains(Type.NUMBER_TREE)) {
                        addTreeObject(object, entry, version, Type.NUMBER_TREE, newEntries);
                        newObjectsNames.add(object.getId() + Type.NUMBER_TREE.getType() + entry.getName());
                    }
                }
                for (Entry newEntry : newEntries) {
                    object.addEntry(newEntry);
                }
                if (object.isArray()) {
                    addSubArrayObject(object, version, newObjectsNames);
                }
            }
            Main.objectNames.addAll(newObjectsNames);
        }
        for (PDFVersion version : PDFVersion.values()) {
            Set<String> newObjectsNames = new HashSet<>();
            for (String objectName : Main.objectNames) {
                Object object = version.getObjectIdMap().get(objectName);
                if (object == null) {
                    continue;
                }
                for (Entry entry : object.getEntries()) {
                    if (entry.isStar()) {
                        addStarEntryObject(object, entry, version);
                        newObjectsNames.add(object.getId() + Type.ENTRY.getType());
                    }
                }
            }
            Main.objectNames.addAll(newObjectsNames);
        }
    }

    private static void addTreeObject(Object object, Entry entry, PDFVersion version, Type type, Set<Entry> newEntries) {
        String newObjectName = object.getId() + type.getType() + entry.getName();
        Entry treeNodeEntry = new Entry(entry);
        Entry newEntry = new Entry();
        newEntry.setName(Constants.STAR);
        if (entry.getSpecialCase(type) != null && entry.getSpecialCase(type).contains("fn:AllowNull(" + entry.getName() + ")")) {
            newEntry.getTypes().add(Type.NULL);
            newEntry.getTypesPredicates().add(Type.NULL.getType());
        }
        for (String link : entry.getLinks(type)) {
            Object currentObject = version.getObjectIdMap().get(link);
            Type currentType = Type.DICTIONARY;
            if (currentObject.isArray()) {
                currentType = Type.ARRAY;
            } else if (currentObject.isStream()) {
                currentType = Type.STREAM;
            }
            if (!newEntry.getTypes().contains(currentType)) {
                newEntry.getTypes().add(currentType);
                newEntry.getTypesPredicates().add(currentType.getType());
                newEntry.setIndirectReference(currentType, entry.getIndirectReference(type));
            }
            List<String> links = newEntry.getLinks(currentType);
            if (links.isEmpty()) {
                links = new LinkedList<>();
                links.add(link);
                newEntry.getLinks().put(currentType, links);
            } else {
                links.add(link);
            }
        }
        entry.getLinks().clear();
        List<String> links = new LinkedList<>();
        links.add(newObjectName);
        entry.getLinks().put(type, links);
        SortedSet<Entry> entries = new TreeSet<>();
        entries.add(newEntry);
        version.getObjectIdMap().put(newObjectName, new Object(newObjectName, entries,
                object.getPossibleParents()));

        treeNodeEntry.setName(treeNodeEntry.getName() + Constants.TREE_NODE);
        treeNodeEntry.getSpecialCases().clear();
        List<String> treeNodeEntryLinks = new LinkedList<>();
        treeNodeEntryLinks.add(type == Type.NAME_TREE ? Constants.NAME_TREE_NODE : Constants.NUMBER_TREE_NODE);
        treeNodeEntry.getLinks().put(type, treeNodeEntryLinks);
        newEntries.add(treeNodeEntry);
    }

    private static void addStarEntryObject(Object object, Entry entry, PDFVersion version) {
        String newObjectName = Object.getObjectEntryName(object.getId());
        Entry newEntry = new Entry(entry);
        newEntry.setName(Constants.CURRENT_ENTRY);
        entry.setRequired(Constants.FALSE);
        List<String> links = new LinkedList<>();
        links.add(newObjectName);
        entry.getLinks().clear();
        entry.getTypes().clear();
        entry.getTypesPredicates().clear();
        entry.getPossibleValues().clear();
        entry.getIndirectReference().clear();
        entry.getLinks().put(Type.ENTRY, links);
        entry.getTypes().add(Type.ENTRY);
        SortedSet<Entry> entries = new TreeSet<>();
        entries.add(newEntry);
        version.getObjectIdMap().put(newObjectName, new Object(newObjectName, entries, object.getPossibleParents()));
    }

    private static void addSubArrayObject(Object object, PDFVersion version, Set<String> newObjectsNames) {
        List<? extends Entry> numberStarEntries = object.getNumberStarEntries();
        if (!numberStarEntries.isEmpty()) {
            String newObjectName = object.getId() + Type.SUB_ARRAY.getType();
            newObjectsNames.add(newObjectName);
            List<Integer> numbers = new LinkedList<>();
            for (Entry entry : numberStarEntries) {
                numbers.add(entry.getNumberWithStar());
            }
            Collections.sort(numbers);
            SortedSet<Entry> entries = new TreeSet<>();
            for (int i = 0; i < numbers.size(); i++) {
                Entry originalEntry = numberStarEntries.get(i);
                Entry newEntry = new Entry();
                newEntry.setName(Integer.toString(i));
                for (Map.Entry<Type, String> entry : originalEntry.getIndirectReference().entrySet()) {
                    newEntry.getIndirectReference().put(entry.getKey(), replace(entry.getValue(), numbers.get(0), numbers.size()));
                }
                newEntry.getTypes().addAll(originalEntry.getTypes());
                newEntry.getTypesPredicates().addAll(originalEntry.getTypesPredicates());
                for (Map.Entry<Type, List<String>> entry : originalEntry.getPossibleValues().entrySet()) {
                    List<String> possibleValues = new LinkedList<>();
                    for (String possibleValue : entry.getValue()) {
                        possibleValues.add(replace(possibleValue, numbers.get(0), numbers.size()));
                    }
                    newEntry.getPossibleValues().put(entry.getKey(), possibleValues);
                }
                for (Map.Entry<Type, List<String>> entry : originalEntry.getLinks().entrySet()) {
                    List<String> links = new LinkedList<>();
                    for (String link : entry.getValue()) {
                        links.add(replace(link, numbers.get(0), numbers.size()));
                    }
                    newEntry.getLinks().put(entry.getKey(), links);
                }
                for (Map.Entry<Type, String> entry : originalEntry.getIndirectReference().entrySet()) {
                    newEntry.getIndirectReference().put(entry.getKey(), replace(entry.getValue(), numbers.get(0), numbers.size()));
                }
                for (Map.Entry<Type, String> entry : originalEntry.getSpecialCases().entrySet()) {
                    newEntry.getSpecialCases().put(entry.getKey(), replace(entry.getValue(), numbers.get(0), numbers.size()));
                }
                newEntry.setRequired(replace(originalEntry.getRequired(), numbers.get(0), numbers.size()));
                entries.add(newEntry);
            }
            Entry newEntry = new Entry();
            newEntry.setName(Constants.SUB_ARRAYS);
            List<String> links = new LinkedList<>();
            links.add(newObjectName);
            newEntry.setRequired(Constants.FALSE);
            newEntry.getLinks().put(Type.SUB_ARRAY, links);
            newEntry.getTypes().add(Type.SUB_ARRAY);
            newEntry.getTypesPredicates().add(Type.SUB_ARRAY.getType());
            object.addEntry(newEntry);
            version.getObjectIdMap().put(newObjectName, new Object(newObjectName, entries, object.getPossibleParents()));
        }
    }

    private static String replace(String string, int startNumber, int numbers) {
        String newString = string;
        for (int i = 0; i < numbers; i++) {
            newString = newString.replace((i + startNumber) + Constants.STAR, Integer.toString(i));
        }
        return newString;
    }
}
