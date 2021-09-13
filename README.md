Static data race detector project structure and working:-

Static data race detector is a data race detector which analyses statically the input multi task programs for industrial robots and tells us whether the multi task programs are potentially data racy or not. The implementation is done specifically for ABB Rapid programming language.
Structure of the project:


Src:-
Src consists of the parser file and associated functionalities.


Examples-
Input to our race detector is a multi -task Rapid program consisting of more than one task. Here we have a set of examples as text files one example for each rule that we implemented in the race detector.


Functionalities:-
Will shows all the functions related to our parser program . From parser file, the race detector code get the Wait instructions and synchronization instructions  for further processing.


Race Detector:-
This is the main static data race detector implementation consists of seven rules, one rule per file which checks the input Rapid program for data race.


Static data race detector working:-
DATA RACES AND OCCUR IN BETWEEN RELATIONS IN RAPID
Let P be a multi-task Rapid program. Let s1 and s2 be two instructions(can be a block of statements consisting of shared variable access also) in P, with associated commands c1 and c2. Let t1 and t2 be two tasks in the program P which may or may not executes in parallel with each other. We say that s1 and s2 are involved in a data race in P, if they are conflicting accesses that may happen in parallel in P or we can say there must be a racy situation if s2 occurs in between s1 or s1 occurs in between s2.

We now proceed to propose sufficient conditions under which one statement in a Rapid program task cannot occur-in-between another statement in another program task: -

Rule1 (Synchronizing between tasks): Following condition must hold.
Synchronization points are set in the code by using the WaitSyncTask instruction after executing s1 in task1 and before executing s2 in task2 with the same synchronisation identity points.

Rule2 (Accessing shared resources by using Flag): Following condition must hold.
-	S1 in task1 and s2 in task2 are enclosed in a block beginning with setting the persistent variable flag to true with the instruction WaitUntil TestAndSet and ending with resetting the flag to false and there will not be any instruction in between s1 or before s1 in task1 which will cause the program control goes from task1 to task2.

Rule3 (Polling among tasks): Each of the following conditions must hold.
-	In task2, before executing s2, a WaitUntil instruction is used along with the same Boolean persistent variable as used in task1. 

Rule4 (Waiting using interrupts): Each of the following conditions must hold.
-	A persistent  digital signal is set in program of first task using SETDO instruction after s1.
-	In task2, before executing s2, An ISignalDO instruction is declared with the same persistent digital signal as declared in task1 and it is used as an interrupt to start the second task.
Rule5 (SetDO-WaitDO block): Each of the following conditions must hold.

-	There is a SETDO instruction after executing s2 with a persistent digital signal.
-	There is a WaitDO instruction in task2 before executing s2 with the same persistent digital signal with the same value as in task1.

Rule6 (Using a dispatcher): Following condition must hold.
-	A digital signal used by task1 after the execution of s1 to call another task2, indicating specifically a particular routine which may contains s2 that could be executed in the called task.

Rule7 (IEnable - IDisable block): Each of the following conditions must hold.

-	s2 will be inside an IEnable – IDisable block for a common interrupt.


Steps to run the project:-
1) Import the project in your IDE(Java)
2) Set path of your input file in all the race detector program.
3) Run each rule one by one.


