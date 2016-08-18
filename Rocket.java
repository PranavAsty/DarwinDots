class Rocket extends Life
{
  int x,y,diameter=5,finishTime=0;
  double recordDist=99999,fitness,velocityX=0,velocityY=0;
  boolean visible,hitTarget,hitObstacle,outOfBounds;
  RandForce[] gene = new RandForce[(int)(((maxTime*1000)/frameRate))+1];

  Rocket()
  {
      x=450;
      y=550;
      fitness=1;
      visible=true;
      outOfBounds=false;
      hitTarget=false;
      hitObstacle=false;

      for(int j=0;j<gene.length;j++)
      {
        gene[j]=new RandForce();
      }
   }
}
