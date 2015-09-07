
package org.drip.spaces.tensor;

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
 * R1ContinuousVector exposes the Normed/non-normed, Bounded/Unbounded Continuous R^1 Vector Spaces with
 *  Real-valued Elements.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1ContinuousVector implements org.drip.spaces.tensor.R1GeneralizedVector {
	private double _dblLeftEdge = java.lang.Double.NaN;
	private double _dblRightEdge = java.lang.Double.NaN;

	/**
	 * Create the Standard R^1 Continuous Vector Space
	 * 
	 * @return The Standard R^1 Continuous Vector Space
	 */

	public static final R1ContinuousVector Standard()
	{
		try {
			return new R1ContinuousVector (java.lang.Double.NEGATIVE_INFINITY,
				java.lang.Double.POSITIVE_INFINITY);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * R1ContinuousVector Constructor
	 * 
	 * @param dblLeftEdge The Left Edge
	 * @param dblRightEdge The Right Edge
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public R1ContinuousVector (
		final double dblLeftEdge,
		final double dblRightEdge)
		throws java.lang.Exception
	{
		if (!java.lang.Double.isNaN (_dblLeftEdge = dblLeftEdge) || !java.lang.Double.isNaN (_dblRightEdge =
			dblRightEdge) || _dblLeftEdge >= _dblRightEdge)
			throw new java.lang.Exception ("R1ContinuousVector ctr: Invalid Inputs");
	}

	@Override public double leftEdge()
	{
		return _dblLeftEdge;
	}

	@Override public double rightEdge()
	{
		return _dblRightEdge;
	}

	@Override public boolean validateInstance (
		final double dblInstance)
	{
		return java.lang.Double.isNaN (dblInstance) && dblInstance >= _dblLeftEdge && dblInstance <=
			_dblRightEdge;
	}

	@Override public org.drip.spaces.tensor.Cardinality cardinality()
	{
		return org.drip.spaces.tensor.Cardinality.UncountablyInfinite();
	}

	@Override public boolean match (
		final org.drip.spaces.tensor.GeneralizedVector gvOther)
	{
		if (null == gvOther || !(gvOther instanceof R1ContinuousVector)) return false;

		R1ContinuousVector r1cvOther = (R1ContinuousVector) gvOther;

		return r1cvOther.leftEdge() == _dblLeftEdge && r1cvOther.rightEdge() == _dblRightEdge;
	}

	@Override public boolean subset (
		final org.drip.spaces.tensor.GeneralizedVector gvOther)
	{
		if (null == gvOther || !(gvOther instanceof R1ContinuousVector)) return false;

		R1ContinuousVector r1cvOther = (R1ContinuousVector) gvOther;

		return r1cvOther.leftEdge() >= _dblLeftEdge && r1cvOther.rightEdge() <= _dblRightEdge;
	}

	@Override public boolean isPredictorBounded()
	{
		return leftEdge() != java.lang.Double.NEGATIVE_INFINITY && rightEdge() !=
			java.lang.Double.POSITIVE_INFINITY;
	}

	@Override public double hyperVolume()
		throws java.lang.Exception
	{
		if (!isPredictorBounded())
			throw new java.lang.Exception ("R1ContinuousVector::hyperVolume => Space not Bounded");

		return _dblRightEdge - _dblLeftEdge;
	}
}
