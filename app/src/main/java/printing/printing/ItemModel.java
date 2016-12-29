package printing.printing;


public class ItemModel {

    String title;
    String description;
    String image;
    String price;

    public ItemModel(String title, String description, String image, String price) {
        this.title=title;
        this.description=description;
        this.image=image;
        this.price = price;
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

    public String getPrice() {
        return price;
    }

}