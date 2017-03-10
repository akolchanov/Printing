package printing.printing;


public class OrdersModel {

    String title;
    String description;
    String image;
    String status;

    public OrdersModel(String title, String description, String image, String status) {
        this.title=title;
        this.description=description;
        this.image=image;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

}