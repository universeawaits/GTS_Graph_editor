package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import static layout.DrawableNode.CIRCLE_RADIUS;


public class FileProcessor {
    private class SAXReaderHandler extends DefaultHandler {
        private GraphPane graphPane;
        private String graphName;

        private ObservableList<DrawableNode> drawableNodes;
        private ObservableList<DrawableArc> drawableArcs;

        private String name;
        private String identifier;
        private String centerX;
        private String centerY;
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
            if (!isNodesRead) {
                if ((name != null)
                        && (identifier != null)
                        && (centerX != null)
                        && (centerY != null)) {

                    int identifierInt;
                    double centerXDouble;
                    double centerYDouble;

                    try {
                        identifierInt = Integer.parseInt(identifier);
                        centerXDouble = Double.parseDouble(centerX);
                        centerYDouble = Double.parseDouble(centerY);
                    } catch (NumberFormatException ex) {
                        identifierInt = -1; // gives an exception had token
                        centerXDouble = -1;
                        centerYDouble = -1;
                    }

                    DrawableNode drawableNode = new DrawableNode(new Node(name));
                    drawableNode.getShape().setCenterX(centerXDouble);
                    drawableNode.getShape().setCenterY(centerYDouble - 5 * CIRCLE_RADIUS);
                    drawableNodes.add(drawableNode);
                    identifiers.put(identifierInt, drawableNode);

                    name = null;
                    centerX = null;
                    centerY = null;
                    identifier = null;
                }
            } else {
                if (beginIdentifier != null && endIdentifier != null && isDirected != null) {
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

                    Arc sourceArc = new Arc(
                            identifiers.get(beginIdentifierInt).getSourceNode(),
                            identifiers.get(endIdentifierInt).getSourceNode(),
                            isDirectedBoolean
                    );
                    DrawableArc drawableArc = new DrawableArc(
                            sourceArc,
                            identifiers.get(beginIdentifierInt),
                            identifiers.get(endIdentifierInt)
                    );
                    drawableArcs.add(drawableArc);

                    beginIdentifier = null;
                    endIdentifier = null;
                    isDirected = null;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String nodeText = new String(ch, start, length);

            nodeText = nodeText.replace("\n", "").trim();

            if (lastElementName.equals(XMLConstant.NAME)) {
                name = nodeText;
                return;
            }

            if (!nodeText.isEmpty()) {
                switch (lastElementName) {
                    case XMLConstant.IDENTIFIER: {
                        if (!isNodesRead) {
                            identifier = nodeText;
                        } else {
                            if (beginIdentifier == null) {
                                beginIdentifier = nodeText;
                            } else {
                                endIdentifier = nodeText;
                            }
                        }

                        break;
                    }
                    case XMLConstant.CENTER_X: {
                        centerX = nodeText;
                        break;
                    }
                    case XMLConstant.CENTER_Y: {
                        centerY = nodeText;
                        break;
                    }
                    case XMLConstant.IS_DIRECTED: {
                        isDirected = nodeText;
                        break;
                    }
                }
            }

            if (lastElementName.equals(XMLConstant.ARCS)) {
                isNodesRead = true;
            }
        }

        public GraphPane getResultGraphPane() {
            Graph graph = new Graph();

            graphPane.getDrawableNodes().addAll(drawableNodes);
            graphPane.getDrawableArcs().addAll(drawableArcs);

            for (DrawableNode drawableNode : drawableNodes) {
                graph.getNodes().add(drawableNode.getSourceNode());
                graphPane.getPane().getChildren().addAll(
                        drawableNode.getShape(), drawableNode.getName(), drawableNode.getIdentifier()
                );
            }

            for (DrawableArc drawableArc : drawableArcs) {
                graph.getArcs().add(drawableArc.getSourceArc());
                graphPane.getPane().getChildren().addAll(drawableArc.getLine(), drawableArc.getArrow());
            }

            for (DrawableNode drawableNode : drawableNodes) {
                drawableNode.getShape().toFront();
            }

            GraphController graphController = new GraphController(graph);
            graphPane.setGraphController(graphController);

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

    public GraphPane read() {
        GraphPane graphPane = new GraphPane();
        String graphName = "";

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

        graphPane = saxReaderHandler.getResultGraphPane();
        graphPane.getGraphController().getGraph().setName(saxReaderHandler.getResultGraphName());

        return graphPane;
    }
}
