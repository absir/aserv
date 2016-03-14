/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-9 下午2:29:09
 */
package com.absir.aserv.developer;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.developer.model.EntityModel;
import com.absir.aserv.developer.model.ModelFactory;
import com.absir.aserv.support.developer.IField;
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.RenderUtils;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import com.absir.orm.value.JoEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.ScripteNode;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
public class DeveloperModel {

    /**
     * ELEMENT_NAME
     */
    private static final String ELEMENT_NAME = DeveloperModel.class + "@ELEMENT_NAME";
    /**
     * NODES_NAME
     */
    private static final String NODES_NAME = DeveloperModel.class + "@NODES_NAME";
    /**
     * INPUT_NAME
     */
    private static final String INPUT_NAME = DeveloperModel.class + "@INPUT_NAME";
    /**
     * IDENTIFIER_NAME
     */
    private static final String IDENTIFIER_NAME = DeveloperModel.class + "@IDENTIFIER_NAME";
    /**
     * FIELD_NAME
     */
    private static final String FIELD_NAME = DeveloperModel.class + "@FIELD_NAME";
    /**
     * RELATIVE_PATHS
     */
    private static final String[] RELATIVE_PATHS = new String[]{""};
    /**
     * entityName
     */
    private String entityName;
    /**
     * entityModel
     */
    private EntityModel entityModel;
    /**
     * entitySuggests
     */
    private List<IField> entitySuggests;

    /**
     * @param joEntity
     * @return
     */
    public static EntityModel getModelEntity(JoEntity joEntity) {
        return ModelFactory.getModelEntity(joEntity);
    }

    /**
     * @param entityName
     * @param request
     * @return
     */
    public static DeveloperModel forEntityName(String entityName, ServletRequest request) {
        return forEntityNameClass(CrudUtils.newJoEntity(entityName, null), request);
    }

    /**
     * @param entityClass
     * @param request
     * @return
     */
    public static DeveloperModel forEntityClass(Class<?> entityClass, ServletRequest request) {
        return forEntityNameClass(CrudUtils.newJoEntity(null, entityClass), request);
    }

    /**
     * @param joEntity
     * @param request
     * @return
     */
    public static DeveloperModel forEntityNameClass(JoEntity joEntity, ServletRequest request) {
        String persistKey = DeveloperModel.class.getName() + joEntity.toString();
        Object modelObject = request.getAttribute(persistKey);
        if (modelObject == null || !(modelObject instanceof DeveloperModel)) {
            DeveloperModel developerModel = new DeveloperModel();
            developerModel.entityName = joEntity.getEntityName();
            developerModel.entityModel = ModelFactory.getModelEntity(joEntity);
            developerModel.entitySuggests = developerModel.entityModel.getGroupFields(JaEdit.GROUP_SUGGEST);
            request.setAttribute(persistKey, developerModel);
            return developerModel;
        }

        return (DeveloperModel) modelObject;
    }

    /**
     * @param request
     * @return
     */
    public static Element getElement(ServletRequest request) {
        return (Element) request.getAttribute(ELEMENT_NAME);
    }

    /**
     * @param request
     * @return
     */
    public static List<Node> getNodes(ServletRequest request) {
        return (List<Node>) request.getAttribute(NODES_NAME);
    }

    /**
     * @param request
     * @return
     */
    public static Element getElementInput(ServletRequest request) {
        return (Element) request.getAttribute(INPUT_NAME);
    }

    /**
     * @param input
     * @param request
     */
    public static void setElementInput(Element input, ServletRequest request) {
        request.setAttribute(INPUT_NAME, input);
    }

    /**
     * @param request
     * @return
     */
    public static String getIdentifier(ServletRequest request) {
        return (String) request.getAttribute(IDENTIFIER_NAME);
    }

    /**
     * @param request
     * @return
     */
    public static IField getField(ServletRequest request) {
        return (IField) request.getAttribute(FIELD_NAME);
    }

    /**
     * @param field
     * @param required
     * @return
     */
    public static boolean allow(IField field, Boolean required) {
        if (required != null) {
            return required != field.isNullable();
        }

        return true;
    }

