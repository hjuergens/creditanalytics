
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
 * FixedPointFinderBracketing customizes the FixedPointFinder for bracketing based fixed point finder
 * 	functionality.
 * 
 * FixedPointFinderBracketing applies the following customization:
 * 	- Initializes the fixed point finder by computing the starting brackets
 * 	- Iterating the next search variate using one of the specified variate iterator primitives.
 * 
 * By default, FixedPointFinderBracketing does not do compound iterations of the variate using any schemes -
 * 	that is done by classes that extend it.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FixedPointFinderBracketing extends org.drip.function.solverR1ToR1.FixedPointFinder {
	protected int _iIteratorPrimitive = -1;
	protected org.drip.function.solverR1ToR1.IteratedBracket _ib = null;

	private org.drip.function.solverR1ToR1.ExecutionInitializer _ei = null;

	protected final double calcNextVariate (
		final double dblCurrentVariate,
		final double dblContraVariate,
		final double dblCurrentOF,
		final double dblContraPointOF,
		final int iIteratorPrimitive,
		final org.drip.function.solverR1ToR1.FixedPointFinderOutput rfop)
		throws java.lang.Exception
	{
		if (org.drip.function.solverR1ToR1.VariateIteratorPrimitive.BISECTION == iIteratorPrimitive)
			return org.drip.function.solverR1ToR1.VariateIteratorPrimitive.Bisection (dblCurrentVariate,
				dblContraVariate);

		if (org.drip.function.solverR1ToR1.VariateIteratorPrimitive.FALSE_POSITION == iIteratorPrimitive)
			return org.drip.function.solverR1ToR1.VariateIteratorPrimitive.FalsePosition (dblCurrentVariate,
				dblContraVariate, dblCurrentOF, dblContraPointOF);

		double dblIntermediateVariate = org.drip.function.solverR1ToR1.VariateIteratorPrimitive.Bisection
			(dblCurrentVariate, dblContraVariate);

		if (!rfop.incrOFCalcs())
			throw new java.lang.Exception
				("FixedPointFinderBracketing::calcNextVariate => Cannot increment rfop!");

		if (org.drip.function.solverR1ToR1.VariateIteratorPrimitive.QUADRATIC_INTERPOLATION == iIteratorPrimitive)
			return org.drip.function.solverR1ToR1.VariateIteratorPrimitive.QuadraticInterpolation (dblCurrentVariate,
				dblIntermediateVariate, dblContraVariate, dblCurrentOF, _of.evaluate
					(dblIntermediateVariate), dblContraPointOF);

		if (org.drip.function.solverR1ToR1.VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION ==
			iIteratorPrimitive)
			return org.drip.function.solverR1ToR1.VariateIteratorPrimitive.InverseQuadraticInterpolation
				(dblCurrentVariate, dblIntermediateVariate, dblContraVariate, dblCurrentOF, _of.evaluate
					(dblIntermediateVariate), dblContraPointOF);

		if (org.drip.function.solverR1ToR1.VariateIteratorPrimitive.RIDDER == iIteratorPrimitive)
			return org.drip.function.solverR1ToR1.VariateIteratorPrimitive.Ridder (dblCurrentVariate,
				dblIntermediateVariate, dblContraVariate, dblCurrentOF, _of.evaluate
					(dblIntermediateVariate), dblContraPointOF);

		throw new java.lang.Exception
			("FixedPointFinderBracketing.calcNextVariate => Unknown Iterator Primitive");
	}

	protected double iterateCompoundVariate (
		final double dblCurrentVariate,
		final double dblContraVariate,
		final double dblCurrentOF,
		final double dblContraPointOF,
		final org.drip.function.solverR1ToR1.FixedPointFinderOutput rfop)
		throws java.lang.Exception
	{
		return calcNextVariate (dblCurrentVariate, dblContraVariate, dblCurrentOF, dblContraPointOF,
			_iIteratorPrimitive, rfop);
	}

	@Override protected boolean iterateVariate (
		final org.drip.function.solverR1ToR1.IteratedVariate iv,
		final org.drip.function.solverR1ToR1.FixedPointFinderOutput rfop)
	{
		if (null == iv || null == rfop) return false;

		double dblContraRoot = java.lang.Double.NaN;
		double dblContraRootOF = java.lang.Double.NaN;

		double dblOF = iv.getOF();

		double dblOFLeft = _ib.getOFLeft();

		double dblOFRight = _ib.getOFRight();

		double dblVariate = iv.getVariate();

		double dblVariateLeft = _ib.getVariateLeft();

		double dblVariateRight = _ib.getVariateRight();

		if (((dblOFLeft - _dblOFGoal) * (dblOF - _dblOFGoal)) > 0.) {
			if (!_ib.setOFLeft (dblOF) || !_ib.setVariateLeft (dblVariate)) return false;

			dblContraRootOF = dblOFRight;
			dblContraRoot = dblVariateRight;
		} else if (((dblOFRight - _dblOFGoal) * (dblOF - _dblOFGoal)) > 0.) {
			if (!_ib.setOFRight (dblOF) || !_ib.setVariateRight (dblVariate)) return false;

			dblContraRootOF = dblOFLeft;
			dblContraRoot = dblVariateLeft;
		}

		try {
			dblVariate = iterateCompoundVariate (dblVariate, dblContraRoot, dblOF, dblContraRootOF, rfop);

			return iv.setVariate (dblVariate) && iv.setOF (_of.evaluate (dblVariate)) && rfop.incrOFCalcs();
		} catch (java.lang.Exception e) {
			if (_bWhine) e.printStackTrace();
		}

		return false;
	}

	@Override protected org.drip.function.solverR1ToR1.ExecutionInitializationOutput initializeVariateZone (
		final org.drip.function.solverR1ToR1.InitializationHeuristics ih)
	{
		org.drip.function.solverR1ToR1.BracketingOutput bop = null;

		if (null != ih && org.drip.function.solverR1ToR1.InitializationHeuristics.SEARCH_HARD_BRACKETS ==
			ih.getDeterminant())
			bop = _ei.verifyHardSearchEdges (ih, _dblOFGoal);
		else
			bop = _ei.initializeBracket (ih, _dblOFGoal);

		if (null == bop || !bop.isDone()) return null;

		try {
			_ib = new org.drip.function.solverR1ToR1.IteratedBracket (bop);

			return bop;
		} catch (java.lang.Exception e) {
			if (_bWhine) e.printStackTrace();
		}

		return null;
	}

	/**
	 * FixedPointFinderBracketing constructor
	 * 
	 * @param dblOFGoal OF Goal
	 * @param of Objective Function
	 * @param ec Execution Control
	 * @param iIteratorPrimitive Iterator Primitive
	 * @param bWhine TRUE => Balk on Encountering Exception
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public FixedPointFinderBracketing (
		final double dblOFGoal,
		final org.drip.function.definition.R1ToR1 of,
		final org.drip.function.solverR1ToR1.ExecutionControl ec,
		final int iIteratorPrimitive,
		final boolean bWhine)
		throws java.lang.Exception
	{
		super (dblOFGoal, of, ec, bWhine);

		if (org.drip.function.solverR1ToR1.VariateIteratorPrimitive.BISECTION != (_iIteratorPrimitive =
			iIteratorPrimitive) && org.drip.function.solverR1ToR1.VariateIteratorPrimitive.FALSE_POSITION !=
				_iIteratorPrimitive &&
					org.drip.function.solverR1ToR1.VariateIteratorPrimitive.QUADRATIC_INTERPOLATION !=
						_iIteratorPrimitive &&
							org.drip.function.solverR1ToR1.VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION
								!= _iIteratorPrimitive &&
									org.drip.function.solverR1ToR1.VariateIteratorPrimitive.RIDDER !=
										_iIteratorPrimitive)
			throw new java.lang.Exception ("FixedPointFinderBracketing constructor: Invalid inputs!");

		_ei = new org.drip.function.solverR1ToR1.ExecutionInitializer (_of, null, true);
	}
}
