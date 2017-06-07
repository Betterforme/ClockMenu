package view;

/**
 * Created by Administrator on 2017/6/7 0007.
 */

public class ItemBean {

    private int state;//0未开始 1 进行  2 暂停  3完成 4提前完成 5已上传
    private String hw_item;//扇形区域显示的文字
    private int isChoose; //0未选中，1选中
    private String hw_d_id;//作业执行ID
    private String hw_type;
    private float startAngle;
    private float endAngle;

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIsChoose() {
        return isChoose;
    }

    public void setIsChoose(int isChoose) {
        this.isChoose = isChoose;
    }

    public String getHw_d_id() {
        return hw_d_id;
    }

    public void setHw_d_id(String hw_d_id) {
        this.hw_d_id = hw_d_id;
    }

    public String getHw_item() {
        return hw_item;
    }

    public void setHw_item(String hw_item) {
        this.hw_item = hw_item;
    }

    public String getHw_type() {
        return hw_type;
    }

    public void setHw_type(String hw_type) {
        this.hw_type = hw_type;
    }
}
