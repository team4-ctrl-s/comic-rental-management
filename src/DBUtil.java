import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    // 데이터베이스 연결 정보
    private static final String URL =
            "jdbc:mysql://localhost:3306/comic_rental?serverTimezone=Asia/Seoul&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    // JDBC 드라이버 로드
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC 드라이버 로드 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC 드라이버 로드 실패");
            e.printStackTrace();
        }
    }

    // 데이터베이스 연결을 생성하는 함수
    public static Connection getConnection() {

        try {
            // DriverManager를 이용해 DB 연결 시도
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 연결 성공 메시지 출력
            System.out.println("데이터베이스 연결 성공");

            return conn;

        } catch (Exception e) {

            // 연결 실패 메시지 출력
            System.out.println("데이터베이스 연결 실패");
            e.printStackTrace();
        }

        return null;
    }
}