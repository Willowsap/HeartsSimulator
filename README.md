# HeartsSimulator
Quick User Guide:

How to use the HeartsSimulator.

1. Decide what players you want to use:
	This Simulator comes with two players: A random player and a medium player. You may also
	implement your own players by creating a class that implements the HeartsPlayer interface. The details
	on what this class must include are outlined in the aforementioned interface. 
	
2. Text file or command line:
	When you first run HeartsSimulator it will ask if you want to use a file or the command line.
	The HeartsSimulator will need 11 pieces of information to run. You can choose to run from the 
	command line, in which case it will ask you for each piece of information. If you want to run
	the program a lot and don't want to spend so much time typing, you can create a file and run from it.
	There is a sample file in this directory called sim.txt. These files must follow this format
		
		package.playerclass1name //String
		package.playerclass2name //String
		package.playerclass3name //String
		package.playerclass4name //String
		player1name //String
		player2name //String
		player3name //String
		player4name //String
		Number of games to play // Integer
		Checkpoint Number* // Integer
		Whether or not to print details about each game** // 0 or 1
		
		*The HeartsSimulator will print a 'checkpoint' every this number of games. So, for example, if you
		choose 500, then at 500 games it will print '500 games played so far' and then again at 1000, 1500, etc
		
		**You should only choose this option if you are playing a single game and want to know what happens every round. 
		This amount of printing significantly slows down the program.
		
3. Run the HeartsSimulator program:
	Once you have entered the information, the program will begin running.
	On a computer with the following specs, here is a table of how long the program takes given how many games were played.
	
	~~~~~~~~~~~~~~~~~~~~~~
	 | #games | time(s) |
	 | 100    | 4.1     |
	 | 1000   | 9.7     |
	 | 10000  | 29.5    |
     	 | 50000  | 137.5   |
     	 | 100000 | 281.3   |
	~~~~~~~~~~~~~~~~~~~~~~
	Specs:
		Processor: AMD FX-8350 Eight-Core Processor
		Memory:    32.0GB DDR3 1866MHz
		OS:        Windows 10 Pro 64-bit
		
