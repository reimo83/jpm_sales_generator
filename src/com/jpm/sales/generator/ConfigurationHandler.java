package com.jpm.sales.generator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigurationHandler
{

  public static final String NUMBER_OF_SALES = "number_of_sales";
  public static final String MIN_ITEMS_PER_SALE = "min_items_per_sale";
  public static final String MAX_ITEMS_PER_SALE = "max_items_per_sale";
  public static final String MIN_QUANTITY = "min_quantity";
  public static final String MAX_QUANTITY = "max_quantity";
  public static final String OPERATION_INTERVAL = "operation_interval";
  private Map configuration = new HashMap();
  private static final Integer HARD_LIMIT = new Integer("1001");

  public Map<String, Integer> loadConfiguration() throws Exception
  {
    configuration = new HashMap();
    Properties prop = new Properties();
    InputStream in = getClass().getResourceAsStream("config.properties");
    prop.load(in);
    in.close();

    List<String> parameterList = new ArrayList<>();
    parameterList.add(ConfigurationHandler.NUMBER_OF_SALES);
    parameterList.add(ConfigurationHandler.MAX_ITEMS_PER_SALE);
    parameterList.add(ConfigurationHandler.MIN_ITEMS_PER_SALE);
    parameterList.add(ConfigurationHandler.MAX_QUANTITY);
    parameterList.add(ConfigurationHandler.MIN_QUANTITY);
    parameterList.add(ConfigurationHandler.OPERATION_INTERVAL);

    for (String parameterName : parameterList)
    {
      this.configuration.put(parameterName, Integer.parseInt(
              prop.getProperty(parameterName))
      );
    }

    return this.configuration;
  }

  public Boolean validateConfiguration()
  {
    // Detect invalid number of sales
    if (
      (Integer)this.configuration.get(ConfigurationHandler.NUMBER_OF_SALES) < 1 ||
      ConfigurationHandler.HARD_LIMIT.compareTo(
        (Integer)this.configuration.get(ConfigurationHandler.NUMBER_OF_SALES)
      ) < 1
    )
    {
      return Boolean.FALSE;
    }

    // Detect invalid value for items per sale
    Integer min = (Integer) this.configuration.get(ConfigurationHandler.MIN_ITEMS_PER_SALE);

    if (
      (Integer) this.configuration.get(ConfigurationHandler.MIN_ITEMS_PER_SALE) < 1 ||
      ConfigurationHandler.HARD_LIMIT.compareTo(
        (Integer)this.configuration.get(ConfigurationHandler.MIN_ITEMS_PER_SALE)
      ) < 1 ||
      ConfigurationHandler.HARD_LIMIT.compareTo(
        (Integer)this.configuration.get(ConfigurationHandler.MAX_ITEMS_PER_SALE)
      ) < 1 ||
      min.compareTo((Integer)this.configuration.get(ConfigurationHandler.MAX_ITEMS_PER_SALE)) == 1
    )
    {
      return Boolean.FALSE;
    }

    // Detect invalid quantity
    min = (Integer) this.configuration.get(ConfigurationHandler.MIN_QUANTITY);

    if (
      (Integer)this.configuration.get(ConfigurationHandler.MIN_QUANTITY) < 1 ||
      ConfigurationHandler.HARD_LIMIT.compareTo(
        (Integer)this.configuration.get(ConfigurationHandler.MAX_QUANTITY)
      ) < 1 ||
      ConfigurationHandler.HARD_LIMIT.compareTo(
        (Integer)this.configuration.get(ConfigurationHandler.MIN_QUANTITY)
      ) < 1 ||
      min.compareTo((Integer)this.configuration.get(ConfigurationHandler.MAX_ITEMS_PER_SALE)) == 1
    )
    {
      return Boolean.FALSE;
    }

    // Detect invalid operation interval
    if (
      (Integer)this.configuration.get(ConfigurationHandler.OPERATION_INTERVAL) < 1 ||
      ConfigurationHandler.HARD_LIMIT.compareTo(
        (Integer)this.configuration.get(ConfigurationHandler.OPERATION_INTERVAL)
      ) < 1
    )
    {
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }
}
