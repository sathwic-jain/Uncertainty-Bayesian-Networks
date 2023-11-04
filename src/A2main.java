import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/********************
 * Starter Code
 * 
 * This class contains some examples on how to handle the required inputs and
 * outputs
 * 
 * @author lf28
 * 
 *         run with
 *         java A2main <Pn> <NID>
 * 
 *         Feel free to change and delete parts of the code as you prefer
 * 
 */

public class A2main {
	// To store all the cpts given in the xml file.
	static ArrayList<Factors> factorsList = new ArrayList<Factors>();

	public static void main(String[] args) {

		try {
			File xmlfile = new File(args[1]);
			// read-in and parse the xml file, e.g.
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlfile);
			//function that reads the xml file according to the given commands
			//This function then creates all the factors required for the execution of the program. 
			sortInputs(doc);
			// assigning parentNode each of the factors for ease of coding.
			assignParents();
			
			//completing the final initializations such as categories and stride calculations
			for(int i = 0; i < factorsList.size(); i++) {
				factorsList.get(i).completeAll();
			}
			//Setting up global variables in the factors.
			factorsList.get(0).setup(factorsList);

			// printing the factorLists -------- for testing purposes
			// for(int i = 0; i < factorsList.size(); i++) {
			// System.out.println(i);
			// for(int j = 0; j < factorsList.get(i).getTotalVarSize(); j++) {
			// System.out.println(factorsList.get(i).getAllVars().get(j));
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(System.in);

		switch (args[0]) {
			case "P1": {
				// use the network constructed based on the specification in args[1]
				String[] query = getQueriedNode(sc);
				// question variable
				String variable = query[0];
				// to store factor object of the variable in question.
				Factors queryFactor;
				// the given value for the variable in question.
				String given = query[1];

				double result;
				// finding the factor of the variable in question.
				queryFactor = findFactorGivenVar(variable);
				int value = 0;
				//getting the value integer numbering of the category.
				value = setValueQuery(given, queryFactor);
				// calling the probability calculator
				// execute query of p(variable=value)
				result = P1(queryFactor, value);
				printResult(result);
			}
				break;

			case "P2": {
				// use the network constructed based on the specification in args[1]
				String[] query = getQueriedNode(sc);
				// variable in question.
				String variable = query[0];
				// the value of the given variable
				String given = query[1];
				int value = 0;

				// order given
				String[] order = getOrder(sc);
				// storing the factor object of the given variable
				Factors queryFactor;
				queryFactor = findFactorGivenVar(variable);
				value = setValueQuery(given, queryFactor);
				// execute query of p(variable=value) with given order of elimination
				double result = P2(queryFactor, value, order);
				printResult(result);
			}
				break;

			case "P3": {
				// use the network constructed based on the specification in args[1]
				String[] query = getQueriedNode(sc);
				// the variable in question.
				String variable = query[0];
				// value of the variable
				String given = query[1];
				int value = 0;
				// storing the evidence.
				ArrayList<String[]> evidence = getEvidence(sc);
				// getting the factor object of the given variable
				Factors queryFactor;
				queryFactor = findFactorGivenVar(variable);
				// setting the integer value for the value.
				value = setValueQuery(given, queryFactor);			
				double result;
				// execute query of p(variable=value|evidence) with an order
				result = P3(queryFactor, value, evidence);
				printResult(result);
			}
				break;

			case "P4": {
				// use the network constructed based on the specification in args[1]
				//BN validation
				validate();
			}
				break;
		}
		sc.close();
	}

	// method to obtain the evidence from the user
	private static ArrayList<String[]> getEvidence(Scanner sc) {

		System.out.println("Evidence:");
		ArrayList<String[]> evidence = new ArrayList<String[]>();
		String[] line = sc.nextLine().split(" ");

		for (String st : line) {
			String[] ev = st.split(":");
			evidence.add(ev);
		}
		return evidence;
	}

	// method to obtain the order from the user
	private static String[] getOrder(Scanner sc) {

		System.out.println("Order:");
		String[] val = sc.nextLine().split(",");
		return val;
	}

	// method to obtain the queried node from the user
	private static String[] getQueriedNode(Scanner sc) {
		System.out.println("Query:");
		String[] val = sc.nextLine().split(":");

		return val;

	}

	// method to format and print the result
	private static void printResult(double result) {

		DecimalFormat dd = new DecimalFormat("#0.00000");
		System.out.println(dd.format(result));
	}

