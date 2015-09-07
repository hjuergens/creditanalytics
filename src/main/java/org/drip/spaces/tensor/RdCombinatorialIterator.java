
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
 * RdCombinatorialIterator contains the Functionality to iterate through an R^d Combinatorial Space.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class RdCombinatorialIterator extends org.drip.analytics.support.MultidimensionalInterator {
	private org.drip.spaces.tensor.R1CombinatorialVector[] _aR1CV = null;

	/**
	 * Retrieve the RdCombinatorialIterator Instance associated with the Underlying Vector Space
	 * 
	 * @param aR1CV Array of R^1 Combinatorial Vectors
	 * 
	 * @return The RdCombinatorialIterator Instance associated with the Underlying Vector Space
	 */

	public static final RdCombinatorialIterator Standard (
		final org.drip.spaces.tensor.R1CombinatorialVector[] aR1CV)
	{
		if (null == aR1CV) return null;

		int iDimension = aR1CV.length;
		int[] aiMax = new int[iDimension];

		if (0 == iDimension) return null;

		for (int i = 0; i < iDimension; ++i)
			aiMax[i] = (int) aR1CV[i].cardinality().number();

		try {
			return new RdCombinatorialIterator (aR1CV, aiMax);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * RdCombinatorialIterator Constructor
	 * 
	 * @param aR1CV Array of the R^1 Combinatorial Vectors
	 * @param aiMax The Array of Dimension Maximum
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public RdCombinatorialIterator (
		final org.drip.spaces.tensor.R1CombinatorialVector[] aR1CV,
		final int[] aiMax)
		throws java.lang.Exception
	{
		super (aiMax, false);

		if (null == (_aR1CV = aR1CV) || _aR1CV.length != aiMax.length)
			throw new java.lang.Exception ("RdCombinatorialIterator ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Array of the R^1 Combinatorial Vectors
	 * 
	 * @return The Array of the R^1 Combinatorial Vectors
	 */

	public org.drip.spaces.tensor.R1CombinatorialVector[] r1()
	{
		return _aR1CV;
	}

	/**
	 * Convert the Vector Space Index Array to the Variate Array
	 * 
	 * @param aiIndex Vector Space Index Array
	 * 
	 * @return Variate Array
	 */

	public double[] vectorSpaceIndexToVariate (
		final int[] aiIndex)
	{
		if (null == aiIndex) return null;

		int iDimension = _aR1CV.length;
		double[] adblVariate = new double[iDimension];

		if (iDimension != aiIndex.length) return null;

		for (int i = 0; i < iDimension; ++i)
			adblVariate[i] = _aR1CV[i].elementSpace().get (aiIndex[i]);

		return adblVariate;
	}

	/**
	 * Retrieve the Cursor Variate Array
	 * 
	 * @return The Cursor Variate Array
	 */

	public double[] cursorVariates()
	{
		return vectorSpaceIndexToVariate (cursor());
	}

	/**
	 * Retrieve the Subsequent Variate Array
	 * 
	 * @return The Subsequent Variate Array
	 */

	public double[] nextVariates()
	{
		return vectorSpaceIndexToVariate (next());
	}
}
