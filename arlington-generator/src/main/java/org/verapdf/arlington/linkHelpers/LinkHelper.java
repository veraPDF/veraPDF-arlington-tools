package org.verapdf.arlington.linkHelpers;

import org.verapdf.arlington.Type;

import java.util.*;

public class LinkHelper {

	public static final List<Map<String, LinkHelper>> helpersList = new LinkedList<>();

	private final String objectName;

	public LinkHelper(String objectName) {
		this.objectName = objectName;
	}

	public static Map<String, LinkHelper> getMap(Set<String> values) {
		for (Map<String, LinkHelper> map : helpersList) {
			if (map.keySet().containsAll(values)) {
				return map;
			}
		}
		return null;
	}

	public String getObjectName() {
		return objectName;
	}

	static {
		Map<String, LinkHelper> helpers;

		//actions
		helpers = new HashMap<>();
		helpers.put("ActionECMAScript", new DifferentKeysValuesLinkHelper("ActionECMAScript", new Key("S", Type.NAME,"JavaScript")));
		helpers.put("ActionGoTo3DView", new DifferentKeysValuesLinkHelper("ActionGoTo3DView", new Key("S", Type.NAME, "GoTo3DView")));
		helpers.put("ActionGoTo", new DifferentKeysValuesLinkHelper("ActionGoTo", new Key("S", Type.NAME, "GoTo")));
		helpers.put("ActionGoToDp", new DifferentKeysValuesLinkHelper("ActionGoToDp", new Key("S", Type.NAME, "GoToDp")));
		helpers.put("ActionGoToE", new DifferentKeysValuesLinkHelper("ActionGoToE", new Key("S", Type.NAME, "GoToE")));
		helpers.put("ActionGoToR", new DifferentKeysValuesLinkHelper("ActionGoToR", new Key("S", Type.NAME, "GoToR")));
		helpers.put("ActionHide", new DifferentKeysValuesLinkHelper("ActionHide", new Key("S", Type.NAME, "Hide")));
		helpers.put("ActionImportData", new DifferentKeysValuesLinkHelper("ActionImportData", new Key("S", Type.NAME, "ImportData")));
		helpers.put("ActionLaunch", new DifferentKeysValuesLinkHelper("ActionLaunch", new Key("S", Type.NAME, "Launch")));
		helpers.put("ActionMovie", new DifferentKeysValuesLinkHelper("ActionMovie", new Key("S", Type.NAME, "Movie")));
		helpers.put("ActionNamed", new DifferentKeysValuesLinkHelper("ActionNamed", new Key("S", Type.NAME, "Named")));
		helpers.put("ActionNOP", new DifferentKeysValuesLinkHelper("ActionNOP", new Key("S", Type.NAME, "NOP")));
		helpers.put("ActionRendition", new DifferentKeysValuesLinkHelper("ActionRendition", new Key("S", Type.NAME, "Rendition")));
		helpers.put("ActionResetForm", new DifferentKeysValuesLinkHelper("ActionResetForm", new Key("S", Type.NAME, "ResetForm")));
		helpers.put("ActionRichMediaExecute", new DifferentKeysValuesLinkHelper("ActionRichMediaExecute", new Key("S", Type.NAME, "RichMediaExecute")));
		helpers.put("ActionSetOCGState", new DifferentKeysValuesLinkHelper("ActionSetOCGState", new Key("S", Type.NAME, "SetOCGState")));
		helpers.put("ActionSetState", new DifferentKeysValuesLinkHelper("ActionSetState", new Key("S", Type.NAME, "SetState")));
		helpers.put("ActionSound", new DifferentKeysValuesLinkHelper("ActionSound", new Key("S", Type.NAME, "Sound")));
		helpers.put("ActionSubmitForm", new DifferentKeysValuesLinkHelper("ActionSubmitForm", new Key("S", Type.NAME, "SubmitForm")));
		helpers.put("ActionThread", new DifferentKeysValuesLinkHelper("ActionThread", new Key("S", Type.NAME, "Thread")));
		helpers.put("ActionTransition", new DifferentKeysValuesLinkHelper("ActionTransition", new Key("S", Type.NAME, "Trans")));
		helpers.put("ActionURI", new DifferentKeysValuesLinkHelper("ActionURI", new Key("S", Type.NAME, "URI")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("AnnotWidgetFieldBtnCheckbox", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnCheckbox", new Key("Ff", Type.BITMASK, 16, "0")));
		helpers.put("AnnotWidgetFieldBtnRadio", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnRadio", new Key("Ff", Type.BITMASK, 16, "1")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("AnnotWidgetFieldBtnCheckbox", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnCheckbox", new Key("Ff", Type.BITMASK, 17, "0")));
		helpers.put("AnnotWidgetFieldBtnRadio", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnRadio", new Key("Ff", Type.BITMASK, 17, "0")));
		helpers.put("AnnotWidgetFieldBtnPush", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnPush", new Key("Ff", Type.BITMASK, 17, "1")));
		helpersList.add(helpers);

		//fields
		helpers = new HashMap<>();
		helpers.put("AnnotWidget", new DifferentKeysLinkHelper("AnnotWidget", ""));
		helpers.put("AnnotWidgetField", new DifferentKeysLinkHelper("AnnotWidgetField", "T", "TU", "TM", "Ff", "AA"));
		helpersList.add(helpers);

		//fields
		helpers = new HashMap<>();
		helpers.put("AnnotWidget", new DifferentKeysValuesLinkHelper("AnnotWidget", new Key("FT", Type.NAME, true, false)));
		helpers.put("AnnotWidgetField", new DifferentKeysValuesLinkHelper("AnnotWidgetField", new Key("FT", Type.NAME, true, false)));
		helpers.put("AnnotWidgetFieldSig", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldSig", new Key("FT", Type.NAME, "Sig")));
		helpers.put("AnnotWidgetFieldTx", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldTx", new Key("FT", Type.NAME, "Tx")));
		helpers.put("AnnotWidgetFieldBtnCheckbox", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnCheckbox", new Key("FT", Type.NAME, "Btn")));
		helpers.put("AnnotWidgetFieldBtnRadio", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnRadio", new Key("FT", Type.NAME, "Btn")));
		helpers.put("AnnotWidgetFieldBtnPush", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnPush", new Key("FT", Type.NAME, "Btn")));
		helpers.put("AnnotWidgetFieldChoice", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldChoice", new Key("FT", Type.NAME, "Ch")));
		helpersList.add(helpers);

		//annotations
		helpers = new HashMap<>();
		helpers.put("Annot3D", new DifferentKeysValuesLinkHelper("Annot3D", new Key("Subtype", Type.NAME, "3D")));
		helpers.put("AnnotCaret", new DifferentKeysValuesLinkHelper("AnnotCaret", new Key("Subtype", Type.NAME, "Caret")));
		helpers.put("AnnotCircle", new DifferentKeysValuesLinkHelper("AnnotCircle", new Key("Subtype", Type.NAME, "Circle")));
		helpers.put("AnnotFileAttachment", new DifferentKeysValuesLinkHelper("AnnotFileAttachment", new Key("Subtype", Type.NAME, "FileAttachment")));
		helpers.put("AnnotFreeText", new DifferentKeysValuesLinkHelper("AnnotFreeText", new Key("Subtype", Type.NAME, "FreeText")));
		helpers.put("AnnotHighlight", new DifferentKeysValuesLinkHelper("AnnotHighlight", new Key("Subtype", Type.NAME, "Highlight")));
		helpers.put("AnnotInk", new DifferentKeysValuesLinkHelper("AnnotInk", new Key("Subtype", Type.NAME, "Ink")));
		helpers.put("AnnotLine", new DifferentKeysValuesLinkHelper("AnnotLine", new Key("Subtype", Type.NAME, "Line")));
		helpers.put("AnnotLink", new DifferentKeysValuesLinkHelper("AnnotLink", new Key("Subtype", Type.NAME, "Link")));
		helpers.put("AnnotMovie", new DifferentKeysValuesLinkHelper("AnnotMovie", new Key("Subtype", Type.NAME, "Movie")));
		helpers.put("AnnotPopup", new DifferentKeysValuesLinkHelper("AnnotPopup", new Key("Subtype", Type.NAME, "Popup")));
		helpers.put("AnnotPolyLine", new DifferentKeysValuesLinkHelper("AnnotPolyLine", new Key("Subtype", Type.NAME, "PolyLine")));
		helpers.put("AnnotPolygon", new DifferentKeysValuesLinkHelper("AnnotPolygon", new Key("Subtype", Type.NAME, "Polygon")));
		helpers.put("AnnotPrinterMark", new DifferentKeysValuesLinkHelper("AnnotPrinterMark", new Key("Subtype", Type.NAME, "PrinterMark")));
		helpers.put("AnnotProjection", new DifferentKeysValuesLinkHelper("AnnotProjection", new Key("Subtype", Type.NAME, "Projection")));
		helpers.put("AnnotRichMedia", new DifferentKeysValuesLinkHelper("AnnotRichMedia", new Key("Subtype", Type.NAME, "RichMedia")));
		helpers.put("AnnotRedact", new DifferentKeysValuesLinkHelper("AnnotRedact", new Key("Subtype", Type.NAME, "Redact")));
		helpers.put("AnnotScreen", new DifferentKeysValuesLinkHelper("AnnotScreen", new Key("Subtype", Type.NAME, "Screen")));
		helpers.put("AnnotSound", new DifferentKeysValuesLinkHelper("AnnotSound", new Key("Subtype", Type.NAME, "Sound")));
		helpers.put("AnnotStamp", new DifferentKeysValuesLinkHelper("AnnotStamp", new Key("Subtype", Type.NAME, "Stamp")));
		helpers.put("AnnotStrikeOut", new DifferentKeysValuesLinkHelper("AnnotStrikeOut", new Key("Subtype", Type.NAME, "StrikeOut")));
		helpers.put("AnnotSquare", new DifferentKeysValuesLinkHelper("AnnotSquare", new Key("Subtype", Type.NAME, "Square")));
		helpers.put("AnnotSquiggly", new DifferentKeysValuesLinkHelper("AnnotSquiggly", new Key("Subtype", Type.NAME, "Squiggly")));
		helpers.put("AnnotText", new DifferentKeysValuesLinkHelper("AnnotText", new Key("Subtype", Type.NAME, "Text")));
		helpers.put("AnnotTrapNetwork", new DifferentKeysValuesLinkHelper("AnnotTrapNetwork", new Key("Subtype", Type.NAME, "TrapNet")));
		helpers.put("AnnotUnderline", new DifferentKeysValuesLinkHelper("AnnotUnderline", new Key("Subtype", Type.NAME, "Underline")));
		helpers.put("AnnotWatermark", new DifferentKeysValuesLinkHelper("AnnotWatermark", new Key("Subtype", Type.NAME, "Watermark")));
		helpers.put("AnnotWidget", new DifferentKeysValuesLinkHelper("AnnotWidget", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetField", new DifferentKeysValuesLinkHelper("AnnotWidgetField", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldSig", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldSig", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldTx", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldTx", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldBtnCheckbox", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnCheckbox", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldBtnRadio", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnRadio", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldBtnPush", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnPush", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldChoice", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldChoice", new Key("Subtype", Type.NAME, "Widget")));

		helpersList.add(helpers);

		//exDatas
		helpers = new HashMap<>();
		helpers.put("ExDataMarkupGeo", new DifferentKeysValuesLinkHelper("ExDataMarkupGeo", new Key("Subtype", Type.NAME,"MarkupGeo")));
		helpers.put("ExData3DMarkup", new DifferentKeysValuesLinkHelper("ExData3DMarkup", new Key("Subtype", Type.NAME,"Markup3D")));
		helpers.put("ExDataProjection", new DifferentKeysValuesLinkHelper("ExDataProjection", new Key("Subtype", Type.NAME,"3DM")));
		helpersList.add(helpers);

		//measures
		helpers = new HashMap<>();
		helpers.put("MeasureGEO", new DifferentKeysValuesLinkHelper("MeasureGEO", new Key("Subtype", Type.NAME, "GEO")));
		helpers.put("MeasureRL", new DifferentKeysValuesLinkHelper("MeasureRL", new Key("Subtype", Type.NAME, "RL")));
		helpersList.add(helpers);

		//renditions
		helpers = new HashMap<>();
		helpers.put("RenditionMedia", new DifferentKeysValuesLinkHelper("RenditionMedia", new Key("S", Type.NAME, "MR")));
		helpers.put("RenditionSelector", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("S", Type.NAME, "SR")));
		helpersList.add(helpers);

		//shading types
		helpers = new HashMap<>();
		helpers.put("ShadingType1", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "1")));
		helpers.put("ShadingType2", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "2")));
		helpers.put("ShadingType3", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "3")));
		helpers.put("ShadingType4", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "4")));
		helpers.put("ShadingType5", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "5")));
		helpers.put("ShadingType6", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "6")));
		helpers.put("ShadingType7", new DifferentKeysValuesLinkHelper("RenditionSelector", new Key("ShadingType", Type.INTEGER, "7")));
		helpersList.add(helpers);

