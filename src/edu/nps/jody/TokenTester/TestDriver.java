package edu.nps.jody.TokenTester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


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
	public static void main(String[] args) 
	{
		try 
		{
			int array[][] = convertStringVectorToIntArray(loadStateVector("tmstates.txt"));
			//printVector(loadStateVector("tmstates.txt"));
			
			WordTokenizer wt = new WordTokenizer(array);
			
			Vector<String> tokenList = wt.wordTokenizeText("you'r I't can't do<S> <S </S '' </S> <a> </a>bcde");
			
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
			}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
