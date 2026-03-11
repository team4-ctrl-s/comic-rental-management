import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class App {
    // DB 연결 객체
    Connection conn = DBUtil.getConnection();

    // 콘솔 입력용 스캐너
    private Scanner sc;

    // 만화책 DB 처리 객체
    private ComicRepository comicRepository;

    public App() {
        sc = new Scanner(System.in);
        comicRepository = new ComicRepository(conn);
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
        // DB에서 전체 만화책 목록 조회
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
            System.out.println("해당 번호의 만화책이 존재하지 않습니다.");
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

        String comicId = rq.getArg(0);

        try {
            Integer.parseInt(comicId);
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호는 숫자로 입력해주세요.");
            return;
        }

        System.out.print("새 제목: ");
        String newTitle = sc.nextLine();

        System.out.print("새 권수: ");
        String newVolumeStr = sc.nextLine();

        System.out.print("새 작가: ");
        String newAuthor = sc.nextLine();

        int newVolume;
        try {
            newVolume = Integer.parseInt(newVolumeStr);
        } catch (NumberFormatException e) {
            System.out.println("권수는 숫자로 입력해주세요.");
            return;
        }

        // TODO:
        // ComicRepository.updateComic(comicId, newTitle, newVolume, newAuthor) 호출

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

        String comicId = rq.getArg(0);

        try {
            Integer.parseInt(comicId);
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호는 숫자로 입력해주세요.");
            return;
        }

        // TODO:
        // ComicRepository.deleteComic(comicId) 호출

        System.out.println("=> 만화책이 삭제되었습니다. (id=" + comicId + ")");
    }

    // =========================
    // 회원 등록
    // member-add
    // =========================
    private void actionMemberAdd() {
        System.out.print("이름: ");
        String name = sc.nextLine();

        System.out.print("전화번호: ");
        String phone = sc.nextLine();

        // TODO:
        // MemberRepository.addMember(name, phone) 호출

        System.out.println("=> 회원이 등록되었습니다. (id=1)");
    }

    // =========================
    // 회원 목록
    // member-list
    // =========================
    private void actionMemberList() {
        // TODO:
        // MemberRepository.listMembers() 호출

        System.out.println("회원id | 이름 | 전화번호 | 등록일");
        System.out.println("--------------------------------------");
        System.out.println("1 | 에밀리 | 010-1111-2222 | 2026-03-03");
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

        String comicId = rq.getArg(0);
        String memberId = rq.getArg(1);

        try {
            Integer.parseInt(comicId);
            Integer.parseInt(memberId);
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호와 회원 번호는 숫자로 입력해주세요.");
            return;
        }

        // TODO:
        // RentalRepository.rentComic(comicId, memberId) 호출
        // 대여중 여부 확인, 회원 존재 여부 확인, 대여 처리

        System.out.println("=> 대여 완료: [대여id=1] 만화(" + comicId + ") → 회원(" + memberId + ")");
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

        String rentalId = rq.getArg(0);

        try {
            Integer.parseInt(rentalId);
        } catch (NumberFormatException e) {
            System.out.println("대여 번호는 숫자로 입력해주세요.");
            return;
        }

        // TODO:
        // RentalRepository.returnComic(rentalId) 호출
        // 이미 반납된 건인지 확인 후 반납 처리

        System.out.println("=> 반납 완료: 대여id=" + rentalId);
    }

    // =========================
    // 대여 목록
    // rental-list
    // =========================
    private void actionRentalList() {
        // TODO:
        // RentalRepository.listRentals() 호출
        // 전체 / 미반납 옵션은 나중에 확장 가능

        System.out.println("대여id | 만화id | 회원id | 대여일 | 반납일");
        System.out.println("------------------------------------------------");
        System.out.println("1 | 1 | 1 | 2026-03-03 | -");
    }
}
