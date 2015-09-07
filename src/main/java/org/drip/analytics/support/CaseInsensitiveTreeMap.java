
package org.drip.analytics.support;

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
 * CaseInsensitiveMap implements a case insensitive key in a hash map
 * 
 * @author Michael Beer
 */

@SuppressWarnings ("serial") public class CaseInsensitiveTreeMap<V> extends
	java.util.TreeMap<java.lang.String, V>
{
    @Override public V put (
    	final java.lang.String strKey,
    	final V v)
    {
	    return null == strKey ? null : super.put (strKey.toLowerCase(), v);
    }

    @Override public V get (
    	final java.lang.Object objKey)
    {
    	return null == objKey || !(objKey instanceof java.lang.String) ? null : super.get
    		(((java.lang.String) objKey).toLowerCase());
    }

    @Override public boolean containsKey (
    	final java.lang.Object objKey)
    {
    	return null == objKey || !(objKey instanceof java.lang.String) ? null : super.containsKey
    		(((java.lang.String) objKey).toLowerCase());
    }

    @Override public V remove (
    	final java.lang.Object objKey)
    {
    	return null == objKey || !(objKey instanceof java.lang.String) ? null : super.remove
    		(((java.lang.String) objKey).toLowerCase());
    }
}
