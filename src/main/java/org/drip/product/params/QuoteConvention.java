
package org.drip.product.params;

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
 * QuoteConvention contains the Component Market Convention Parameters - the quote convention, the
 *  calculation type, the first settle date, and the redemption amount. It exports serialization into and
 *  de-serialization out of byte arrays.
 *
 * @author Lakshmi Krishnamurthy
 */

public class QuoteConvention implements org.drip.product.params.Validatable {
	private String _strCalculationType = "";
	private double _dblFirstSettleDate = java.lang.Double.NaN;
	private double _dblRedemptionValue = java.lang.Double.NaN;
	private org.drip.param.valuation.CashSettleParams _cashSettleParams = null;
	private org.drip.param.valuation.ValuationCustomizationParams _valuationCustomizationParams = null;

	/**
	 * Construct the QuoteConvention object from the valuation Customization Parameters, the calculation
	 *  type, the first settle date, and the redemption value.
	 * 
	 * @param valuationCustomizationParams Valuation Customization Parameters
	 * @param strCalculationType Calculation Type
	 * @param dblFirstSettleDate First Settle Date
	 * @param dblRedemptionValue Redemption Value
	 * @param iSettleLag Settle Lag
	 * @param strSettleCalendar Settlement Calendar
	 * @param iSettleAdjustMode Is Settle date business adjusted
	 */

	public QuoteConvention (
		final org.drip.param.valuation.ValuationCustomizationParams valuationCustomizationParams,
		final String strCalculationType,
		final double dblFirstSettleDate,
		final double dblRedemptionValue,
		final int iSettleLag,
		final String strSettleCalendar,
		final int iSettleAdjustMode)
	{
		_dblFirstSettleDate = dblFirstSettleDate;
		_dblRedemptionValue = dblRedemptionValue;
		_strCalculationType = strCalculationType;
		_valuationCustomizationParams = valuationCustomizationParams;
		
		_cashSettleParams = new org.drip.param.valuation.CashSettleParams (iSettleLag, strSettleCalendar,
			iSettleAdjustMode);
	}

	public double settleDate (
		final org.drip.param.valuation.ValuationParams valParams)
		throws java.lang.Exception
	{
		if (null == valParams)
			throw new java.lang.Exception ("QuoteConvention::settleDate => Invalid inputs");

		return _cashSettleParams.cashSettleDate (valParams.valueDate());
	}

	@Override public boolean validate()
	{
		return org.drip.quant.common.NumberUtil.IsValid (_dblFirstSettleDate) &&
			org.drip.quant.common.NumberUtil.IsValid (_dblRedemptionValue);
	}

	/**
	 * Retrieve the Calculation Type
	 * 
	 * @return The Calculation Type
	 */

	public String calculationType()
	{
		return _strCalculationType;
	}

	/**
	 * Retrieve the First Settle Date
	 * 
	 * @return The First Settle Date
	 */

	public double firstSettleDate()
	{
		return _dblFirstSettleDate;
	}

	/**
	 * Retrieve the Redemption Value
	 * 
	 * @return The Redemption Value
	 */

	public double redemptionValue()
	{
		return _dblRedemptionValue;
	}

	/**
	 * Retrieve the Cash Settle Parameters
	 * 
	 * @return The Cash Settle Parameters
	 */

	public org.drip.param.valuation.CashSettleParams cashSettleParams()
	{
		return _cashSettleParams;
	}

	/**
	 * Retrieve the Valuation Customization Parameters
	 * 
	 * @return The Valuation Customization Parameters
	 */

	public org.drip.param.valuation.ValuationCustomizationParams valuationCustomizationParams()
	{
		return _valuationCustomizationParams;
	}
}
