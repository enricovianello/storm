---
layout: default
title: "StoRM BackEnd v. 1.11.7 release notes"
release_date: "09.02.2015"
rfcs:
- id: STOR-686
  title: Drop support for storm-SA-read storm-SA-write
- id: STOR-701
  title: StoRM should be able to serve ptg requests for the "xroot" protocol
- id: STOR-718
  title: Slow status update queries impact on mysql performance on multiple srmRm requests
---

## StoRM Backend v. 1.11.7

Released on **{{ page.release_date }}** with [StoRM v. 1.11.7]({{ site.baseurl }}/release-notes/StoRM-v1.11.7.html).

### Description

This release provides several bug fixes and adds support for the xroot protocol alias (
formerly you would use 'root' for requesting a xrootd TURL).

This update of the backend requires a YAIM reconfiguration.
The YAIM reconfiguration is needed to add the new xroot protocol
alias to the list of supported transport protocols in the StoRM database.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
