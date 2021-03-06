---
layout: default
title: "StoRM native libs v.1.0.1 release notes"
release_date: "03.06.2013"
rfcs:
    - id: STOR-148
      title: Change GPFS Quota Job to leverage native quota info call
    - id: STOR-10
      title: StoRM should get quota information directly from GPFS filesystem
---

## StoRM native-libs v. 1.0.1

Released on **{{ page.release_date }}** with [StoRM v. 1.11.1]({{ site.baseurl }}/release-notes/StoRM-v1.11.1.html).

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.1