/**
 * This class represents a TExtraInfoArray
 *
 * @author  EGRID ICTP Trieste / CNAF Bologna
 * @date    March 23rd, 2005
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ArrayOfSURLs implements Serializable {

    private static final long serialVersionUID = -6162739978949956886L;

    public static String ARRAYOFSURLS = "arrayOfSURLs";

    ArrayList<TSURL> surlList;

    /**
     * Constructor that requires a String. If it is null, then an InvalidArrayOfTExtraInfoAttributeException is thrown.
     */
    public ArrayOfSURLs(TSURL[] surlArray) throws InvalidArrayOfSURLsAttributeException {

        if (surlArray == null)
            throw new InvalidArrayOfSURLsAttributeException(null);
        // FIXME this.tokenArray = tokenArray;
        // surlList = Arrays.asList(surlArray);
    }

    public ArrayOfSURLs() {
        surlList = new ArrayList<TSURL>();
    }

    public List<TSURL> getArrayList() {
        return surlList;
    }

    public TSURL getTSURL(int i) {
        return (TSURL) surlList.get(i);
    }

    public void setTSURL(int index, TSURL surl) {
        surlList.set(index, surl);
    }

    public void addTSURL(TSURL surl) {
        surlList.add(surl);
    }

    public int size() {
        return surlList.size();
    }

	public static ArrayOfSURLs decode(Map inputParam, String name)
			throws InvalidArrayOfSURLsAttributeException {

        List list = null;
		ArrayOfSURLs surlArray = new ArrayOfSURLs();
        try {
			list = Arrays.asList((Object[]) inputParam.get(name));
        } catch (NullPointerException e) {
			// log.warn("Empty SURL array found!");
		}

		if(list == null)
			throw new InvalidArrayOfSURLsAttributeException(list);			
        for (int i = 0; i < list.size(); i++) {
			TSURL surl = null;
            try {
                surl = TSURL.makeFromStringValidate((String) list.get(i));
            } catch (InvalidTSURLAttributesException e) {
				throw new InvalidArrayOfSURLsAttributeException(null);
			}
			surlArray.addTSURL(surl);
		}
		return surlArray;
	}

    public String toString() {
        StringBuffer buf = new StringBuffer("");

        if (surlList != null) {
            for (int i = 0; i < surlList.size(); i++) {
                buf.append("'" + (TSURL) surlList.get(i) + "'");
                if (i + 1 < surlList.size())
                    buf.append(",");
            }

        } else {
            return buf.toString();
        }

        return buf.toString();

    }
}
