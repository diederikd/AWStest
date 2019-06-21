# Starting a AWS statemachine from a AWS lambda function

This repo contains a java project with a sample of how to start a AWS statemachine from a AWS lambda function

# Run this demo

I created this project using IntelliJ IDEA community edition 2019.1. In this IDE I installed the AWS toolkit version 1.3.
I runned this code using a local installation of sam (aws-sam-cli).

# Start function

The code contains a class Statemachine with a function start.
The function has as a input parameter the stateMachineArn.
The function returns an executionArn.

# My Purpose

I want to use this code in connecting the different state machines which I generate from the Demo Workbench. (See my DemoWB repo.)
From each transaction a AWS statemachine is generated. Demo has initiation links between transactions.
This source is a sample for the implementation of this initiation link.
