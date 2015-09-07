
package org.drip.learning.regularization;

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
 * RegularizerFunction the R^1 -> R^1 and the R^d -> R^1 Regularization Functions.
 *  
 * The References are:
 *  
 *  1) Alon, N., S. Ben-David, N. Cesa Bianchi, and D. Haussler (1997): Scale-sensitive Dimensions, Uniform
 *  	Convergence, and Learnability, Journal of Association of Computational Machinery, 44 (4) 615-631.
 * 
 *  2) Anthony, M., and P. L. Bartlett (1999): Artificial Neural Network Learning - Theoretical Foundations,
 *  	Cambridge University Press, Cambridge, UK.
 *  
 *  3) Kearns, M. J., R. E. Schapire, and L. M. Sellie (1994): Towards Efficient Agnostic Learning, Machine
 *  	Learning, 17 (2) 115-141.
 *  
 *  4) Lee, W. S., P. L. Bartlett, and R. C. Williamson (1998): The Importance of Convexity in Learning with
 *  	Squared Loss, IEEE Transactions on Information Theory, 44 1974-1980.
 * 
 *  5) Vapnik, V. N. (1998): Statistical Learning Theory, Wiley, New York.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RegularizationFunction {
	private double _dblLambda = java.lang.Double.NaN;
	private org.drip.function.definition.R1ToR1 _regR1ToR1 = null;
	private org.drip.function.definition.RdToR1 _regRdToR1 = null;

	/**
	 * RegularizationFunction Constructor
	 * 
	 * @param regR1ToR1 R^1 -> R^1 Regularization Function
	 * @param regRdToR1 R^d -> R^1 Regularization Function
	 * @param dblLambda The Regularizer Lambda
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public RegularizationFunction (
		final org.drip.function.definition.R1ToR1 regR1ToR1,
		final org.drip.function.definition.RdToR1 regRdToR1,
		final double dblLambda)
		throws java.lang.Exception
	{
		if (null == (_regR1ToR1 = regR1ToR1) && null == (_regRdToR1 = regRdToR1) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblLambda = dblLambda))
			throw new java.lang.Exception ("RegularizationFunction ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the R^1 -> R^1 Regularization Function
	 * 
	 * @return The R^1 -> R^1 Regularization Function Instance
	 */

	public org.drip.function.definition.R1ToR1 r1Tor1()
	{
		return _regR1ToR1;
	}

	/**
	 * Retrieve the R^d -> R^1 Regularization Function
	 * 
	 * @return The R^d -> R^1 Regularization Function Instance
	 */

	public org.drip.function.definition.RdToR1 rdTor1()
	{
		return _regRdToR1;
	}

	/**
	 * Retrieve the Regularization Constant Lambda
	 * 
	 * @return The Regularization Constant Lambda
	 */

	public double lambda()
	{
		return _dblLambda;
	}
}
