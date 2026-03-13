package rental;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalRepository {
    private final Connection conn;

    public RentalRepository(Connection conn) {
        this.conn = conn;
    }

    /**
     * 만화 대여
     * - 이미 반납되지 않은 대여가 있으면 null 반환
     * - 성공 시 생성된 Rental 객체 반환
     */
    public Rental rentComic(int comicId, int memberId) {
        String sql =
            "INSERT INTO rental (comic_id, member_id, rent_date) " +
                "SELECT ?, ?, CURRENT_DATE " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 FROM rental WHERE comic_id = ? AND return_date IS NULL " +
                ")";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, comicId);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, comicId);

            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                // 이미 대여 중이거나 삽입 실패
                return null;
            }

            int generatedId;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (!rs.next()) {
                    return null;
                }
                generatedId = rs.getInt(1);
            }

            // 만화 대여 상태 업데이트 (옵션)
            try (PreparedStatement updateComic = conn.prepareStatement(
                "UPDATE comic SET is_rented = TRUE WHERE id = ?"
            )) {
                updateComic.setInt(1, comicId);
                updateComic.executeUpdate();
            }

            return new Rental(generatedId, comicId, memberId, LocalDate.now());
        } catch (SQLException e) {
            System.out.println("대여 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 반납 처리
     * - 이미 반납된 건이면 false 반환
     */
    public boolean returnComic(int rentalId) {
        String findSql = "SELECT comic_id, return_date FROM rental WHERE id = ?";

        try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setInt(1, rentalId);

            try (ResultSet rs = findStmt.executeQuery()) {
                if (!rs.next()) {
                    return false; // 존재하지 않는 대여
                }
                if (rs.getDate("return_date") != null) {
                    return false; // 이미 반납됨
                }

                int comicId = rs.getInt("comic_id");

                // 반납일 업데이트
                try (PreparedStatement updateRental = conn.prepareStatement(
                    "UPDATE rental SET return_date = CURRENT_DATE WHERE id = ?"
                )) {
                    updateRental.setInt(1, rentalId);
                    int updated = updateRental.executeUpdate();
                    if (updated == 0) {
                        return false;
                    }
                }

                // 만화 대여 상태 해제
                try (PreparedStatement updateComic = conn.prepareStatement(
                    "UPDATE comic SET is_rented = FALSE WHERE id = ?"
                )) {
                    updateComic.setInt(1, comicId);
                    updateComic.executeUpdate();
                }

                return true;
            }
        } catch (SQLException e) {
            System.out.println("반납 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 대여 목록 전체 조회
     */
    public List<Rental> listRentals() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, comic_id, member_id, rent_date, return_date FROM rental ORDER BY id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapRental(rs));
            }
        } catch (SQLException e) {
            System.out.println("대여 목록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return rentals;
    }

    /**
     * ID로 대여 단건 조회
     */
    public Rental findById(int rentalId) {
        String sql = "SELECT id, comic_id, member_id, rent_date, return_date FROM rental WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rentalId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRental(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("대여 단건 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 특정 만화가 대여 중인지 확인
     */
    public boolean isComicRented(int comicId) {
        String sql = "SELECT 1 FROM rental WHERE comic_id = ? AND return_date IS NULL";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, comicId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("대여 상태 확인 중 오류가 발생했습니다.");
            e.printStackTrace();
            // 실패 시 안전하게 true 처리하여 중복 대여를 막음
            return true;
        }
    }

    private Rental mapRental(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int comicId = rs.getInt("comic_id");
        int memberId = rs.getInt("member_id");
        LocalDate rentDate = rs.getDate("rent_date").toLocalDate();

        Date returnDateSql = rs.getDate("return_date");
        LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;

        Rental rental = new Rental(id, comicId, memberId, rentDate);
        if (returnDate != null) {
            rental.markReturned(returnDate);
        }
        return rental;
    }
}
