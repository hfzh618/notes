#include <iostream>
#include <vector>
using namespace std;

int N;

void dfs(vector<vector<char>>& board, int x, int y) {
        if (x < 0 || x >= N || y < 0 || y >= N || board[x][y] != '0') {
            return;
        }
        board[x][y] = '2';
        dfs(board, x + 1, y);
        dfs(board, x - 1, y);
        dfs(board, x, y + 1);
        dfs(board, x, y - 1);
    }

int main() {
  cin>>N;
  int arr[N][N];
  vector<vector<char>> board;
  for(int i =0;i<N;i++){
    vector<char> line;
    for(int j=0;j<N;j++){
      char temp;
      cin>>temp;
      line.push_back(temp);
    }
    board.push_back(line);
  }

   for (int i = 0; i < N; i++) {
            dfs(board, i, 0);
            dfs(board, i, N - 1);
        }
        for (int i = 1; i < N - 1; i++) {
            dfs(board, 0, i);
            dfs(board, N - 1, i);
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == '2') {
                    board[i][j] = '0';
                } else if (board[i][j] == '0') {
                    board[i][j] = '1';
                }
            }
        }  

        for(int i=0;i<N;i++){
          vector<char> line = board[i];
            for(int j=0;j<N;j++){
              cout<<line[j];
            }
            cout<<endl;
        }
        return 0;
}
