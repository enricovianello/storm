---
layout: service-guide
title: StoRM System Administration Guide - Upgrade to StoRM 1.11.20
navigation:
  - link: documentation/sysadmin-guide/1.11.20/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.20/upgrading/index.html
    label: Upgrading
---

## Upgrade to StoRM 1.11.20 <a name="upgrading">&nbsp;</a>

In case you're updating from **StoRM v1.11.19**, the services that needs to be updated are:

* _storm-backend-server_
* _storm-native-libs_
* _storm-webdav_
* _storm-frontend_

First of all upgrade all the released packages:

```
yum update -y storm-backend-server storm-webdav storm-frontend-server
```

The update will upgrade also the native libraries as a dependency. <br/>
After the successful upgrade, the services will be restarted and you should have both Java 1.8 and Java 11 installed, but
**Java 11 must be set as your default runtime**. None of the latest StoRM Java components still need Java 1.8 so it can be safely removed as follows:

```
yum remove java-1.8.0-openjdk java-1.8.0-openjdk-headless
```

You shouldn't see any storm components within the involved dependencies. <br/>
If you cannot remove it, you can also set java 11 as default runtime JDK by running:

```
update-alternatives --config java
```

and select the proper Java 11 option. <br/>

Now, you can restart services:

```
systemctl restart storm-backend-server storm-frontend-server storm-webdav
```

Split the above commands properly if you have a distributed deployment.
In case you have any kind of questions or problems please contact us.

<hr/>

### Known issue \[Updated on 30.04.2021\]

After the update from StoRM v1.11.19 to StoRM v1.11.20, if JVM and database are not on the same timezone, the Backend's communication with MariaDB could start failing with the following error:

```
Caused by: java.sql.SQLException: The server time zone value 'CEST' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support.
```

This bug is tracked at [STOR-1397](https://issues.infn.it/jira/browse/STOR-1397).

The possible solutions to avoid this problem are:
 * downgrade StoRM Backend to v1.11.19 (**recommended**)
 * apply a workaround within MariaDB
 * install StoRM Backend v1.11.21 beta

Read more [here][known-issue-post] 

<hr/>

If you are upgrading from **StoRM v1.11.18** (or earlier versions) on CentOS 6 please follow
[these instructions][upgrade-18] before.

[upgrade-18]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/upgrading/

[known-issue-post]: {{site.baseurl}}/2021/04/30/storm-v1.11.20-known-issue.html