	/**
	 * Method to set the given truth table set
	 * The True values or (T) is set as 1.
	 * The False values or (F) is set as 2.
	 * 
	 * @param given The variable
	 * @return the integer value according to T/F
	 */
	// setting the query value given according to the value required for the program
	public static int setValueQuery(String given, Factors queryFactor) {
		// if (given.equals("T")) {
		// 	return 1;
		// } else if (given.equals("F")) {
		// 	return 2;
		// } else {
		// 	System.out.println("Wrong value");
		// 	System.exit(0);
		// 	return 0;
		// }
		ArrayList<String> categories = queryFactor.givenCategories;
		for(int i = 0; i < categories.size(); i++) {
			if(categories.get(i).equals(given)) {
				return i + 1;
			}
		}
		System.exit(0);
		return 0;
	}

	/**
	 * Method that creates a virtual cpt table according to the xml file.
	 * 
	 * @param doc
	 */
	// function that reads the xml file in detail and creates the CPTs accordingly
	// to it
	public static void sortInputs(Document doc) {
		String mainVar; // to store the main Var
		String mainVarForCategory;
		ArrayList<String> tempGivenVars; // to store the givenVars.
		ArrayList<String> givenVars; // to store the reversed order(real).
		ArrayList<String> givenCategories = new ArrayList<String>();
		String valueSet; // stores the table as a string.

		//
		NodeList list = doc.getElementsByTagName("DEFINITION");

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element element = (Element) node;
			mainVar = null;
			tempGivenVars = new ArrayList<String>();
			givenVars = new ArrayList<String>();
			mainVar = element.getElementsByTagName("FOR").item(0).getTextContent();
			if (element.getElementsByTagName("GIVEN").item(0) != null) {
				int j = 0;
				// taking the given tag elements if they exists
				while (element.getElementsByTagName("GIVEN").item(j) != null) {
					tempGivenVars.add(element.getElementsByTagName("GIVEN").item(j).getTextContent());
					j++;
				}
			} else {
				tempGivenVars = null;
			}
			valueSet = element.getElementsByTagName("TABLE").item(0).getTextContent();
			// reversing the order of given Vars (required for the table style provided)
			if (tempGivenVars != null) {
				for (int k = tempGivenVars.size() - 1; k >= 0; k--) {
					givenVars.add(tempGivenVars.get(k));
				}
			} else if (tempGivenVars == null) {
				givenVars = null;
			}
			//creating the Factors object by invoking the constructor
			Factors eachFactor = new Factors(mainVar, givenVars, valueSet);
			//adding all the factors created to a factorsList which is globally accessible.
			factorsList.add(eachFactor);
		}

