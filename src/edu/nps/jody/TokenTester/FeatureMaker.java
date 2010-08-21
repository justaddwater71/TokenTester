/** FeatureMaker is a set of static methods and static constants that are used to convert strings of words into 
  * word features that can be used for natural language processing.
  * 
     * @author      Jody Grady <jhgrady@nps.edu>
     * @version     2010.0703
     * @since       1.6
     */

package edu.nps.jody.TokenTester;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import opennlp.tools.sentdetect.SentenceDetectorME;

public class FeatureMaker 
{
	//Constructor
		//Static class. No constructors.
	
	//Data Members
		//FIXME This would be better as a static map or array vice hard coding ints here and Strings down in a modified get
	public static final int 			FEATURE_OSB 						= 0;
	public static final int 			FEATURE_GB 							= 1;
	public static final String 		FEATURE_LABEL_OSB 			= "OSB";
	public static final String 		FEATURE_LABEL_GB				= "GB";
	public static final String 		MODEL_FILE 							= "EnglishSD.bin.gz";
	public static final String 		LEMMA_DICTIONARY_FILE 	= "lemmaDictionaryFIle.bin";
	public static final String 		LEMMA_DICTIONARY_PATH	= "";//TODO Figure out this remote WordNET thing
	public static final int			NO_STEMMING 						= 0;
	public static final int			PORTER_STEMMING				=1;
	public static final int			YASS_STEMMING					=2;
	public static final int			LEMMATIZE							=3;
	
	//Accessors
		//Static class. No Accessors.
	
	//Mutators
		//Static class. No mutators.
	
	//Data Methods
	
	/**
     * Convert integer feature type to a label  (OSB, Gappy Bigram, etc).
     *
     * Authorative location for integer/label pairs.  Currenty supports Orthgonal Sparse Bigrams (OSB) and Gappy Bigrams (GB).
     *
     * @param featureType  	integer value to be converted into a String describing the feature type.
     * @return 							String describing the feature type associated with the parameter integer value.
     */
	public static String featureTypeToLabel(int featureType)
	{
		HashMap<Integer, String> typeToLabel = new HashMap<Integer, String>();
		
		typeToLabel.put(FEATURE_OSB, FEATURE_LABEL_OSB);
		typeToLabel.put(FEATURE_GB, FEATURE_LABEL_GB);
		
		return typeToLabel.get(featureType);
	}
	

	
	public static String allLowerCase(String text)
	{
		return text.toLowerCase();
	}
	
	
	public static Vector<String> parseOSB(Vector<String> tokenizedText, int maxGap)
	{
		Vector<String> OSBVector = new Vector<String>();
		int totalTokens = tokenizedText.size();
		
		//Create loop for word1. Use totalTokens vice words.length to save cycles.
		for (int i=0; i < totalTokens; i++)
		{
			//Create loop for word2.  Going from 1 to (< maxGap + 1) vice traditional 0 to (< maxGap).
			for (int j=1; j < maxGap + 1; j++)
			{
				//Don't run off the end of the words array
				if ((i + j) > totalTokens - 1) 
				{
					break; //Get out of current word2 loop, but continue word1 loop.
				}
				//k goes from the current gap out to the maxGap
				for (int k=(j - 1); k < maxGap; k++)
				{
					OSBVector.add(tokenizedText.get(i) + " " + tokenizedText.get(i + j)+ " " + k);
				}
			}
		}
		
		return OSBVector;
	}
	
	
	public static Vector<String> parseGB(Vector<String> tokenizedText, int maxGap)
	{
		Vector<String> OSBVector = new Vector<String>();
		int totalTokens = tokenizedText.size();
		
		//Create loop for word1. Use totalTokens vice words.length to save cycles.
		for (int i=0; i < totalTokens; i++)
		{
			//Create loop for word2.  Going from 1 to (< maxGap + 1) vice traditional 0 to (< maxGap).
			for (int j=1; j < maxGap + 1; j++)
			{
				//Don't run off the end of the words array
				if ((i + j) > totalTokens - 1) 
				{
					break; //Get out of current word2 loop, but continue word1 loop.
				}			
					OSBVector.add(tokenizedText.get(i) + " " + tokenizedText.get(i + j));
			}
		}
		
		return OSBVector;
	}
	
