/**
 * This is the Abort executor for a PtP request.
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.space.ReserveSpaceCommand;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PtPAbortExecutor implements AbortExecutorInterface {

    private static final Logger log = LoggerFactory.getLogger(PtPAbortExecutor.class);

    //TODO DEFINE THE TIMEOUT FROM CONFIGURATION
    // CMQ sotto i 3 minuti per discorso tcp timeout?

    static Configuration  config        = Configuration.getInstance();
    private static int maxLoopTimes = PtPAbortExecutor.config.getMaxLoop();
    //private static int maxLoopTimes = 100;

    private NamespaceInterface namespace;



    public PtPAbortExecutor() {};

    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData) {
        //Used to delete the physical file
        namespace = NamespaceDirector.getNamespace();

        AbortGeneralOutputData outputData = new AbortGeneralOutputData();
        ArrayOfTSURLReturnStatus arrayOfTSurlRetStatus = new ArrayOfTSURLReturnStatus();

        boolean requestFailure, requestSuccess;
        TReturnStatus globalStatus = null;

        PtPAbortExecutor.log.debug("srmAbortRequest: Start PtPAbortExecutor...");

        ArrayOfSURLs surlArray = inputData.getArrayOfSURLs();

        /**
         * 0) Get all Chunk related to the specified request according with user specification (in case of AbortFiles).
         * 1) Wait until the request goes in SRM_SPACE_AVAILABLE status.
         * 2) Rollback. For a PtP request the rollback means to:
         * - PtP without space reservation:
         * Remove the file (if not existing? And if partially wrote?).
         * - PtP with implicit space reservation (size specified):
         * Remove the file.(the space is associated directly to the file.)
         * - PtP with SpaceToken:
         * Recreaete the SpaceFile, update the SpaceCatalog and remove the file.
         * - PtP with SpaceToken and size
         * Recreate the space, update the space file, update the space catalog
         * and remove the files
         */


        /****************** Phase 0 ******************/

        /* Query the DB to find all the Chunk associated to the request token */
        PtPChunkCatalog putCatalog = PtPChunkCatalog.getInstance();

        //Get the CHunk collection
        Collection chunks = putCatalog.lookup(inputData.getRequestToken());

        if(chunks.isEmpty()) { // No Chunk found
            PtPAbortExecutor.log.debug("PtPAbortExecutor: Request - Invalid request token");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /**
         * Get only the SURL requested in a AbortFile Request
         * Define a new Collection to contains the "epurated" chunk, removing the ones not specified
         * in input request
         */

        ArrayList newChunk = new ArrayList();

        if(inputData.getType() ==  AbortGeneralInputData.ABORT_FILES) {
            PtPAbortExecutor.log.debug("srmAbortFiles: PtPAbortExecutor: Case of AbortFile request. Purge Chunks with SurlArray.");

            /**
             * Get the related Chunk for each SURL in the input SurlArray.
             * If a Surl requested is not founf, the TSURLReturnStatus related is setted to SRM_INVALID_PATH
             */

            for(int i=0;i<surlArray.size();i++){

                TSURL surl = surlArray.getTSURL(i);
                PtPAbortExecutor.log.debug("srmAbortFiles: Checking SURL[" + i + "]: " + surl.toString());

                // auxChunkData stores information about a single item (SURL) in the catalog
                PtPChunkData auxChunkData = null;

                /* Search SURL[i] (specified in the request) inside the catalog */
                Iterator j = chunks.iterator();
                boolean surlFound = false;

                while ((j.hasNext())&&!surlFound) {
                    auxChunkData = (PtPChunkData) j.next();
                    if (surl.equals(auxChunkData.toSURL())) {
                        surlFound = true;
                    }
                }

                if(!surlFound) {
                    /**
                     * Surl not found.
                     * Specifiy an appropriate output status and add to the global SURLStatusArray
                     */
                    PtPAbortExecutor.log.debug("srmAbortFiles: Abort: SURL NOT found, invalid Chunk!Set TSURLReturnStatus");
                    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
                    surlReturnStatus.setSurl(surl);
                    surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INVALID_PATH, "SURL specified does not referes to this request token."));
                    PtPAbortExecutor.log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+(i+1)+" of "+surlArray.size()+" [SURL:"+surlArray.getTSURL(i)+"] failed  with [status: "+surlReturnStatus.getStatus()+"]");
                    arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);

                } else {
                    /**
                     * Add this chunk to the new chunk Container.
                     * The new Chunk container contains only the chunk to abort according on the user specification
                     * (using the SurlArray) parameter
                     */
                    PtPAbortExecutor.log.debug("Abort: Surl FOUND. Valid Chunk. Added to new chunk container.");
                    newChunk.add(auxChunkData);
                }

            } //for

            //Substitute the old Chunks to with the "epurated" version.
            PtPAbortExecutor.log.debug("Abort: Old chunk size ="+chunks.size());
            chunks = newChunk;
            PtPAbortExecutor.log.debug("Abort: New chunk size ="+chunks.size());


        }//if

        /********* Check here the new chunks container is not empty! ******/
        if(chunks.isEmpty()) { // No Chunk found
            PtPAbortExecutor.log.debug("Abort Request - No surl specified associated to request token");
            globalStatus = manageStatus(TStatusCode.SRM_FAILURE, "All surl specified does not referes to the request token.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
            PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }



        /********* Phase 1 Wait until the request goes in a status different from REQUEST_QUEUED ********/

        int  chunkAborted = 0;

        //To avoid deadlock
        int counter = 0;
        //NumberOfFailure for globalStatus
        int errorCount = 0;
        //Total initial Size
        int totalSize = chunks.size();

        while((chunkAborted<totalSize)&&(counter<PtPAbortExecutor.maxLoopTimes)) {
            //Increment loop times
            counter++;

            // auxChunkData stores information about a single item (SURL) in the catalog
            PtPChunkData auxChunkData = null;

            //This new Collection is used to avoid the ConcurrentModification exception
            //on the chsunks Collection

            Collection listOfChunk = new ArrayList();
            listOfChunk.addAll(chunks);
            Iterator j = listOfChunk.iterator();
            int numOfSurl=0;
            while((j.hasNext())) {
                numOfSurl++;
                auxChunkData = (PtPChunkData) j.next();

                /*
                 * To GET the actual status from DB the Chunk must be updated!
                 */
                PtPAbortExecutor.log.debug("PtPAbortExecutor: refresh chunk...");
                auxChunkData = putCatalog.refreshStatus(auxChunkData);
                PtPAbortExecutor.log.debug("PtPAbortExecutor: refresh done.");

                /*
                 * Check if any chunk have a status different from SRM_REQUEST_INPROGESS.
                 * That means the request execution is finished, and the rollback start.
                 */

                if (!(auxChunkData.status().getStatusCode().equals(TStatusCode.SRM_REQUEST_INPROGRESS ))) {
                    /*
                     * If an EXECUTED CHUNK is found, then it is ABORTED.
                     */
                    PtPAbortExecutor.log.debug("PtPAbortExecutor: PtPChunk not in IN_PROGRESS state. Ready for Abort.");
                    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();

                    /******** Phase (3) of Abort *************/
                    PtPAbortExecutor.log.debug("PtPAbortExecutor: start Phase(3)");

                    /*
                     * AvancePicker HACK!
                     * Due to a thread issue in the advancePicker.abortRequest(...) we have to
                     * check here if the chunk has already been aborted frome the picker.
                     * In that case, the manageAbort is not needed
                     */
                    if((auxChunkData.status().getStatusCode().equals(TStatusCode.SRM_ABORTED))) {
                        //The AdvancedPicker have already aborted the chunk.
                        PtPAbortExecutor.log.debug("PtPAbortExecutor: CHUNK already aborted!");
                        surlReturnStatus.setSurl(auxChunkData.toSURL());
                        surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request successfully aborted."));
                    } else {
                        PtPAbortExecutor.log.debug("PtPAbortExecutor:CHUNK to abort.");
                        //Chunk not ABORTED. We have to work...
                        surlReturnStatus = manageAuthorizedAbort(auxChunkData, inputData.getUser());
                    }

                    //Remove this chunks from the other to abort.
                    chunks.remove(auxChunkData);


                    if((surlReturnStatus.getStatus().getStatusCode().equals(TStatusCode.SRM_SUCCESS))) {
                        PtPAbortExecutor.log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+numOfSurl+" of "+listOfChunk.size()+" [SURL:"+auxChunkData.toSURL()+"] successfully done  with [status: "+surlReturnStatus.getStatus()+"]");
                    } else {
                        PtPAbortExecutor.log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+numOfSurl+" of "+listOfChunk.size()+" [SURL:"+auxChunkData.toSURL()+"] failed with [status: "+surlReturnStatus.getStatus()+"]");
                        errorCount++;
                    }
                    //Add returnStatus
                    arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);

                    //Increment number of chunk aborted
                    chunkAborted++;

                }

            } //while chunk Collection

            if (chunkAborted<totalSize) {
                PtPAbortExecutor.log.debug("PtPAbortExecutor: Wait for some time...");
                //Be patient until some chunk finish its work...
                //  try {
                //Sleep 1 Second
                PtPAbortExecutor.log.debug("Abort: I'm waiting...");
                //this.wait(1000);
                //Wait for 1 second
                //long start =  System.currentTimeMillis();
                //long index = 0;
                //while(index<start+1000)
                //    index = System.currentTimeMillis();
                // } catch (InterruptedException e) {
                //     // TODO Auto-generated catch block
                //     e.printStackTrace();
                // }


                try {
                    long numMillisecondsToSleep = 100; // 1 seconds
                    Thread.sleep(numMillisecondsToSleep);
                } catch (InterruptedException e) {

                }


                PtPAbortExecutor.log.debug("PtPAbortExecutor: Wake up! It's time to work...");
            }

        }

        // LoopTimes Exceeded?
        if(counter == PtPAbortExecutor.maxLoopTimes) {
            //The ABORT execution is interrupted to prevent a deadlock situation
            PtPAbortExecutor.log.debug("Abort: Timeout exceeded.");
            globalStatus = manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"TimeOut for abort execution exceeded.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
            PtPAbortExecutor.log.error("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+surlArray+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }

        if(errorCount == totalSize) {
            globalStatus = manageStatus(TStatusCode.SRM_FAILURE,"Abort failed.");
        } else if(errorCount > 0) {
            if(inputData.getType() == AbortGeneralInputData.ABORT_REQUEST) {
                globalStatus = manageStatus(TStatusCode.SRM_FAILURE, "Some chunks failed.");
            } else {
                globalStatus = manageStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Some chunks failed.");
            }
        } else {
            globalStatus = manageStatus(TStatusCode.SRM_SUCCESS,"Abort request completed.");
            if((inputData.getType() == AbortGeneralInputData.ABORT_REQUEST)) {
                RequestSummaryCatalog cat = RequestSummaryCatalog.getInstance();
                TReturnStatus requestStatus = null;
                try {
                    requestStatus = new TReturnStatus(TStatusCode.SRM_ABORTED, "Request Aborted.");
                } catch (InvalidTReturnStatusAttributeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                cat.updateGlobalStatus(inputData.getRequestToken(), requestStatus);
            }
        }
        //Set output data
        outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
        outputData.setReturnStatus(globalStatus);
        PtPAbortExecutor.log.error("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+surlArray+"] failed with [status: "+globalStatus+"]");
        return outputData;

    }


    /**
     *
     * @param statusCode statusCode
     * @param explanation explanation string
     * @return returnStatus returnStatus
     */

    private TReturnStatus manageStatus(TStatusCode statusCode,String explanation) {
        TReturnStatus returnStatus = null;
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            PtPAbortExecutor.log.debug("AbortExecutor : Error creating returnStatus " + ex1);
        }
        return returnStatus;
    }


    /**
     *
     * Manage the roll back needed to execute an abort request.
     * @param chunkData PtGChunkData
     * @return returnStatus TSURLReturnStatus
     */

    private TSURLReturnStatus manageAuthorizedAbort(PtPChunkData chunkData, GridUserInterface guser) {
        boolean failure = false;
        //Used for ReserveSpace Operation
        namespace = NamespaceDirector.getNamespace();



        /* Query the DB update the desired Chunk */
        PtPChunkCatalog putCatalog = PtPChunkCatalog.getInstance();

        /******************************* Phase (3) ************************************/

        PtPAbortExecutor.log.debug("PtPAbortExecutor: Phase(3) RollBack start.");

        TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
        TSURL surl  =  chunkData.toSURL();
        StoRI stori = null;
        boolean res = false;

        //Check also for the SRM_QUEUED status, in case Phase 1 or Phase2

        if((chunkData.status().getStatusCode().equals(TStatusCode.SRM_SPACE_AVAILABLE))||
                (chunkData.status().getStatusCode().equals(TStatusCode.SRM_REQUEST_QUEUED)) ) { //some other status || ???  )
            // Valid Status
            PtPAbortExecutor.log.debug("PtPAbortExecutor: The Chunk is in a VALID state for the RollBack action!");

            //Manage Rollback
            if(!(chunkData.spaceToken().isEmpty())) {

                TSpaceToken sToken = chunkData.spaceToken();
                TReturnStatus status;
                ReserveSpaceCommand rexec = new ReserveSpaceCommand();

                //Space Token specified in the request
                PtPAbortExecutor.log.debug("PtPAbortExecutor: Space token found.");
                /*
                 * We have now two possible cases:
                 * 1) With size specified.
                 * 2) Without size specified.
                 */
                if(chunkData.expectedFileSize().isEmpty()) {


                    /*
                     * Without fileSize specified.
                     * In this case the whole space_file has been associated to
                     * the requested file.
                     * So the roll back must recreate the Space Catalog entry and
                     * update the information on used space.
                     * 
                     */

                    status = rexec.resetReservation(sToken);

                    if(status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                        //Everithing works well
                    } else{
                        //Something goes wrong
                        failure = true;
                    }

                } else {
                    /*
                     * With fileSize specified.
                     * In this case the spaceFile have to be increased
                     * from the free block of the removed put placeholder.
                     */

                    status = rexec.updateReservation(chunkData.spaceToken(), chunkData.expectedFileSize(), chunkData.toSURL());

                    if(status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                        //Everithing works well
                    } else{
                        //Something goes wrong
                        failure = true;
                    }

                }

            } else {

            }
            // The File Must be removed in any case!

            PtPAbortExecutor.log.debug("PtPAbortExecutor: No space token found.");
            /*
             * We have now two possible cases:
             * 1) With size specified.
             * 2) Without size specified.
             * But in both case the thing to do is the same! Remove
             * the file (the placeholder or the pre-allocated).
             */

            //Remove the file from the filesystem
            PtPAbortExecutor.log.debug("PtPAbortExecutor: Start removing file from the file system...");
            //Create Stori
            LocalFile fileToRemove  = null;
            if (!surl.isEmpty()) {
                try {
                    stori = namespace.resolveStoRIbySURL(surl, guser);
                } catch (NamespaceException ex) {
                    PtPAbortExecutor.log.error("Unable to build StoRI by SURL", ex);
                }
            }

            //Get LocalFile and Remove
            fileToRemove = stori.getLocalFile();
            res = fileToRemove.delete();
            res = true;


            PtPAbortExecutor.log.debug("PtPAbortExecutor: File removed.");


            //This method is used to mark as expired the specified entry
            //in the JitCatalog. In this way the next time the GarbageCollector
            //will wake up, it will remove the entry as expired.
            //The file is already removed, but the garbage collection is
            //smart enought to manage the case.

            VolatileAndJiTCatalog jitCat = VolatileAndJiTCatalog.getInstance();

            //jitCat.expireNowGet(PFN, LocalUser, ACL )
            //}


            //Remove the File from the JIT Catalog
            //JitCatalog.removeSomething

            //Change status to aborted
            if(failure) {
                //Something goes wrong during the update catalog operation
                chunkData.changeStatusSRM_ABORTED("Request aborted.");
                putCatalog.update(chunkData);
                surlReturnStatus.setSurl(chunkData.toSURL());
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"Database Not Updated."));
                return surlReturnStatus;
            }

            if(res) {
                chunkData.changeStatusSRM_ABORTED("Request Aborted");
                putCatalog.update(chunkData);
                surlReturnStatus.setSurl(chunkData.toSURL());
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request successfully aborted."));
                return surlReturnStatus;
            } else {
                chunkData.changeStatusSRM_ABORTED("Request aborted.");
                putCatalog.update(chunkData);
                surlReturnStatus.setSurl(chunkData.toSURL());
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"File not removed."));
                return surlReturnStatus;
            }

        } else {
            //The Chunk is in a SRM_Status from wich an Abort operation is not possible! (SRM_FAILURE or SRM_SUCCESS)
            surlReturnStatus.setSurl(chunkData.toSURL());

            //Create different case that depends on the CHUNK status!

            if((chunkData.status().getStatusCode().equals(TStatusCode.SRM_SUCCESS))) {
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Request is in a final status. Abort not allowed."));
            } else {
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Abort request not executed."));
            }

            return surlReturnStatus;

        }

    }




}