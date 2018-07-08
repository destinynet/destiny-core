# 命理網 https://destiny.to 核心 library

此核心 library 包含命理網所有正在開發、上線中或還未上線的核心演算法。

包含：八字、紫微斗數、占星術（西洋、古典）、節氣、日蝕月蝕、真太陽時、星體升落、風水、羅盤、易經、易卦、陰陽曆法、六壬（金口訣）、一掌經...（持續增加中）。

### Q : 這是什麼 library ?
A : 此即整個命理架構最核心的演算法、資料結構、抽象介面 (interface)。

### Q : 程式語言用哪種？
A : Kotlin . 原本使用 Java , 最近半年 (since 2017年年底) 逐漸 migrate 到 Kotlin。

### Q : 請問怎麼使用？
A : 請自行讀 code (本人不負責教學). Test case 都有附上，自己看。

### Q : 我要怎麼學習讀懂？
A : 您必須熟悉 Java / Kotlin / Object-Oriented & Functional programming 才能看得懂。

### Q : 可以教我嗎？
A : 花錢請我。

### Q : 我可以在我電腦上執行排盤程式嗎？
A : 不行。

### Q : 為什麼不行？
A : 這只是「演算法」，其餘 底層實作（某些有版權問題）、graphic、web、DB 端的模組並未釋出。

### Q : 那釋出這 library 有什麼用？
A : 提供給想要寫命理程式的人一個參考。

### Q : 沒有版號或 changelog 嗎？
A : 十多年來一直是 0.0.1 版 (見 pom.xml ) , changelog 就是 commit log 的 comments。

### Q : 本 Library 的 API 穩定嗎？
A : 不穩定。您若是觀看 changelog , 會發現整個架構，大約三個月一小變，兩年一大變。

### Q : 為什麼 API 不穩定？
A : 因為本 library 會隨著程式設計師（就是站長一人）的眼界、經驗，逐漸引入架構中。
從最古早的 Java 1.x 大量採用 Stateful 的 OO design，
到Java 8 之後大量引入 Optional , stream , map , flatMap 等運算子，
逐漸轉向 stateless / functional style。
又到了2018大量轉向 Kotlin 後，又棄用了 Java8 的 Optional / Stream，整個架構更 pure-kotlin，更 functional。
未來還會不會大改變？有可能。

### Q : 大概多久更新程式碼？
A : 可自行看 commit code，有時幾乎天天都會 commit code 。有時會偷懶一兩週。

### Q : 這真的就是命理網在跑的程式嗎？
A : 是的，千真萬確，而且還更新。許多更新的模組還沒放到網路上。

### Q : 接受 PR (Pull Request) 嗎？
A : 不 , 您可以把書籍原文或是 link 給我，我會自己看。或是在命理網站務區提出來討論也可以。

### Q : 版權呢？
A : GPLv3



---------------------------------------
Good Luck

2018-Jul-08