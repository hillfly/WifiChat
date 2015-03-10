package hillfly.wifichat.bean;

public class Avatar extends Entity {
    private int imageId;

    public Avatar() {
        super();
    }

    public Avatar(int imageId) {
        super();
        this.imageId = imageId;
    }  

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
