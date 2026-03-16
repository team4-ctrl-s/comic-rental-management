import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import comic.Comic;
import comic.ComicRepository;
import member.Member;
import member.MemberRepository;
import rental.Rental;
import rental.RentalRepository;

public class App {
    // DB 연결 객체
    Connection conn = DBUtil.getConnection();

    // 콘솔 입력용 스캐너
    private Scanner sc;

    // 만화책 / 회원 / 대여 DB 처리 객체
    private ComicRepository comicRepository;
    private MemberRepository memberRepository;
    private RentalRepository rentalRepository;

    public App() {
        sc = new Scanner(System.in);
        comicRepository = new ComicRepository(conn);
        memberRepository = new MemberRepository(conn);
        rentalRepository = new RentalRepository(conn);
    }

    // 프로그램 실행 메서드
    public void run() {
        System.out.println("== 만화책 대여점 프로그램 ==");
        showCommandList();

        while (true) {
            System.out.print("명령어: ");
            String command = sc.nextLine();

            Rq rq = new Rq(command);

            // exit 입력 시 프로그램 종료
            if (rq.getActionName().equals("exit")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            // 명령어별 기능 실행
            doAction(rq);

            System.out.println();
        }

        sc.close();
    }

    // =========================
    // 명령어 목록 출력
    // =========================
    private void showCommandList() {
        System.out.println("==== 사용 가능한 명령어 ====");

        System.out.println("[만화책]");
        System.out.println("comic-add                : 만화책 등록");
        System.out.println("comic-list               : 만화책 목록");
        System.out.println("comic-detail [id]        : 만화책 상세보기");
        System.out.println("comic-update [id]        : 만화책 수정");
        System.out.println("comic-delete [id]        : 만화책 삭제");

        System.out.println();

        System.out.println("[회원]");
        System.out.println("member-add               : 회원 등록");
        System.out.println("member-list              : 회원 목록");

        System.out.println();

        System.out.println("[대여]");
        System.out.println("rent [comicId] [memberId] : 만화책 대여");
        System.out.println("return [rentalId]         : 만화책 반납");
        System.out.println("rental-list               : 대여 목록");

        System.out.println();

        System.out.println("[기타]");
        System.out.println("help                     : 명령어 목록");
        System.out.println("exit                     : 프로그램 종료");

        System.out.println("=========================");
    }

    // 명령어에 따라 기능을 분기하는 메서드
    private void doAction(Rq rq) {
        switch (rq.getActionName()) {

            // 아무 입력도 안 했을 때
            case "":
                System.out.println("명령어를 입력해주세요.");
                break;

            case "help":
                showCommandList();
                break;

            // 만화책 관련 기능
            case "comic-add":
                actionComicAdd();
                break;

            case "comic-list":
                actionComicList();
                break;

            case "comic-detail":
                actionComicDetail(rq);
                break;

            case "comic-update":
                actionComicUpdate(rq);
                break;

            case "comic-delete":
                actionComicDelete(rq);
                break;

            // 회원 관련 기능
            case "member-add":
                actionMemberAdd();
                break;

            case "member-list":
                actionMemberList();
                break;

            // 대여 / 반납 관련 기능
            case "rent":
                actionRent(rq);
                break;

            case "return":
                actionReturn(rq);
                break;

            case "rental-list":
                actionRentalList();
                break;

            // 없는 명령어 입력 시
            default:
                System.out.println("존재하지 않는 명령어입니다.");
                break;
        }
    }

    // =========================
    // 만화책 등록
    // comic-add
    // =========================
    private void actionComicAdd() {
        System.out.print("제목: ");
        String title = sc.nextLine().trim();

        System.out.print("권수: ");
        String volumeStr = sc.nextLine().trim();

        System.out.print("작가: ");
        String author = sc.nextLine().trim();

        // 제목은 비어 있으면 안 됨
        if (title.isEmpty()) {
            System.out.println("제목을 입력해주세요.");
            return;
        }

        // 작가는 비어 있으면 안 됨
        if (author.isEmpty()) {
            System.out.println("작가를 입력해주세요.");
            return;
        }

        int volume;
        try {
            // 권수는 숫자로 변환
            volume = Integer.parseInt(volumeStr);
        } catch (NumberFormatException e) {
            System.out.println("권수는 숫자로 입력해주세요.");
            return;
        }

        // 권수는 1 이상만 허용
        if (volume <= 0) {
            System.out.println("권수는 1 이상으로 입력해주세요.");
            return;
        }

        // DB에 만화책 저장
        int newId = comicRepository.addComic(title, volume, author);

        if (newId == -1) {
            System.out.println("만화책 등록에 실패했습니다.");
            return;
        }

        System.out.println("=> 만화책이 등록되었습니다. (id=" + newId + ")");
    }

    // =========================
    // 만화책 목록
    // comic-list
    // =========================
    private void actionComicList() {
        // 삭제되지 않은 만화책 목록 조회
        List<Comic> comics = comicRepository.listComics();

        // 조회 결과가 없으면 안내 메시지 출력
        if (comics.isEmpty()) {
            System.out.println("등록된 만화책이 없습니다.");
            return;
        }

        System.out.println("번호 | 제목 | 권수 | 작가 | 상태 | 등록일");
        System.out.println("--------------------------------------------------");

        // 번호순으로 조회된 만화책 목록 출력
        for (Comic comic : comics) {
            String rentStatus = comic.isRented() ? "대여중" : "대여가능";

            System.out.printf("%d | %s | %d | %s | %s | %s%n",
                comic.getId(),
                comic.getTitle(),
                comic.getVolume(),
                comic.getAuthor(),
                rentStatus,
                comic.getRegDate());
        }
    }

    // =========================
    // 만화책 상세보기
    // comic-detail [id]
    // =========================
    private void actionComicDetail(Rq rq) {
        if (rq.getArgsCount() < 1) {
            System.out.println("사용법: comic-detail [id]");
            return;
        }

        int comicId;
        try {
            // 상세보기 대상 번호를 숫자로 변환
            comicId = Integer.parseInt(rq.getArg(0));
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호는 숫자로 입력해주세요.");
            return;
        }

        // 번호로 만화책 1건 조회
        Comic comic = comicRepository.findComicById(comicId);

        // 조회 결과가 없으면 안내 메시지 출력
        if (comic == null) {
            System.out.println("해당 번호의 만화책이 존재하지 않거나 이미 삭제되었습니다.");
            return;
        }

        // 상세 정보 출력
        System.out.println("번호: " + comic.getId());
        System.out.println("제목: " + comic.getTitle());
        System.out.println("권수: " + comic.getVolume());
        System.out.println("작가: " + comic.getAuthor());
        System.out.println("상태: " + (comic.isRented() ? "대여중" : "대여가능"));
        System.out.println("등록일: " + comic.getRegDate());
    }

    // =========================
    // 만화책 수정
    // comic-update [id]
    // =========================
    private void actionComicUpdate(Rq rq) {
        if (rq.getArgsCount() < 1) {
            System.out.println("사용법: comic-update [id]");
            return;
        }

        int comicId;
        try {
            // 수정 대상 번호를 숫자로 변환
            comicId = Integer.parseInt(rq.getArg(0));
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호는 숫자로 입력해주세요.");
            return;
        }

        // 수정 전 기존 만화책 정보를 조회
        Comic comic = comicRepository.findComicById(comicId);

        // 조회 결과가 없으면 수정할 수 없음
        if (comic == null) {
            System.out.println("해당 번호의 만화책이 존재하지 않거나 이미 삭제되었습니다.");
            return;
        }

        System.out.print("새 제목(현재: " + comic.getTitle() + "): ");
        String newTitle = sc.nextLine().trim();

        System.out.print("새 권수(현재: " + comic.getVolume() + "): ");
        String newVolumeStr = sc.nextLine().trim();

        System.out.print("새 작가(현재: " + comic.getAuthor() + "): ");
        String newAuthor = sc.nextLine().trim();

        // 제목을 비우면 기존 제목 유지
        if (newTitle.isEmpty()) {
            newTitle = comic.getTitle();
        }

        // 작가를 비우면 기존 작가 유지
        if (newAuthor.isEmpty()) {
            newAuthor = comic.getAuthor();
        }

        int newVolume;
        if (newVolumeStr.isEmpty()) {
            // 권수를 비우면 기존 권수 유지
            newVolume = comic.getVolume();
        } else {
            try {
                // 새 권수는 숫자로 변환
                newVolume = Integer.parseInt(newVolumeStr);
            } catch (NumberFormatException e) {
                System.out.println("권수는 숫자로 입력해주세요.");
                return;
            }
        }

        // 권수는 1 이상만 허용
        if (newVolume <= 0) {
            System.out.println("권수는 1 이상으로 입력해주세요.");
            return;
        }

        // 수정 결과를 DB에 반영
        boolean isUpdated = comicRepository.updateComic(comicId, newTitle, newVolume, newAuthor);

        if (!isUpdated) {
            System.out.println("만화책 수정에 실패했습니다.");
            return;
        }

        System.out.println("=> 만화책이 수정되었습니다. (id=" + comicId + ")");
    }

    // =========================
    // 만화책 삭제
    // comic-delete [id]
    // =========================
    private void actionComicDelete(Rq rq) {
        if (rq.getArgsCount() < 1) {
            System.out.println("사용법: comic-delete [id]");
            return;
        }

        int comicId;
        try {
            // 삭제 대상 번호를 숫자로 변환
            comicId = Integer.parseInt(rq.getArg(0));
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호는 숫자로 입력해주세요.");
            return;
        }

        // 삭제 전 만화책 존재 여부 확인
        Comic comic = comicRepository.findComicById(comicId);

        if (comic == null) {
            System.out.println("해당 번호의 만화책이 존재하지 않거나 이미 삭제되었습니다.");
            return;
        }

        // 현재 대여 중인 만화책은 삭제 불가
        if (comic.isRented()) {
            System.out.println("현재 대여 중인 만화책은 삭제할 수 없습니다.");
            return;
        }

        // 실제 삭제 대신 soft delete 수행
        boolean isDeleted = comicRepository.softDeleteComic(comicId);

        if (!isDeleted) {
            System.out.println("만화책 삭제에 실패했습니다.");
            return;
        }

        System.out.println("=> 만화책이 삭제되었습니다. (id=" + comicId + ")");
    }

    // =========================
    // 회원 등록
    // member-add
    // =========================
    private void actionMemberAdd() {
        System.out.print("이름: ");
        String name = sc.nextLine().trim();

        System.out.print("전화번호: ");
        String phone = sc.nextLine().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            System.out.println("이름과 전화번호를 모두 입력해주세요.");
            return;
        }

        memberRepository.insert(name, phone);
    }

