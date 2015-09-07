
package org.drip.state.curve;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * Copyright (C) 2011 Lakshmi Krishnamurthy
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
 * DerivedFXBasis manages the constant forward basis based FX Basis Curve holder object. It exports the
 *  following functionality:
 *  - Extract currency, currency pair, spot epoch, spot FX, and whether the basis is boot-strapped
 *  - Compute the FX Forward Array
 *  - Retrieve Array of the Calibration Components
 *  - Retrieve the Curve Construction Input Set
 *  - Synthesize scenario Latent State by parallel shifting/custom tweaking the quantification metric
 *  - Synthesize scenario Latent State by parallel/custom shifting/custom tweaking the manifest measure
 *  - Serialize into and de-serialize out of byte array
 *
 * @author Lakshmi Krishnamurthy
 */

public class DerivedFXBasis extends org.drip.analytics.definition.FXBasisCurve {
	private double[] _adblDate = null;
	private double[] _adblFXBasis = null;
	private boolean _bIsFXBasisBootstrapped = false;
	private double _dblFXSpot = java.lang.Double.NaN;
	private double _dblSpotDate = java.lang.Double.NaN;
	private org.drip.product.params.CurrencyPair _cp = null;

	/**
	 * Construct an DerivedFXBasis instance from the currency pair, FX Spot, and FX basis parameters
	 * 
	 * @param cp Currency Pair
	 * @param dtSpot Spot Date
	 * @param dblFXSpot FX Spot
	 * @param adblDate Array of dates
	 * @param adblFXBasis Array of FX Basis
	 * @param bIsFXBasisBootstrapped True if the inputs are for bootstrapped FX basis
	 * 
	 * @throws java.lang.Exception Thrown if the FXBasis instance cannot be created
	 */