		//halftone types
		helpers = new HashMap<>();
		helpers.put("HalftoneType1", new DifferentKeysValuesLinkHelper("HalftoneType1", new Key("HalftoneType", Type.INTEGER, "1")));
		helpers.put("HalftoneType5", new DifferentKeysValuesLinkHelper("HalftoneType5", new Key("HalftoneType", Type.INTEGER, "5")));
		helpers.put("HalftoneType6", new DifferentKeysValuesLinkHelper("HalftoneType6", new Key("HalftoneType", Type.INTEGER, "6")));
		helpers.put("HalftoneType10", new DifferentKeysValuesLinkHelper("HalftoneType10", new Key("HalftoneType", Type.INTEGER, "10")));
		helpers.put("HalftoneType16", new DifferentKeysValuesLinkHelper("HalftoneType16", new Key("HalftoneType", Type.INTEGER, "16")));
		helpersList.add(helpers);

		// opt contents
		helpers = new HashMap<>();
		helpers.put("OptContentMembership", new DifferentKeysValuesLinkHelper("OptContentMembership", new Key("Type", Type.NAME, "OCMD")));
		helpers.put("OptContentGroup", new DifferentKeysValuesLinkHelper("OptContentGroup", new Key("Type", Type.NAME, "OCG")));
		//...
		helpersList.add(helpers);

