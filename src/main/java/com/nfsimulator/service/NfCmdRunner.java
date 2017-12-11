package com.nfsimulator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by netstorm on 18/3/17.
 */
@Component
public class NfCmdRunner implements CommandLineRunner {
    @Value("${logTypesSupported:ACCESS}")
    private String[] logsSupported;

    @Value("${accessLogFormat}")
    private String accessLogFormat;

    @Value("${accessLogLocation:/tmp/nf-access.log}")
    private String accessLogLocation;

    @Value("${noOfSetaMsgsPerBurst:20}")
    private int noOfSetaMsgsPerBurst;

    @Value("${noOfErrorMsgsPerBurst:20}")
    private int noOfErrorMsgsPerBurst;

    @Value("${InstanceLogFormat}")
    private String InstanceLogFormat;

    @Value("${InstanceLogLocation:/tmp/nf-instance.log}")
    private String InstanceLogLocation;

    @Value("${noOfInstanceMsgsPerBurst:30}")
    private int noOfInstanceMsgsPerBurst;

    @Value("${noOfGcMsgsPerBurst:20}")
    private int noOfGcMsgsPerBurst;

    @Value("${noOfInfoMsgsPerBurst:20}")
    private int noOfInfoMsgsPerBurst;

    @Value("${noOfAccessMsgsPerBurst:20}")
    private int noOfAccessMsgsPerBurst;

    @Value("${noOfWarningMsgsPerBurst:20}")
    private int noOfWarningMsgsPerBurst;

    @Value("${waitTimeInSec:1}")
    private int waitTimeInsec;

    @Value("${setaLogLocation:/tmp/nf-seta.log}")
    private String setaLogLocation;

    @Value("${setaLogFormat}")
    private String setaLogFormat;

    @Value("${errorLogLocation:/tmp/nf-error.log}")
    private String errorLogLocation;

    @Value("${errorLogFormat}")
    private String errorLogFormat;

    @Value("${gcLogLocation:/tmp/nf-gc.log}")
    private String gcLogLocation;

    @Value("${gcLogFormat}")
    private String gcLogFormat;

    @Value("${infoLogLocation:/tmp/nf-info.log}")
    private String infoLogLocation;

    @Value("${infoLogFormat}")
    private String infoLogFormat;

    @Value("${warningLogLocation:/tmp/nf-warning.log}")
    private String warningLogLocation;

    @Value("${warningLogFormat}")
    private String warningLogFormat;

    @Value("${writeToFile:true}")
    private boolean writeToFile;

    @Override
    public void run(String... args) throws Exception {
        List<String> supportedLogsList = Arrays.asList(logsSupported);

        ExecutorService executorService = Executors.newFixedThreadPool(logsSupported.length);

        if (supportedLogsList.contains("ACCESSLOG")){
            LogWorkers accessWorker = new LogWorkers(accessLogLocation, accessLogFormat, "ACCESSLOG", waitTimeInsec, noOfAccessMsgsPerBurst, writeToFile);
            executorService.submit(accessWorker);
        }
        if (supportedLogsList.contains("SETALOG")) {
            LogWorkers setaWorker = new LogWorkers(setaLogLocation, setaLogFormat, "SETALOG", waitTimeInsec, noOfSetaMsgsPerBurst, writeToFile);
            executorService.submit(setaWorker);
        }
        if (supportedLogsList.contains("ERRORLOG")) {
            LogWorkers errorWorker = new LogWorkers(errorLogLocation, errorLogFormat, "ERRORLOG", waitTimeInsec, noOfErrorMsgsPerBurst, writeToFile);
            executorService.submit(errorWorker);
        }
        if (supportedLogsList.contains("GCLOG")) {
            LogWorkers gcWorker = new LogWorkers(gcLogLocation, gcLogFormat, "GCLOG", waitTimeInsec, noOfGcMsgsPerBurst, writeToFile);
            executorService.submit(gcWorker);
        }
        if (supportedLogsList.contains("INFOLOG")) {
            LogWorkers gcWorker = new LogWorkers(infoLogLocation, infoLogFormat, "INFOLOG", waitTimeInsec, noOfInfoMsgsPerBurst, writeToFile);
            executorService.submit(gcWorker);
        }
        if (supportedLogsList.contains("INSTANCELOG")) {
            LogWorkers instanceWorker = new LogWorkers(InstanceLogLocation,InstanceLogFormat,"INSTANCELOG",waitTimeInsec,noOfInstanceMsgsPerBurst,writeToFile);
            executorService.submit(instanceWorker);
        }
        if (supportedLogsList.contains("WARNINGLOG")) {
            LogWorkers gcWorker = new LogWorkers(warningLogLocation, warningLogFormat, "WARNINGLOG", waitTimeInsec,noOfWarningMsgsPerBurst, writeToFile);
            executorService.submit(gcWorker);
        }
    }
}
