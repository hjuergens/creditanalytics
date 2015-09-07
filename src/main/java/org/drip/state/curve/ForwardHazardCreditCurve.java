
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
 * ForwardHazardCreditCurve manages the Survival Latent State, using the Hazard Rate as the State Response
 *  Representation. It exports the following functionality:
 *  - Boot Methods - Set/Bump Specific Node Quantification Metric, or Set Flat Value
 *  - Boot Calibration - Initialize Run, Compute Calibration Metric
 *  - Compute the survival probability, recovery rate, or the hazard rate from the Hazard Rate Latent State
 *  - Retrieve Array of the Calibration Components
 *  - Retrieve the Curve Construction Input Set
 *  - Synthesize scenario Latent State by parallel shifting/custom tweaking the quantification metric
 *  - Synthesize scenario Latent State by parallel/custom shifting/custom tweaking the manifest measure
 *  - Serialize into and de-serialize out of byte array
 *
 * @author Lakshmi Krishnamurthy
 */

public class ForwardHazardCreditCurve extends org.drip.analytics.definition.ExplicitBootCreditCurve {
	private double[] _adblHazardDate = null;
	private double[] _adblHazardRate = null;
	private double[] _adblRecoveryDate = null;
	private double[] _adblRecoveryRate = null;

	private org.drip.analytics.definition.CreditCurve createFromBaseMMTP (
		final org.drip.param.definition.ResponseValueTweakParams mmtp)
	{
		double[] adblHazardBumped = org.drip.analytics.support.AnalyticsHelper.TweakManifestMeasure
			(_adblHazardRate, mmtp);

		if (null == adblHazardBumped || _adblHazardRate.length != adblHazardBumped.length) return null;

		try {
			return new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency, adblHazardBumped,
				_adblHazardDate, _adblRecoveryRate, _adblRecoveryDate, _dblSpecificDefaultDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a credit curve from hazard rate and recovery rate term structures
	 * 
	 * @param dblStartDate Curve Epoch date
	 * @param label Credit Curve Label
	 * @param strCurrency Currency
	 * @param adblHazardRate Matched array of hazard rates
	 * @param adblHazardDate Matched array of hazard dates
	 * @param adblRecoveryRate Matched array of recovery rates
	 * @param adblRecoveryDate Matched array of recovery dates
	 * @param dblSpecificDefaultDate (Optional) Specific Default Date
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public ForwardHazardCreditCurve (
		final double dblStartDate,
		final org.drip.state.identifier.CreditLabel label,
		final String strCurrency,
		final double adblHazardRate[],
		final double adblHazardDate[],
		final double[] adblRecoveryRate,
		final double[] adblRecoveryDate,
		final double dblSpecificDefaultDate)
		throws java.lang.Exception
	{
		super (dblStartDate, label, strCurrency);

		if (null == adblHazardRate || 0 == adblHazardRate.length || null == adblHazardDate || 0 ==
			adblHazardDate.length || adblHazardRate.length != adblHazardDate.length || null ==
				adblRecoveryRate || 0 == adblRecoveryRate.length || null == adblRecoveryDate || 0 ==
					adblRecoveryDate.length || adblRecoveryRate.length != adblRecoveryDate.length)
			throw new java.lang.Exception ("ForwardHazardCreditCurve ctr: Invalid Params!");

		_dblSpecificDefaultDate = dblSpecificDefaultDate;
		_adblHazardRate = new double[adblHazardRate.length];
		_adblRecoveryRate = new double[adblRecoveryRate.length];
		_adblHazardDate = new double[adblHazardDate.length];
		_adblRecoveryDate = new double[adblRecoveryDate.length];

		for (int i = 0; i < adblHazardRate.length; ++i)
			_adblHazardRate[i] = adblHazardRate[i];

		for (int i = 0; i < _adblHazardDate.length; ++i)
			_adblHazardDate[i] = adblHazardDate[i];

		for (int i = 0; i < adblRecoveryRate.length; ++i)
			_adblRecoveryRate[i] = adblRecoveryRate[i];

		for (int i = 0; i < adblRecoveryDate.length; ++i)
			_adblRecoveryDate[i] = adblRecoveryDate[i];
	}

	@Override public org.drip.param.valuation.CollateralizationParams collateralParams()
	{
		return null;
	}

	@Override public double survival (
		final double dblDate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDate))
			throw new java.lang.Exception ("ForwardHazardCreditCurve::survival => No surv for NaN date");

		if (dblDate <= _dblEpochDate) return 1.;

		if (org.drip.quant.common.NumberUtil.IsValid (_dblSpecificDefaultDate) && dblDate >=
			_dblSpecificDefaultDate)
			return 0.;

		int i = 0;
		double dblExpArg = 0.;
		double dblStartDate = _dblEpochDate;

		while (i < _adblHazardRate.length && dblDate > _adblHazardDate[i]) {
			dblExpArg -= _adblHazardRate[i] * (_adblHazardDate[i] - dblStartDate);
			dblStartDate = _adblHazardDate[i++];
		}

		if (i >= _adblHazardRate.length) i = _adblHazardRate.length - 1;

		dblExpArg -= _adblHazardRate[i] * (dblDate - dblStartDate);

		return java.lang.Math.exp (dblExpArg / 365.25);
	}

