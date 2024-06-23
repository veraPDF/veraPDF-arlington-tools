veraPDF support for the PDF Arlington model
=============================

The [PDF Arlington Model](https://github.com/pdf-association/arlington-pdf-model) covers the requirements of PDF object model as specified in ISO 32000-2:2020 (PDF 2.0, including [resolved errata](https://pdf-issues.pdfa.org/)) as well as _some_ (but not all!) aspects from earlier Adobe PDF references and various extensions (identified by the predicate `fn:Extension(...)`). It is based on its own [formal grammar](https://github.com/pdf-association/arlington-pdf-model/blob/master/INTERNAL_GRAMMAR.md) serialized as a set of TSV files (see also the [Arlington Model Notes](https://github.com/pdf-association/arlington-pdf-model/blob/master/MODEL_NOTES.md)). References to Tables and clauses below all refer to ISO 32000-2:2020.

veraPDF adds the support for this model by translating TSV files into its own validation profile based on [veraPDF formal grammar](https://docs.verapdf.org/validation/rules/) for validation rules. 

This repository implements the Java-based utility performing this translation.

Alington types
==============================
The Arlington PDF model utilizes an [expanded set of types](https://github.com/pdf-association/arlington-pdf-model/blob/master/INTERNAL_GRAMMAR.md#column-2---type) in order to more precisely define PDF objects and data integrity relationships. veraPDF fully supports this expanded set and implements the necessary additional checks. These checks include:
- all dictionary keys must be direct objects
- `dates` must be a valid PDF date string
-` rectangle` must be a PDF array with precisely 4 entries
- `matrix` must be a PDF array with precisely 6 entries and represent a valid affine transform
- `bitmask` must be a PDF integer object
- `string-ascii` must not start with a Unicode byte order marker 
- `name-tree` is a complex data structure (see Table 36), internally comprising various PDF objects:
    - NameTreeNode shall not contain entries except **Kids**, **Limits**, **Names**.
    - Entry **Kids** in NameTreeNode shall have type Array.
    - Entry **Kids** in NameTreeNode is required, when `fn:Not(fn:IsPresent(Names))`.
    - Entry **Kids** with type Array in NameTreeNode shall satisfy special case: `fn:Not(fn:IsPresent(fn:IsPresent(Names)))`
    - Entry **Limits** in NameTreeNode shall have type Array.
    - Entry **Names** in NameTreeNode shall have type Array.
    - Entry **Names** in NameTreeNode is required, when `fn:Not(fn:IsPresent(Kids))`.
    - Entry **Names** with type Array in NameTreeNode shall satisfy special case: `fn:Not(fn:IsPresent(fn:IsPresent(Kids)))`
    - NameTreeNodeLimitsArray shall contain exactly 2 elements.
    - Entry 0 in NameTreeNodeLimitsArray shall have type String.
    - Entry 1 in NameTreeNodeLimitsArray shall have type String.
    - NameTreeNodeNamesArray shall contain 2 * _n_ elements.
    - NameTreeNodeNamesArraySubArray shall contain exactly 2 elements check.
    - Entry 0 in NameTreeNodeNamesArraySubArray shall have type String.
    - Entry in NameTreeNodesArray shall have type Dictionary.
- `number-tree` is a complex data structure (see Table 37), internally comprising various PDF objects:
    - NumberTreeNode shall not contain entries except **Kids**, **Limits**, **Nums**.
    - Entry **Kids** in NumberTreeNode shall have type Array
    - Entry **Kids** in NumberTreeNode is required, when `fn:Not(fn:IsPresent(Nums))`
    - Entry **Kids** with type Array in NumberTreeNode shall satisfy special case: `fn:Not(fn:IsPresent(fn:IsPresent(Nums)))`
    - Entry **Limits** in NumberTreeNode shall have type Array
    - Entry **Nums** in NumberTreeNode shall have type Array
    - Entry **Nums** in NumberTreeNode is required, when `fn:Not(fn:IsPresent(Kids))`
    - Entry **Nums** with type Array in NumberTreeNode shall satisfy special case: `fn:Not(fn:IsPresent(fn:IsPresent(Kids))) && fn:ArraySortAscending(Nums,2)`
    - NumberTreeNodeLimitsArray shall contain exactly 2 elements
    - Entry 0 in NumberTreeNodeLimitsArray shall have type Integer
    - Entry 1 in NumberTreeNodeLimitsArray shall have type Integer
    - NumberTreeNodeNumsArray shall contain 2 * _n_ elements
    - NumberTreeNodeNumsArraySubArray shall contain exactly 2 elements
    - Entry 0 in NumberTreeNodeNumsArraySubArray shall have type Integer
    - Entry in NumberTreeNodesArray shall have type Dictionary

Note however that veraPDF does _not_ perform any checks against architectural limitations or previous limitations noted in older PDF specifications (such as assumptions about the size of integers, the length of PDF names or strings, etc). 

Limitations of Arlington support by veraPDF
==============================

The following [Arlington predicates](https://github.com/pdf-association/arlington-pdf-model/blob/master/INTERNAL_GRAMMAR.md#predicates-declarative-functions) are not supported:
- AlwaysUnencrypted
- FontHasLatinChars
- Ignore
- IsAssociatedFile
- IsLastInNumberFormatArray
- IsMeaningful
- KeyNameIsColorant

Implementation notes
===============================

veraPDF adds extra logic to the Arlington model. Namely, it:

- extends Arlington model for checking PDF Fields and Widgets by automatically generating new types `AnnotWidgetField`, `AnnotWidgetFieldTx`, `AnnotWidgetFieldCh`, `AnnotWidgetFieldBtn`, `AnnotWidgetFieldSig` that represent merged Widget annotation and Form field dictionaries (ISO 32000-2, 12.5.6.19). See https://github.com/pdf-association/arlington-pdf-model/issues/28 for additional discussion.

- includes fixes of some known Arlington model issues:
  - wrong use of fn:InNameTree predicate. See https://github.com/pdf-association/arlington-pdf-model/issues/49
  - incorrect required condition for AS entry in Widget annotations. See https://github.com/pdf-association/arlington-pdf-model/issues/61
  - null in array of structure elements. See https://github.com/pdf-association/arlington-pdf-model/issues/90

- supports some [future Arlington model predicates](https://github.com/pdf-association/arlington-pdf-model/blob/master/INTERNAL_GRAMMAR.md#proposals-for-future-predicates):
  - ValueOnlyWhen. See https://github.com/pdf-association/arlington-pdf-model/issues/74
  - IsInArray. See https://github.com/pdf-association/arlington-pdf-model/discussions/114

- contains additional rule about **PageLabel** number tree. See https://github.com/pdf-association/arlington-pdf-model/issues/118

- custom structure attribute names of the standard structure attribute owners (Table 376) defined by external specifications (such as `HTML-3.20`, `HTML-4.01`, `CSS-1`, `CSS-2`, etc.) will be reported as deviations (i.e. veraPDF does not implement an implicit understanding of those external specifications)

- the PDF Metadata (clause 14.3) and Associated Files (clause 14.13) features are documented as being permitted on _any_ PDF object, however ISO 32000-2:2020 only formally documents the presence of the **Metadata** and **AF** keys on a few specific objects. Because the Arlington model accurately reflects the specification as written, it only defines support for those explicit objects in the TSV data. As a result, veraPDF will report deviations when a  **Metadata** or **AF** keys occurs in other objects, which may or may not be an issue. 
    - See the [Arlington Model Notes](https://github.com/pdf-association/arlington-pdf-model/blob/master/MODEL_NOTES.md) and [PDF Errata #403](https://github.com/pdf-association/pdf-issues/issues/403).

- veraPDF does _not_ check object numbers against the trailer **Size** entry and thus will _not_ report object numbers that are invalid (see Table 15) - this is not correct according to the PDF specification.
    - If veraPDF can’t find an object by its object number, it shows the log message "_Trying to get object \<object number> that is not present in the document_" and considers that this object is **null** (which is correct according to the PDF specification).

- veraPDF only operates as a "_PDF 1.5 processor_" and will always process both conventional cross-reference sections and all cross-reference streams that are present in hybrid-reference PDF files.
    -  Thus veraPDF will _not_ report issues that a pre-PDF 1.5 processor may experience when processing the same hybrid-reference PDF file (i.e. when objects defined in cross-reference streams are not present). See clause 7.5.8.4.

- the set of enabled extensions in the Arlington model (predicate `fn:Extension(...)`) will influence the deviations reported by veraPDF. Currently this is configurable in veraPDF via the CLI and GUI, but not yet the REST implementation.

