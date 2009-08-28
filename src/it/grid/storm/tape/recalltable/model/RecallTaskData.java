/**
 * 
 */
package it.grid.storm.tape.recalltable.model;

import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.tape.recalltable.RecallTableException;
import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 * 
 *         This class is the representation of a Recall Task data. Only
 *         user-settable data are writable. The rest data are only read.
 * 
 * 
 *         { "stfn":"<file-name>", "dn":"<DN>", "fqans":["fqan":"<FQAN>",
 *         "fqan":"<FQAN>"], "vo-name":"<vo-name>" }
 * 
 * 
 */
public class RecallTaskData {

    private static final Logger log = LoggerFactory.getLogger(RecallTaskBuilder.class);
    
 
        
    private String fileName = null;
    private String userDN = null;
    private ArrayList<FQAN> fqans = null;
    private String[] fqansString = null;
    private String voName = null;
    public static String UNSPECIFIED = "unspecified-VO";

    /**
     * Constructor with x.509 certificate
     * 
     * @param filename
     * @param user
     */
    public RecallTaskData(String filename, GridUserInterface user) {
        fileName = filename;
        if (user instanceof VomsGridUser) {
            VomsGridUser vu = (VomsGridUser) user;
            fqans = new ArrayList<FQAN>(vu.getFQANsList());
            fqansString = vu.getFQANsString();
            voName = vu.getVO().getValue();
        } else {
            voName = RecallTaskData.UNSPECIFIED;
        }
        userDN = user.getDistinguishedName().getDN();
    }
    
    /**
     * 
     * @param filename
     * @param dn
     * @param fqans
     * @param voName
     */
    public RecallTaskData(String filename, String dn, String[] fqans, String voName) {
        fileName = filename;
        DistinguishedName dn500 = (new DistinguishedName(dn));
        userDN = dn500.getX500DN_rfc2253();
        fqansString = fqans;
        this.voName = voName;
    }



