package com.kkm.tdanalyzer.parse;
import com.kkm.tdanalyzer.model.*;
import java.io.*;
import java.util.regex.*;

public class ThreadDumpParser {
    private static final Pattern START = Pattern.compile("^\"(.*)\".*nid=(0x[0-9a-fA-F]+)");
    private static final Pattern STATE = Pattern.compile("Thread\\.State: (\\w+)");
    private static final Pattern STACK = Pattern.compile("^\\s+at (.*)");
    private static final Pattern LOCK  = Pattern.compile("- (locked|waiting to lock|waiting on) <(0x[0-9a-f]+)>");

    public ThreadDump parse(File file) {
        ThreadDump dump = new ThreadDump();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; JavaThread current = null;
            while ((line = br.readLine()) != null) {
                Matcher m = START.matcher(line);
                if (m.find()) {
                    current = new JavaThread();
                    current.setName(m.group(1));
                    current.setNativeIdHex(m.group(2));
                    dump.getThreads().add(current);
                    continue;
                }
                if (current != null) {
                    Matcher sm = STATE.matcher(line);
                    if (sm.find()) current.setState(sm.group(1));
                    Matcher stm = STACK.matcher(line);
                    if (stm.find()) current.getStackTrace().add(new StackFrame(stm.group(1)));
                    Matcher lm = LOCK.matcher(line);
                    if (lm.find()) {
                        MonitorEvent.Type t = MonitorEvent.Type.valueOf(lm.group(1).toUpperCase().replace(" ", "_"));
                        current.getMonitors().add(new MonitorEvent(t, lm.group(2)));
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return dump;
    }
}
