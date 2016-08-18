import java.util.Random;

class RandForce
{
    double magn;
    double direction;

    RandForce()
    {
        magn=new Random().nextDouble()*10;
        direction=new Random().nextDouble()*2*Math.PI;
    }

}
