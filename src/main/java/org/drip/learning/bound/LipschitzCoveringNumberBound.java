
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
 * LipschitzCoveringNumberBound contains the Upper Bounds of the Covering Numbers induced by Lipschitz and
 * 	approximate Lipschitz Loss Function Class.
 *  
 * The Reference are:
 *  
 *  1) Bartlett, P. L., P. Long, and R. C. Williamson (1996): Fat-shattering and the Learnability of Real-
 *  	Valued Functions, Journal of Computational System Science, 52 (3) 434-452.
 * 
 *  2) Anthony, M., and P. L. Bartlett (1999): Artificial Neural Network Learning - Theoretical Foundations,
 *  	Cambridge University Press, Cambridge, UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LipschitzCoveringNumberBound {
	private double _dblLpUpperBound = java.lang.Double.NaN;
	private double _dblSupremumUpperBound = java.lang.Double.NaN;

	/**
	 * LipschitzCoveringNumberBound Constructor
	 * 
	 * @param dblSupremumUpperBound Supremum Upper Bound for the Covering Number
	 * @param dblLpUpperBound The Lp Upper Bound for the Covering Number
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public LipschitzCoveringNumberBound (
		final double dblSupremumUpperBound,
		final double dblLpUpperBound)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblSupremumUpperBound = dblSupremumUpperBound) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblLpUpperBound = dblLpUpperBound))
			throw new java.lang.Exception ("LipschitzCoveringNumberBound ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Supremum-based Covering Number Upper Bound
	 * 
	 * @return The Supremum-based Covering Number Upper Bound
	 */

	public double supremumUpperBound()
	{
		return _dblSupremumUpperBound;
	}

	/**
	 * Retrieve the Lp-based Covering Number Upper Bound
	 * 
	 * @return The Lp-based Covering Number Upper Bound
	 */

	public double lpUpperBound()
	{
		return _dblLpUpperBound;
	}

	/**
	 * Retrieve the Least Covering Number Upper Bound
	 * 
	 * @return The Least Covering Number Upper Bound
	 */

	public double leastUpperBound()
	{
		return _dblLpUpperBound > _dblSupremumUpperBound ? _dblSupremumUpperBound : _dblLpUpperBound;
	}
}