	/**
     * Convert a String text message into a set of Orthogonal Sparse Bigrams (OSB).
     *
     * Tokenize each word in the provided message.  Pair up individual words into Orthodgonal Sparse Bigrams (OSB).
     * For instance, "the quick brown fox jumped over" gets converted into into the quick 0, the quick 1, the quick 2, 
     * the quick 3, for a maximum gap of 4 and continues for "the brown", "the fox" etc.  The build continues for "quick brown"
     * "quick fox" etc.  OSB is different from Gappy Bigram (GB) in that OSB keeps count of the lesser included distance
     * between word1 and word2 where GB only pairs the words.
     *
     * @param text		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @return 							String array of word pairs with distance created from the text message parameter.
     */
	/*public static Vector<String> parseOSB(String text, int maxGap)
	{
		//Tokenizer to parse out words (defined as characters surrounded by whitespace)
		StringTokenizer tokenizer = new StringTokenizer(text);
		
		//Determine total number of words in message to size words array
		int totalTokens = tokenizer.countTokens();
		
		//Determine total size of resultArray. Thank you Wolfram-Alfa
		int totalOSB = (maxGap*(maxGap+1)/2)*totalTokens
			- (maxGap)*(maxGap+1)*(maxGap+2)/6;
	
		//Create resultArray to return for addition to hashMap
		String[] resultArray = new String[totalOSB];
	
		//Create words array for looping through to get gappy bigrams
		String words[] = new String[totalTokens];
		
		//Load the words array with words from text
		for (int h=0; h < totalTokens; h++)
		{
			words[h] = tokenizer.nextToken();
		}
		
		
		 * "n" will be used to advance through result array
		 * There is no check to ensure I don't run off the
		 * end of the array.  Depending on Wolfram-Alfa
		 * formula for that.
		
		int n = 0;
		
		//Create loop for word1. Use totalTokens vice words.length to save cycles.
		for (int i=0; i < totalTokens; i++)
		{
			//Create loop for word2.  Going from 1 to (< maxGap + 1) vice traditional 0 to (< maxGap).
			for (int j=1; j < maxGap + 1; j++)
			{
				//Don't run off the end of the words array
				if ((i + j) > totalTokens - 1) 
				{
					break; //Get out of current word2 loop, but continue word1 loop.
				}
				//k goes from the current gap out to the maxGap
				for (int k=(j - 1); k < maxGap; k++)
				{
					resultArray[n] = words[i] + " " + words[i + j] + " " + k;
					n++;
				}
			}
		}
		
		return resultArray;
	}*/
	
	/**
     * Convert a String text message into a set of Gappy Bigrams.
     * 
     * Tokenize each word in the provided message.  Pair up individual words into Orthodgonal Sparse Bigrams (OSB).
     * For instance, "the quick brown fox jumped over" gets converted into into the quick 0, the quick 1, the quick 2, 
     * the quick 3, for a maximum gap of 4 and continues for "the brown", "the fox" etc.  The build continues for "quick brown"
     * "quick fox" etc.  OSB is different from Gappy Bigram (GB) in that OSB keeps count of the lesser included distance
     * between word1 and word2 where GB only pairs the words.
     *
     * @param text		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @return 							String array of word pairs with distance created from the text message parameter.
     */
	
	/**
     * Convert a String text message into a feature set.
     *
     * Based on the feature type provided
     *
     * @param text		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @param featureType		integer value representing type of feature to extract from text message (ie OSB or GB).
     * @return 							String array of word pairs with distance created from the text message and feature type parameters.
     */
	
