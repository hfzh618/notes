接下来你将学习一些 Git 的强大功能，这些功能你可能并不会在日常操作中使用，但在某些时候你可能会需要。

# Git工具

## 选择修订版本

### git show

查看最新的commit

**git show commitId**
查看指定commit hashID的所有修改

**git show commitId fileName**
查看某次commit中具体某个文件的修改

### 简短的SHA-1a

Git 可以为 SHA-1 值生成出简短且唯一的缩写。 如果你在 `git log` 后加上 `--abbrev-commit` 参数，输出结果里就会显示简短且唯一的值； 默认使用七个字符，不过有时为了避免 SHA-1 的歧义，会增加字符数：

```shell
$ git log --abbrev-commit --pretty=oneline
ca82a6d changed the version number
085bb3b removed unnecessary test code
a11bef0 first commit
```

### 分支引用

如果你想知道某个分支指向哪个特定的 SHA-1，或者想看任何一个例子中被简写的 SHA-1， 你可以使用一个叫做 `rev-parse` 的 Git 探测工具。

简单来说，`rev-parse` 是为了底层操作而不是日常操作设计的。 不过，有时你想看 Git 现在到底处于什么状态时，它可能会很有用。 你可以在你的分支上执行 `rev-parse`

```shell
$ git rev-parse topic1
ca82a6dff817ec66f44342007202690a93763949
```

### 引用日志

当你在工作时， Git 会在后台保存一个引用日志（reflog）， 引用日志记录了最近几个月你的 HEAD 和分支引用所指向的历史。

你可以使用 `git reflog` 来查看引用日志

```shell
$ git reflog
734713b HEAD@{0}: commit: fixed refs handling, added gc auto, updated
d921970 HEAD@{1}: merge phedders/rdocs: Merge made by the 'recursive' strategy.
1c002dd HEAD@{2}: commit: added some blame and merge stuff
1c36188 HEAD@{3}: rebase -i (squash): updating HEAD
95df984 HEAD@{4}: commit: # This is a combination of two commits.
1c36188 HEAD@{5}: rebase -i (squash): updating HEAD
7e05da5 HEAD@{6}: rebase -i (pick): updating HEAD
```

每当你的 HEAD 所指向的位置发生了变化，Git 就会将这个信息存储到引用日志这个历史记录里。 你也可以通过 reflog 数据来获取之前的提交历史。 如果你想查看仓库中 HEAD 在五次前的所指向的提交，你可以使用 `@{n}` 来引用 reflog 中输出的提交记录。

```shell
$ git show HEAD@{5}
```

你同样可以使用这个语法来查看某个分支在一定时间前的位置。 例如，查看你的 `master` 分支在昨天的时候指向了哪个提交，你可以输入

```shell
$ git show master@{yesterday}
```

就会显示昨天 `master` 分支的顶端指向了哪个提交。 这个方法只对还在你引用日志里的数据有用，所以不能用来查好几个月之前的提交。

### 祖先引用

祖先引用是另一种指明一个提交的方式。 如果你在引用的尾部加上一个 `^`（脱字符）， Git 会将其解析为该引用的上一个提交。

你可以使用 `HEAD^` 来查看上一个提交，也就是 “HEAD 的父提交”：

```shell
$ git show HEAD^
commit d921970aadf03b3cf0e71becdaab3147ba71cdef
Merge: 1c002dd... 35cfb2b...
Author: Scott Chacon <schacon@gmail.com>
Date:   Thu Dec 11 15:08:43 2008 -0800

    Merge commit 'phedders/rdocs'
```

你也可以在 `^` 后面添加一个数字来指明想要 **哪一个** 父提交——例如 `d921970^2` 代表 “d921970 的第二父提交” 这个语法只适用于合并的提交，因为合并提交会有多个父提交。 合并提交的第一父提交是你合并时所在分支（通常为 `master`），而第二父提交是你所合并的分支（例如 `topic`）：

```shell
$ git show d921970^
commit 1c002dd4b536e7479fe34593e72e6c6c1819e53b
Author: Scott Chacon <schacon@gmail.com>
Date:   Thu Dec 11 14:58:32 2008 -0800

    added some blame and merge stuff
```

## 贮藏与清理