		//media clips
		helpers = new HashMap<>();
		helpers.put("MediaClipData", new DifferentKeysValuesLinkHelper("MediaClipData", new Key("S", Type.NAME, "MCD")));
		helpers.put("MediaClipSection", new DifferentKeysValuesLinkHelper("MediaClipSection", new Key("S", Type.NAME, "MCS")));
		helpersList.add(helpers);

		//soft masks
		helpers = new HashMap<>();
		helpers.put("SoftMaskAlpha", new DifferentKeysValuesLinkHelper("SoftMaskAlpha", new Key("S", Type.NAME, "Alpha")));
		helpers.put("SoftMaskLuminosity", new DifferentKeysValuesLinkHelper("SoftMaskLuminosity", new Key("S", Type.NAME, "Luminosity")));
		helpersList.add(helpers);

		// coordinate systems
		helpers = new HashMap<>();
		helpers.put("ProjectedCoordinateSystem", new DifferentKeysValuesLinkHelper("ProjectedCoordinateSystem", new Key("Type", Type.NAME, "PROJCS")));
		helpers.put("GeographicCoordinateSystem", new DifferentKeysValuesLinkHelper("GeographicCoordinateSystem", new Key("Type", Type.NAME, "GEOGCS")));
		helpersList.add(helpers);

		// functions
		helpers = new HashMap<>();
		helpers.put("FunctionType0", new DifferentKeysValuesLinkHelper("FunctionType0", new Key("FunctionType", Type.INTEGER, "0")));
		helpers.put("FunctionType2", new DifferentKeysValuesLinkHelper("FunctionType2", new Key("FunctionType", Type.INTEGER, "2")));
		helpers.put("FunctionType3", new DifferentKeysValuesLinkHelper("FunctionType3", new Key("FunctionType", Type.INTEGER, "3")));
		helpers.put("FunctionType4", new DifferentKeysValuesLinkHelper("FunctionType4", new Key("FunctionType", Type.INTEGER, "4")));
		helpersList.add(helpers);

