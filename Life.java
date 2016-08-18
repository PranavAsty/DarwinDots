import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Life extends JPanel
{

    //////PARAMETERS//////
    int popu=1000;
    int g=5;//Gravity
    double maxTime=5;//second
    int frameRate=100;

    int gen=0;
    int maxGen = 200;
    double mutationRate=0.03;

    int targetX=450;
    int targetY=50;
    int targetDiameter=25;

    int obstacleX = 250;
    int obstacleY = 250;
    int obstacleWidth = 350;
    int obstacleHeight = 50;

    Rocket[] rocket=new Rocket[popu];
    ArrayList<Rocket> matingPool = new ArrayList<Rocket>();

    void initialPopulation()
    {
        int i=0;
        while(i<popu)
        {
          rocket[i]= new Rocket();
          i++;
        }

    }

    void evaluateFitness(int time) throws InterruptedException
    {
        for(int j=0;j<popu;j++)
        {
            if(rocket[j].visible)
            {
                int x =(int) (rocket[j].x + rocket[j].diameter/2);
                int y =(int) (rocket[j].y + rocket[j].diameter/2);
                int tx = (int)(targetX + targetDiameter/2);
                int ty = (int)(targetY + targetDiameter/2);
                double distance= Math.sqrt((tx-x)*(tx-x) + (ty-y)*(ty-y));
                if(distance<rocket[j].recordDist)
                    rocket[j].recordDist=distance;
                if(rocket[j].recordDist<1)
                    rocket[j].recordDist=1;

                //System.out.print(" "+distance);
                double fitness = 1/(rocket[j].recordDist*rocket[j].finishTime);
                if(distance<targetDiameter/2)
                {
                    //System.out.println(x+" "+y);
                    rocket[j].visible=false;
                    if(!rocket[j].hitTarget)
                    {
                        fitness*=10;
                        rocket[j].hitTarget=true;
                    }
                }

                detectCollision();

                fitness*=Math.pow(10,4); //Arbitary multiplier
                rocket[j].fitness=fitness;
                //System.out.println(fitness);
            }
        }


    }

    void selectRockets() //Roulette Wheel Selection
    {
         // Clear the ArrayList
         matingPool.clear();

         // Calculate total fitness of whole population
         double maxFitness = 0;
         for(int i=0;i<popu;i++)
         {
             if(rocket[i].fitness>maxFitness)
                maxFitness=rocket[i].fitness;
         }
         double avgFitness=0;
         for (int i=0;i<popu;i++)
         {
             avgFitness+=rocket[i].fitness;
         }
         avgFitness/=popu;
         System.out.print(","+avgFitness+""); //Prints the average fitness per population onto standard output

         for (int i = 0; i < popu; i++) {
           double fitnessNormal = rocket[i].fitness/maxFitness; //Normalize fitness between 0 and 1
           //System.out.println(fitnessNormal);
           int n = (int) (fitnessNormal * 100);  // Arbitrary multiplier
           for (int j = 0; j < n; j++) {
             matingPool.add(rocket[i]); //Based on fitness, probability of selection will be higher for higher fitness because that organism's genes will get added to the mating pool more number of times
           }
         }
         //System.out.println(matingPool.size());
    }

    void mateRockets()
    {
        // Refill the population from the mating pool
        for (int i = 0; i < popu; i++) {
              // Sping the wheel of fortune to pick two parents
              int m = new Random().nextInt(matingPool.size());
              int d = new Random().nextInt(matingPool.size());
              // Pick two parents
              Rocket mom = matingPool.get(m);
              Rocket dad = matingPool.get(d);
              RandForce[] momGenes = mom.gene;
              RandForce[] dadGenes = dad.gene;

              // Mating
              RandForce[] childGenes = crossover(momGenes,dadGenes);

              // Mutation
              childGenes = mutate(childGenes,mutationRate);
              //System.out.println(childGenes[0].magn);
              rocket[i] = new Rocket();
              for(int j=0;j<childGenes.length;j++) //Add the child to the population
              {
                rocket[i].gene[j].magn=childGenes[j].magn;
                rocket[i].gene[j].direction=childGenes[j].direction;
              }
              //System.out.println(rocket[i].x+" "+rocket[i].y);
        }
        gen++;
    }

    RandForce[] crossover(RandForce[] mom,RandForce[] dad) //Single point crossover
    {
        RandForce[] child= new RandForce[mom.length];
        int n = new Random().nextInt(mom.length);
        //Mom gives beginning(general direction) of path and dad gives end(fine details)
        for(int i=0;i<mom.length;i++)
        {
            if(i<n)
                child[i]=mom[i];
            else
                child[i]=dad[i];
        }
        return child;
    }

    RandForce[] mutate(RandForce[] gene,double m)
    {
        RandForce[] mutated = gene;
        for (int i = 0; i < gene.length; i++) {
          if (new Random().nextDouble() < m) {
            mutated[i] = new RandForce();
          }
        }
        return mutated;
    }


    void moveRockets(int count)
    {
        int k=count;
        for(int j=0;j<popu;j++)
        {

            if(!(k<((maxTime*1000)/frameRate)))
            {
                k=(int)((maxTime*1000)/frameRate);
            }
            //System.out.print(rocket[j].gene[k].magn+" & "+rocket[j].gene[k].direction+"\n");
            //System.out.println(rocket[j].gene[0]);
            double r = rocket[j].gene[k].magn;
            double theta = rocket[j].gene[k].direction;
            int cVX = (int) (r*Math.cos(theta));
            int cVY = (int) (r*Math.sin(theta));

            //velocity+=acceleration; location+=velocity;
            rocket[j].velocityX+=cVX;
            rocket[j].velocityY+=cVY;

            //System.out.println(r+" "+cX+" "+cY);

            rocket[j].x+=rocket[j].velocityX;
            rocket[j].y+=rocket[j].velocityY;

            if(!rocket[j].hitTarget)
                rocket[j].finishTime++;

            if((rocket[j].x>900 || rocket[j].x<0 || rocket[j].y<0 || rocket[j].y>600)&&!rocket[j].outOfBounds)
            {
                rocket[j].fitness*=0.1;
                rocket[j].outOfBounds=true;
            }

        }
    }

    void deActivateRockets()
    {
        for(int j=0;j<popu;j++)
        {
            rocket[j].gene[(int)((maxTime*1000)/frameRate)].direction=Math.PI/2;
            rocket[j].gene[(int)((maxTime*1000)/frameRate)].magn=g;
        }
        //System.out.println("GENERATION "+gen+" COMPLETE");


    }



    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillOval(targetX,targetY,targetDiameter,targetDiameter);
        g2d.setColor(Color.red);
        g2d.fillOval(targetX+5,targetY+5,15,15);
        g2d.setColor(Color.gray);
        g2d.fillRect(obstacleX,obstacleY,obstacleWidth,obstacleHeight);
        g2d.setColor(Color.black);
        g2d.drawString("Gen: "+gen,800,25);

        for(int j=0;j<popu;j++)
        {
            if(rocket[j].visible)
                g2d.fillOval(rocket[j].x,rocket[j].y,rocket[j].diameter,rocket[j].diameter);
        }
    }

    void detectCollision()
    {
        Rectangle obstacleRect = new Rectangle(obstacleX,obstacleY,obstacleWidth,obstacleHeight);
        for(int j=0;j<popu;j++)
        {
            Rectangle rocketRect = new Rectangle(rocket[j].x,rocket[j].y,rocket[j].diameter,rocket[j].diameter);
            if(rocketRect.intersects(obstacleRect))
            {
                rocket[j].hitObstacle=true;
                rocket[j].fitness*=0.01;
                rocket[j].visible=false;
            }
        }
    }

    void simulate(int generation) throws InterruptedException
    {
        int loopCount=0;

        while(true)
        {
            moveRockets(loopCount);
            repaint();

            //System.out.println(loopCount);
            if(loopCount==((maxTime*1000)/frameRate))
            {
                deActivateRockets();
                Thread.sleep(10);
            }
            else if(loopCount<(((maxTime*1000)/frameRate)))
            {
                evaluateFitness(loopCount);
            }

            if(loopCount>(((maxTime*1000)/frameRate)))
            {
                int flag=1;
                for(int j=0;j<popu;j++)
                {
                    if(rocket[j].outOfBounds)
                         rocket[j].visible=false;
                    if(rocket[j].y<600 && rocket[j].visible)
                        flag=0;
                }
                if(flag==1)
                {
                    Thread.sleep(1000);
        //            System.exit(0);
                    break;
                }
            }

            loopCount++;
            Thread.sleep(frameRate);
        }
    }


    public static void main(String[] args) throws InterruptedException
    {

        JFrame frame = new JFrame();
        Life simulation = new Life();

        simulation.initialPopulation();
        frame.add(simulation);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(900,600));
        frame.setMinimumSize(new Dimension(900,600));
        simulation.repaint();
        frame.setResizable(false);
        frame.setVisible(true);
        while(simulation.gen<simulation.maxGen)
        {
            simulation.simulate(simulation.gen);
            simulation.selectRockets();
            simulation.mateRockets();
        }
        System.out.println("\nSIMULATION END");
        Thread.sleep(5000);
        System.exit(0);
    }
}
