
package org.drip.learning.RxToR1;

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
 * EmpiricalPenaltySupremum holds the Learning Function that corresponds to the Empirical Supremum, as well
 *  as the corresponding Supremum Value.
 *
 * @author Lakshmi Krishnamurthy
 */

public class EmpiricalPenaltySupremum {
	private int _iIndex = -1;
	private double _dblValue = java.lang.Double.NaN;

	public EmpiricalPenaltySupremum (
		final int iIndex,
		final double dblValue)
		throws java.lang.Exception
	{
		if (0 > (_iIndex = iIndex) || !org.drip.quant.common.NumberUtil.IsValid (_dblValue = dblValue))
			throw new java.lang.Exception ("EmpiricalPenaltySupremum Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Index of the Supremum Empirical Function
	 * 
	 * @return The Index of the Supremum Empirical Function
	 */

	public int index()
	{
		return _iIndex;
	}

	/**
	 * Retrieve the Value of the Supremum Empirical Function
	 * 
	 * @return The Value of the Supremum Empirical Function
	 */

	public double value()
	{
		return _dblValue;
	}
}
