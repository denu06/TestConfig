package TestConfig.TestConfig;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkAPIResults;

public class TestingThread
{
	private static final int	MYTHREADS	= 10;

	public static void main()
	{
		try
		{
			ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
			System.out.println("Testing project : Branch");
			String Filepath = System.getProperty("FilePath");
			System.out.println("FilePath :" + Filepath);

			String DEV_KEY = System.getProperty("DEV_KEY");
			System.out.println("Dev Key :" + DEV_KEY);

			String SERVER_URL = System.getProperty("SERVER_URL");
			String PROJECT_NAME = System.getProperty("PROJECT_NAME");
			String PLAN_NAME = System.getProperty("PLAN_NAME");
			String BUILD_NAME = System.getProperty("BUILD_NAME");

			String result = "";
			String exception = null;

			////// Disable SSL Verification /////////

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
			{

				public java.security.cert.X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}

				public void checkClientTrusted1(X509Certificate[] certs, String authType)
				{
				}

				public void checkServerTrusted1(X509Certificate[] certs, String authType)
				{
				}

				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
				{
					// TODO Auto-generated method stub

				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
				{
					// TODO Auto-generated method stub

				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier()
			{
				public boolean verify(String hostname, SSLSession session)
				{
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			// Read data from Excel

			TestLinkAPIClient testLinkAPIClient = new TestLinkAPIClient(DEV_KEY, SERVER_URL);

			File file = new File("C:/Users/DeNy/Desktop/011020170706_TestCasesReport.xls");

			Workbook w = Workbook.getWorkbook(file);

			Sheet sheet = w.getSheet(0);

			int intRows = sheet.getRows();

			int intColumns = sheet.getColumns();

			for (int i = 2; i < intRows; i++)
			{
				Cell cell = sheet.getCell(3, i);

				String strStatusValue = cell.getContents();

				if (strStatusValue.equalsIgnoreCase("Pass"))
				{
					cell = sheet.getCell(2, i);

					String strTestCaseID = cell.getContents();

					System.out.println("TestCase ID is ::" + strTestCaseID);
					result = TestLinkAPIResults.TEST_PASSED;
					Runnable worker = new MyRunnable(testLinkAPIClient, PROJECT_NAME, PLAN_NAME, strTestCaseID, BUILD_NAME, exception, result);
					executor.execute(worker);
					//result = TestLinkAPIResults.TEST_PASSED;

					//testLinkAPIClient.reportTestCaseResult(PROJECT_NAME, PLAN_NAME, strTestCaseID, BUILD_NAME, exception, result);
				}
			}

			executor.shutdown();
			// Wait until all threads are finish
			while (!executor.isTerminated())
			{

			}
			System.out.println("\nFinished all threads");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static class MyRunnable implements Runnable
	{

		private final String			PROJECT_NAME, PLAN_NAME, strTestCaseID, BUILD_NAME, exception, result;
		private final TestLinkAPIClient	testLinkAPIClient;

		MyRunnable(TestLinkAPIClient testLinkAPIClient, String PROJECT_NAME, String PLAN_NAME, String strTestCaseID, String BUILD_NAME, String exception, String result)
		{
			this.PROJECT_NAME = PROJECT_NAME;
			this.PLAN_NAME = PLAN_NAME;
			this.BUILD_NAME = BUILD_NAME;
			this.strTestCaseID = strTestCaseID;
			this.exception = exception;
			this.result = result;
			this.testLinkAPIClient = testLinkAPIClient;
		}

		public void run()
		{
			// TODO Auto-generated method stub
			try
			{
				testLinkAPIClient.reportTestCaseResult(PROJECT_NAME, PLAN_NAME, strTestCaseID, BUILD_NAME, exception, result);
				System.out.println("TestCase Pass in Testlink :" + strTestCaseID);
			}
			catch (TestLinkAPIException e)
			{
				// TODO Auto-generated catch block
				System.out.println("TestCase not pass in Testlink :" + strTestCaseID);

			}
		}

	}

}
