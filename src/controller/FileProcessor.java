package controller;

import layout.DrawableArc;
import layout.DrawableNode;
import layout.form.GraphPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;


public class FileProcessor {
    /*private class SAXReaderHandler extends DefaultHandler {
        private GraphPane graphPane;

        private String name;
        private String firstName;
        private String middleName;
        private String lastName;
        private String publishingName;
        private String volumeCount;
        private String circulation;

        private String lastElementName;

        public SAXReaderHandler(GraphPane graphPane) {
            this.graphPane = graphPane;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            lastElementName = qName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String[] param = { name, lastName, firstName, middleName, publishingName, volumeCount, circulation };

            for (String parameter : param) {
                if (parameter == null || parameter.isEmpty()) {
                    return;
                }
            }


            int volumeCountInt;
            int circulationInt;

            try {
                volumeCountInt = Integer.parseInt(volumeCount);
                circulationInt = Integer.parseInt(circulation);
            } catch (NumberFormatException ex) {
                volumeCountInt = -1; // gives an exception had token
                circulationInt = -1;
            }

            books.add(new Book(name, firstName, middleName, lastName, publishingName, volumeCountInt, circulationInt));

            name = null;
            lastName = null;
            firstName = null;
            middleName = null;
            publishingName = null;
            volumeCount = null;
            circulation = null;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String nodeText = new String(ch, start, length);

            nodeText = nodeText.replace("\n", "").trim();

            if (!nodeText.isEmpty()) {
                if (lastElementName.equals(ControllerConstant.NAME_FIELD)) {
                    name = nodeText;
                }

                if (lastElementName.equals(ControllerConstant.LAST_NAME_XML_FIELD)) {
                    lastName = nodeText;
                }

                if (lastElementName.equals(ControllerConstant.FIRST_NAME_XML_FIELD)) {
                    firstName = nodeText;
                }

                if (lastElementName.equals(ControllerConstant.MIDDLE_NAME_XML_FIELD)) {
                    middleName = nodeText;
                }

                if (lastElementName.equals(ControllerConstant.PUBLISHING_NAME_XML_FIELD)) {
                    publishingName = nodeText;
                }

                if (lastElementName.equals(ControllerConstant.CIRCULATION_FIELD)) {
                    circulation = nodeText;
                }

                if (lastElementName.equals(ControllerConstant.VOLUME_COUNT_XML_FIELD)) {
                    volumeCount = nodeText;
                }
            }
        }
    }*/


    private String filePath;


    public FileProcessor(String filePath) {
        this.filePath = filePath;
    }

    public void write(GraphPane graphPane)  {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return;
        }

        Document document = documentBuilder.newDocument();

        Element root = document.createElement(XMLConstant.ROOT);
        document.appendChild(root);

        for (DrawableNode node : graphPane.getDrawableNodes()) {
            Element name = document.createElement(XMLConstant.NAME);
            Element identifier = document.createElement(XMLConstant.IDENTIFIER);

            Element centerX = document.createElement(XMLConstant.CENTER_X);
            Element centerY = document.createElement(XMLConstant.CENTER_Y);

            name.appendChild(document.createTextNode(node.getSourceNode().getName()));
            identifier.appendChild(document.createTextNode(String.valueOf(node.getSourceNode().getIdentifier())));
            centerX.appendChild(document.createTextNode(String.valueOf(node.getShape().getCenterX())));
            centerY.appendChild(document.createTextNode(String.valueOf(node.getShape().getCenterY())));

            Element drawableNode = document.createElement(XMLConstant.DRAWABLE_NODE);
            drawableNode.appendChild(name);
            drawableNode.appendChild(identifier);
            drawableNode.appendChild(centerX);
            drawableNode.appendChild(centerY);

            root.appendChild(drawableNode);
        }

        for (DrawableArc arc : graphPane.getDrawableArcs()) {
            Element beginNode = document.createElement(XMLConstant.BEGIN_NODE);
            Element beginNodeIdentifier = document.createElement(XMLConstant.IDENTIFIER);
            Element endNode = document.createElement(XMLConstant.END_NODE);
            Element endNodeIdentifier = document.createElement(XMLConstant.IDENTIFIER);

            beginNodeIdentifier.appendChild(
                    document.createTextNode(String.valueOf(arc.getSourceArc().getBegin().getIdentifier())));
            endNodeIdentifier.appendChild(
                    document.createTextNode(String.valueOf(arc.getSourceArc().getEnd().getIdentifier())));

            beginNode.appendChild(beginNodeIdentifier);
            endNode.appendChild(endNodeIdentifier);

            Element drawableArc = document.createElement(XMLConstant.DRAWABLE_ARC);
            drawableArc.appendChild(beginNode);
            drawableArc.appendChild(endNode);

            root.appendChild(drawableArc);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;

        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
    }

    /*public void read(ObservableList<Book> books) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;

        try {
            parser = factory.newSAXParser();
        } catch (SAXException | ParserConfigurationException ex) {
            ex.printStackTrace();
            return;
        }

        try {
            parser.parse(new File(filePath), new SAXReaderHandler(books));
        } catch (SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }*/
}
