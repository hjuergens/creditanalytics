
package org.drip.regression.spline;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * HermiteBasisSplineRegressor implements the Hermite basis spline regressor for the given basis spline. As
 *  part of the regression run, it executes the following:
 *  - Calibrate and compute the left and the right Jacobian.
 *  - Reset right node and re-run calibration.
 *  - Compute an intermediate value Jacobian.
 *
 * @author Lakshmi Krishnamurthy
 */

public class HermiteBasisSplineRegressor extends org.drip.regression.spline.BasisSplineRegressor {
	private String _strName = "";
	private org.drip.spline.segment.LatentStateResponseModel _seg1 = null;
	private org.drip.spline.segment.LatentStateResponseModel _seg2 = null;
	private org.drip.quant.calculus.WengertJacobian _wjLeft = null;
	private org.drip.quant.calculus.WengertJacobian _wjRight = null;
	private org.drip.quant.calculus.WengertJacobian _wjValue = null;

	/**
	 * Create an instance of Hermite BasisSplineRegressor
	 * 
	 * @param strName Regressor Name
	 * @param strScenarioName Regressor Scenario Name
	 * @param iNumBasis Number of Basis Functions
	 * @param iCk Ck
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public static final org.drip.regression.spline.BasisSplineRegressor CreateHermiteSplineRegressor (
		final String strName,
		final String strScenarioName,
		final int iNumBasis,
		final int iCk)
	{
		try {
			org.drip.spline.basis.FunctionSet fs =
				org.drip.spline.basis.FunctionSetBuilder.PolynomialBasisSet (new
					org.drip.spline.basis.PolynomialFunctionSetParams (iNumBasis));

			return null == fs ? null : new HermiteBasisSplineRegressor (strName, strScenarioName, fs, iCk);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private HermiteBasisSplineRegressor (
		final String strName,
		final String strScenarioName,
		final org.drip.spline.basis.FunctionSet fs,
		final int iCk)
		throws java.lang.Exception
	{
		super (strName, strScenarioName, fs, iCk);

		org.drip.spline.params.SegmentInelasticDesignControl segParams =
			org.drip.spline.params.SegmentInelasticDesignControl.Create (iCk, 1);

		org.drip.spline.params.ResponseScalingShapeControl rssc = new
			org.drip.spline.params.ResponseScalingShapeControl (true, new
				org.drip.function.R1ToR1.QuadraticRationalShapeControl (1.));

		if (null == (_seg1 = org.drip.spline.segment.LatentStateResponseModel.Create (0.0, 1.0, fs, rssc,
			segParams)) || null == (_seg2 = org.drip.spline.segment.LatentStateResponseModel.Create (1.0,
				2.0, fs, rssc, segParams)))
			throw new java.lang.Exception ("HermiteBasisSplineRegressor ctr: Cant create the segments");
	}

	@Override public boolean execRegression()
	{
		try {
			return null != (_wjLeft = _seg1.jackDCoeffDEdgeParams (new double[] {0., 1.}, new double[] {1.,
				4.}, new double[] {1.}, new double[] {6.}, null, null)) && null != (_wjRight =
					_seg2.jackDCoeffDEdgeParams (new double[] {1., 2.}, new double[] {4., 15.}, new double[]
						{6.}, new double[] {17.}, null, null)) && _seg2.calibrate (_seg1, 14., null) && null
							!= (_wjValue = _seg2.jackDResponseDEdgeInput (1.5, 1));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override public boolean postRegression (
		final org.drip.regression.core.RegressionRunDetail rnvd)
	{
		try {
			if (!rnvd.set (_strName + "_Seg1_0_0", "" + _seg1.responseValue (0.))) return false;

			if (!rnvd.set (_strName + "_Seg1_1_0", "" + _seg1.responseValue (1.))) return false;

			if (!rnvd.set (_strName + "_Seg1_Jack", _wjLeft.displayString()));

			if (!rnvd.set (_strName + "_Seg1_Head_Jack", _seg1.jackDCoeffDEdgeInputs().displayString()));

			if (!rnvd.set (_strName + "_Seg1_Monotone", _seg1.monotoneType().toString()));

			if (!rnvd.set (_strName + "_Seg2_1_0", "" + _seg2.responseValue (1.))) return false;

			if (!rnvd.set (_strName + "_Seg2_2_0", "" + _seg2.responseValue (2.))) return false;

			if (!rnvd.set (_strName + "_Seg2_Jack", _wjRight.displayString()));

			if (!rnvd.set (_strName + "_Seg2_Head_Jack", _seg2.jackDCoeffDEdgeInputs().displayString()));

			if (!rnvd.set (_strName + "_Seg2_Monotone", _seg2.monotoneType().toString()));

			return rnvd.set (_strName + "_Seg2_Value_Jack", _wjValue.displayString());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