				//getting the initial categories.
				//Now we initialize the types or number of different types of values this specific random variable (main variable)has
				NodeList topList = doc.getElementsByTagName("VARIABLE");
				for(int i = 0; i < topList.getLength(); i++) {
					Node node = topList.item(i);
					Element element = (Element) node;
					mainVarForCategory = null;
					tempGivenVars = new ArrayList<String>();
					givenVars = new ArrayList<String>();
					mainVarForCategory = element.getElementsByTagName("NAME").item(0).getTextContent();
					int j = 0;
					while (element.getElementsByTagName("OUTCOME").item(j) != null) {
						tempGivenVars.add(element.getElementsByTagName("OUTCOME").item(j).getTextContent());
						j++;
					}
					givenCategories = new ArrayList<String>();
					for (int k = 0; k < tempGivenVars.size(); k++) {
						givenCategories.add(tempGivenVars.get(k));
					}
				//finally adding the categories of the values read, to the respective factors.
				for(int l = 0; l < factorsList.size(); l++) {
					if(factorsList.get(l).getMainVar().equals(mainVarForCategory)) {
						factorsList.get(l).setCategoriesGiven(givenCategories);
					}
				}
			}
	}

	/**
	 * Each factor object has a parentNode variable.
	 * This method stores the parent/given variables to all the variables.
	 */
	// method that assigns parent Nodes
	public static void assignParents() {
		for (int i = 0; i < factorsList.size(); i++) {
			if (factorsList.get(i).getGivenVars() != null) {
				ArrayList<String> givenVars = factorsList.get(i).getGivenVars();
				for (int j = 0; j < givenVars.size(); j++) {
					for (int k = 0; k < i; k++) {
						if (factorsList.get(k).getMainVar().equals(givenVars.get(j))) {
							factorsList.get(i).addParent(factorsList.get(k));
						}
					}
				}
			}
		}
	}

	/**
	 * Method that finds the factor object given the main Variable
	 * 
	 * @param variable
	 * @return factor object
	 */
	public static Factors findFactorGivenVar(String variable) {
		for (int i = 0; i < factorsList.size(); i++) {
			if (factorsList.get(i).getMainVar().equals(variable)) {
				return (factorsList.get(i));
			}
		}
		return null;
	}

	/**
	 * Method that calculates probability for P1.
	 * 
	 * @param queryFactor queryFactor object.
	 * @param value       the value the variable carries whose probability is to be
	 *                    found.
	 * @return double - probability
	 */
	// calculating probability
	public static double P1(Factors queryFactor, int value) {
		int parentValue;
		int childValue;
		int index;

		if (queryFactor.getParentFactors().size() == 0) {
			index = queryFactor.getIndexOfAssignment(value);
			return queryFactor.getValueFromIndex(index);
		}

		parentValue = value;
		childValue = value;

		int[] givenValues = { parentValue, childValue };
		int[] notValue = { parentValue, childValue };
		if (parentValue == 1) {
			notValue[1] = 2;
		} else {
			notValue[1] = 1;
		}
		double parentProbabilty = P1(queryFactor.getParentFactors().get(0), parentValue);

		double result = (parentProbabilty
				* (queryFactor.getValueFromIndex(queryFactor.getIndexOfAssignment(givenValues)))) +
				((1 - parentProbabilty) * (queryFactor.getValueFromIndex(queryFactor.getIndexOfAssignment(notValue))));
		return result;

	}

	/**
	 * Method for finding probability for P2
	 * 
	 * @param queryFactor the factor object that has the main Var
	 * @param value       the required value of the varibable to find the
	 *                    probability
	 * @param order       The order in which variable elimination is to be done.
	 * @return probabiity of P2
	 */
	public static double P2(Factors queryFactor, int value, String[] order) {
		
		////////////// TESTING OUT SUMOUT -- for BNB.xml
		// for(int i = 0; i < factorsList.size(); i++) {
		// if(factorsList.get(i).mainVar.equals("K")) {
		// Factors temp = factorsList.get(i).sumout("J");
		// for(int j = 0; j < temp.valueSet.length; j++) {
		// System.out.print(temp.valueSet[j] + " ,");
		// }
		// System.exit(0);
		// }
		// }
		//

		/////////////// TESTING JOIN  -- for BNB.xml
		// Factors one = null;
		// Factors two = null;
		// for(int i = 0; i < factorsList.size(); i++) {
		// if(factorsList.get(i).mainVar.equals("K")) {
		// one = factorsList.get(i);
		// System.out.println("done1");
		// }
		// if(factorsList.get(i).mainVar.equals("J")) {
		// two = factorsList.get(i);
		// System.out.println("done2");
		// }
		// }
		// try{
		// Factors temp = one.join(two);
		// for(int j = 0; j < temp.valueSet.length; j++) {
		// System.out.print(temp.valueSet[j] + " ,");
		// }
		// System.exit(0);
		// } catch (Exception e) {
		// System.out.println(e.getMessage());
		// }

		//A local temp factor object to store the joined factors
		Factors factorsCombined;
		ArrayList<Factors> containsVarGiven;
		// the containsGiven is a temporary variable that takes in all the factors
		// that has a specific varible from the order.
		// We go through a for loop and for each variable in the order the factors are
		// joined
		// and the variable is summed out.
		for (int k = 0; k < order.length; k++) {
			containsVarGiven = new ArrayList<Factors>();
			for (int i = 0; i < factorsList.size(); i++) {
				if (factorsList.get(i).contains(order[k])) {
					containsVarGiven.add(factorsList.get(i));
					factorsList.remove(i); // removing the factor from the factor list for joining and summing out.
					i--;
				}
			}
			// joining the factors that contain the variable.
			factorsCombined = join(containsVarGiven);
			// summing out the variable
			Factors finalFactor = factorsCombined.sumout(order[k]);
			factorsList.add(finalFactor); // adding the final factor to the factorsList.
		}
		// after the loop, if all the nuisance variable are given in the order,
		// every nuisance variable is taken care in the order
		// now if the size of factorsList is not one, then we join them togethor
		Factors finalOne;
		if (factorsList.size() != 1) {
			finalOne = join(factorsList);
		} else {
			finalOne = factorsList.get(0);
		}
		// normalizing the final factor
		finalOne.normalize();
		int index = finalOne.getIndexOfAssignment(value);
		return (finalOne.getValueFromIndex(index));
	}

	/**
	 * Method that joins an arrayList of factors.
	 * @param factors the factors that are required to be joined.
	 * @return The final joined factor.
	 */
	public static Factors join(ArrayList<Factors> factors) {
		
		ArrayList<Factors> toJoin = factors;
		Factors mainFactor = null;
		int maxVars = 0;
		int indexOfMax = 0;
		// first deciding on the biggest factor:
		for (int i = 0; i < toJoin.size(); i++) {
			if (toJoin.get(i).getTotalVarSize() > maxVars) {
				try {
					Factors temp = toJoin.get(i);
					mainFactor = (Factors) temp.clone();
					indexOfMax = i;
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		toJoin.remove(indexOfMax);
		// now mainFactor is the factor with most number of rvs
		for (int i = 0; i < toJoin.size(); i++) {
			try {
				mainFactor = mainFactor.join(toJoin.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mainFactor;
	}

	// implementing P3
	/**
	 * Method to implemnent P3
	 * @param factor The factore that contains the main variable whouse probability we need to find.
	 * @param value The value of the factor that is to be found.
	 * @param evidence ArrayList of evidence provided
	 * @return
	 */
	public static double P3(Factors factor, int value, ArrayList<String[]> evidence) {
		Factors factorsCombined;
		ArrayList<Factors> containsVarGiven;
		String resultVar = factor.getMainVar();
		// setting an arraylist evidence variables separately for ease.
		ArrayList<String> evidenceVars = new ArrayList<String>();
		for (int i = 0; i < evidence.size(); i++) {
			evidenceVars.add(evidence.get(i)[0]);
		}
		// setting the nuisance variables
		ArrayList<String> nuisanceVars = new ArrayList<String>();
		setNuisanceVars(factor.getMainVar(), evidenceVars, nuisanceVars);
		
		//until all the nuisance variables are summed out.
		while (nuisanceVars.size() != 0) {
			// assigning method for the factors
			assignFactors(evidenceVars, evidence);
			containsVarGiven = new ArrayList<Factors>();
			String toJoinVar = getOrder(nuisanceVars);
			for (int i = 0; i < factorsList.size(); i++) {
				if (factorsList.get(i).contains(toJoinVar)) {
					containsVarGiven.add(factorsList.get(i));
					factorsList.remove(i);
					i--;
				}
			}
			factorsCombined = join(containsVarGiven);
			// summing out the variable
			Factors finalFactor = factorsCombined.sumout(toJoinVar);
			factorsList.add(finalFactor);

		}
		Factors finalOne = null;
		if (factorsList.size() != 1) {
				finalOne = join(factorsList);
		} else {
			finalOne = factorsList.get(0);
		}
		//normalizing the final factor.
		finalOne.normalize();

		return (finalOne.getResult(resultVar, value, evidence, evidenceVars));
	}

	/**
	 * Method that finds and adds nuisance variables to an arraylist
	 * @param mainVar the main variable
	 * @param evidenceVars the set of evidence variables provided
	 * @param nuisanceVars the nuisance variables that are to be filled.
	 */
	public static void setNuisanceVars(String mainVar, ArrayList<String> evidenceVars, ArrayList<String> nuisanceVars) {
		outer: for (int i = 0; i < factorsList.size(); i++) {
			if (factorsList.get(i).getMainVar().equals(mainVar)) {
				continue;
			} else {
				for (int j = 0; j < evidenceVars.size(); j++) {
					if (factorsList.get(i).getMainVar().equals(evidenceVars.get(j))) {
						continue outer; //we only check the mainVar of all the factors as they are unique and will account for all the variables.
					}
				}
			}
			nuisanceVars.add(factorsList.get(i).getMainVar());
		}
	}

	/**
	 * Method that finds the nuisance variable that is linked with the least variables through greedy algortihm.
	 * The function checks each of the nuisance variables with all of the other factors for variables similarity.
	 * The number of variables in the final factor for each nuisance variable after joining the order-variable-factor
	 * with it is checked and the one with the minimum dependencies is returned. 
	 * @param nuisanceVars Set of nuisance variables
	 * @return The variable to be joined and summed out.
	 */
	public static String getOrder(ArrayList<String> nuisanceVars) {
		//order of the nusiance variables
		String[] order = new String[nuisanceVars.size()];
		String joinVar;
		int[] joinedVarSize = new int[order.length];
		ArrayList<String> allVars;

		ArrayList<String> dependentVars = new ArrayList<String>(); //variable that stores the r.v.s after joining a specific nuisance variable.
		for (int i = 0; i < nuisanceVars.size(); i++) {
			order[i] = nuisanceVars.get(i);
		}

		for (int i = 0; i < order.length; i++) {
			joinVar = order[i];
			for (int j = 0; j < factorsList.size(); j++) {
				if (factorsList.get(j).getAllVars().contains(joinVar)) {
					allVars = factorsList.get(j).getAllVars(); //all the variables of the specific factor.
					for (int k = 0; k < allVars.size(); k++) {
						if (dependentVars.contains(allVars.get(k)) || allVars.get(k).equals(joinVar)) {
							continue;
						} else {
							dependentVars.add(allVars.get(k));
						}
					}
				}
			}
			//storing the size of the total variables after joining each of the nuisance variables with others.
			joinedVarSize[i] = dependentVars.size();
			dependentVars = new ArrayList<String>();
		}
		//now we find the nuisance variable with minimum number of r.v.s on the factor after joining
		//we need the smallest one so as to not complicate the variable elimination.
		int minCount = joinedVarSize[0];
		int index = 0;
		for (int i = 0; i < joinedVarSize.length; i++) {
			if (joinedVarSize[i] < minCount) {
				minCount = joinedVarSize[i];
				index = i;
			}
		}
		nuisanceVars.remove(order[index]);
		//returning the nuisance variable after greedy selection for joining and summing out.
		return order[index];
	}

	// in the assigning process we set the alternate assignment value to zero
	/**
	 * Method that assigns the value of the evidence variable provided
	 * to all the given CPT
	 * @param evidenceVars
	 * @param evidence
	 */
	public static void assignFactors(ArrayList<String> evidenceVars, ArrayList<String[]> evidence) {

		String changingVar;
		int valToChange = 0;
		for (int i = 0; i < evidence.size(); i++) {
			changingVar = evidence.get(i)[0];
			// System.out.println(changingVar + "cahngererr");
			if (evidence.get(i)[1].equals("T")) {
				valToChange = 2;
			} else {
				valToChange = 1;
			}
			for (int j = 0; j < factorsList.size(); j++) {
				if (factorsList.get(j).getAllVars().contains(changingVar)) {
					factorsList.get(j).setZero(changingVar, valToChange);
				}
			}
		}
	}

	/**
	 * Method to validate the given BN
	 */
	public static void validate() {
		//validating each CPT.
		//Since there is a specific format for the inputs given in the .xml files
		//we take the advantage of the given format for checking the value validation
		boolean valid = true;
		float[] values;
		float sum = 0;
		int categoryLength;
		outer:for(int i = 0 ; i < factorsList.size(); i++) {
			values = factorsList.get(i).initialFloatValues;
			categoryLength = factorsList.get(i).getCategoryLength();
			for(int j = 0; j < factorsList.get(i).valueSet.length ; j++) {
				if(values[j] < 0) {
					valid = false;
					break outer;
				}
			}
			for(int j = 0; j < values.length;) {
				for(int k = 0; k < categoryLength; k++) {
				sum += values[j+k];
				}
				// sum = values[j] + values[j + 1];
				j = j + categoryLength;
				if(sum != 1) {
					valid = false;
					break outer;
				}
				sum = 0;
			}
		}
		if(!valid) {
			System.out.println("The probability table is invalid");
		} else {
			System.out.println("The given probability table is valid");
		}

	//validating the graph - if not DAG, then validation fails
	//for validating DAG we create childNodes for each random variable, which is stored 
	//in each of the object factors of each main variables.
	boolean validity = true;
	for(int i = 0; i < factorsList.size(); i++) {
		factorsList.get(i).setChildNodes(factorsList);
	}
	//printing children
	// for(int i = 0; i < factorsList.size(); i++) {
	// 	for(int j = 0; j < factorsList.get(i).childNodes.size(); j++) {
	// 	System.out.println("parent"+factorsList.get(i).getMainVar());
	// 	System.out.println(factorsList.get(i).childNodes.get(j).getMainVar());
	// 	}
	// }
	Factors checkingFactor;
	for(int i = 0; i < factorsList.size(); i++) {
		checkingFactor = factorsList.get(i);
		validity = checkChildren(checkingFactor.childNodes, checkingFactor.getMainVar());
		if(!validity) {
			break;
		}
	}
	if(!valid) {
		System.out.println("The given graph is a DAG");
	} else {
		System.out.println("The given graph is a DCG");
	}
	}

	/**
	 * Method that checks if any of the children factors has the variable we search for.
	 * This method is called recursively to check the given variable with the children variables of the children variables until the 
	 * there are no children vars for the given child variable
	 * @param children arrayLIST of childrenNodes
	 * @param var Variable to check if any of the children nodes have it.
	 * @return true/false depending whether the variable is found
	 */
	public static boolean checkChildren(ArrayList<Factors> children, String var) {
		boolean checker = true;
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i).getMainVar().equals(var)) {
				checker = false;
				return checker;
			}
		}
		for(int i = 0; i < children.size(); i++) {
			checker = checkChildren(children.get(i).childNodes,var);
			if(!checker) {
				return false;
			}
		}
		return true;
	}

}
