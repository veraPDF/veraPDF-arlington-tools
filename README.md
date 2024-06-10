veraPDF support for the PDF Arlington model
=============================

The [PDF Arlington model](https://github.com/pdf-association/arlington-pdf-model) covers the requirements of PDF object model as specified in PDF 2.0 and earlier. It is based on its own [formal grammar](https://github.com/pdf-association/arlington-pdf-model/blob/master/INTERNAL_GRAMMAR.md) serialized as a set of TSV files. 

veraPDF adds the support for this model by translating TSV files into its own validation profile based on [veraPDF formal grammar](https://docs.verapdf.org/validation/rules/) for validation rules. 

The current repository implement the Java-based utility performing this conversion.

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

Additional implementation notes
===============================

- veraPDF extends Arlington model for checking PDF Fields and Widgets by automatically generating new types `AnnotWidgetField`, `AnnotWidgetFieldTx`, `AnnotWidgetFieldCh`, `AnnotWidgetFieldBtn`, `AnnotWidgetFieldSig` that represent merged Widget annotation and Form field dictionaries as (see ISO 32000-2, 12.5.6.19). See https://github.com/pdf-association/arlington-pdf-model/issues/28 for additional discussion.
- veraPDF includes fixes of some known Arlington model issues:
  - wrong use of fn:InNameTree predicate. See https://github.com/pdf-association/arlington-pdf-model/issues/49
  - incorrect required condition for AS entry in Widget annotations. See https://github.com/pdf-association/arlington-pdf-model/issues/61
  - null in array of structure elements. See https://github.com/pdf-association/arlington-pdf-model/issues/90
- veraPDF supports some [future Arlington model predicates](https://github.com/pdf-association/arlington-pdf-model/blob/master/INTERNAL_GRAMMAR.md#proposals-for-future-predicates):
  - ValueOnlyWhen. See https://github.com/pdf-association/arlington-pdf-model/issues/74
  - IsInArray. See https://github.com/pdf-association/arlington-pdf-model/discussions/114
- veraPDF contains additional rule about PageLabel number tree. See https://github.com/pdf-association/arlington-pdf-model/issues/118
- veraPDF extends Arlington model for checking name tree node and number tree node dictionaries (ISO 32000-2:2020, Tables 36 and 37)

