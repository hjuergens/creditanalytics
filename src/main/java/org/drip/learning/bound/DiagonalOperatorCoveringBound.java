
package org.drip.learning.bound;

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
 * DiagonalOperatorCoveringBound implements the Behavior of the Bound on the Covering Number of the Diagonal
 * 	Scaling Operator. The Asymptote is set as either
 * 
 * 				log [e_n(A)] ~ O {(1/log n)^alpha}
 * 
 * 		- OR -
 * 
 * 					  e_n(A) ~ O {(1/log n)^alpha}
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
 *  6) Williamson, R. C., A. J. Smola, and B. Scholkopf (2001): Generalization Performance of Regularization
 *  	Networks and Support Vector Machines via Entropy Numbers of Compact Operators, IEEE Transactions on
 *  	Information Theory 47 (6) 2516-2532.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DiagonalOperatorCoveringBound {

	/**
	 * Asymptote on the Base Diagonal Operator Entropy Number
	 */

	public static final int BASE_DIAGONAL_ENTROPY_ASYMPTOTE_EXPONENT = 1;

	/**
	 * Asymptote on the Log of the Diagonal Operator Entropy Number
	 */

	public static final int LOG_DIAGONAL_ENTROPY_ASYMPTOTE_EXPONENT = 2;

	private double _dblOperatorEntropyAsymptoteExponent = java.lang.Double.NaN;
	private int _iOperatorEntropyAsymptoteBase = BASE_DIAGONAL_ENTROPY_ASYMPTOTE_EXPONENT;

	/**
	 * DiagonalOperatorCoveringBound Constructor
	 * 
	 * @param iOperatorEntropyAsymptoteBase Indicate the Asymptote is on the Base Value or Log Value
	 * @param dblOperatorEntropyAsymptoteExponent The Entropy Number Asymptote Exponent
	 * 
	 * @throws java.lang.Exception Throws if the Inputs are Invalid
	 */

	public DiagonalOperatorCoveringBound (
		final int iOperatorEntropyAsymptoteBase,
		final double dblOperatorEntropyAsymptoteExponent)
		throws java.lang.Exception
	{
		if ((BASE_DIAGONAL_ENTROPY_ASYMPTOTE_EXPONENT != (_iOperatorEntropyAsymptoteBase =
			iOperatorEntropyAsymptoteBase) && LOG_DIAGONAL_ENTROPY_ASYMPTOTE_EXPONENT !=
				_iOperatorEntropyAsymptoteBase) || !org.drip.quant.common.NumberUtil.IsValid
					(_dblOperatorEntropyAsymptoteExponent = dblOperatorEntropyAsymptoteExponent))
			throw new java.lang.Exception ("DiagonalOperatorCoveringBound ctr => Invalid Inputs");
	}

	/**
	 * Retrieve the Entropy Number Asymptote Type
	 * 
	 * @return The Entropy Number Asymptote Type
	 */

	public int entropyNumberAsymptoteType()
	{
		return _iOperatorEntropyAsymptoteBase;
	}

	/**
	 * Retrieve the Entropy Number Asymptote Exponent
	 * 
	 * @return The Entropy Number Asymptote Exponent
	 */

	public double entropyNumberAsymptoteExponent()
	{
		return _dblOperatorEntropyAsymptoteExponent;
	}
}
