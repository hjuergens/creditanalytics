
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
 * RdContinuousVector implements the Normed/non-normed, Bounded/Unbounded Continuous R^d Vector Spaces.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RdContinuousVector extends org.drip.spaces.tensor.RdAggregate {

	/**
	 * Construct the RdContinuousVector Instance
	 * 
	 * @param iDimension The Space Dimension
	 * 
	 * @return The RdContinuousVector Instance
	 */

	public static final RdContinuousVector Standard (
		final int iDimension)
	{
		try {
			return 0 >= iDimension ? null : new RdContinuousVector (new
				org.drip.spaces.tensor.R1ContinuousVector[iDimension]);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * RdContinuousVector Constructor
	 * 
	 * @param aR1CV Array of the Continuous R^1 Vector Spaces
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public RdContinuousVector (
		final org.drip.spaces.tensor.R1ContinuousVector[] aR1CV)
		throws java.lang.Exception
	{
		super (aR1CV);
	}

	@Override public double[] leftDimensionEdge()
	{
		org.drip.spaces.tensor.R1GeneralizedVector[] aR1GV = vectorSpaces();

		int iDimension = aR1GV.length;
		double[] adblLeftEdge = new double[iDimension];

		for (int i = 0; i < iDimension; ++i)
			adblLeftEdge[i] = ((org.drip.spaces.tensor.R1ContinuousVector) aR1GV[i]).leftEdge();

		return adblLeftEdge;
	}

	@Override public double[] rightDimensionEdge()
	{
		org.drip.spaces.tensor.R1GeneralizedVector[] aR1GV = vectorSpaces();

		int iDimension = aR1GV.length;
		double[] adblRightEdge = new double[iDimension];

		for (int i = 0; i < iDimension; ++i)
			adblRightEdge[i] = ((org.drip.spaces.tensor.R1ContinuousVector) aR1GV[i]).rightEdge();

		return adblRightEdge;
	}

	@Override public org.drip.spaces.tensor.Cardinality cardinality()
	{
		return org.drip.spaces.tensor.Cardinality.UncountablyInfinite();
	}

	@Override public double leftEdge()
	{
		double[] adblLeftEdge = leftDimensionEdge();

		int iDimension = adblLeftEdge.length;
		double dblLeftEdge = adblLeftEdge[0];

		for (int i = 1; i < iDimension; ++i) {
			if (dblLeftEdge > adblLeftEdge[i]) dblLeftEdge = adblLeftEdge[i];
		}

		return dblLeftEdge;
	}

	@Override public double rightEdge()
	{
		double[] adblRightEdge = rightDimensionEdge();

		int iDimension = adblRightEdge.length;
		double dblRightEdge = adblRightEdge[0];

		for (int i = 1; i < iDimension; ++i) {
			if (dblRightEdge < adblRightEdge[i]) dblRightEdge = adblRightEdge[i];
		}

		return dblRightEdge;
	}

	@Override public double hyperVolume()
		throws java.lang.Exception
	{
		if (!isPredictorBounded())
			throw new java.lang.Exception ("ContinuousVectorRd::hyperVolume => Space not Bounded");

		double[] adblLeftEdge = leftDimensionEdge();

		double dblHyperVolume = 1.;
		int iDimension = adblLeftEdge.length;

		double[] adblRightEdge = rightDimensionEdge();

		for (int i = 0; i < iDimension; ++i)
			dblHyperVolume *= (adblRightEdge[i] - adblLeftEdge[i]);

		return dblHyperVolume;
	}
}
