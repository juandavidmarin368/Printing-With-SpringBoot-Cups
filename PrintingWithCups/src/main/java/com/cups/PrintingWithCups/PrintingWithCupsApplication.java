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

    // System.out.println("it is working from here..");

  }

}
