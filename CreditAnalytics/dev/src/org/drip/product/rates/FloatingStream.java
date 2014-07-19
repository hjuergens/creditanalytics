
package org.drip.product.rates;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
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
 * FloatingStream contains an implementation of the Floating leg cash flow stream. It exports the following
 *  functionality:
 *  - Standard/Custom Constructor for the FloatingStream Component
 *  - Dates: Effective, Maturity, Coupon dates and Product settlement Parameters
 *  - Coupon/Notional Outstanding as well as schedules
 *  - Market Parameters: Discount, Forward, Credit, Treasury Curves
 *  - Cash Flow Periods: Coupon flows and (Optionally) Loss Flows
 *  - Valuation: Named Measure Generation
 *  - Calibration: The codes and constraints generation
 *  - Jacobians: Quote/DF and PV/DF micro-Jacobian generation
 *  - Serialization into and de-serialization out of byte arrays
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FloatingStream extends org.drip.product.definition.RatesComponent {
	private static final boolean s_bBlog = false;

	private double _dblNotional = 1.;
	private double _dblSpread = 0.0001;
	private boolean _bIsReference = true;
	private java.lang.String _strCode = "";
	private java.lang.String _strCurrency = "";
	private double _dblMaturity = java.lang.Double.NaN;
	private double _dblEffective = java.lang.Double.NaN;
	private org.drip.product.params.FXMTMSetting _fxmtm = null;
	private org.drip.product.params.FloatingRateIndex _fri = null;
	private org.drip.product.params.FactorSchedule _notlSchedule = null;
	private org.drip.param.valuation.CashSettleParams _settleParams = null;
	private java.util.List<org.drip.analytics.period.CashflowPeriod> _lsCouponPeriod = null;

	protected double getFixing (
		final double dblValueDate,
		final org.drip.product.params.FloatingRateIndex fri,
		final org.drip.analytics.period.CashflowPeriod currentPeriod,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs)
		throws java.lang.Exception
	{
		java.util.Map<org.drip.analytics.date.JulianDate,
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> mapFixings =
				csqs.fixings();

		if (null != mapFixings) {
			double dblCurrentResetDate = currentPeriod.getResetDate();

			if (org.drip.quant.common.NumberUtil.IsValid (dblCurrentResetDate)) {
				org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapIndexFixing =
					mapFixings.get (new org.drip.analytics.date.JulianDate (dblCurrentResetDate));

				if (null != mapIndexFixing) {
					java.lang.Double dblFixing = mapIndexFixing.get (fri.fullyQualifiedName());

					if (null != dblFixing && org.drip.quant.common.NumberUtil.IsValid (dblFixing))
						return dblFixing + _dblSpread;
				}
			}
		}

		throw new java.lang.Exception ("FloatingStream::getRegularFixing => Cannot get Fixing");
	}

	@Override protected org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> calibMeasures (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		return null;
	}

	/**
	 * FloatingStream constructor
	 * 
	 * @param strCurrency Cash Flow Currency
	 * @param fxmtm FX MTM setting
	 * @param dblSpread Spread
	 * @param dblNotional Initial Notional Amount
	 * @param notlSchedule Notional Schedule
	 * @param lsCouponPeriod List of the Coupon Periods
	 * @param fri Floating Rate Index
	 * @param bIsReference Is this the Reference Leg in a Float-Float Swap?
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public FloatingStream (
		final java.lang.String strCurrency,
		final org.drip.product.params.FXMTMSetting fxmtm,
		final double dblSpread,
		final double dblNotional,
		final org.drip.product.params.FactorSchedule notlSchedule,
		final java.util.List<org.drip.analytics.period.CashflowPeriod> lsCouponPeriod,
		final org.drip.product.params.FloatingRateIndex fri,
		final boolean bIsReference)
		throws java.lang.Exception
	{
		if (null == (_strCurrency = strCurrency) || _strCurrency.isEmpty() ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblSpread = dblSpread) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblNotional = dblNotional) || 0. == _dblNotional
					|| null == (_lsCouponPeriod = lsCouponPeriod) || null == (_fri = fri))
			throw new java.lang.Exception ("FloatingStream ctr => Invalid Input params!");

		int iNumPeriod = _lsCouponPeriod.size();

		if (0 == iNumPeriod) throw new java.lang.Exception ("FloatingStream ctr => Invalid Input params!");

		_fxmtm = fxmtm;
		_bIsReference = bIsReference;

		if (null == (_notlSchedule = notlSchedule))
			_notlSchedule = org.drip.product.params.FactorSchedule.CreateBulletSchedule();

		_dblEffective = _lsCouponPeriod.get (0).getStartDate();

		_dblMaturity = _lsCouponPeriod.get (iNumPeriod - 1).getEndDate();
	}

	/**
	 * FloatingStream de-serialization from input byte array
	 * 
	 * @param ab Byte Array
	 * 
	 * @throws java.lang.Exception Thrown if FloatingStream cannot be properly de-serialized
	 */

	public FloatingStream (
		final byte[] ab)
		throws java.lang.Exception
	{
		if (null == ab || 0 == ab.length)
			throw new java.lang.Exception ("FloatingStream de-serializer: Invalid input Byte array");

		java.lang.String strRawString = new java.lang.String (ab);

		if (null == strRawString || strRawString.isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Empty state");

		java.lang.String strSerializedFloatingStream = strRawString.substring (0, strRawString.indexOf
			(objectTrailer()));

		if (null == strSerializedFloatingStream || strSerializedFloatingStream.isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate state");

		java.lang.String[] astrField = org.drip.quant.common.StringUtil.Split (strSerializedFloatingStream,
			fieldDelimiter());

		if (null == astrField || 12 > astrField.length)
			throw new java.lang.Exception ("FloatingStream de-serializer: Invalid reqd field set");

		// double dblVersion = new java.lang.Double (astrField[0]);

		if (null == astrField[1] || astrField[1].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[1]))
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate notional");

		_dblNotional = new java.lang.Double (astrField[1]);

		if (null == astrField[2] || astrField[2].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[2]))
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate coupon");

		_dblSpread = new java.lang.Double (astrField[2]);

		if (null == astrField[3] || astrField[3].isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate IR curve name");

		if (!org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[3]))
			_strCurrency = astrField[3];
		else
			_strCurrency = "";

		if (null == astrField[4] || astrField[4].isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate code");

		if (!org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[4]))
			_strCode = astrField[4];
		else
			_strCode = "";

		if (null == astrField[5] || astrField[5].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[5]))
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate maturity date");

		_dblMaturity = new java.lang.Double (astrField[5]);

		if (null == astrField[6] || astrField[6].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[6]))
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate effective date");

		_dblEffective = new java.lang.Double (astrField[6]);

		if (null == astrField[7] || astrField[7].isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate rate index");

		if (org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[7]))
			_fri = null;
		else
			_fri = new org.drip.product.params.FloatingRateIndex (astrField[7].getBytes());

		if (null == astrField[8] || astrField[8].isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate notional schedule");

		if (org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[8]))
			_notlSchedule = null;
		else
			_notlSchedule = new org.drip.product.params.FactorSchedule (astrField[8].getBytes());

		if (null == astrField[9] || astrField[9].isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate cash settle params");

		if (org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[9]))
			_settleParams = null;
		else
			_settleParams = new org.drip.param.valuation.CashSettleParams (astrField[9].getBytes());

		if (null == astrField[10] || astrField[10].isEmpty())
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate the periods");

		if (org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[10]))
			_lsCouponPeriod = null;
		else {
			java.lang.String[] astrRecord = org.drip.quant.common.StringUtil.Split (astrField[10],
				collectionRecordDelimiter());

			if (null != astrRecord && 0 != astrRecord.length) {
				for (int i = 0; i < astrRecord.length; ++i) {
					if (null == astrRecord[i] || astrRecord[i].isEmpty() ||
						org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrRecord[i]))
						continue;

					if (null == _lsCouponPeriod)
						_lsCouponPeriod = new java.util.ArrayList<org.drip.analytics.period.CashflowPeriod>();

					_lsCouponPeriod.add (new org.drip.analytics.period.CashflowPeriod
						(astrRecord[i].getBytes()));
				}
			}
		}

		if (null == astrField[11] || astrField[11].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[11]))
			throw new java.lang.Exception ("FloatingStream de-serializer: Cannot locate the reference flag");

		_bIsReference = new java.lang.Boolean (astrField[11]);
	}

	@Override public java.lang.String primaryCode()
	{
		return _strCode;
	}

	@Override public void setPrimaryCode (
		final java.lang.String strCode)
	{
		_strCode = strCode;
	}

	@Override public java.lang.String name()
	{
		return _strCode;
	}

	@Override public java.util.Set<java.lang.String> cashflowCurrencySet()
	{
		java.util.Set<java.lang.String> setCcy = new java.util.HashSet<java.lang.String>();

		setCcy.add (_strCurrency);

		return setCcy;
	}

	@Override public java.lang.String[] couponCurrency()
	{
		return new java.lang.String[] {_strCurrency};
	}

	@Override public java.lang.String[] principalCurrency()
	{
		return new java.lang.String[] {_strCurrency};
	}

	@Override public double initialNotional()
	{
		return _dblNotional;
	}

	@Override public double notional (
		final double dblDate)
		throws java.lang.Exception
	{
		if (null == _notlSchedule || !org.drip.quant.common.NumberUtil.IsValid (dblDate))
			throw new java.lang.Exception ("FloatingStream::notional => Bad date into getNotional");

		return _notlSchedule.getFactor (dblDate);
	}

	@Override public double notional (
		final double dblDate1,
		final double dblDate2)
		throws java.lang.Exception
	{
		if (null == _notlSchedule || !org.drip.quant.common.NumberUtil.IsValid (dblDate1) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblDate2))
			throw new java.lang.Exception ("FloatingStream::notional => Bad date into getNotional");

		return _notlSchedule.getFactor (dblDate1, dblDate2);
	}

	@Override public double coupon (
		final double dblValueDate,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValueDate) || null == csqs)
			throw new java.lang.Exception ("FloatingStream::coupon => Invalid Inputs");

		org.drip.analytics.period.CashflowPeriod currentPeriod = null;

		if (dblValueDate <= _dblEffective)
			currentPeriod = _lsCouponPeriod.get (0);
		else {
			for (org.drip.analytics.period.CashflowPeriod period : _lsCouponPeriod) {
				if (null == period) continue;

				if (dblValueDate >= period.getStartDate() && dblValueDate <= period.getEndDate()) {
					currentPeriod = period;
					break;
				}
			}
		}

		if (null == currentPeriod)
			throw new java.lang.Exception ("FloatingStream::coupon => Invalid Inputs");

		try {
			return getFixing (dblValueDate, _fri, currentPeriod, csqs);
		} catch (java.lang.Exception e) {
		}

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (couponCurrency()[0]);

		if (null == dcFunding)
			throw new java.lang.Exception ("FloatingStream::getCoupon => cant determine index");

		double dblStartDate = currentPeriod.getStartDate();

		double dblEndDate = currentPeriod.getEndDate();

		double dblEpochDate = dcFunding.epoch().julian();

		if (dblEpochDate > dblStartDate)
			dblEndDate = new org.drip.analytics.date.JulianDate (dblStartDate = dblEpochDate).addTenor
				(_fri.tenor()).julian();

		return dcFunding.libor (dblStartDate, dblEndDate, currentPeriod.getCouponDCF()) + _dblSpread;
	}

	@Override public int freq()
	{
		return cashFlowPeriod().get (0).freq();
	}

	@Override public java.lang.String[] forwardCurveName()
	{
		return new java.lang.String[] {_fri.fullyQualifiedName()};
	}

	@Override public java.lang.String[] creditCurveName()
	{
		return null;
	}

	@Override public java.lang.String[] currencyPairCode()
	{
		if (null == _fxmtm) return null;

		org.drip.product.params.CurrencyPair cp = _fxmtm.currencyPair();

		return null == cp ? null : new java.lang.String[] {cp.code()};
	}

	@Override public org.drip.analytics.date.JulianDate effective()
	{
		try {
			return new org.drip.analytics.date.JulianDate (_dblEffective);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.date.JulianDate maturity()
	{
		try {
			return new org.drip.analytics.date.JulianDate (_dblMaturity);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.date.JulianDate firstCouponDate()
	{
		try {
			return new org.drip.analytics.date.JulianDate (_lsCouponPeriod.get (0).getEndDate());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public java.util.List<org.drip.analytics.period.CashflowPeriod> cashFlowPeriod()
	{
		return _lsCouponPeriod;
	}

	@Override public org.drip.param.valuation.CashSettleParams cashSettleParams()
	{
		return _settleParams;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> value (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || null == csqs) return null;

		java.lang.String strCurrency = couponCurrency()[0];

		org.drip.product.params.CurrencyPair cp = null == _fxmtm ? null : _fxmtm.currencyPair();

		org.drip.quant.function1D.AbstractUnivariate auFX = csqs.fxCurve (cp);

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (strCurrency);

		if (null == dcFunding) return null;

		long lStart = System.nanoTime();

		double dblFixing01 = 0.;
		double dblAccrued01 = 0.;
		boolean bFirstPeriod = true;
		double dblUnadjustedDirtyPV = 0.;
		double dblUnadjustedDirtyDV01 = 0.;
		double dblQuantoAdjustedDirtyPV = 0.;
		double dblQuantoAdjustedDirtyDV01 = 0.;
		double dblAdjustedNotional = _dblNotional;
		double dblCashPayDF = java.lang.Double.NaN;
		double dblResetDate = java.lang.Double.NaN;
		double dblResetRate = java.lang.Double.NaN;
		double dblValueNotional = java.lang.Double.NaN;

		double dblValueDate = valParams.valueDate();

		try {
			dblAdjustedNotional *= (null != auFX && null != _fxmtm && !_fxmtm.mtmMode() ? auFX.evaluate
				(dblValueDate) : 1.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		java.lang.String strFRI = _fri.fullyQualifiedName();

		org.drip.analytics.rates.ForwardRateEstimator fc = csqs.forwardCurve (_fri);

		for (org.drip.analytics.period.CashflowPeriod period : _lsCouponPeriod) {
			double dblFloatingRate = 0.;
			double dblPeriodQuantoAdjustment = 1.;
			double dblUnadjustedDirtyPeriodDV01 = java.lang.Double.NaN;

			double dblPeriodAcrualStartDate = period.getAccrualStartDate();

			double dblPeriodResetDate = period.getResetDate();

			double dblPeriodStartDate = period.getStartDate();

			double dblPeriodEndDate = period.getEndDate();

			double dblPeriodPayDate = period.getPayDate();

			double dblPeriodDCF = period.getCouponDCF();

			if (dblPeriodPayDate < dblValueDate) continue;

			try {
				if (bFirstPeriod) {
					bFirstPeriod = false;
					dblResetDate = dblPeriodResetDate;

					dblResetRate = dblFloatingRate = null == fc ? coupon (dblValueDate, csqs) : fc.forward
						(dblPeriodPayDate);

					dblFixing01 = period.getAccrualDCF (dblValueDate) * 0.0001 * notional
						(dblPeriodAcrualStartDate, dblValueDate) * (null != auFX && null != _fxmtm &&
							_fxmtm.mtmMode() ? auFX.evaluate (dblValueDate) : 1.);

					if (dblPeriodStartDate < dblValueDate) dblAccrued01 = dblFixing01;
				} else {
					dblPeriodQuantoAdjustment = null != pricerParams &&
						pricerParams._bAmetranoBianchettiForwardQuanto ?
							org.drip.analytics.support.OptionHelper.MultiplicativeCrossVolQuanto (csqs,
								strFRI, "ForwardToDomesticExchangeVolatility",
									"FRIForwardToDomesticExchangeCorrelation", dblValueDate,
										dblPeriodStartDate) : java.lang.Math.exp
											(org.drip.analytics.support.OptionHelper.IntegratedCrossVolQuanto
												(csqs.fundingCurveVolSurface (strCurrency),
													csqs.forwardCurveVolSurface (_fri),
														csqs.forwardFundingCorrSurface (_fri, strCurrency),
															dblValueDate, dblPeriodStartDate));

					if (null != _fxmtm && _fxmtm.mtmMode())
						dblPeriodQuantoAdjustment *= java.lang.Math.exp
							(org.drip.analytics.support.OptionHelper.IntegratedCrossVolQuanto
								(csqs.fundingCurveVolSurface (strCurrency), csqs.fxCurveVolSurface (cp),
									csqs.fundingFXCorrSurface (strCurrency, cp), dblValueDate,
										dblPeriodPayDate) +
											org.drip.analytics.support.OptionHelper.IntegratedCrossVolQuanto
												(csqs.forwardCurveVolSurface (_fri), csqs.fxCurveVolSurface
													(cp), csqs.forwardFXCorrSurface (_fri, cp), dblValueDate,
														dblPeriodStartDate));

					dblFloatingRate = (null == fc ? dcFunding.libor (dblPeriodStartDate, dblPeriodPayDate,
						dblPeriodDCF) : fc.forward (dblPeriodPayDate));
				}

				dblUnadjustedDirtyPeriodDV01 = 0.0001 * dblPeriodDCF * dcFunding.df (dblPeriodPayDate) *
					notional (dblPeriodAcrualStartDate, dblPeriodEndDate) * (null != auFX && null != _fxmtm
						&& _fxmtm.mtmMode() ? auFX.evaluate (dblPeriodPayDate) : 1.);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			if (s_bBlog) {
				try {
					System.out.println (new org.drip.analytics.date.JulianDate (dblPeriodResetDate) + " [" +
						new org.drip.analytics.date.JulianDate (dblPeriodStartDate) + "->" + new
							org.drip.analytics.date.JulianDate (dblPeriodEndDate) + "] => " +
								org.drip.quant.common.FormatUtil.FormatDouble (dblFloatingRate, 1, 4, 100.));
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}
			}

			dblUnadjustedDirtyDV01 += dblUnadjustedDirtyPeriodDV01;
			dblUnadjustedDirtyPV += dblUnadjustedDirtyPeriodDV01 * 10000. * (dblFloatingRate + _dblSpread);
			double dblQuantoAdjustedDirtyPeriodDV01 = dblUnadjustedDirtyPeriodDV01 *
				dblPeriodQuantoAdjustment;
			dblQuantoAdjustedDirtyDV01 += dblQuantoAdjustedDirtyPeriodDV01;
			dblQuantoAdjustedDirtyPV += dblQuantoAdjustedDirtyPeriodDV01 * 10000. * (dblFloatingRate +
				_dblSpread);
		}

		try {
			double dblCashSettle = valParams.cashPayDate();

			if (null != _settleParams) dblCashSettle = _settleParams.cashSettleDate (dblValueDate);

			dblCashPayDF = dcFunding.df (dblCashSettle);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		dblAccrued01 *= dblAdjustedNotional;
		dblUnadjustedDirtyPV *= (dblAdjustedNotional / dblCashPayDF);
		dblUnadjustedDirtyDV01 *= (dblAdjustedNotional / dblCashPayDF);
		dblQuantoAdjustedDirtyPV *= (dblAdjustedNotional / dblCashPayDF);
		dblQuantoAdjustedDirtyDV01 *= (dblAdjustedNotional / dblCashPayDF);
		double dblAccrued = dblAccrued01 * 10000. * (dblResetRate + _dblSpread);
		double dblUnadjustedCleanPV = dblUnadjustedDirtyPV - dblAccrued;
		double dblUnadjustedCleanDV01 = dblUnadjustedDirtyDV01 - dblAccrued01;
		double dblQuantoAdjustedCleanPV = dblQuantoAdjustedDirtyPV - dblAccrued;
		double dblQuantoAdjustedCleanDV01 = dblQuantoAdjustedDirtyDV01 - dblAccrued01;
		double dblUnadjustedFairPremium = 0.0001 * dblUnadjustedCleanPV / dblUnadjustedCleanDV01;
		double dblQuantoAdjustedFairPremium = 0.0001 * dblQuantoAdjustedCleanPV / dblQuantoAdjustedCleanDV01;

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapResult = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>();

		mapResult.put ("Accrued", dblAccrued);

		mapResult.put ("Accrued01", dblAccrued01);

		mapResult.put ("CleanDV01", dblQuantoAdjustedCleanDV01);

		mapResult.put ("CleanPV", dblQuantoAdjustedCleanPV);

		mapResult.put ("CV01", dblQuantoAdjustedCleanDV01);

		mapResult.put ("DirtyDV01", dblQuantoAdjustedDirtyDV01);

		mapResult.put ("DirtyPV", dblQuantoAdjustedDirtyPV);

		mapResult.put ("DV01", dblQuantoAdjustedCleanDV01);

		mapResult.put ("FairPremium", dblQuantoAdjustedFairPremium);

		mapResult.put ("Fixing01", dblFixing01 * dblAdjustedNotional / dblCashPayDF);

		mapResult.put ("ParRate", dblQuantoAdjustedFairPremium);

		mapResult.put ("PV", dblQuantoAdjustedCleanPV);

		mapResult.put ("QuantoAdjustedCleanPV", dblQuantoAdjustedCleanPV);

		mapResult.put ("QuantoAdjustedCleanDV01", dblQuantoAdjustedCleanDV01);

		mapResult.put ("QuantoAdjustedDirtyDV01", dblQuantoAdjustedDirtyDV01);

		mapResult.put ("QuantoAdjustedDirtyPV", dblQuantoAdjustedDirtyPV);

		mapResult.put ("QuantoAdjustedFairPremium", dblQuantoAdjustedFairPremium);

		mapResult.put ("QuantoAdjustedParRate", dblQuantoAdjustedFairPremium);

		mapResult.put ("QuantoAdjustedPV", dblQuantoAdjustedCleanPV);

		mapResult.put ("QuantoAdjustedRate", dblQuantoAdjustedFairPremium);

		mapResult.put ("QuantoAdjustedUpfront", dblQuantoAdjustedCleanPV);

		mapResult.put ("QuantoAdjustmentFactor", dblQuantoAdjustedDirtyDV01 / dblUnadjustedDirtyDV01);

		mapResult.put ("QuantoAdjustmentPremium", (dblQuantoAdjustedCleanPV - dblUnadjustedCleanPV) /
			dblAdjustedNotional);

		mapResult.put ("Rate", dblQuantoAdjustedFairPremium);

		mapResult.put ("ResetDate", dblResetDate);

		mapResult.put ("ResetRate", dblResetRate);

		mapResult.put ("UnadjustedCleanPV", dblUnadjustedCleanPV);

		mapResult.put ("UnadjustedCleanDV01", dblUnadjustedCleanDV01);

		mapResult.put ("UnadjustedDirtyDV01", dblUnadjustedDirtyDV01);

		mapResult.put ("UnadjustedDirtyPV", dblUnadjustedDirtyPV);

		mapResult.put ("UnadjustedFairPremium", dblUnadjustedFairPremium);

		mapResult.put ("UnadjustedParRate", dblUnadjustedFairPremium);

		mapResult.put ("UnadjustedPV", dblUnadjustedCleanPV);

		mapResult.put ("UnadjustedRate", dblUnadjustedFairPremium);

		mapResult.put ("UnadjustedUpfront", dblUnadjustedCleanPV);

		mapResult.put ("Upfront", dblQuantoAdjustedCleanPV);

		try {
			dblValueNotional = notional (dblValueDate) * (null != auFX && null != _fxmtm && !_fxmtm.mtmMode()
				? auFX.evaluate (dblValueDate) : 1.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		if (org.drip.quant.common.NumberUtil.IsValid (dblValueNotional)) {
			double dblUnadjustedPrice = 100. * (1. + (dblUnadjustedCleanPV / dblAdjustedNotional /
				dblValueNotional));

			double dblQuantoAdjustedPrice = 100. * (1. + (dblQuantoAdjustedCleanPV / dblAdjustedNotional /
				dblValueNotional));

			mapResult.put ("CleanPrice", dblQuantoAdjustedPrice);

			mapResult.put ("DirtyPrice", 100. * (1. + (dblQuantoAdjustedDirtyPV / dblAdjustedNotional /
				dblValueNotional)));

			mapResult.put ("Price", dblQuantoAdjustedPrice);

			mapResult.put ("QuantoAdjustedCleanPrice", dblQuantoAdjustedPrice);

			mapResult.put ("QuantoAdjustedDirtyPrice", 100. * (1. + (dblQuantoAdjustedDirtyPV /
				dblAdjustedNotional / dblValueNotional)));

			mapResult.put ("QuantoAdjustedPrice", dblQuantoAdjustedPrice);

			mapResult.put ("UnadjustedCleanPrice", dblUnadjustedPrice);

			mapResult.put ("UnadjustedDirtyPrice", 100. * (1. + (dblUnadjustedDirtyPV / dblAdjustedNotional /
				dblValueNotional)));

			mapResult.put ("UnadjustedPrice", dblUnadjustedPrice);
		}

		mapResult.put ("CalcTime", (System.nanoTime() - lStart) * 1.e-09);

		return mapResult;
	}

	@Override public java.util.Set<java.lang.String> measureNames()
	{
		java.util.Set<java.lang.String> setstrMeasureNames = new java.util.TreeSet<java.lang.String>();

		setstrMeasureNames.add ("Accrued01");

		setstrMeasureNames.add ("Accrued");

		setstrMeasureNames.add ("CalcTime");

		setstrMeasureNames.add ("CleanDV01");

		setstrMeasureNames.add ("CleanPrice");

		setstrMeasureNames.add ("CleanPV");

		setstrMeasureNames.add ("CV01");

		setstrMeasureNames.add ("DirtyDV01");

		setstrMeasureNames.add ("DirtyPrice");

		setstrMeasureNames.add ("DirtyPV");

		setstrMeasureNames.add ("DV01");

		setstrMeasureNames.add ("FairPremium");

		setstrMeasureNames.add ("Fixing01");

		setstrMeasureNames.add ("ParRate");

		setstrMeasureNames.add ("Price");

		setstrMeasureNames.add ("PV");

		setstrMeasureNames.add ("QuantoAdjustedCleanDV01");

		setstrMeasureNames.add ("QuantoAdjustedCleanPrice");

		setstrMeasureNames.add ("QuantoAdjustedCleanPV");

		setstrMeasureNames.add ("QuantoAdjustedDirtyDV01");

		setstrMeasureNames.add ("QuantoAdjustedDirtyPrice");

		setstrMeasureNames.add ("QuantoAdjustedDirtyPV");

		setstrMeasureNames.add ("QuantoAdjustedFairPremium");

		setstrMeasureNames.add ("QuantoAdjustedParRate");

		setstrMeasureNames.add ("QuantoAdjustedPrice");

		setstrMeasureNames.add ("QuantoAdjustedPV");

		setstrMeasureNames.add ("QuantoAdjustedRate");

		setstrMeasureNames.add ("QuantoAdjustedUpfront");

		setstrMeasureNames.add ("QuantoAdjustmentFactor");

		setstrMeasureNames.add ("QuantoAdjustmentPremium");

		setstrMeasureNames.add ("Rate");

		setstrMeasureNames.add ("ResetDate");

		setstrMeasureNames.add ("ResetRate");

		setstrMeasureNames.add ("UnadjustedCleanDV01");

		setstrMeasureNames.add ("UnadjustedCleanPrice");

		setstrMeasureNames.add ("UnadjustedCleanPV");

		setstrMeasureNames.add ("UnadjustedDirtyDV01");

		setstrMeasureNames.add ("UnadjustedDirtyPrice");

		setstrMeasureNames.add ("UnadjustedDirtyPV");

		setstrMeasureNames.add ("UnadjustedFairPremium");

		setstrMeasureNames.add ("UnadjustedParRate");

		setstrMeasureNames.add ("UnadjustedPrice");

		setstrMeasureNames.add ("UnadjustedPV");

		setstrMeasureNames.add ("UnadjustedRate");

		setstrMeasureNames.add ("UnadjustedUpfront");

		setstrMeasureNames.add ("Upfront");

		return setstrMeasureNames;
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDDirtyPVDManifestMeasure (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || valParams.valueDate() >= _dblMaturity || null == csqs) return null;

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (couponCurrency()[0]);

		if (null == dcFunding) return null;

		try {
			org.drip.quant.calculus.WengertJacobian jackDDirtyPVDManifestMeasure = null;

			for (org.drip.analytics.period.CashflowPeriod p : _lsCouponPeriod) {
				double dblPeriodPayDate = p.getPayDate();

				if (p.getStartDate() < valParams.valueDate()) continue;

				org.drip.quant.calculus.WengertJacobian wjDForwardDManifestMeasure =
					dcFunding.jackDForwardDManifestMeasure (p.getStartDate(), p.getEndDate(), "Rate",
						p.getCouponDCF());

				if (null == wjDForwardDManifestMeasure) continue;

				int iNumQuote = wjDForwardDManifestMeasure.numParameters();

				if (0 == iNumQuote) continue;

				org.drip.quant.calculus.WengertJacobian wjDPayDFDManifestMeasure =
					dcFunding.jackDDFDManifestMeasure (dblPeriodPayDate, "Rate");

				if (null == wjDPayDFDManifestMeasure || iNumQuote !=
					wjDPayDFDManifestMeasure.numParameters())
					continue;

				double dblForward = dcFunding.libor (p.getStartDate(), p.getEndDate());

				double dblPayDF = dcFunding.df (dblPeriodPayDate);

				if (null == jackDDirtyPVDManifestMeasure)
					jackDDirtyPVDManifestMeasure = new org.drip.quant.calculus.WengertJacobian (1,
						iNumQuote);

				double dblPeriodNotional = _dblNotional * notional (p.getStartDate(), p.getEndDate());

				double dblPeriodDCF = p.getCouponDCF();

				for (int i = 0; i < iNumQuote; ++i) {
					double dblDCashflowPVDManifestMeasurei = dblPeriodDCF * (dblForward *
						wjDPayDFDManifestMeasure.getFirstDerivative (0, i) + dblPayDF *
							wjDForwardDManifestMeasure.getFirstDerivative (0, i));

					if (!jackDDirtyPVDManifestMeasure.accumulatePartialFirstDerivative (0, i,
						dblPeriodNotional * dblDCashflowPVDManifestMeasurei))
						return null;
				}
			}

			return jackDDirtyPVDManifestMeasure;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.quant.calculus.WengertJacobian manifestMeasureDFMicroJack (
		final java.lang.String strManifestMeasure,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || valParams.valueDate() >= _dblMaturity || null == strManifestMeasure)
			return null;

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (couponCurrency()[0]);

		if (null == dcFunding) return null;

		if ("Rate".equalsIgnoreCase (strManifestMeasure) || "SwapRate".equalsIgnoreCase (strManifestMeasure))
		{
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapMeasures = value
				(valParams, pricerParams, csqs, quotingParams);

			if (null == mapMeasures) return null;

			double dblDirtyDV01 = mapMeasures.get ("DirtyDV01");

			double dblParSwapRate = mapMeasures.get ("SwapRate");

			try {
				org.drip.quant.calculus.WengertJacobian wjSwapRateDFMicroJack = null;

				for (org.drip.analytics.period.CashflowPeriod p : _lsCouponPeriod) {
					double dblPeriodPayDate = p.getPayDate();

					if (dblPeriodPayDate < valParams.valueDate()) continue;

					org.drip.quant.calculus.WengertJacobian wjPeriodFwdRateDF =
						dcFunding.jackDForwardDManifestMeasure (p.getStartDate(), p.getEndDate(), "Rate",
							p.getCouponDCF());

					org.drip.quant.calculus.WengertJacobian wjPeriodPayDFDF =
						dcFunding.jackDDFDManifestMeasure (dblPeriodPayDate, "Rate");

					if (null == wjPeriodFwdRateDF || null == wjPeriodPayDFDF) continue;

					double dblForwardRate = dcFunding.libor (p.getStartDate(), p.getEndDate());

					double dblPeriodPayDF = dcFunding.df (dblPeriodPayDate);

					if (null == wjSwapRateDFMicroJack)
						wjSwapRateDFMicroJack = new org.drip.quant.calculus.WengertJacobian (1,
							wjPeriodFwdRateDF.numParameters());

					double dblPeriodNotional = notional (p.getStartDate(), p.getEndDate());

					double dblPeriodDCF = p.getCouponDCF();

					for (int k = 0; k < wjPeriodFwdRateDF.numParameters(); ++k) {
						double dblPeriodMicroJack = (dblForwardRate - dblParSwapRate) *
							wjPeriodPayDFDF.getFirstDerivative (0, k) + dblPeriodPayDF *
								wjPeriodFwdRateDF.getFirstDerivative (0, k);

						if (!wjSwapRateDFMicroJack.accumulatePartialFirstDerivative (0, k, dblPeriodNotional
							* dblPeriodDCF * dblPeriodMicroJack / dblDirtyDV01))
							return null;
					}
				}

				return wjSwapRateDFMicroJack;
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private org.drip.state.estimator.PredictorResponseWeightConstraint forwardLatentStatePRWC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final org.drip.state.representation.LatentStateMetricMeasure lsmm)
	{
		double dblValueDate = valParams.valueDate();

		if (dblValueDate >= maturity().julian() || null == csqs) return null;

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (couponCurrency()[0]);

		if (null == dcFunding) return null;

		org.drip.state.estimator.PredictorResponseWeightConstraint prwc = new
			org.drip.state.estimator.PredictorResponseWeightConstraint();

		double dblCleanCV100 = 0.;
		boolean bFirstPeriod = true;
		double dblDerivedParBasisSpread = 0.;

		java.lang.String[] astrManifestMeasure = lsmm.manifestMeasures();

		boolean bPV = org.drip.quant.common.StringUtil.MatchInStringArray ("PV", astrManifestMeasure, false);

		boolean bSwapRate = org.drip.quant.common.StringUtil.MatchInStringArray ("SwapRate",
			astrManifestMeasure, false);

		boolean bDerivedParBasisSpread = org.drip.quant.common.StringUtil.MatchInStringArray
			("DerivedParBasisSpread", astrManifestMeasure, false);

		boolean bReferenceParBasisSpread = org.drip.quant.common.StringUtil.MatchInStringArray
			("ReferenceParBasisSpread", astrManifestMeasure, false);

		try {
			if (bDerivedParBasisSpread)
				dblDerivedParBasisSpread = lsmm.measureQuoteValue ("DerivedParBasisSpread");

			for (org.drip.analytics.period.CashflowPeriod period : _lsCouponPeriod) {
				if (null == period) continue;

				double dblPayDate = period.getPayDate();

				if (dblValueDate > dblPayDate) {
					bFirstPeriod = false;
					continue;
				}

				double dblPeriodDCF = period.getCouponDCF();

				if (bFirstPeriod) {
					bFirstPeriod = false;

					if (dblValueDate > period.getStartDate())
						dblPeriodDCF -= period.getAccrualDCF (dblValueDate);
				}

				double dblPeriodCV100 = dblPeriodDCF * dcFunding.df (dblPayDate) * notional (dblPayDate);

				dblCleanCV100 += dblPeriodCV100;
				double dblNotionalPeriodCV100 = dblPeriodCV100 * _dblNotional;

				if (!_bIsReference) {
					if (!prwc.addPredictorResponseWeight (dblPayDate, dblNotionalPeriodCV100)) return null;

					if (bPV && !prwc.addDResponseWeightDManifestMeasure ("PV", dblPayDate,
						dblNotionalPeriodCV100))
						return null;

					if (bSwapRate && !prwc.addDResponseWeightDManifestMeasure ("SwapRate", dblPayDate,
						dblNotionalPeriodCV100))
						return null;

					if (bDerivedParBasisSpread && !prwc.addDResponseWeightDManifestMeasure
						("DerivedParBasisSpread", dblPayDate, dblNotionalPeriodCV100))
						return null;

					if (bReferenceParBasisSpread && !prwc.addDResponseWeightDManifestMeasure
						("ReferenceParBasisSpread", dblPayDate, dblNotionalPeriodCV100))
						return null;
				}
			}

			double dblNotionalCleanCV100 = dblCleanCV100 * _dblNotional;

			if (!_bIsReference) {
				if (!prwc.updateValue (-1. * dblNotionalCleanCV100 * dblDerivedParBasisSpread)) return null;

				if (bDerivedParBasisSpread && !prwc.updateDValueDManifestMeasure ("DerivedParBasisSpread",
					-1. * dblNotionalCleanCV100))
					return null;
			} else {
				if (bReferenceParBasisSpread && !prwc.updateDValueDManifestMeasure
					("ReferenceParBasisSpread", -1. * dblNotionalCleanCV100))
					return null;
			}

			return prwc;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private org.drip.state.estimator.PredictorResponseWeightConstraint discountLatentStatePRWC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final org.drip.state.representation.LatentStateMetricMeasure lsmm)
	{
		return valParams.valueDate() >= maturity().julian() ? null : new
			org.drip.state.estimator.PredictorResponseWeightConstraint();
	}

	@Override public org.drip.state.estimator.PredictorResponseWeightConstraint generateCalibPRLC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final org.drip.state.representation.LatentStateMetricMeasure lsmm)
	{
		if (null == valParams || null == lsmm || null == csqs || !(lsmm instanceof
			org.drip.analytics.rates.RatesLSMM))
			return null;

		java.lang.String strQuantificationMetric = lsmm.quantificationMetric();

		if (null == strQuantificationMetric) return null;

		if (org.drip.analytics.rates.DiscountCurve.QUANTIFICATION_METRIC_DISCOUNT_FACTOR.equalsIgnoreCase
			(strQuantificationMetric))
			return discountLatentStatePRWC (valParams, pricerParams, csqs, quotingParams, lsmm);

		if (org.drip.analytics.rates.ForwardCurve.QUANTIFICATION_METRIC_FORWARD_RATE.equalsIgnoreCase
			(strQuantificationMetric))
			return forwardLatentStatePRWC (valParams, pricerParams, csqs, quotingParams, lsmm);

		return null;
	}

	@Override public java.lang.String fieldDelimiter()
	{
		return "!";
	}

	@Override public java.lang.String objectTrailer()
	{
		return "&";
	}

	@Override public byte[] serialize()
	{
		java.lang.StringBuffer sb = new java.lang.StringBuffer();

		sb.append (org.drip.service.stream.Serializer.VERSION + fieldDelimiter());

		sb.append (_dblNotional + fieldDelimiter());

		sb.append (_dblSpread + fieldDelimiter());

		if (null == _strCurrency || _strCurrency.isEmpty())
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + fieldDelimiter());
		else
			sb.append (_strCurrency + fieldDelimiter());

		if (null == _strCode || _strCode.isEmpty())
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + fieldDelimiter());
		else
			sb.append (_strCode + fieldDelimiter());

		sb.append (_dblMaturity + fieldDelimiter());

		sb.append (_dblEffective + fieldDelimiter());

		if (null == _fri)
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + fieldDelimiter());
		else
			sb.append (new java.lang.String (_fri.serialize()) + fieldDelimiter());

		if (null == _notlSchedule)
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + fieldDelimiter());
		else
			sb.append (new java.lang.String (_notlSchedule.serialize()) + fieldDelimiter());

		if (null == _settleParams)
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + fieldDelimiter());
		else
			sb.append (new java.lang.String (_settleParams.serialize()) + fieldDelimiter());

		if (null == _lsCouponPeriod)
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING);
		else {
			boolean bFirstEntry = true;

			java.lang.StringBuffer sbPeriods = new java.lang.StringBuffer();

			for (org.drip.analytics.period.CashflowPeriod p : _lsCouponPeriod) {
				if (null == p) continue;

				if (bFirstEntry)
					bFirstEntry = false;
				else
					sbPeriods.append (collectionRecordDelimiter());

				sbPeriods.append (new java.lang.String (p.serialize()));
			}

			if (sbPeriods.toString().isEmpty())
				sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING);
			else
				sb.append (sbPeriods.toString());
		}

		sb.append (fieldDelimiter() + _bIsReference);

		return sb.append (objectTrailer()).toString().getBytes();
	}

	@Override public org.drip.service.stream.Serializer deserialize (
		final byte[] ab)
	{
		try {
			return new FloatingStream (ab);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Floating Rate Index
	 * 
	 * @return The Floating Rate Index
	 */

	public org.drip.product.params.FloatingRateIndex fri()
	{
		return _fri;
	}

	/**
	 * Retrieve the Stream Spread
	 *  
	 * @return The Spread
	 */

	public double spread()
	{
		return _dblSpread;
	}

	/**
	 * Is this a Reference Stream?
	 *  
	 * @return TRUE => This is a Reference Stream
	 */

	public boolean reference()
	{
		return _bIsReference;
	}
}
