
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
 * RegularizerRdxToR1 exposes the Structural Loss and Risk Calculations for the specified Normed R^d -> Normed
 *  R^1 Learning Function.
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

public interface RegularizerRdToR1 {

	/**
	 * Retrieve the Regularization Constant Lambda
	 * 
	 * @return The Regularization Constant Lambda
	 */

	public abstract double lambda();

	/**
	 * Compute the Regularization Sample Structural Loss
	 * 
	 * @param funcRdToR1 R^d -> R^1 Function Instance
	 * @param aadblX The Sample Instance
	 * 
	 * @return The Regularization Sample Structural Loss
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract double structuralLoss (
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double[][] aadblX)
		throws java.lang.Exception;

	/**
	 * Compute the Regularization Sample Structural Loss
	 * 
	 * @param distRdR1 R^d R^1 Multivariate Measure
	 * @param funcRdToR1 R^d -> R^1 Function Instance
	 * @param aadblX The Sample Instance
	 * @param adblY The Response Instance
	 * 
	 * @return The Regularization Sample Structural Loss
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract double structuralRisk (
		final org.drip.measure.continuous.RdR1 distRdR1,
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double[][] aadblX,
		final double[] adblY)
		throws java.lang.Exception;
}
