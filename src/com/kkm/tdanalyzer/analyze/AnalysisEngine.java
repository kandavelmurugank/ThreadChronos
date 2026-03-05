package com.kkm.tdanalyzer.analyze;
import com.kkm.tdanalyzer.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalysisEngine {
    public Map<String, Long> getStates(List<ThreadDump> d) {
        return d.stream().flatMap(dump -> dump.getThreads().stream()).collect(Collectors.groupingBy(JavaThread::getState, Collectors.counting()));
    }

    public Map<String, Long> getHotspots(List<ThreadDump> dumps, String targetState) {
        return dumps.stream()
                .flatMap(dump -> dump.getThreads().stream())
                .filter(t -> targetState.equals(t.getState()))
                .filter(t -> !t.getStackTrace().isEmpty())
                .map(t -> t.getStackTrace().get(0).getMethodInfo()) // Get the top method
                .filter(this::isRelevantHotspot) // Updated filter logic
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()));
    }

    private boolean isRelevantHotspot(String method) {
        // List of "Noise" patterns to ignore
        String[] idlePatterns = {
                "java.net.PlainSocketImpl.accept0",
                "java.lang.ref.Reference.waitForReferencePendingList",
                "sun.awt.windows.WToolkit.eventLoop",
                "java.net.SocketInputStream.socketRead0",
                "sun.nio.ch.EPollArrayWrapper.epollWait",
                "java.lang.Object.wait"
        };
        for (String pattern : idlePatterns) {
            if (method.contains(pattern)) {
                return false; // Filter out if it matches a noise pattern
            }
        }
        return true; // Keep it if it's likely application logic
    }

    public Map<String, Integer> getContention(List<ThreadDump> dumps) {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, String> owners = new HashMap<>();
        for (ThreadDump d : dumps) {
            d.getThreads().forEach(t -> t.getMonitors().stream()
                    .filter(m -> m.getType() == MonitorEvent.Type.LOCKED)
                    .forEach(m -> owners.put(m.getAddress(), t.getName())));
            d.getThreads().forEach(t -> t.getMonitors().stream()
                    .filter(m -> m.getType() == MonitorEvent.Type.WAITING_TO_LOCK)
                    .forEach(m -> counts.put(owners.getOrDefault(m.getAddress(), "Unknown"), counts.getOrDefault(owners.get(m.getAddress()), 0) + 1)));
        }
        return counts;
    }

    public Map<String, List<String>> getTimeline(List<ThreadDump> dumps) {
        Map<String, List<String>> timeline = new TreeMap<>();
        for (ThreadDump dump : dumps) {
            Map<String, String> current = dump.getThreads().stream().collect(Collectors.toMap(JavaThread::getName, JavaThread::getState, (a,b)->a));
            timeline.keySet().forEach(n -> timeline.get(n).add(current.getOrDefault(n, "MISSING")));
            current.forEach((n, s) -> { if(!timeline.containsKey(n)) {
                List<String> list = new ArrayList<>();
                for(int i=0; i<dumps.indexOf(dump); i++) list.add("MISSING");
                list.add(s); timeline.put(n, list);
            }});
        }
        return timeline;
    }

    private boolean isNotIdleState(String method) {
        return !method.contains("java.net.SocketInputStream.socketRead0") &&
                !method.contains("sun.nio.ch.EPollArrayWrapper.epollWait") &&
                !method.contains("WToolkit.eventLoop") && // Add this
                !method.contains("waitForReferencePendingList") && // Add this
                !method.contains("PlainSocketImpl.accept0"); // Add this
    }

    public Map<String, Long> getDeepHotspots(List<ThreadDump> dumps, String targetState) {
        return dumps.stream()
                .flatMap(dump -> dump.getThreads().stream())
                .filter(t -> targetState.equals(t.getState()))
                .map(this::findFirstApplicationFrame) // The "Deep" part
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()));
    }

    private String findFirstApplicationFrame(JavaThread thread) {
        List<StackFrame> stack = thread.getStackTrace();
        for (StackFrame frame : stack) {
            String info = frame.getMethodInfo();
            // Skip common JVM noise and infrastructure
            if (!isSystemFrame(info)) {
                return info; // Found the first line of "your" code
            }
        }
        return null;
    }

    private boolean isSystemFrame(String method) {
        return method.startsWith("java.") ||
                method.startsWith("sun.") ||
                method.startsWith("javax.") ||
                method.startsWith("jdk.") ||
                method.contains("Native Method");
    }
}
