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

package it.grid.storm.space.quota;

import java.util.*;

public class GPFSQuotaParameters implements QuotaParametersInterface {

  private static final String COMMAND = "mmrepquota";
  private List<String> parameters = null;

  public GPFSQuotaParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  /**
   *
   * @return String
   * @todo Implement this
   *   it.grid.storm.synchcall.space.quota.QuotaParametersInterface method
   */
  public String getCommand() {
    return COMMAND;
  }

  /**
   *
   * @return List
   * @todo Implement this
   *   it.grid.storm.synchcall.space.quota.QuotaParametersInterface method
   */
  public List<String> getParameters() {
    return this.parameters;
  }
}