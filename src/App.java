import java.sql.Connection;
import java.util.Scanner;

public class App {
    // DB 연결 실행 테스트
    Connection conn = DBUtil.getConnection();

    private Scanner sc;

    public App() {
        sc = new Scanner(System.in);
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
        String title = sc.nextLine();

        System.out.print("권수: ");
        String volumeStr = sc.nextLine();

        System.out.print("작가: ");
        String author = sc.nextLine();

        int volume;
        try {
            volume = Integer.parseInt(volumeStr);
        } catch (NumberFormatException e) {
            System.out.println("권수는 숫자로 입력해주세요.");
            return;
        }

        // TODO:
        // ComicRepository.addComic(title, volume, author) 호출
        // 실제 DB 저장 후 생성된 id 받아서 출력

        System.out.println("=> 만화책이 등록되었습니다. (id=1)");
    }

    // =========================
    // 만화책 목록
    // comic-list
    // =========================
    private void actionComicList() {
        // TODO:
        // ComicRepository.listComics() 호출
        // DB에서 전체 목록 조회 후 출력

    	System.out.println("번호 | 제목 | 권수 | 작가 | 상태 | 등록일");
        System.out.println("--------------------------------------------------");
        System.out.println("1 | 슬램덩크 | 1 | 이노우에 다케히코 | 대여가능 | 2026-03-03");
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

        String comicId = rq.getArg(0);

        // 숫자인지 기본 검증
        try {
            Integer.parseInt(comicId);
        } catch (NumberFormatException e) {
            System.out.println("만화책 번호는 숫자로 입력해주세요.");
            return;
        }

        // TODO:
        // ComicRepository.showComicDetail(comicId) 호출

        System.out.println("[만화책 상세보기 기능 연결 예정]");
        System.out.println("입력한 만화책 번호: " + comicId);
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