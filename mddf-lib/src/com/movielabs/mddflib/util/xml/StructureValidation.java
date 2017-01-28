/**
 * Copyright (c) 2017 MovieLabs

 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.movielabs.mddflib.util.xml;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.movielabs.mddflib.logging.IssueLogger;
import com.movielabs.mddflib.logging.LogMgmt;
import com.movielabs.mddflib.logging.LogReference;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A 'helper' class that supports the validation of an XML file against a set of
 * structural requirements not specified via an XSD. The MDDF specifications
 * define many requirements for specific use cases as well as recommended 'best
 * practices'. These requirements and recommendations specify relationships
 * between XML elements that are not defined via the XSD. In order to support
 * validation they are instead formally specified via a JSON-formatted
 * <i>structure definition</i> file. For example:
 * 
 * <pre>
 * <tt>
 * 
		"Season": 
		{
			"requirement": 
			[
				{
					"docRef": "AVAIL:struc01",
					"min": "1",
					"max": "1", 
					"xpath": "{avail}SeasonMetadata[../{avail}WorkType/text()='Season']"
				}
			]
		},
 *</tt>
 * </pre>
 * <p>
 * The <tt>StructureValidation</tt> class provides the functions that can
 * interpret the requirements and test an XML file for compliance.
 * </p>
 * 
 * @author L. Levin, Critical Architectures LLC
 *
 */
public class StructureValidation {

	private IssueLogger logger;
	protected String logMsgSrcId;
	private String availPrefix;
	private String mdPrefix;
	private XPathFactory xpfac = XPathFactory.instance();

	public StructureValidation(IssueLogger logger, String logMsgSrcId) {
		this.logger = logger;
		availPrefix = XmlIngester.availsNSpace.getPrefix() + ":";
		mdPrefix = XmlIngester.mdNSpace.getPrefix() + ":";
	}

	public boolean validateStructure(Element target, JSONObject rqmt) {
		boolean curFileIsValid = true;

		int min = rqmt.optInt("min", 0);
		int max = rqmt.optInt("max", -1);
		String docRef = rqmt.optString("docRef");

		Object xpaths = rqmt.opt("xpath");
		List<XPathExpression<Element>> xpeList = new ArrayList<XPathExpression<Element>>();
		String targetList = ""; // for use if error msg is required
		if (xpaths instanceof String) {
			String xpathDef = (String) xpaths;
			xpeList.add(resolveXPath(xpathDef));

			String[] xpParts = xpathDef.split("\\[");
			targetList = xpParts[0];
		} else if (xpaths instanceof JSONArray) {
			JSONArray xpArray = (JSONArray) xpaths;
			for (int i = 0; i < xpArray.size(); i++) {
				String xpathDef = xpArray.getString(i);
				xpeList.add(resolveXPath(xpathDef));
				String[] xpParts = xpathDef.split("\\[");
				if (i < 1) {
					targetList = xpParts[0];
				} else if (i == (xpArray.size() - 1)) {
					targetList = targetList + ", or " + xpParts[0];
				} else {
					targetList = targetList + ", " + xpParts[0];
				}
			}
		}
		targetList = targetList.replaceAll("\\{\\w+\\}", "");

		List<Element> matchedElList = new ArrayList<Element>();
		for (int i = 0; i < xpeList.size(); i++) {
			XPathExpression<Element> xpExp = xpeList.get(i);
			List<Element> nextElList = xpExp.evaluate(target);
			matchedElList.addAll(nextElList);
		}

		// check cardinality
		int count = matchedElList.size();
		if (min > 0 && (count < min)) {
			String elName = target.getName();
			String msg = "Invalid " + elName + " structure: missing child elements";
			String explanation = elName + " requires minimum of " + min + " of child " + targetList + " elements";
			LogReference srcRef = resolveDocRef(docRef);
			logger.logIssue(LogMgmt.TAG_AVAIL, LogMgmt.LEV_ERR, target, msg, explanation, srcRef, logMsgSrcId);
			curFileIsValid = false;
		}
		if (max > -1 && (count > max)) {
			String elName = target.getName();
			String msg = "Invalid " + elName + " structure: too many child elements";
			String explanation = elName + " permits maximum of " + max + " of child " + targetList + " elements";
			LogReference srcRef = resolveDocRef(docRef);
			logger.logIssue(LogMgmt.TAG_AVAIL, LogMgmt.LEV_ERR, target, msg, explanation, srcRef, logMsgSrcId);
			curFileIsValid = false;
		}

		return curFileIsValid;
	}

	private XPathExpression<Element> resolveXPath(String xpathDef) {
		/*
		 * replace namespace placeholders with actual prefix being used
		 */
		String t1 = xpathDef.replaceAll("\\{avail\\}", availPrefix);
		String t2 = t1.replaceAll("\\{md\\}", mdPrefix);
		// Now format an XPath
		String xpath = "./" + t2;
		XPathExpression<Element> xpExp = xpfac.compile(xpath, Filters.element(), null, XmlIngester.availsNSpace,
				XmlIngester.mdNSpace);
		return xpExp;
	}

	private LogReference resolveDocRef(String docRef) {
		String docStandard = null;
		String docSection = null;
		LogReference srcRef = null;
		if (docRef != null) {
			String[] parts = docRef.split(":");
			docStandard = parts[0];
			docSection = parts[1];
			srcRef = LogReference.getRef(docStandard, docSection);
		}
		return srcRef;
	}
}