import java.io.InputStreamReader;

/**
 * Parsimonious - a mathematical parser.
 * Known bugs: Will only process one line with each run. Does not do any maths. Does not validate grammar. No tree traversal.
 * @author Wilfred Hughes
 */

// invalid syntax suggestions: "1.0.1" "sin" "css" "coos" "3**3" "2 co"

public class Parsimonious
{	public static void main(String[] args)
	{	//intro
		System.out.printf("The following operators are accepted in descending order of priority:%n");
		System.out.printf("cos ! * + - (cos in radians, ! only on integers)%n");
		System.out.printf("Real number are accepted in the forms 0, 0.0 or .0 (implicit: 0. and .) (and stored as doubles)%n");
		System.out.printf("********************************************%n");
		System.out.printf("Type a mathematical expression and hit enter. All whitespace will be ignored.%n");

		//take input
		InputStreamReader input = new InputStreamReader(System.in);

		String inputString = ""; //need to instantiate this outside the try block to keep java happy

		try
		{	int a = input.read();
			//put input in string
			while (a != -1 && a != 10) //-1 is end of stream, 10 is character return
			{	inputString = inputString + (char)a;
				a = input.read();
			}
			System.out.printf("Original input is: %s%n",inputString);
		}
		catch (java.io.IOException e) 
		{	//I'm not convinced this is ever called
			System.out.println("IOException! Exiting.");
			System.exit(1);
		}

		String strippedInput = Lexer.removeWhitespace(inputString);
		System.out.printf("Stripping whitespace: %s%n",strippedInput);

		System.out.printf("Checking for illegal characters...");
		Lexer.checkCharacters(strippedInput);
		System.out.printf("OK%n");

		System.out.printf("Tokenising...");
		Token[] mathsArray = Lexer.tokenise(strippedInput);
		System.out.printf("OK%n");
		System.out.printf("Current token array: "); printArray(mathsArray);

		System.out.printf("Checking syntax and normalising numbers...");
		Lexer.validateTokens(mathsArray);
		System.out.printf("OK%n");

		System.out.printf("mathsArray contains: ");
		printArray(mathsArray);
		
		//validate grammar
		//parse
		//Parser.parse(mathsArray);
		//System.out.printf("Parsed result: "); printArray(mathsArray);
	}

	private static void printArray(Object[] input) //we want to be able to print strings or tokens
	{	for (int i=0; i<input.length; i++)
		{	System.out.printf("%s ",input[i]);
		}
		System.out.printf("%n");
	}
}

/**
 * Necessity is the mother of invention - a token class. Can be either a number or an operator.
 *
 */

class Token
{	private String data;
	private boolean isOperator;

	public boolean isOperator()
	{	return isOperator;
	}

	public Token(String value, boolean tokenIsOperator)
	{	data = value;
		isOperator = tokenIsOperator;
	}

	public String getValue()
	{	return data;
	}

	public String toString()
	{	return data;
	}
}

/**
 * The Lexer class is a collection of static methods that ensure the parser only recieves valid tokens.
 */

class Lexer
{	public static String removeWhitespace(String input)
	{	String returnme = "";
		for (int i=0; i<input.length(); i++)
		{	if((int)input.charAt(i) == 9 || (int)input.charAt(i) == 32) //tab or space
			{	//do nothing
			}
			else
			{	returnme = returnme + input.charAt(i);
			}
		}
		return returnme;
	}

	/**
	 * checkCharacters searches a string for illegal characters. It is the first line of defence from invalid syntax.
	 * @deprecated 
	 */

	public static void checkCharacters(String input)
	{	for (int i=0; i<input.length(); i++)
		{	if (isNumeric(input.charAt(i)) || isShortOperator(input.charAt(i)) || 
				input.charAt(i) == 'c' || input.charAt(i) == 'o' || input.charAt(i) == 's')
			{	//is a valid character
			}
			else
			{	System.out.printf("'%s' is an invalid character, sorry.%n",input.charAt(i));
				System.exit(1);
			}
		}
	}

	/**
	 * tokenise takes a String that only contains valid characters and returns an array of tokens.
	 */

	public static Token[] tokenise(String input)
	{	String[] returnme = new String[0];
		for (int i=0; i<input.length(); i++)
		{	if (isShortOperator(input.charAt(i)))
			{	returnme = extendArray(returnme,input.charAt(i)+""); //single character operator token
			}
			else if (isNumeric(input.charAt(i)))
			{	if (i == 0) //expression starts with a number
				{	returnme = extendArray(returnme,input.charAt(i)+"");
				}
				else
				{	if (isNumeric(returnme[returnme.length-1].charAt(0))) //first character of last token is numeric
					{	//last token is number so far, add this digit or d.p. to it
						returnme[returnme.length-1] = returnme[returnme.length-1] + input.charAt(i);
					}
					else //last token was operator, start new token
					{	returnme = extendArray(returnme,input.charAt(i)+"");
					}
				}
			}
			else //is hopefully a valid cos token, but we haven't checked yet, so we just take the next 3 characters
			{	//risk of IndexException here, so catch it (occurs if expression ends 'c' or 'co' etc)
				try
				{	String token = "" + input.charAt(i) + input.charAt(i+1) + input.charAt(i+2);
					returnme = extendArray(returnme,token);
					i += 2;
				}
				catch (StringIndexOutOfBoundsException e)
				{	System.out.printf("Syntax error: Operators can only be 1 or 3 characters long.%n");
					System.exit(1);
				}
			}
		}
		return toTokens(returnme);
	}

