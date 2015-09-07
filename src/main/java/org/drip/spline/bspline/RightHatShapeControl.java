
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
 * RightHatShapeControl implements the BasisHatShapeControl interface for the right hat basis set as laid out
 *  in the basic framework outlined in Koch and Lyche (1989), Koch and Lyche (1993), and Kvasov (2000)
 *  Papers.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RightHatShapeControl extends org.drip.spline.bspline.BasisHatShapeControl {

	/**
	 * RightHatShapeControl constructor
	 * 
	 * @param dblLeftPredictorOrdinate The Left Predictor Ordinate
	 * @param dblRightPredictorOrdinate The Right Predictor Ordinate
	 * @param strShapeControlType Type of the Shape Controller to be used - NONE, LINEAR/QUADRATIC Rational
	 * @param dblTension Tension of the Tension Hat Function
	 * 
	 * @throws java.lang.Exception Thrown if the input is invalid
	 */

	public RightHatShapeControl (
		final double dblLeftPredictorOrdinate,
		final double dblRightPredictorOrdinate,
		final java.lang.String strShapeControlType,
		final double dblTension)
		throws java.lang.Exception
	{
		super (dblLeftPredictorOrdinate, dblRightPredictorOrdinate, strShapeControlType, dblTension);
	}

	@Override public double evaluate (
		final double dblPredictorOrdinate)
		throws java.lang.Exception
	{
		if (!in (dblPredictorOrdinate)) return 0.;

		double dblWidth = right() - left();

		double dblScale = 1. / (dblWidth * (6. + 6. * tension() * dblWidth + 2. * tension() * dblWidth *
			dblWidth));

		if (SHAPE_CONTROL_RATIONAL_LINEAR.equalsIgnoreCase (shapeControlType()))
			return dblScale / (1. + tension() * (dblPredictorOrdinate - left()));

		if (SHAPE_CONTROL_RATIONAL_QUADRATIC.equalsIgnoreCase (shapeControlType()))
			return dblScale / (1. + tension() * (right() - dblPredictorOrdinate) * (dblPredictorOrdinate -
				left()) / dblWidth);

		return (java.lang.Math.exp (-tension() * (dblPredictorOrdinate - left()))) / (dblWidth * (6. + 6. *
			tension() * dblWidth + tension() * dblWidth * dblWidth));
	}

	@Override public double derivative (
		final double dblPredictorOrdinate,
		final int iOrder)
		throws java.lang.Exception
	{
		if (0 >= iOrder)
			throw new java.lang.Exception ("RightHatShapeControl::derivative => Invalid Inputs");

		if (!in (dblPredictorOrdinate) || 0. == tension()) return 0.;

		double dblWidth = right() - left();

		if (SHAPE_CONTROL_RATIONAL_LINEAR.equalsIgnoreCase (shapeControlType()))
			return (org.drip.quant.common.NumberUtil.Factorial (iOrder) * java.lang.Math.pow (-tension(),
				iOrder) * java.lang.Math.pow (1. + tension() * (dblPredictorOrdinate - left()), -iOrder - 1))
					/ (dblWidth * (6. + 6. * tension() * dblWidth + 2. * tension() * dblWidth * dblWidth));

		if (SHAPE_CONTROL_RATIONAL_EXPONENTIAL.equalsIgnoreCase (shapeControlType()))
			return (java.lang.Math.pow (-tension(), iOrder) * java.lang.Math.exp (-tension() *
				(dblPredictorOrdinate - left()))) / (dblWidth * (6. + 6. * tension() * dblWidth + tension() *
					dblWidth * dblWidth));

		return super.derivative (dblPredictorOrdinate, iOrder);
	}

	@Override public double integrate (
		final double dblBegin,
		final double dblEnd)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblBegin) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			throw new java.lang.Exception ("RightHatShapeControl::integrate => Invalid Inputs");

		double dblBoundedBegin = org.drip.quant.common.NumberUtil.Bound (dblBegin, left(), right());

		double dblBoundedEnd = org.drip.quant.common.NumberUtil.Bound (dblEnd, left(), right());

		if (dblBoundedBegin >= dblBoundedEnd) return 0.;

		if (0. == tension()) return dblBoundedEnd - dblBoundedBegin;

		double dblWidth = right() - left();

		if (SHAPE_CONTROL_RATIONAL_LINEAR.equalsIgnoreCase (shapeControlType()))
			return (java.lang.Math.log ((1. + tension() * (dblBoundedEnd - left())) / (1. + tension() *
				(dblBoundedBegin - left())))) / tension() / (dblWidth * (6. + 6. * tension() * dblWidth + 2.
					* tension() * dblWidth * dblWidth));

		if (SHAPE_CONTROL_RATIONAL_EXPONENTIAL.equalsIgnoreCase (shapeControlType()))
			return (java.lang.Math.exp (-tension() * (dblBoundedBegin - left())) - java.lang.Math.exp
				(-tension() * (dblBoundedEnd - left()))) / tension() / (dblWidth * (6. + 6. * tension() *
					dblWidth + tension() * dblWidth * dblWidth));

		return super.integrate (dblBoundedBegin, dblBoundedEnd);
	}

	@Override public double normalizer()
		throws java.lang.Exception
	{
		double dblWidth = right() - left();

		if (0. == tension()) return dblWidth;

		if (SHAPE_CONTROL_RATIONAL_LINEAR.equalsIgnoreCase (shapeControlType()))
			return (java.lang.Math.log ((1. + tension() * dblWidth))) / tension() / (dblWidth * (6. + 6. *
				tension() * dblWidth + 2. * tension() * dblWidth * dblWidth));

		if (SHAPE_CONTROL_RATIONAL_EXPONENTIAL.equalsIgnoreCase (shapeControlType()))
			return (1. - java.lang.Math.exp (-tension() * dblWidth)) / tension() / (dblWidth * (6. + 6. *
				tension() * dblWidth + tension() * dblWidth * dblWidth));

		return super.integrate (left(), right());
	}
}
