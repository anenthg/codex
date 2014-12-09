package path;

import helper.Lanes;

/**
 * Created by ashwin on 24/6/14.
 */
public class Path {

    String[] path;
    int cost,time;
    Lanes lanesChanged;
    public Path(String[] path,int time, Lanes lanesChanged)
    {
        this.path=path;
       // this.cost=cost;
        this.time=time;
        this.lanesChanged=lanesChanged;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Lanes getLanesChanged() {
        return lanesChanged;
    }

    public void setLanesChanged(Lanes lanesChanged) {
        this.lanesChanged = lanesChanged;
    }
}
