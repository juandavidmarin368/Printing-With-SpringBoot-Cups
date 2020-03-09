package com.cups.PrintingWithCups;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value = { "/api" })
public class MainController {

    String UPLOADED_FOLDER = System.getProperty("user.dir") + "/";

    @GetMapping("/impresion") //
    public ResponseEntity<String> getEmpresas(@RequestParam("file") MultipartFile file) {

        try {
            // System.out.println("starting the process..****");
            // System.out.println(getExtensionByApacheCommonLib(file.getOriginalFilename()));

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            System.out.println("PATH --> " + UPLOADED_FOLDER + file.getOriginalFilename());

            try {// 190.145.67.142
                print("127.0.0.1", "PI", UPLOADED_FOLDER + file.getOriginalFilename(), 1, "1", false, "");
            } catch (Exception e) {
                // TODO Auto-generated catch block e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<String>("-->", HttpStatus.OK);
    }

    private static void print(String host, String printerName, String fileName, int copies, String pages,
            boolean duplex, String attributes) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(fileName);

        CupsPrinter printer = null;
        CupsClient cupsClient = new CupsClient(host, CupsClient.DEFAULT_PORT);
        if (printerName == null) {

            printer = cupsClient.getDefaultPrinter();
        } else {
            printer = new CupsPrinter(
                    new URL("http://" + host + ":" + CupsClient.DEFAULT_PORT + "/printers/" + printerName), printerName,
                    false);
        }

        HashMap<String, String> attributeMap = new HashMap<String, String>();
        if (attributes != null) {
            attributeMap.put("job-attributes", attributes.replace("+", "#"));
        }

        PrintJob printJob = new PrintJob.Builder(fileInputStream).jobName("testJobName").userName("harald")
                .copies(copies).pageRanges(pages).duplex(duplex).attributes(attributeMap).build();

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
            throw new Exception("print error! status code: " + printRequestResult.getResultCode()
                    + " status description: " + printRequestResult.getResultDescription());

        }

    }
}