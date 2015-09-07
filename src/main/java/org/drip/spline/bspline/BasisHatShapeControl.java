
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
 * BasisHatShapeControl implements the shape control function for the hat basis set as laid out in the
 *  framework outlined in Koch and Lyche (1989), Koch and Lyche (1993), and Kvasov (2000) Papers.
 *  
 *  Currently BasisHatShapeControl implements the following shape control customizers:
 *  - Cubic Polynomial with Rational Linear Shape Controller.
 *  - Cubic Polynomial with Rational Quadratic Shape Controller.
 *  - Cubic Polynomial with Rational Exponential Shape Controller.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class BasisHatShapeControl extends org.drip.spline.bspline.TensionBasisHat {

	/**
	 * Cubic Polynomial with Rational Linear Shape Controller
	 */

	public static final String SHAPE_CONTROL_RATIONAL_LINEAR =
		"SHAPE_CONTROL_RATIONAL_LINEAR";

	/**
	 * Cubic Polynomial with Rational Quadratic Shape Controller
	 */

	public static final String SHAPE_CONTROL_RATIONAL_QUADRATIC =
		"SHAPE_CONTROL_RATIONAL_QUADRATIC";

	/**
	 * Cubic Polynomial with Rational Exponential Shape Controller
	 */

	public static final String SHAPE_CONTROL_RATIONAL_EXPONENTIAL =
		"SHAPE_CONTROL_RATIONAL_EXPONENTIAL";

	private String _strShapeControlType = "";

	/**
	 * BasisHatShapeControl constructor
	 * 
	 * @param dblLeftPredictorOrdinate The Left Predictor Ordinate
	 * @param dblRightPredictorOrdinate The Right Predictor Ordinate
	 * @param strShapeControlType Type of the Shape Controller to be used - LINEAR/QUADRATIC/EXPONENTIAL
	 * 	Rational
	 * @param dblTension Tension of the Tension Hat Function
	 * 
	 * @throws java.lang.Exception Thrown if the input is invalid
	 */

	public BasisHatShapeControl (
		final double dblLeftPredictorOrdinate,
		final double dblRightPredictorOrdinate,
		final String strShapeControlType,
		final double dblTension)
		throws java.lang.Exception
	{
		super (dblLeftPredictorOrdinate, dblRightPredictorOrdinate, dblTension);

		if (null == (_strShapeControlType = strShapeControlType) ||
			(!SHAPE_CONTROL_RATIONAL_LINEAR.equalsIgnoreCase (_strShapeControlType) &&
				!SHAPE_CONTROL_RATIONAL_QUADRATIC.equalsIgnoreCase (_strShapeControlType) &&
					!SHAPE_CONTROL_RATIONAL_EXPONENTIAL.equalsIgnoreCase (_strShapeControlType)))
			throw new java.lang.Exception ("BasisHatShapeControl ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Type of the Shape Controller
	 * 
	 * @return The Type of the Shape Controller
	 */

	public String shapeControlType()
	{
		return _strShapeControlType;
	}
}
