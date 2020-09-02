# 写在前面

参考资料

- [官网文档](https://sourceware.org/gdb/current/onlinedocs/gdb/)
- [资料](http://c.biancheng.net/gdb/)

# 总结

## what is GDB？

GDB,全称是GNU Project debugger。允许我们在执行程序时看到里面发生了什么，或者程序崩溃时发生了什么

GDB可以做下面四种事情来帮助我们找到bug

- 启动程序，执行可能影响其行为的操作
- 在特定情况下终止程序
- 程序停止时测试发生了什么
- 改变程序中的东西，以此来试验修复bug

## GDB支持哪些语言

gdb主要支持

- C
- C++
- D
- Fortran
- Go
- Objective-C
- Pascal
- Rust

# 常用命令

| 命令                                                      | 解释                                                         | 示例                                                         |
| --------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| file <文件名>                                             | 加载被调试的可执行程序文件。 因为一般都在被调试程序所在目录下执行GDB，因而文本名不需要带路径。 | (gdb) file gdb-sample                                        |
| r                                                         | Run的简写，运行被调试的程序。 如果此前没有下过断点，则执行完整个程序；如果有断点，则程序暂停在第一个可用断点处。 | (gdb) r                                                      |
| c                                                         | Continue的简写，继续执行被调试程序，直至下一个断点或程序结束。 | (gdb) c                                                      |
| b <行号> b <函数名称> b *<函数名称> b *<代码地址>d [编号] | b: Breakpoint的简写，设置断点。两可以使用“行号”“函数名称”“执行地址”等方式指定断点位置。 其中在函数名称前面加“*”符号表示将断点设置在“由编译器生成的prolog代码处”。如果不了解汇编，可以不予理会此用法。d: Delete breakpoint的简写，删除指定编号的某个断点，或删除所有断点。断点编号从1开始递增。 | (gdb) b 8 (gdb) b main (gdb) b *main (gdb) b *0x804835c(gdb) d |
| s, n                                                      | s: 执行一行源程序代码，如果此行代码中有函数调用，则进入该函数； n: 执行一行源程序代码，此行代码中的函数调用也一并执行。s 相当于其它调试器中的“Step Into (单步跟踪进入)”； n 相当于其它调试器中的“Step Over (单步跟踪)”。这两个命令必须在有源代码调试信息的情况下才可以使用（GCC编译时使用“-g”参数）。 | (gdb) s (gdb) n                                              |
| si, ni                                                    | si命令类似于s命令，ni命令类似于n命令。所不同的是，这两个命令（si/ni）所针对的是汇编指令，而s/n针对的是源代码。 | (gdb) si (gdb) ni                                            |
| p <变量名称>                                              | Print的简写，显示指定变量（临时变量或全局变量）的值。        | (gdb) p i (gdb) p nGlobalVar                                 |
| display ...undisplay <编号>                               | display，设置程序中断后欲显示的数据及其格式。 例如，如果希望每次程序中断后可以看到即将被执行的下一条汇编指令，可以使用命令 “display /i $pc” 其中 $pc 代表当前汇编指令，/i 表示以十六进行显示。当需要关心汇编代码时，此命令相当有用。undispaly，取消先前的display设置，编号从1开始递增。 | (gdb) display /i $pc(gdb) undisplay 1                        |
| i                                                         | Info的简写，用于显示各类信息，详情请查阅“help i”。           | (gdb) i r                                                    |
| q                                                         | Quit的简写，退出GDB调试环境。                                | (gdb) q                                                      |
| help [命令名称]                                           | GDB帮助命令，提供对GDB名种命令的解释说明。 如果指定了“命令名称”参数，则显示该命令的详细说明；如果没有指定参数，则分类显示所有GDB命令，供用户进一步浏览和查询。 | (gdb) help display                                           |
| bt                                                        | 当前运行的堆栈列表                                           |                                                              |
| until                                                     | 当你厌倦了在一个循环体内单步跟踪时，这个命令可以运行程序直到退出循环体。 |                                                              |
| list                                                      | 其作用就是列出程序的源代码，默认每次显示10行。                                           list 行号：将显示当前文件以“行号”为中心的前后10行代码，如：list 12                 list 函数名：将显示“函数名”所在函数的源代码，如：list main |                                                              |
| layout                                                    | 用于分割窗口，可以一边查看代码，一边测试：                                                             src 显示源代码窗口 |                                                              |
| watch                                                    | 监控变量值的变化                                                           |        (gdb) watch cond                                                      |

# 示例代码

```c
#include <stdio.h>

int nGlobalVar = 0;

int tempFunction(int a, int b)
{
    printf("tempFunction is called, a = %d, b = %d /n", a, b);
    return (a + b);
}

int main()
{
    int n;
    n = 1;
    n++;
    n--;

    nGlobalVar += 100;
    nGlobalVar -= 12;

    printf("n = %d, nGlobalVar = %d /n", n, nGlobalVar);

    n = tempFunction(1, 2);
    printf("n = %d", n);

    return 0;
}
```

保存在gdb-sample.c中。然后使用gcc编译

```shell
gcc gdb-sample.c -o gdb-sample -g
```

在上面的命令行中，使用 -o 参数指定了编译生成的可执行文件名为 gdb-sample，使用参数 -g 表示将源代码信息编译到可执行文件中。如果不使用参数 -g，会给后面的GDB调试造成不便。

使用gdb命令启动

```powershell
GNU gdb Red Hat Linux (5.3post-0.20021129.18rh)
Copyright 2003 Free Software Foundation, Inc.
GDB is free software, covered by the GNU General Public License, and you are
welcome to change it and/or distribute copies of it under certain conditions.
Type "show copying" to see the conditions.
There is absolutely no warranty for GDB. Type "show warranty" for details.
This GDB was configured as "i386-redhat-linux-gnu".
(gdb) 
```

使用file命令载入被调试的程序，这里加载可执行文件gdb-sample

```shell
(gdb) file gdb-sample
Reading symbols from gdb-sample...done.
```

下面使用“r”命令执行（Run）被调试文件，因为尚未设置任何断点，将直接执行到程序结束：

```shell
(gdb) r
Starting program: /home/liigo/temp/test_jmp/test_jmp/gdb-sample
n = 1, nGlobalVar = 88
tempFunction is called, a = 1, b = 2
n = 3
Program exited normally.
```

下面使用“b”命令在 main 函数开头设置一个断点（Breakpoint）：

```
(gdb) b main 
Breakpoint 1 at 0x804835c: file gdb-sample.c, line 19.
```

上面最后一行提示已经成功设置断点，并给出了该断点信息：在源文件 gdb-sample.c 第19行处设置断点；这是本程序的第一个断点（序号为1）；断点处的代码地址为 0x804835c（此值可能仅在本次调试过程中有效）。回过头去看源代码，第19行中的代码为“n = 1”，恰好是 main 函数中的第一个可执行语句（前面的“int n;”为变量定义语句，并非可执行语句）。

再次使用“r”命令执行（Run）被调试程序：

```shell
(gdb) r 
Starting program: /home/liigo/temp/gdb-sample  
Breakpoint 1, main () at gdb-sample.c:19 
19 n = 1;
```

程序中断在gdb-sample.c第19行处，即main函数是第一个可执行语句处。

上面最后一行信息为：下一条将要执行的源代码为“n = 1;”，它是源代码文件gdb-sample.c中的第19行。

下面使用“s”命令（Step）执行下一行代码（即第19行“n = 1;”）：

```shell
(gdb) s
20 n++;
```

上面的信息表示已经执行完“n = 1;”，并显示下一条要执行的代码为第20行的“n++;”。

既然已经执行了“n = 1;”，即给变量 n 赋值为 1，那我们用“p”命令（Print）看一下变量 n 的值是不是 1 ：

```shell
(gdb) p n 
$1 = 1
```

果然是 1。（$1大致是表示这是第一次使用“p”命令——再次执行“p n”将显示“$2 = 1”——此信息应该没有什么用处。）

下面我们分别在第26行、tempFunction 函数开头各设置一个断点（分别使用命令“b 26”“b tempFunction”）：

```shell
(gdb) b 26 
Breakpoint 2 at 0x804837b: file gdb-sample.c, line 26. 
(gdb) b tempFunction 
Breakpoint 3 at 0x804832e: file gdb-sample.c, line 12.
```

使用“c”命令继续（Continue）执行被调试程序，程序将中断在第二个断点（26行），此时全局变量 nGlobalVar 的值应该是 88；再一次执行“c”命令，程序将中断于第三个断点（12行，tempFunction 函数开头处），此时tempFunction 函数的两个参数 a、b 的值应分别是 1 和 2：

```
(gdb) c 
Continuing.  

Breakpoint 2, main () at gdb-sample.c:26 
26 printf("n = %d, nGlobalVar = %d /n", n, nGlobalVar); 
(gdb) p nGlobalVar 
$2 = 88 
(gdb) c 
Continuing. 

n = 1, nGlobalVar = 88  

Breakpoint 3, tempFunction (a=1, b=2) at gdb-sample.c:12 
12 printf("tempFunction is called, a = %d, b = %d /n", a, b); 
(gdb) p a 
$3 = 1 
(gdb) p b 
$4 = 2
```



再一次执行“c”命令（Continue），因为后面再也没有其它断点，程序将一直执行到结束：

```
(gdb) c 
Continuing. 
tempFunction is called, a = 1, b = 2
n = 3 
Program exited normally.
```

# 更强大的工具

## cgdb

cgdb可以看作gdb的界面增强版,用来替代gdb的 gdb -tui。cgdb主要功能是在调试时进行代码的同步显示，这无疑增加了调试的方便性，提高了调试效率。界面类似vi，符合unix/linux下开发人员习惯;如果熟悉gdb和vi，几乎可以立即使用cgdb。
