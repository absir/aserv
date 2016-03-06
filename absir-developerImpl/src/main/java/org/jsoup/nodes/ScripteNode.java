/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-18 上午10:00:11
 */
package org.jsoup.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;

import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class ScripteNode extends TextNode {

	/** NONE */
	public static final EscapeMode NONE = KernelObject.serializeClone(EscapeMode.extended);

	static {
		KernelObject.declaredSet(ScripteNode.NONE, "map", new HashMap<Object, Object>());
	}

	/**
	 * @param element
	 * @param html
	 * @return
	 */
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

	/**
	 * @param html
	 * @return
	 */
	public static ScripteNode node(String html) {
		return new ScripteNode("\r\n" + html);
	}

	/**
	 * @param text
	 */
	public ScripteNode(String text) {
		this(text, "");
	}

	/**
	 * @param text
	 * @param baseUri
	 */
	public ScripteNode(String text, String baseUri) {
		super(text, baseUri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsoup.nodes.TextNode#text()
	 */
	@Override
	public String text() {
		return getWholeText();
	}

	/**
	 * @param accum
	 * @param depth
	 * @param out
	 */
	@Override
	void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
		String html = getWholeText();

		if (out.prettyPrint() && siblingIndex() == 0 && parentNode instanceof Element && ((Element) parentNode).tag().formatAsBlock() && !isBlank())
			indent(accum, depth, out);
		accum.append(html);
	}
}
