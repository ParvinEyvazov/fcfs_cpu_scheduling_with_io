# fcfs_cpu_scheduling_with_io
FCFS (First Come First Served) is a cpu scheduling algorithm in Operating Systems. This is an modified verion of FCFS that Processes has IO times.

## Explantion of code

There is a text file that includes our processes and these processes has their own tuples.This tuples includes CPU burst and I/O burst times. The program have to schedule them that makes that CPU is going to work in every possible time or in other words, this program is a FCFS scheduling algorithm with IO.
The program is using a double Hashmap. Hashmap has the KEY and VALUE parts. In the KEY part program holds process_id`s and in the value part of this key, program holds a double ArrayList<ArrayList<Integer>>. This double arraylist holds RETURN_TIME, PROCESS_OVER ( boolean (0/1)), TUPLES.
In the first Arraylist we will just hold the return time of this process.It will start with 0. And in the second arraylist we will hold just 0 or 1. It will start with 0, when all tuples are over it turns to 1 .   
1 means that this process is over. And after that each of the rest arraylists holds each tuples. But each has extra parameter. For example our process is like that: 

```bash
1:(45,15);(16,20);(80,10);(40,-1)
```

It is 1st process and has 4 tuples. We firstly hold them like this:

```bash
[[0],[0],[45,15,0],[16,20,0],[80,10,0],[40,-1,0]]
```
First 2 arraylist which hold 0s are RETURN_TIME, PROCESS_OVER (boolean(0/1)).  This extra 0 in tuple arraylists –are for checking the each tuple that this tuple is completed before or not. I can also push the tuples when program used them ,but I didn`t want to delete anything after using. Because of that I have to use this “0” parameter. When program use this tuple or when this tuple is over, 0 turns to 1. Because of that when we check , we can understant that whose turn is this.


## How It Works?
