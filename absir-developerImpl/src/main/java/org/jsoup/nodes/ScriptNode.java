/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-18 上午10:00:11
 */
package org.jsoup.nodes;

import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ScriptNode extends TextNode {

    public static final EscapeMode NONE = KernelObject.serializeClone(EscapeMode.extended);
    protected static final Field attrKeyField = KernelReflect.declaredField(Attribute.class, "key");

    static {
        KernelObject.declaredSet(ScriptNode.NONE, "map", new HashMap<Object, Object>());
    }

    public ScriptNode(String text) {
        this(text, "");
    }

    public ScriptNode(String text, String baseUri) {
        super(text, baseUri);
    }

    public static List<Node> append(Element element, String html) {
        Validate.notNull(html);
        int start = element.childNodes().size();
        List<Node> nodes = Parser.parseFragment(html, element, element.baseUri());
        for (Node node : KernelCollection.toArray(nodes, Node.class)) {
            html = node.outerHtml();
            if (!KernelString.isEmpty(html.trim())) {
                element.appendChild(node);
            }
        }

        nodes = new ArrayList<Node>();
        int end = element.childNodes().size();
        for (; start < end; start++) {
            nodes.add(element.childNodes().get(start));
        }

        return nodes;
    }

    public static void appendNode(Element element, Node node) {
        element.appendChild(node);
    }

    public static void appendNode(Element element, Node node, int index) {
        Validate.notNull(node);
        element.addChildren(index, new Node[]{node});
    }

    public static ScriptNode node(String html) {
        return new ScriptNode("\r\n" + html);
    }

    public static String html(Element element) {
        StringBuilder accum = new StringBuilder();
        Iterator i$ = element.childNodes.iterator();

        while (i$.hasNext()) {
            Node node = (Node) i$.next();
            node.outerHtml(accum);
        }

        return accum.toString();
    }

    public static void attr(Node node, String key) {
        ScriptAttribute attribute = new ScriptAttribute(key, null);
        KernelReflect.set(attribute, attrKeyField, key);
        node.attributes.put(attribute);
    }

    public static void attr(Node node, String key, String value) {
        Attribute attribute = new Attribute(key, value);
        KernelReflect.set(attribute, attrKeyField, key);
        node.attributes.put(attribute);
    }

    @Override
    public String text() {
        return getWholeText();
    }

    @Override
    void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        String html = getWholeText();

        if (out.prettyPrint() && siblingIndex() == 0 && parentNode instanceof Element && ((Element) parentNode).tag().formatAsBlock() && !isBlank())
            indent(accum, depth, out);
        accum.append(html);
    }

    public static class ScriptAttribute extends Attribute {

        public ScriptAttribute(String key, String value) {
            super(key, "");
        }

        @Override
        public String html() {
            return getKey();
        }

        @Override
        protected void html(StringBuilder accum, Document.OutputSettings out) {
            accum.append(getKey());
        }
    }
}
