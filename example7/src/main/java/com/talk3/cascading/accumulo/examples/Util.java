package com.talk3.cascading.accumulo.examples;

import cascading.tuple.Fields;
import java.util.HashMap;

public class Util {

    public static Fields buildFieldList(String jobPropertyValue) 
	{
		Fields retVal=new Fields();
		for (String field : jobPropertyValue.split(",")) {
			retVal = retVal.append(new Fields(field.trim()));
		}
		return retVal;
	}
	
	public static HashMap<String,String> getMap(String jobPropertyKey,String jobPropertyValue)
	{
                HashMap<String,String> map = new HashMap<String,String>();
		for (String keyValuePair : jobPropertyValue.split("&")) 
		{
			String[] keyValueArray = keyValuePair
					.split("=");
		
			String key= null;
			String value = null;
			
			if(keyValueArray.length > 1)
			{
				key=keyValueArray[0].trim();
				value=keyValueArray[1].trim();
				
			}
			else if(keyValueArray.length ==1)
			{
				key=keyValueArray[0];
				value="";
			}
			
			if(!key.equals(null) && !value.equals(null))
			{
				map.put(key,value);
			}
			
		}
		return map;
	}
	
	public static String parseError(String jobPropertyValue, String type)
	{
		String retVal="";
		try{
			if(type.equals("int"))
			{
				int i=Integer.parseInt(jobPropertyValue);			}
		}catch(Exception e)
		{
			retVal = "Error";
		}
		return retVal;
		
	}
	
	public static int getFieldIndex(Fields fieldList, String specificFieldName)
	{
		int index =0;
		
		if(fieldList.contains(new Fields(specificFieldName)))
			index=fieldList.getPos(specificFieldName);
		else
			index = -1;
		
		
		return index;
	}
        
        public static String getValueFromMysqlConnectionString(
			String inputConnectionString, String key) {
		// Sample inputConnectionString
		// "jdbc:mysql://107.23.xx.xx:3306/talk3?user=accumulo&password=xxxxxx&autoReconnect=true&removeAbandonedTimeout=60";
		String value = "";

		String[] urlParts = inputConnectionString.split("\\?");
		if (urlParts.length > 1) {
			for (String param : urlParts[1].split("&")) {
				String[] pair = param.split("=");
				if (pair[0].toLowerCase().equals(key.toLowerCase())) {
					value = pair[1];
				}
			}
		}

		return value;
	}

	public static String[] getValueArrayFromMysqlConnectionString(
			String inputConnectionString, String key) {

		// Sample inputConnectionString
		// "jdbc:mysql://107.23.xx.xx:3306/talk3?user=accumulo&password=xxxxxx&autoReconnect=true&removeAbandonedTimeout=60";
		String[] arrListMySqlDetail = null;

		String[] urlParts = inputConnectionString.split("\\?");
		if (urlParts.length > 1) {
			for (String param : urlParts[1].split("&")) {
				String[] pair = param.split("=");
				if (pair[0].toLowerCase().equals(key.toLowerCase())) {
					if (pair.length > 0) {
						String[] arrayOfElements = pair[1].split(",");
						arrListMySqlDetail = new String[arrayOfElements.length];

						for (int i = 0; i < arrayOfElements.length; i++) {
							arrListMySqlDetail[i] = arrayOfElements[i];
						}
					}

				}
			}
		}
		
		return arrListMySqlDetail;
	}

	public static String getMySqlConnectionString(String inputConnectionString) {
		// Used to discard unwanted elements in the input string
		// resulting in a mysql connection string like-
		// "jdbc:mysql://107.23.xx.xx:3306/talk3?user=accumulo&password=xxxxxx&autoReconnect=true&removeAbandonedTimeout=60";
		StringBuilder connBuilder = new StringBuilder();
		String[] urlParts = inputConnectionString.split("\\?");

		for (int i = 0; i < urlParts.length; i++) {
			if (i == 0) {
				connBuilder.append(urlParts[i]).append("?");
			} else {
				for (String param : urlParts[1].split("&")) {
					if (!(param.indexOf("tableName") > -1
							|| param.indexOf("colNames") > -1
							|| param.indexOf("primaryKey") > -1 || param
							.indexOf("colDefinition") > -1)) {
						connBuilder.append(param).append("&");
					}
				}
			}
		}
		return connBuilder.toString().substring(0,
				connBuilder.toString().length() - 1);
	}



}