	private static Token[] toTokens(String[] tokenStrings)
	{	Token[] returnme = new Token[tokenStrings.length];
		for (int i=0; i<tokenStrings.length; i++)
		{	if (tokenStrings[i].charAt(0) == 'c' || isShortOperator(tokenStrings[i].charAt(0)))
			{	returnme[i] = new Token(tokenStrings[i],true);
			}
			else
			{	//numeric token
				returnme[i] = new Token(tokenStrings[i],false);
			}
		}
		return returnme;
	}

	/**
	 * validateTokens checks tokens are valid operators or number, and normalises the numbers. It uses validateOperatorToken and normaliseNumericToken to do the work.
	 */

	public static void validateTokens(Token[] tokenArray)
	{	for (int i=0; i<tokenArray.length; i++)
		{	if (tokenArray[i].isOperator())
			{	validateOperatorToken(tokenArray[i]);
			}
			else
			{	tokenArray[i] = normaliseNumericToken(tokenArray[i]);
			}
		}
	}

	private static void validateOperatorToken(Token token)
	{	if (token.getValue().length() == 1 && isShortOperator(token.getValue().charAt(0)))
		{	//it's a valid single character operator token
			return;
		}
		else if (token.getValue().equals("cos"))
		{	return;
		}
		else
		{	System.out.printf("Syntax error:'%s' is not a valid operator.%n",token);
			System.exit(1);
		}
	}

	private static Token normaliseNumericToken(Token token)
	{	//check number has no more than one d.p., normalise to format 123.456
		boolean afterPoint = false;
		String returnme = token.getValue();
		for (int i=0; i<token.getValue().length(); i++)
		{	if (token.getValue().charAt(i) == '.')
			{	if (!afterPoint)
				{	afterPoint = true;
				}
				else //we have already seen one d.p.
				{	System.out.printf("Syntax error: '%s': contains too many decimal points.%n",token);
					System.exit(1);
				}
			}
		}
		if (token.getValue().charAt(0) == '.') //number of from .123 convert to 0.123
		{	returnme = "0" + returnme;
		}
		if (!afterPoint) //numer without d.p.
		{	returnme = returnme + ".0";
		}
		if (token.getValue().charAt(token.getValue().length()-1) == '.') //number of form 123. convert to 123.0
		{	returnme = returnme + "0";
		}
		return new Token(returnme,false);
	}

	//inefficient but quick and dirty
	private static String[] extendArray(String[] input, String element)
	{	//if statement due to nasty empty array corner case
		String[] returnme = new String[input.length+1];
		int i;
		for (i=0; i<returnme.length; i++)
		{	if (i == input.length)
			{	returnme[i] = element;
			}
			else
			{	returnme[i] = input[i];
			}
		}		
		return returnme;
	}

	private static boolean isNumeric(char input)
	{	if (input == '0' || input == '1' || input == '2' || input == '3' || 
		    input == '4' || input == '5' || input == '6' || input == '7' || 
		    input == '8' || input == '9' || input == '.')
		{	return true;
		}
		else
		{	return false;
		}
	}

	private static boolean isShortOperator(char input) //ie operator other than cos
	{	if (input == '!' || input == '*' || input == '+' || input == '-')
		{	return true;
		}
		else
		{	return false;
		}
	}
}

/**
 * The parser class is a collection of methods that gradually replace operators with their numeric results.
 * By using the removeEmptyTokens(...) method we should only have a one element array when we finish.
 * The only the way the parser class is used by calling Parser.parse(tokenArray), everything else is private.
 */

class Parser
{	public static String parse(String[] tokenArray)
	{	parseCos(tokenArray);
		parseFactorial(tokenArray);
		parseMultiplication(tokenArray);
		parseAddition(tokenArray);
		parseSubtraction(tokenArray);
		return tokenArray[0]; //should only have one element in array now
	}

	private static void removeEmptyTokens(String[] tokenArray)
	{	int emptyTokenCount = 0;
		for (int i=0; i<tokenArray.length; i++)
		{	if (tokenArray[i].equals("Empty"))
			{	emptyTokenCount++;
			}
		}
		if (emptyTokenCount != 0)
		{	String[] newTokenArray = new String[tokenArray.length-emptyTokenCount];
			int j=0;
			for (int k=0; k<tokenArray.length; k++)
			{	newTokenArray[j] = tokenArray[k];
				if (!tokenArray[k].equals("Empty"))
				{	j++;
				}
			}
			tokenArray = newTokenArray;
		}
	}

	private static void parseCos(String[] tokenArray)
	{	boolean doneSomething = false; //we need to recurse becuase of expressions like "cos cos 0.0"
		for (int i=tokenArray.length-1; i>=0; i--) //right associative
		{	
		}
	}

	private static void parseFactorial(String[] tokenArray)
	{
	}

	private static void parseMultiplication(String[] tokenArray)
	{
	}

	private static void parseAddition(String[] tokenArray)
	{
	}

	private static void parseSubtraction(String[] tokenArray)
	{
	}
}

/* 
simplified grammar, showing precedence:
expr -> cos expr
expr -> expr !
expr -> expr * expr
expr -> expr + expr
expr -> expr - expr
expr -> real
real -> the set of real numbers

full grammar:
expr -> cos expr
expr -> expr !
expr -> expr * expr
expr -> expr + expr
expr -> expr - expr
expr -> digits.digits
digits -> digits digit | digit
digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
*/