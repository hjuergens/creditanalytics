
package org.drip.spaces.cover;

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
 * CarlStephaniNormedBounds contains the Normed Bounds that result from the Convolution Product of 2 Normed
 *  R^x -> Normed R^x Function Spaces.
 * 
 *  The References are:
 *  
 *  1) Carl, B. (1985): Inequalities of the Bernstein-Jackson type and the Degree of Compactness of Operators
 *  	in Banach Spaces, Annals of the Fourier Institute 35 (3) 79-118.
 *  
 *  2) Carl, B., and I. Stephani (1990): Entropy, Compactness, and the Approximation of Operators, Cambridge
 *  	University Press, Cambridge UK. 
 *  
 *  3) Williamson, R. C., A. J. Smola, and B. Scholkopf (2000): Entropy Numbers of Linear Function Classes,
 *  	in: Proceedings of the 13th Annual Conference on Computational Learning Theory, ACM New York.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CarlStephaniNormedBounds {
	private double _dblEntropyBoundNormA = java.lang.Double.NaN;
	private double _dblEntropyBoundNormB = java.lang.Double.NaN;

	/**
	 * CarlStephaniNormedBounds Constructor
	 * 
	 * @param dblEntropyBoundNormA The Entropy Bound using the Function Class Norm A
	 * @param dblEntropyBoundNormB The Entropy Bound using the Function Class Norm B
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CarlStephaniNormedBounds (
		final double dblEntropyBoundNormA,
		final double dblEntropyBoundNormB)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblEntropyBoundNormA = dblEntropyBoundNormA) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblEntropyBoundNormB = dblEntropyBoundNormB))
			throw new java.lang.Exception ("CarlStephaniNormedBounds Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Entropy Bound using the Function Class Norm A
	 * 
	 * @return The Entropy Bound using the Function Class Norm A
	 */

	public double entropyBoundNormA()
	{
		return _dblEntropyBoundNormA;
	}

	/**
	 * Retrieve the Entropy Bound using the Function Class Norm B
	 * 
	 * @return The Entropy Bound using the Function Class Norm B
	 */

	public double entropyBoundNormB()
	{
		return _dblEntropyBoundNormB;
	}

	/**
	 * Retrieve the Minimum Upper Entropy Bound
	 * 
	 * @return The Minimum Upper Entropy Bound
	 */

	public double minimumUpperBound()
	{
		return _dblEntropyBoundNormA < _dblEntropyBoundNormB ? _dblEntropyBoundNormA : _dblEntropyBoundNormB;
	}
}
