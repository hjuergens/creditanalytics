
package org.drip.product.params;

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
 * CDXIdentifier implements the creation and the static details of the all the NA, EU, SovX, EMEA, and ASIA
 *	standardized CDS indexes. It contains the index, the tenor, the series, and the version of a given CDX.
 *  It exports serialization into and de-serialization out of byte arrays.
 *  
 * @author Lakshmi Krishnamurthy
 */

public class CDXIdentifier {
	public int _iSeries = 0;
	public int _iVersion = 0;
	public java.lang.String _strIndex = "";
	public java.lang.String _strTenor = "";

	/**
	 * Create the CDX Identifier from the CDX Code
	 * 
	 * @param strCode The CDX Code
	 * 
	 * @return CDXIdentifier output
	 */

	public static final CDXIdentifier CreateCDXIdentifierFromCode (
		final java.lang.String strCode)
	{
		if (null == strCode || strCode.isEmpty()) return null;

		java.lang.String[] astrFields = strCode.split (".");

		if (null == astrFields || 4 > astrFields.length) return null;

		try {
			return new CDXIdentifier (new java.lang.Integer (astrFields[astrFields.length - 2]), new
				java.lang.Integer (astrFields[astrFields.length - 1]), astrFields[astrFields.length - 3],
					astrFields[astrFields.length - 4]);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create the CDX identifier from the CDX index, series, tenor, and the version
	 * 
	 * @param iSeries CDX Series
	 * @param iVersion CDX Version
	 * @param strIndex CDX Index
	 * @param strTenor CDX Tenor
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public CDXIdentifier (
		final int iSeries,
		final int iVersion,
		final java.lang.String strIndex,
		final java.lang.String strTenor)
		throws java.lang.Exception
	{
		if (null == (_strIndex = strIndex) || _strIndex.isEmpty() || null == (_strTenor = strTenor) ||
			_strTenor.isEmpty())
			throw new java.lang.Exception ("CDXIdentifier ctr => Invalid Inputs");

		_iSeries = iSeries;
		_iVersion = iVersion;
	}

	/**
	 * Return the CDX code string composed off of the index, tenor, series, and the version
	 * 
	 * @return The CDX Code string
	 */

	public java.lang.String getCode()
	{
		return _strIndex + "." + _strTenor + "." + _iSeries + "." + _iVersion;
	}
}
