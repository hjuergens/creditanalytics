
package org.drip.spline.tension;

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
 * KLKHyperbolicTensionPhy implements the basic framework and the family of C2 Tension Splines outlined in
 *  Koch and Lyche (1989), Koch and Lyche (1993), and Kvasov (2000) Papers.
 *
 * KLKHyperbolicTensionPsy implements the custom evaluator, differentiator, and integrator for the KLK
 *  Tension Phy Functions outlined in the publications above.
 *  
 * @author Lakshmi Krishnamurthy
 */

public class KLKHyperbolicTensionPhy extends org.drip.function.definition.R1ToR1 {
	private double _dblTension = java.lang.Double.NaN;

	/**
	 * KLKHyperbolicTensionPhy constructor
	 * 
	 * @param dblTension Tension of the HyperbolicTension Function
	 * 
	 * @throws java.lang.Exception Thrown if the input is invalid
	 */

	public KLKHyperbolicTensionPhy (
		final double dblTension)
		throws java.lang.Exception
	{
		super (null);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblTension = dblTension))
			throw new java.lang.Exception ("KLKHyperbolicTensionPhy ctr: Invalid Inputs");
	}

	@Override public double evaluate (
		final double dblVariate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblVariate))
			throw new java.lang.Exception ("KLKHyperbolicTensionPhy::evaluate => Invalid Inputs");

		return java.lang.Math.sinh (_dblTension * dblVariate) / java.lang.Math.sinh (_dblTension);
	}

	@Override public double derivative (
		final double dblVariate,
		final int iOrder)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblVariate) || 0 > iOrder)
			throw new java.lang.Exception ("KLKHyperbolicTensionPhy::derivative => Invalid Inputs");

		return java.lang.Math.pow (_dblTension, iOrder) * java.lang.Math.sinh (_dblTension * dblVariate) /
			java.lang.Math.sinh (_dblTension);
	}

	@Override public double integrate (
		final double dblBegin,
		final double dblEnd)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblBegin) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			throw new java.lang.Exception ("HyperbolicTension::integrate => Invalid Inputs");

		return (java.lang.Math.cosh (_dblTension * dblEnd) - java.lang.Math.cosh (_dblTension * dblBegin)) /
			(_dblTension * java.lang.Math.sinh (_dblTension));
	}

	/**
	 * Retrieve the Tension Parameter
	 * 
	 * @return Tension Parameter
	 */

	public double getTension()
	{
		return _dblTension;
	}

	public static final void main (
		final String[] astrArgs)
		throws java.lang.Exception
	{
		KLKHyperbolicTensionPhy khtp = new KLKHyperbolicTensionPhy (2.);

		System.out.println ("KLKHyperbolicTensionPhy[0.0] = " + khtp.evaluate (0.0));

		System.out.println ("KLKHyperbolicTensionPhy[0.5] = " + khtp.evaluate (0.5));

		System.out.println ("KLKHyperbolicTensionPhy[1.0] = " + khtp.evaluate (1.0));

		System.out.println ("KLKHyperbolicTensionPhyDeriv[0.0] = " + khtp.derivative (0.0, 2));

		System.out.println ("KLKHyperbolicTensionPhyDeriv[0.5] = " + khtp.derivative (0.5, 2));

		System.out.println ("KLKHyperbolicTensionPhyDeriv[1.0] = " + khtp.derivative (1.0, 2));
	}
}
