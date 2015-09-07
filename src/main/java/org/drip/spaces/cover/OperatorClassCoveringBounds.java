
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
 * OperatorClassCoveringBounds implements the estimate Lower/Upper Bounds and/or Absolute Values of the
 * 	Covering Number for the Operator Class. The Main References are:
 * 
 * 	1) Guo, Y., P. L. Bartlett, J. Shawe-Taylor, and R. C. Williamson (1999): Covering Numbers for Support
 * 		Vector Machines, in: Proceedings of the 12th Annual Conference of Computational Learning Theory, ACM,
 * 		New York, 267-277.
 *
 * @author Lakshmi Krishnamurthy
 */

/**
 * @author Spooky
 *
 */
public interface OperatorClassCoveringBounds {

	/**
	 * Lower Bound of the Operator Entropy Number
	 * 
	 * @return Lower Bound of the Operator Entropy Number
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract double entropyNumberLowerBound()
		throws java.lang.Exception;

	/**
	 * Upper Bound of the Operator Entropy Number
	 * 
	 * @return Upper Bound of the Operator Entropy Number
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract double entropyNumberUpperBound()
		throws java.lang.Exception;

	/**
	 * Compute the Entropy Number Index of the Operator
	 * 
	 * @return The Entropy Number Index of the Operator
	 */

	public abstract int entropyNumberIndex();

	/**
	 * Compute the Metric Norm of the Operator
	 * 
	 * @return The Metric Norm of the Operator
	 * 
	 * @throws java.lang.Exception Thrown if the Metric Norm cannot be computed
	 */

	public abstract double norm()
		throws java.lang.Exception;

	/**
	 * Compute the Entropy Number Asymptotic Behavior
	 * 
	 * @return the Entropy Number Asymptote Instance
	 */

	public abstract org.drip.learning.bound.DiagonalOperatorCoveringBound entropyNumberAsymptote();
}
