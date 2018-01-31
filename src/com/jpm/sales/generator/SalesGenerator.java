package com.jpm.sales.generator;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates example input files for processing of sales messages based on the
 * configuration and input data.
 */
public class SalesGenerator
{
  public static void main(String[] args)
  {
    System.out.println("Starting ...");
    System.out.println("Load configuration ...");
    ConfigurationHandler configurationHandler = new ConfigurationHandler();

    try
    {
      Map config = configurationHandler.loadConfiguration();

      if (configurationHandler.validateConfiguration().equals(Boolean.FALSE))
      {
        System.out.println("ERROR: Invalid configuration");
        System.exit(0);
      }

      XmlGenerator xmlGenerator = new XmlGenerator();
      xmlGenerator.setCurrency("GBP");
      xmlGenerator.generateFiles(config);
    }
    catch (Exception ex)
    {
      Logger.getLogger(SalesGenerator.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
