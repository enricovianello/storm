package it.grid.storm.xmlrpc.converter;

import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;

import java.util.Map;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 *
 * Authors:
 *     @author=lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Oct 9, 2008
 *
 */

public interface Converter {

    /**
     * This method return a RmInputData created from input Hashtable structure
     * of an xmlrpc Rm v2.1 call. Rm Input Data can be used to invoke mkdir
     * method of DirectoryFunctionsManager
     */
    public abstract InputData convertToInputData(Map inputParam);

    public abstract Map convertFromOutputData(OutputData outputData);

}