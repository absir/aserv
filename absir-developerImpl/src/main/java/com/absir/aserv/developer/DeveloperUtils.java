/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.developer.model.EntityModel;
import com.absir.aserv.developer.model.ModelFactory;
import com.absir.aserv.support.DeveloperBreak;
import com.absir.aserv.support.developer.IDeveloper;
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.IRenderSuffix;
import com.absir.aserv.support.developer.RenderUtils;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Value;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilContext;
import com.absir.orm.value.JoEntity;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Configure
public class DeveloperUtils {

    protected static final String DIY = DeveloperUtils.class.getName() + "@DIY";

    private static final String DEVELOPER = "developer/";

    private static final int DEVELOPER_LENGTH = DEVELOPER.length();

    @Value("developer.direct")
    protected static boolean direct = true;

    @Value("developer.suffix")
    protected static String suffix = getSuffix();

    private static Map<String, Object> Generator_Map_Token = new HashMap<String, Object>();

    private static Set<Object> Generator_Tokens = new HashSet<Object>();

    private static String getSuffix() {
        IRenderSuffix renderSuffix = BeanFactoryUtils.get(IRenderSuffix.class);
        if (renderSuffix == null) {
            if (IRender.ME != null && IRender.ME instanceof IRenderSuffix) {
                renderSuffix = (IRenderSuffix) IRender.ME;
            }
        }

        String suffix = renderSuffix == null ? null : renderSuffix.getSuffix();
        if (KernelString.isEmpty(suffix)) {
            suffix = ".dev";
        }

        return suffix;
    }

    public static String getGeneratePath(String generatePath) {
        int prefix = generatePath.indexOf("/", 1) + 1;
        if (generatePath.startsWith(DEVELOPER, prefix)) {
            return generatePath.substring(0, prefix) + generatePath.substring(prefix + DEVELOPER_LENGTH);
        }

        return generatePath;
    }

    public static String getDeveloperPath(String includePath) {
        int prefix = includePath.indexOf("/", 1) + 1;
        if (includePath.startsWith(DEVELOPER, prefix)) {
            return includePath;
        }

        return includePath.substring(0, prefix) + DEVELOPER + includePath.substring(prefix);
    }

    public static void clearToken(String filePath) {
        UtilAbsir.clearToken(filePath, Generator_Map_Token);
    }

    protected static void generateRenders(String filePath, String includePath, Object... renders) throws IOException {
        ServletRequest request = KernelArray.getAssignable(renders, ServletRequest.class);
        if (request != null) {
            generate(filePath, includePath, request, renders);
        }
    }

    public static void diy(ServletRequest request, boolean diy) {
        request.setAttribute(DIY, diy);
    }

    public static void setJoEntity(String entityName, Class<?> entityClass, HttpServletRequest request) {
        request.setAttribute("joEntity", new JoEntity(entityName, entityClass));
    }

    public static void setEntityModel(String entityName, Class<?> entityClass, HttpServletRequest request) {
        JoEntity joEntity = new JoEntity(entityName, entityClass, true);
        request.setAttribute("joEntity", joEntity);
        request.setAttribute("entityModel", ModelFactory.getModelEntity((JoEntity) joEntity));
    }

