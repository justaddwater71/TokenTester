package edu.nps.jody.TokenTester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;

import edu.nps.jody.HashFinder.MembershipChecker;

public class EnronUtteranceReader 
{
	//Data Members
	String								userName;
	Reader							reader;
	BufferedReader 			bufferedReader;
	PrintWriter						libSVMTextWriter;
	TextToSVM						textToSVM;
	WordTokenizer				wordTokenizer;
	MembershipChecker	membershipChecker;
	final int							MAXGAP	= 4;
	
	//Constructors
	EnronUtteranceReader(File libSVMDestination, TextToSVM textToSVM, WordTokenizer wordTokenizer, MembershipChecker membershipChecker) throws IOException
	{
		try 
		{
			libSVMTextWriter = new PrintWriter(libSVMDestination);
		} 
		catch (FileNotFoundException e) 
		{
			libSVMDestination.createNewFile();
			libSVMTextWriter = new PrintWriter(libSVMDestination);
		}
		
		this.textToSVM 					= textToSVM;
		this.wordTokenizer 			= wordTokenizer;
		this.membershipChecker 	= membershipChecker;
	}
	
	//Methods
	public void recurseUtteranceFiles(String fileName, boolean oneForOne)
	{
		File file = new File(fileName);
		String[] fileList;
		//FIXME This is for initial monitoring only, remove once code is functioning
		System.out.println("Evaluating " + fileName);
		
		if (file.isDirectory())
		{
			fileList = file.list();
			
			String absolutePath = file.getAbsolutePath();;
			
			for (int i = 0; i < fileList.length;i++)
			{
				fileList[i] = absolutePath +"/" +  fileList[i];
			}
			
			for (int j = 0; j < fileList.length;j++)
			{
				processUtteranceFile(fileList[j]);
			}
		}
		else if (file.isFile())
		{
			processUtteranceFile(file, oneForOne);
		}
	}
	
	public void recurseUtteranceFIles(String fileName)
	{
		File file = new File(fileName);
		String[] fileList;
		//FIXME This is for initial monitoring only, remove once code is functioning
		System.out.println("Evaluating " + fileName);
		
		if (file.isDirectory())
		{
			fileList = file.list();
			
			String absolutePath = file.getAbsolutePath();;
			
			for (int i = 0; i < fileList.length;i++)
			{
				fileList[i] = absolutePath +"/" +  fileList[i];
			}
			
			for (int j = 0; j < fileList.length;j++)
			{
				processUtteranceFile(fileList[j]);
			}
		}
		else if (file.isFile())
		{
			processUtteranceFile(file);
		}
	}
	public void processUtteranceFile(String utteranceFileName)
	{
		File utteranceFile = new File(utteranceFileName);
		
		//FIXME This is for initial monitoring only, remove once code is functioning
		System.out.println("Processing " + utteranceFile.getName());
		processUtteranceFile(utteranceFile);
	}
	
	public void processUtteranceFile(File utteranceFile, boolean oneForOne)
	{
		String text = "";
		String line = "";		
		String svmText;
		HashMap<String, Integer> stringMap;
		HashMap<Integer, Integer> integerMap;
		File libSVMTextFile = new File(utteranceFile.getName() + ".svm");
		PrintWriter localLibSVMTextWriter;
		
		try 
		{
			localLibSVMTextWriter = new PrintWriter(libSVMTextFile);
			reader					= new FileReader(utteranceFile);
		} 
		catch (FileNotFoundException e) 
		{
			//Bad file, jump out of this method
			return;
		}
		
		bufferedReader 	= new BufferedReader(reader);
		
		try {
			while ((line = bufferedReader.readLine()) != null)
			{
				if (line.startsWith("newutterance"))
				{
					if (!text.equalsIgnoreCase(""))
					{
						System.out.println(line);
						userName = utteranceFile.getName();//FIXME This will not work anywhere but in ENRON corpus.  Generalize when porting code over for Android email and text.
						stringMap = FeatureMaker.textToFeatureMap(text, MAXGAP, null, FeatureMaker.FEATURE_OSB, wordTokenizer);
						integerMap = textToSVM.StringMapToIntegerMap(stringMap, membershipChecker);
						svmText = textToSVM.mapToString(userName, integerMap);
						localLibSVMTextWriter.println(svmText);
						line = "";
						text = "";
					}
					else
					{
						line = "";
					}
				}
				else
				{
					text = text + line;
				}
			}
			
			if (!text.equalsIgnoreCase(""))
			{
				System.out.println(line);
				userName 		= utteranceFile.getName();//FIXME This will not work anywhere but in ENRON corpus.  Generalize when porting code over for Android email and text.
				stringMap 		= FeatureMaker.textToFeatureMap(text, MAXGAP, null, FeatureMaker.FEATURE_OSB, wordTokenizer);
				integerMap 	= textToSVM.StringMapToIntegerMap(stringMap, membershipChecker);
				svmText 			= textToSVM.mapToString(userName, integerMap);
				localLibSVMTextWriter.println(svmText);
				line = "";
				text = "";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		localLibSVMTextWriter.flush();
		localLibSVMTextWriter.close();
		
		return;
	}
	
	
	public void processUtteranceFile(File utteranceFile)
	{
		String text = "";
		String line = "";		
		String svmText;
		HashMap<String, Integer> stringMap;
		HashMap<Integer, Integer> integerMap;
		
		try 
		{
			reader					= new FileReader(utteranceFile);
		} 
		catch (FileNotFoundException e) 
		{
			//Bad file, jump out of this method
			return;
		}
		
		bufferedReader 	= new BufferedReader(reader);
		
		try {
			while ((line = bufferedReader.readLine()) != null)
			{
				if (line.startsWith("newutterance"))
				{
					if (!text.equalsIgnoreCase(""))
					{
						System.out.println(line);
						userName = utteranceFile.getName();//FIXME This will not work anywhere but in ENRON corpus.  Generalize when porting code over for Android email and text.
						stringMap = FeatureMaker.textToFeatureMap(text, MAXGAP, null, FeatureMaker.FEATURE_OSB, wordTokenizer);
						integerMap = textToSVM.StringMapToIntegerMap(stringMap, membershipChecker);
						svmText = textToSVM.mapToString(userName, integerMap);
						libSVMTextWriter.println(svmText);
						line = "";
						text = "";
					}
					else
					{
						line = "";
					}
				}
				else
				{
					text = text + line;
				}
			}
			
			if (!text.equalsIgnoreCase(""))
			{
				System.out.println(line);
				userName = utteranceFile.getName();//FIXME This will not work anywhere but in ENRON corpus.  Generalize when porting code over for Android email and text.
				stringMap = FeatureMaker.textToFeatureMap(text, MAXGAP, null, FeatureMaker.FEATURE_OSB, wordTokenizer);
				integerMap = textToSVM.StringMapToIntegerMap(stringMap, membershipChecker);
				svmText = textToSVM.mapToString(userName, integerMap);
				libSVMTextWriter.println(svmText);
				line = "";
				text = "";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		libSVMTextWriter.flush();
		libSVMTextWriter.close();
		
		System.out.println("Process Complete -- enjoy your file!");
		
		return;
	}

}
