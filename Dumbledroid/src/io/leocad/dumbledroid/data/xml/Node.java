package io.leocad.dumbledroid.data.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	public String name;
	public String text;
	public List<Node> subnodes;
	public Map<String, String> attributes;

	public Node() {
		this(null, null);
	}

	public Node(String name, String text) {
		this.name = name;
		this.text = text;
	}

	public String getAttribute(String attributeName) {
		if (this.attributes == null) {
			return null;
		}

		return this.attributes.get(attributeName);
	}

	public void setAttribute(String attributeName, String attributeValue) {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, String>();
		}
		this.attributes.put(attributeName, attributeValue);
	}

	public Node getFirstChild() {
		if (this.subnodes != null || this.subnodes.size() > 0) {
			return this.subnodes.get(0);
		}
		return null;
	}

	public void addChild(Node subNode) {
		if (this.subnodes == null) {
			this.subnodes = new ArrayList<Node>();
		}
		this.subnodes.add(subNode);
	}

	public List<Node> getChildrenByName(String nodeName) {
		List<Node> result = new ArrayList<Node>();

		if (this.subnodes == null || nodeName == null || nodeName.length() == 0) {
			return result;
		}

		for (Node node : this.subnodes) {
			if (nodeName.equalsIgnoreCase(node.name)) {
				result.add(node);
			}
		}

		return result;
	}

	public Node getChildByName(String name) {
		if (this.subnodes == null || name == null || name.length() == 0) {
			return null;
		}

		for (Node node : this.subnodes) {
			if (name.equalsIgnoreCase(node.name)) {
				return node;
			}
		}

		return null;
	}

	protected void removeAllChildren() {
		if (this.subnodes != null) {
			this.subnodes.clear();
		}
	}
}
