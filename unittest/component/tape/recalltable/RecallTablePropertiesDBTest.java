/**
 * 
 */
package component.tape.recalltable;


import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;
import it.grid.storm.tape.recalltable.persistence.PropertiesDB;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 */
public class RecallTablePropertiesDBTest {


    private static final Logger log = LoggerFactory.getLogger(RecallTablePropertiesDBTest.class);


//    public static void main(String[] args) {
//        RecallTablePropertiesDBTest testDB = new RecallTablePropertiesDBTest();
//        testDB.createTasks(3);
//        // testDB.showDB();
//        try {
//            testDB.numberOfTasks();
//        }
//        catch (DataAccessException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        try {
//            Thread.sleep(1000);
//        }
//        catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        try {
//            testDB.takeoverNTasks(2);
//            // testDB.showDB();
//            testDB.numberOfTasks();
//            testDB.showDB();
//            testDB.printTaskList(testDB.getInProgressTask());
//            testDB.completedTasks(1);
//            // testDB.showDB();
//            testDB.numberOfTasks();
//            testDB.purgeCatalog(2);
//            testDB.numberOfTasks();
//        }
//        catch (DataAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }


    /**
     * 
     */
//    private void showDB() {
//        PropertiesDB tasksDB = new PropertiesDB(true);
//        try {
//            ArrayList<TapeRecallTO> allTasks = new ArrayList<TapeRecallTO>(tasksDB.getAll().values());
//            for (TapeRecallTO TapeRecallTO : allTasks) {
//                log.debug(TapeRecallTO.toString());
//            }
//        }
//        catch (FileNotFoundException e) {
//            log.error("RecallTask DB does not exists!");
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            log.error("IO Error while reading RecallTaskDB.");
//            e.printStackTrace();
//        }
//        catch (DataAccessException de) {
//            log.error("Data Access Error");
//            de.printStackTrace();
//        }
//    }


    private void numberOfTasks() throws DataAccessException {
//        TapeRecallCatalog recallTableCatalog = new TapeRecallCatalog(true);
//        log.debug("#Tasks Queued = " + recallTableCatalog.getNumberTaskQueued());
//        log.debug("#Tasks In progress = " + recallTableCatalog.getNumberTaskInProgress());
//        log.debug("RecallTable size = " + recallTableCatalog.getRecallTableSize());
    }


    private void takeoverATask() throws DataAccessException {
//        TapeRecallCatalog recallTableCatalog = new TapeRecallCatalog(true);
//        TapeRecallTO task = recallTableCatalog.takeoverTask();
//        log.debug("Taken Task : " + task);
    }


    private void takeoverNTasks(int n) throws DataAccessException {
//        TapeRecallCatalog recallTableCatalog = new TapeRecallCatalog(true);
//        ArrayList<TapeRecallTO> tasksList = recallTableCatalog.takeoverNTasks(n);
//        printTaskList(tasksList);
    }


    private List<TapeRecallTO> getInProgressTask() throws DataAccessException {
//        TapeRecallCatalog recallTableCatalog = new TapeRecallCatalog(true);
//        ArrayList<TapeRecallTO> taskList = new ArrayList<TapeRecallTO>(recallTableCatalog.getInProgressTasks());
//        return taskList;
        return null;
    }


    private void printTaskList(List<TapeRecallTO> taskList) {
        for (Object element : taskList) {
            TapeRecallTO TapeRecallTO = (TapeRecallTO) element;
            log.debug("Task : " + TapeRecallTO);
        }
    }


    private void changeStatusInSuccess(TapeRecallTO task) throws DataAccessException {
//        TapeRecallCatalog recallTableCatalog = new TapeRecallCatalog(true);
// recallTableCatalog.changeStatus(task.getTaskId(), task.getRequestTokenStr(), RecallTaskStatus.SUCCESS);
    }


    private void completedTasks(int number) throws DataAccessException {
        ArrayList<TapeRecallTO> taskList = new ArrayList<TapeRecallTO>(getInProgressTask());
        log.debug("TaskList size = " + taskList.size() + " and number = " + number);
        if (taskList.size() > number) {
            taskList = new ArrayList<TapeRecallTO>(taskList.subList(0, number));
        }
        for (Object element : taskList) {
            TapeRecallTO TapeRecallTO = (TapeRecallTO) element;
            changeStatusInSuccess(TapeRecallTO);
        }
    }


    private void purgeCatalog(int n) throws DataAccessException {
//        TapeRecallCatalog recallTableCatalog = new TapeRecallCatalog(true);
//        recallTableCatalog.purgeCatalog(n);
    }


    /**
     * @param i
     */
    private void createTasks(int numTasks) {
        TapeRecallTO task;
        int year = 2009;
        int month = 8;
        int day = 18;
        int hour = 0;
        int min = 0;
        int sec = 0;
        int secRnd;
        int minRnd;
        int hourRnd;
        for (int j = 0; j < numTasks; j++) {
            hourRnd = (int) Math.round(Math.random() * 23);
            minRnd = (int) Math.round(Math.random() * 59);
            secRnd = (int) Math.round(Math.random() * 59);
            // log.debug("date-" + j + " = " + hourRnd + ":" + minRnd + ":" +
            // secRnd);
            GregorianCalendar gc = new GregorianCalendar(year, month, day, hour + hourRnd, min + minRnd, sec + secRnd);
            Date date = gc.getTime();
            Format formatter = new SimpleDateFormat(TapeRecallTO.dateFormat);
            log.debug("Date = " + date + "formatted = " + formatter.format(date));
            task = TapeRecallTO.createRandom(date, "test");
            log.debug("TASK: " + task);
            PropertiesDB tasksDB = new PropertiesDB(true);
            try {
                tasksDB.addRecallTask(task);
            }
            catch (FileNotFoundException e) {
                log.error("Properties DB does not exists!");
                e.printStackTrace();
            }
            catch (IOException e) {
                log.error("I/O Error");
                e.printStackTrace();
            }
            catch (DataAccessException de) {
                log.error("Data Access Error");
                de.printStackTrace();
            }
        }
    }
}
