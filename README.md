# Bayesian Network Inference
This project implements Bayesian network inference using Java. It includes several classes that together allow reading a Bayesian network from an XML file, performing inference queries using different algorithms, and writing the results to an output file.

# What is a Bayesian Network?
A Bayesian network, also known as a belief network or a probabilistic graphical model, is a graphical representation of probabilistic relationships among a set of variables. It provides a way to model and reason about uncertainty and dependencies in a system.

In a Bayesian network, variables are represented as nodes, and the relationships between variables are represented as directed edges or arcs. Each node in the network represents a random variable, and the edges represent the dependencies between variables. The strength of the dependencies is quantified by conditional probability distributions.

The structure of a Bayesian network is typically defined by an expert or learned from data. Once the structure is defined, the network can be used to answer probabilistic queries about the variables in the system, such as computing the probability of an event given evidence or performing inference on hidden variables.

# What is the purpose of Bayesian Networks?
The purpose of Bayesian networks is to model and reason about uncertainty and dependencies in a system. They provide a framework for representing and manipulating probabilistic knowledge, enabling efficient inference and decision-making under uncertainty.

Some common applications of Bayesian networks include:
* Medical diagnosis: Bayesian networks can be used to model the relationships between symptoms, diseases, and test results, allowing for efficient and accurate diagnosis.
* Risk assessment: Bayesian networks can model the dependencies between different risk factors and help in assessing and managing risks in various domains, such as finance, insurance, and engineering.
* Decision support systems: Bayesian networks can assist in decision-making by providing a probabilistic framework for evaluating different options and considering uncertain information.
* Predictive modeling: Bayesian networks can be used for predictive modeling tasks, such as predicting customer behavior, forecasting market trends, or analyzing sensor data.

# Algorithm Purpose:
The project includes two algorithms for performing inference in Bayesian networks:

1. Simple Inference: This algorithm performs inference by explicitly enumerating all possible combinations of values for the variables in the network. It calculates the joint probability distribution of the query variable and the evidence variables using the chain rule of probability. The purpose of this algorithm is to provide a simple and straightforward method for inference in small Bayesian networks.

2. Variable Elimination: This algorithm performs inference by eliminating variables from the network one at a time, based on their irrelevance to the query variable. It factors the joint probability distribution into a product of factors, eliminates irrelevant variables, and performs variable elimination operations to compute the desired probabilities. The purpose of this algorithm is to improve the efficiency of inference by avoiding unnecessary computations in large Bayesian networks.


# Input File Format
The input file should follow a specific format. It consists of:

* The first line: The name of the XML file that represents the Bayesian network.
* Subsequent lines: Queries to be answered by the Bayesian network and the number of algorithms to be used for inference on the given query .

Example input.txt file:

<div dir='ltr'>

    big_net.xml
    P(D1=T|A1=T,A2=F,A3=T,C1=T,C2=v1),2
</div>

# Code Structure
The project consists of several Java classes:

* Ex1.java: The main class that serves as the entry point for the program. It reads the input file, builds the Bayesian network, performs inference queries using different algorithms, and writes the results to the output file.
* Factor.java: Represents a factor in the Bayesian network. It provides methods for factor operations such as variable elimination and pointwise product.
* Network.java: Represents the Bayesian network. It stores the variables and factors of the network, provides methods for network operations, and performs inference using different algorithms.
* Variable.java: Represents a variable in the Bayesian network. It stores the variable's name, parents, outcomes, and conditional probability table (CPT).
* XmlReader.java: Reads the XML file containing the Bayesian network structure and builds the variables of the network.

# Running the Program
1. Prepare the input file following the specified format.
2. Run the Ex1 class, providing the input file as an argument.
3. The program will perform inference queries using the Bayesian network and write the results to the output file.

# Output
The program will generate an output.txt file containing the results of the inference queries. Each line in the output file corresponds to a query in the input file and includes the probabilities or values calculated by the specified algorithm, along with the number of addition and multiplication operations performed.

Example output.txt file:
<div dir='ltr'>

    0.39160,21,48
  
</div>
