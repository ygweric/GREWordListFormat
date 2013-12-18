package com.kafeidev.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class GREWordListFormat {
	public static void main(String args[]) throws java.io.IOException,
			java.io.FileNotFoundException {
		System.out.println("hello world---begin");
		 try {
			 
			 //Get the DOM Builder Factory
			    DocumentBuilderFactory factory = 
			        DocumentBuilderFactory.newInstance();

			    //Get the DOM Builder
			    DocumentBuilder builder = factory.newDocumentBuilder();

			    //Load and Parse the XML document
			    //document contains the complete XML as a Tree.
			    Document document = 
			      builder.parse(ClassLoader.getSystemResourceAsStream("com/kafeidev/main/GRE-Wrod-7000.xml"));

			    List<Word> wordList = new ArrayList<Word>();

			    //Iterating through the nodes and extracting the data.
			    NodeList nodeList = document.getDocumentElement().getChildNodes();

			    for (int i = 0; i < nodeList.getLength(); i++) {

			      //We have encountered an <employee> tag.
			      Node node = nodeList.item(i);//item
			      if (node instanceof Element) {
			    	  Word word = new Word();
//			        emp.wordCN = node.getAttributes().getNamedItem("id").getNodeValue();

			        NodeList childNodes = node.getChildNodes();
			        for (int j = 0; j < childNodes.getLength(); j++) {
			          Node cNode = childNodes.item(j);//word,trans,tags...

			          //Identifying the child tag of employee encountered. 
			          if (cNode instanceof Element) {
			            String content = cNode.getLastChild(). getTextContent().trim();
//			            System.out.println("content:" + content);
			            String name=cNode.getNodeName();
//			            System.out.println("name:"+name);
			            if (name.equals("word")) {
			            		word.wordEN=cNode.getTextContent();
//			            		System.out.println("---word:"+cNode.getTextContent());
			            } else if(name.equals("phonetic")){
//			            		System.out.println("---phonetic:"+cNode.getTextContent());
			            		word.wordSound=cNode.getTextContent();
			            } else if(name.equals("trans")){
//			            		System.out.println("---trans:"+cNode.getTextContent());
			            		word.wordCN= cNode.getTextContent();
						}
			           
			          }
			        }
			        System.out.println("word:\n"+word);
			        wordList.add(word);
			      }

			    }
		
		 }catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
	class Word{
		  String wordEN;
		  String wordSound;
		  String wordCN;


		  @Override
		  public String toString() {
			  return wordEN+ " ["+ wordSound+ "] \n" + wordCN;
		  }
		}
