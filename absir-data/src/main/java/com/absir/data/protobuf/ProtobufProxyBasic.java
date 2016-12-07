package com.absir.data.protobuf;

import com.baidu.bjf.remoting.protobuf.CodeGenerator;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by absir on 2016/12/8.
 */
public final class ProtobufProxyBasic {

    public static final ThreadLocal<Boolean> DEBUG_CONTROLLER = new ThreadLocal();
    private static final Logger LOGGER = Logger.getLogger(ProtobufProxy.class.getName());
    private static final Map<String, Codec> CACHED = new HashMap();
    public static final ThreadLocal<File> OUTPUT_PATH = new ThreadLocal();

    public ProtobufProxyBasic() {
    }

    public static void dynamicCodeGenerate(OutputStream os, Class cls, Charset charset) throws IOException {
        if(cls == null) {
            throw new NullPointerException("Parameter cls is null");
        } else if(os == null) {
            throw new NullPointerException("Parameter os is null");
        } else {
            if(charset == null) {
                charset = Charset.defaultCharset();
            }

            CodeGenerator cg = getCodeGenerator(cls);
            String code = cg.getCode();
            os.write(code.getBytes(charset));
        }
    }

    private static CodeGenerator getCodeGenerator(Class cls) {
        if(!cls.isMemberClass()) {
            try {
                cls.getConstructor(new Class[0]);
            } catch (NoSuchMethodException var4) {
                throw new IllegalArgumentException("Class \'" + cls.getName() + "\' must has default constructor method with no parameters.", var4);
            } catch (SecurityException var5) {
                throw new IllegalArgumentException(var5.getMessage(), var5);
            }
        }

        List fields = FieldUtils.findMatchedFields(cls, Protobuf.class);
        if(fields.isEmpty()) {
            throw new IllegalArgumentException("Invalid class [" + cls.getName() + "] no field use annotation @" + Protobuf.class.getName() + " at class " + cls.getName());
        } else {
            List fieldInfos = ProtobufProxyUtils.processDefaultValue(fields);
            CodeGenerator cg = new CodeGeneratorBasic(fieldInfos, cls);
            return cg;
        }
    }

    public static <T> Codec<T> create(Class<T> cls) {
        Boolean debug = (Boolean)DEBUG_CONTROLLER.get();
        if(debug == null) {
            debug = Boolean.valueOf(false);
        }

        return create(cls, debug.booleanValue(), (File)null);
    }

    public static void compile(Class<?> cls, File outputPath) {
        if(outputPath == null) {
            throw new NullPointerException("Param \'outputPath\' is null.");
        } else if(!outputPath.isDirectory()) {
            throw new RuntimeException("Param \'outputPath\' value should be a path directory.");
        }
    }

    public static <T> Codec<T> create(Class<T> cls, boolean debug) {
        return create(cls, debug, (File)null);
    }

    public static <T> Codec<T> create(Class<T> cls, boolean debug, File path) {
        DEBUG_CONTROLLER.set(Boolean.valueOf(debug));
        OUTPUT_PATH.set(path);

        Codec var3;
        try {
            var3 = doCreate(cls, debug);
        } finally {
            DEBUG_CONTROLLER.remove();
            OUTPUT_PATH.remove();
        }

        return var3;
    }

    private static ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null?contextClassLoader:null;
    }

    protected static <T> Codec<T> doCreate(Class<T> cls, boolean debug) {
        if(cls == null) {
            throw new NullPointerException("Parameter cls is null");
        } else {
            long lastModify = ClassHelper.getLastModifyTime(cls);
            String uniClsName = cls.getName();
            Codec codec = (Codec)CACHED.get(uniClsName);
            if(codec != null) {
                return codec;
            } else {
                CodeGenerator cg = getCodeGenerator(cls);
                cg.setDebug(debug);
                File path = (File)OUTPUT_PATH.get();
                cg.setOutputPath(path);
                String className = cg.getFullClassName();
                Class c = null;

                try {
                    c = Class.forName(className, true, getClassLoader());
                } catch (ClassNotFoundException var21) {
                    c = null;
                }

                if(c != null) {
                    try {
                        Codec code1 = (Codec)c.newInstance();
                        return code1;
                    } catch (InstantiationException var17) {
                        throw new RuntimeException(var17.getMessage(), var17);
                    } catch (IllegalAccessException var18) {
                        throw new RuntimeException(var18.getMessage(), var18);
                    }
                } else {
                    String code = cg.getCode();
                    if(debug) {
                        CodePrinter.printCode(code, "generate protobuf proxy code");
                    }

                    FileOutputStream fos = null;
                    if(path != null && path.isDirectory()) {
                        String newClass = "";
                        if(className.indexOf(46) != -1) {
                            newClass = StringUtils.substringBeforeLast(className, ".");
                        }

                        String e = path + File.separator + newClass.replace('.', File.separatorChar);
                        File e1 = new File(e);
                        e1.mkdirs();

                        try {
                            fos = new FileOutputStream(new File(e1, cg.getClassName() + ".class"));
                        } catch (Exception var20) {
                            throw new RuntimeException(var20.getMessage(), var20);
                        }
                    }

                    Class newClass1 = JDKCompilerHelper.getJdkCompiler().compile(className, code, cls.getClassLoader(), fos, lastModify);
                    if(fos != null) {
                        try {
                            fos.close();
                        } catch (IOException var19) {
                            throw new RuntimeException(var19.getMessage(), var19);
                        }
                    }

                    try {
                        Codec e2 = (Codec)newClass1.newInstance();
                        if(!CACHED.containsKey(uniClsName)) {
                            CACHED.put(uniClsName, e2);
                        }

                        try {
                            Set e3 = cg.getRelativeProxyClasses();
                            Iterator i$ = e3.iterator();

                            while(i$.hasNext()) {
                                Class relativeClass = (Class)i$.next();
                                create(relativeClass, debug, path);
                            }
                        } catch (Exception var22) {
                            LOGGER.log(Level.FINE, var22.getMessage(), var22.getCause());
                        }

                        return e2;
                    } catch (InstantiationException var23) {
                        throw new RuntimeException(var23.getMessage(), var23);
                    } catch (IllegalAccessException var24) {
                        throw new RuntimeException(var24.getMessage(), var24);
                    }
                }
            }
        }
    }

    public static void clearCache() {
        CACHED.clear();
    }
}
