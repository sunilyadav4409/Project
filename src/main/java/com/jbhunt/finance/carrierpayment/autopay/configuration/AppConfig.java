package com.jbhunt.finance.carrierpayment.autopay.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Configuration
@EnableAutoConfiguration//(exclude = HibernateJpaAutoConfiguration.class)
public class AppConfig {

    @Bean(name = "sqlConfigMapper")
    public Properties sqlProperties() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        builderFactory.setValidating(false);
        builderFactory.setCoalescing(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(new ClassPathResource("/carrierpayment-queries.xml").getInputStream());

        NodeList nList = doc.getElementsByTagName("entry");
        Stream<Node> nodeStream = IntStream.range(0, nList.getLength()).mapToObj(nList::item);

        Properties properties = new Properties();
        nodeStream.forEach(node -> {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String key = eElement.getAttributes().getNamedItem("key").getTextContent();
                String value = eElement.getTextContent();
                properties.put(key, value.trim());
            }
        });
        return properties;
    }
}
