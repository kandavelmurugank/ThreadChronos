package com.kkm.tdanalyzer.model;

public class StackFrame {
    private String methodInfo;
    public StackFrame(String m) { this.methodInfo = m; }
    public String getMethodInfo() { return methodInfo; }
}
