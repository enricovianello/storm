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


/**
 * @author Michele Dibenedetto
 */
public enum SupportedFSType
{
    EXT3, GPFS, XFS;

    /**
     * Parses the provided fsString and returns the matching SupportedFSType
     * 
     * @param fsString
     * @return
     * @throws IllegalArgumentException if the provided fsString does not match any SupportedFSType
     */
    public static SupportedFSType parseFS(String fsString) throws IllegalArgumentException
    {
        if (fsString.trim().equals("xfs"))
        {
            return SupportedFSType.XFS;
        }
        if (fsString.trim().equals("gpfs"))
        {
            return SupportedFSType.GPFS;
        }
        if (fsString.trim().equals("ext3"))
        {
            return SupportedFSType.EXT3;
        }
        throw new IllegalArgumentException("Unable to parse file system string \'" + fsString
                + "\' No matching value available");
    }
}