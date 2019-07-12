package csb.chase.com.reticularview.view;

/**
 * Created by CSB on 2018/9/10 10:07
 * E-Mail Address：
 * effect:
 */
public class Vertex {
    private float x;
    private float y;
    private float angle;
    private double value;
    private String dataName;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                '}';
    }
}
