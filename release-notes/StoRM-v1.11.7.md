---
layout: default
title: "StoRM v.1.11.7 - release notes"
release_date: "05.02.2015"
rfcs:
- id: STOR-346
  title: WebDAV DELETE response is 401 UNAUTHORIZED instead of 404 NOT EXISTS for authorized users on nonexistent resources
- id: STOR-632
  title: StoRM WebDAV service handles multi-range partial get incorrectly
- id: STOR-652
  title: yaim-storm asks for a mandatory variable even if it's been defined
- id: STOR-669
  title: HTTP requests fail if path contains trailing slashes
- id: STOR-686
  title: Drop support for storm-SA-read storm-SA-write
- id: STOR-690
  title: StoRM returns SRM_SUCCESS as request status for SBOL when one of the multiple SURL is still SRM_REQUEST_QUEUED or SRM_IN_PROGRESS
- id: STOR-701
  title: StoRM should be able to serve ptg requests for the "xroot" protocol
- id: STOR-718
  title: Slow status update queries impact on mysql performance on multiple srmRm requests
- id: STOR-738
  title: WebDAV MOVE with same destination as source fails with 500
- id: STOR-741
  title: WebDAV MOVE and COPY with source equal to destination fail with 412 instead of 403

components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.7
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.7
    - name: StoRM GridHTTPs
      package: storm-gridhttps-server
      version: 3.0.3
    - name: StoRM WebDAV
      package: storm-webdav
      version: 1.0.2
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.7
---

## StoRM v. 1.11.7

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes and a new component: storm-webdav. This brand new component provide a WebDAV endpoint which is logically separated from StoRM Backend. It's able to read/write directly on the filesystem out of SRM logic.

### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7