
package org.drip.sequence.random;

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
 * PrincipalFactorSequenceGenerator implements the Principal Factors Based Multivariate Random Sequence
 *  Generator Functionality.
 *
 * @author Lakshmi Krishnamurthy
 */

public class PrincipalFactorSequenceGenerator extends org.drip.sequence.random.MultivariateSequenceGenerator
{
	private double[][] _aadblFactor = null;
	private double[] _adblFactorWeight = null;

	/**
	 * PrincipalFactorSequenceGenerator Constructor
	 * 
	 * @param aUSG Array of Univariate Sequence Generators
	 * @param aadblCorrelation The Correlation Matrix
	 * @param iNumFactor Number of Factors
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public PrincipalFactorSequenceGenerator (
		final org.drip.sequence.random.UnivariateSequenceGenerator[] aUSG,
		final double[][] aadblCorrelation,
		final int iNumFactor)
		throws java.lang.Exception
	{
		super (aUSG, aadblCorrelation);

		int iNumVariate = aUSG.length;

		if (0 >= iNumFactor || iNumFactor > iNumVariate)
			throw new java.lang.Exception ("PrincipalFactorSequenceGenerator ctr: Invalid Inputs");

		org.drip.quant.eigen.QREigenComponentExtractor qrece = new
			org.drip.quant.eigen.QREigenComponentExtractor (80, 0.00001);

		org.drip.quant.eigen.EigenComponent[] aEC = qrece.orderedComponents (aadblCorrelation);

		if (null == aEC || 0 == aEC.length)
			throw new java.lang.Exception ("PrincipalFactorSequenceGenerator ctr: Invalid Inputs");

		double dblNormalizer = 0.;
		_adblFactorWeight = new double[iNumFactor];
		_aadblFactor = new double[iNumFactor][iNumVariate];

		for (int i = 0; i < iNumFactor; ++i) {
			for (int j = 0; j < iNumVariate; ++j)
				_aadblFactor[i] = aEC[i].eigenvector();

			_adblFactorWeight[i] = aEC[i].eigenvalue();

			dblNormalizer += _adblFactorWeight[i] * _adblFactorWeight[i];
		}

		dblNormalizer = java.lang.Math.sqrt (dblNormalizer);

		for (int i = 0; i < iNumFactor; ++i)
			_adblFactorWeight[i] /= dblNormalizer;
	}

	/**
	 * Retrieve the Number of Factors
	 * 
	 * @return The Number of Factors
	 */

	public int numFactor()
	{
		return _adblFactorWeight.length;
	}

	/**
	 * Retrieve the Principal Component Factor Array
	 * 
	 * @return The Principal Component Factor Array
	 */

	public double[][] factors()
	{
		return _aadblFactor;
	}

	/**
	 * Retrieve the Array of Factor Weights
	 * 
	 * @return The Array of Factor Weights
	 */

	public double[] factorWeight()
	{
		return _adblFactorWeight;
	}

	@Override public double[] random()
	{
		double[] adblBaseRandom = super.random();

		int iNumVariate = _aadblFactor[0].length;
		int iNumFactor = _adblFactorWeight.length;
		double[] adblRandom = new double[iNumFactor];

		if (iNumFactor == iNumVariate) return adblBaseRandom;

		for (int i = 0; i < iNumFactor; ++i) {
			adblRandom[i] = 0.;

			for (int j = 0; j < iNumVariate; ++j)
				adblRandom[i] += _aadblFactor[i][j] * adblBaseRandom[j];
		}

		return adblRandom;
	}
}
