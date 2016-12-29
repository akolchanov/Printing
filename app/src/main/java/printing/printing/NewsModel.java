package printing.printing;


public class NewsModel {

    String title;
    String text;
    String image;

    public NewsModel(String title, String text, String image ) {
        this.title=title;
        this.text=text;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }


}