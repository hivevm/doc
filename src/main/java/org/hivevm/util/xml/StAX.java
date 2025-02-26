// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.util.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The {@link StAX} utility provides an XML parser.
 */
public abstract class StAX {

    private static final XMLInputFactory    READER_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory   WRITER_FACTORY = XMLOutputFactory.newInstance();
    private static final TransformerFactory TRANSFORMER    = TransformerFactory.newInstance();

    static {
        StAX.READER_FACTORY.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);

        StAX.WRITER_FACTORY.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        if (StAX.WRITER_FACTORY.isPropertySupported("com.ctc.wstx.useDoubleQuotesInXmlDecl")) {
            StAX.WRITER_FACTORY.setProperty("com.ctc.wstx.useDoubleQuotesInXmlDecl", Boolean.TRUE);
        }

        StAX.TRANSFORMER.setAttribute("indent-number", 2);
    }

    /**
     * Constructs a(n) {@link StAX} object.
     */
    private StAX() {
    }

    /**
     * Creates an instance of {@link XMLStreamReader}.
     *
     * @param stream
     */
    public static XMLStreamReader createReader(InputStream stream) throws XMLStreamException {
        return StAX.READER_FACTORY.createXMLStreamReader(stream, "UTF-8");
    }

    /**
     * Creates an instance of {@link XMLStreamWriter}.
     *
     * @param writer
     */
    public static XMLStreamWriter createWriter(Writer writer) throws XMLStreamException {
        return StAX.WRITER_FACTORY.createXMLStreamWriter(writer);
    }

    /**
     * Creates an instance of {@link XMLStreamWriter}.
     *
     * @param writer
     * @param indent
     */
    public static XMLStreamWriter createWriter(Writer writer, int indent) throws XMLStreamException {
        XMLStreamWriter xmlWriter = StAX.createWriter(writer);
        return (indent == 0) ? xmlWriter : (XMLStreamWriter) Proxy.newProxyInstance(XMLStreamWriter.class.getClassLoader(),
                new Class[]{XMLStreamWriter.class}, new XmlPrettyWriter(xmlWriter, indent));
    }

    /**
     * Create a {@link StAX} from the a resource.
     *
     * @param stream
     * @param handler
     */
    public static void parse(InputStream stream, Handler handler) throws IOException {
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(stream);

            StringBuffer buffer = null;
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement start = event.asStartElement();
                        handler.handleEvent(start.getName().getLocalPart(), StAX.parseAttributes(start));
                        buffer = new StringBuffer();
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        EndElement end = event.asEndElement();
                        handler.handleEvent(end.getName().getLocalPart(),
                                buffer == null ? null : buffer.toString());
                        buffer = new StringBuffer();
                        break;

                    case XMLStreamConstants.CDATA:
                    case XMLStreamConstants.CHARACTERS:
                        if (buffer != null) {
                            buffer.append(event.asCharacters().getData());
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    /**
     * Parses the attributes from {@link StartElement}.
     *
     * @param elem
     */
    private static Attributes parseAttributes(StartElement elem) {
        Map<String, String> attributes = new HashMap<>();
        Iterator<Attribute> iterator = elem.getAttributes();
        while (iterator.hasNext()) {
            QName name = iterator.next().getName();
            String value = elem.getAttributeByName(name).getValue();
            attributes.put(name.getLocalPart(), value);
        }
        return new Attributes(attributes);
    }

    /**
     * The {@link Handler} class.
     */
    public interface Handler {

        void handleEvent(String name, Attributes attributes);

        void handleEvent(String name, String content);
    }

    public static class Attributes {

        private final Map<String, String> attrs;

        private Attributes(Map<String, String> attrs) {
            this.attrs = attrs;
        }

        public final boolean isSet(String name) {
            return this.attrs.containsKey(name);
        }

        public final String get(String name) {
            return this.attrs.get(name);
        }

        public final String get(String name, String value) {
            return isSet(name) ? this.attrs.get(name) : value;
        }

        public final boolean getBool(String name) {
            return Boolean.parseBoolean(get(name, "false"));
        }

        public final void onAttribute(String name, Consumer<String> consumer) {
            if (this.attrs.containsKey(name)) {
                consumer.accept(this.attrs.get(name));
            }
        }
    }
}
