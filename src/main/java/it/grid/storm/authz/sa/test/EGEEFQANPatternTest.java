/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * 
 */
package it.grid.storm.authz.sa.test;

import it.grid.storm.authz.sa.model.FQANPattern;
import it.grid.storm.griduser.FQAN;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zappi
 * 
 */
public class EGEEFQANPatternTest {

	private final List<FQANPattern> testListFQANPattern = new ArrayList<FQANPattern>();
	private final List<FQAN> testListFQAN = new ArrayList<FQAN>();

	public void invalidFQANPattern() {

		String invalidFQANPattern = "/atlas*";
		// FQANPattern fqanPattern = new EGEEFQANMatchingRule
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub

	}

}
