package com.kkm.tdanalyzer.model;

public class MonitorEvent {
    public enum Type { LOCKED, WAITING_TO_LOCK, WAITING_ON }
    private Type type; private String address;
    public MonitorEvent(Type t, String a) { this.type = t; this.address = a; }
    public Type getType() { return type; }
    public String getAddress() { return address; }
}
