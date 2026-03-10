import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class ComicRepository {
    // DB 연결 객체를 저장
    private final Connection conn;

    public ComicRepository(Connection conn) {
        this.conn = conn;
    }

    // 만화책을 DB에 저장하고 생성된 id를 반환
    public int addComic(String title, int volume, String author) {
        String sql = "INSERT INTO comic (title, volume, author, is_rented, reg_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, volume);
            pstmt.setString(3, author);

            // 새로 등록된 만화책은 기본값을 대여 가능으로 저장
            pstmt.setBoolean(4, false);

            // 등록일은 오늘 날짜로 저장
            pstmt.setDate(5, Date.valueOf(LocalDate.now()));

            int affectedRows = pstmt.executeUpdate();

            // 저장 실패 시 -1 반환
            if (affectedRows == 0) {
                return -1;
            }

            // 생성된 PK(id) 조회
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("만화책 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return -1;
    }
}