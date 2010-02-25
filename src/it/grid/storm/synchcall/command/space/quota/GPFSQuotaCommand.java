package it.grid.storm.synchcall.command.space.quota;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPFSQuotaCommand implements QuotaCommandInterface{

    private static final Logger log = LoggerFactory.getLogger(GPFSQuotaCommand.class);

    public GPFSQuotaCommand() {
        super();
    }

    public QuotaInfoInterface executeGetQuotaInfo(QuotaParametersInterface parameters) throws QuotaException {
        GPFSQuotaInfo gpfsQuota = new GPFSQuotaInfo();
        if (!(parameters instanceof GPFSQuotaParameters)) {
            throw new QuotaException("Not conform parameters (parameters:'" + parameters
                    + "')while trying to execute GPFS Quota Command.");
        } else {
            GPFSQuotaParameters gpfsParameters = (GPFSQuotaParameters) parameters;
            String[] command = buildCommandString(gpfsParameters);

            StringBuffer commandOutput = new StringBuffer();
            for (String element : command) {
                commandOutput.append(element).append(" ");
            }
            log.debug("GPFSQuota Command INPUT String : " + commandOutput.toString());

            String output = getOutput(command);
            log.debug("GPFSQuota Command OUTPUT String : " + output);
            gpfsQuota.build(output);
        }
        return gpfsQuota;
    }

    /**
     *
     * @param gpfsParameters GPFSQuotaParameters
     * @return String[]
     */
    private String[] buildCommandString(GPFSQuotaParameters gpfsParameters) {
        String[] command = null;
        List<String> param = gpfsParameters.getParameters();
        if (param!=null) {
            command = new String[1+param.size()];
            command[0] = gpfsParameters.getCommand() ;
            int cont = 0;
            // Adding parameters to the command
            for (Object element : param) {
                cont++;
                String p = (String)element;
                command[cont]=p;
            }
        } else {
            command = new String[]{gpfsParameters.getCommand()};
        }
        return command;
    }

    /**
     * @param command String[]
     * @return String
     */
    private String getOutput(String[] command) throws QuotaException {
        String result = null;
        Process child;

        try {
            child = Runtime.getRuntime().exec(command);
            try {
                child.waitFor();
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
            log.error("getQuota I/O Exception: " + e);
            throw new QuotaException(e);
        }

        // Get the input stream and read from it
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));

        try {

            // process the Command Output (Input for StoRM ;) )
            String line;
            int row = 0;
            log.debug("Quota Command Output :");
            while ((line = stdInput.readLine()) != null) {
                log.debug(row + ": " + line);
                boolean lineOk = processOutput(row, line);
                if (lineOk) {
                    result = line;
                    break;
                }
                row++;
            }

            // process the Errors
            String errLine;
            while ((errLine = stdError.readLine()) != null) {
                log.warn("Quota Command Output contains an ERROR message " + errLine);
                throw new QuotaException(errLine);
            }

        } catch (IOException ex) {
            
            log.error("getQuota I/O Exception: " + ex);
            throw new QuotaException(ex);
            
        } finally {
            
            try {
                stdInput.close();
                stdError.close();
            } catch (IOException e) {
                log.warn("getQuota. Error occurred closing the Std-I/O " + e );
            }
            
            child.destroy();
            child = null;
        }
        
        return result;
    }


    private boolean processOutput(int row, String line) {
        boolean result = false;
        if (row>1) {
            /**
             * @todo : Implement a more smart check to verify the right line
             */
            result = true;
        }
        return result;
    }

}