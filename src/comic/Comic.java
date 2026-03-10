public class Comic {
    // 만화책 번호
    private int id;

    // 만화책 제목
    private String title;

    // 만화책 권수
    private int volume;

    // 만화책 작가
    private String author;

    // 대여 여부
    private boolean isRented;

    // 등록일
    private String regDate;

    public Comic() {
    }

    public Comic(int id, String title, int volume, String author, boolean isRented, String regDate) {
        this.id = id;
        this.title = title;
        this.volume = volume;
        this.author = author;
        this.isRented = isRented;
        this.regDate = regDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
}