    // =========================
    // 회원 목록
    // member-list
    // =========================
    private void actionMemberList() {
        // DB에서 전체 회원 목록 조회
        List<Member> members = memberRepository.findAll();

        // 조회 결과가 없으면 안내 메시지 출력
        if (members.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
            return;
        }

        System.out.println("번호 | 이름 | 전화번호 | 등록일");
        System.out.println("--------------------------------------------------");

        // 회원 정보 출력
        for (Member member : members) {
            System.out.printf("%d | %s | %s | %s%n",
                member.getId(),
                member.getName(),
                member.getPhone(),
                member.getRegDate());
        }
    }

    // =========================
    // 대여
    // rent [comicId] [memberId]
    // =========================
    private void actionRent(Rq rq) {
        if (rq.getArgsCount() < 2) {
            System.out.println("사용법: rent [comicId] [memberId]");
            return;
        }

        int comicId;
        int memberId;

        try {
            // 만화책 번호와 회원 번호를 숫자로 변환
            comicId = Integer.parseInt(rq.getArg(0));
            memberId = Integer.parseInt(rq.getArg(1));
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호와 회원 번호는 숫자로 입력해주세요.");
            return;
        }

        // 삭제되지 않은 만화책만 대여 가능
        Comic comic = comicRepository.findComicById(comicId);
        if (comic == null) {
            System.out.println("해당 번호의 만화책이 존재하지 않거나 이미 삭제되었습니다.");
            return;
        }

        // 회원 존재 여부 확인
        Member member = findMemberById(memberId);
        if (member == null) {
            System.out.println("해당 번호의 회원이 존재하지 않습니다.");
            return;
        }

        // 현재 만화책 상태가 이미 대여중이면 중단
        if (comic.isRented()) {
            System.out.println("이미 대여 중인 만화책입니다.");
            return;
        }

        // 대여 처리
        Rental rental = rentalRepository.rentComic(comicId, memberId);

        if (rental == null) {
            System.out.println("이미 대여 중이거나 대여 처리에 실패했습니다.");
            return;
        }

        System.out.println("=> 대여 완료: [대여id=" + rental.getId() + "] "
            + comic.getTitle() + " → " + member.getName());
    }

