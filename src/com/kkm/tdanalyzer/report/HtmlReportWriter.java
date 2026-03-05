package com.kkm.tdanalyzer.report;
import com.kkm.tdanalyzer.model.ThreadDump;
import java.io.*;
import java.util.*;

public class HtmlReportWriter {
    public void write(String path, Map<String, Long> states, Map<String, Long> cpu, Map<String, Integer> locks, Map<String, List<String>> timeline, int snapshots) {
        StringBuilder sb = new StringBuilder("<html><head><style>");
        sb.append("body{font-family:sans-serif;margin:40px;background:#f4f7f6;} .card{background:#fff;padding:20px;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);margin-bottom:20px;}");
        sb.append("table{width:100%;border-collapse:collapse;} th,td{border:1px solid #ddd;padding:8px;} th{background:#3498db;color:#fff;}");
        sb.append(".state-RUNNABLE{background:#2ecc71;color:#fff;} .state-BLOCKED{background:#e74c3c;color:#fff;} .state-WAITING{background:#f1c40f;}");
        sb.append("</style><script>function filter(){var val=document.getElementById('s').value.toUpperCase(); var trs=document.querySelectorAll('#tl tr'); for(var i=1;i<trs.length;i++){ var txt=trs[i].cells[0].innerText.toUpperCase(); trs[i].style.display=txt.indexOf(val)>-1?'':'none';}}</script></head><body>");

        sb.append("<h1>Thread Performance Report</h1>");

        // States Table
        sb.append("<div class='card'><h2>Thread States</h2><table><tr><th>State</th><th>Count</th></tr>");
        states.forEach((k,v) -> sb.append("<tr><td>").append(k).append("</td><td>").append(v).append("</td></tr>"));
        sb.append("</table></div>");

        // CPU Hotspots
        sb.append("<div class='card'><h2>Top CPU Hotspots</h2><table><tr><th>Method</th><th>Hits</th></tr>");
        cpu.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(10).forEach(e -> sb.append("<tr><td>").append(e.getKey()).append("</td><td>").append(e.getValue()).append("</td></tr>"));
        sb.append("</table></div>");

        // Timeline with Search
        sb.append("<div class='card'><h2>Thread Timeline</h2><input type='text' id='s' onkeyup='filter()' placeholder='Search threads...' style='width:100%;padding:10px;margin-bottom:10px;border-radius:5px;border:1px solid #ddd;'>");
        sb.append("<table id='tl'><tr><th>Thread Name</th>");
        for(int i=1; i<=snapshots; i++) sb.append("<th>S").append(i).append("</th>");
        sb.append("</tr>");
        timeline.forEach((name, list) -> {
            sb.append("<tr><td>").append(name).append("</td>");
            for(String s : list) sb.append("<td class='state-").append(s).append("'>").append(s.substring(0,1)).append("</td>");
            sb.append("</tr>");
        });
        sb.append("</table></div></body></html>");

        try (FileWriter fw = new FileWriter(path)) { fw.write(sb.toString()); } catch (IOException e) { e.printStackTrace(); }
    }
}
