
package org.drip.param.period;

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
 * CompositePeriodSetting implements the custom setting parameters for the composite coupon period.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CompositePeriodSetting {
	private int _iFreq = -1;
	private String _strTenor = "";
	private String _strPayCurrency = "";
	private double _dblBaseNotional = java.lang.Double.NaN;
	private org.drip.quant.common.Array2D _fsCoupon = null;
	private org.drip.quant.common.Array2D _fsNotional = null;
	private org.drip.state.identifier.CreditLabel _creditLabel = null;
	private org.drip.param.period.FixingSetting _fxFixingSetting = null;
	private org.drip.analytics.daycount.DateAdjustParams _dapPay = null;

	/**
	 * CompositePeriodSetting Constructor
	 * 
	 * @param iFreq The Frequency
	 * @param strTenor The Period Tenor
	 * @param strPayCurrency The Pay Currency
	 * @param dapPay Composite Pay Date Adjust Parameters
	 * @param dblBaseNotional The Period Base Notional
	 * @param fsCoupon The Period Coupon Schedule
	 * @param fsNotional The Period Notional Schedule
	 * @param fxFixingSetting The FX Fixing Setting
	 * @param creditLabel The Period Credit Label
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public CompositePeriodSetting (
		final int iFreq,
		final String strTenor,
		final String strPayCurrency,
		final org.drip.analytics.daycount.DateAdjustParams dapPay,
		final double dblBaseNotional,
		final org.drip.quant.common.Array2D fsCoupon,
		final org.drip.quant.common.Array2D fsNotional,
		final org.drip.param.period.FixingSetting fxFixingSetting,
		final org.drip.state.identifier.CreditLabel creditLabel)
		throws java.lang.Exception
	{
		if (0 >= (_iFreq = iFreq) || null == (_strTenor = strTenor) || _strTenor.isEmpty() || null ==
			(_strPayCurrency = strPayCurrency) || _strPayCurrency.isEmpty() ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblBaseNotional = dblBaseNotional))
			throw new java.lang.Exception ("CompositePeriodSetting ctr: Invalid Inputs");

		_dapPay = dapPay;
		_creditLabel = creditLabel;
		_fxFixingSetting = fxFixingSetting;

		if (null == (_fsCoupon = fsCoupon))
			_fsCoupon = org.drip.quant.common.Array2D.BulletSchedule();

		if (null == (_fsNotional = fsNotional))
			_fsNotional = org.drip.quant.common.Array2D.BulletSchedule();
	}

	/**
	 * Retrieve the Frequency
	 * 
	 * @return The Frequency
	 */

	public int freq()
	{
		return _iFreq;
	}

	/**
	 * Retrieve the Tenor
	 * 
	 * @return The Tenor
	 */

	public String tenor()
	{
		return _strTenor;
	}

	/**
	 * Retrieve the Pay Currency
	 * 
	 * @return The Pay Currency
	 */

	public String payCurrency()
	{
		return _strPayCurrency;
	}

	/**
	 * Retrieve the Pay DAP
	 * 
	 * @return The Pay DAP
	 */

	public org.drip.analytics.daycount.DateAdjustParams dapPay()
	{
		return _dapPay;
	}

	/**
	 * Retrieve the Base Notional
	 * 
	 * @return The Base Notional
	 */

	public double baseNotional()
	{
		return _dblBaseNotional;
	}

	/**
	 * Retrieve the Notional Schedule
	 * 
	 * @return The Notional Schedule
	 */

	public org.drip.quant.common.Array2D notionalSchedule()
	{
		return _fsNotional;
	}

	/**
	 * Retrieve the Coupon Schedule
	 * 
	 * @return The Coupon Schedule
	 */

	public org.drip.quant.common.Array2D couponSchedule()
	{
		return _fsCoupon;
	}

	/**
	 * Retrieve the FX Fixing Setting
	 * 
	 * @return The FX Fixing Setting
	 */

	public org.drip.param.period.FixingSetting fxFixingSetting()
	{
		return _fxFixingSetting;
	}

	/**
	 * Retrieve the Credit Label
	 * 
	 * @return The Credit Label
	 */

	public org.drip.state.identifier.CreditLabel creditLabel()
	{
		return _creditLabel;
	}
}