有时，当你在项目的一部分上已经工作一段时间后，所有东西都进入了混乱的状态， 而这时你想要切换到另一个分支做一点别的事情。 问题是，你不想仅仅因为过会儿回到这一点而为做了一半的工作创建一次提交。 针对这个问题的答案是 `git stash` 命令。

贮藏（stash）会处理工作目录的脏的状态——即跟踪文件的修改与暂存的改动——然后将未完成的修改保存到一个栈上， 而你可以在任何时候重新应用这些改动（甚至在不同的分支上）。

### 贮藏工作

为了演示贮藏，你需要进入项目并改动几个文件，然后可以暂存其中的一个改动。 如果运行 `git status`，可以看到有改动的状态：

```shell
$ git status
Changes to be committed:
  (use "git reset HEAD <file>..." to unstage)

	modified:   index.html

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	modified:   lib/simplegit.rb
```

现在想要切换分支，但是还不想要提交之前的工作；所以贮藏修改。 将新的贮藏推送到栈上，运行 `git stash` 或 `git stash push`：

```shell
$ git stash
Saved working directory and index state \
  "WIP on master: 049d078 added the index file"
HEAD is now at 049d078 added the index file
(To restore them type "git stash apply")
```

可以看到工作目录是干净的了：

```console
$ git status
# On branch master
nothing to commit, working directory clean
```

此时，你可以切换分支并在其他地方工作；你的修改被存储在栈上。 要查看贮藏的东西，可以使用 `git stash list`：

```console
$ git stash list
stash@{0}: WIP on master: 049d078 added the index file
stash@{1}: WIP on master: c264051 Revert "added file_size"
stash@{2}: WIP on master: 21d80a5 added number to log
```

在本例中，有两个之前的贮藏，所以你接触到了三个不同的贮藏工作。 可以通过原来 stash 命令的帮助提示中的命令将你刚刚贮藏的工作重新应用：`git stash apply`。 如果想要应用其中一个更旧的贮藏，可以通过名字指定它，像这样：`git stash apply stash@{2}`。 如果不指定一个贮藏，Git 认为指定的是最近的贮藏：

```shell
$ git stash apply
On branch master
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	modified:   index.html
	modified:   lib/simplegit.rb

no changes added to commit (use "git add" and/or "git commit -a")
```

可以看到 Git 重新修改了当你保存贮藏时撤消的文件。 在本例中，当尝试应用贮藏时有一个干净的工作目录，并且尝试将它应用在保存它时所在的分支。 并不是必须要有一个干净的工作目录，或者要应用到同一分支才能成功应用贮藏。 可以在一个分支上保存一个贮藏，切换到另一个分支，然后尝试重新应用这些修改。 当应用贮藏时工作目录中也可以有修改与未提交的文件——如果有任何东西不能干净地应用，Git 会产生合并冲突。

文件的改动被重新应用了，但是之前暂存的文件却没有重新暂存。 想要那样的话，必须使用 `--index` 选项来运行 `git stash apply` 命令，来尝试重新应用暂存的修改。 如果已经那样做了，那么你将回到原来的位置：

```shell
$ git stash apply --index
On branch master
Changes to be committed:
  (use "git reset HEAD <file>..." to unstage)

	modified:   index.html

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	modified:   lib/simplegit.rb
```

应用选项只会尝试应用贮藏的工作——在堆栈上还有它。 可以运行 `git stash drop` 加上将要移除的贮藏的名字来移除它：

```shell
$ git stash list
stash@{0}: WIP on master: 049d078 added the index file
stash@{1}: WIP on master: c264051 Revert "added file_size"
stash@{2}: WIP on master: 21d80a5 added number to log
$ git stash drop stash@{0}
Dropped stash@{0} (364e91f3f268f0900bc3ee613f9f733e82aaed43)
```

也可以运行 `git stash pop` 来应用贮藏然后立即从栈上扔掉它。

### 清理工作目录

对于工作目录中一些工作或文件，你想做的也许不是贮藏而是移除。 `git clean` 命令就是用来干这个的。

清理工作目录有一些常见的原因，比如说为了移除由合并或外部工具生成的东西， 或是为了运行一个干净的构建而移除之前构建的残留。

