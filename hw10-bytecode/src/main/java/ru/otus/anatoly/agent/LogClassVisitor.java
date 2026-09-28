package ru.otus.anatoly.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class LogClassVisitor extends ClassVisitor {
    public LogClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                    String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new LogMethodVisitor(api, mv, access, name, descriptor);
    }
}
