// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.util.xml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;

/**
 * The {@link XmlBuilder} implements an XML based {@link NodeBuilder}. The {@link XmlBuilder} is
 * able to create pretty XML content on the {@link XMLStreamWriter} without a {@link Transformer}.
 */
public class XmlBuilder extends NodeBuilder<XmlBuilder> {

    private final XMLStreamWriter writer;

    private boolean         deferred;
    private String          namespace;


    private final Map<String, String> namespaces  = new HashMap<>();
    private final Map<String, String> attributeNs = new HashMap<>();
    private final Map<String, String> attribute   = new LinkedHashMap<>();

    /**
     * Creates a new named {@link XmlBuilder} depending optional on another instance.
     *
     * @param writer
     */
    public XmlBuilder(XMLStreamWriter writer) throws XMLStreamException {
        this.writer = writer;
        this.writer.writeStartDocument("UTF-8", "1.0");
    }

    /**
     * Get the {@link XMLStreamWriter}.
     */
    protected final XMLStreamWriter getWriter() {
        return this.writer;
    }

    /**
     * Set the current namespace.
     *
     * @param namespace
     */
    public final void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Add the namespace prefixes.
     *
     * @param prefix
     * @param namespace
     */
    public final void addNamespace(String prefix, String namespace) {
        this.namespaces.put(namespace, prefix);
    }

    /**
     * Add a XML document attribute.
     *
     * @param name
     * @param value
     * @param namespace
     */
    public final void setAttribute(String name, String value, String namespace) {
        this.attribute.put(name, value);
        if (namespace != null)
            this.attributeNs.put(name, namespace);
    }

    /**
     * Append a node on the top of the {@link NodeBuilder}.
     *
     * @param name
     */
    @Override
    public final XmlBuilder push(String name) {
        if (this.deferred)
            startElement(top(), false);
        this.deferred = true;
        super.push(name);
        return this;
    }

    /**
     * Build the top most node from {@link XmlBuilder}.
     *
     * @return pop the top node
     */
    @Override
    public final String build() {
        if (this.deferred)
            startElement(top(), true);
        else {
            try {
                getWriter().writeEndElement();
            } catch (XMLStreamException e) {
                throw new IllegalArgumentException("Failed to generate", e);
            }
        }
        return super.build();
    }

    /**
     * Writes a string value for the named attribute.
     *
     * @param name
     * @param value
     * @param empty
     */
    @Override
    public XmlBuilder set(String name, String value, boolean empty) {
        if ((value != null) && (empty || !value.isEmpty()))
            this.attribute.put(name, value);
        return this;
    }

    /**
     * Writes a comment for the next element.
     *
     * @param comment
     */
    @Override
    public XmlBuilder addComment(String comment) {
        if (this.deferred)
            startElement(top(), false);

        try {
            getWriter().writeComment(comment);
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException(String.format("Failed to write comment"), e);
        }
        return this;
    }


    /**
     * Writes a data value.
     *
     * @param value
     * @param isCData
     */
    @Override
    public final XmlBuilder addContent(String value, boolean isCData) {
        if ((value != null) && !value.isEmpty()) {
            if (this.deferred)
                startElement(top(), false);

            try {
                if (isCData) {
                    getWriter().writeCData(value);
                } else {
                    getWriter().writeCharacters(value);
                }
            } catch (XMLStreamException e) {
                throw new IllegalArgumentException(String.format("Failed to generate %s", value), e);
            }
        }
        return this;
    }

    /**
     * Closes this resource, relinquishing any underlying resources. This method is invoked
     * automatically on objects managed by the {@code try}-with-resources statement.
     */
    @Override
    public void close() {
        try {
            if (this.deferred)
                startElement(top(), true);

            getWriter().writeEndDocument();
            getWriter().flush();
            getWriter().close();
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException("Failed to close document", e);
        }
    }

    /**
     * Start the element.
     *
     * @param name
     */
    private final void startElement(String name, boolean isEmpty) {
        try {
            if (isEmpty)
                XmlBuilder.writeEmptyElement(getWriter(), name, this.namespace);
            else
                XmlBuilder.writeElement(getWriter(), name, this.namespace);

            if (count() == 1) {
                for (String ns : this.namespaces.keySet())
                    getWriter().writeNamespace(this.namespaces.get(ns), ns);
            }

            for (String key : this.attribute.keySet()) {
                try {
                    XmlBuilder.writeAttribute(getWriter(), key, this.attribute.get(key), this.attributeNs.get(key));
                } catch (XMLStreamException e) {
                    throw new IllegalArgumentException(
                            String.format("Failed to generate attribute %s=%s", key, this.attribute.get(key)), e);
                }
            }

            this.attribute.clear();
            this.deferred = false;
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException(String.format("Failed to generate %s", name), e);
        }
    }

    /**
     * Write an XML element with an optional namespace.
     *
     * @param writer
     * @param name
     * @param namespace
     */
    private static final void writeElement(XMLStreamWriter writer, String name, String namespace)
            throws XMLStreamException {
        if (namespace == null)
            writer.writeStartElement(name);
        else
            writer.writeStartElement(namespace, name);
    }

    /**
     * Write an empty XML element with an optional namespace.
     *
     * @param writer
     * @param name
     * @param namespace
     */
    private static void writeEmptyElement(XMLStreamWriter writer, String name, String namespace)
            throws XMLStreamException {
        if (namespace == null)
            writer.writeEmptyElement(name);
        else
            writer.writeEmptyElement(namespace, name);
    }

    /**
     * Write an XML attribute with an optional namespace.
     *
     * @param writer
     * @param name
     * @param value
     * @param namespace
     */
    private static void writeAttribute(XMLStreamWriter writer, String name, String value, String namespace)
            throws XMLStreamException {
        if (namespace == null) {
            writer.writeAttribute(name, value);
        } else {
            writer.writeAttribute(namespace, name, value);
        }
    }
}
