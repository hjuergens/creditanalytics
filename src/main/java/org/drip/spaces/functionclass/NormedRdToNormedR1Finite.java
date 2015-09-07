
package org.drip.spaces.functionclass;

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
 * NormedRdToNormedR1Finite implements the Class F of f : Normed R^d -> Normed R^1 Spaces of Finite
 *  Functions.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class NormedRdToNormedR1Finite extends org.drip.spaces.functionclass.NormedRxToNormedR1Finite {

	/**
	 * NormedRdToNormedR1Finite Function Class Constructor
	 * 
	 * @param dblMaureyConstant The Maurey Constant
	 * @param aNormedRdToNormedR1 Array of the Function Spaces
	 * 
	 * @throws java.lang.Exception Thrown if NormedRdToNormedR1Class Instance cannot be created
	 */

	public NormedRdToNormedR1Finite (
		final double dblMaureyConstant,
		final org.drip.spaces.RxToR1.NormedRdToNormedR1[] aNormedRdToNormedR1)
		throws java.lang.Exception
	{
		super (dblMaureyConstant, aNormedRdToNormedR1);

		for (int i = 0; i < aNormedRdToNormedR1.length; ++i) {
			if (null == aNormedRdToNormedR1[i])
				throw new java.lang.Exception ("NormedRdToNormedR1Finite ctr: Invalid Input Function");
		}
	}

	/**
	 * Retrieve the Finite Class of R^d -> R^1 Functions
	 * 
	 * @return The Finite Class of R^d -> R^1 Functions
	 */

	public org.drip.function.definition.RdToR1[] functionRdToR1Set()
	{
		org.drip.spaces.RxToR1.NormedRdToNormedR1[] aNormedRdToNormedR1 =
			(org.drip.spaces.RxToR1.NormedRdToNormedR1[]) functionSpaces();

		if (null == aNormedRdToNormedR1) return null;

		int iNumFunction = aNormedRdToNormedR1.length;
		org.drip.function.definition.RdToR1[] aRdToR1 = new
			org.drip.function.definition.RdToR1[iNumFunction];

		for (int i = 0; i < iNumFunction; ++i)
			aRdToR1[i] = aNormedRdToNormedR1[i].function();

		return aRdToR1;
	}
}
