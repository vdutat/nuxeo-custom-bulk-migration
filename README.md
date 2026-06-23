# nuxeo-custom-bulk-migration
## About / Synopsis

This plugin demonstrates how to contribute a custom **migration** to the **Migration Service**.
It can happen that you need to change the data type of a document property e.g. from `Integer` to `String`. Directly changing the data type of a document property is strongly discouraged because the **Nuxeo Platform** does not check if document properties had their data type changed in the Nuxeo model therefore it does not change the data type of the document property in the document repository, which can lead to major issues in your application.
The recommended way to migrate a document property is to actually define a new document property with the desired data type and copy the value of the document property to it, with some conversion if necessary.
After the migration has been performed, the old document property, being unused, can be removed from the Nuxeo mdel.
This custom **migration** shows how to migrate the value of an `Integer` document property to a `String` document property using the **Bulka Action Framework**. It copy the value of document property `mydoc:intprop` to document property `mydoc:stringprop` in documents of type `MyDoc`.

**TODO**  Configuration variables

This plugin was generated with the following commands:
```
mkdir nuxeo-custom-bulk-migration && cd $_
nuxeo b multi-module contribution
# Edit contribution's XML file, java class, and test java class
nuxeo b package
mvn clean install
```

## Table of contents

> * [nuxeo-custom-bulk-migration](#nuxeo-custom-bulk-migration)
>   * [About / Synopsis](#about--synopsis)
>   * [Table of contents](#table-of-contents)
>   * [Installation](#installation)
>   * [Requirements](#requirements)
>   * [Build](#build)
>   * [License](#license)
>   * [About Hyland Nuxeo](#about-hyland-nuxeo)

## Requirements

Building requires the following software:

* git
* maven

## Build

```
git clone ...
cd nuxeo-custom-bulk-migration

mvn clean install
```

## Installation

```
nuxeoctl mp-install nuxeo-custom-bulk-migration/nuxeo-custom-bulk-migration-package/target/nuxeo-custom-bulk-migration-*.zip
```

## Support

**These features are not part of the Nuxeo Production platform, they are not supported**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## About Hyland Nuxeo

Nuxeo Platform is an open source Content Services platform, written in Java. Data can be stored in both SQL & NoSQL databases.

The development of the Nuxeo Platform is mostly done by Nuxeo employees with an open development model.

The source code, documentation, roadmap, issue tracker, testing, benchmarks are all public.

Typically, Nuxeo users build different types of information management solutions for [document management](https://www.nuxeo.com/solutions/document-management/), [case management](https://www.nuxeo.com/solutions/case-management/), and [digital asset management](https://www.nuxeo.com/solutions/dam-digital-asset-management/), use cases. It uses schema-flexible metadata & content models that allows content to be repurposed to fulfill future use cases.

More information is available at [www.nuxeo.com](https://www.nuxeo.com).


