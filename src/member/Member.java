package member;

public class Member {
    private int id;
    private String name;
    private String phone;
    private String regDate;

    // 생성자: 데이터를 객체로 만들 때 사용
    public Member(int id, String name, String phone, String regDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.regDate = regDate;
    }

    // Getter: 데이터를 꺼낼 때 사용 (목록 출력 시 필요)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRegDate() { return regDate; }
}