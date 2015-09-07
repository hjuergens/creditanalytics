
package org.drip.learning.kernel;

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
 * DiagonalScalingOperator implements the Scaling Operator that is used to determine the Bounds of the R^x L2
 *  -> R^x L2 Kernel Linear Integral Operator defined by:
 * 
 * 		T_k [f(.)] := Integral Over Input Space {k (., y) * f(y) * d[Prob(y)]}
 *  
 *  The References are:
 *  
 *  1) Ash, R. (1965): Information Theory, Inter-science New York.
 *  
 *  2) Konig, H. (1986): Eigenvalue Distribution of Compact Operators, Birkhauser, Basel, Switzerland. 
 *  
 *  3) Gordon, Y., H. Konig, and C. Schutt (1987): Geometric and Probabilistic Estimates of Entropy and
 *  	Approximation Numbers of Operators, Journal of Approximation Theory 49 219-237.
 *  
 * 	4) Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 * 
 *  5) Smola, A. J., A. Elisseff, B. Scholkopf, and R. C. Williamson (2000): Entropy Numbers for Convex
 *  	Combinations and mlps, in: Advances in Large Margin Classifiers, A. Smola, P. Bartlett, B. Scholkopf,
 *  	and D. Schuurmans - editors, MIT Press, Cambridge, MA.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class DiagonalScalingOperator implements org.drip.spaces.cover.OperatorClassCoveringBounds {
	private double[] _adblDiagonalScaler = null;
	private double _dblScalingProductSupremumBound = java.lang.Double.NaN;

	/**
	 * DiagonalScalingOperator Constructor
	 * 
	 * @param adblDiagonalScaler The Diagonal Scaling Multiplier Array
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public DiagonalScalingOperator (
		final double[] adblDiagonalScaler)
		throws java.lang.Exception
	{
		if (null == (_adblDiagonalScaler = adblDiagonalScaler))
			throw new java.lang.Exception ("DiagonalScalingOperator Constructor: Invalid Inputs");

		double dblScalingProduct = 1.;
		int iScalingSize = _adblDiagonalScaler.length;

		if (0 == iScalingSize)
			throw new java.lang.Exception ("DiagonalScalingOperator Constructor: Invalid Inputs");

		for (int i = 0; i < iScalingSize; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblDiagonalScaler[i]) || 0. >
				_adblDiagonalScaler[i])
				throw new java.lang.Exception ("DiagonalScalingOperator Constructor: Invalid Inputs");

			if (0 == i) _dblScalingProductSupremumBound = _adblDiagonalScaler[i];

			if (i > 0) {
				if (_adblDiagonalScaler[i - 1] < _adblDiagonalScaler[i])
					throw new java.lang.Exception ("DiagonalScalingOperator Constructor: Invalid Inputs");

				double dblCurrentSupremumBound = java.lang.Math.pow ((dblScalingProduct *=
					_adblDiagonalScaler[i]) / iScalingSize, 1. / i);

				if (_dblScalingProductSupremumBound < dblCurrentSupremumBound)
					_dblScalingProductSupremumBound = dblCurrentSupremumBound;
			}
		}
	}

	/**
	 * Retrieve the Diagonal Scaling Multiplier Array
	 * 
	 * @return The Diagonal Scaling Multiplier Array
	 */

	public double[] scaler()
	{
		return _adblDiagonalScaler;
	}

	@Override public int entropyNumberIndex()
	{
		return _adblDiagonalScaler.length;
	}

	@Override public double entropyNumberLowerBound()
	{
		return _dblScalingProductSupremumBound;
	}

	@Override public double entropyNumberUpperBound()
	{
		return 6. * _dblScalingProductSupremumBound;
	}
}
