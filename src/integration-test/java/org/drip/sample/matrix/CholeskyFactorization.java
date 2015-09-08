
package org.drip.sample.matrix;

import org.drip.quant.common.NumberUtil;
import org.drip.quant.linearalgebra.Matrix;
import org.drip.service.api.CreditAnalytics;
import org.testng.annotations.Test;

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
 * CholeskyFactorization demonstrates the Cholesky Factorization and Transpose Reconciliation of the Input
 * 	Matrix.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CholeskyFactorization {

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArg)
		throws Exception
	{
		CreditAnalytics.Init ("");

		double[][] aadblA = {
			{1.0, 0.3, 0.0},
			{0.3, 1.0, 0.1},
			{0.0, 0.1, 1.0}
		};

		double[][] aadblACholesky = Matrix.CholeskyBanachiewiczFactorization (aadblA);

		double[][] aadblACholeskyTranspose = Matrix.Transpose (aadblACholesky);

		System.out.println ("\n\t------------------------------------------------------------------------------------------------------");

		NumberUtil.Print2DArrayTriplet (
			"\t\tSOURCE",
			"CHOLESKY",
			"ORIGINAL",
			aadblA,
			aadblACholesky,
			Matrix.Product (
				aadblACholesky,
				aadblACholeskyTranspose
			),
			false
		);

		System.out.println ("\t------------------------------------------------------------------------------------------------------");
	}
}
