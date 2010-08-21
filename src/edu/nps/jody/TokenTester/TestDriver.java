package edu.nps.jody.TokenTester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import edu.nps.jody.HashFinder.MembershipChecker;


public class TestDriver 
{
	//Data Members

	
	//Constructors
	
	
	//Methods
	public static Vector<Vector<String>> loadStateVector(String filename) throws IOException
	{
		Vector<Vector<String>> stateRowVector = new Vector<Vector<String>>();
		File tableFile									= new File(filename);
		Reader tableReader					= new FileReader(tableFile);
		BufferedReader tableBufferedReader = new BufferedReader(tableReader);
		
		String tableLine;
		StringTokenizer tableLineTokenizer;
		String token;
		int localRow			= 0;
		int localColumn 	= 0;
		
		while ((tableLine = tableBufferedReader.readLine()) != null)
		{
			localColumn = 0;
			tableLineTokenizer  = new StringTokenizer(tableLine);
			stateRowVector.add(new Vector<String>());
			while (tableLineTokenizer.hasMoreTokens())
			{
				token =tableLineTokenizer.nextToken();
				stateRowVector.get(localRow).add(token);
				localColumn++;
			}		
			localRow++;
		}
		
		return stateRowVector;
	}
	
	public static int[][] convertStringVectorToIntArray(Vector<Vector<String>> stateRowVector)
	{
		int localRow = stateRowVector.size();
		int localColumn = stateRowVector.get(0).size();
		
		int[][] stateTable = new int[localRow][localColumn];
		
		for (int i=0; i < localRow; i++)
		{
			for (int j=0; j < localColumn; j++)
			{
				stateTable[i][j] = Integer.parseInt(stateRowVector.get(i).get(j));
			}
		}
		
		return stateTable;
	}
	
	public static void printVector(Vector<Vector<String>> vector)
	{
		Iterator<Vector<String>> rowIterator = vector.iterator();
		Iterator<String> columnIterator;
		while (rowIterator.hasNext())
		{
			columnIterator = rowIterator.next().iterator();
			
			while (columnIterator.hasNext())
			{
				System.out.print(columnIterator.next() + "\t");
			}
			
			System.out.println();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{

			//Load the file with all the DFA/Turing state information in it to be used by DFA engine.
			int array[][] = convertStringVectorToIntArray(loadStateVector("tmstates.txt"));
			//printVector(loadStateVector("tmstates.txt"));
			
			//Create the WordTokenizer engine to break incoming strings into tokens
			WordTokenizer wt = new WordTokenizer(array);
			
			//String[] featureArray = wt.tokenize("The quick brown fox jumped over the lazy dog.");
			
			//tokenList is a simple list of tokens, no bigram, OSB, or Gappy Bigram work is being done here
/*			Vector<String> tokenList = wt.wordTokenizeText("The quick brown fox jumped over the lazy dog.");
			
			//Just checking here to ensure the list isn't empty (which would throw a null pointer exception)
			//Should probably do some exception handling here instead of a if-then-else
			if (tokenList.size() > 0)
			{
				Iterator<String> tokenIterator = tokenList.iterator();
				
				while (tokenIterator.hasNext())
				{
					System.out.println(tokenIterator.next());
				}
			}
			else
			{
				System.out.println("The list is empty!!");
			}*/
			
/*			Vector<String> stringVector = FeatureMaker.parse("the quick brown fox jumped over the lazy dog", 4, FeatureMaker.FEATURE_OSB, wt);
			
			for (int i = 0; i < stringVector.size();i++)
			{
				System.out.println(stringVector.get(i));
			}*/
			
			//Load the features and counts of features into a HashMap
			HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
			
			FeatureMaker.textToFeatureMap("The quick brown fox jumped over the lazy dog.", 4, hashMap, FeatureMaker.FEATURE_OSB, wt);
			
			//Convert each String feature in above hashMap into an Integer MPHF representation of that String Feature 
			Iterator<String> hashMapIterator = hashMap.keySet().iterator();
			
			String key;
			
			MembershipChecker osbMember = new MembershipChecker("keys.mph", "signature");

			Integer	theValue;
			Integer 	theIndex;
			
			HashMap<Integer, Integer> hashIntegers = new HashMap<Integer, Integer>();
			
			while (hashMapIterator.hasNext())
			{
				key 				= hashMapIterator.next();
				theValue	= hashMap.get(key);
				theIndex = osbMember.getIndex(key);
				System.out.println(key + ", " + theValue.toString());
				System.out.println(theIndex.toString() + ", " + theValue.toString());
				hashIntegers.put(theIndex, theValue);
			}
			
			File svmDataFile = new File("svmDataFile");
			
			File classToIntegerFile = new File("classToIntegerFile");
			
			TextToSVM textToSVM = new TextToSVM(svmDataFile, classToIntegerFile);
			
			String libSVMLine = textToSVM.addInstance("test", hashIntegers);
			
			System.out.println(libSVMLine);
			
			textToSVM.writeInstanceToFile(libSVMLine);
			
			


		
		

	}

}
