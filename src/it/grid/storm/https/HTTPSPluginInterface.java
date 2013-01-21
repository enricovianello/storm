/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.grid.storm.https;


import it.grid.storm.acl.AclManagementInterface;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;

/**
 * @author Michele Dibenedetto
 */
public interface HTTPSPluginInterface extends AclManagementInterface  
{

    public String mapLocalPath(String hostname, String localAbsolutePath) throws HTTPSPluginException;
    
    public ServiceStatus getServiceStatus(String hostname, int port, Protocol protocol) throws HTTPSPluginException;

    public void grantServiceGroupPermission(LocalFile localFile, FilesystemPermission permission);

    public void grantServiceUserPermission(LocalFile localFile, FilesystemPermission permission);
    
    public enum Protocol{
        HTTP, HTTPS;
    }
    
    public enum ServiceStatus
    {
        RUNNING, NOT_RESPONDING, UNEXPECTED_BEHAVIOUR;
    }
    
}
