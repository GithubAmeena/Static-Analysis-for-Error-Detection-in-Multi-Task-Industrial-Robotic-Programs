Static analysis for error detection in multi-task Industrial robotic programs

This project is an error  detector which analyses statically the input multi task programs for industrial robots and tells us whether the multi task programs are potentially erronious or not. The implementation is done specifically for ABB Rapid programming language.
Structure of the project:


Src:-
Src consists of the parser file and associated functionalities.


Examples-
Input to our race detector is a multi -task Rapid program consisting of more than one task. Here we have a set of examples as text files one example for each rule that we implemented in the race detector.


Functionalities:-
Will shows all the functions related to our parser program . From parser file, the error detector and race detector code get the Wait instructions and synchronization instructions  for further processing.
Part 1: Error Detection:-
The project basically checks for ERRORS among the different tasks in a multitasking Rapid programs. For that first we implement a Parser for parsing Rapid program tasks. We different functionalities like MangeWaitInstructions,ManageSyncinstructions, etc. We parse through each and every sentences in the program line by line and stored the output. In Rapid we know global variables are declared using PERS in each tasks. So during parsing, they extracted the global variables and store them in "globalTasklist" in the form of LinkedHashmap<String, Bool_lis>, where the bool list comprises of 4 Boolean representing them to be true if they are present in the corresponding Roboy. The next step in our project is analysing the different RULES(1 to 7) defined later, starting with the WaitSyncTask(Rule#1(a)) and SyncMoveOn-SyncMoveOff which is Rule#1(b) (Line 64-65 of CheckAllRules/TaskAnalyzer.java).Then the Rule#2 (Line 66), Rule#3 (Line 67) , Rule#4, ... Rule#7(Line 71) are checked by calling the corresponding function in  CheckAllRules/TaskAnalyzer.java file. The input to that file is a multitask Rapid program consists of two or more tasks (Each "TASK1" is Robot1 ,"TASK2" is Robot2 ,"TASK3" is Robot3 ,"TASK4" is Robot4) which is seperated by using BEGINTASK-ENDTASK commands. We need to check whether the RULES are satisfied or not . Rules are nothing but whether there exists some Wait instructions and Sync instruction in a particular order around the same set of shared variables in ALL the 4 Robots. There are totally 7 rules to check. Implemented one(Rule 1(a) using WaitSyncTask instruction and Rule 1(b) using SyncMove instruction). It works fine. So as an output we need the set of shared variables in all the tasks, and check whether the Wait instruction is placed in the correct order as specified in the Rule in both the tasks. Finally, output the results like the multitask program has an ERROR or not. If you compile the already given project, you will understand how all the rules are working. I'm attaching examples  here to understand the rules, I also attaching the same examples as seperate input files(name of each input file is according to the rules) along with the project.So you can pick the files directly from there ( In the "InputFiles" folder).


RULES IMPLEMENTED in the project are :
Rule 1(a):- 
	Check for All the Robots whether there is a WaitSyncTask instruction in the first task after the updation of all the shared variables, and before the updation of the same set of shared variables in the other tasks. Also we check that whether for a WaitSyncTask called out , there exists a VAR syncident or not . And we check that there is no duplication of WaitSync calls or there isnt any PERS "task" variable which doesn't exist in the globalTasklist , but is used for a WaitSync call .  These all conditions of WaitSync should be same in each of the corresponding calls according to the Robots of that particular PERS "task" in globalTasklist.
Rule 1(b):- 
	Check for All the Robots whether there is a SyncMove block instruction in the first task after the updation of all the shared variables, and before the updation of the same set of shared variables in the other tasks. Also we check that whether for a SyncMove block called out , there exists a VAR syncident or not corresponding to the syncident used for SyncMoveOn and SyncMoveOff. And we check that there is no duplication or NESTING of SyncMove  calls or there isnt any PERS "task" variable which doesn't exist in the globalTasklist , but is used for a WaitSync call . Also the syncident variable of SyncMoveOn and SyncMoveOff of a SyncMove block must not be same and the order of the id of MOVE commands in  between them should be the same. These all conditions of SyncMove block should be same in each of the corresponding calls according to the Robots of that particular PERS "task" .
Rule 2:- 
	Check whether there is a WaitUntilTestAndSet(consider as a lock) instruction along with a boolean variable  before  the updation of all the shared variables and the same boolean variable is set to false (consider as an unlock)after  the updation of shared variables  in both the tasks.
Rule 3:-
	Check whether there is a boolean variable that is set to true  in the first task before the updation of all the shared variables, and there is a WaitUntil instruction with the same boolean variable before the updation of the same set of shared variables in the second task.
Rule 4:-
	Check whether there is a SETDO instruction with a digital signal(eg.do1) after the updation of all the shared variables in the first task, and there is an ISignalDO instruction with the same digital output (eg.do1) before the updation of all shared variables in the second task.
Rule 5:-
	Check whether there is a SETDO instruction with a digital signal(eg.do1) in the first task after the updation of all  the shared variables, and a WaitDO instruction with the same digital signal(eg.do1)before the updation of the same set of shared variables in the second task.
Rule 6:-
	There is a global variable as string is shared in both the tasks using PERS and in the first task after updation of all the shared variables call a procedure in the second task using that routine string, and check whether the corresponding  procedure is defined in the second task and that procedure uses the shared variables as in the first task.
Rule 7:-
    Check whether there is a set of shared variables in both the tasks and check whether in the second task the set of shared variables are declared as a trap routine inside an IEnable-IDisable block inside main.


ASSUMPTIONS :-
1. The Robot names should be of the form "T_ROB1","T_ROB2","T_ROB3"and "T_ROB4" .
2. The PERS task names should be of the form "task1","task2",etc.
3. The input file of RAPID code should be of the form RObot1->RObot2->RObot3->RObot4


Part 2:Race Detector:-
This is the static data race detector implementation consists of seven rules, one rule per file which checks the input Rapid program for data race.


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


