Static Analysis for Error Detection in Multi-Task Industrial Robotic Programs
This project implements a static analysis tool to detect potential errors in multi-task programs for industrial robots. Specifically, it targets the ABB Rapid Programming Language and verifies the correct synchronization and usage of shared variables across multiple tasks. The analysis tool identifies whether a given multi-task program is potentially erroneous by checking adherence to a set of predefined rules.

Project Structure
Src/
Contains the core parser and functionality implementations for error and race detection.

Examples/
A collection of example input files in text format. Each file demonstrates one of the rules implemented by the race detector.

InputFiles/
Stores example Rapid programs. The filenames correspond to specific rules for easy reference.

Functional Overview
Part 1: Error Detection
This component parses multi-task Rapid programs and analyzes synchronization and task interactions. Key steps include:

Parsing Tasks: The parser extracts global variables and stores them in a globalTasklist as a LinkedHashMap<String, Bool_list>.

The Bool_list tracks variable usage across tasks.
Rule Checking: Seven rules are applied to validate task synchronization and shared variable handling. The rules are implemented in CheckAllRules/TaskAnalyzer.java.

Rules Summary
Rule 1(a): Checks for proper use of WaitSyncTask synchronization.
Rule 1(b): Validates correct SyncMoveOn and SyncMoveOff usage.
Rule 2: Ensures WaitUntilTestAndSet is used for locking and unlocking around shared variable updates.
Rule 3: Verifies polling mechanisms with WaitUntil.
Rule 4: Checks for interrupt handling with SETDO and ISignalDO.
Rule 5: Validates SETDO-WaitDO synchronization blocks.
Rule 6: Ensures dispatcher routines use shared variables correctly.
Rule 7: Confirms shared variable updates inside IEnable-IDisable blocks.
Part 2: Race Detection
This component checks for static data races. A data race occurs when:

Conflicting shared variable accesses happen in parallel.
Statements in one task occur in-between shared variable accesses in another task.
Each rule addresses specific synchronization scenarios, ensuring tasks access shared resources without conflicts.

Assumptions
Robot names follow the format: T_ROB1, T_ROB2, T_ROB3, T_ROB4.
Persistent task variables use the format: task1, task2, etc.
Input files are structured with tasks separated by BEGINTASK-ENDTASK blocks.
How to Run the Project
Import the project into a Java IDE (e.g., Eclipse, IntelliJ).
Set the path to the input Rapid program file in the race detector.
Run the rules individually to validate the input program:
Rule 1(a): CheckAllRules/TaskAnalyzer.java (Line 64)
Rule 1(b): CheckAllRules/TaskAnalyzer.java (Line 65)
Follow the remaining lines for other rules.
Output
For each rule, the program outputs:
Set of shared variables: Identifies shared variables across tasks.
Validation results: Indicates whether the program adheres to the rules or contains errors.
Example Inputs
Refer to the Examples/ and InputFiles/ directories for sample multi-task Rapid programs. Each file is named to correspond with the rule it demonstrates.

Future Scope
Extend support for additional synchronization patterns.
Generalize the tool for other robotic programming languages.
Integrate dynamic analysis for runtime verification.

