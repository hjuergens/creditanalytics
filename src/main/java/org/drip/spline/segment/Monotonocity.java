
package org.drip.spline.segment;

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
 * This class contains the monotonicity details related to the given segment. Indicates whether the segment
 * 	is monotonic, and if not, whether it contains a maximum, a minimum, or an inflection.
 *
 * @author Lakshmi Krishnamurthy
 */

public class Monotonocity {

	/**
	 * MONOTONIC
	 */

	public static final int MONOTONIC = 2;

	/**
	 * NON-MONOTONIC
	 */

	public static final int NON_MONOTONIC = 4;

	/**
	 * NON MONOTONE - MINIMA
	 */

	public static final int MINIMA = 5;

	/**
	 * NON MONOTONE - MAXIMA
	 */

	public static final int MAXIMA = 6;

	/**
	 * NON MONOTONE - INFLECTION
	 */

	public static final int INFLECTION = 7;

	private int _iMonotoneType = -1;

	/**
	 * Monotonocity constructor
	 * 
	 * @param iMonotoneType One of the valid monotone types
	 * 
	 * @throws java.lang.Exception Thrown if the input monotone type is invalid
	 */

	public Monotonocity (
		final int iMonotoneType)
		throws java.lang.Exception
	{
		if (org.drip.spline.segment.Monotonocity.MONOTONIC != (_iMonotoneType = iMonotoneType) &&
			org.drip.spline.segment.Monotonocity.NON_MONOTONIC != _iMonotoneType &&
				org.drip.spline.segment.Monotonocity.MINIMA != _iMonotoneType &&
					org.drip.spline.segment.Monotonocity.MAXIMA != _iMonotoneType &&
						org.drip.spline.segment.Monotonocity.INFLECTION != _iMonotoneType)
			throw new java.lang.Exception ("Monotonocity ctr: Unknown monotone type " + _iMonotoneType);
	}

	/**
	 * Retrieve the Monotone Type
	 * 
	 * @return The Monotone Type
	 */

	public int type()
	{
		return _iMonotoneType;
	}

	@Override public String toString()
	{
		if (org.drip.spline.segment.Monotonocity.NON_MONOTONIC == _iMonotoneType) return "NON_MONOTONIC";

		if (org.drip.spline.segment.Monotonocity.MONOTONIC == _iMonotoneType) return "MONOTONIC    ";

		if (org.drip.spline.segment.Monotonocity.MINIMA == _iMonotoneType) return "MINIMA       ";

		if (org.drip.spline.segment.Monotonocity.MAXIMA == _iMonotoneType) return "MAXIMA       ";

		return "INFLECTION   ";
	}
}
