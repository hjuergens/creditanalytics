
package org.drip.spline.bspline;

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
 * ExponentialTensionRightHat implements the TensionBasisHat interface in accordance with the right
 *  exponential hat basis function laid out in the basic framework outlined in Koch and Lyche (1989), Koch
 *  and Lyche (1993), and Kvasov (2000) Papers.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ExponentialTensionRightHat extends org.drip.spline.bspline.TensionBasisHat {

	/**
	 * ExponentialTensionRightHat constructor
	 * 
	 * @param dblLeftPredictorOrdinate The Left Predictor Ordinate
	 * @param dblRightPredictorOrdinate The Right Predictor Ordinate
	 * @param dblTension Tension of the Tension Hat Function
	 * 
	 * @throws java.lang.Exception Thrown if the input is invalid
	 */

	public ExponentialTensionRightHat (
		final double dblLeftPredictorOrdinate,
		final double dblRightPredictorOrdinate,
		final double dblTension)
		throws java.lang.Exception
	{
		super (dblLeftPredictorOrdinate, dblRightPredictorOrdinate, dblTension);
	}

	@Override public double evaluate (
		final double dblPredictorOrdinate)
		throws java.lang.Exception
	{
		if (!in (dblPredictorOrdinate)) return 0.;

		return java.lang.Math.sinh (tension() * (right() - dblPredictorOrdinate)) * normalizer();
	}

	@Override public double derivative (
		final double dblPredictorOrdinate,
		final int iOrder)
		throws java.lang.Exception
	{
		if (0 > iOrder)
			throw new java.lang.Exception ("ExponentialTensionRightHat::derivative => Invalid Inputs");

		if (!in (dblPredictorOrdinate)) return 0.;

		return java.lang.Math.pow (-1. * tension(), iOrder) * (0 == iOrder % 2 ? java.lang.Math.sinh
			(tension() * (right() - dblPredictorOrdinate)) : java.lang.Math.cosh (tension() * (right() -
				dblPredictorOrdinate))) / normalizer();
	}

	@Override public double integrate (
		final double dblBegin,
		final double dblEnd)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblBegin) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			throw new java.lang.Exception ("ExponentialTensionRightHat::integrate => Invalid Inputs");

		double dblBoundedBegin = org.drip.quant.common.NumberUtil.Bound (dblBegin, left(), right());

		double dblBoundedEnd = org.drip.quant.common.NumberUtil.Bound (dblEnd, left(), right());

		if (dblBoundedBegin >= dblBoundedEnd) return 0.;

		if (0. == tension()) return dblBoundedEnd - dblBoundedBegin;

		return -1. * (java.lang.Math.cosh (tension() * (right() - dblBoundedEnd)) - java.lang.Math.cosh
			(tension() * (right() - dblBoundedBegin))) * normalizer() / tension();
	}

	@Override public double normalizer()
		throws java.lang.Exception
	{
		return (java.lang.Math.cosh (tension() * (right() - left())) - 1.) / tension();
	}
}
