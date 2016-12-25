
Instructions on how to run program

1. Replace the input_program_name with the desired input file name along with extension.
2. Compile the Phase1.java using command javac Phase1.java  (Ignore all the warnings)
3. Run java file using java Phase1 filename   ie  java Phase1 foo.c
4. Check the working directory for newly generated file.
5. If input program doesn't follow rules, console will display "the input program is illegal" but it will also generate incorrect output file.
6. If input program is valid, console will display nothing and will generate a correct output file.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------

Implementation Methodology

The methodology used is table driven approach described in the textbook. The structure of program is similar to what is given on page 61 of our textbook.
Although some changes have been made to correctly predict tokens.
We have used 2D array to represent transitions occuring in the DFA. Transition table is a 9*9 matrix (See the pdf for details)



--------------------------------------------------------------------------------------------------------------------------------------------------------------------

Problems in the scanner

1. The scanner doesn't understand the difference between individual symbols. So on encountering a symbol it generates a token <symbol> rather than specific token such as 
<left_brace>,<comma>,<semicolon>.


  (==  the output is <symbol><symbol><symbol> 
       the output should have been <left_parenthesis><equal_sign><equal_sign>

Possible Solution : We could get rid of the symbols from DFA and just token them using conditional statements. 
 
if(NextChar=='(')
   token="<left_parenthesis>";
if(NextChar==',')
   token="<comma>";

2. The scanner cannot identify "<=", ">=","&&", "||"  symbols with two characters. It classifies them as two individual separate symbols.

  && output is <symbol><symbol>
     output should be <double_and_sign> 

Possible solution: We could look one character ahead in our input to check if we have two character symbols such as &&,<=,>= etc. 

3. Due to above two problems scanner still processes input such as '&', '|' which are not defined in our language.
   To resolve this issue we must resolve problems 1 and 2

4. The scanner cannot recognize strings between (and including) the closest pair of quotation marks. 
  
   "abcd" if
   The output is <string> which is wrong
   The output should have been <string><ws><reserved_word>. Here <ws> stands for white spaces.	
   
    		

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Improvement over existing code

The existing code tries to solve specific errors by introducing new statements. This can be avoided by creating a robust DFA and corresponding transition table 
which can account for all the possible inputs combinations. In this way an approach which is exactly similar to the textbook approach can be made to tokenize
the input program.