    public static void generate(String filePath, String includePath, ServletRequest request, Object... renders) throws IOException {
        if (IRender.ME == null) {
            return;
        }

        try {
            // 实体生成信息
            Object joEntity = request.getAttribute("joEntity");
            if (joEntity == null || !(joEntity instanceof JoEntity)) {
                Object value = request.getAttribute("entityName");
                String entityName = value != null && value instanceof String ? (String) value : null;
                if (entityName == null) {
                    entityName = request.getParameter("entityName");
                }

                value = request.getAttribute("entityClass");
                Class<?> entityClass = value != null && value instanceof Class ? (Class<?>) value : null;
                if (entityName != null || entityClass != null) {
                    joEntity = CrudUtils.newJoEntity(entityName, entityClass);
                    request.setAttribute("joEntity", joEntity);
                }
            }

            filePath = getGeneratePath(filePath);
            File file = new File(IRender.ME.getRealPath(filePath));
            EntityModel entityModel = joEntity == null ? null : ModelFactory.getModelEntity((JoEntity) joEntity);
            // DIY生成
            if (request.getAttribute(DIY) == null) {
                // 非关联实体生成
                if (entityModel == null) {
                    joEntity = null;
                    if (BeanFactoryUtils.getEnvironment() != Environment.DEVELOP && Generator_Map_Token.containsKey(filePath)) {
                        joEntity = Boolean.TRUE;
                    }
                }

                // 如果生成文件没过期
                if (!direct && file.exists()) {
                    if (entityModel == null) {
                        if (joEntity != null) {
                            return;
                        }

                    } else if (entityModel.lastModified() != null && entityModel.lastModified() <= file.lastModified()) {
                        return;
                    }
                }
            }

            // 检测开发文件是否存在
            includePath = getDeveloperPath(includePath);
            if (!new File(IRender.ME.getRealPath(includePath)).exists()) {
                return;
            }

            Object token = UtilAbsir.getToken(filePath, Generator_Map_Token);
            try {
                synchronized (token) {
                    if (!Generator_Tokens.add(token)) {
                        return;
                    }

                    final DeveloperGenerator generator = DeveloperGenerator.pushDeveloperGenerator(request);
                    try {
                        // 读取原文件定义信息
                        StringBuilder fileBuilder = new StringBuilder();
                        if (file.exists()) {
                            final StringBuilder readBuilder = fileBuilder;
                            final ObjectTemplate<String> gDefine = new ObjectTemplate<String>(null);
                            try {
                                HelperFile.doWithReadLine(file, new CallbackBreak<String>() {

                                    @Override
                                    public void doWith(String template) throws BreakException {
                                        if (readBuilder.length() > 0) {
                                            readBuilder.append("\r\n");
                                        }

                                        readBuilder.append(template);
                                        String define = template.trim();
                                        if (define.contains("<%-- G_DEFINED")) {
                                            throw new DeveloperBreak();
                                        }

                                        int pos = define.indexOf("<%-- G_TAG[");
                                        if (pos >= 0) {
                                            int start = pos + "<%-- G_TAG[".length();
                                            int nPos = define.indexOf("]", start);
                                            String tag = nPos < start ? define.substring(start) : define.substring(start, nPos);
                                            generator.setTag(tag);

                                        } else {
                                            pos = define.indexOf("<%-- G_BEGAN");
                                            if (pos >= 0) {
                                                if (gDefine.object == null) {
                                                    gDefine.object = "\r\n";
                                                }
                                            }

                                            if (gDefine.object != null) {
                                                gDefine.object += template + "\r\n";
                                                if (define.endsWith("G_END --%>")) {
                                                    generator.addGeneratorDefine(gDefine.object);
                                                    gDefine.object = null;
                                                }
                                            }
                                        }
                                    }
                                });

                            } catch (IOException e) {
                                // 显示生成错误
                                if (BeanFactoryUtils.getBeanConfig().getEnvironment() == Environment.DEVELOP) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        // 获取生成文件流
                        FileOutputStream output = HelperFile.openOutputStream(file,
                                direct || entityModel == null ? null : entityModel.lastModified());
                        ByteArrayOutputStream outputStream = null;
                        if (output != null) {
                            try {
                                generator.setTag(null);
                                outputStream = new ByteArrayOutputStream();
                                request.setAttribute("entityModel", entityModel);
                                IRender.ME.rend(outputStream, includePath, renders);
                                String dev = IRender.ME.dev(UtilContext.getCurrentTime());
                                if (!KernelString.isEmpty(dev)) {
                                    HelperIO.write(dev, output);
                                    HelperIO.write("\r\n", output);
                                }

                                HelperIO.write(outputStream.toByteArray(), output);
                                fileBuilder = null;

                                // 复制生成文件到开发环境
                                IDeveloper.ME.copyDeveloper(file, filePath);

                            } finally {
                                if (output != null) {
                                    if (fileBuilder != null) {
                                        HelperIO.write(fileBuilder.toString(), output);
                                        if (outputStream != null) {
                                            if (BeanFactoryUtils.getBeanConfig().getEnvironment() == Environment.DEVELOP) {

                                            }
                                        }
                                    }

                                    output.close();
                                }

                                if (outputStream != null) {
                                    HelperIO.closeQuietly(outputStream);
                                }
                            }
                        }

                    } finally {
                        DeveloperGenerator.popDeveloperGenerator(request);
                    }
                }

            } finally {
                synchronized (token) {
                    Generator_Tokens.remove(token);
                }

                clearToken(filePath);
            }

        } catch (Exception e) {
            // 显示生成错误
            if (BeanFactoryUtils.getBeanConfig().getEnvironment() == Environment.DEVELOP && !(e instanceof DeveloperBreak)) {
                e.printStackTrace();
            }
        }
    }

    public static void generate(String filePath, Object... renders) throws IOException {
        generate(filePath, IRender.ME.getPath(renders), renders);
    }

    public static void generate(String filePath, String includePath, Object... renders) throws IOException {
        generateRenders(IRender.ME.getFullPath(filePath, renders), includePath, renders);
    }

    public static void includeExist(String option, List<String> types, Object... renders) throws IOException {
        includeExist(option, types, new String[]{"/WEB-INF/developer/type/"}, renders);
    }

    public static void includeExist(String option, List<String> types, String[] relativePaths, Object... renders)
            throws IOException {
        ServletRequest request = KernelArray.getAssignable(renders, ServletRequest.class);
        for (String relativePath : relativePaths) {
            if (request != null) {
                request.removeAttribute(relativePath + option);
            }

            RenderUtils.includeExist(relativePath + option + "/base" + suffix, renders);
            for (String type : types) {
                if (RenderUtils.includeExist(relativePath + option + "/" + type + suffix, renders)) {
                    request.setAttribute(relativePath + option, type);
                    break;
                }
            }

            RenderUtils.includeExist(relativePath + option + "/type" + suffix, renders);
        }
    }

    public static void includeExistName(String option, String entityName, Object... renders) throws IOException {
        includeExistName(option, entityName, new String[]{"/WEB-INF/developer/bean/"}, renders);
    }

    public static void includeExistName(String option, String entityName, String[] relativePaths, Object... renders) throws IOException {
        for (String relativePath : relativePaths) {
            RenderUtils.includeExist(relativePath + option + "/" + entityName + suffix, renders);
        }
    }
}
