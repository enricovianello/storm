/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.check.sanity.filesystem;


import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.check.Check;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.naming.NamespaceUtil;

/**
 * @author Michele Dibenedetto
 */
public class NamespaceFSAssociationCheck implements Check
{

    private static final Logger log = LoggerFactory.getLogger(NamespaceFSAssociationCheck.class);

    private static final String CHECK_NAME = "NamespaceFSvalidation";

    private static final String CHECK_DESCRIPTION = "This check verifies that the file system type declared in namespace.xml and phisical filesystem type matches";

    private static final boolean criticalCheck = true;

    private static final String POSIX_FILESYSTEM_TYPE = "ext3";

    private Map<String, String> mountPoints; 

    private Collection<VirtualFSInterface> vfsSet;
    
    private NamespaceFSAssociationCheck(){};
    
    /**
     * @param mountPoints
     * @param vfsSet
     * @throws IllegalArgumentException
     */
    public NamespaceFSAssociationCheck(Map<String, String> mountPoints , Collection<VirtualFSInterface> vfsSet) throws IllegalArgumentException
    {
        this();
        if(mountPoints == null || vfsSet == null)
        {
            log.error("Unable to create NamespaceFSAssociationCheck received null arguments: mountPoints=" + mountPoints + " vfsSet=" + vfsSet);
            throw new IllegalArgumentException("Unable to create NamespaceFSAssociationCheck received null arguments");
        }
        if(!verifyMountPoints(mountPoints))
        {
            log.error("Unable to create NamespaceFSAssociationCheck received invalid mountPoints");
            throw new IllegalArgumentException("Unable to create NamespaceFSAssociationCheck received invalid mountPoints");
        }
        if(!verifyVfsSet(vfsSet))
        {
            log.error("Unable to create NamespaceFSAssociationCheck received invalid vfsSet");
            throw new IllegalArgumentException("Unable to create NamespaceFSAssociationCheck received invalid vfsSet");
        }
        this.mountPoints = mountPoints;
        this.vfsSet = vfsSet;
    };
    
    /**
     * @param vfsSet
     * @return
     */
    private boolean verifyVfsSet(Collection<VirtualFSInterface> vfsSet)
    {
        for (VirtualFSInterface vfs : vfsSet)
        {
            if(vfs == null)
            {
                log.info("The vfsSet contains null entries");
                return false;
            }
            try
            {
                if(vfs.getFSType() == null)
                {
                    log.info("The vfs " + vfs.getAliasName() + " has null FSType");
                    return false;
                }
            }
            catch (NamespaceException e)
            {
                log.info("Unable to get FSType from vfs " + vfs);
                return false;
            }
            try
            {
                if(vfs.getRootPath() == null)
                {
                    log.info("The vfs " + vfs.getAliasName() + " has null rootPath");
                    return false;
                }
            }
            catch (NamespaceException e)
            {
                log.info("Unable to get rootPath from vfs " + vfs);
                return false;
            }
            
        }
        log.debug("verifyVfsSet: vfsSet is valid");
        return true;
    }

    /**
     * @param mountPoints
     * @return
     */
    private boolean verifyMountPoints(Map<String, String> mountPoints)
    {
        for(String key : mountPoints.keySet())
        {
            if(key == null)
            {
                log.info("The mountPoints map contains null keys");
                return false;
            }
            if(mountPoints.get(key) == null)
            {
                log.info("The mountPoint key " + key + " points to a null value");
                return false;
            }
        }
        log.debug("verifyMountPoints: mountPoints is valid");
        return true;
    }

