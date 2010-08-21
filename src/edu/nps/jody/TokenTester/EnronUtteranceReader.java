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
	public void processUtteranceFile(File utteranceFile)
	{
		String text = "";
		String line = "";		
		String svmText;
		HashMap<String, Integer> stringMap;
		
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
					stringMap = FeatureMaker.textToFeatureMap(text, MAXGAP, null, FeatureMaker.FEATURE_OSB, wordTokenizer);
					svmText = textToSVM.mapToString(userName, textToSVM.StringMapToIntegerMap(stringMap, membershipChecker));
					libSVMTextWriter.println(svmText);
				}
				else
				{
					text = text + line;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
