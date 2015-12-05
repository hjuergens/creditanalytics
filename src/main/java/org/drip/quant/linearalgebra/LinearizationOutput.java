
package org.drip.quant.linearalgebra;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * LinearizationOutput holds the output of a sequence of linearization operations. It contains the
 * 	transformed original matrix, the transformed RHS, and the method used for the linearization operation.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LinearizationOutput {
	private double[] _adblTransformedRHS = null;
	private double[][] _aadblTransformedMatrix = null;
	private String _strLinearizationMethod = "";

	/**
	 * LinearizationOutput constructor
	 * 
	 * @param adblTransformedRHS The Transformed RHS
	 * @param aadblTransformedMatrix The Transformed Matrix
	 * @param strLinearizationMethod Method used for the Linearization
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public LinearizationOutput (
		final double[] adblTransformedRHS,
		final double[][] aadblTransformedMatrix,
		final String strLinearizationMethod)
		throws java.lang.Exception
	{
		if (null == (_adblTransformedRHS = adblTransformedRHS) || null == (_aadblTransformedMatrix =
			aadblTransformedMatrix) || null == (_strLinearizationMethod = strLinearizationMethod) ||
				_strLinearizationMethod.isEmpty())
			throw new java.lang.Exception ("LinearizationOutput ctr: Invalid Inputs");

		int iSize = _adblTransformedRHS.length;

		if (0 == iSize || iSize != _aadblTransformedMatrix.length || null == _aadblTransformedMatrix[0] ||
			iSize != _aadblTransformedMatrix[0].length)
			throw new java.lang.Exception ("LinearizationOutput ctr: Invalid Inputs");
	}

	/**
	 * The RHS
	 * 
	 * @return The RHS
	 */

	public double[] getTransformedRHS()
	{
		return _adblTransformedRHS;
	}

	/**
	 * The Transformed Matrix
	 * 
	 * @return The Transformed Matrix
	 */

	public double[][] getTransformedMatrix()
	{
		return _aadblTransformedMatrix;
	}

	/**
	 * The Linearization Method
	 * 
	 * @return The Linearization Method
	 */

	public String getLinearizationMethod()
	{
		return _strLinearizationMethod;
	}
}