Version 1.28 (May  5, 2025)
=================================

- implements Arlington model as of the release date (latest commit https://github.com/pdf-association/arlington-pdf-model/commit/7ac625c).
- upgraded veraPDF parser to version 1.28.1 (see [release notes](https://github.com/veraPDF/veraPDF-library/blob/rel/1.28/RELEASENOTES.md#pdf-parser))
- added extension-specific permitted values and links
- fixed processing of AA entry in merged Widget annotation and Form field dictionaries
- fixed checkbox processing with no Ff entry
- fixed processing of cyclic links in the interactive fields tree
- added support for CF entry in public key encryption dictionary
- permit empty arrays in case of arrays which require even number of elements

Version 1.26 (July  23, 2024)
=================================

- implements Arlington model as of the release date (latest commit https://github.com/pdf-association/arlington-pdf-model/commit/d187e8a). 
- see [README](https://github.com/veraPDF/veraPDF-arlington-tools/blob/master/README.md) for the implementation details
- based on verapdf-parser version 1.27.4 (identical to 1.26.1 except for some cosmetic changes)
