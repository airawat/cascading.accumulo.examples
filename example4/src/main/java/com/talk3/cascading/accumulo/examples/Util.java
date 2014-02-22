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


}
