package com.ccms.transactionreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionReportServiceApplication.class, args);
		System.out.println("Transaction Report Service started...");
	}

}
