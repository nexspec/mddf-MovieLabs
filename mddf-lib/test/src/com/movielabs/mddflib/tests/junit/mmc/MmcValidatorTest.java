/**
 * Copyright (c) 2018 MovieLabs

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
package com.movielabs.mddflib.tests.junit.mmc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

import com.movielabs.mddflib.logging.LogMgmt;
import com.movielabs.mddflib.manifest.validation.profiles.MMCoreValidator;
import com.movielabs.mddflib.testsupport.InstrumentedLogger;
import com.movielabs.mddflib.util.xml.MddfTarget;

/**
 * @author L. Levin, Critical Architectures LLC
 *
 */
public class MmcValidatorTest extends MMCoreValidator {

	private static String rsrcPath = "./test/resources/mmc/";
	private InstrumentedLogger iLog;

	public MmcValidatorTest() {
		super(new InstrumentedLogger());
		iLog = (InstrumentedLogger) loggingMgr;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		curFile = null;
		curFileName = null;
		curFileIsValid = true;
		curRootEl = null;
		rootNS = null;
		iLog.clearLog();
		iLog.setPrintToConsole(false);
	}

	@AfterEach
	public void tearDown() {
	}

	/**
	 * @param string
	 */
	protected MddfTarget initialize(String testFileName) {
		iLog.setPrintToConsole(false);
		iLog.setMinLevel(iLog.LEV_DEBUG);
		iLog.setInfoIncluded(true);
		iLog.log(iLog.LEV_INFO, iLog.TAG_N_A, "*** Testing with file " + testFileName, null, "JUnit");

		String srcFilePath = rsrcPath + testFileName;
		File srcFile = new File(srcFilePath);
		if(!srcFile.exists()) {
			throw new MissingResourceException("Missing test artifact " + srcFilePath, "File", srcFilePath);
		} 
			MddfTarget target = new MddfTarget(srcFile, iLog);
			return target; 
	}

	/**
	 * @throws JDOMException
	 * @throws IOException
	 * 
	 */
	@Test
	public void testV1_noErrors() throws IOException, JDOMException {
		MddfTarget target = initialize("MMCore_v1_noErr.xml");
		execute(target, "MMC-1");
		try {
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_FATAL));
			// 1 error due to missing MEC file
			assertEquals(1, iLog.getCountForLevel(LogMgmt.LEV_ERR));
//		assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_WARN));
//		assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_NOTICE));
		} catch (AssertionFailedError e) {
			dumpLog();
			throw e;
		}
	}

	/**
	 * @throws JDOMException
	 * @throws IOException
	 * 
	 */
	// @Test
	public void testV1_Errors() throws IOException, JDOMException {
		MddfTarget target = initialize("MMCore_v1_ERRORS.xml");
		execute(target, "MMC-1");
		try {
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_FATAL));
			assertEquals(3, iLog.getCountForLevel(LogMgmt.LEV_ERR));
			assertEquals(2, iLog.getCountForLevel(LogMgmt.LEV_WARN));
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_NOTICE));
		} catch (AssertionFailedError e) {
			dumpLog();
			throw e;
		}
	}

	/**
	 * @throws JDOMException
	 * @throws IOException
	 * 
	 */
	@Test
	public void test_TV_Series() throws IOException, JDOMException {
		MddfTarget target = initialize("TV/VEEP_Series_manifest.xml");
		execute(target, "MMC-1");
		try {
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_FATAL));
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_ERR));
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_WARN));
			assertEquals(3, iLog.getCountForLevel(LogMgmt.LEV_NOTICE));
		} catch (AssertionFailedError e) {
			dumpLog();
			throw e;
		}
	}

	/**
	 * @throws JDOMException
	 * @throws IOException
	 * 
	 */
	@Test
	public void test_TV_Season() throws IOException, JDOMException {
		MddfTarget target = initialize("TV/VEEP_Season5_manifest.xml");
		execute(target, "MMC-1");
		try {
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_FATAL));
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_ERR));
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_WARN));
//		assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_NOTICE));
		} catch (AssertionFailedError e) {
			dumpLog();
			throw e;
		}
	}

	/**
	 * @throws JDOMException
	 * @throws IOException
	 * 
	 */
	@Test
	public void test_TV_Episode() throws IOException, JDOMException {
		MddfTarget target = initialize("TV/VEEP_Season5_E5_manifest.xml");
		execute(target, "MMC-1");
		try {
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_FATAL));
			assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_ERR));
			assertEquals(1, iLog.getCountForLevel(LogMgmt.LEV_WARN));
//		assertEquals(0, iLog.getCountForLevel(LogMgmt.LEV_NOTICE)); 
		} catch (AssertionFailedError e) {
			dumpLog();
			throw e;
		}
	}

	protected void execute(MddfTarget target, String profile) throws IOException, JDOMException {
		iLog.log(iLog.LEV_INFO, iLog.TAG_N_A, "Testing with file " + target.getSrcFile().getCanonicalPath(), null,
				"JUnit");
		super.process(target, profile);
		iLog.log(iLog.LEV_INFO, iLog.TAG_N_A, "===== Test completed =====", null, "JUnit");
		iLog.setPrintToConsole(false);
	}

	private void dumpLog() {
		System.out.println("\n\n === FAILED TEST... dumping log ===");
		iLog.printLog();
		System.out.println(" === End log dump for FAILED TEST ===");

	}
}
