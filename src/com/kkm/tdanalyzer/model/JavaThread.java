package com.kkm.tdanalyzer.model;
import java.util.*;

public class JavaThread {
    private String name, state = "UNKNOWN", nativeIdHex;
    private long nativeIdDecimal;
    private List<StackFrame> stackTrace = new ArrayList<>();
    private List<MonitorEvent> monitors = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getState() { return state; }
    public void setState(String s) { this.state = s; }
    public String getNativeIdHex() { return nativeIdHex; }
    public void setNativeIdHex(String h) {
        this.nativeIdHex = h;
        try { this.nativeIdDecimal = Long.decode(h); } catch(Exception e) { this.nativeIdDecimal = -1; }
    }
    public long getNativeIdDecimal() { return nativeIdDecimal; }
    public List<StackFrame> getStackTrace() { return stackTrace; }
    public List<MonitorEvent> getMonitors() { return monitors; }
}
