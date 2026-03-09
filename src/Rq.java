public class Rq {
    
	// 명령어 이름을 저장 
	private String actionName;
	
	// 명령어 뒤에 붙는 값을 저장
    private String[] args;

    // 명령어와 인자를 분리
    public Rq(String command) {
    	// 사용자가 입력하지 않았거나 공백만 입력한 경우
        if (command == null || command.trim().equals("")) {
            actionName = "";
            args = new String[0];
            return;
        }

        // 공백 제거
        String[] parts = command.trim().split(" ");
        actionName = parts[0];

        args = new String[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            args[i - 1] = parts[i];
        }
    }

    // getter
    public String getActionName() {
        return actionName;
    }
    
    public String getArg(int index) {
        if (index >= 0 && index < args.length) {
            return args[index];
        }
        return null;
    }

    public int getArgsCount() {
        return args.length;
    }
}