你需要谨慎地使用这个命令，因为它被设计为从工作目录中移除未被追踪的文件。 如果你改变主意了，你也不一定能找回来那些文件的内容。 一个更安全的选项是运行 `git stash --all` 来移除每一样东西并存放在栈中。

你可以使用 `git clean` 命令去除冗余文件或者清理工作目录。 使用 `git clean -f -d` 命令来移除工作目录中所有未追踪的文件以及空的子目录。 `-f` 意味着“强制（force）”或“确定要移除”，使用它需要 Git 配置变量 `clean.requireForce` 没有显式设置为 `false`。

如果只是想要看看它会做什么，可以使用 `--dry-run` 或 `-n` 选项来运行命令， 这意味着“做一次演习然后告诉你 **将要** 移除什么”。

```shell
$ git clean -d -n
Would remove test.o
Would remove tmp/
```

## 搜索

无论仓库里的代码量有多少，你经常需要查找一个函数是在哪里调用或者定义的，或者显示一个方法的变更历史。 Git 提供了两个有用的工具来快速地从它的数据库中浏览代码和提交。 我们来简单的看一下。

### Git Grep

Git 提供了一个 `grep` 命令，你可以很方便地从提交历史、工作目录、甚至索引中查找一个字符串或者正则表达式。 我们用 Git 本身源代码的查找作为例子。

默认情况下 `git grep` 会查找你工作目录的文件。 第一种变体是，你可以传递 `-n` 或 `--line-number` 选项数来输出 Git 找到的匹配行的行号。

```shell
$ git grep -n gmtime_r
compat/gmtime.c:3:#undef gmtime_r
compat/gmtime.c:8:      return git_gmtime_r(timep, &result);
compat/gmtime.c:11:struct tm *git_gmtime_r(const time_t *timep, struct tm *result)
compat/gmtime.c:16:     ret = gmtime_r(timep, result);
compat/mingw.c:826:struct tm *gmtime_r(const time_t *timep, struct tm *result)
compat/mingw.h:206:struct tm *gmtime_r(const time_t *timep, struct tm *result);
date.c:482:             if (gmtime_r(&now, &now_tm))
date.c:545:             if (gmtime_r(&time, tm)) {
date.c:758:             /* gmtime_r() in match_digit() may have clobbered it */
git-compat-util.h:1138:struct tm *git_gmtime_r(const time_t *, struct tm *);
git-compat-util.h:1140:#define gmtime_r git_gmtime_r
```

### Git 日志搜索

或许你不想知道某一项在 **哪里** ，而是想知道是什么 **时候** 存在或者引入的。 `git log` 命令有许多强大的工具可以通过提交信息甚至是 diff 的内容来找到某个特定的提交。

例如，如果我们想找到 `ZLIB_BUF_MAX` 常量是什么时候引入的，我们可以使用 `-S` 选项 （在 Git 中俗称“鹤嘴锄（pickaxe）”选项）来显示新增和删除该字符串的提交。

```shell
$ git log -S ZLIB_BUF_MAX --oneline
e01503b zlib: allow feeding more than 4GB in one go
ef49a7a zlib: zlib can only process 4GB at a time
```

如果我们查看这些提交的 diff，我们可以看到在 `ef49a7a` 这个提交引入了常量，并且在 `e01503b` 这个提交中被修改了。

### 行日志搜索

行日志搜索是另一个相当高级并且有用的日志搜索功能。 在 `git log` 后加上 `-L` 选项即可调用，它可以展示代码中一行或者一个函数的历史。

例如，假设我们想查看 `zlib.c` 文件中`git_deflate_bound` 函数的每一次变更， 我们可以执行 `git log -L :git_deflate_bound:zlib.c`。 Git 会尝试找出这个函数的范围，然后查找历史记录，并且显示从函数创建之后一系列变更对应的补丁。

