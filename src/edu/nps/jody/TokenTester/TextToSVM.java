package edu.nps.jody.TokenTester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.nps.jody.HashFinder.MembershipChecker;

public class TextToSVM 
{
	//Data Members
	Integer 										maxMapValue;
	HashMap<String, Integer>	classToIntegerMap;
	HashMap<Integer, Integer>	featureCountMap;
	final boolean 							APPEND = true;
	
	//Constructors
	TextToSVM(HashMap<String, Integer> classToIntegerMap)
	{
		this.classToIntegerMap 	= classToIntegerMap;
		
		maxMapValue			= Collections.max(classToIntegerMap.values());
	}
	
	
	TextToSVM(String classificationToIntegerMapName)
	{
		File classificationToIntegerMapFile = new File(classificationToIntegerMapName);
		
		this.classToIntegerMap		= loadClassToIntegerMapFile(classificationToIntegerMapFile);
				
		maxMapValue			= Collections.max(classToIntegerMap.values());
	}
	
	TextToSVM(File classificationToIntegerMapFile)
	{
		this.classToIntegerMap = loadClassToIntegerMapFile(classificationToIntegerMapFile);
		
		if (classToIntegerMap == null || classToIntegerMap.isEmpty())
		{
			maxMapValue = 0;
		}
		else
		{
		maxMapValue			= Collections.max(classToIntegerMap.values());
		}
	}
	
	//Methods
	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> loadClassToIntegerMapFile(File classToIntegerMapFile)
	{
		//Read classification to integer map file into HashMap
		try 
		{
			if (!classToIntegerMapFile.exists())
			{
				classToIntegerMapFile.createNewFile();
				return new HashMap<String, Integer>();
			}
			
			FileInputStream mapFileInputStream 				= new FileInputStream(classToIntegerMapFile);
			ObjectInputStream mapObjectInputStream 	= new ObjectInputStream(mapFileInputStream);
			
			return (HashMap<String, Integer>)mapObjectInputStream.readObject();
		} 
		catch (FileNotFoundException e) 
		{
			// FIXME Put a clause in here to create an empty HashMap
			e.printStackTrace();
		} 
		catch (IOException f)
		{
			//FIXME Put something clever here.
		}
		catch (ClassNotFoundException g)
		{
			//FIXME Put something clever here.
		}
		
		return null;	
	}
	public HashMap<Integer, Integer> StringMapToIntegerMap(HashMap<String, Integer> stringMap, MembershipChecker membershipChecker)
	{
		HashMap<Integer, Integer> integerMap = new HashMap<Integer, Integer>();
		
		String key;
		
		Integer keyHash;
		
		Iterator<String> iterator = stringMap.keySet().iterator();
		
		while (iterator.hasNext())
		{
			key = iterator.next();
			
			keyHash = membershipChecker.getIndex(key);
			
			integerMap.put(keyHash, stringMap.get(key));
		}
		
		return integerMap;
	}
	public String mapToString(String classification, HashMap<Integer, Integer> features)
	{
		Integer classInteger;
		
		if (classToIntegerMap == null)
		{
			classToIntegerMap = new HashMap<String, Integer>();
		}
		
		if (classToIntegerMap.containsKey(classification))
		{
			 classInteger = classToIntegerMap.get(classification);
		}
		else
		{
			maxMapValue++;
			 
			 classInteger = maxMapValue;
			 
			 classToIntegerMap.put(classification, classInteger);
		}
		
		String libSVMLine = classInteger.toString();
		
		SortedSet<Integer> sortedSet =new TreeSet<Integer>( features.keySet());
		
		Iterator<Integer> iterator =sortedSet.iterator(); 
		
		Integer featureTag;
		
		while (iterator.hasNext())
		{
			featureTag = iterator.next();
			libSVMLine = libSVMLine + " " + featureTag.toString() + ":" + features.get(featureTag).toString();
		}
		
		return libSVMLine;
	}
	
/*	public void writeInstanceToFile(String libSVMLine)
	{
		svmFileWriter.println(libSVMLine);
	}*/
	
}
