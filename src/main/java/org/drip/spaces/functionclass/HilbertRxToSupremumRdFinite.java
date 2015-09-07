
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
 * HilbertRxToSupremumRdFinite implements the Class F with f E f : Hilbert R^x -> Supremum R^d Space of
 *  Finite Functions.
 * 
 *  The References are:
 *  
 *  1) Carl, B. (1985): Inequalities of the Bernstein-Jackson type and the Degree of Compactness of Operators
 *  	in Banach Spaces, Annals of the Fourier Institute 35 (3) 79-118.
 *  
 *  2) Carl, B., and I. Stephani (1990): Entropy, Compactness, and the Approximation of Operators, Cambridge
 *  	University Press, Cambridge UK. 
 *  
 *  3) Williamson, R. C., A. J. Smola, and B. Scholkopf (2000): Entropy Numbers of Linear Function Classes,
 *  	in: Proceedings of the 13th Annual Conference on Computational Learning Theory, ACM New York.
 *
 * @author Lakshmi Krishnamurthy
 */

public class HilbertRxToSupremumRdFinite extends org.drip.spaces.functionclass.NormedRxToNormedRdFinite {

	/**
	 * HilbertRxToSupremumRdFinite Constructor
	 * 
	 * @param dblMaureyConstant Maurey Constant
	 * @param aHilbertRxToSupremumRd Array of the Hilbert R^x -> Supremum R^d Spaces
	 *  
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public HilbertRxToSupremumRdFinite (
		final double dblMaureyConstant,
		final org.drip.spaces.RxToRd.NormedRxToNormedRd[] aHilbertRxToSupremumRd)
		throws java.lang.Exception
	{
		super (dblMaureyConstant, aHilbertRxToSupremumRd);

		if (2 != aHilbertRxToSupremumRd[0].inputMetricVectorSpace().pNorm() || java.lang.Integer.MAX_VALUE !=
			aHilbertRxToSupremumRd[0].outputMetricVectorSpace().pNorm())
			throw new java.lang.Exception ("HilbertRxToSupremumRdFinite ctr: Invalid Inputs");
	}
}
