package comic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // 전체 만화책 목록을 번호순으로 조회
    public List<Comic> listComics() {
        List<Comic> comics = new ArrayList<>();
        String sql = "SELECT id, title, volume, author, is_rented, reg_date FROM comic ORDER BY id ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // 조회된 데이터를 Comic 객체로 변환하여 리스트에 저장
            while (rs.next()) {
                Comic comic = new Comic(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("volume"),
                    rs.getString("author"),
                    rs.getBoolean("is_rented"),
                    rs.getString("reg_date")
                );

                comics.add(comic);
            }
        } catch (SQLException e) {
            System.out.println("만화책 목록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return comics;
    }

    // 번호로 만화책 1건을 조회
    public Comic findComicById(int id) {
        String sql = "SELECT id, title, volume, author, is_rented, reg_date FROM comic WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 조회 결과가 있으면 Comic 객체로 반환
                if (rs.next()) {
                    return new Comic(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("volume"),
                        rs.getString("author"),
                        rs.getBoolean("is_rented"),
                        rs.getString("reg_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("만화책 상세 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        // 조회 결과가 없으면 null 반환
        return null;
    }
}
