<<<<<<< HEAD
## Getting started
Why this project started!? Because we're forced to do it to get 4 points from 22 points :)

and what is this project!? An java based strategy game such that didn't have any logic, why!?
 Because its scenario built in very very short time.

Enough talking, let's see how this project was implemented.

## Deploying
### Data structures
For saving data's in RAM we need some data structures such that:

- Save data's in a array, but it's capacity can increase.
- Save data's in a structure and for access to them use a key and get value that connected to key.

Any one who worked a little bit with java and has a sound mind, says use LinkedList and HashMap;
But we're not allowed to use them and we must deploy them ourself :(

I deployed linked list with such things I learned in class, but I didn't have idea to how deploy Hashmap.
I know we need a node class that contains a string as key and a generic type of data as value but how save them in our memory!?
With a little search I found Nodes must be saved in a LinkedList and tada, the Hashmap is ready.

### config files
In middle of pervious process we held a meet and set values of game mechanics.
It's took 4 hours and I think our values still not balanced.
