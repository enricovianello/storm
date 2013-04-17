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

package it.grid.storm.asynch;

import it.grid.storm.scheduler.Scheduler;

/**
 * This is a Facade to the Schedulers.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date April 25th, 2005
 * @version 1.0
 */
public class SchedulerFacade {

	private static SchedulerFacade sf = new SchedulerFacade();
	private Scheduler crusherSched = SchedulerFactory.crusherSched(); // Scheduler
																																		// that
																																		// manages
																																		// Feeder
																																		// tasks
	private Scheduler chunkSched = SchedulerFactory.chunkSched(); // Scheduler
																																// that manages
																																// Chunk tasks

	private SchedulerFacade() {

	}

	/**
	 * Method that returns the only instance of SchedulerFacade.
	 */
	public static SchedulerFacade getInstance() {

		return sf;
	}

	/**
	 * Method that returns the Scheduler in charge of handling Chunk.
	 */
	public Scheduler chunkScheduler() {

		return chunkSched;
	}

	/**
	 * Method that returns the Scheduler in charge of handling Feeder
	 */
	public Scheduler crusherScheduler() {

		return crusherSched;
	}
}
