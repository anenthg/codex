package helper;

/**
 * Created by ashwin on 25/6/14.
 */
public class Lanes {
    int lanesChanged;
    String pointOfLaneChange;
    public Lanes(int lanesChanged1,String pointOfLaneChange1)
    {
        this.lanesChanged=lanesChanged1;
        this.pointOfLaneChange=pointOfLaneChange1;
    }

    public int getLanesChanged() {
        return lanesChanged;
    }

    public void setLanesChanged(int lanesChanged) {
        this.lanesChanged = lanesChanged;
    }

    public String getPointOfLaneChange() {
        return pointOfLaneChange;
    }

    public void setPointOfLaneChange(String pointOfLaneChange) {
        this.pointOfLaneChange = pointOfLaneChange;
    }
}
