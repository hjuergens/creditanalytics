
package org.drip.sample.matrix;

import org.drip.quant.common.FormatUtil;
import org.drip.quant.common.NumberUtil;
import org.drip.quant.linearalgebra.LinearSystemSolver;
import org.drip.quant.linearalgebra.LinearizationOutput;
import org.drip.quant.linearalgebra.Matrix;
import org.testng.annotations.Test;

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
 * LinearAlgebra implements Samples for Linear Algebra and Matrix Manipulations. It demonstrates the
 * 	following:
 * 	- Compute the inverse of a matrix, and multiply with the original to recover the unit matrix
 * 	- Solves system of linear equations using one the exposed techniques
 *
 * @author Lakshmi Krishnamurthy
 */

public class LinearAlgebra {

	/*
	 * Sample illustrating the Invocation of Base Matrix Inversion and Product Computation Verification.
	 * 
	 * 	WARNING: Insufficient Error Checking, so use caution
	 */

	private static final void InverseVerifyDump (
		final String strLabel,
		final double[][] aadblA)
	{
		double[][] aadblAInv = Matrix.InvertUsingGaussianElimination (aadblA);

		System.out.println ("--- TESTS FOR " + strLabel + "---");

		System.out.println ("---------------------------------");

		NumberUtil.Print2DArrayTriplet (
			"\tSOURCE" + strLabel,
			"INVERSE" + strLabel,
			"PRODUCT" + strLabel,
			aadblA,
			aadblAInv,
			Matrix.Product (
				aadblA,
				aadblAInv
			),
			false
		);

		System.out.println ("---------------------------------\n\n");
	}

	/*
	 * Sample illustrating the Invocation of Base Matrix Manipulation Functionality
	 * 
	 * 	WARNING: Insufficient Error Checking, so use caution
	 */

	public static final void MatrixManipulation()
	{
		InverseVerifyDump ("#A", new double[][] {
			{1, 2, 3},
			{4, 5, 6},
			{7, 8, 9.01}
		});

		InverseVerifyDump ("#B", new double[][] {
			{ 0.1667,  0.0000,  0.0000,  0.0000},
			{ 0.0000,  0.0000,  0.0000,  0.1667},
			{-0.6667,  0.5000,  0.0000,  0.0000},
			{ 2.6667, -3.0000,  1.0000,  0.0000}
		});

		InverseVerifyDump ("#C", new double[][] {
			{ 1.0000,  0.0000,  0.0000,  0.0000},
			{ 1.0000,  1.0000,  1.0000,  1.0000},
			{ 0.0000,  1.0000,  0.0000,  0.0000},
			{ 0.0000,  0.0000,  2.0000,  0.0000}
		});

		InverseVerifyDump ("#D", new double[][] {
			{ 0.0000,  1.0000},
			{ 1.0000,  2.0000}
		});

		InverseVerifyDump ("#E", new double[][] {
			{ 0.0000,  1.0000},
			{ 1.0000,  0.0000}
		});

		InverseVerifyDump ("#F", new double[][] {
			{ 1.0000,  0.0000,  0.0000,  0.0000},
			{ 1.0000,  1.0000,  1.0000,  1.0000},
			{-1.0000,  1.0000,  0.0000,  0.0000},
			{ 1.0000,  2.0000,  3.0000,  4.0000}
		});

		InverseVerifyDump ("#G", new double[][] {
			{ 0.0000,  1.0000,  0.0000,  0.0000},
			{ 0.0000,  0.0000,  2.0000,  0.0000},
			{ 0.0434,  0.0188, 16.0083, 24.0037},
			{ 0.0188,  0.0083, 24.0037, 48.0017}
		});
	}

	/*
	 * Sample illustrating the Invocation of Linear System Solver Functionality
	 * 
	 * 	WARNING: Insufficient Error Checking, so use caution
	 */

	public static final void LinearSystemSolver()
	{
		double[][] aadblA = new double[][] {
			{1.000, 0.500, 0.333,  0.000,  0.000, 0.000},
			{0.000, 0.000, 0.000,  1.000,  0.500, 0.333},
			{1.000, 1.000, 1.000, -1.000,  0.000, 0.000},
			{0.000, 0.500, 2.000,  0.000, -0.500, 0.000},
			{0.000, 1.000, 0.000,  0.000,  0.000, 0.000},
			{0.000, 0.000, 0.000,  0.000,  1.000, 0.000},
		};
		double[] adblB = new double[] {0.02, 0.026, 0., 0., 0., 0.};

		org.drip.quant.common.NumberUtil.Print2DArray (
			"\tCOEFF",
			aadblA,
			false
		);

		/*
		 * Solve the Linear System using Gaussian Elimination
		 */

		LinearizationOutput lssGaussianElimination = LinearSystemSolver.SolveUsingGaussianElimination (
			aadblA,
			adblB
		);

		for (int i = 0; i < lssGaussianElimination.getTransformedRHS().length; ++i)
			System.out.println ("GaussianElimination[" + i + "] = " + FormatUtil.FormatDouble
				(lssGaussianElimination.getTransformedRHS()[i], 0, 6, 1.));

		for (int i = 0; i < 6; ++i) {
			double dblRHS = 0.;

			for (int j = 0; j < 6; ++j)
				dblRHS += aadblA[i][j] * lssGaussianElimination.getTransformedRHS()[j];

			System.out.println ("RHS[" + i + "]: " + dblRHS);
		}

		/*
		 * Solve the Linear System using the Gauss-Seidel method
		 */

		/* LinearSystemSolution lssGaussSeidel = LinearSystemSolver.SolveUsingGaussSeidel (aadblA, adblB);

		for (int i = 0; i < lssGaussSeidel.getSolution().length; ++i)
			System.out.println ("GaussSeidel[" + i + "] = " + FormatUtil.FormatDouble (lssGaussSeidel.getSolution()[i], 0, 2, 1.)); */
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
	{
		MatrixManipulation();

		// LinearSystemSolver();
	}
}