    // =========================
    // 반납
    // return [rentalId]
    // =========================
    private void actionReturn(Rq rq) {
        if (rq.getArgsCount() < 1) {
            System.out.println("사용법: return [rentalId]");
            return;
        }

        int rentalId;
        try {
            // 반납할 대여 번호를 숫자로 변환
            rentalId = Integer.parseInt(rq.getArg(0));
        } catch (NumberFormatException e) {
            System.out.println("대여 번호는 숫자로 입력해주세요.");
            return;
        }

        // 반납 대상 대여 내역 조회
        Rental rental = rentalRepository.findById(rentalId);

        if (rental == null) {
            System.out.println("해당 번호의 대여 내역이 존재하지 않습니다.");
            return;
        }

        // 이미 반납된 경우 중단
        if (rental.isReturned()) {
            System.out.println("이미 반납된 대여입니다.");
            return;
        }

        // 출력용 만화책 / 회원 정보 조회
        Comic comic = comicRepository.findComicByIdIncludeDeleted(rental.getComicId());
        Member member = findMemberById(rental.getMemberId());

        String comicTitle = comic != null ? comic.getTitle() : "알 수 없는 만화책";
        String memberName = member != null ? member.getName() : "알 수 없는 회원";

        // 반납 처리
        boolean isReturned = rentalRepository.returnComic(rentalId);

        if (!isReturned) {
            System.out.println("반납 처리에 실패했습니다.");
            return;
        }

        System.out.println("=> 반납 완료: [대여id=" + rentalId + "] "
            + comicTitle + " / " + memberName);
    }

