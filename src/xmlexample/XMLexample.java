package xmlexample;

import java.lang.reflect.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xmlsaxparser.*;

public class XMLexample {

    private BaseElement root;
    
    public BaseElement getRoot(){
        return root;
    }  
    public void recurse(Node node, BaseElement elem){
        BaseElement current = null;
        if (node instanceof Element){
            try {
                Class c = Class.forName(node.getNodeName());
                Object element = c.newInstance();
                if (element instanceof BaseElement)
                    current = (BaseElement)element;
                if (elem!=null) elem.add(current);
                else root=current;
                System.out.println(current);
                
                NamedNodeMap nm = node.getAttributes();
                for (int i=0; i<nm.getLength(); i++){
                    try{
                        Attr attr = (Attr) nm.item(i);
                        String attrname = attr.getName();
                        String value = attr.getValue();
                        Method m = c.getMethod("set" + attrname, new Class[]{String.class});
                        System.out.printf("method %s \n", m);
                        m.invoke(current, new Object[]{value});
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } 
            catch (ClassNotFoundException ex) {
                Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(XMLexample.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (node instanceof org.w3c.dom.Text){
            String text =  ((org.w3c.dom.Text)node).getData();
            if (text!=null && elem!=null){
                try{
                    elem.setLabel(((org.w3c.dom.Text)node).getData());
                }
                catch(UnsupportedOperationException e){
                   
                }
            }            
        }
        NodeList nl = node.getChildNodes();
        for (int i=0; i<nl.getLength(); i++){
            recurse(nl.item(i), current);
        }
    }
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory = 
            DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("test.xml");
            XMLexample obj = new XMLexample();
            //ReaderDOM obj = new ReaderDOM();
            obj.recurse(document, null);
            UTIL.draw(obj.getRoot(), "XMLDopParser");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }
    
}
