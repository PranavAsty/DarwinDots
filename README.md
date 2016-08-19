# DarwinDots
## Optimization with biological evolution

The idea is that of simple evolution of a species over generations. This is an attempt to make a GUI for visualizing how evolution can be applied to an optimization problem.

The species consists of organisms that have a common goal and develop the competence to reach that goal after **evolving**.

Here, the goal is to reach a point marked as the target from their birth-point in the minimum time without colliding with obstacles or the walls of the frame.

The algorithm also incorporates the idea of mutations to maintain the genetic diversity of the species to some extent. 


## Basic idea
Biological evolution says that in a population, the fittest organisms have a better chance of giving birth to an offspring that is healthy and can take the species forward.
We define fitness of an individual here with two parameters:
* Minimum distance to the target
* How fast it got to that minimum distance

Once we have selected the fittest individuals in the population, we then mix the genes of 2 of the individuals(mate) to make a child for the next generation and so on.

## What are we calling genes?
The gene is an array of forces that are applied to the organism at every iteration of the simulation. Initially *(generation 0)*, this is random and the organism follows a random path. 

## Selection
We take the fitness value of each individual and normalize it to a value between 0 and 1. We then fill our **mating pool** with each individual and the number of times we add that individual to the mating pool is in the ratio of its normalized fitness.

## Mating (Genetic crossover)
We have used a single point crossover. This means that one contiguous part of the new gene is from one parent and the rest from the other parent. The point at which the cutting and splicing of the genes is to be done is a random value between 0 and the length of the gene.
 A child gene could look like *AAAAAAAAAAAABBBB*, where A is from the first parent and B is from the second.

## Build instructions 
Run the following commands:
```
javac Life.java
java Life
```
