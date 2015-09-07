
package org.drip.analytics.input;

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
 * BootCurveConstructionInput contains the Parameters needed for the Curve Calibration/Estimation. It
 * 	contains the following:
 *  - Calibration Valuation Parameters
 *  - Calibration Quoting Parameters
 *  - Array of Calibration Instruments
 *  - Map of Calibration Quotes
 *  - Map of Calibration Measures
 *  - Latent State Fixings Container
 *
 * @author Lakshmi Krishnamurthy
 */

public class BootCurveConstructionInput implements org.drip.analytics.input.CurveConstructionInputSet {
	private org.drip.param.valuation.ValuationParams _valParam = null;
	private org.drip.param.market.LatentStateFixingsContainer _lsfc = null;
	private org.drip.param.valuation.ValuationCustomizationParams _quotingParam = null;
	private org.drip.product.definition.CalibratableFixedIncomeComponent[] _aCalibInst = null;
	private org.drip.analytics.support.CaseInsensitiveTreeMap<String[]> _mapMeasures = null;
	private
		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
			_mapQuote = null;

	/**
	 * Create an Instance of BootCurveConstructionInput from the given Calibration Inputs
	 * 
	 * @param valParam Valuation Parameters
	 * @param quotingParam Quoting Parameters
	 * @param aCalibInst Array of the Calibration Instruments
	 * @param adblCalibQuote Array of the Calibration Quotes
	 * @param astrCalibMeasure Array of the Calibration Measures
	 * @param lsfc Latent State Fixings Container
	 * 
	 * @return Instance of BootCurveConstructionInput
	 */

	public static final BootCurveConstructionInput Create (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParam,
		final org.drip.product.definition.CalibratableFixedIncomeComponent[] aCalibInst,
		final double[] adblCalibQuote,
		final String[] astrCalibMeasure,
		final org.drip.param.market.LatentStateFixingsContainer lsfc)
	{
		if (null == aCalibInst || null == adblCalibQuote || null == astrCalibMeasure) return null;

		int iNumInst = aCalibInst.length;

		if (0 == iNumInst || adblCalibQuote.length != iNumInst || astrCalibMeasure.length != iNumInst)
			return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
			mapQuote = new
				org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>();

		org.drip.analytics.support.CaseInsensitiveTreeMap<String[]> mapMeasures = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<String[]>();

		for (int i = 0; i < iNumInst; ++i) {
			if (null == aCalibInst[i]) return null;

			String strInstrumentCode = aCalibInst[i].primaryCode();

			if (null == strInstrumentCode || strInstrumentCode.isEmpty() || null == astrCalibMeasure[i] ||
				astrCalibMeasure[i].isEmpty() || !org.drip.quant.common.NumberUtil.IsValid
					(adblCalibQuote[i]))
				return null;

			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapCalibManifestMeasureQuote
				= new org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>();

			mapCalibManifestMeasureQuote.put (astrCalibMeasure[i], adblCalibQuote[i]);

			mapMeasures.put (strInstrumentCode, new String[] {astrCalibMeasure[i]});

			mapQuote.put (strInstrumentCode, mapCalibManifestMeasureQuote);

			String[] astrSecCode = aCalibInst[i].secondaryCode();

			if (null != astrSecCode) {
				int iNumSecCode = astrSecCode.length;

				for (int j = 0; j < iNumSecCode; ++j) {
					String strSecCode = astrSecCode[j];

					if (null == strSecCode || strSecCode.isEmpty())
						mapQuote.put (strSecCode, mapCalibManifestMeasureQuote);
				}
			}
		}

		try {
			return new BootCurveConstructionInput (valParam, quotingParam, aCalibInst, mapQuote, mapMeasures,
				lsfc);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * BootCurveConstructionInput constructor
	 * 
	 * @param valParam Valuation Parameter
	 * @param quotingParam Quoting Parameter
	 * @param aCalibInst Array of Calibration Instruments
	 * @param mapQuote Map of the Calibration Instrument Quotes
	 * @param mapMeasures Map containing the Array of the Calibration Instrument Measures
	 * @param lsfc Latent State Fixings Container
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are Invalid
	 */

	public BootCurveConstructionInput (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParam,
		final org.drip.product.definition.CalibratableFixedIncomeComponent[] aCalibInst,
		final
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
				mapQuote,
		final org.drip.analytics.support.CaseInsensitiveTreeMap<String[]> mapMeasures,
		final org.drip.param.market.LatentStateFixingsContainer lsfc)
		throws java.lang.Exception
	{
		if (null == (_valParam = valParam) || null == (_aCalibInst = aCalibInst) || null == (_mapQuote =
			mapQuote) || null == (_mapMeasures = mapMeasures))
			throw new java.lang.Exception ("BootCurveConstructionInput ctr: Invalid Inputs");

		int iNumInst = _aCalibInst.length;

		if (0 == iNumInst || iNumInst > _mapQuote.size() || iNumInst > _mapMeasures.size())
			throw new java.lang.Exception ("BootCurveConstructionInput ctr: Invalid Inputs");

		_lsfc = lsfc;
		_quotingParam = quotingParam;
	}

	@Override public org.drip.param.valuation.ValuationParams valuationParameter()
	{
		return _valParam;
	}

	@Override public org.drip.param.pricer.CreditPricerParams pricerParameter()
	{
		return null;
	}

	@Override public org.drip.param.market.CurveSurfaceQuoteSet marketParameters()
	{
		return null;
	}

	@Override public org.drip.param.valuation.ValuationCustomizationParams quotingParameter()
	{
		return _quotingParam;
	}

	@Override public org.drip.product.definition.CalibratableFixedIncomeComponent[] components()
	{
		return _aCalibInst;
	}

	@Override public
		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
			quoteMap()
	{
		return _mapQuote;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<String[]> measures()
	{
		return _mapMeasures;
	}

	@Override public org.drip.param.market.LatentStateFixingsContainer fixing()
	{
		return _lsfc;
	}
}
