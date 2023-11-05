## Bayesian Network System

This Bayesian Network system, implemented in Java, enables users to perform various probabilistic inferences on Bayesian Networks (BNs). It can handle different types of BNs, not restricted to binary variables. The system covers four main parts - Simple Inference, Variable Elimination, General Inference, and Extension for BN validation.

Refer to the question.pdf for further details on the problem 

### Features

- **Part 1: Simple Inference**
  - Performs simple inference on a given chain-structured Bayesian Network.
  - Calculates the probability of a queried variable given its truth value (T/F).

- **Part 2: Variable Elimination without Evidence**
  - Reads BNs with a maximum branching factor of 2.
  - Conducts probabilistic inferences using the variable elimination algorithm.
  - Requires user input for queries and the order of variable elimination.

- **Part 3: General Inference on a General BN**
  - Extends variable elimination algorithm to handle evidence queries.
  - Automatically decides the elimination order based on given evidence.

- **Part 4: Extension**
  - Validates the given BN for correctness.
  - Checks if the provided graph is a Directed Acyclic Graph (DAG) and if conditional probability tables are valid.

### Installation and Usage

1. **Compile the Code**
   - Navigate to the `src` directory.
   - Compile the program using `javac A2main.java`.

2. **Run the Code**
   - Execute the program by running `java A2main <Pn> <Filename.xml>`.
     - Replace `<Pn>` with the part number (e.g., P1, P2, etc.).
     - Replace `<Filename.xml>` with the filename of the XML file containing the Bayesian Network.

3. **Input Instructions for Each Part**
   - Follow the prompts to input queries, evidence (if required), and variable elimination orders as per the part's requirements.

### Usage Examples with Results

- **Part 1: Simple Inference**
  ```
  Query:
  D:T

  Output:
  Probability of D = true: 0.57050
  ```

- **Part 2: Variable Elimination**
  ```
  Query:
  N:T
  Order:
  J,L,K,M,O

  Output:
  Probability of N = true: 0.59180
  ```

- **Part 3: General Inference**
  ```
  Query:
  Z:T
  Evidence:
  R:T U:T

  Output:
  Probability of Z = true given evidence (R = true, U = true): 0.71235
  ```

- **Part 4: Extension (Validation)**
  ```
  Output:
  The given Bayesian Network is validated as a Directed Acyclic Graph (DAG) and the conditional probability tables are correct.
  ```

These example sections in the README give users an idea of how the queries are formatted and the expected output results. It can assist users in understanding how to input queries and interpret the calculated probabilities or validation outcomes from each part of the Bayesian Network system.

### Implementation Details

The code is structured in two main classes:
- `A2main.java`: Contains the core implementation for all parts.
- `Factors.java`: Defines the properties and characteristics of factors used for inference.

### Testing and Verification

The system is rigorously tested using various test cases, including the STACScheck tool, ensuring its accuracy and functionality across different parts.

For detailed information, refer to the report (CS5011 – REPORT(P2)).
