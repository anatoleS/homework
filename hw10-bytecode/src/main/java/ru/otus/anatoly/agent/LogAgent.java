package ru.otus.anatoly.agent;

import java.lang.instrument.Instrumentation;

public class LogAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[LogAgent] premain started");
        inst.addTransformer(new LogClassFileTransformer(), true);
    }
}
