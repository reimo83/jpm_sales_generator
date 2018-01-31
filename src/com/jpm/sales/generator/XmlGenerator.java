package com.jpm.sales.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generate XML files based on configuration and input data for processing sales
 * data.
 */
public class XmlGenerator
{
  private String currency = "EUR";
  private Map<String, Float> inputData = new HashMap();
  private List<String> operationTypeList = new ArrayList<>();

  /**
   * Initializing of class variables.
   */
  public XmlGenerator()
  {
    operationTypeList.add("add");
    operationTypeList.add("multiply");
    operationTypeList.add("subtract");
  }

  /**
   * Process XML files creation based on the configuration.
   *
   * @param config
   */
  public void generateFiles(Map config)
  {
    this.loadInputData("input.csv");
    Boolean isOperationDefined;
    Integer operationIntervalCounter;

    for (int i = 1; i <= (Integer)config.get(ConfigurationHandler.NUMBER_OF_SALES); i++)
    {
      operationIntervalCounter = i % (Integer)config.get(ConfigurationHandler.OPERATION_INTERVAL);
      isOperationDefined = (operationIntervalCounter.compareTo(0) == 0) ? Boolean.TRUE : Boolean.FALSE;

      this.generateXmlFile(
        this.randomNumberGenerator(
          (Integer)config.get(ConfigurationHandler.MIN_ITEMS_PER_SALE),
          (Integer)config.get(ConfigurationHandler.MAX_ITEMS_PER_SALE)
        ),
        isOperationDefined,
        i,
        (Integer)config.get(ConfigurationHandler.MIN_QUANTITY),
        (Integer)config.get(ConfigurationHandler.MAX_QUANTITY)
      );
    }
  }

  /**
   * Generate single XML file based on the input parameters.
   *
   * @param numberOfItems Defines number of item tags for the sale
   * @param isOperationDefined Operation tag is added when this value is TRUE
   * @param numberOfFile Sequence number for the generated files
   * @param minQuantity Defines minimum value for product quantity
   * @param maxQuantity Defines maximum value for product quantity
   */
  public void generateXmlFile(
    Integer numberOfItems,
    Boolean isOperationDefined,
    Integer numberOfFile,
    Integer minQuantity,
    Integer maxQuantity
  )
  {
    try
    {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      Document doc = docBuilder.newDocument();
      doc.setXmlStandalone(true);
      Element rootElement = doc.createElement("sale");
      doc.appendChild(rootElement);

      Element items = doc.createElement("items");
      rootElement.appendChild(items);

      Element item;
      Attr productType;
      Attr price;
      Attr currency;
      Attr quantity;
      String randomProductType;
      Float randomProductPrice;

      for (int i = 1; i <= numberOfItems; i++)
      {
        item = doc.createElement("item");
        items.appendChild(item);

        randomProductType = this.randomProductTypeGenerator();
        randomProductPrice = this.inputData.get(randomProductType);

        productType = doc.createAttribute("productType");
        productType.setValue(randomProductType);
        item.setAttributeNode(productType);

        price = doc.createAttribute("price");
        price.setValue(randomProductPrice.toString());
        item.setAttributeNode(price);

        currency = doc.createAttribute("currency");
        currency.setValue(this.currency);
        item.setAttributeNode(currency);

        quantity = doc.createAttribute("quantity");
        quantity.setValue(this.randomNumberGenerator(minQuantity, maxQuantity).toString());
        item.setAttributeNode(quantity);
      }

      if (isOperationDefined.equals(Boolean.TRUE))
      {
        Element operations = doc.createElement("operations");
        rootElement.appendChild(operations);

        Element operation = doc.createElement("operation");
        operations.appendChild(operation);

        Attr operationType = doc.createAttribute("type");
        operationType.setValue(this.randomOperationTypeGenerator());
        operation.setAttributeNode(operationType);

        randomProductType = this.randomProductTypeGenerator();
        randomProductPrice = this.inputData.get(randomProductType);

        Attr productTypeForOperation = doc.createAttribute("productType");
        productTypeForOperation.setValue(randomProductType);
        operation.setAttributeNode(productTypeForOperation);

        Attr currencyForOperation = doc.createAttribute("currency");
        currencyForOperation.setValue(this.currency);
        operation.setAttributeNode(currencyForOperation);

        Attr priceForOperation = doc.createAttribute("price");
        priceForOperation.setValue(randomProductPrice.toString());
        operation.setAttributeNode(priceForOperation);
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File("file" + numberOfFile + ".xml"));
      transformer.transform(source, result);

      System.out.println("File saved!");

    }
    catch (Exception ex)
    {
      Logger.getLogger(SalesGenerator.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Generate random number in the range from min to max.
   *
   * @param min Minimum value for randomly generated number
   * @param max Maximum value for randomly generated number
   * @return
   */
  private Integer randomNumberGenerator(Integer min, Integer max)
  {
    Random rand = new Random();

    return rand.nextInt(max) + min;
  }

  /**
   * Returns random value for product type.
   *
   * @return
   */
  private String randomProductTypeGenerator()
  {
    Random random = new Random();
    List<String> keyList = new ArrayList<>(this.inputData.keySet());
    String productType = keyList.get(random.nextInt(keyList.size()));

    return productType;
  }

  /**
   * Returns random value for operation type.
   *
   * @return
   */
  private String randomOperationTypeGenerator()
  {
    Random random = new Random();
    String operationType = operationTypeList.get(random.nextInt(operationTypeList.size()));

    return operationType;
  }

  /**
   * Load input data from the CSV data. First column includes product type and
   * the second one includes the price of the product.
   *
   * @param csvFile CSV file with the data for generating XML files
   */
  private void loadInputData(String csvFile)
  {
    String line = "";
    String splitBy = ",";

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
    {
      while ((line = br.readLine()) != null)
      {
        String[] data = line.split(splitBy);
        this.inputData.put(data[0], new Float(data[1]));
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Returns current currency defined for data processing.
   *
   * @return
   */
  public String getCurrency()
  {
    return this.currency;
  }

  /**
   * Change the currency for data processing.
   *
   * @param currency Three letter international currency code
   */
  public void setCurrency(String currency)
  {
    this.currency = currency;
  }
}