	public static Vector<String> parse(String text, int maxGap, int featureType, WordTokenizer tokenizer)
	{
		Vector<String> tokenizedText = tokenizer.tokenize(text);
		
		switch (featureType)
		{
			case FEATURE_OSB: //=0
			
				return parseOSB(tokenizedText, maxGap);
			
			
			case FEATURE_GB: //=1
	
				return parseGB(tokenizedText, maxGap);
			
			
			default:

				return null;
		}
	}
	
	public static String[] parse(String text, int maxGap, int featureType, WordTokenizer tokenizer, SentenceDetectorME sbd)
	{
		String [] resultArray = null;
		return resultArray;
	}
	
	/**
     * Convert a String text message into a set of Orthogonal Sparse Bigrams (OSB).
     *
     * Tokenize each word in the provided message.  Pair up individual words into Orthodgonal Sparse Bigrams (OSB).
     * For instance, "the quick brown fox jumped over" gets converted into into the quick 0, the quick 1, the quick 2, 
     * the quick 3, for a maximum gap of 4 and continues for "the brown", "the fox" etc.  The build continues for "quick brown"
     * "quick fox" etc.  OSB is different from Gappy Bigram (GB) in that OSB keeps count of the lesser included distance
     * between word1 and word2 where GB only pairs the words.
     *
     * @param text		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @param hashMap			previously built HashMap using the same feature set and maximum gap.
     * @param featureType		integer value representing type of feature to extract from text message (ie OSB or GB).
     * @return 							HashMap of features to the integer count of the number of occurrences of that feature in the text message. 
     */
	

	/**
	 * @param text
	 * @param maxGap
	 * @param hashMap
	 * @param featureType
	 * @param wt
	 * @return
	 */
	public static HashMap<String, Integer> textToFeatureMap(String text, int maxGap, HashMap<String, Integer> hashMap, int featureType, WordTokenizer wt)
	{
		//FIXME This breaks the processing path in this program.  Now need to determine how to implement SBD, lemmatize, lowercase, punctuation, etc inline (pre-process is the method built to handle this, but how do I get that accomplished?)
		Vector<String> stringVector = parse(text, maxGap, featureType, wt);
		
		for (int i = 0; i < stringVector.size(); i++)
		{
			//If the key is already there, add 1 to the value
			//This looks WAY convoluted and uses TOO MANY lookups
			//in the hashmap.  There has to be an iterator way
			//to do this smartly
			if (hashMap.containsKey(stringVector.get(i)))
			{
				hashMap.put(stringVector.get(i), hashMap.get(stringVector.get(i)) + 1);
			}
			else
			{
				//Prime the entry in the hashmap with count =1
				hashMap.put(stringVector.get(i), Integer.valueOf(1));
			}
		}
		
		return hashMap;
	}
	