```shell
$ git log -L :git_deflate_bound:zlib.c
commit ef49a7a0126d64359c974b4b3b71d7ad42ee3bca
Author: Junio C Hamano <gitster@pobox.com>
Date:   Fri Jun 10 11:52:15 2011 -0700

    zlib: zlib can only process 4GB at a time

diff --git a/zlib.c b/zlib.c
--- a/zlib.c
+++ b/zlib.c
@@ -85,5 +130,5 @@
-unsigned long git_deflate_bound(z_streamp strm, unsigned long size)
+unsigned long git_deflate_bound(git_zstream *strm, unsigned long size)
 {
-       return deflateBound(strm, size);
+       return deflateBound(&strm->z, size);
 }


commit 225a6f1068f71723a910e8565db4e252b3ca21fa
Author: Junio C Hamano <gitster@pobox.com>
Date:   Fri Jun 10 11:18:17 2011 -0700

    zlib: wrap deflateBound() too

diff --git a/zlib.c b/zlib.c
--- a/zlib.c
+++ b/zlib.c
@@ -81,0 +85,5 @@
+unsigned long git_deflate_bound(z_streamp strm, unsigned long size)
+{
+       return deflateBound(strm, size);
+}
+
```

如果 Git 无法计算出如何匹配你代码中的函数或者方法，你可以提供一个正则表达式。 例如，这个命令和上面的是等同的：`git log -L '/unsigned long git_deflate_bound/',/^}/:zlib.c`。 你也可以提供单行或者一个范围的行号来获得相同的输出。

## 重写历史

许多时候，在使用 Git 时，你可能想要修订提交历史。 Git 很棒的一点是它允许你在最后时刻做决定。 你可以在将暂存区内容提交前决定哪些文件进入提交，可以通过 `git stash` 来决定不与某些内容工作， 也可以重写已经发生的提交就像它们以另一种方式发生的一样。 这可能涉及改变提交的顺序，改变提交中的信息或修改文件，将提交压缩或是拆分， 或完全地移除提交——在将你的工作成果与他人共享之前。

> 在满意之前不要推送你的工作
>
> Git 的基本原则之一是，由于克隆中有很多工作是本地的，因此你可以 **在本地** 随便重写历史记录。 然而一旦推送了你的工作，那就完全是另一回事了，除非你有充分的理由进行更改，否则应该将推送的工作视为最终结果。 简而言之，在对它感到满意并准备与他人分享之前，应当避免推送你的工作。

### 修改最后一次提交

修改你最近一次提交可能是所有修改历史提交的操作中最常见的一个。 对于你的最近一次提交，你往往想做两件事情：简单地修改提交信息， 或者通过添加、移除或修改文件来更改提交实际的内容。

如果，你只是想修改最近一次提交的提交信息，那么很简单：

```shell
$ git commit --amend
```

上面这条命令会将最后一次的提交信息载入到编辑器中供你修改。 当保存并关闭编辑器后，编辑器会将更新后的提交信息写入新提交中，它会成为新的最后一次提交。

另一方面，如果你想要修改最后一次提交的实际内容，那么流程很相似：首先作出你想要补上的修改， 暂存它们，然后用 `git commit --amend` 以新的改进后的提交来 **替换** 掉旧有的最后一次提交，

使用这个技巧的时候需要小心，因为修正会改变提交的 SHA-1 校验和。 它类似于一个小的变基——如果已经推送了最后一次提交就不要修正它。

### 修改多个提交信息

为了修改在提交历史中较远的提交，必须使用更复杂的工具。 Git 没有一个改变历史工具，但是可以使用变基工具来变基一系列提交，基于它们原来的 HEAD 而不是将其移动到另一个新的上面。 通过交互式变基工具，可以在任何想要修改的提交后停止，然后修改信息、添加文件或做任何想做的事情。 可以通过给 `git rebase` 增加 `-i` 选项来交互式地运行变基。 必须指定想要重写多久远的历史，这可以通过告诉命令将要变基到的提交来做到。

例如，如果想要修改最近三次提交信息，或者那组提交中的任意一个提交信息， 将想要修改的最近一次提交的父提交作为参数传递给 `git rebase -i` 命令，即 `HEAD~2^` 或 `HEAD~3`。 记住 `~3` 可能比较容易，因为你正尝试修改最后三次提交；但是注意实际上指定了以前的四次提交，即想要修改提交的父提交：

```console
$ git rebase -i HEAD~3
```

