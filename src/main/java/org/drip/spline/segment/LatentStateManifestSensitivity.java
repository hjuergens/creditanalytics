
package org.drip.spline.segment;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * LatentStateManifestSensitivity contains the Manifest Sensitivity generation control parameters and the
 * 	Manifest Sensitivity outputs related to the given Segment.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LatentStateManifestSensitivity {
	private double[] _adblDBasisCoeffDLocalManifest = null;
	private double[] _adblDBasisCoeffDPreceedingManifest = null;
	private double _dblDResponseDPreceedingManifest = java.lang.Double.NaN;
	private org.drip.spline.params.PreceedingManifestSensitivityControl _pmsc = null;

	/**
	 * LatentStateManifestSensitivity constructor
	 * 
	 * @param pmsc The Preceeding Manifest Measure Sensitivity Control Parameters
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public LatentStateManifestSensitivity (
		final org.drip.spline.params.PreceedingManifestSensitivityControl pmsc)
		throws java.lang.Exception
	{
		if (null == (_pmsc = pmsc))
			_pmsc = new org.drip.spline.params.PreceedingManifestSensitivityControl (true, 0, null);
	}

	/**
	 * Set the Array containing the Sensitivities of the Basis Coefficients to the Local Manifest Measure
	 * 
	 * @param adblDBasisCoeffDLocalManifest The Array containing the Sensitivities of the Basis Coefficients
	 * 	to the Local Manifest Measure
	 * 
	 * @return TRUE => Basis Coefficient Manifest Measure Sensitivity Array Entries successfully set
	 */

	public boolean setDBasisCoeffDLocalManifest (
		final double[] adblDBasisCoeffDLocalManifest)
	{
		if (null == adblDBasisCoeffDLocalManifest) return false;

		int iNumCoeff = adblDBasisCoeffDLocalManifest.length;
		_adblDBasisCoeffDLocalManifest = new double[iNumCoeff];

		if (0 == iNumCoeff) return false;

		for (int i = 0; i < iNumCoeff; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblDBasisCoeffDLocalManifest[i] =
				adblDBasisCoeffDLocalManifest[i]))
				return false;
		}

		return true;
	}

	/**
	 * Get the Array containing the Sensitivities of the Basis Coefficients to the Local Manifest Measure
	 * 
	 * @return The Array containing the Sensitivities of the Basis Coefficients to the Local Manifest Measure
	 */

	public double[] getDBasisCoeffDLocalManifest()
	{
		return _adblDBasisCoeffDLocalManifest;
	}

	/**
	 * Set the Array containing the Sensitivities of the Basis Coefficients to the Preceeding Manifest
	 * 	Measure
	 * 
	 * @param adblDBasisCoeffDLocalManifest The Array containing the Sensitivities of the Basis Coefficients
	 * 	to the Preceeding Manifest Measure
	 * 
	 * @return TRUE => Array Entries successfully set
	 */

	public boolean setDBasisCoeffDPreceedingManifest (
		final double[] adblDBasisCoeffDPreceedingManifest)
	{
		if (null == adblDBasisCoeffDPreceedingManifest) return false;

		int iNumCoeff = adblDBasisCoeffDPreceedingManifest.length;
		_adblDBasisCoeffDPreceedingManifest= new double[iNumCoeff];

		if (0 == iNumCoeff) return false;

		for (int i = 0; i < iNumCoeff; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblDBasisCoeffDPreceedingManifest[i] =
				adblDBasisCoeffDPreceedingManifest[i]))
				return false;
		}

		return true;
	}

	/**
	 * Get the Array containing the Sensitivities of the Basis Coefficients to the Preceeding Manifest
	 *	Measure
	 * 
	 * @return The Array containing the Sensitivities of the Basis Coefficients to the Preceeding Manifest
	 * 	Measure
	 */

	public double[] getDBasisCoeffDPreceedingManifest()
	{
		return _adblDBasisCoeffDPreceedingManifest;
	}

	/**
	 * Set the Sensitivity of the Segment Response to the Preceeding Manifest Measure
	 * 
	 * @param dblDResponseDPreceedingQuote Sensitivity of the Segment Response to the Preceeding Manifest
	 * 	Measure
	 * 
	 * @return TRUE => Sensitivity of the Segment Response to the Preceeding Manifest Measure successfully
	 * 	set
	 */

	public boolean setDResponseDPreceedingManifest (
		final double dblDResponseDPreceedingManifest)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDResponseDPreceedingManifest)) return false;

		_dblDResponseDPreceedingManifest = dblDResponseDPreceedingManifest;
		return true;
	}

	/**
	 * Get the Sensitivity of the Segment Response to the Preceeding Manifest Measure
	 * 
	 * @return The Sensitivity of the Segment Response to the Preceeding Manifest Measure
	 */

	public double getDResponseDPreceedingManifest()
	{
		return _dblDResponseDPreceedingManifest;
	}

	/**
	 * Get the Preceeding Manifest Measure Sensitivity Control Parameters
	 * 
	 * @return The Preceeding Manifest Measure Sensitivity Control Parameters
	 */

	public org.drip.spline.params.PreceedingManifestSensitivityControl getPMSC()
	{
		return _pmsc;
	}
}
