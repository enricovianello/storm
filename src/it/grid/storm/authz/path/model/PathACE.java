/**
 * 
 */
package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathACE {

    private final Logger log = AuthzDirector.getLogger();

    public static final String ALL_GROUPS = "@ALL@?|\\*";
    private static final Pattern allGroupsPattern = Pattern.compile(ALL_GROUPS);
    public static final String FIELD_SEP = "\\s"; // * White space character **/
    private static final boolean PERMIT_ACE = true;
    public static final String ALGORITHM = "algorithm"; // property key used to define the algorithm

    public static final PathACE PERMIT_ALL = new PathACE(PathACE.ALL_GROUPS,
                                                         StFN.makeEmpty(),
                                                         PathAccessMask.DEFAULT,
                                                         PathACE.PERMIT_ACE);
    public static final String COMMENT = "#";

    private String localGroupName;
    private StFN storageFileName;
    private PathAccessMask pathAccessMask;
    private boolean isPermitACE;

    // =========== CONSTRUCTORs ============

    public PathACE(String localGroup, StFN stfn, PathAccessMask accessMask, boolean permitACE) {
        localGroupName = localGroup;
        storageFileName = stfn;
        pathAccessMask = accessMask;
        isPermitACE = permitACE;
    }

    public PathACE() {
        localGroupName = null;
        storageFileName = StFN.makeEmpty();
        pathAccessMask = PathAccessMask.DEFAULT;
        isPermitACE = PathACE.PERMIT_ACE;
    }

    /**
     * @param pathACEString
     * @return
     * @throws AuthzException
     */
    public static PathACE buildFromString(String pathACEString) throws AuthzException {
        PathACE result = new PathACE();
        String[] fieldsRough = pathACEString.split(PathACE.FIELD_SEP, -1);
        // Remove empty fields
        ArrayList<String> fields = new ArrayList<String>();
        for (String element : fieldsRough) {
            if (element.length() > 0) {
                fields.add(element);
            }
        }
        if (fields.size() < 4) {
            throw new AuthzException("Error while parsing the Path ACE '" + pathACEString + "'");
        } else {
            // Setting the Local Group Name
            result.setLocalGroupName(fields.get(0));

            // Setting the StFN
            try {
                StFN stfn = StFN.make(fields.get(1));
                result.setStorageFileName(stfn);
            } catch (InvalidStFNAttributeException e) {
                throw new AuthzException("Error while parsing the StFN '" + fields.get(1) + "' in Path ACE ");
            }

            // Setting the Permission Mask
            PathAccessMask pAccessMask = new PathAccessMask();
            for (int i = 0; i < fields.get(2).length(); i++) {
                PathOperation pathOper = PathOperation.getSpaceOperation(fields.get(2).charAt(i));
                pAccessMask.addPathOperation(pathOper);
            }
            result.setPathAccessMask(pAccessMask);

            // Check if the ACE is DENY or PERMIT
            // ** IMP ** : permit is the default
            if (fields.get(3).toLowerCase().equals("deny")) {
                result.setIsPermitType(false);
            } else {
                result.setIsPermitType(true);
            }
        }
        return result;
    }

    public void setLocalGroupName(String localGroup) {
        localGroupName = localGroup;
    }

    public void setStorageFileName(StFN stfn) {
        storageFileName = stfn;
    }

    public void setPathAccessMask(PathAccessMask accessMask) {
        pathAccessMask = accessMask;
    }

    public void setIsPermitType(boolean value) {
        isPermitACE = value;
    }

    public String getLocalGroupName() {
        return localGroupName;
    }

    public StFN getStorageFileName() {
        return storageFileName;
    }

    public PathAccessMask getPathAccessMask() {
        return pathAccessMask;
    }

    public boolean isPermitAce() {
        return isPermitACE;
    }

    /**
     * ## BUSINESS Methods
     */

    public boolean subjectMatch(String subjectGroup) {
        boolean result = false;
        Matcher allGroupsMatcher = allGroupsPattern.matcher(subjectGroup);
        if (localGroupName != null) {
            if (allGroupsMatcher.matches()) {
                result = true;
                log.debug("ACE (" + toString() + ") matches with subject '" + subjectGroup + "'");
            } else {
                if (localGroupName.equals(subjectGroup)) {
                    result = true;
                    log.debug("ACE (" + toString() + ") matches with subject '" + subjectGroup + "'");
                }
            }
        }
        return result;
    }

    /**
     * 
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof PathACE) {
            PathACE pathOther = (PathACE) other;
            if (pathOther.getLocalGroupName() == localGroupName) {
                if (pathOther.getStorageFileName().equals(storageFileName)) {
                    if (pathOther.getPathAccessMask().equals(pathAccessMask)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result += 31 * result + (localGroupName == null ? 0 : localGroupName.hashCode());
        result += 31 * result + (storageFileName == null ? 0 : storageFileName.hashCode());
        result += 31 * result + (pathAccessMask == null ? 0 : pathAccessMask.hashCode());
        return result;
    }

    @Override
    public String toString() {
        String result = "";
        if (localGroupName == null) {
            result += "NULL";
        } else {
            result += localGroupName;
        }
        result += " ";
        result += storageFileName;
        result += " ";
        result += pathAccessMask;
        return result;
    }

}
