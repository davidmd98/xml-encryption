package isdcm.xml.encryption;

import java.io.File;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;

public class XmlEncryption {

    private final String seed = "seed";
    private XMLCipher xmlCipher;
    private SecretKey symmetricKey;
    private static final byte[] fixedIV = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 };

    public XmlEncryption() {
        org.apache.xml.security.Init.init();
        symmetricKey = generateAESKey(seed);
        try{
            xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        } catch(Exception e){
            System.out.println("Error:" + e.getMessage());
        }
    }

    public void encryptXML(String inputFile) {
        try {
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(fixedIV);
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);
            boolean encryptContentsOnly = false;
            Node node = convertFileToNode("src\\main\\resources\\decrypted\\" + inputFile);
            xmlCipher.doFinal(node.getOwnerDocument(), (Element) node, encryptContentsOnly);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(node.getOwnerDocument());
            StreamResult result = new StreamResult(new File("src\\main\\resources\\encrypted\\" + inputFile));
            transformer.transform(source, result);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
    
    public void decryptXML(String inputFile) {
    try {
        xmlCipher.init(XMLCipher.DECRYPT_MODE, symmetricKey);
        Node node = convertFileToNode("src\\main\\resources\\encrypted\\" + inputFile);
        xmlCipher.doFinal(node.getOwnerDocument(), (Element) node, false);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(node.getOwnerDocument());
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
    } catch (Exception e) {
        System.out.println("Error decrypting:" + e.getMessage());
        e.printStackTrace();
        }
    }

    private static Node convertFileToNode(String filePath) throws Exception {
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        return doc.getDocumentElement();
    }

    private static SecretKey generateAESKey(String seed) {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed.getBytes());
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, random);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }
}