    /**
     * 
     * 
     * { "filename":"<file-name>"; "dn":"<DN>"; "fqans":["fqan":"<FQAN>",
     * "fqan":"<FQAN>"]; "vo-name":"<vo-name>" }
     * 
     * 
     * @param textFormat
     * @throws RecallTableException
     */
    public RecallTaskData(String textFormat) throws RecallTableException {
        //Parsing of the String
        if (textFormat.startsWith(RecallTaskBuilder.taskStart)) {
            
            if (textFormat.contains(RecallTaskBuilder.taskEnd)) {
                int beginIndex = 1;
                int endIndex = textFormat.indexOf(RecallTaskBuilder.taskEnd);
                String taskDataText = textFormat.substring(beginIndex, endIndex);
                int fnPos = taskDataText.indexOf(RecallTaskBuilder.fnPrefix);
                if (fnPos < 0) {
                    throw new RecallTableException("Unable to find the File Name");
                } else {
                    // Parsing the elements
                    int dnPos = taskDataText.indexOf(RecallTaskBuilder.dnPrefix);
                    if (dnPos < 0) {
                        log.warn("Task Data without User DN");
                    }
                    int fqansPos = taskDataText.indexOf(RecallTaskBuilder.fqansPrefix);
                    if (fqansPos < 1) {
                        log.warn("Task Data without User FQANs");
                    }
                    int voNamePos = taskDataText.indexOf(RecallTaskBuilder.voNamePrefix);
                    if (voNamePos < 1) {
                        log.warn("Task Data without VO Name");
                    }
                    fileName = parseFN(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    userDN = parseDN(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    fqans = parseFQANs(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    voName = parseVOName(taskDataText, fnPos, dnPos, fqansPos, voNamePos);
                    log.debug("filename='" + fileName + "'");
                    log.debug("userDN  ='" + userDN + "'");
                    log.debug("fqans='" + fqans + "'");
                    log.debug("voName='" + voName + "'");
                }
            } else {
                throw new RecallTableException("Unable to find the File Name");
            }
        }
    }

    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private String parseFN(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        String result = null;
        if (dnPos > 0) {
            result = taskDataText.substring(fnPos, dnPos);
        } else if (fqansPos > 0) {
            result = taskDataText.substring(fnPos, fqansPos);
        } else if (voNamePos > 0) {
            result = taskDataText.substring(fnPos, voNamePos);
        } else {
            result = taskDataText.substring(fnPos);
        }
        log.debug("FN to parse ='" + result + "'");
        int pos = result.indexOf(RecallTaskBuilder.elementSep);
        pos = pos < 0 ? result.length() : pos;
        result = result.substring(RecallTaskBuilder.fnPrefix.length(), pos);
        log.debug("FN parsed ='" + result + "'");
        return result;
    }

    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private String parseDN(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        String result = null;
        if (dnPos > 0) {
            if (fqansPos > 0) {
                result = taskDataText.substring(dnPos, fqansPos - 1);
                log.debug("dn to parse (dnPos=" + dnPos + " fqansPos=" + fqansPos + ": " + result);
            } else if (voNamePos > 0) {
                result = taskDataText.substring(dnPos, voNamePos - 1);
            } else {
                result = taskDataText.substring(dnPos);
            }
        } else {
            result = "no-DN";
        }
        // Trim out the prefix and the separator (if there)
        int pos = result.indexOf(RecallTaskBuilder.elementSep);
        pos = pos < 0 ? result.length() : pos;
        result = result.substring(RecallTaskBuilder.dnPrefix.length(), pos);
        log.debug("DN to parse ='" + result + "'");
        
        log.warn("## TODO Feature ## : Bug in parsing DN in RFC 2253 format.");
        /**
         * ############### TEMPORARY FIX
         */
        // DistinguishedName dn = new DistinguishedName(result);
        // result = dn.getX500DN_rfc1779();
        if (result.contains("CN=")) {
            int beginIndex = result.indexOf("CN=") + 3;
            result = result.substring(beginIndex);
            int endIndex = result.contains(",") ? result.indexOf(',') : result.length();
            result = result.substring(0, endIndex);
            endIndex = result.contains("/") ? result.indexOf('/') : result.length();
            result = result.substring(0, endIndex);
        }
        /**
         * ################# END TEMPORARY FIX
         */
        
        log.debug("DN parsed ='" + result + "'");
        return result;
    }

    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private ArrayList<FQAN> parseFQANs(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        ArrayList<FQAN> result = new ArrayList<FQAN>();
        String fqansSt = null;
        if (fqansPos > 0) {
            if (voNamePos > 0) {
                fqansSt = taskDataText.substring(fqansPos, voNamePos);
            } else {
                fqansSt = taskDataText.substring(fqansPos);
            }
            
            // Trim out the prefix and the separator (if there)
            int pos = fqansSt.indexOf(RecallTaskBuilder.elementSep);
            pos = pos < 0 ? fqansSt.length() : pos;
            fqansSt = fqansSt.substring(RecallTaskBuilder.fqansPrefix.length(), pos);
            log.debug(fqansSt);
            
            pos = fqansSt.indexOf(RecallTaskBuilder.fqansArrayEnd);
            fqansSt = fqansSt.substring(RecallTaskBuilder.fqansArrayStart.length(), pos);
            log.debug(fqansSt);
            
            String[] fqans = fqansSt.split(RecallTaskBuilder.fqanSep);
            fqansString = new String[fqans.length]; 
            FQAN fqan;
            int count = 0;
            for (String fqanIter : fqans) {
                log.debug("fqanString = '" + fqanIter + "'");
                // Trim out the prefix and the separator (if there)
                pos = fqansSt.indexOf(RecallTaskBuilder.fqanSep);
                pos = pos < 0 ? fqansSt.length() : pos;
                fqanIter = fqanIter.substring(RecallTaskBuilder.fqanPrefix.length(), pos);
                log.debug("fqanString (purged) = '" + fqanIter + "'");
                fqan = new FQAN(fqanIter);
                result.add(fqan);
                fqansString[count] = fqan.toString();
                count++;
            }
        } else {
            log.debug("no FQANs are present into RecallTask Data.");
        }
        return result;
    }
    
    
    /**
     * @param taskDataText
     * @param fnPos
     * @param dnPos
     * @param fqansPos
     * @param voNamePos
     * @return
     */
    private String parseVOName(String taskDataText, int fnPos, int dnPos, int fqansPos, int voNamePos) {
        String result = null;
        if (voNamePos > 0) {
            result = taskDataText.substring(voNamePos);
            log.debug("vo-name to parse ='" + result + "'");
            int pos = result.indexOf(RecallTaskBuilder.elementSep);
            pos = pos < 0 ? result.length() : pos;
            result = result.substring(RecallTaskBuilder.voNamePrefix.length(), pos);
        } else {
            if (fqans != null) {
                FQAN fqan = fqans.get(0);
                result = fqan.getVo();
            } else {
                result = "no-voname";
            }
        }
        
        log.debug("vo-name parsed ='" + result + "'");
        return result;
    }







    /**
     * 
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 
     * @return
     */
    public String getUserDN() {
        return userDN;
    }

    /**
     * 
     * @return
     */
    public String[] getFqans() {
        
        if ((fqans != null) && (fqans.size() > 0)) {
            fqansString = new String[fqans.size()];
            int count = 0;
            for (FQAN fqan : fqans) {
                fqansString[count] = fqan.toString();
                count++;
            }
        }
        return fqansString;
    }

    
    public String getUserID() {
        String userId = "anonymous";
        if (getUserDN() != null) {
            userId = getUserDN();
            if (getFqansTextFormat() != null) {
                userId += " - " + getFqansTextFormat();
            }
        }
        return userId;
    }
    
    /**
     * 
     * @return
     */
    public String getVoName() {
        return voName;
    }    
    
    
    /**
     * 
     * @return
     */
    private String getFileNameTextFormat() {
        String result = "";
        result += RecallTaskBuilder.fnPrefix;
        result += getFileName();
        return result;
    }

    /**
     * 
     * @return
     */
    private String getUserDNTextFormat() {
        String result = "";
        result += RecallTaskBuilder.dnPrefix;
        result += getUserDN();
        return result;
    }

    /**
     * 
     * @return
     */
    private String getFqansTextFormat() {
        String result = "";
        int fqansNum = (fqansString != null ? fqansString.length : 0);
        result += RecallTaskBuilder.fqansPrefix;
        result += RecallTaskBuilder.fqansArrayStart;
        for (int i = 0; i < fqansNum; i++) {
            result += fqansString[i];
            if (i < (fqansNum - 1)) {
                result += RecallTaskBuilder.fqanSep;
            }
        }
        result += RecallTaskBuilder.fqansArrayEnd;
        return result;
    }

    /**
     * 
     * @return
     */
    private String getVONameTextFormat() {
        String result = "";
        result += RecallTaskBuilder.voNamePrefix;
        result += getVoName();
        return result;
    }

    /**
     * 
     */
    public String getRecallTaskData_textFormat() {
        String result = "";
        result += RecallTaskBuilder.taskStart;
        result += getFileNameTextFormat() + RecallTaskBuilder.elementSep;
        result += getUserDNTextFormat() + RecallTaskBuilder.elementSep;
        result += getFqansTextFormat() + RecallTaskBuilder.elementSep;
        result += getVONameTextFormat();
        result += RecallTaskBuilder.taskEnd;
        return result;
    }
    
}