		//dparts
		helpers = new HashMap<>();
		helpers.put("DPartRoot", new DifferentKeysValuesLinkHelper("DPartRoot", new Key("Type", Type.NAME, "DPartRoot")));
		helpers.put("DPart", new DifferentKeysValuesLinkHelper("DPart", new Key("Type", Type.NAME, "DPart")));
		helpersList.add(helpers);

		//signatures
		helpers = new HashMap<>();
		helpers.put("Signature", new DifferentKeysValuesLinkHelper("Signature", new Key("Type", Type.NAME, "Sig")));
		helpers.put("DocTimeStamp", new DifferentKeysValuesLinkHelper("DocTimeStamp", new Key("Type", Type.NAME, "DocTimeStamp")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("XObjectFormPS", new DifferentKeysValuesLinkHelper("XObjectFormPS", new Key("Subtype2", Type.NAME, true, false)));
		helpers.put("XObjectFormPSpassthrough", new DifferentKeysValuesLinkHelper("XObjectFormPSpassthrough", new Key("Subtype2", Type.NAME, "PS")));
		helpersList.add(helpers);

		//xobjects
		helpers = new HashMap<>();
		helpers.put("XObjectFormType1", new DifferentKeysValuesLinkHelper("XObjectFormType1", new Key("Subtype", Type.NAME, "Form")));
		helpers.put("XObjectImage", new DifferentKeysValuesLinkHelper("XObjectImage", new Key("Subtype", Type.NAME, "Image")));
		helpers.put("XObjectFormPS", new DifferentKeysValuesLinkHelper("XObjectFormPS",
				new Key("Subtype", Type.NAME, "PS")));
		helpers.put("XObjectFormPSpassthrough", new DifferentKeysValuesLinkHelper("XObjectFormPSpassthrough",
				new Key("Subtype", Type.NAME, "PS")));
		helpersList.add(helpers);

		//media offsets
		helpers = new HashMap<>();
		helpers.put("MediaOffsetTime", new DifferentKeysValuesLinkHelper("MediaOffsetTime", new Key("S", Type.NAME, "T")));
		helpers.put("MediaOffsetMarker", new DifferentKeysValuesLinkHelper("MediaOffsetMarker", new Key("S", Type.NAME, "M")));
		helpers.put("MediaOffsetFrame", new DifferentKeysValuesLinkHelper("MediaOffsetFrame", new Key("S", Type.NAME, "F")));
		helpersList.add(helpers);

		//struct elem children
		helpers = new HashMap<>();
		helpers.put("StructElem", new DifferentKeysValuesLinkHelper("StructElem", new Key("Type", Type.NAME, true, false, "StructElem")));
		helpers.put("ObjectReference", new DifferentKeysValuesLinkHelper("ObjectReference", new Key("Type", Type.NAME, "OBJR")));
		helpers.put("MarkedContentReference", new DifferentKeysValuesLinkHelper("MarkedContentReference", new Key("Type", Type.NAME, "MCR")));
		helpers.put("StructTreeRoot", new DifferentKeysValuesLinkHelper("StructTreeRoot", new Key("Type", Type.NAME, "StructTreeRoot")));
		helpersList.add(helpers);

		//fonts
		helpers = new HashMap<>();
		helpers.put("FontType1", new DifferentKeysValuesLinkHelper("FontType1", new Key("Subtype", Type.NAME, "Type1")));
		helpers.put("FontTrueType", new DifferentKeysValuesLinkHelper("FontTrueType", new Key("Subtype", Type.NAME, "TrueType")));
		helpers.put("FontMultipleMaster", new DifferentKeysValuesLinkHelper("FontMultipleMaster", new Key("Subtype", Type.NAME, "MMType1")));
		helpers.put("FontCIDType2", new DifferentKeysValuesLinkHelper("FontCIDType2", new Key("Subtype", Type.NAME, "CIDFontType2")));
		helpers.put("FontCIDType0", new DifferentKeysValuesLinkHelper("FontCIDType0", new Key("Subtype", Type.NAME, "CIDFontType0")));
		helpers.put("FontType0", new DifferentKeysValuesLinkHelper("FontType0", new Key("Subtype", Type.NAME, "Type0")));
		helpers.put("FontType3", new DifferentKeysValuesLinkHelper("FontType3", new Key("Subtype", Type.NAME, "Type3")));
		helpersList.add(helpers);

		//3DMeasures
		helpers = new HashMap<>();
		helpers.put("3DMeasure3DC", new DifferentKeysValuesLinkHelper("3DMeasure3DC", new Key("Subtype", Type.NAME, "3DC")));
		helpers.put("3DMeasureAD3", new DifferentKeysValuesLinkHelper("3DMeasureAD3", new Key("Subtype", Type.NAME, "AD3")));
		helpers.put("3DMeasureLD3", new DifferentKeysValuesLinkHelper("3DMeasureLD3", new Key("Subtype", Type.NAME, "LD3")));
		helpers.put("3DMeasurePD3", new DifferentKeysValuesLinkHelper("3DMeasurePD3", new Key("Subtype", Type.NAME, "PD3")));
		helpers.put("3DMeasureRD3", new DifferentKeysValuesLinkHelper("3DMeasureRD3", new Key("Subtype", Type.NAME, "RD3")));
		helpersList.add(helpers);

		//web captures
		helpers = new HashMap<>();
		helpers.put("WebCapturePageSet", new DifferentKeysValuesLinkHelper("WebCapturePageSet", new Key("S", Type.NAME, "SPS")));
		helpers.put("WebCaptureImageSet", new DifferentKeysValuesLinkHelper("WebCaptureImageSet", new Key("S", Type.NAME, "SIS")));
		helpersList.add(helpers);

		//encryptions
		helpers = new HashMap<>();
		helpers.put("EncryptionStandard", new DifferentKeysValuesLinkHelper("EncryptionStandard", new Key("Filter", Type.NAME, "Standard")));
		helpers.put("EncryptionPublicKey", new DifferentKeysValuesLinkHelper("EncryptionPublicKey", new Key("Filter", Type.NAME, true, false, "Adobe.PubSec", "AdobePPKLite")));
		helpersList.add(helpers);

		//beads
		helpers = new HashMap<>();
		helpers.put("Bead", new KeyNameLinkHelper("Bead", null, false));
		helpers.put("BeadFirst", new KeyNameLinkHelper("BeadFirst", "0", false));
		helpersList.add(helpers);

		//requirements
		helpers = new HashMap<>();
		helpers.put("RequirementsRichMedia", new DifferentKeysValuesLinkHelper("RequirementsRichMedia", new Key("S", Type.NAME, "RichMedia")));
		helpers.put("RequirementsHandler", new DifferentKeysValuesLinkHelper("RequirementsHandler", new Key("S", Type.NAME, "JS", "NoOp")));
		helpers.put("RequirementsMultimedia", new DifferentKeysValuesLinkHelper("RequirementsMultimedia", new Key("S", Type.NAME, "Multimedia")));
		helpers.put("RequirementsU3D", new DifferentKeysValuesLinkHelper("RequirementsU3D", new Key("S", Type.NAME, "U3D")));
		helpers.put("RequirementsAttachmentEditing", new DifferentKeysValuesLinkHelper("RequirementsAttachmentEditing", new Key("S", Type.NAME, "AttachmentEditing")));
		helpers.put("RequirementsOCAutoStates", new DifferentKeysValuesLinkHelper("RequirementsOCAutoStates", new Key("S", Type.NAME, "OCAutoStates")));
		helpers.put("RequirementsSeparationSimulation", new DifferentKeysValuesLinkHelper("RequirementsSeparationSimulation", new Key("S", Type.NAME, "SeparationSimulation")));
		helpers.put("RequirementsDigSig", new DifferentKeysValuesLinkHelper("RequirementsDigSig", new Key("S", Type.NAME, "DigSig")));
		helpers.put("RequirementsCollectionEditing", new DifferentKeysValuesLinkHelper("RequirementsCollectionEditing", new Key("S", Type.NAME, "CollectionEditing")));
		helpers.put("RequirementsTransitions", new DifferentKeysValuesLinkHelper("RequirementsTransitions", new Key("S", Type.NAME, "Transitions")));
		helpers.put("RequirementsEncryption", new DifferentKeysValuesLinkHelper("RequirementsEncryption", new Key("S", Type.NAME, "Encryption")));
		helpers.put("RequirementsglTF", new DifferentKeysValuesLinkHelper("RequirementsglTF", new Key("S", Type.NAME, "glTF")));
		helpers.put("RequirementsDigSigValidation", new DifferentKeysValuesLinkHelper("RequirementsDigSigValidation", new Key("S", Type.NAME, "DigSigValidation")));
		helpers.put("RequirementsSTEP", new DifferentKeysValuesLinkHelper("RequirementsSTEP", new Key("S", Type.NAME, "STEP")));
		helpers.put("RequirementsNavigation", new DifferentKeysValuesLinkHelper("RequirementsNavigation", new Key("S", Type.NAME, "Navigation")));
		helpers.put("RequirementsDPartInteract", new DifferentKeysValuesLinkHelper("RequirementsDPartInteract", new Key("S", Type.NAME, "DPartInteract")));
		helpers.put("RequirementsOCInteract", new DifferentKeysValuesLinkHelper("RequirementsOCInteract", new Key("S", Type.NAME, "OCInteract")));
		helpers.put("RequirementsDigSigMDP", new DifferentKeysValuesLinkHelper("RequirementsDigSigMDP", new Key("S", Type.NAME, "DigSigMDP")));
		helpers.put("RequirementsPRC", new DifferentKeysValuesLinkHelper("RequirementsPRC", new Key("S", Type.NAME, "PRC")));
		helpers.put("RequirementsAttachment", new DifferentKeysValuesLinkHelper("RequirementsAttachment", new Key("S", Type.NAME, "Attachment")));
		helpers.put("Requirements3DMarkup", new DifferentKeysValuesLinkHelper("Requirements3DMarkup", new Key("S", Type.NAME, "3DMarkup")));
		helpers.put("RequirementsGeospatial2D", new DifferentKeysValuesLinkHelper("RequirementsGeospatial2D", new Key("S", Type.NAME, "Geospatial2D")));
		helpers.put("RequirementsGeospatial3D", new DifferentKeysValuesLinkHelper("RequirementsGeospatial3D", new Key("S", Type.NAME, "Geospatial3D")));
		helpers.put("RequirementsCollection", new DifferentKeysValuesLinkHelper("RequirementsCollection", new Key("S", Type.NAME, "Collection")));
		helpers.put("RequirementsMarkup", new DifferentKeysValuesLinkHelper("RequirementsMarkup", new Key("S", Type.NAME, "Markup")));
		helpers.put("RequirementsAction", new DifferentKeysValuesLinkHelper("RequirementsAction", new Key("S", Type.NAME, "Action")));
		helpers.put("RequirementsEnableJavaScripts", new DifferentKeysValuesLinkHelper("RequirementsEnableJavaScripts", new Key("S", Type.NAME, "EnableJavaScripts")));
		helpers.put("RequirementsAcroFormInteract", new DifferentKeysValuesLinkHelper("RequirementsAcroFormInteract", new Key("S", Type.NAME, "AcroFormInteract")));
		helpersList.add(helpers);

		//signature references
		helpers = new HashMap<>();
		helpers.put("SignatureReferenceDocMDP", new DifferentKeysValuesLinkHelper("SignatureReferenceDocMDP", new Key("TransformMethod", Type.NAME, "DocMDP")));
		helpers.put("SignatureReferenceFieldMDP", new DifferentKeysValuesLinkHelper("SignatureReferenceFieldMDP", new Key("TransformMethod", Type.NAME, "FieldMDP")));
		helpers.put("SignatureReferenceIdentity", new DifferentKeysValuesLinkHelper("SignatureReferenceIdentity", new Key("TransformMethod", Type.NAME, "Identity")));
		helpers.put("SignatureReferenceUR", new DifferentKeysValuesLinkHelper("SignatureReferenceUR", new Key("TransformMethod", Type.NAME, "UR", "UR3")));
		helpersList.add(helpers);

		//page objects
		helpers = new HashMap<>();
		helpers.put("PageObject", new DifferentKeysValuesLinkHelper("PageObject", new Key("Type", Type.NAME, "Page", "Template")));
		helpers.put("PageTreeNode", new DifferentKeysValuesLinkHelper("PageTreeNode", new Key("Type", Type.NAME, "Pages")));
		helpersList.add(helpers);

		//color spaces
		helpers = new HashMap<>();
		helpers.put("PatternColorSpace", new DifferentKeysValuesLinkHelper("PatternColorSpace", new Key("0", Type.NAME, "Pattern")));
		helpers.put("IndexedColorSpace", new DifferentKeysValuesLinkHelper("IndexedColorSpace", new Key("0", Type.NAME, "Indexed")));
		helpers.put("SeparationColorSpace", new DifferentKeysValuesLinkHelper("SeparationColorSpace", new Key("0", Type.NAME, "Separation")));
		helpers.put("CalGrayColorSpace", new DifferentKeysValuesLinkHelper("CalGrayColorSpace", new Key("0", Type.NAME, "CalGray")));
		helpers.put("LabColorSpace", new DifferentKeysValuesLinkHelper("LabColorSpace", new Key("0", Type.NAME, "Lab")));
		helpers.put("CalRGBColorSpace", new DifferentKeysValuesLinkHelper("CalRGBColorSpace", new Key("0", Type.NAME, "CalRGB")));
		helpers.put("DeviceNColorSpace", new DifferentKeysValuesLinkHelper("DeviceNColorSpace", new Key("0", Type.NAME, "DeviceN")));
		helpers.put("ICCBasedColorSpace", new DifferentKeysValuesLinkHelper("ICCBasedColorSpace", new Key("0", Type.NAME, "ICCBased")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("Dest0Array", new SizeLinkHelper("Dest0Array", 2));
		helpers.put("Dest1Array", new SizeLinkHelper("Dest1Array", 3));
		helpers.put("DestXYZArray", new SizeLinkHelper("DestXYZArray", 5));
		helpers.put("Dest4Array", new SizeLinkHelper("Dest4Array", 6));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("Dest0StructArray", new SizeLinkHelper("Dest0StructArray", 2));
		helpers.put("Dest1StructArray", new SizeLinkHelper("Dest1StructArray", 3));
		helpers.put("DestXYZStructArray", new SizeLinkHelper("DestXYZStructArray", 5));
		helpers.put("Dest4StructArray", new SizeLinkHelper("Dest4StructArray", 6));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("ArrayOf_2Integers", new SizeLinkHelper("ArrayOf_2Integers", 2));
		helpers.put("ArrayOf_4Integers", new SizeLinkHelper("ArrayOf_4Integers", 4));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("ArrayOf_3RGBNumbers", new SizeLinkHelper("ArrayOf_3RGBNumbers", 3));
		helpers.put("ArrayOf_4BorderColorArrays", new SizeLinkHelper("ArrayOf_4BorderColorArrays", 4));
		//filters
		helpersList.add(helpers);
		helpers = new HashMap<>();
		helpers.put("FilterLZWDecode", new DifferentKeysValuesLinkHelper("FilterLZWDecode", new Key("Filter", Type.NAME, false, true, "LZWDecode")));
		helpers.put("FilterFlateDecode", new DifferentKeysValuesLinkHelper("FilterFlateDecode", new Key("Filter", Type.NAME, false, true, "FlateDecode")));
		helpers.put("FilterCrypt", new DifferentKeysValuesLinkHelper("FilterCrypt", new Key("Filter", Type.NAME, false, true, "Crypt")));
		helpers.put("FilterCCITTFaxDecode", new DifferentKeysValuesLinkHelper("FilterCCITTFaxDecode", new Key("Filter", Type.NAME, false, true, "CCITTFaxDecode")));
		helpers.put("FilterJBIG2Decode", new DifferentKeysValuesLinkHelper("FilterJBIG2Decode", new Key("Filter", Type.NAME, false, true, "JBIG2Decode")));
		helpers.put("FilterDCTDecode", new DifferentKeysValuesLinkHelper("FilterDCTDecode", new Key("Filter", Type.NAME, false, true, "DCTDecode")));
		helpersList.add(helpers);

		//page tree nodes
		helpers = new HashMap<>();
		helpers.put("Outline", new DifferentKeysLinkHelper("Outline", ""));
		helpers.put("OutlineItem", new DifferentKeysLinkHelper("OutlineItem", "Parent"));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("3DView", new DifferentKeysLinkHelper("3DView", ""));
		helpers.put("3DViewAddEntries", new DifferentKeysLinkHelper("3DViewAddEntries", "Snapshot", "Params"));
		helpersList.add(helpers);

		//outlines
		helpers = new HashMap<>();
		helpers.put("PageTreeNode", new DifferentKeysLinkHelper("PageTreeNode", "Parent"));
		helpers.put("PageTreeNodeRoot", new DifferentKeysLinkHelper("PageTreeNodeRoot", ""));
		helpersList.add(helpers);

		//opi
		helpers = new HashMap<>();
		helpers.put("OPIVersion13", new DifferentKeysLinkHelper("OPIVersion13", "1.3"));
		helpers.put("OPIVersion20", new DifferentKeysLinkHelper("OPIVersion20", "2.0"));
		helpersList.add(helpers);

		//opi dicts
		helpers = new HashMap<>();
		helpers.put("OPIVersion13Dict", new DifferentKeysValuesLinkHelper("OPIVersion13Dict", new Key("Version", Type.NAME, "1.3")));
		helpers.put("OPIVersion20Dict", new DifferentKeysValuesLinkHelper("OPIVersion20Dict", new Key("Version", Type.NAME, "2.0")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("FieldBtnCheckbox", new DifferentKeysValuesLinkHelper("FieldBtnCheckbox", new Key("Ff", Type.BITMASK, 16, "0")));
		helpers.put("FieldBtnRadio", new DifferentKeysValuesLinkHelper("FieldBtnRadio", new Key("Ff", Type.BITMASK, 16, "1")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("FieldBtnCheckbox", new DifferentKeysValuesLinkHelper("FieldBtnCheckbox", new Key("Ff", Type.BITMASK, 17, "0")));
		helpers.put("FieldBtnRadio", new DifferentKeysValuesLinkHelper("FieldBtnRadio", new Key("Ff", Type.BITMASK, 17, "0")));
		helpers.put("FieldBtnPush", new DifferentKeysValuesLinkHelper("FieldBtnPush", new Key("Ff", Type.BITMASK, 17, "1")));
		helpersList.add(helpers);

		//fields
		helpers = new HashMap<>();
		helpers.put("Field", new DifferentKeysValuesLinkHelper("Field", new Key("FT", Type.NAME, true, false)));
		helpers.put("FieldSig", new DifferentKeysValuesLinkHelper("FieldSig", new Key("FT", Type.NAME, "Sig")));
		helpers.put("FieldTx", new DifferentKeysValuesLinkHelper("FieldTx", new Key("FT", Type.NAME, "Tx")));
		helpers.put("FieldBtnCheckbox", new DifferentKeysValuesLinkHelper("FieldBtnCheckbox", new Key("FT", Type.NAME, "Btn")));
		helpers.put("FieldBtnRadio", new DifferentKeysValuesLinkHelper("FieldBtnRadio", new Key("FT", Type.NAME, "Btn")));
		helpers.put("FieldBtnPush", new DifferentKeysValuesLinkHelper("FieldBtnPush", new Key("FT", Type.NAME, "Btn")));
		helpers.put("FieldChoice", new DifferentKeysValuesLinkHelper("FieldChoice", new Key("FT", Type.NAME, "Ch")));
		helpersList.add(helpers);

		//fields
		helpers = new HashMap<>();
		helpers.put("AnnotWidget", new DifferentKeysValuesLinkHelper("AnnotWidget", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetField", new DifferentKeysValuesLinkHelper("AnnotWidgetField", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldSig", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldSig", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldTx", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldTx", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldBtnCheckbox", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnCheckbox", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldBtnRadio", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnRadio", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldBtnPush", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldBtnPush", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("AnnotWidgetFieldChoice", new DifferentKeysValuesLinkHelper("AnnotWidgetFieldChoice", new Key("Subtype", Type.NAME, "Widget")));
		helpers.put("Field", new DifferentKeysValuesLinkHelper("Field", new Key("Subtype", Type.NAME, true, false)));
		helpers.put("FieldSig", new DifferentKeysValuesLinkHelper("FieldSig", new Key("Subtype", Type.NAME, true, false)));
		helpers.put("FieldTx", new DifferentKeysValuesLinkHelper("FieldTx", new Key("Subtype", Type.NAME, true, false)));
		helpers.put("FieldBtnCheckbox", new DifferentKeysValuesLinkHelper("FieldBtnCheckbox", new Key("Subtype", Type.NAME, true, false)));
		helpers.put("FieldBtnRadio", new DifferentKeysValuesLinkHelper("FieldBtnRadio", new Key("Subtype", Type.NAME, true, false)));
		helpers.put("FieldBtnPush", new DifferentKeysValuesLinkHelper("FieldBtnPush", new Key("Subtype", Type.NAME, true, false)));
		helpers.put("FieldChoice", new DifferentKeysValuesLinkHelper("FieldChoice", new Key("Subtype", Type.NAME, true, false)));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("XObjectImage", new DifferentKeysValuesLinkHelper("XObjectImage", new Key("Subtype", Type.NAME, "Image")));
		helpers.put("EmbeddedFileStream", new DifferentKeysValuesLinkHelper("EmbeddedFileStream", new Key("Subtype", Type.NAME, true, false)));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("OPIVersion13Dict", new DifferentKeysValuesLinkHelper("OPIVersion13Dict", new Key("Type", Type.NAME, "OPI")));
		helpers.put("OPIVersion20Dict", new DifferentKeysValuesLinkHelper("OPIVersion20Dict", new Key("Type", Type.NAME, "OPI")));
		helpers.put("GraphicsStateParameter", new DifferentKeysValuesLinkHelper("GraphicsStateParameter", new Key("Type", Type.NAME, "ExtGState")));
		helpers.put("PatternType2", new DifferentKeysValuesLinkHelper("PatternType2", new Key("Type", Type.NAME, "Pattern")));
		helpers.put("XObjectFormType1", new DifferentKeysValuesLinkHelper("XObjectFormType1", new Key("Type", Type.NAME, "XObject")));
		helpers.put("XObjectImage", new DifferentKeysValuesLinkHelper("XObjectImage", new Key("Type", Type.NAME, "XObject")));
		helpers.put("XObjectFormPS", new DifferentKeysValuesLinkHelper("XObjectFormPS", new Key("Type", Type.NAME, "XObject")));
		helpers.put("XObjectFormPSpassthrough", new DifferentKeysValuesLinkHelper("XObjectFormPSpassthrough", new Key("Type", Type.NAME, "XObject")));
		helpers.put("FontType1", new DifferentKeysValuesLinkHelper("FontType1", new Key("Type", Type.NAME, "Font")));
		helpers.put("FontTrueType", new DifferentKeysValuesLinkHelper("FontTrueType", new Key("Type", Type.NAME, "Font")));
		helpers.put("FontMultipleMaster", new DifferentKeysValuesLinkHelper("FontMultipleMaster", new Key("Type", Type.NAME, "Font")));
		helpers.put("FontType3", new DifferentKeysValuesLinkHelper("FontType3", new Key("Type", Type.NAME, "Font")));
		helpers.put("FontType0", new DifferentKeysValuesLinkHelper("FontType0", new Key("Type", Type.NAME, "Font")));
		helpers.put("FontCIDType0", new DifferentKeysValuesLinkHelper("FontCIDType0", new Key("Type", Type.NAME, "Font")));
		helpers.put("FontCIDType2", new DifferentKeysValuesLinkHelper("FontCIDType2", new Key("Type", Type.NAME, "Font")));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("OPIVersion13Dict", new DifferentKeysLinkHelper("OPIVersion13Dict", "Type"));
		helpers.put("OPIVersion20Dict", new DifferentKeysLinkHelper("OPIVersion20Dict", "Type"));
		helpers.put("GraphicsStateParameter", new DifferentKeysLinkHelper("GraphicsStateParameter", "Type"));
		helpers.put("PatternType2", new DifferentKeysLinkHelper("PatternType2", "Type"));
		helpers.put("ShadingType1", new DifferentKeysLinkHelper("ShadingType1", "ShadingType"));
		helpers.put("ShadingType2", new DifferentKeysLinkHelper("ShadingType2", "ShadingType"));
		helpers.put("ShadingType3", new DifferentKeysLinkHelper("ShadingType3", "ShadingType"));
		helpers.put("XObjectFormType1", new DifferentKeysLinkHelper("XObjectFormType1", "Type"));
		helpers.put("XObjectImage", new DifferentKeysLinkHelper("XObjectImage", "Type"));
		helpers.put("XObjectFormPS", new DifferentKeysLinkHelper("XObjectFormPS", "Type"));
		helpers.put("XObjectFormPSpassthrough", new DifferentKeysLinkHelper("XObjectFormPSpassthrough", "Type"));
		helpers.put("FontType1", new DifferentKeysLinkHelper("FontType1", "Type"));
		helpers.put("FontTrueType", new DifferentKeysLinkHelper("FontTrueType", "Type"));
		helpers.put("FontMultipleMaster", new DifferentKeysLinkHelper("FontMultipleMaster", "Type"));
		helpers.put("FontType3", new DifferentKeysLinkHelper("FontType3", "Type"));
		helpers.put("FontType0", new DifferentKeysLinkHelper("FontType0", "Type"));
		helpers.put("FontCIDType0", new DifferentKeysLinkHelper("FontCIDType0", "Type"));
		helpers.put("FontCIDType2", new DifferentKeysLinkHelper("FontCIDType2", "Type"));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("Stream", new DifferentKeysLinkHelper("Stream", ""));
		helpers.put("PatternType1", new DifferentKeysLinkHelper("PatternType1", "Type"));
		helpers.put("ShadingType4", new DifferentKeysLinkHelper("ShadingType4", "ShadingType"));
		helpers.put("ShadingType5", new DifferentKeysLinkHelper("ShadingType5", "ShadingType"));
		helpers.put("ShadingType6", new DifferentKeysLinkHelper("ShadingType6", "ShadingType"));
		helpers.put("ShadingType7", new DifferentKeysLinkHelper("ShadingType7", "ShadingType"));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("DestDict", new DifferentKeysLinkHelper("DestDict", ""));
		helpers.put("ActionGoTo", new DifferentKeysLinkHelper("ActionGoTo", "S"));
		helpers.put("ActionGoToR", new DifferentKeysLinkHelper("ActionGoToR", "S"));
		helpers.put("ActionGoToE", new DifferentKeysLinkHelper("ActionGoToE", "S"));
		helpersList.add(helpers);

		//CryptFilterMap,CryptFilterPublicKeyMap

		helpers = new HashMap<>();
		helpers.put("CryptFilterMap", new DifferentKeysLinkHelper("CryptFilterMap", true, ""));
		helpers.put("CryptFilterPublicKeyMap", new DifferentKeysLinkHelper("CryptFilterPublicKeyMap", true, "Recipients"));
		helpersList.add(helpers);

		helpers = new HashMap<>();
		helpers.put("DevExtensions", new KeyNameLinkHelper("DevExtensions", null, true));
		helpers.put("ISO_DevExtensions", new KeyNameLinkHelper("ISO_DevExtensions", "ISO_", true));
		helpersList.add(helpers);
	}
}
