---
layout: default
title: "YAIM StoRM v.4.3.6 release notes"
release_date: 07.01.2015
rfcs:
- id: STOR-394
  title: Changes on the yaim STORM_FRONTEND_PORT variable doesn't update Frontend's configuration file
- id: STOR-654
  title: Refactor StoRM dynamic info provider service
- id: STOR-671
  title: StoRM publishes inconsistent values due to an approximation problem
- id: STOR-693
  title: YAIM-StoRM ignores STORM_SURL_ENDPOINT_LIST value
---

## YAIM StoRM v. 4.3.6

Released on **{{ page.release_date }}** with [StoRM v. 1.11.5]({{ site.baseurl }}/release-notes/StoRM-v1.11.5.html).

### Description

This release provides bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.5
