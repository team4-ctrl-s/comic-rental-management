package member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {
    private Connection conn;

    // 생성자: App.java에서 넘겨준 DB 연결 객체를 받음
    public MemberRepository(Connection conn) {
        this.conn = conn;
    }

    // 회원 등록 (C: Create)
    public void insert(String name, String phone) {
        String sql = "INSERT INTO member (name, phone, reg_date) VALUES (?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
            System.out.println("== " + name + " 회원님이 등록되었습니다. ==");
        } catch (SQLException e) {
            System.err.println("회원 등록 중 오류 발생: " + e.getMessage());
        }
    }

    // 회원 목록 조회 (R: Read)
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT id, name, phone, reg_date FROM member ORDER BY id DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                members.add(new Member(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("reg_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("목록 조회 중 오류 발생: " + e.getMessage());
        }
        return members;
    }
}