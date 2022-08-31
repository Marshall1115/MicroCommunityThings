package com.java110.gateway.sip.message.helper;

import com.java110.core.util.DateUtils;
import gov.nist.javax.sip.header.SIPHeader;

import java.util.Date;

public class CustomSIPDateHeader extends SIPHeader{

	@Override
	protected StringBuilder encodeBody(StringBuilder buffer) {
		return buffer.append(DateUtils.getGBFormatDate(new Date()));
	}


}
