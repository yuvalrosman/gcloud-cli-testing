package com.mendio.gcloud;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class SignUrlTest {

    @Test
    public void testSignUrl() throws IOException, InterruptedException {
        String bucketName = "simple_bucket_for_testing";
        String objectName = "testfile.txt";
        String keyFilePath = "C:\\Users\\yuval\\Downloads\\mend-io-gcloud-cli-testing-35e205309d27.json";
        String gcloudPath = "C:\\Users\\yuval\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\gcloud.cmd";

        String command = gcloudPath + " storage sign-url --duration=1h --private-key-file=" + keyFilePath + " gs://" + bucketName + "/" + objectName;
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String signedUrl = null;
        while ((line = reader.readLine()) != null) {
            System.out.println("gcloud output line: " + line);
            System.out.flush();
            if (line.startsWith("signed_url:")) {
                signedUrl = line.substring(11).trim();
                break;
            }
        }

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine;
        StringBuilder errorOutput = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorOutput.append(errorLine).append(System.lineSeparator());
        }

        if (!process.waitFor(60, TimeUnit.SECONDS)) {
            System.err.println("gcloud command timed out.");
            System.err.flush();
            fail("gcloud command timed out.");
        }

        System.err.println("gcloud error output: " + errorOutput.toString());
        System.err.flush();
        System.out.println("gcloud command exit code: " + process.exitValue());
        System.out.flush();

        if (process.exitValue() != 0) {
            System.err.println("gcloud storage sign-url command failed with exit code: " + process.exitValue());
            System.err.flush();
            System.err.println("Error output: " + errorOutput.toString());
            System.err.flush();

            if (errorOutput.toString().contains("No such file or directory")) {
                fail("gcloud storage sign-url command failed: File, bucket, key file, or gcloud not found. Error: " + errorOutput.toString());
            } else if (errorOutput.toString().contains("Permission denied")) {
                fail("gcloud storage sign-url command failed: Permission denied. Error: " + errorOutput.toString());
            } else if (errorOutput.toString().contains("Invalid argument")) {
                fail("gcloud storage sign-url command failed: Invalid argument (bucket or object name). Error: " + errorOutput.toString());
            } else {
                fail("gcloud storage sign-url command failed with exit code: " + process.exitValue() + ". Error: " + errorOutput.toString());
            }
        }
        if (signedUrl == null || signedUrl.equals("---")) {
            fail("Signed URL is invalid or not generated");
        }

        assertNotNull(signedUrl, "Signed URL is null");
        assertTrue(!signedUrl.isEmpty(), "Signed URL is empty");

        System.out.println("Generated Signed URL: " + signedUrl);
        System.out.flush();

        // Playwright Validation
        try (Playwright playwright = Playwright.create()) {
            System.out.println("Launching browser...");
            System.out.flush();
            Browser browser = playwright.chromium().launch();
            System.out.println("Browser launched.");
            System.out.flush();
            Page page = browser.newPage();
            System.out.println("Navigating to: " + signedUrl);
            System.out.flush();
            page.navigate(signedUrl, new Page.NavigateOptions().setTimeout(10000));
            System.out.println("Navigation complete.");
            System.out.flush();
            System.out.println("Page content: " + page.textContent("body", new Page.TextContentOptions().setTimeout(10000)));
            System.out.flush();

            assertTrue(page.textContent("body").contains("this is a test file"), "Content verification failed");
            assertTrue(!page.textContent("body").contains("phishing"), "Phishing warning detected");

            browser.close();
        }
    }
}