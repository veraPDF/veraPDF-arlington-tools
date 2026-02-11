Version 1.30 RC (February 10, 2026)
- implements Arlington model as of the release date ([latest commit](https://github.com/pdf-association/arlington-pdf-model/commit/2d6d9a70de50c1929783e974d31e8f4db8623400)).
- upgraded veraPDF parser to version 1.30.1 RC (see [release notes](https://github.com/veraPDF/veraPDF-library/blob/rc/1.30/RELEASENOTES.md#pdf-parser))
- fixed getting PDF version via **Version** entry in the **Catalog** ([commit](https://github.com/veraPDF/veraPDF-validation/commit/63fe885987570f744b430016bd10022e067918b0))
- added auto-detect for ISO_19005_3 and WTPDF extensions (commits [1](https://github.com/veraPDF/veraPDF-validation/commit/4adcb64371e26ad02b2c81612d2ec47dd44d8f8d) [2](https://github.com/veraPDF/veraPDF-validation/commit/9cc00faa6d70a8ccca3f90d75a3dae9a363821d9))
- fixed issues in processing merged annotations and fields (commits [1](https://github.com/veraPDF/veraPDF-library/commit/2c1112e716341aebb088a3e04d3e4417704b46bf) [2](https://github.com/veraPDF/veraPDF-validation/commit/170966b8f65376399edbbcdc98e5344f7a802d4a) [3](https://github.com/veraPDF/veraPDF-validation/commit/62535c0aff508742a38b78c16859870dcbe269c8))
- fixed exception ([commit](https://github.com/veraPDF/veraPDF-library/commit/e7cecc3942a70c66de9b0e8ababb12ddb320ece6))
- improved wording of messages ([commit](https://github.com/veraPDF/veraPDF-library/commit/8bfe197062328e624e468d6078d717854133b7c4))
- added warning for invalid language escape sequences in text strings ([commit](https://github.com/veraPDF/veraPDF-parser/commit/08a131a5cd00f242100f311751629cc37aefe855))

Version 1.28.2 (July 15, 2025)
================================

- implements Arlington model as of the release date (latest commit https://github.com/pdf-association/arlington-pdf-model/commit/b7d880b).
- upgraded veraPDF parser to version 1.28.2 (see [release notes](https://github.com/veraPDF/veraPDF-library/blob/rel/1.28/RELEASENOTES.md#pdf-parser))
- added rule about the A entry in Target object ([commit](https://github.com/veraPDF/veraPDF-library/commit/39b849c))

Version 1.28 (May  5, 2025)
=================================

- implements Arlington model as of the release date (latest commit https://github.com/pdf-association/arlington-pdf-model/commit/7ac625c).
- upgraded veraPDF parser to version 1.28.1 (see [release notes](https://github.com/veraPDF/veraPDF-library/blob/rel/1.28/RELEASENOTES.md#pdf-parser-1))
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
