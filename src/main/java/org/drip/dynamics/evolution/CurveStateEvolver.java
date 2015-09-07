
package org.drip.dynamics.evolution;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * CurveStateEvolver is the Interface on top of which the Curve State Evolution Dynamics is constructed.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface CurveStateEvolver {

	/**
	 * Evolve the Latent State and return the LSQM Curve Update
	 * 
	 * @param dblSpotDate The Spot Date
	 * @param dblViewDate The View Date
	 * @param dblSpotTimeIncrement The Spot Evolution Increment
	 * @param lsqmPrev The Previous LSQM Curve Update
	 * 
	 * @return The LSQM Curve Update
	 */

	public abstract org.drip.dynamics.evolution.LSQMCurveUpdate evolve (
		final double dblSpotDate,
		final double dblViewDate,
		final double dblSpotTimeIncrement,
		final org.drip.dynamics.evolution.LSQMCurveUpdate lsqmPrev
	);

	/**
	 * Simulate the Principal Metric from the Start to the End Date
	 * 
	 * @param dblEvolutionStartDate The Evolution Start Date
	 * @param dblEvolutionFinishDate The Evolution Finish Date
	 * @param dblEvolutionIncrement The Evolution Increment
	 * @param dblViewDate The View Date
	 * @param lsqmStart The Starting State Metrics
	 * @param iNumSimulation Number of Simulations
	 * 
	 * @return The Array of the Evolved Tenor LIBOR's
	 */

	public abstract double[][] simulatePrincipalMetric (
		final double dblEvolutionStartDate,
		final double dblEvolutionFinishDate,
		final double dblEvolutionIncrement,
		final double dblViewDate,
		final org.drip.dynamics.evolution.LSQMCurveUpdate lsqmStart,
		final int iNumSimulation
	);
}
