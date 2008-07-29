/*if (isNumeric(b) || b == 'c' || b == 'o' || b == 's' || isShortOperator(b))
				{	strippedInput = strippedInput + b;
					
				}
				else if (a == 32 || a == 9)
				{ //tab or space; ignore
				}
				else //cannot possibly be valid syntax
				{	System.out.printf("'%s' is an invalid character, sorry.%n",b);
					System.exit(0); /
				} */

	private static String[] validate(char[] charArray)
	{	//check for invalid spellings of cos and invalid numbers, normalise all numbers
		String[] mathsArray = new String[0];
		for (int i = 0; i<charArray.length; i++)
		{	if (isShortOperator(charArray[i]))
			{	mathsArray = extendArray(mathsArray,charArray[i]+""); //must concatenate empty string because we can't cast to string
			}
			else if (charArray[i] == 'c')
			{	//c is only valid in the form 'cos'
				if (charArray[i+1] == 'o' && charArray[i+2] == 's') //may cause an array exception here
				{	mathsArray = extendArray(mathsArray,"cos");
					i += 2; //we have dealt with 'o' and 's', move on
				}
				else
				{	System.out.printf("'%s%s%s' is not a valid operator, sorry.%n",charArray[i],charArray[i+1],charArray[i+2]);
					System.exit(0);
				}
			}
			else //is a number; validate it
			{	boolean afterPoint = false;
				String number = "";
				while (isNumeric(charArray[i]))
				{	if (charArray[i] == '.' && number.equals("")) //we have a number of the form .123, convert to 0.123
					{	number = "0.";
						afterPoint = true;
					}
					else if (charArray[i] == '.')
					{	if (!afterPoint) //not yet used a decimal point in this number
						{	number = number + '.';
							afterPoint = true;
						}
						else //this is the second decimal point therefore it's not valid
						{	System.out.printf("Syntax error: Multiple decimal points.%n");
							System.exit(0);
						}
					}
					else //just a digit
					{	number = number + charArray[i];
					}

					if (i+1 < charArray.length && isNumeric(charArray[i+1]))
					{	i++; //get next digit, if there are any left in this expression
					}
					else
					{	break; //solves corner case if last bit of expression is a number
					}
				}
				if (!afterPoint) //number was of the form 345, convert to 345.0
				{	number = number + ".0";
				}
				mathsArray = extendArray(mathsArray,number);
			}
		}
		return mathsArray;
	}

	private static void validateOperatorTokens(String[] tokenArray)
	{	for (int i=0; i<tokenArray.length; i++)
		{	if (isNumeric(tokenArray[i].charAt(0)))
			{	//do nothing, we have a separate method for numeric tokens
			}
			else if (tokenArray[i].length() == 1) //single character token, should be single character operator
			{	if (isShortOperator(tokenArray[i].charAt(0)))
				{	//do nothing, valid token
				}
				else
				{	System.out.printf("Syntax error: '%s' is an invalid operator%n");
					System.exit(1);
				}
			}
			else if (tokenArray[i].charAt(0) == 'c' && tokenArray[i].charAt(1) == 'o' && tokenArray[i].charAt(2) == 's')
			{	//this assumes any remaining tokens are three characters long, but tokenise should ensure this
				//do nothing, valid cos operator
			}
			else
			{	System.out.printf("Syntax error: '%s' is not a valid operator.%n",tokenArray[i]);
				System.exit(1);
			}
		}
	}

	public static void validateOperatorTokens(String[] tokenArray)
	{	for (int i=0; i<tokenArray.length; i++)
		{	if (isNumeric(tokenArray[i].charAt(0)))
			{	//do nothing, we have a separate method for numeric tokens
			}
			else if (tokenArray[i].length() == 1) //single character token, should be single character operator
			{	if (isShortOperator(tokenArray[i].charAt(0)))
				{	//do nothing, valid token
				}
				else
				{	System.out.printf("Syntax error: '%s' is an invalid operator%n");
					System.exit(1);
				}
			}
			else if (tokenArray[i].charAt(0) == 'c' && tokenArray[i].charAt(1) == 'o' && tokenArray[i].charAt(2) == 's')
			{	//this assumes any remaining tokens are three characters long, but tokenise should ensure this
				//do nothing, valid cos operator
			}
			else
			{	System.out.printf("Syntax error: '%s' is not a valid operator.%n",tokenArray[i]);
				System.exit(1);
			}
		}
	}

	public static void normaliseNumericTokens(String[] tokenArray)
	{	for (int i=0; i<tokenArray.length; i++)
		{	if (isNumeric(tokenArray[i].charAt(0)))
			{	//check number has no more than one d.p., normalise to format 123.456
				boolean afterPoint = false;
				for (int j=0; j<tokenArray[i].length(); j++)
				{	if (tokenArray[i].charAt(j) == '.')
					{	if (!afterPoint)
						{	afterPoint = true;
						}
						else //we have already seen one d.p.
						{	System.out.printf("Syntax error: '%s': contains too many decimal points.%n");
							System.exit(1);
						}
					}
				}
				if (tokenArray[i].charAt(0) == '.') // number of form .123, convert to 0.123
				{	tokenArray[i] = "0" + tokenArray[i];
				}
				if (!afterPoint) // number without d.p.
				{	tokenArray[i] = tokenArray[i] + ".0";
				}
				if (tokenArray[i].charAt(tokenArray[i].length()-1) == '.') //number of form 123., conver to 123.0
				{	tokenArray[i] = tokenArray[i] + "0";
				}
			}
			else
			{ //is not numeric token
			}
		}
	}
