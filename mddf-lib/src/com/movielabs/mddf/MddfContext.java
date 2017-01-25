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
package com.movielabs.mddf;

import org.jdom2.Element;

/**
 * Defines constants and Enum types used to indicate the context of various MDDF
 * operations and requests. A primary function of the <tt>MddfContext</tt> class
 * is providing a mechanism for identifying the type and format of an MDDF file
 * (e.g. an Avails file that conforms to v2.2 of the standard and is encoded as
 * XML). <tt>MddfContext</tt> also provides constants that are used to identify and
 * locate the XSD files required for processing MDDF files.
 * 
 * @author L. Levin, Critical Architectures LLC
 *
 */
public class MddfContext {

	public static final String SCHEMA_PREFIX = "http://www.movielabs.com/schema/";

	public static final String NSPACE_CMD_PREFIX = SCHEMA_PREFIX + "md/v";
	public static final String NSPACE_CMD_SUFFIX = "/md";

	public static final String NSPACE_MANIFEST_PREFIX = SCHEMA_PREFIX + "manifest/v";
	public static final String NSPACE_MANIFEST_SUFFIX = "/manifest";

	public static final String NSPACE_MDMEC_PREFIX = SCHEMA_PREFIX + "mdmec/v";
	public static final String NSPACE_MDMEC_SUFFIX = "/mdmec";

	public static final String NSPACE_AVAILS_PREFIX = SCHEMA_PREFIX + "avails/v";
	public static final String NSPACE_AVAILS_SUFFIX = "/avails";

	/**
	 * Path indicating location of XSD files for all supported MDDF schemas.
	 */
	public static final String RSRC_PATH = "/com/movielabs/mddf/resources/";

	public enum MDDF_TYPE {
		MEC, AVAILS, MANIFEST
	};

	public enum FILE_FMT {
		AVAILS_1_6("Avail", "1.6", "xlsx"),
		AVAILS_1_7("Avail", "1.7", "xlsx"),
		AVAILS_2_1("Avail", "2.1","xlsx"),
		AVAILS_2_2("Avail", "2.2", "xml"),
		AVAILS_2_2_1("Avail", "2.2.1", "xml"),
		MANIFEST_1_4("Manifest", "1.4", "xml"),
		MANIFEST_1_5("Manifest", "1.5", "xml"),
		MANIFEST_1_6("Manifest", "1.6", "xml"),
		MDMEC_2_4("MEC", "2.4", "xml"),
		MDMEC_2_5("MEC", "2.5", "xml");

		private String standard;
		private String ver;
		private String encoding;
		private String label;

		/**
		 * @param standard
		 * @param ver
		 * @param encoding
		 */
		private FILE_FMT(String standard, String ver, String encoding) {
			this.standard = standard;
			this.ver = ver;
			this.encoding = encoding;
			label = standard + " v" + ver + " (" + encoding + ")";
		}

		@Override
		public String toString() {
			return label;
		}

	}

	public static FILE_FMT identifyMddfFormat(Element docRootEl) {
		String nSpaceUri = docRootEl.getNamespaceURI();
		String schemaType = null;
		if (nSpaceUri.contains("manifest")) {
			schemaType = "manifest";
		} else if (nSpaceUri.contains("avails")) {
			schemaType = "avails";
		} else if (nSpaceUri.contains("mdmec")) {
			schemaType = "mdmec";
		} else {
			return null;
		}
		String schemaPrefix = MddfContext.SCHEMA_PREFIX + schemaType + "/v";
		String schemaVer = nSpaceUri.replace(schemaPrefix, "");
		schemaVer = schemaVer.replace("/" + schemaType, "");
		switch (schemaType) {
		case "manifest":
			switch (schemaVer) {
			case "1.4":
				return FILE_FMT.MANIFEST_1_4;
			case "1.5":
				return FILE_FMT.MANIFEST_1_5;
			case "1.6":
				return FILE_FMT.MANIFEST_1_6;
			}
			break;
		case "avails":
			switch (schemaVer) {
			case "2.2.1":
				return FILE_FMT.AVAILS_2_2_1;
			case "2.2":
				return FILE_FMT.AVAILS_2_2;
			case "2.1":
				return FILE_FMT.AVAILS_2_1;
			}
			break;
		case "mdmec":
			switch (schemaVer) {
			case "2.4":
				return FILE_FMT.MDMEC_2_4;
			case "2.5":
				return FILE_FMT.MDMEC_2_5;
			}
			break;
		}

		return null;
	}

}
