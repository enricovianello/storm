package it.grid.storm.synchcall.data.space;


import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.griduser.VomsGridUser;

/**
 * This class represents the SpaceReservationData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * Number of files progressing, Number of files finished, and whether the request
 * is currently suspended.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */



public class ReleaseSpaceInputData implements InputData {

    private VomsGridUser auth = null;
    private TSpaceToken spaceToken = null;
    private ArrayOfTExtraInfo storageInfo = null;
    private boolean forceFileRelease = false;

    public ReleaseSpaceInputData(VomsGridUser auth, TSpaceToken spaceToken, ArrayOfTExtraInfo storageInfo,
			    boolean forceFileRelease) throws InvalidReleaseSpaceAttributesException
    {

    	boolean ok = auth!=null&&spaceToken!=null;
    	if (!ok) {
    	    throw new InvalidReleaseSpaceAttributesException(auth, spaceToken);
    	}
    	this.auth = auth;
    	this.spaceToken = spaceToken;
    	this.storageInfo = storageInfo;
    	this.forceFileRelease = forceFileRelease;

    }


    /**
     * Method that returns UserID specify in SRM request.
     */
    public VomsGridUser getUser()
    {
        	return auth;
    }


    /**
     * Method that returns the SpaceToken.
     */
    public TSpaceToken getSpaceToken()
    {
        return spaceToken;
    }


    public boolean getForceFileRelease() {
       return forceFileRelease;
    }

}
