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

/**
 * 
 */
package it.grid.storm.tape.recalltable.resources;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 * 
 */
@Path("/recalltable/cardinality/tasks/")
public class TasksCardinality {

    private static final Logger log = LoggerFactory.getLogger(TasksCardinality.class);

    @GET
    @Path("/queued")
    @Produces("text/plain")
    public String getNumberQueued() throws TapeRecallException {

        // Variables used to create the result
        String numberQueued = "";
        int nQueued = -1;

        // String used to built the error message
        String errorStr = null;

        // Recall Table Catalog
        TapeRecallCatalog rtCat = null;
        try {
            rtCat = new TapeRecallCatalog();
            nQueued = rtCat.getNumberTaskQueued();
            if (nQueued > 0) {
                log.info("Number of tasks queued = " + nQueued);
            } else {
                log.trace("Number of tasks queued = " + nQueued);
            }
            numberQueued += nQueued;
        } catch (DataAccessException e) {
            errorStr = "Unable to use RecallTable DB.";
            log.error(errorStr);
            throw new TapeRecallException(errorStr);
        }
        return numberQueued;
    }

    @GET
    @Path("/readyTakeOver")
    @Produces("text/plain")
    public String getReadyForTakeover() throws TapeRecallException {

        // Variables used to create the result
        String numberReadyForTakeover = "";
        int nReadyForTakeover = -1;

        // String used to built the error message
        String errorStr = null;

        // Recall Table Catalog
        TapeRecallCatalog rtCat = null;
        try {
            rtCat = new TapeRecallCatalog();
            nReadyForTakeover = rtCat.getReadyForTakeOver();
            log.debug("Number of tasks queued = " + nReadyForTakeover);
            numberReadyForTakeover += nReadyForTakeover;
        } catch (DataAccessException e) {
            errorStr = "Unable to use RecallTable DB.";
            log.error(errorStr);
            throw new TapeRecallException(errorStr);
        }
        return numberReadyForTakeover;
    }

}