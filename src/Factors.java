import java.util.ArrayList;


/**
 * This class deals with all teh functionalities of a factor.
 * For P1 we use the same class as the probability alternative.
 * The class contains given variables, main variables and parent variables which are mainly used in P1.
 */
public class Factors implements Cloneable {
    // the main variable to which the CPT belongs to
    String mainVar;
    // the arrayList of the given variables stores separately
    ArrayList<String> givenVars = new ArrayList<String>();
    // all the variables stored in a single arrayList in a specific order
    ArrayList<String> allVars = new ArrayList<String>();
    // String that stores the initial value set (table).
    String intialValues;
    //given categories from file
    ArrayList<String> givenCategories = new ArrayList<String>();
    // Given values as it is in float format - for validation
    float[] initialFloatValues;
    // finally storing each value as an array of float values.
    double[] valueSet;
    //variable to stores an integer value for the number of categories according to the total vars of the factor
    int[] categories;
    //storing the number of categories/division for each of the vars in the program
    public static ArrayList<Integer> eachCategories = new ArrayList<Integer>();
    //categories is stored for each of the vars
    public static ArrayList<String> vars = new ArrayList<String>();
    //WE need another arraylist of arraylist that stores the converted values - i.e; from T,F - 1,2
    public static ArrayList<ArrayList<Integer>> eachCategoryValues = new ArrayList<ArrayList<Integer>>();
    //stride
    int[] strideOfVars;
    // Factor that stores the details of the parent variable (or dependency
    // variable)
    ArrayList<Factors> parentVariable = new ArrayList<Factors>();
    //Factor that stores the details of childNodes of the mainVAR of this object -- used and invoked only in the case of validation.
    ArrayList<Factors> childNodes = new ArrayList<Factors>();

    Factors(String mainVar, ArrayList<String> givenVars, String intialValues) {
        this.mainVar = mainVar;
        this.givenVars = givenVars;
        this.intialValues = intialValues;
        setValues();
        setAllVars();
        setFloatValues();
    }

    /**
     * Method that fills in the initial categories of the mainVar and the stride for
     * the factor.
     */
    public void completeAll() {

        setInitialCategories();
        setStrideForCategory();
        
    }

    /**
     * Method that sets the categories.
     * categories is the variable that stores the number of types of variables
     * for each variable in a factor.
     * The index of categories and allVars are in sync making the retrieval easy.
     */
        public void setInitialCategories() {

        categories = new int[getTotalVarSize()];
        categories[0] = getCategoryLength();
        if(parentVariable.size() != 0) {
            for(int i = 0; i < parentVariable.size(); i++) {
             categories[i+1] = parentVariable.get(i).getCategoryLength() ;  
            }
        }

     }

    public void setCategoriesGiven(ArrayList<String> givenCategories) {
        this.givenCategories = givenCategories;
    }
    /**
     * Method to setup the static variables used by all the objects
     * Such as variables  - all the variables in the program
     * eachCategory - how many categories of each variable exist
     * eachCategoryValues - converted simplified integer values of eachCategory
     * @param allfactors
     */
    public void setup(ArrayList<Factors> allfactors) {
        for(int i = 0; i < allfactors.size(); i++) {
            if(vars.contains(allfactors.get(i).getMainVar())) {
                continue;
            } else {
                vars.add(allfactors.get(i).getMainVar());
                eachCategories.add(allfactors.get(i).getCategoryLength());

            }
        }
        ArrayList<Integer> eachCatValue;
        for(int i = 0; i < eachCategories.size(); i++) {
            eachCatValue = new ArrayList<Integer>();
            for(int j = 1; j <= eachCategories.get(i); j++) {
                eachCatValue.add(j);
            }
            eachCategoryValues.add(eachCatValue);
        }
    }

    /**
     * Initializes the stride value for each variable of the factor.
     * They are connected by the same index.
     */
    public void setStrideForCategory() {
        strideOfVars = new int[getTotalVarSize()];

        strideOfVars[0] = 1; //this is always the case
        for(int i = 1; i < getTotalVarSize(); i++) {
            strideOfVars[i] = strideOfVars[i - 1] * categories[i-1];
        }
    }


