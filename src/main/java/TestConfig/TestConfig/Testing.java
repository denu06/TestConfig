package TestConfig.TestConfig;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
import testlink.api.java.client.TestLinkAPIResults;

public class Testing
{

	public static void main()
	{
		try
		{
			System.out.println("Testing project");
			String Filepath = System.getProperty("FilePath");
			System.out.println("FilePath :" + Filepath);

			String DEV_KEY = System.getProperty("DEV_KEY");
			System.out.println("FilePath :" + Filepath);

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

					TestLinkAPIClient testLinkAPIClient = new TestLinkAPIClient(DEV_KEY, SERVER_URL);
					testLinkAPIClient.reportTestCaseResult(PROJECT_NAME, PLAN_NAME, strTestCaseID, BUILD_NAME, exception, result);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
