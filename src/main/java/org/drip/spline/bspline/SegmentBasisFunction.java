
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
 * SegmentBasisFunction is the abstract class over which the local ordered envelope functions for the B Splines
 * 	are implemented. It exposes the following stubs:
 * 	- Retrieve the Order of the B Spline.
 * 	- Retrieve the Leading Predictor Ordinate.
 * 	- Retrieve the Following Predictor Ordinate.
 * 	- Retrieve the Trailing Predictor Ordinate.
 * 	- Compute the complete Envelope Integrand - this will serve as the Envelope Normalizer.
 * 	- Evaluate the Cumulative Normalized Integrand up to the given ordinate.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class SegmentBasisFunction extends org.drip.function.definition.R1ToR1 {
	private int _iBSplineOrder = -1;
	private double _dblLeadingPredictorOrdinate = java.lang.Double.NaN;
	private double _dblTrailingPredictorOrdinate = java.lang.Double.NaN;
	private double _dblFollowingPredictorOrdinate = java.lang.Double.NaN;

	protected SegmentBasisFunction (
		final int iBSplineOrder,
		final double dblLeadingPredictorOrdinate,
		final double dblFollowingPredictorOrdinate,
		final double dblTrailingPredictorOrdinate)
		throws java.lang.Exception
	{
		super (null);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblLeadingPredictorOrdinate =
			dblLeadingPredictorOrdinate) || !org.drip.quant.common.NumberUtil.IsValid
				(_dblFollowingPredictorOrdinate = dblFollowingPredictorOrdinate) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblTrailingPredictorOrdinate =
						dblTrailingPredictorOrdinate) || _dblLeadingPredictorOrdinate >=
							_dblFollowingPredictorOrdinate || _dblFollowingPredictorOrdinate >=
								_dblTrailingPredictorOrdinate || 2 > (_iBSplineOrder = iBSplineOrder))
			throw new java.lang.Exception ("SegmentBasisFunction ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Order of the B Spline
	 * 
	 * @return The Order of the B Spline
	 */

	public int bSplineOrder()
	{
		return _iBSplineOrder;
	}

	/**
	 * Retrieve the Leading Predictor Ordinate
	 * 
	 * @return The Leading Predictor Ordinate
	 */

	public double leading()
	{
		return _dblLeadingPredictorOrdinate;
	}

	/**
	 * Retrieve the Following Predictor Ordinate
	 * 
	 * @return The Following Predictor Ordinate
	 */

	public double following()
	{
		return _dblFollowingPredictorOrdinate;
	}

	/**
	 * Retrieve the Trailing Predictor Ordinate
	 * 
	 * @return The Trailing Predictor Ordinate
	 */

	public double trailing()
	{
		return _dblTrailingPredictorOrdinate;
	}

	/**
	 * Compute the complete Envelope Integrand - this will serve as the Envelope Normalizer.
	 * 
	 * @return The Complete Envelope Integrand.
	 * 
	 * @throws java.lang.Exception Thrown if the Complete Envelope Integrand cannot be calculated.
	 */

	public abstract double normalizer()
		throws java.lang.Exception;

	/**
	 * Evaluate the Cumulative Normalized Integrand up to the given ordinate
	 * 
	 * @param dblPredictorOrdinate The Predictor Ordinate
	 * 
	 * @return The Cumulative Normalized Integrand up to the given ordinate
	 * 
	 * @throws java.lang.Exception Thrown if input is invalid
	 */

	public abstract double normalizedCumulative (
		final double dblPredictorOrdinate)
		throws java.lang.Exception;
}
