/**
 * This class represents the Type Converter for GetSpaceTokens function.
 * This class gets input data from xmlrpc call and converts it into a
 * StoRM Type that can be used to invoke the GetSpaceTokensExecutor
 *
 * @author  Alberto Forti
 * @author  CNAF -INFN Bologna
 * @date    November 2006
 * @version 1.0
 */

package it.grid.storm.xmlrpc.converter.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.GetSpaceTokensInputData;
import it.grid.storm.synchcall.data.space.GetSpaceTokensOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSpaceTokensConverter implements Converter
{
    // Logger
    private static final Logger log = LoggerFactory.getLogger(GetSpaceTokensConverter.class);

    public GetSpaceTokensConverter() {};

    /**
     * Returns an instance of GetSpaceTokenInputData from a Hashtable structure
     * created by a xmlrpc GetSpaceTokens v2.2 call.
     */
    public InputData convertToInputData(Map inputParam)
    {
        GetSpaceTokensInputData inputData = null;

        String memberName = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        /* (2) userSpaceTokenDescription */
        memberName = new String("userSpaceTokenDescription");
        String userSpaceTokenDescription = (String) inputParam.get(memberName);

        inputData = new GetSpaceTokensInputData((VomsGridUser) guser, userSpaceTokenDescription);

        return inputData;
    }

    public Map convertFromOutputData(OutputData data)
    {
        log.debug("GetSpaceTokensConverter. Creation of XMLRPC Output Structure! ");

        // Creation of new Hashtable to return
        Hashtable outputParam = new Hashtable();

        GetSpaceTokensOutputData outputData = (GetSpaceTokensOutputData) data;

        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }

        /* (2) arrayOfSpaceTokens */
        ArrayOfTSpaceToken arrayOfSpaceTokens = outputData.getArrayOfSpaceTokens();
        if (arrayOfSpaceTokens != null) {
            arrayOfSpaceTokens.encode(outputParam, ArrayOfTSpaceToken.PNAME_ARRAYOFSPACETOKENS);
        }

        log.debug("Sending: " + outputParam.toString());

        //Return output Parameter structure
        return outputParam;
    }
}