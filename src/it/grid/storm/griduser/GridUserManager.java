/*
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 * For a copy of the licence please visit
 *
 *    http://www.cnaf.infn.it/license.html
 *
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 *
 * $Id: GridUserManager.java 3604 2007-05-22 11:16:27Z rzappi $
 *
 */

package it.grid.storm.griduser;

import it.grid.storm.config.Configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */
public class GridUserManager {

    static final Logger log = LoggerFactory.getLogger(GridUserManager.class);
    static Configuration config = Configuration.getInstance();
    static GridUserFactory userFactory = null;

    static {
        log.debug("Inizializating Grid User Director...");
        userFactory = initializeFactory();
    }

    private GridUserManager() {
        super();
    }


    private static GridUserFactory initializeFactory() {
        GridUserFactory factory = GridUserFactory.getInstance();
        return factory;
    }


    public static String getMapperClassName() {
        String className = null;
        className = config.getGridUserMapperClassname();
        //className = "it.grid.storm.griduser.LcmapsMapper";
        return className;
    }

    /**
     * PUBLIC and STATIC methods
     */

    public static GridUserInterface makeVOMSGridUser(String dn, String proxy, FQAN[] fqans) {
        GridUserInterface result = null;
        result = userFactory.createGridUser(dn, fqans, proxy);
        return result;
    }

    public static GridUserInterface makeVOMSGridUser(String dn, FQAN[] fqans) {
        GridUserInterface result = null;
        result = userFactory.createGridUser(dn, fqans);
        return result;
    }

    public static GridUserInterface makeGridUser(String dn) {
        GridUserInterface result = null;
        result = userFactory.createGridUser(dn);
        return result;
    }

    public static GridUserInterface makeGridUser(String dn, String proxy) {
        GridUserInterface result = null;
        result = userFactory.createGridUser(dn, proxy);
        return result;
    }

    public static GridUserInterface makeStoRMGridUser() {
        GridUserInterface result = null;
        String dn = "/DC=it/DC=infngrid/OU=Services/CN=storm-t1.cnaf.infn.it";
        result = userFactory.createGridUser(dn);
        return result;
    }

    public static GridUserInterface makeSAGridUser() {
        GridUserInterface result = null;
        String dn = "/DC=it/DC=infngrid/OU=Services/CN=storm";
        result = userFactory.createGridUser(dn);
        return result;
    }


    public static GridUserInterface decode(Map inputParam) {
        GridUserInterface result = null;
        result = userFactory.decode(inputParam);
        return result;

    }

}