	@Override public double recovery (
		final double dblDate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDate))
			throw new java.lang.Exception ("ForwardHazardCreditCurve::recovery => NaN date");

		for (int i = 0; i < _adblRecoveryDate.length; ++i) {
			if (dblDate < _adblRecoveryDate[i]) return _adblRecoveryRate[i];
		}

		return _adblRecoveryRate[_adblRecoveryDate.length - 1];
	}

	@Override public ForwardHazardCreditCurve parallelShiftQuantificationMetric (
		final double dblShift)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblShift)) return null;

		double[] adblHazard = new double[_adblHazardRate.length];

		for (int i = 0; i < _adblHazardRate.length; ++i)
			adblHazard[i] = _adblHazardRate[i] + dblShift;

		try {
			return new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency, adblHazard,
				_adblHazardDate, _adblRecoveryRate, _adblRecoveryDate, _dblSpecificDefaultDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.definition.Curve customTweakQuantificationMetric (
		final org.drip.param.definition.ResponseValueTweakParams rvtp)
	{
		return null;
	}

	@Override public ForwardHazardCreditCurve parallelShiftManifestMeasure (
		final String strManifestMeasure,
		final double dblShift)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblShift)) return null;

		if (null == _valParam || null == _aCalibInst || 0 == _aCalibInst.length || null == _adblCalibQuote ||
			0 == _adblCalibQuote.length || null == _astrCalibMeasure || 0 == _astrCalibMeasure.length ||
				_astrCalibMeasure.length != _adblCalibQuote.length || _adblCalibQuote.length !=
					_aCalibInst.length)
			return parallelShiftQuantificationMetric (dblShift);

		ForwardHazardCreditCurve cc = null;
		double[] adblCalibQuote = new double[_adblCalibQuote.length];

		org.drip.state.estimator.NonlinearCurveCalibrator calibrator = new
			org.drip.state.estimator.NonlinearCurveCalibrator();

		try {
			cc = new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency, _adblHazardRate,
				_adblHazardDate, _adblRecoveryRate, _adblRecoveryDate, _dblSpecificDefaultDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0; i < _adblCalibQuote.length; ++i) {
			try {
				calibrator.bootstrapHazardRate (cc, _aCalibInst[i], i, _valParam, _dc, _dcTSY,
					_pricerParam, _astrCalibMeasure[i], adblCalibQuote[i] = _adblCalibQuote[i] + dblShift,
						_lsfc, _quotingParams, _bFlat);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		cc.setInstrCalibInputs (_valParam, _bFlat, _dc, _dcTSY, _pricerParam, _aCalibInst, adblCalibQuote,
			_astrCalibMeasure, _lsfc, _quotingParams);

		return cc;
	}

	@Override public ForwardHazardCreditCurve shiftManifestMeasure (
		final int iSpanIndex,
		final String strManifestMeasure,
		final double dblShift)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblShift)) return null;

		if (null == _valParam || null == _aCalibInst || 0 == _aCalibInst.length || null == _adblCalibQuote ||
			0 == _adblCalibQuote.length || null == _astrCalibMeasure || 0 == _astrCalibMeasure.length ||
				_astrCalibMeasure.length != _adblCalibQuote.length || _adblCalibQuote.length !=
					_aCalibInst.length)
			return parallelShiftQuantificationMetric (dblShift);

		ForwardHazardCreditCurve cc = null;
		double[] adblCalibQuote = new double[_adblCalibQuote.length];

		if (iSpanIndex >= _adblCalibQuote.length) return null;

		org.drip.state.estimator.NonlinearCurveCalibrator calibrator = new
			org.drip.state.estimator.NonlinearCurveCalibrator();

		try {
			cc = new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency, _adblHazardRate,
				_adblHazardDate, _adblRecoveryRate, _adblRecoveryDate, _dblSpecificDefaultDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0; i < _adblCalibQuote.length; ++i) {
			try {
				calibrator.bootstrapHazardRate (cc, _aCalibInst[i], i, _valParam, _dc, _dcTSY, _pricerParam,
					_astrCalibMeasure[i], adblCalibQuote[i] = _adblCalibQuote[i] + (i == iSpanIndex ?
						dblShift : 0.), _lsfc, _quotingParams, _bFlat);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		cc.setInstrCalibInputs (_valParam, _bFlat, _dc, _dcTSY, _pricerParam, _aCalibInst, adblCalibQuote,
			_astrCalibMeasure, _lsfc, _quotingParams);

		return cc;
	}

	@Override public org.drip.analytics.definition.CreditCurve flatCurve (
		final double dblFlatNodeValue,
		final boolean bSingleNode,
		final double dblRecovery)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblFlatNodeValue) || 0. >= dblFlatNodeValue || null ==
			_valParam || null == _aCalibInst || 0 == _aCalibInst.length || null == _adblCalibQuote || 0 ==
				_adblCalibQuote.length || null == _astrCalibMeasure || 0 == _astrCalibMeasure.length ||
					_astrCalibMeasure.length != _adblCalibQuote.length || _adblCalibQuote.length !=
						_aCalibInst.length)
			return null;

		org.drip.analytics.definition.ExplicitBootCreditCurve cc = null;

		org.drip.state.estimator.NonlinearCurveCalibrator calibrator = new
			org.drip.state.estimator.NonlinearCurveCalibrator();

		try {
			if (bSingleNode)
				cc = org.drip.state.creator.CreditCurveBuilder.FromHazardNode (_dblEpochDate,
					_label.fullyQualifiedName(), _strCurrency, _adblHazardRate[0], _adblHazardDate[0],
						!org.drip.quant.common.NumberUtil.IsValid (dblRecovery) ? _adblRecoveryRate[0] :
							dblRecovery);
			else
				cc = new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency, _adblHazardRate,
					_adblHazardDate, _adblRecoveryRate, _adblRecoveryDate, _dblSpecificDefaultDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0; i < _adblCalibQuote.length; ++i) {
			try {
				calibrator.bootstrapHazardRate (cc, _aCalibInst[i], i, _valParam, _dc, _dcTSY, _pricerParam,
					_astrCalibMeasure[i], dblFlatNodeValue, _lsfc, _quotingParams, true);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		if (bSingleNode)
			cc.setInstrCalibInputs (_valParam, true, _dc, _dcTSY, _pricerParam, new
				org.drip.product.definition.CalibratableFixedIncomeComponent[] {_aCalibInst[0]}, new double[]
					{dblFlatNodeValue}, _astrCalibMeasure, _lsfc, _quotingParams);
		else {
			double[] adblCalibValue = new double[_adblCalibQuote.length];

			for (int i = 0; i < _adblCalibQuote.length; ++i)
				adblCalibValue[i] = dblFlatNodeValue;

			cc.setInstrCalibInputs (_valParam, true, _dc, _dcTSY, _pricerParam, _aCalibInst, adblCalibValue,
				_astrCalibMeasure, _lsfc, _quotingParams);
		}

		return cc;
	}

	@Override  public org.drip.analytics.definition.CreditCurve customTweakManifestMeasure (
		final String strManifestMeasure,
		final org.drip.param.definition.ResponseValueTweakParams mmtp)
	{
		if (null == mmtp) return null;

		if (!(mmtp instanceof org.drip.param.definition.CreditManifestMeasureTweak))
			return createFromBaseMMTP (mmtp);

		org.drip.param.definition.CreditManifestMeasureTweak cmmt =
			(org.drip.param.definition.CreditManifestMeasureTweak) mmtp;

		if (org.drip.param.definition.CreditManifestMeasureTweak.CREDIT_TWEAK_NODE_PARAM_RECOVERY.equalsIgnoreCase
			(cmmt.tweakParamType())) {
			double[] adblRecoveryRateBumped = null;

			if (null == (adblRecoveryRateBumped =
				org.drip.analytics.support.AnalyticsHelper.TweakManifestMeasure (_adblRecoveryRate, cmmt)) ||
					adblRecoveryRateBumped.length != _adblRecoveryRate.length)
				return null;

			try {
				return new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency, _adblHazardRate,
					_adblHazardDate, adblRecoveryRateBumped, _adblRecoveryDate, _dblSpecificDefaultDate);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		} else if
			(org.drip.param.definition.CreditManifestMeasureTweak.CREDIT_TWEAK_NODE_PARAM_QUOTE.equalsIgnoreCase
				(cmmt.tweakParamType())) {
			if (org.drip.param.definition.CreditManifestMeasureTweak.CREDIT_TWEAK_NODE_MEASURE_HAZARD.equalsIgnoreCase
				(cmmt.tweakMeasureType())) {
				double[] adblHazardBumped = null;

				if (null == (adblHazardBumped =
					org.drip.analytics.support.AnalyticsHelper.TweakManifestMeasure (_adblHazardRate, cmmt))
						|| adblHazardBumped.length != _adblHazardRate.length)
					return null;

				try {
					return new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency,
						adblHazardBumped, _adblHazardDate, _adblRecoveryRate, _adblRecoveryDate,
							_dblSpecificDefaultDate);
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}
			} else if
				(org.drip.param.definition.CreditManifestMeasureTweak.CREDIT_TWEAK_NODE_MEASURE_QUOTE.equalsIgnoreCase
					(cmmt.tweakMeasureType())) {
				double[] adblQuoteBumped = null;

				if (null == (adblQuoteBumped =
					org.drip.analytics.support.AnalyticsHelper.TweakManifestMeasure (_adblHazardRate, cmmt))
						|| adblQuoteBumped.length != _adblHazardRate.length)
					return null;

				org.drip.analytics.definition.ExplicitBootCreditCurve cc = null;

				org.drip.state.estimator.NonlinearCurveCalibrator calibrator = new
					org.drip.state.estimator.NonlinearCurveCalibrator();

				try {
					if (cmmt.singleNodeCalib())
						cc = org.drip.state.creator.CreditCurveBuilder.FromHazardNode (_dblEpochDate,
							_strCurrency, _label.fullyQualifiedName(), _adblHazardRate[0],
								_adblHazardDate[0], _adblRecoveryRate[0]);
					else
						cc = new ForwardHazardCreditCurve (_dblEpochDate, _label, _strCurrency,
							_adblHazardRate, _adblHazardDate, _adblRecoveryRate, _adblRecoveryDate,
								_dblSpecificDefaultDate);
				} catch (java.lang.Exception e) {
					e.printStackTrace();

					return null;
				}

				for (int i = 0; i < adblQuoteBumped.length; ++i) {
					try {
						calibrator.bootstrapHazardRate (cc, _aCalibInst[i], i, _valParam, _dc, _dcTSY,
							_pricerParam, _astrCalibMeasure[i], adblQuoteBumped[i], _lsfc, _quotingParams,
								_bFlat);
					} catch (java.lang.Exception e) {
						e.printStackTrace();

						return null;
					}
				}

				cc.setInstrCalibInputs (_valParam, _bFlat, _dc, _dcTSY, _pricerParam, _aCalibInst,
					adblQuoteBumped, _astrCalibMeasure, _lsfc, _quotingParams);

				return cc;
			}
		}

		return null;
	}

	@Override public boolean setNodeValue (
		final int iNodeIndex,
		final double dblValue)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValue) || iNodeIndex > _adblHazardRate.length)
			return false;

		for (int i = iNodeIndex; i < _adblHazardRate.length; ++i)
			_adblHazardRate[i] = dblValue;

		return true;
	}

	@Override public boolean bumpNodeValue (
		final int iNodeIndex,
		final double dblValue)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValue) || iNodeIndex > _adblHazardRate.length)
			return false;

		for (int i = iNodeIndex; i < _adblHazardRate.length; ++i)
			_adblHazardRate[i] += dblValue;

		return true;
	}

	@Override public boolean setFlatValue (
		final double dblValue)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValue)) return false;

		for (int i = 0; i < _adblHazardRate.length; ++i)
			_adblHazardRate[i] = dblValue;

		return true;
	}
}