    public void setCategories() {
        int index;
        categories = new int[getTotalVarSize()];
        for(int i = 0; i < getTotalVarSize(); i++) {
            index = vars.indexOf(allVars.get(i));
            categories[i] = eachCategories.get(index);
        }

    }

    // /**
    //  * Printing for testing purposes.
    //  * @return
    //  */
    // public void printing(){
    //  
    //     for(int i = 0; i < vars.size(); i++) {
    //         System.out.println(vars.get(i));
    //     }
    //     for(int i = 0; i < eachCategories.size(); i++) {
    //         System.out.println(eachCategories.get(i));
    //     }
    // }

    /**
     * Method that returns the category length of the mainVar only
     */
    public int getCategoryLength() {
        return givenCategories.size();
    }
    /**
     * Method that returns the mainVariable of the factor.
     * @return main Variable
     */
    public String getMainVar() {
        return mainVar;
    }

    /**
     * Method that returns the given variables.
     * @return an arraylist of given variables
     */
    public ArrayList<String> getGivenVars() {
        return givenVars;
    }

    /**
     * Method that returns the total variables that are in the factor.
     * @return size of total variables
     */
    public int getTotalVarSize() {
        return allVars.size();
    }

    /**
     * Method to return all the variables
     * @return all variables
     */
    public ArrayList<String> getAllVars() {
        return allVars;
    }

    /**
     * Method that removes a variable from the factor.
     * @param var to be removed
     */
    public void removeVar(String var) {
        this.getAllVars().remove(var);
    }

    /**
     * Method that adds a factor to the parentVariable variable
     * @param factor factor object to be added.
     */
    public void addParent(Factors factor) {
        parentVariable.add(factor);
    }

    /**
     * Method that returns the parentVariable
     * @return an arrayList of factors.
     */
    public ArrayList<Factors> getParentFactors() {
        return parentVariable;
    }

    /**
     * Method that fills allVars variable with all the variable involved in the factor.
     */
    public void setAllVars() {
        allVars.add(mainVar);
        if (givenVars != null) {
            for (int i = 0; i < givenVars.size(); i++) {
                allVars.add(givenVars.get(i));
            }
        }
    }

    /**
     * Method that fills the value set of the factor
     */
    public void setValues() {

        String[] eachVal = intialValues.split(" ");

        valueSet = new double[eachVal.length];
        for (int i = 0; i < eachVal.length; i++) {
            valueSet[i] = Float.parseFloat(eachVal[i]);
        }
    }

    /**
     * Method that fills the initialvalue set of the factor - used for validation
    */
    public void setFloatValues() {
        String[] eachVal = intialValues.split(" ");
        initialFloatValues = new float[eachVal.length];
        for (int i = 0; i < eachVal.length; i++) {
            initialFloatValues[i] = Float.parseFloat(eachVal[i]);
        }
    }

