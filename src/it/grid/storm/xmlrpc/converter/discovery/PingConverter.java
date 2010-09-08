/**
 * This class represents the Type Converter for the Ping function.
 * This class receives input data from xmlrpc call and fills the PingInputData class.
 *
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    Feb 2007
 * @version 1.0
 */

package it.grid.storm.xmlrpc.converter.discovery;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.discovery.PingInputData;
import it.grid.storm.synchcall.data.discovery.PingOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingConverter implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(PingConverter.class);

    public PingConverter() {}

    public InputData convertToInputData(Map inputParam)
    {
        log.debug("Ping: input converter started. InputParam ");

        /* Retrieve the Requestor */
        GridUserInterface requestor = null;
        requestor = GridUserManager.decode(inputParam);
        //requestor = VomsGridUser.decode(inputParam);

        /* Retrieve the String AuthorizationID */
        String authorizationID = (String) inputParam.get("authorizationID");

        /* Build the InputData used by Executor */
        PingInputData inputData = new PingInputData(requestor, authorizationID);
        log.debug("Ping: input converter has finished.");
        return inputData;
    }

    public Map convertFromOutputData(OutputData data) {

		log.debug("Ping: output converter started.");
		Hashtable<String, String> outputParam = new Hashtable<String, String>();
		PingOutputData outputData = (PingOutputData) data;
		String versionInfo = outputData.getVersionInfo();
		if(versionInfo != null)
		{
			outputParam.put("versionInfo", versionInfo);
		}

		ArrayOfTExtraInfo extraInfoArray = outputData.getExtraInfoArray();
		if(extraInfoArray != null)
		{
			extraInfoArray.encode(outputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
		}

		log.debug("Ping: output converter has finished.");
		return outputParam;
	}
}
