package io.leocad.dumbledroid.data.xml;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {

	private Node root;
	private Stack<Node> elementStack;
	private StringBuffer currentElementText;

	public SaxHandler() {

		this.elementStack = new Stack<Node>();
		this.currentElementText = new StringBuffer();
	}

	public Node getRoot() {
		return root;
	}

	public void startDocument() {

		this.root = null;
		this.elementStack.clear();
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {
		
		Node currentElement = new Node();
		currentElement.name = name;

		int attsLen = atts.getLength();
		for (int i = 0; i < attsLen; i++) {
			currentElement.setAttribute(atts.getLocalName(i), atts.getValue(i));
		}

		if (this.elementStack.isEmpty()) {
			this.root = currentElement;
		} else {
			this.elementStack.peek().addChild(currentElement);
		}

		this.elementStack.push(currentElement);

		this.currentElementText.setLength(0);
	}

	public void characters (char ch[], int start, int length) {
		for (int i = start; i < start + length; i++) {
			this.currentElementText.append(ch[i]);
		}
	}

	public void endElement(String uri, String name, String qName) {
		
		Node currentElement = this.elementStack.pop();

		String currentText = this.currentElementText.toString();
		currentText = currentText.replaceAll("\\n", " ");
		currentText = currentText.replaceAll("\\t", "");
		currentElement.text = currentText;
	}

	public void endDocument() {}
}
