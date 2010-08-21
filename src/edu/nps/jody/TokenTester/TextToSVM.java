package edu.nps.jody.TokenTester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class TextToSVM 
{
	//Data Members
	//File												svmDataFile;
	//File 												classificationToIntegerMapFile;
	Integer 										maxMapValue;
	HashMap<String, Integer>	classToIntegerMap;
	HashMap<Integer, Integer>	featureCountMap;
	PrintWriter 								svmFileWriter;
	final boolean 							APPEND = true;
	
	//Constructors
	TextToSVM(String svmDataFileName, HashMap<String, Integer> classToIntegerMap)
	{
		File svmDataFile 				= new File(svmDataFileName);
		this.classToIntegerMap = classToIntegerMap;
		
		readyLibSVMFileWriter(svmDataFile);
		
		maxMapValue			= Collections.max(classToIntegerMap.values());
	}
	
	TextToSVM(File svmDataFile, HashMap<String, Integer> classToIntegerMap)
	{
		//this.svmDataFile 				= svmDataFile;
		this.classToIntegerMap 	= classToIntegerMap;
		
		readyLibSVMFileWriter(svmDataFile);
		
		maxMapValue			= Collections.max(classToIntegerMap.values());
	}
	
	
	TextToSVM(String svmDataFileName, String classificationToIntegerMapName)
	{
		File svmDataFile									= new File(svmDataFileName);
		File classificationToIntegerMapFile = new File(classificationToIntegerMapName);
		
		this.classToIntegerMap		= loadClassToIntegerMapFile(classificationToIntegerMapFile);
		
		readyLibSVMFileWriter(svmDataFile);
		
		maxMapValue			= Collections.max(classToIntegerMap.values());
	}
	
	TextToSVM(File svmDataFile, File classificationToIntegerMapFile)
	{
		this.classToIntegerMap = loadClassToIntegerMapFile(classificationToIntegerMapFile);
		
		readyLibSVMFileWriter(svmDataFile);
		
		maxMapValue			= Collections.max(classToIntegerMap.values());
	}
	
	//Methods
	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> loadClassToIntegerMapFile(File classToIntegerMapFile)
	{
		//Read classification to integer map file into HashMap
		try 
		{
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
	
	private void readyLibSVMFileWriter(File svmDataFile)
	{
		try 
		{
			//Using FileWriter vice just FIle so I can invoke APPEND
			svmFileWriter = new PrintWriter( new FileWriter(svmDataFile, APPEND));
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Put in a code block to create the file if it does not already exist.
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//TODO put something clever here
		}
	}
	public String addInstance(String classification, HashMap<String, Integer> classMap, HashMap<Integer, Integer> features)
	{
		Integer classInteger;
		
		if (classMap.containsKey(classification))
		{
			 classInteger = classMap.get(classification);
		}
		else
		{
			maxMapValue++;
			 
			 classInteger = maxMapValue;
		}
		
		String libSVMLine = classInteger.toString();
		
		Iterator<Integer> iterator = features.keySet().iterator();
		
		Integer featureTag;
		
		while (iterator.hasNext())
		{
			featureTag = iterator.next();
			libSVMLine.concat(featureTag.toString() + ":" + features.get(featureTag).toString());
		}
		
		return libSVMLine;
	}
	
}
