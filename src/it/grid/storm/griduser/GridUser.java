/*
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 * For a copy of the licence please visit
 *
 *    http://www.cnaf.infn.it/license.html
 *
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 *
 * $Id: GridUser.java 3604 2007-05-22 11:16:27Z rzappi $
 *
 */

package it.grid.storm.griduser;

import it.grid.storm.common.types.*;

/**
 * Title:
 * <p>
 * Description:
 * <p>
 * Copyright: Copyright (c) 2006
 * <p>
 * Company: INFN-CNAF
 * </p>
 * 
 * @author R.Zappi
 * @version 1.0
 */
public class GridUser extends AbstractGridUser implements GridUserInterface {

    GridUser(MapperInterface mapper) {
        super(mapper);
    }

    GridUser(MapperInterface mapper, String distinguishedName) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
    }

    /**
     * Return the main Virtual Organization of the User. Since User is presenting without VOMS Proxy, then default VO is
     * named NO_VO.
     * 
     * @return VO
     */
    public VO getVO() {
        VO vo = VO.makeNoVo();
        return vo;
    }

    /**
     * Return the local user on which the GridUser is mapped. Note that the mapping is done at Mapper construction time.
     * 
     * @throws CannotMapUserException
     * @return LocalUser
     */
    public LocalUser getLocalUser() throws CannotMapUserException {
        if (localUser == null) {
            try {

                localUser = userMapperClass.map(getDn(), null);
            } catch (CannotMapUserException ex) {
                // log the operation that failed
                log.error("Error in mapping '" + subjectDN.getX500DN_rfc1779() + "' to a local user: "
                        + ex.getMessage());
                // re-thorw same exception
                throw ex;
            }
        }
        return localUser;
    }

    public String toString() {
        return "Grid User (no VOMS): '" + getDistinguishedName().getX500DN_rfc1779() + "'";
    }

}