    // =========================
    // 대여 목록
    // rental-list
    // =========================
    private void actionRentalList() {
        // DB에서 전체 대여 목록 조회
        List<Rental> rentals = rentalRepository.listRentals();

        if (rentals.isEmpty()) {
            System.out.println("대여 내역이 없습니다.");
            return;
        }

        // 삭제된 만화책도 보이도록 전체 만화책 조회
        List<Comic> comics = comicRepository.listAllComics();
        List<Member> members = memberRepository.findAll();

        System.out.println("대여번호 | 만화책 | 회원 | 대여일 | 반납일 | 상태");
        System.out.println("--------------------------------------------------------------------------------");

        for (Rental rental : rentals) {
            Comic comic = findComicFromList(comics, rental.getComicId());
            Member member = findMemberFromList(members, rental.getMemberId());

            String comicInfo = comic != null
                ? comic.getTitle() + "(" + rental.getComicId() + ")"
                : "알 수 없음(" + rental.getComicId() + ")";

            String memberInfo = member != null
                ? member.getName() + "(" + rental.getMemberId() + ")"
                : "알 수 없음(" + rental.getMemberId() + ")";

            String returnDate = rental.getReturnDate() != null
                ? rental.getReturnDate().toString()
                : "-";

            String status = rental.isReturned() ? "반납완료" : "대여중";

            System.out.printf("%d | %s | %s | %s | %s | %s%n",
                rental.getId(),
                comicInfo,
                memberInfo,
                rental.getRentDate(),
                returnDate,
                status);
        }
    }

    // 회원 번호로 회원 1명 조회
    private Member findMemberById(int memberId) {
        List<Member> members = memberRepository.findAll();
        return findMemberFromList(members, memberId);
    }

    // 회원 목록에서 번호로 회원 찾기
    private Member findMemberFromList(List<Member> members, int memberId) {
        for (Member member : members) {
            if (member.getId() == memberId) {
                return member;
            }
        }
        return null;
    }

    // 만화책 목록에서 번호로 만화책 찾기
    private Comic findComicFromList(List<Comic> comics, int comicId) {
        for (Comic comic : comics) {
            if (comic.getId() == comicId) {
                return comic;
            }
        }
        return null;
    }
}
