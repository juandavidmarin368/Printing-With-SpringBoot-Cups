package com.cups.PrintingWithCups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.WhichJobsEnum;

@SpringBootApplication
public class PrintingWithCupsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrintingWithCupsApplication.class, args);

		System.out.println("it is working from here..");
		try {
			print("192.168.70.45", "juandavid", "D://zm.txt", 1, "1", false, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void print(String host, String printerName, String fileName, int copies, String pages, boolean duplex,
      String attributes) throws Exception {
    FileInputStream fileInputStream = new FileInputStream(fileName);

    CupsPrinter printer = null;
    CupsClient cupsClient = new CupsClient(host, CupsClient.DEFAULT_PORT);
    if (printerName == null) {

      printer = cupsClient.getDefaultPrinter();
    } else {
      printer = new CupsPrinter(new URL("http://" + host + ":" + CupsClient.DEFAULT_PORT + "/printers/" + printerName),
          printerName, false);
    }

    HashMap<String, String> attributeMap = new HashMap<String, String>();
    if (attributes != null) {
      attributeMap.put("job-attributes", attributes.replace("+", "#"));
    }

    PrintJob printJob = new PrintJob.Builder(fileInputStream).jobName("testJobName").userName("harald").copies(copies)
        .pageRanges(pages).duplex(duplex).attributes(attributeMap).build();

    PrintRequestResult printRequestResult = printer.print(printJob);
    if (printRequestResult.isSuccessfulResult()) {
      int jobID = printRequestResult.getJobId();

      System.out.println("file sent to " + printer.getPrinterURL() + " jobID: " + jobID);
      System.out.println("... current status = " + printer.getJobStatus(jobID));
      Thread.sleep(1000);
      System.out.println("... status after 1 sec. = " + printer.getJobStatus(jobID));

      System.out.println("Get last Printjob");
      PrintJobAttributes job = cupsClient.getJobAttributes(host, jobID);
      System.out.println("ID: " + job.getJobID() + " user: " + job.getUserName() + " url: " + job.getJobURL()
          + " status: " + job.getJobState());
    } else {
      // you might throw an exception or try to retry printing the job
      throw new Exception("print error! status code: " + printRequestResult.getResultCode() + " status description: "
          + printRequestResult.getResultDescription());

    }

  }

}
