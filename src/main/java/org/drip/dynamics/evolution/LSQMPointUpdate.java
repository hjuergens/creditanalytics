
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
 * LSQMPointUpdate contains the Snapshot and the Increment of the Evolving Point Latent State Quantification
 *  Metrics.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LSQMPointUpdate {
	private double _dblViewDate = java.lang.Double.NaN;
	private double _dblEvolutionStartDate = java.lang.Double.NaN;
	private double _dblEvolutionFinishDate = java.lang.Double.NaN;
	private org.drip.dynamics.evolution.LSQMPointRecord _lprSnapshot = null;
	private org.drip.dynamics.evolution.LSQMPointRecord _lprIncrement = null;

	/**
	 * LSQMPointUpdate Constructor
	 * 
	 * @param dblEvolutionStartDate The Evolution Start Date
	 * @param dblEvolutionFinishDate The Evolution Finish Date
	 * @param dblViewDate The View Date
	 * @param lprSnapshot The LSQM Point Record Snapshot
	 * @param lprIncrement The LSQM Point Record Update
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public LSQMPointUpdate (
		final double dblEvolutionStartDate,
		final double dblEvolutionFinishDate,
		final double dblViewDate,
		final org.drip.dynamics.evolution.LSQMPointRecord lprSnapshot,
		final org.drip.dynamics.evolution.LSQMPointRecord lprIncrement)
		throws java.lang.Exception
	{
		if (null == (_lprSnapshot = lprSnapshot) || !org.drip.quant.common.NumberUtil.IsValid
			(_dblEvolutionStartDate = dblEvolutionStartDate) || !org.drip.quant.common.NumberUtil.IsValid
				(_dblEvolutionFinishDate = dblEvolutionFinishDate) || _dblEvolutionFinishDate <
					_dblEvolutionStartDate || !org.drip.quant.common.NumberUtil.IsValid (_dblViewDate =
						dblViewDate) || _dblViewDate < _dblEvolutionStartDate)
			throw new java.lang.Exception ("LSQMPointUpdate ctr: Invalid Inputs");

		_lprIncrement = lprIncrement;
	}

	/**
	 * Retrieve the Evolution Start Date
	 * 
	 * @return The Evolution Start Date
	 */

	public double evolutionStartDate()
	{
		return _dblEvolutionStartDate;
	}

	/**
	 * Retrieve the Evolution Finish Date
	 * 
	 * @return The Evolution Finish Date
	 */

	public double evolutionFinishDate()
	{
		return _dblEvolutionFinishDate;
	}

	/**
	 * Retrieve the View Date
	 * 
	 * @return The View Date
	 */

	public double viewDate()
	{
		return _dblViewDate;
	}

	/**
	 * Retrieve the LSQM Point Snapshot
	 * 
	 * @return The LSQM Point Snapshot
	 */

	public org.drip.dynamics.evolution.LSQMPointRecord snapshot()
	{
		return _lprSnapshot;
	}

	/**
	 * Retrieve the LSQM Point Increment
	 * 
	 * @return The LSQM Point Increment
	 */

	public org.drip.dynamics.evolution.LSQMPointRecord increment()
	{
		return _lprIncrement;
	}
}