再次记住这是一个变基命令——在 `HEAD~3..HEAD` 范围内的每一个修改了提交信息的提交及其 **所有后裔** 都会被重写。 不要涉及任何已经推送到中央服务器的提交——这样做会产生一次变更的两个版本，因而使他人困惑。

运行这个命令会在文本编辑器上给你一个提交的列表，看起来像下面这样：

```shell
pick f7f3f6d changed my name a bit
pick 310154e updated README formatting and added blame
pick a5f4a0d added cat-file

# Rebase 710f0f8..a5f4a0d onto 710f0f8
#
# Commands:
# p, pick <commit> = use commit
# r, reword <commit> = use commit, but edit the commit message
# e, edit <commit> = use commit, but stop for amending
# s, squash <commit> = use commit, but meld into previous commit
# f, fixup <commit> = like "squash", but discard this commit's log message
# x, exec <command> = run command (the rest of the line) using shell
# b, break = stop here (continue rebase later with 'git rebase --continue')
# d, drop <commit> = remove commit
# l, label <label> = label current HEAD with a name
# t, reset <label> = reset HEAD to a label
# m, merge [-C <commit> | -c <commit>] <label> [# <oneline>]
# .       create a merge commit using the original merge commit's
# .       message (or the oneline, if no original merge commit was
# .       specified). Use -c <commit> to reword the commit message.
#
# These lines can be re-ordered; they are executed from top to bottom.
#
# If you remove a line here THAT COMMIT WILL BE LOST.
#
# However, if you remove everything, the rebase will be aborted.
#
# Note that empty commits are commented out
```

需要重点注意的是相对于正常使用的 `log` 命令，这些提交显示的顺序是相反的。 运行一次 *log* 命令，会看到类似这样的东西：

```shell
$ git log --pretty=format:"%h %s" HEAD~3..HEAD
a5f4a0d added cat-file
310154e updated README formatting and added blame
f7f3f6d changed my name a bit
```

注意其中的反序显示。 交互式变基给你一个它将会运行的脚本。 它将会从你在命令行中指定的提交（`HEAD~3`）开始，从上到下的依次重演每一个提交引入的修改。 它将最旧的而不是最新的列在上面，因为那会是第一个将要重演的。

你需要修改脚本来让它停留在你想修改的变更上。 要达到这个目的，你只要将你想修改的每一次提交前面的 ‘pick’ 改为 ‘edit’。 例如，只想修改第三次提交信息，可以像下面这样修改文件：

```shell
edit f7f3f6d changed my name a bit
pick 310154e updated README formatting and added blame
pick a5f4a0d added cat-file
```

当保存并退出编辑器时，Git 将你带回到列表中的最后一次提交，把你送回命令行并提示以下信息：

```shell
$ git rebase -i HEAD~3
Stopped at f7f3f6d... changed my name a bit
You can amend the commit now, with

       git commit --amend

Once you're satisfied with your changes, run

       git rebase --continue
```

这些指令准确地告诉你该做什么。 输入

```shell
$ git commit --amend
```

修改提交信息，然后退出编辑器。 然后，运行

```shell
$ git rebase --continue
```

这个命令将会自动地应用另外两个提交，然后就完成了。 如果需要将不止一处的 pick 改为 edit，需要在每一个修改为 edit 的提交上重复这些步骤。 每一次，Git 将会停止，让你修正提交，然后继续直到完成。

### 重新排序提交

也可以使用交互式变基来重新排序或完全移除提交。 如果想要移除 “added cat-file” 提交然后修改另外两个提交引入的顺序，可以将变基脚本从这样：

```console
pick f7f3f6d changed my name a bit
pick 310154e updated README formatting and added blame
pick a5f4a0d added cat-file
```

改为这样：

```console
pick 310154e updated README formatting and added blame
pick f7f3f6d changed my name a bit
```

当保存并退出编辑器时，Git 将你的分支带回这些提交的父提交，应用 `310154e` 然后应用 `f7f3f6d`，最后停止。 事实修改了那些提交的顺序并完全地移除了 “added cat-file” 提交。

### 压缩提交

通过交互式变基工具，也可以将一连串提交压缩成一个单独的提交。 在变基信息中脚本给出了有用的指令：

