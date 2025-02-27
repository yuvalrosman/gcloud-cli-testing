# GCloud Storage Tests

This is my implementation of automated testing for the gcloud storage commands.

## What You'll Need (The Essentials)

* **Java (JDK 8+):** You'll need Java installed. Think of it as the engine for these tests. Grab it from Oracle or use OpenJDK if you prefer.
* **Maven:** This is your project manager. It helps download all the stuff we need and build the tests. You can get it from the Apache Maven site.
* **Google Cloud SDK (gcloud):** This is your ticket to talk to Google Cloud. Get it from their site and make sure it's set up with your account.
* **A Google Cloud Project:** You'll need a project with a storage bucket and a test file ready to go.
* **Service Account Key (JSON):** This is like a special password that lets the tests sign URLs. You'll need one with the right permissions.


## Let's Start

1.  **Grab the Code:**
    * Open Git Bash.
    * Go to where you want the project to live.
    * Run `git clone <repository_url>`.

2.  **Dive into the Project:**
    * Run `cd gcloud-storage-tests`.

3.  **Make it Your Own:**
    * **Edit `SignUrlTest.java`:** You'll see some placeholders:
        * `bucketName`: Put in your bucket's name.
        * `objectName`: Put in your test file's name.
        * `keyFilePath`: Point to your service account key file.
        * `gcloudPath` and `mvnPath`: Double check these if your gcloud or maven are installed in a non-standard location.
    * **Check Your Files:** Make sure your test file and key file are where you said they'd be.

4.  **Run the Tests!**
    * In Git Bash, type `mvn clean test -U` and hit Enter.
    * Maven will do its thing, and you'll see the test results pop up in the console.

## What's Going On?

* We're using TestNG to run the tests and check if everything's working.
* Playwright helps us automate the browser and see if the signed URLs work.
* The tests use gcloud to make a signed URL and then check if it works in a browser.

## Got Issues?

* **"Permission denied (publickey)"**: Check your SSH keys for GitHub.
* **"gcloud command timed out"**: Make sure gcloud is installed and working.
* **"Content verification failed"**: Double-check the content of your test file.

Have fun!
