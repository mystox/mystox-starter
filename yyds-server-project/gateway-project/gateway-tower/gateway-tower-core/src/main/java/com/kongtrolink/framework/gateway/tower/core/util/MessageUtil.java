/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.gateway.tower.core.util;


import com.kongtrolink.framework.gateway.tower.core.entity.base.Message;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MessageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);

    public static String messageToString(Message message)
            throws ParserConfigurationException, JAXBException, TransformerException {
        return messageToStringTool(message);
    }

    public static String messageToString(MessageResp message)
            throws ParserConfigurationException, JAXBException, TransformerException {
        return messageToStringTool(message);
    }

    public static String messageToStringTool(Object message)
            throws ParserConfigurationException, JAXBException, TransformerException {
        // Object 转成 DOM
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlStandalone(true);
        Marshaller marshaller = JAXBContext.newInstance(message.getClass()).createMarshaller();
        marshaller.marshal(message, document);
        // DOM 转成 String
        Source source = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        return stringWriter.getBuffer().toString();
    }

    public static <T> Object stringToMessage(String str, Class<T> classType) 
            throws JAXBException {

    	try {
    		str=str.replaceAll("\n", "");
    		StringReader reader = new StringReader(str);
    	        JAXBContext context = JAXBContext.newInstance(classType);
    	        Unmarshaller unmarshaller = context.createUnmarshaller();
    	        Object o = (T)unmarshaller.unmarshal(reader);
    	        return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
       
        return null;
    }
    
    public static Document stringToDocument(String str) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader reader = new StringReader(str);
            InputSource source = new InputSource(reader);
            return builder.parse(source);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return null;
    }
    
    private static String nodeToString(Node node) {
        NodeList childNodes = node.getChildNodes();
        List<String> nodeStrs = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node temp = childNodes.item(i);
            switch (temp.getNodeType()) {
                case Node.TEXT_NODE:
                    // 文本节点：保留非空白的文本
                    String value = temp.getNodeValue();
                    if (value != null && (!value.trim().isEmpty())) {
                        nodeStrs.add(value);
                    }
                    break;
                case Node.ATTRIBUTE_NODE:
                	break;
                case Node.COMMENT_NODE:
                	
                	break;
                case Node.DOCUMENT_NODE:
                	break;
                case Node.ELEMENT_NODE:
                    // 元素节点：保留元素标签，单独遍历其所有包含的子节点
                    NamedNodeMap attrMap = temp.getAttributes();
                    StringBuilder attrBuilder = new StringBuilder();
                    for (int j = 0; j < attrMap.getLength(); j++) {
                        Node attr = attrMap.item(j);
                        attrBuilder.append(" ").append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\"");
                    }
                    
                    StringBuilder builder = new StringBuilder();
                    if (temp.hasChildNodes()) {
                        builder.append("<").append(temp.getNodeName()).append(attrBuilder.toString()).append(">").append("!#!")
                                .append("</").append(temp.getNodeName()).append(">");
                        String childString = nodeToString(temp);
                        if("!#!".equals(childString)){
                            childString = "";
                        }
                        nodeStrs.add(builder.toString().replace("!#!", childString));
                    } else {
                        builder.append("<").append(temp.getNodeName()).append(attrBuilder.toString()).append("/>");
                        if (temp.getTextContent() != null) {
                            nodeStrs.add(builder.toString().replace("!#!", temp.getTextContent().trim()));
                        } else {
                            nodeStrs.add(builder.toString());
                        }
                    }
                    break;
                case Node.ENTITY_NODE:
                	break;
                default:
            }
        }
        StringBuilder builder = new StringBuilder();
        String temp = "!#!";
        for (String nodeStr : nodeStrs) {
            if (temp.contains("!#!")) {
                temp = temp.replace("!#!", nodeStr);
            } else {
                builder.append(temp);
                temp = nodeStr;
            }
        }
        builder.append(temp);
        return builder.toString();
    }

    public static String infoNodeToString(Node node) {
        StringBuilder builder = new StringBuilder();
        builder.append("<Info>").append(nodeToString(node)).append("</Info>");
        return builder.toString();
    }
    
}