```shell
#
# Commands:
# p, pick <commit> = use commit
# r, reword <commit> = use commit, but edit the commit message
# e, edit <commit> = use commit, but stop for amending
# s, squash <commit> = use commit, but meld into previous commit
# f, fixup <commit> = like "squash", but discard this commit's log message
# x, exec <command> = run command (the rest of the line) using shell
# b, break = stop here (continue rebase later with 'git rebase --continue')
# d, drop <commit> = remove commit
# l, label <label> = label current HEAD with a name
# t, reset <label> = reset HEAD to a label
# m, merge [-C <commit> | -c <commit>] <label> [# <oneline>]
# .       create a merge commit using the original merge commit's
# .       message (or the oneline, if no original merge commit was
# .       specified). Use -c <commit> to reword the commit message.
#
# These lines can be re-ordered; they are executed from top to bottom.
#
# If you remove a line here THAT COMMIT WILL BE LOST.
#
# However, if you remove everything, the rebase will be aborted.
#
# Note that empty commits are commented out
```

如果，指定 “squash” 而不是 “pick” 或 “edit”，Git 将应用两者的修改并合并提交信息在一起。 所以，如果想要这三次提交变为一个提交，可以这样修改脚本：

```shell
pick f7f3f6d changed my name a bit
squash 310154e updated README formatting and added blame
squash a5f4a0d added cat-file
```

当保存并退出编辑器时，Git 应用所有的三次修改然后将你放到编辑器中来合并三次提交信息：

```shell
# This is a combination of 3 commits.
# The first commit's message is:
changed my name a bit

# This is the 2nd commit message:

updated README formatting and added blame

# This is the 3rd commit message:

added cat-file
```

当你保存之后，你就拥有了一个包含前三次提交的全部变更的提交。

## 重置揭秘

在继续了解更专业的工具前，我们先探讨一下 Git 的 `reset` 和 `checkout` 命令

### 三棵树

理解 `reset` 和 `checkout` 的最简方法，就是以 Git 的思维框架（将其作为内容管理器）来管理三棵不同的树。 “树” 在我们这里的实际意思是 “文件的集合”，而不是指特定的数据结构。 （在某些情况下索引看起来并不像一棵树，不过我们现在的目的是用简单的方式思考它。）

Git 作为一个系统，是以它的一般操作来管理并操纵这三棵树的：

| 树                | 用途                                 |
| :---------------- | :----------------------------------- |
| HEAD              | 上一次提交的快照，下一次提交的父结点 |
| Index             | 预期的下一次提交的快照               |
| Working Directory | 沙盒                                 |

#### HEAD

HEAD 是当前分支引用的指针，它总是指向该分支上的最后一次提交。 这表示 HEAD 将是下一次提交的父结点。 通常，理解 HEAD 的最简方式，就是将它看做 **该分支上的最后一次提交** 的快照。

#### 索引

索引是你的 **预期的下一次提交**。 我们也会将这个概念引用为 Git 的“暂存区”，这就是当你运行 `git commit` 时 Git 看起来的样子。

Git 将上一次检出到工作目录中的所有文件填充到索引区，它们看起来就像最初被检出时的样子。 之后你会将其中一些文件替换为新版本，接着通过 `git commit` 将它们转换为树来用作新的提交。

#### 工作目录

最后，你就有了自己的 **工作目录**（通常也叫 **工作区**）。 另外两棵树以一种高效但并不直观的方式，将它们的内容存储在 `.git` 文件夹中。 工作目录会将它们解包为实际的文件以便编辑。 你可以把工作目录当做 **沙盒**。在你将修改提交到暂存区并记录到历史之前，可以随意更改。

### 工作流程

经典的 Git 工作流程是通过操纵这三个区域来以更加连续的状态记录项目快照的。

![](https://git-scm.com/book/en/v2/images/reset-workflow.png)

让我们跟着 `reset` 看看它都做了什么。 它以一种简单可预见的方式直接操纵这三棵树。 它做了三个基本操作。

`reset` 命令会以特定的顺序重写这三棵树，在你指定以下选项时停止：

1. 移动 HEAD 分支的指向 *（若指定了 `--soft`，则到此停止）*
2. 使索引看起来像 HEAD *（若未指定 `--hard`，则到此停止）*
3. 使工作目录看起来像索引
