package cn.dormao.mcpe.parallelserver;

import java.util.Locale;

public class Vector3 {
    public float x,y,z;

    public Vector3(){ this(0); }
    public Vector3(float x){ this(x,0); }
    public Vector3(float x, float y){ this(x, y, 0); }
    public Vector3(float x, float y, float z){ this.x = x;this.y = y;this.z = z; }

    public Vector3 add(float x){return add(x,0);}
    public Vector3 add(float x, float y){return add(x,y,0);}
    public Vector3 add(float x, float y, float z){
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 ceil(){
        return new Vector3(getCeilX(), getCeilY(), getCeilZ());
    }

    public Vector3 floor(){
        return new Vector3(getFloorX(), getFloorY(), getFloorZ());
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

    public int getFloorX(){return (int) Math.floor(getX());}
    public int getFloorY(){return (int) Math.floor(getY());}
    public int getFloorZ(){return (int) Math.floor(getZ());}

    public int getCeilX(){return (int) Math.ceil(getX());}
    public int getCeilY(){return (int) Math.ceil(getY());}
    public int getCeilZ(){return (int) Math.ceil(getZ());}

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Vector3{%.2f,%.2f,%.2f}", getX(),getY(),getZ());
    }
}
