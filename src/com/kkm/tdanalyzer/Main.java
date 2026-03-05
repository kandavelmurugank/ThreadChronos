package com.kkm.tdanalyzer;
import com.kkm.tdanalyzer.analyze.AnalysisEngine;
import com.kkm.tdanalyzer.model.ThreadDump;
import com.kkm.tdanalyzer.parse.ThreadDumpParser;
import com.kkm.tdanalyzer.report.HtmlReportWriter;
import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String dirPath = "./thread_dumps";
        File folder = new File(dirPath);
        File[] files = folder.listFiles((d, n) -> n.endsWith(".txt") || n.endsWith(".dump"));

        if (files == null || files.length == 0) {
            System.out.println("Place your .txt thread dump files in the folder: " + folder.getAbsolutePath());
            return;
        }

        ThreadDumpParser parser = new ThreadDumpParser();
        List<ThreadDump> dumps = new ArrayList<>();
        Arrays.sort(files, Comparator.comparing(File::getName)); // Ensure time order
        for (File f : files) dumps.add(parser.parse(f));

        AnalysisEngine engine = new AnalysisEngine();


        // 1. Ensure the 'report' directory exists
        File reportDir = new File("report");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
            }

        // 2. Generate a timestamped filename
        // Format: yyyy-MM-dd_HHmmss -> e.g., 2026-03-06_143005.html
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new java.util.Date());
            String fileName = "AnalysisReport_" + timestamp + ".html";
            String filePath = "report/" + fileName;

        // 3. Generate the report
        HtmlReportWriter reporter = new HtmlReportWriter();
        reporter.write(filePath,
                engine.getStates(dumps),
                engine.getHotspots(dumps, "RUNNABLE"),
                engine.getContention(dumps),
                engine.getTimeline(dumps),
                files.length);

        System.out.println("Success! Report saved to: " + filePath);
    }
}