	public DerivedFXBasis (
		final org.drip.product.params.CurrencyPair cp,
		final org.drip.analytics.date.JulianDate dtSpot,
		final double dblFXSpot,
		final double[] adblDate,
		final double[] adblFXBasis,
		final boolean bIsFXBasisBootstrapped)
		throws java.lang.Exception
	{
		if (null == cp || null == dtSpot || !org.drip.quant.common.NumberUtil.IsValid (dblFXSpot) || null ==
			adblDate || 0 == adblDate.length || null == adblFXBasis || 0 == adblFXBasis.length ||
				adblDate.length != adblFXBasis.length)
			throw new java.lang.Exception ("DerivedFXBasis ctr: Invalid Params");

		_dblSpotDate = dtSpot.julian();

		_cp = cp;
		_dblFXSpot = dblFXSpot;
		_adblDate = new double[adblDate.length];
		_adblFXBasis = new double[adblFXBasis.length];
		_bIsFXBasisBootstrapped = bIsFXBasisBootstrapped;

		for (int i = 0; i < adblFXBasis.length; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (adblDate[i]) || adblDate[i] <= _dblSpotDate)
				throw new java.lang.Exception ("DerivedFXBasis ctr: Invalid params: Node date " +
					org.drip.analytics.date.DateUtil.FromJulian (adblDate[i]) + " before spot " + dtSpot);

			_adblDate[i] = adblDate[i];
			_adblFXBasis[i] = adblFXBasis[i];
		}
	}

	@Override public org.drip.product.params.CurrencyPair currencyPair()
	{
		return _cp;
	}

	@Override public org.drip.analytics.date.JulianDate spotDate()
	{
		try {
			return new org.drip.analytics.date.JulianDate (_dblSpotDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public double fxSpot()
	{
		return _dblFXSpot;
	}

	@Override public String currency()
	{
		return _cp.denomCcy();
	}

	@Override public org.drip.param.valuation.CollateralizationParams collateralParams()
	{
		return null;
	}

	@Override public boolean isBasisBootstrapped()
	{
		return _bIsFXBasisBootstrapped;
	}

	@Override public double[] fxForward (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final boolean bBasisOnDenom,
		final boolean bFwdAsPIP)
	{
		org.drip.analytics.rates.DiscountCurve dcBasisAdj = null;
		double[] adblFXFwd = new double[_adblFXBasis.length];

		if (bBasisOnDenom)
			dcBasisAdj = dcDenom;
		else
			dcBasisAdj = dcNum;

		for (int i = 0; i < _adblFXBasis.length; ++i) {
			try {
				org.drip.product.definition.FXForward fxfwd =
					org.drip.product.creator.FXForwardBuilder.CreateFXForward (_cp, new
						org.drip.analytics.date.JulianDate (_dblSpotDate), new
							org.drip.analytics.date.JulianDate (_adblDate[i]));

				if (null == fxfwd) {
					System.out.println ("DerivedFXBasis::getFullFXFwd => Cannot make fxfwd at maturity " +
						org.drip.analytics.date.DateUtil.FromJulian (_adblDate[i]));

					return null;
				}

				if (bBasisOnDenom) {
					if (_bIsFXBasisBootstrapped) {
						if (!(dcBasisAdj instanceof org.drip.analytics.rates.ExplicitBootDiscountCurve))
							return null;

						if (((org.drip.analytics.rates.ExplicitBootDiscountCurve)
							dcBasisAdj).bumpNodeValue (i, _adblFXBasis[i]))
							return null;;
					} else
						dcBasisAdj = (org.drip.analytics.rates.DiscountCurve)
							dcDenom.parallelShiftQuantificationMetric (_adblFXBasis[i]);

					if (null == dcBasisAdj) {
						System.out.println
							("DerivedFXBasis::getFullFXFwd => Cannot create bootstrapped/full denom curve at node "
								+ org.drip.analytics.date.DateUtil.FromJulian (_adblDate[i]));

						return null;
					}

					adblFXFwd[i] = fxfwd.imply (valParam, dcNum, dcBasisAdj, _dblFXSpot, bFwdAsPIP);
				} else {
					if (_bIsFXBasisBootstrapped) {
						if (!(dcBasisAdj instanceof org.drip.analytics.rates.ExplicitBootDiscountCurve))
							return null;

						if (((org.drip.analytics.rates.ExplicitBootDiscountCurve)
							dcBasisAdj).bumpNodeValue (i, _adblFXBasis[i]))
							return null;;
					} else
						dcBasisAdj = (org.drip.analytics.rates.DiscountCurve)
							dcNum.parallelShiftQuantificationMetric (_adblFXBasis[i]);

					if (null == dcBasisAdj) {
						System.out.println
							("DerivedFXBasis::getFullFXFwd => Cannot create bootstrapped/full num curve at node "
								+ org.drip.analytics.date.DateUtil.FromJulian (_adblDate[i]));

						return null;
					}

					adblFXFwd[i] = fxfwd.imply (valParam, dcBasisAdj, dcDenom, _dblFXSpot, bFwdAsPIP);
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return adblFXFwd;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> manifestMeasure (
		final String strInstr)
	{
		return null;
	}

	@Override public boolean setCCIS (
		final org.drip.analytics.input.CurveConstructionInputSet ccis)
	{
		return false;
	}

	@Override public org.drip.product.definition.CalibratableFixedIncomeComponent[] calibComp()
	{
		return null;
	}

	@Override public org.drip.state.identifier.LatentStateLabel label()
	{
		StringBuffer sb = new StringBuffer();

		sb.append ("FXFWDBASIS[" + _cp.code() + "]=");

		for (int i = 0; i < _adblDate.length; ++i) {
			if (0 != i) sb.append (";");

			sb.append (_adblDate[i] + ":" + _adblFXBasis[i]);
		}

		return org.drip.state.identifier.CustomMetricLabel.Standard (sb.toString());
	}

	@Override public org.drip.analytics.definition.Curve parallelShiftQuantificationMetric (
		final double dblShift)
	{
		return null;
	}

	@Override public org.drip.analytics.definition.Curve customTweakQuantificationMetric (
		final org.drip.param.definition.ResponseValueTweakParams rvtp)
	{
		return null;
	}

	@Override public org.drip.analytics.definition.Curve parallelShiftManifestMeasure (
		final String strManifestMeasure,
		final double dblShift)
	{
		double[] adblFXBasisBumped = new double[_adblFXBasis.length];

		for (int i = 0; i < _adblFXBasis.length; ++i)
			adblFXBasisBumped[i] = _adblFXBasis[i] + dblShift;

		try {
			return new DerivedFXBasis (_cp, new org.drip.analytics.date.JulianDate (_dblSpotDate),
				_dblFXSpot, _adblDate, adblFXBasisBumped, _bIsFXBasisBootstrapped);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.definition.Curve shiftManifestMeasure (
		final int iSpanIndex,
		final String strManifestMeasure,
		final double dblShift)
	{
		int iNumBasis = _adblFXBasis.length;

		if (iSpanIndex >= iNumBasis || !org.drip.quant.common.NumberUtil.IsValid (dblShift)) return null;

		double[] adblFXBasisBumped = new double[iNumBasis];

		for (int i = 0; i < iNumBasis; ++i)
			adblFXBasisBumped[i] = _adblFXBasis[i] + (i == iSpanIndex ? dblShift : 0.);

		try {
			return new DerivedFXBasis (_cp, new org.drip.analytics.date.JulianDate (_dblSpotDate),
				_dblFXSpot, _adblDate, adblFXBasisBumped, _bIsFXBasisBootstrapped);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.definition.Curve customTweakManifestMeasure (
		final String strManifestMeasure,
		final org.drip.param.definition.ResponseValueTweakParams mmtp)
	{
		if (null == mmtp) return null;

		double[] adblFXBasisBumped = org.drip.analytics.support.AnalyticsHelper.TweakManifestMeasure
			(_adblFXBasis, mmtp);

		if (null == adblFXBasisBumped || 0 == adblFXBasisBumped.length) return null;

		try {
			return new DerivedFXBasis (_cp, new org.drip.analytics.date.JulianDate (_dblSpotDate),
				_dblFXSpot, _adblDate, adblFXBasisBumped, _bIsFXBasisBootstrapped);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.date.JulianDate epoch()
	{
		try {
			return new org.drip.analytics.date.JulianDate (_dblSpotDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
