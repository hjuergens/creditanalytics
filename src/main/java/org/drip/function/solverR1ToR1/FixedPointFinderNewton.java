
package org.drip.function.solverR1ToR1;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * FixedPointFinderNewton customizes the FixedPointFinder for Open (Newton's) fixed point finder
 * 	functionality.
 * 
 * FixedPointFinderNewton applies the following customization:
 * 	- Initializes the fixed point finder by computing a starting variate in the convergence zone
 * 	- Iterating the next search variate using the Newton's method.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FixedPointFinderNewton extends org.drip.function.solverR1ToR1.FixedPointFinder {
	private org.drip.function.solverR1ToR1.ExecutionInitializer _ei = null;

	private double calcVariateOFSlope (
		final double dblVariate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblVariate))
			throw new java.lang.Exception ("FixedPointFinderNewton::calcVariateOFSlope => Invalid input!");

		org.drip.quant.calculus.Differential diff = _of.differential (dblVariate, 1);

		if (null == diff)
			throw new java.lang.Exception
				("FixedPointFinderNewton::calcVariateTargetSlope => Cannot evaluate Derivative for variate "
					+ dblVariate);

		return diff.calcSlope (false);
	}

	@Override protected boolean iterateVariate (
		final org.drip.function.solverR1ToR1.IteratedVariate vi,
		final org.drip.function.solverR1ToR1.FixedPointFinderOutput rfop)
	{
		if (null == vi || null == rfop) return false;

		double dblVariate = vi.getVariate();

		try {
			double dblVariateNext = dblVariate - calcVariateOFSlope (dblVariate) * vi.getOF();

			return vi.setVariate (dblVariateNext) && vi.setOF (_of.evaluate (dblVariateNext)) &&
				rfop.incrOFDerivCalcs() && rfop.incrOFCalcs();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override protected org.drip.function.solverR1ToR1.ExecutionInitializationOutput initializeVariateZone (
		final org.drip.function.solverR1ToR1.InitializationHeuristics ih)
	{
		return _ei.initializeBracket (ih, _dblOFGoal);
	}

	/**
	 * FixedPointFinderNewton constructor
	 * 
	 * @param dblOFGoal OF Goal
	 * @param of Objective Function
	 * @param bWhine TRUE => Balk on Encountering Exception
	 * 
	 * @throws java.lang.Exception Propogated from underneath
	 */

	public FixedPointFinderNewton (
		final double dblOFGoal,
		final org.drip.function.definition.R1ToR1 of,
		final boolean bWhine)
		throws java.lang.Exception
	{
		super (dblOFGoal, of, null, bWhine);

		_ei = new org.drip.function.solverR1ToR1.ExecutionInitializer (_of, null, true);
	}
}