	/**
	 * Preprocess incoming text based on intended feature set.
	 * 
	 * Process the incoming text based on chosen features set, namely, whether to allow capitalization,
	 * punctuation, unknown words, sentence boundaries, and stemming.  The choices of feature set
	 * impact the size of the required files.  Specifically, capitalization reduces the required
	 * feature set files (for reference, not for each text message),  removing punctuation reduces the 
	 * required file size, dropping unknown text vice substituting with the <UNK> marker reduces the
	 * required file size, adding SentenceBoundaries increases the required file size, and using stemming
	 * decreases the required file size.
	 * 
	 * @param text										Incoming text message to be processed
	 * @param removePunctuation			Remove all capitalization if set to true, leave capitalization in text if false.
	 * @param makeLowerCase					Convert entire message to lower case letters if true.  Leave capitals alone if false.
	 * @param dropUnknownWord				Remove unknown word if set to true.  Convert unknown words to <UNK> marker if false.
	 * @param dropUnknownGram			Remove unknown grams if set to true.  Convert unknown grams to <UNKGRAM> marker if false.
	 * @param addSentenceBoundaries	Find sentence boundaries and mark with <s> (start) and </s> (end) if true. Do not find sentence boundaries if false.
	 * @param doStemming						Removed common word suffixes if true.  Leave suffixes in place if false
	 * @return
	 * @throws IOException 
	 */
	public static String preProcessText(String text, boolean removePunctuation, boolean makeLowerCase, boolean dropUnknownWord, boolean addSentenceBoundaries, boolean doStemming) throws IOException
	{
		String processedText = text;
		
		//Order of applying this processing IS significant.  
		
		//If we add sentence boundaries before stemming, we might confuse the stemming routine.
		if (doStemming)
		{
			processedText = wordStemming(processedText);
		}
		//If we strip the punctuation BEFORE doing sentence tokenizer, the sentence tokenizer won't be happy.  
		if (addSentenceBoundaries)
		{
			//processedText = sentenceTokenizer(processedText);
			//Placeholder for now
		}
		
		//If we're going to strip capitalization, need to do that BEFORE putting in the <UNK> grams
		//so we don't munge the <UNK> tag
		if (makeLowerCase)
		{
			processedText = processedText.toLowerCase();
		}
		
		if (dropUnknownWord)
		{
			//processedText = MembershipCheck.check(MembershipCheck.DROP_WORD, processedText);
		}
		else
		{
			//processedText = MembershipCheck.check(MembershipCheck.TAG_UNK, processedText);
		}
		
		if (removePunctuation)
			{
				processedText = noPunctuation(processedText);
			}
		
		return processedText;
	}
	
	/*//Much of this code borrowed from Wicked Cool Java (No Starch Press)	Copyright (C) 2005 Brian D. Eubanks
	private static String lemmatize(String text) throws JWNLException, IOException 
	{
		//FIXME What happens when punctuation is not stripped and the text is broken on whitespace only?  We need to deal with splitting text that has punctuation in it.
		JWNL.initialize(new FileInputStream(new File(LEMMA_DICTIONARY_FILE)));
		
		String postagText = posTag(text);
		
		String textArray[] = text.split(" ");
		
		String lemmaArray[];
		
		String lemmas = "";
		//FIXME JWNL.initizalize and JWNLDictionary should not necesary reference the same file.  Sort this out!
		
		JWNLDictionary dictionary = new JWNLDictionary(LEMMA_DICTIONARY_FILE);
		
		int textArraySize = textArray.length;
		
		for (int i=0; i < textArraySize; i++)
		{
			lemmaArray = null;
			
			lemmaArray = dictionary.getLemmas(textArray[i], "VERB");
			
			lemmas.concat(lemmaArray[0] + " ");
		}
		
		return lemmas;
	}*/

/*	private static String posTag(String text)
	{
		return text;
	}*/
	
	public static String noPunctuation(String text)
	{	
		int 		textLength = text.length();
		char[] textArray = text.toCharArray();
		char[] processedArray = new char[textLength];
		int 		processedIndex = 0;
		
		for (int i=0;i < textLength; i++)
		{
			if (Character.isLetterOrDigit(textArray[i]))
			{
				processedArray[processedIndex] = textArray[i];
				processedIndex++;
			}
		}
						
		return new String(processedArray);
	}
	
	public static String sentenceTokenizer(String text, SentenceDetectorME sbd) throws IOException
	{
		String processedText = text;
			
			String sentenceArray[] = sbd.sentDetect(text);
			
			int sentenceArrayLength = sentenceArray.length;
			
			for (int i=0; i < sentenceArrayLength; i++)
			{
				processedText.concat("<S>");
				processedText.concat(sentenceArray[i]);
				processedText.concat("</S>");
			}

		return processedText;
	}
	
	//FIXME Implement word stemming method
	public static String wordStemming(String text)
	{
		return text;
	}
	
	public static boolean charInEnum(char currentChar, char[] charArray)
	{
		//FIXME should be a list of char not a list of char[].  Something is still wrong here.
		return Arrays.asList(charArray).contains(currentChar);
	}
}
