package it.grid.storm.synchcall.command.space.quota;

import java.util.List;

public interface QuotaParametersInterface {


   /**
    *
    * @return List
    */
   public List<String> getParameters();

   /**
    *
    * @return String
    */
   public String getCommand();

}