import java.util.*;

public class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in); 
    Integer T, N;
    T = Integer.parseInt(sc.nextLine());
    
    for(int i = 0;i < T; i++) {
      ArrayList<String> list = new ArrayList(); 
      N = Integer.parseInt(sc.nextLine());
      Map<Integer, String> map = new HashMap<Integer, String>();
      boolean[] fds = new boolean[1000000];      
      for(int j = 0;j < N; j++) {
        String line = sc.nextLine();

        int fd_idx = 0;
        for (int x =0;x<1000000;x++){
          if(fds[x] == false){
            fd_idx = x;
            break;
          }
        }
        String[] words = line.split(" ");
        if (line.contains("open")){
            list.add(String.valueOf(fd_idx));
            //System.out.println(fd_idx);
            fds[fd_idx] = true;
            map.put(fd_idx, words[1]);
        } else if(line.contains("dup2")){
            int origin_fd = Integer.parseInt(words[1]);
            int new_fd = Integer.parseInt(words[2]);
            map.put(new_fd, map.get(origin_fd));
        } else if(line.contains("dup")){
            list.add(String.valueOf(fd_idx));
            //System.out.println(fd_idx);
            fds[fd_idx] = true;
            int fd = Integer.parseInt(words[1]);
            map.put(fd_idx,map.get(fd));
        } else if(line.contains("query")){
            int fd = Integer.parseInt(words[1]);
            list.add(String.valueOf(map.get(fd)));
           // System.out.println(map.get(fd));
        } else if(line.contains("close")){
            int fd = Integer.parseInt(words[1]);
            fds[fd] = false;
            map.remove(fd);
        }
      }
      for (String str:list){
        System.out.println(str);
      }
    }
  }
}
