/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.TReturnStatus;

public class InvalidGetSpaceMetaDataOutputAttributeException extends Exception {

    private boolean nullStatus = true;
    private boolean nullMeta = true;

    public InvalidGetSpaceMetaDataOutputAttributeException(TReturnStatus status, ArrayOfTMetaDataSpace meta)
    {
	nullStatus = (status==null);
	nullMeta = (meta==null);
    }


    public String toString()
    {
	return "nullStatus = "+nullStatus+"- nullArray = "+nullMeta;
    }
}