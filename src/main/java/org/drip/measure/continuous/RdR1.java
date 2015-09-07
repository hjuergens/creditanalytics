
package org.drip.measure.continuous;

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
 * Rd implements the Base Abstract Class behind R^d X R^1 Distributions. It exports Methods for incremental,
 *  cumulative, and inverse cumulative Distribution Densities.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class RdR1 {

	/**
	 * Compute the Cumulative under the Distribution to the given Variate Array/Variate Combination
	 * 
	 * @param adblX R^d The Variate Array to which the Cumulative is to be computed
	 * @param dblY R^1 The Variate to which the Cumulative is to be computed
	 * 
	 * @return The Cumulative under the Distribution to the given Variate Array/Variate Combination
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public abstract double cumulative (
		final double[] adblX,
		final double dblY)
		throws java.lang.Exception;

	/**
	 * Compute the Incremental under the Distribution between the Variate Array/Variate Pair
	 * 
	 * @param adblXLeft Left R^d Variate Array from which the Cumulative is to be computed
	 * @param dblYLeft Left R^1 Variate from which the Cumulative is to be computed
	 * @param adblXRight Right R^d Variate Array to which the Cumulative is to be computed
	 * @param dblYRight Right R^1 Variate to which the Cumulative is to be computed
	 * 
	 * @return The Incremental under the Distribution between the Variate Array/Variate Pair
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public abstract double incremental (
		final double[] adblXLeft,
		final double dblYLeft,
		final double[] adblXRight,
		final double dblYRight)
		throws java.lang.Exception;

	/**
	 * Compute the Density under the Distribution at the given Variate Array/Variate
	 * 
	 * @param adblX R^d The Variate Array to which the Cumulative is to be computed
	 * @param dblY R^1 The Variate to which the Cumulative is to be computed
	 * 
	 * @return The Density under the Distribution at the given Variate Array/Variate
	 * 
	 * @throws java.lang.Exception Thrown if the Input is Invalid
	 */

	public abstract double density (
		final double[] adblX,
		final double dblY)
		throws java.lang.Exception;
}
