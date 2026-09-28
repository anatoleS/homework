package ru.otus.anatoly.agent;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class LogMethodVisitor extends AdviceAdapter {
    private final String methodName;
    private final int access;
    private boolean hasLog = false;

    protected LogMethodVisitor(int api, MethodVisitor mv, int access, String name, String descriptor) {
        super(api, mv, access, name, descriptor);
        this.methodName = name;
        this.access = access;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if ("Lru/otus/anatoly/Log;".equals(desc)) hasLog = true;
        return super.visitAnnotation(desc, visible);
    }

    @Override
    protected void onMethodEnter() {
        if (!hasLog) return;
        
        Type[] args = Type.getArgumentTypes(methodDesc);
        int offset = (access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
        
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("executed method: " + methodName + ", param: ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
        
        for (int i = 0; i < args.length; i++) {
            int idx = offset + i;
            Type t = args[i];
            switch (t.getSort()) {
                case Type.INT: case Type.BYTE: case Type.CHAR: case Type.SHORT:
                    mv.visitVarInsn(ILOAD, idx); break;
                case Type.LONG:
                    mv.visitVarInsn(LLOAD, idx); break;
                case Type.FLOAT:
                    mv.visitVarInsn(FLOAD, idx); break;
                case Type.DOUBLE:
                    mv.visitVarInsn(DLOAD, idx); break;
                default:
                    mv.visitVarInsn(ALOAD, idx);
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", 
                t.getSort() == Type.INT ? "(I)Ljava/lang/StringBuilder;" : 
                t.getSort() == Type.LONG ? "(J)Ljava/lang/StringBuilder;" :
                t.getSort() == Type.FLOAT ? "(F)Ljava/lang/StringBuilder;" :
                t.getSort() == Type.DOUBLE ? "(D)Ljava/lang/StringBuilder;" :
                "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            
            if (i < args.length - 1) {
                mv.visitLdcInsn(", ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            }
        }
        
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