    /*
     * (non-Javadoc)
     * @see it.grid.storm.check.Check#execute()
     */
    @Override
    public CheckResponse execute() throws GenericCheckException
    {
        CheckStatus status = CheckStatus.SUCCESS;
        String errorMessage = "";
            try
            {
                for (VirtualFSInterface vfs : vfsSet)
                {
                    //check if is simple posix FS 
                    boolean currentResponse = verifyPosixDeclaredFS(vfs.getFSType());
                    if(!currentResponse)
                    {
                    // check their association against mtab
                        currentResponse = this.check(vfs.getRootPath(), vfs.getFSType(), mountPoints);
                    } 
                    if (!currentResponse)
                    {
                        log.error("Check on VFS " + vfs.getAliasName() + " failed. Type ="
                                + vfs.getFSType() + " , root path =" + vfs.getRootPath());
                        errorMessage += "Check on VFS " + vfs.getAliasName() + " failed. Type ="
                                + vfs.getFSType() + " , root path =" + vfs.getRootPath();
                    }
                    log.debug("Check response for path " + vfs.getRootPath() + " is "
                            + (currentResponse ? "success" : "failure"));
                    status = CheckStatus.and(status, currentResponse);
                    log.debug("Partial result is " + status.toString());
                }
            }
            catch (NamespaceException e)
            {
                // NOTE: this exception is never thrown
                log.warn("Unexpected NamespaceException, unable to proceede : " + e.getMessage());
                errorMessage += "Unable to proceede received a NamespaceException : " + e.getMessage() + "; ";
                status = CheckStatus.INDETERMINATE;
            }
        return new CheckResponse(status, errorMessage);
    }

    /**
     * @param string
     * @return
     */
    private boolean verifyPosixDeclaredFS(String FSType) throws IllegalArgumentException
    {
        if(FSType == null)
        {
            log.error("Unable to check posix filesystem declaration received null argument: FSType= " + FSType);
            throw new IllegalArgumentException("Unable to check posix filesystem declaration received null argument");
        }
        return POSIX_FILESYSTEM_TYPE.equals(FSType.trim());
    }

    /**
     * Checks if the provided fsRootPath in the provided mountPoints map has the provided fsType
     * 
     * @param fsRootPath
     * @param fsType
     * @param mountPoints
     * @return
     */
    private boolean check(String fsRootPath, String fsType, Map<String, String> mountPoints)
    {
        boolean response = false;
        log.debug("Checking fs at " + fsRootPath + " as a " + fsType);
        
        String mountPointFSType = getMountPointFSTypeBestmatch(fsRootPath, mountPoints);
        if(mountPointFSType != null)
        {
            log.debug("Found on a mountPoint of a \'" + mountPointFSType + "\' FS");
            if (fsType.equals(mountPointFSType))
            {
                response = true;
            }
            else
            {
                log.warn("Mount point File System type " + mountPointFSType
                        + " differs from the declared " + fsType + ". Check failed");
            }
        }
        else
        {
            log.warn("No file systems are mounted at path " + fsRootPath + "! Check failed");
        }
        return response;
    }


    /**
     * @param fsRootPath
     * @param mountPoints
     * @return
     */
    private String getMountPointFSTypeBestmatch(String fsRootPath, Map<String, String> mountPoints)
    {
        String fsType = null;
        int minDistance = -1;
        int pathSize = conputePathSize(fsRootPath);
        for (String mountPoint : mountPoints.keySet())
        {
            if(conputePathSize(mountPoint) > pathSize)
            {
                continue;
            }
            int distance = NamespaceUtil.computeDistanceFromPath(fsRootPath , mountPoint);
            if(distance >= 0 && distance <= pathSize)
            {
                //is contained
                if (fsType == null || distance < minDistance)
                {
                    minDistance = distance;
                    fsType = mountPoints.get(mountPoint).trim();
                }
            }
        }
        return fsType;
    }

    /**
     * @param fsRootPath
     * @return
     */
    private int conputePathSize(String fsRootPath)
    {
        //If on windows...this split can be a problem
        String[] elements = fsRootPath.split("/");
        int counter = 0;
        for(String element : elements)
        {
            if(element.trim().length() > 0)
            {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public String getName()
    {
        return CHECK_NAME;
    }

    @Override
    public String getDescription()
    {
        return CHECK_DESCRIPTION;
    }
    
    @Override
    public boolean isCritical()
    {
        return criticalCheck;
    }
}