    /**
     * @param field
     * @param include
     * @param exclude
     * @param required
     * @param nameSet
     * @return
     */
    public static boolean allow(IField field, int include, int exclude, Boolean required, Set<String> nameSet) {
        if (field.getCrudField().allow(include, exclude)) {
            if (nameSet == null) {
                return include == 0 && allow(field, required);

            } else {
                return nameSet.contains(field.getName()) && allow(field, required);
            }
        }

        return false;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the entityModel
     */
    public EntityModel getEntityModel() {
        return entityModel;
    }

    /**
     * @param div
     * @param theme
     * @param name
     * @param renders
     * @return
     * @throws IOException
     */
    public Element show(String div, String theme, String name, Object... renders) throws IOException {
        IField nameField = null;
        for (IField field : entityModel.getFields()) {
            if (field.getName().equals(name)) {
                nameField = field;
                break;
            }
        }

        for (IField field : entityModel.getPrimaries()) {
            if (field.getName().equals(name)) {
                nameField = field;
                break;
            }
        }

        if (nameField == null) {
            return null;
        }

        ServletRequest request = KernelArray.getAssignable(renders, ServletRequest.class);
        DeveloperGenerator generator = DeveloperGenerator.getDeveloperGenerator(request);
        if (generator == null) {
            generator = new DeveloperGenerator();
        }

        Document document = new Document("");
        Element element = document.appendElement(div);
        request.setAttribute(ELEMENT_NAME, element);
        request.setAttribute(FIELD_NAME, nameField);
        List<Node> nodes = ScripteNode.append(element, RenderUtils.load(theme + "show" + DeveloperUtils.suffix, renders));
        request.setAttribute(NODES_NAME, nodes);
        DeveloperUtils.includeExist(theme + "type/", nameField.getTypes(), RELATIVE_PATHS, renders);
        return element;
    }

    /**
     * 获取开发模版
     *
     * @param div
     * @param theme
     * @param subtable
     * @param group
     * @param include
     * @param exclude
     * @param required
     * @param names
     * @param renders
     * @return
     * @throws IOException
     */
    public Element generate(String div, String theme, boolean subtable, String group, int include, int exclude, Boolean required, String[] names, Object... renders) throws IOException {
        ServletRequest request = KernelArray.getAssignable(renders, ServletRequest.class);
        DeveloperGenerator generator = DeveloperGenerator.getDeveloperGenerator(request);
        if (generator == null) {
            generator = new DeveloperGenerator();
        }

        Set<String> nameSet = null;
        if (names != null) {
            nameSet = new HashSet<String>();
            for (String name : names) {
                nameSet.add(name);
            }
        }

        Document document = new Document("");
        Element element = document.appendElement(div);
        // 顶部代码
        String node = RenderUtils.loadExist(theme + "top" + DeveloperUtils.suffix, renders);
        List<Node> nodes = node == null ? null : ScripteNode.append(element, node);
        request.setAttribute(ELEMENT_NAME, element);
        request.setAttribute(NODES_NAME, nodes);
        String identifier;
        String themeType = theme + "type/";
        if (group == null) {
            for (IField field : entityModel.getPrimaries()) {
                if (allow(field, include, exclude, required, nameSet)) {
                    identifier = "name=" + "\"" + field.getName() + "\"";
                    if (!generator.append(identifier, element)) {
                        request.setAttribute(IDENTIFIER_NAME, identifier);
                        request.setAttribute(FIELD_NAME, field);
                        nodes = ScripteNode.append(element, RenderUtils.load(theme + "primary" + DeveloperUtils.suffix, renders));
                        request.setAttribute(NODES_NAME, nodes);
                        DeveloperUtils.includeExist(themeType, field.getTypes(), RELATIVE_PATHS, renders);
                    }
                }
            }
        }

        List<IField> subtableFields = subtable ? new ArrayList<IField>() : null;
        Map<String, List<IField>> subtableSubFields = subtable ? new HashMap<String, List<IField>>() : null;
        for (IField field : group == null ? entityModel.getFields() : entityModel.getGroupFields(group)) {
            if (allow(field, include, exclude, required, nameSet)) {
                if (subtable) {
                    if (field.getTypes().size() > 0 && "subtable".equals(field.getTypes().get(0))) {
                        // 关联实体字段
                        subtableFields.add(field);
                        continue;

                    } else {
                        // 关联实体索引字段
                        String subField = (String) field.getMetas().get("subField");
                        if (subField != null) {
                            List<IField> fields = subtableSubFields.get(subField);
                            if (fields == null) {
                                fields = new ArrayList<IField>();
                                subtableSubFields.put(subField, fields);
                            }

                            fields.add(field);
                            continue;
                        }
                    }

                    identifier = "name=" + "\"" + field.getName() + "\"";
                    if (!generator.append(identifier, element)) {
                        request.setAttribute(IDENTIFIER_NAME, identifier);
                        request.setAttribute(FIELD_NAME, field);
                        nodes = ScripteNode.append(element, RenderUtils.load(theme + "field" + DeveloperUtils.suffix, renders));
                        request.setAttribute(NODES_NAME, nodes);
                        DeveloperUtils.includeExist(themeType, field.getTypes(), RELATIVE_PATHS, renders);
                    }
                }
            }
        }

        // 分割代码
        node = RenderUtils.loadExist(theme + "center" + DeveloperUtils.suffix, renders);
        nodes = node == null ? null : ScripteNode.append(element, node);
        // 编辑关联实体
        if (subtable && subtableFields.size() > 0) {
            request.setAttribute("subtable", true);
            // 关联顶部代码
            node = RenderUtils.loadExist(theme + "subtop" + DeveloperUtils.suffix, renders);
            nodes = node == null ? null : ScripteNode.append(element, node);
            String themeSub = theme + "sub/";
            for (IField field : subtableFields) {
                identifier = "name=\"" + field.getName() + "-sub\"";
                if (!!generator.append(identifier, element)) {
                    request.setAttribute(IDENTIFIER_NAME, identifier);
                    request.setAttribute(FIELD_NAME, field);
                    nodes = ScripteNode.append(element, RenderUtils.load(theme + "sub" + DeveloperUtils.suffix, renders));
                    request.setAttribute(NODES_NAME, nodes);
                    DeveloperUtils.includeExist(themeSub, field.getTypes(), RELATIVE_PATHS, renders);
                }
            }

            // 关联分割代码
            node = RenderUtils.loadExist(theme + "subcenter" + DeveloperUtils.suffix, renders);
            nodes = node == null ? null : ScripteNode.append(element, node);

            for (IField field : subtableFields) {
                identifier = "name=\"" + field.getName() + "-subtable\"";
                if (!!generator.append(identifier, element)) {
                    request.setAttribute(IDENTIFIER_NAME, identifier);
                    request.setAttribute(FIELD_NAME, field);
                    nodes = ScripteNode.append(element, RenderUtils.load(theme + "subfield" + DeveloperUtils.suffix, renders));
                    request.setAttribute(NODES_NAME, nodes);
                    DeveloperUtils.includeExist(themeType, field.getTypes(), RELATIVE_PATHS, renders);
                }
            }

            // 关联底部代码
            node = RenderUtils.loadExist(theme + "subbottom" + DeveloperUtils.suffix, renders);
            nodes = node == null ? null : ScripteNode.append(element, node);
        }

        // 底部代码
        node = RenderUtils.loadExist(theme + "bottom" + DeveloperUtils.suffix, renders);
        nodes = node == null ? null : ScripteNode.append(element, node);
        // 生成代码对象再处理
        request.setAttribute("element", element);
        String path = IRender.ME.getPath(renders);
        path = KernelString.replaceLast(path, DeveloperUtils.suffix, "") + "." + entityName + "." + HelperFileName.getName(HelperFileName.getPathNoEndSeparator(theme)) + DeveloperUtils.suffix;
        node = RenderUtils.loadExist(path);
        nodes = node == null ? null : ScripteNode.append(element, node);
        // 返回处理完成代码
        return element;
    }

    /**
     * @param value
     * @return
     */
    public String suggest(String value) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("<c:set value=\"" + value + "\" var=\"value\"/>");
        suggest(out);
        return writer.toString();
    }

    /**
     * @param values
     * @return
     */
    public String suggests(String values) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("<c:set value=\"\" var=\"values\"/>");
        out.println("<c:set value=\"\" var=\"ids\"/>");
        out.println("<c:forEach items='" + values + "' var=\"value\" varStatus=\"status\">");
        out.println("<c:if test=\"${status.index > 0}\">");
        out.println("<c:set value=\"${ids},\" var=\"ids\"/>");
        out.println("<c:set value=\"${values},\" var=\"values\"/>");
        out.println("</c:if>");
        suggest(out);
        out.println("<c:set value=\"${ids}${id}\" var=\"ids\"/>");
        out.println("<c:set value=\"${values}${value}\" var=\"values\"/>");
        out.print("</c:forEach>");
        return writer.toString();
    }

    /**
     * @param out
     */
    private void suggest(PrintWriter out) {
        String values = "";
        int size = entitySuggests == null ? 0 : entitySuggests.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                values += ".";
            }

            values += "${value." + entitySuggests.get(i).getName() + "}";
        }

        out.println("<c:set value=\"${value." + entityModel.getPrimary().getName() + "}\" var=\"id\"/>");
        out.println("<c:set value=\"" + values + "\" var=\"value\"/>");
    }
}
