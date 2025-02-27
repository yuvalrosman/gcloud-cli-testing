package com.mendio.gcloud;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashTest {

    @Test
    public void testHashCommand() throws IOException, InterruptedException {
        String bucketName = "simple_bucket_for_testing"; // Replace if needed
        String objectName = "testfile.txt"; // Replace if needed
        String gcloudPath = "C:\\Users\\yuval\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\gcloud.cmd"; // Replace with your gcloud path

        String command = gcloudPath + " storage hash gs://" + bucketName + "/" + objectName;
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine;
        StringBuilder errorOutput = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorOutput.append(errorLine).append(System.lineSeparator());
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            System.err.println("gcloud storage hash command failed with exit code: " + exitCode);
            System.err.println("Error output: " + errorOutput.toString());
            fail("gcloud storage hash command failed: " + errorOutput.toString());
        }

        String outputStr = output.toString();
        System.out.println("gcloud hash output:\n" + outputStr);

        // Check for base64 encoded MD5 hash
        Pattern pattern = Pattern.compile("md5_hash:\\s*([a-zA-Z0-9+/=]+)");
        Matcher matcher = pattern.matcher(outputStr);

        assertTrue(matcher.find(), "Output does not contain md5_hash in base64 format.");
    }
}