    /**
     * Method that returns the deep copy of the factor object.
     */
    public Object clone() throws CloneNotSupportedException {
        Factors clone = (Factors) super.clone();
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < clone.getTotalVarSize(); i++) {
            temp.add(clone.getAllVars().get(i));
        }
        clone.allVars = temp;
        valueSet = valueSet.clone();
        return clone;
    }

    // method to add extra variables to the factor
    // this especially comes handy when joining two factors and the variables in the
    // final factor
    // are the combined variables of both the factors
    public void addVars(String a) {
        allVars.add(a);
    }

    // method that returns the index given the assignment in a specific order.
    /**
     * Method that gets in the variables aeeee amoni9ng. 
     * @param assignment 
     * @return the index of value .
     */
    public int getIndexOfAssignment(int assignment) {
        int strideOfVars = 1;
        int index = 0;
        index += (strideOfVars * (assignment - 1));
        return index;
    }

    // /**
    //  * Overloading trhe get index from assignment function to incorporate 
    //  * an array
    //  * @param assignment Array of values that match the assignment of the 
    //  * variables in the respective orders in single row.
    //  * @return int index of assignment.
    //  */
    // public int getIndexOfAssignment(int[] assignment) {
    //     int[] strideOfVars = new int[assignment.length];
    //     int index = 0;
    //     for (int i = 0; i < assignment.length; i++) {
    //         strideOfVars[i] = (int) Math.pow(2, i);
    //     }
    //     for (int i = 0; i < assignment.length; i++) {
    //         index += (strideOfVars[i] * (assignment[i] - 1));
    //     }
    //     return index;
    // }
        /**
     * Overloading trhe get index from assignment function to incorporate 
     * an array
     * @param assignment Array of values that match the assignment of the 
     * variables in the respective orders in single row.
     * @return int index of assignment.
     */
    public int getIndexOfAssignment(int[] assignment) {
        int index = 0;
        for(int i = 0; i < assignment.length; i++) {
        }
        for (int i = 0; i < assignment.length; i++) {
            index += (strideOfVars[i] * (assignment[i] - 1));
        }
        return index;
    }

    /**
     * method to get value from the given index
     * @param index - index
     * @return value of the index
     */
    public double getValueFromIndex(int index) {
        return this.valueSet[index];
    }

    /**
     * method that return the assignment according to the variable order given the
     * index.
     *  note that in the assignment, 1 is for T and 2 is for F.
     * @param index The index of the valuesSet 
     * @return assignment of the variables
     */
    public ArrayList<Integer> getAssignmentOfIndex(int index) {

        ArrayList<Integer> assignment = new ArrayList<Integer>();

        // for (int i = 0; i < strideOfVars.length; i++) {
        //     if ((index / strideOfVars[i]) % 2 == 0) { //if the index divided by the variable stride is even
        //         assignment.add(1);
        //     } else if ((index / strideOfVars[i]) % 2 != 0) { //if the index divided by the variable stride is odd.
        //         assignment.add(2);
        //     }
        // }
        for (int i = 0; i < strideOfVars.length; i++) {
            assignment.add(((index/strideOfVars[i])%categories[i]) + 1);
        }
        return assignment;
    }

    /**
     * method for summing out a variable from a factor.
     * @param var The variable to be summed out.
     * @return A factor object from which var has been summed out.
     */
    public Factors sumout(String var) {
        try {
            Factors sumOutFactor = (Factors) this.clone();
            int indexOfSummingVar = this.getAllVars().indexOf(var);
            sumOutFactor.removeVar(var);
            sumOutFactor.valueSet = new double[(this.valueSet.length) / categories[indexOfSummingVar]]; //new valueset

            double summingValue = 0; //temporary summing variable
            //since the variables in the factor changes we need to invoke these.
            sumOutFactor.setCategories();
            sumOutFactor.setStrideForCategory();
            //variable to store the original index of the factor -- this is kept to compare and retrieve the required values
            int[] indexOfVarOriginal = new int[this.getTotalVarSize()];
            ArrayList<Integer> eachAssignment;
            //assignment for the original factor
            int[] assignmentForOriginal = new int[this.getTotalVarSize()];
            for (int i = 0; i < this.getTotalVarSize(); i++) {
                for (int j = 0; j < sumOutFactor.getTotalVarSize(); j++) {
                    if (getAllVars().get(i).equals(sumOutFactor.getAllVars().get(j))) {
                        indexOfVarOriginal[i] = j;
                        break;
                    }
                }
            }
            for (int i = 0; i < sumOutFactor.valueSet.length; i++) {
                eachAssignment = sumOutFactor.getAssignmentOfIndex(i);

                for (int j = 0; j < assignmentForOriginal.length; j++) {
                    if (j == indexOfSummingVar) {
                        continue;
                    }
                    assignmentForOriginal[j] = eachAssignment.get(indexOfVarOriginal[j]);
                }
                // initializing the to be summed variable and adding then both
                ArrayList<Integer> valuesOfVariable = eachCategoryValues.get(vars.indexOf(var));
                summingValue = 0;
                for(int k = 0; k < valuesOfVariable.size(); k++) {

                    assignmentForOriginal[indexOfSummingVar] = valuesOfVariable.get(k);
                    summingValue += this.valueSet[this.getIndexOfAssignment(assignmentForOriginal)];
                }
                sumOutFactor.valueSet[i] = summingValue;

            }
            return sumOutFactor;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }


    /**
     * Method to join two factors and return a final factor
     * @param factor the factor object to be joined
     * @return A factor object 
     * @throws CloneNotSupportedException
     */
    public Factors join(Factors factor) throws CloneNotSupportedException {
        // throws CloneNotSupportedException {
        Factors finalFactor = (Factors) this.clone();
        ArrayList<String> toAdd = new ArrayList<String>();

        for (int i = 0; i < factor.getTotalVarSize(); i++) {
            //adding the variables that are not common to both the factors
            toAdd.add(factor.getAllVars().get(i));

            for (int j = 0; j < finalFactor.getTotalVarSize(); j++) {
                if (finalFactor.getAllVars().get(j).equals(factor.getAllVars().get(i))) {
                    //remove if the variable is repeated
                    toAdd.remove(factor.getAllVars().get(i));
                    break;
                }
            }
        }
        //adding the non-common, non-exisiting varibles into the factor.
        for (int i = 0; i < toAdd.size(); i++) {
            finalFactor.addVars(toAdd.get(i));
        }
        
        //calling setCategories and set Stride as the number of variables have changed.
        //These 2 functions are crucial to get the index and value of a specific assignment correctly.
        finalFactor.setCategories();
        finalFactor.setStrideForCategory();
        // setting the size of the valueSet
        int size = 1;
        for(int i = 0; i < finalFactor.categories.length; i++) {
            size *= finalFactor.categories[i];
        }
        // finalFactor.valueSet = new double[(int) Math.pow(2, finalFactor.getTotalVarSize())];
        finalFactor.valueSet = new double[size];
        //Two matrices are created to stores the indexes of the variables on multiplier and current
        //comparing it to the finalFActor.

        int[] indexOfVarFromMultiplier = new int[factor.getTotalVarSize()];
        int[] indexOfVarFromCurrent = new int[this.getTotalVarSize()];
        for (int i = 0; i < indexOfVarFromMultiplier.length; i++) {
            for (int j = 0; j < finalFactor.getTotalVarSize(); j++) {
                if (factor.getAllVars().get(i).equals(finalFactor.getAllVars().get(j))) {
                    indexOfVarFromMultiplier[i] = j;
                    break;
                }
            }
        }
        for (int i = 0; i < indexOfVarFromCurrent.length; i++) {
            for (int j = 0; j < finalFactor.getTotalVarSize(); j++) {
                if (getAllVars().get(i).equals(finalFactor.getAllVars().get(j))) {
                    indexOfVarFromCurrent[i] = j;
                    break;
                }
            }
        }
        // assigning values to the truthtable for finalFactor
        //now we get the values accordingly to the index stored earlier.
        ArrayList<Integer> eachAssignment;
        //the assignment variable in general has the categorically assigned values as mentioned in the report which makes it easier
        //to find the index in the valueSet or vise-versa
        int[] assignmentForMultipler = new int[indexOfVarFromMultiplier.length];
        int[] assignmentForCurrent = new int[indexOfVarFromCurrent.length];
        double valueFromCurrent = 0;
        double valueFromMultiplier = 0;
        for (int i = 0; i < finalFactor.valueSet.length; i++) {
            eachAssignment = finalFactor.getAssignmentOfIndex(i);
            for (int j = 0; j < assignmentForCurrent.length; j++) {
                assignmentForCurrent[j] = eachAssignment.get(indexOfVarFromCurrent[j]); //the assignment for currentfactor (main - multiplier) found from the indexes saved earlier 
            }                                                                           
            for (int j = 0; j < assignmentForMultipler.length; j++) {
                assignmentForMultipler[j] = eachAssignment.get(indexOfVarFromMultiplier[j]); //same thing is done with the multiplier
            }
            valueFromCurrent = this.valueSet[this.getIndexOfAssignment(assignmentForCurrent)];
            valueFromMultiplier = factor.valueSet[factor.getIndexOfAssignment(assignmentForMultipler)];
            // setting the values
            finalFactor.valueSet[i] = valueFromCurrent * valueFromMultiplier; //finally with the help of the assignment we find the values which are then multiplied to get the final value.
        }
        return finalFactor;
    }

    // method that checks if a given r.v exists in the factor or not.
    /**
     * Method that checks if a given r.v exists int eh factor or not.
     * @param var variable to be checked
     * @return Boolean value
     */
    public boolean contains(String var) {
        for (int i = 0; i < getTotalVarSize(); i++) {
            if (getAllVars().get(i).equals(var)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to normalize the given factor.
     */
    public void normalize() {
        double sum = 0.0;
        for (int i = 0; i < valueSet.length; i++) {
            sum += valueSet[i];
        }
        for (int i = 0; i < valueSet.length; i++) {
            valueSet[i] = valueSet[i] / sum;
        }
    }

    /**
     * Method that sets the valueSet of the given variable with the given value to zero
     * @param changingVar Variable
     * @param valueToChange value of the variable
     */
    public void setZero(String changingVar, int valueToChange) {
        
        int indexOfVar = getAllVars().indexOf(changingVar);
        ArrayList<Integer> eachAssignment = new ArrayList<Integer>();
        for (int i = 0; i < valueSet.length; i++) {
            eachAssignment = getAssignmentOfIndex(i);
            // //PRINTING EACH ASSIGNMENT
            // System.out.println("each assignment");
            // System.out.println(indexOfVar);
            // System.out.println(eachAssignment.size());
            // System.out.println(getTotalVarSize());
            // //
            if (eachAssignment.get(indexOfVar) == valueToChange) {
                this.valueSet[i] = 0;
            }
        }
        // System.out.println(changingVar + "ivneeeeeeeeeeeeee");
        // System.out.println("myre ivde ivde");
        // for (int i = 0; i < valueSet.length; i++) {
        //     System.out.println(valueSet[i]);
        // }
        // System.out.println("myre ivde ivde");
    }

    /**
     * Method to get the result for P3 specifically
     * @param resultVar The final variable
     * @param valueToFind The value of the final variable given
     * @param evidence evidence provided
     * @param evidenceVars evidence vars for ease of coding
     * @return - th probability calculated.
     */
    public double getResult(String resultVar, int valueToFind, ArrayList<String[]> evidence,
            ArrayList<String> evidenceVars) {
        int finalIndex;
        int[] valueOfEvidence = new int[evidence.size() + 1];
        int[] assignment = new int[getTotalVarSize()];

        // for (int i = 0; i < evidenceVars.size(); i++) {
        //     if (evidence.get(i)[1].equals("T")) {
        //         valueOfEvidence[i] = 1;
        //     } else if (evidence.get(i)[1].equals("F")) {
        //         valueOfEvidence[i] = 2;
        //     }
        // }
        for(int i = 0; i < getTotalVarSize(); i++) {
            if(getAllVars().get(i).equals(resultVar)) {
            ArrayList<Integer> categoryValues = eachCategoryValues.get(i);
            ArrayList<String> givenVar = givenCategories;
            for(int j = 0; j < evidenceVars.size(); j++) {
                for(int k = 0; k < categoryValues.size(); k++) {
                if(evidence.get(j)[1].equals(givenVar.get(k))) {
                    valueOfEvidence[j] = categoryValues.get(k);
                }
                }
            }
            }
        }

        for (int i = 0; i < getTotalVarSize(); i++) {
            for (int j = 0; j < evidenceVars.size(); j++) {
                if (getAllVars().get(i).equals(evidenceVars.get(j))) {
                    assignment[i] = (valueOfEvidence[j]);
                }
            }
            if (getAllVars().get(i).equals(resultVar)) {
                assignment[i] = (valueToFind);
            }
        }

        finalIndex = getIndexOfAssignment(assignment);
        return (valueSet[finalIndex]);
    }

    /**
     * Method to fill the childNodes
     */
    public void setChildNodes(ArrayList<Factors> factorsList) {
        ArrayList<String> givenVarsOfEach;
        for(int i = 0; i < factorsList.size(); i++) {
            givenVarsOfEach = factorsList.get(i).getGivenVars();
            if(givenVarsOfEach == null) {
                continue;
            }
            for(int j = 0; j < givenVarsOfEach.size(); j++) {
                if(givenVarsOfEach.get(j).equals(getMainVar())) { //the main var is checked in each of the givenVar
                    childNodes.add(factorsList.get(i));
                }
            }
        }
    
}
}