# nuxeo-custom-bulk-migration

## Table of contents

> * [nuxeo-custom-bulk-migration](#nuxeo-custom-bulk-migration)
>   * [Table of contents](#table-of-contents)
>   * [About / Synopsis](#about--synopsis)
>   * [Configuration variables](#configuration-variables)
>   * [How to launch and check status of migration](#how-to-launch-and-check-status-of-migration)
>   * [Requirements](#requirements)
>   * [Build](#build)
>   * [Installation](#installation)
>   * [Support](#support)
>   * [License](#license)
>   * [About Hyland Nuxeo](#about-hyland-nuxeo)

## About / Synopsis

This plugin demonstrates how to contribute a custom **migration** to the **Migration Service**.

It can happen that you need to change the data type of a document property e.g. from `Integer` to `String`. Directly changing the data type of a document property is strongly discouraged because the **Nuxeo Platform** does not check if document properties had their data type changed in the Nuxeo model therefore it does not change the data type of the document property in the document repository, which can lead to major issues in your application.

The recommended way to migrate a document property is to actually define a new document property in the document definition with the desired data type and copy the value of the document property to it, with some conversion if necessary.

After the migration has been performed, the old document property, being unused, can be removed from the Nuxeo model and removed from the document repository.

In summary, the main steps in a migration are:
1. define the new document property in the Nuxeo model, the migration process, and the new Elasticsearch mapping. Update all processes and UI related to the origin document property
1. deploy the Nuxeo model
1. perform the migration followed by a full ES re-indexing (required because of the new mapping)
1. remove the unused origin document property from the Nuxeo model
1. deploy the Nuxeo model
1. remove the unused property from documents in the document repository

This custom **migration** shows how to migrate the value of an `Integer` document property to a `String` document property using the **Bulk Action Framework**. It copy the value of document property `mydoc:intprop` to document property `mydoc:stringprop` in documents of type `MyDoc`.

This plugin was generated with the following commands:
```
mkdir nuxeo-custom-bulk-migration && cd $_
nuxeo b multi-module contribution
# Edit contribution's XML file, java class, and test java class
nuxeo b package
mvn clean install
```

## Configuration variables

This migration can be configured to migrate other properties in documents of another document type using the following configuration variables to set in your `nuxeo.conf` file:
- NXQL query to retrieve the documents to migrate:
```
acme.migrator.basic-bulk-migration.nxql=SELECT * FROM Document WHERE ecm:isVersion=0 AND ecm:primaryType='YourDocumentType'
```
- name of the origin integer property:
```
acme.migrator.basic-bulk-migration..intergerPropName=yourdoctype:prop1
```
- name of the destination string property:
```
acme.migrator.basic-bulk-migration..stringPropName=yourdoctype:prop2
```

## How to launch and check status of migration

This custom migratrion uses the **Nuxeo Stream** named `bulk/migration` and the **Stream processor** named `migration`.

Before running the migration, check if it is deployed:
```
curl -su Administrator:Administrator \
http://localhost:8080/nuxeo/api/v1/management/migration/basic-bulk-migration
```

Run migration:
```
curl -X POST -su Administrator:Administrator \
http://localhost:8080/nuxeo/api/v1/management/migration/basic-bulk-migration/run
```

Check migration status:
```
curl -su Administrator:Administrator \
http://localhost:8080/nuxeo/api/v1/management/migration/basic-bulk-migration
```

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


