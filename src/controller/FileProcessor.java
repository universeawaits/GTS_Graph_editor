package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import layout.DrawableArc;
import layout.DrawableNode;
import layout.form.GraphPane;
import model.Arc;
import model.Graph;
import model.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FileProcessor {
    private class SAXReaderHandler extends DefaultHandler {
        private GraphPane graphPane;
        private String graphName;

        private ObservableList<DrawableNode> drawableNodes;
        private ObservableList<DrawableArc> drawableArcs;

        private String name;
        private String identifier;
        private Map<Integer, DrawableNode> identifiers;

        private String lastElementName;
        private String beginIdentifier;
        private String endIdentifier;
        private String isDirected;
        private boolean isNodesRead;


        public SAXReaderHandler(GraphPane graphPane, String graphName) {
            this.graphPane = graphPane;
            this.graphName = graphName;

            drawableNodes = FXCollections.observableArrayList();
            drawableArcs = FXCollections.observableArrayList();

            identifiers = new HashMap<>();

            isNodesRead = false;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            lastElementName = qName;

            if (attributes.getLength() != 0 && qName.equals(XMLConstant.GRAPH)) {
                graphName = attributes.getValue(XMLConstant.NAME);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String[] param = { name, identifier };

            for (String parameter : param) {
                if (parameter == null || parameter.isEmpty()) {
                    return;
                }
            }

            if (!isNodesRead) {
                int identifierInt;

                try {
                    identifierInt = Integer.parseInt(identifier);
                } catch (NumberFormatException ex) {
                    identifierInt = -1; // gives an exception had token
                }

                DrawableNode drawableNode = new DrawableNode(new Node(name));
                drawableNodes.add(drawableNode);
                identifiers.put(identifierInt, drawableNode);

                name = null;
            } else {
                if (!("".equals(beginIdentifier) || "".equals(endIdentifier)) || "".equals(isDirected)) {
                    int beginIdentifierInt;
                    int endIdentifierInt;
                    boolean isDirectedBoolean = isDirected.equals(XMLConstant.TRUE);

                    try {
                        beginIdentifierInt = Integer.parseInt(beginIdentifier);
                        endIdentifierInt = Integer.parseInt(endIdentifier);
                    } catch (NumberFormatException ex) {
                        beginIdentifierInt = -1; // gives an exception had token
                        endIdentifierInt = -1;
                    }

                    drawableArcs.add(new DrawableArc(
                            new Arc(
                                    identifiers.get(beginIdentifierInt).getSourceNode(),
                                    identifiers.get(endIdentifierInt).getSourceNode(),
                                    isDirectedBoolean),
                            identifiers.get(beginIdentifierInt),
                            identifiers.get(endIdentifierInt)
                    ));

                    beginIdentifier = null;
                    endIdentifier = null;
                    isDirected = null;
                }
            }

            identifier = null;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String nodeText = new String(ch, start, length);

            nodeText = nodeText.replace("\n", "").trim();

            if (!nodeText.isEmpty()) {
                if (lastElementName.equals(XMLConstant.NAME)) {
                    name = nodeText;
                }

                if (lastElementName.equals(XMLConstant.IDENTIFIER)) {
                    identifier = nodeText;
                }

                if (lastElementName.equals(XMLConstant.ARCS)) {
                    isNodesRead = true;
                }

                if (lastElementName.equals(XMLConstant.BEGIN_NODE)) {
                    beginIdentifier = nodeText;
                }

                if (lastElementName.equals(XMLConstant.END_NODE)) {
                    endIdentifier = nodeText;
                }
            }
        }

        public GraphPane getResultGraphPane() {
            graphPane.getDrawableNodes().addAll(drawableNodes);
            graphPane.getDrawableArcs().addAll(drawableArcs);

            for (DrawableNode drawableNode : drawableNodes) {
                graphPane.getPane().getChildren().add(drawableNode.getShape());
            }

            for (DrawableArc drawableArc : drawableArcs) {
                graphPane.getPane().getChildren().addAll(drawableArc.getLine(), drawableArc.getArrow()); // mb nullexption
            }

            return graphPane;
        }

        public String getResultGraphName() {
            return graphName;
        }
    }


    private String filePath;


    public FileProcessor(String filePath) {
        this.filePath = filePath;
    }

    public void write(GraphPane graphPane, String graphName)  {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return;
        }

        Document document = documentBuilder.newDocument();

        Element root = document.createElement(XMLConstant.GRAPH);
        root.setAttribute(XMLConstant.NAME, graphName);
        document.appendChild(root);

        Element nodes = document.createElement(XMLConstant.NODES);
        Element arcs = document.createElement(XMLConstant.ARCS);

        for (DrawableNode node : graphPane.getDrawableNodes()) {
            Element name = document.createElement(XMLConstant.NAME);
            Element identifier = document.createElement(XMLConstant.IDENTIFIER);

            Element centerX = document.createElement(XMLConstant.CENTER_X);
            Element centerY = document.createElement(XMLConstant.CENTER_Y);

            name.appendChild(document.createTextNode(node.getSourceNode().getName()));
            identifier.appendChild(document.createTextNode(String.valueOf(node.getSourceNode().getIdentifier())));
            centerX.appendChild(document.createTextNode(String.valueOf(node.getShape().getCenterX())));
            centerY.appendChild(document.createTextNode(String.valueOf(node.getShape().getCenterY())));

            Element drawableNode = document.createElement(XMLConstant.NODE);
            drawableNode.appendChild(name);
            drawableNode.appendChild(identifier);
            drawableNode.appendChild(centerX);
            drawableNode.appendChild(centerY);

            nodes.appendChild(drawableNode);
        }

        for (DrawableArc arc : graphPane.getDrawableArcs()) {
            Element beginNode = document.createElement(XMLConstant.BEGIN_NODE);
            Element beginNodeIdentifier = document.createElement(XMLConstant.IDENTIFIER);

            Element endNode = document.createElement(XMLConstant.END_NODE);
            Element endNodeIdentifier = document.createElement(XMLConstant.IDENTIFIER);
            Element isDirected = document.createElement(XMLConstant.IS_DIRECTED);

            beginNodeIdentifier.appendChild(
                    document.createTextNode(String.valueOf(arc.getSourceArc().getBegin().getIdentifier())));
            endNodeIdentifier.appendChild(
                    document.createTextNode(String.valueOf(arc.getSourceArc().getEnd().getIdentifier())));
            isDirected.appendChild(
                    document.createTextNode(arc.getSourceArc().isDirected() ? XMLConstant.TRUE : XMLConstant.FALSE)
            );

            beginNode.appendChild(beginNodeIdentifier);
            endNode.appendChild(endNodeIdentifier);

            Element drawableArc = document.createElement(XMLConstant.ARC);
            drawableArc.appendChild(beginNode);
            drawableArc.appendChild(endNode);
            drawableArc.appendChild(isDirected);

            arcs.appendChild(drawableArc);
        }

        root.appendChild(nodes);
        root.appendChild(arcs);

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

    public Pair<String, GraphPane> read() {
        GraphPane graphPane = new GraphPane();
        String graphName;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;

        try {
            parser = factory.newSAXParser();
        } catch (SAXException | ParserConfigurationException ex) {
            ex.printStackTrace();
            return null;
        }

        SAXReaderHandler saxReaderHandler = new SAXReaderHandler(graphPane, graphName);

        try {
            parser.parse(new File(filePath), saxReaderHandler);
        } catch (SAXException | IOException ex) {
            ex.printStackTrace();
        }

        graphName = saxReaderHandler.getResultGraphName();
        graphPane = saxReaderHandler.getResultGraphPane();

        return new Pair<>(graphName, graphPane);
    }
}
