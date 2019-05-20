/*
 * @author smallufo 
 * Created on 2007/2/5 at 上午 10:42:36
 * 
 * Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
 * Jad home page: <a href="http://www.geocities.com/SiliconValley/Bridge/8617/jad.html" target="_blank">http://www.geocities.com/SiliconValley/Bridge/8617/jad.html</a>
 * Decompiler options: packimports(3) fieldsfirst ansi 
 * Source File Name:   DataFilter.java
 */

package destiny.tools

import java.util.regex.Pattern

class UBBCodeConverter {
  private var str: String
  private var str1: String
  internal var result: Boolean = false

  init {
    this.str = ""
    this.str1 = ""
  }

  fun getAll(s: String): String {
    var s1 = s
    s1 = reImg(s1)
    s1 = reUrl(s1)
    s1 = reFly(s1)
    s1 = reEmail(s1)
    s1 = reBold(s1)
    s1 = reSlope(s1)
    s1 = reDownLine(s1)
    s1 = reMove(s1)
    s1 = reTxtUrl(s1)
    s1 = reTxtEmail(s1)
    s1 = reAlign(s1)
    s1 = reTxtSize(s1)
    s1 = reQuote(s1)
    s1 = reCode(s1)
    s1 = reSup(s1)
    s1 = reSub(s1)
    s1 = reDelLine(s1)
    s1 = reFliph(s1)
    s1 = reFlipv(s1)
    s1 = reShadow(s1)
    s1 = reGlow(s1)
    s1 = reBlur(s1)
    s1 = reSwf(s1)
    s1 = reRm(s1)
    s1 = reMp(s1)
    s1 = reQt(s1)
    s1 = reSk(s1)
    s1 = reSound(s1)
    s1 = reInvert(s1)
    s1 = reXray(s1)
    s1 = reLi(s1)
    s1 = reLi1(s1)
    s1 = reLi2(s1)
    return s1
  }

  /**
   * 2017-03-23 新增 : 除了 [quote] 以外都處理
   */
  fun getAllExceptQuote(s: String): String {
    var s1 = s
    s1 = reImg(s1)
    s1 = reUrl(s1)
    s1 = reFly(s1)
    s1 = reEmail(s1)
    s1 = reBold(s1)
    s1 = reSlope(s1)
    s1 = reDownLine(s1)
    s1 = reMove(s1)
    s1 = reTxtUrl(s1)
    s1 = reTxtEmail(s1)
    s1 = reAlign(s1)
    s1 = reTxtSize(s1)
    s1 = reCode(s1)
    s1 = reSup(s1)
    s1 = reSub(s1)
    s1 = reDelLine(s1)
    s1 = reFliph(s1)
    s1 = reFlipv(s1)
    s1 = reShadow(s1)
    s1 = reGlow(s1)
    s1 = reBlur(s1)
    s1 = reSwf(s1)
    s1 = reRm(s1)
    s1 = reMp(s1)
    s1 = reQt(s1)
    s1 = reSk(s1)
    s1 = reSound(s1)
    s1 = reInvert(s1)
    s1 = reXray(s1)
    s1 = reLi(s1)
    s1 = reLi1(s1)
    s1 = reLi2(s1)
    return s1
  }


  fun replace(s: String, s1: String, s2: String): String {
    return s.replace(s1, s2)
//    var s = s
//    val s3 = StringBuilder()
//    val i = s1.length
//    var j: Int
//    while ((j = s.indexOf(s1)) != -1) {
//      s3.append(s, 0, j)
//      s3.append(s2)
//      s = s.substring(j + i)
//    }
//    s3.append(s)
//    return s3.toString()
  }

  private fun reImg(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[img\\])(.+?)(\\[\\/img\\])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        //str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<a href=\"" + matcher.group(2) + "\" target=\"_blank\" title=\"開新視窗瀏覽\"><img src=" + matcher.group(2) + " border=0 onload=\"javascript:if(this.w>screen.w-333)this.w=screen.w-333;\" this.alt='點擊查看全圖'\" onmouseover=\"if(this.alt) this.style.cursor='hand';\"></a>");
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<img src=" + matcher.group(2) + " border=0>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reInvert(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val up = Pattern.compile("(\\[invert])(.+?)(\\[/invert])", 2)
      val um = up.matcher(us2)
      result = um.find()
      while (result) {
        str = replace(str1, um.group(1) + um.group(2) + um.group(3), "<table style=\"filter:invert\"><img src=" + um
          .group(2) + " border=0></table>")
        str1 = str
        um.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reXray(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[xray])(.+?)(\\[/xray])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<table style=\"filter:xray\"><img src=" + matcher.group(2) + " border=0></table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reUrl(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[url])(.+?)(\\[/url])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<a href=" + matcher.group(2) + " target=_blank>" + matcher
          .group(2) + "</a>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reTxtUrl(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[url=)([^]]+)(])(.+?)(\\[/url])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        //str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<a href=" + matcher.group(2) + " target=_blank title=" + matcher.group(4) + ">" + matcher.group(4) + "</a>");
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<a href=" + matcher.group(2) + " target=_blank >" + matcher.group(4) + "</a>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reEmail(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[email])(.+?)(\\[/email])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<a href=mailto:" + matcher.group(2) + ">" + matcher.group(2) + "</a>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reTxtEmail(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[email=)([^]]+)([]])(.+?)(\\[/email])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<a href=mailto:" + matcher
          .group(2) + ">" + matcher.group(4) + "</a>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reAlign(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[align=)([^]]+)([]])(.+?)(\\[/align])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<pattern align=" + matcher
          .group(2) + ">" + matcher.group(4) + "</pattern>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reFly(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[fly])(.+?)(\\[/fly])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<div><marquee behavior=alternate>" + matcher.group(2) + "</marquee></div>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reMove(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[move])(.+?)(\\[/move])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<div><marquee>" + matcher.group(2) + "</marquee></div>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reBold(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[b])(.+?)(\\[/b])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<b>" + matcher.group(2) + "</b>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reSlope(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[i])(.+?)(\\[/i])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<i>" + matcher.group(2) + "</i>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reDownLine(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[u])(.+?)(\\[/u])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<u>" + matcher.group(2) + "</u>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reSup(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[sup])(.+?)(\\[/sup])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<sup>" + matcher.group(2) + "</sup>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reSub(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[sub])(.+?)(\\[/sub])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<sub>" + matcher.group(2) + "</sub>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reDelLine(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[s])(.+?)(\\[/s])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<s>" + matcher.group(2) + "</s>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reFliph(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[fliph])(.+?)(\\[/fliph])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<table style=\"filter:fliph\">" + matcher.group(2) + "</table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reFlipv(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[flipv])(.+?)(\\[/flipv])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<table style=\"filter:flipv\">" + matcher.group(2) + "</table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reLi(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[list=)([^]]+)([]])(.+?)(\\[/list])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<ol type=" + matcher
          .group(2) + ">" + matcher.group(4) + "</ol>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reLi1(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[list])(.+?)(\\[/list])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<ul>" + matcher.group(2) + "</ul>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reLi2(s: String): String {
    return replace(s, "[*]", "<li>")
  }

  private fun reShadow(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[shadow=)([0-9]+)([,])([^,]+)([,])([^]]+)(])(.+?)(\\[/shadow])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7) + matcher.group(8) + matcher.group(9), "<table w=" + matcher.group(2) + " style=\"filter:shadow(color=" + matcher
          .group(4) + ", direction=" + matcher.group(6) + ")\">" + matcher.group(8) + "</table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reGlow(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[glow=)([0-9]+)([,])([^,]+)([,])([^]]+)(])(.+?)(\\[/glow])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7) + matcher.group(8) + matcher.group(9), "<table w=" + matcher.group(2) + " style=\"filter:glow(color=" + matcher
          .group(4) + ", strength=" + matcher.group(6) + ")\">" + matcher.group(8) + "</table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reBlur(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[blur=)([0-9]+)([,])([^,]+)([,])([^]]+)(])(.+?)(\\[/blur])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7) + matcher.group(8) + matcher.group(9), "<table w=" + matcher.group(2) + " style=\"filter:blur(Add=0,direction=" + matcher
          .group(4) + ", strength=" + matcher.group(6) + ")\">" + matcher.group(8) + "</table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reQuote(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[quote])(.+?)(\\[/quote])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<hr noshade size=1 ><blockquote>" + matcher.group(2) + "</blockquote><hr noshade size=1>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reCode(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[code])(.+?)(\\[/code])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<table bgcolor=\"black\"><tr><td><font color=white>" + matcher
          .group(2) + "</font></td></tr></table>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reTxtSize(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[size=)([1-9])([]])(.+?)(\\[/size])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<font size=" + matcher
          .group(2) + ">" + matcher.group(4) + "</font>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reSwf(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[swf=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/swf])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7), "<a href=\"" + matcher.group(6) + "\" target=\"_blank\" title=\"全螢幕播放\">全螢幕播放</a><br><OBJECT codeBase=http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=4,0,2,0 classid=clsid:D27CDB6E-AE6D-11cf-96B8-444553540000 w=" + matcher
          .group(2) + " h=" + matcher.group(4) + "><PARAM NAME=movie VALUE=\"" + matcher.group(6) + "\"><PARAM NAME=quality VALUE=high><embed src=\"" + matcher
          .group(6) + "\" quality=high pluginspage='<img align=absmiddle src=pic/url.gif border=0><a target=_blank href=http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'>http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'</a> type='application/x-shockwave-flash' w=" + matcher
          .group(2) + " h=" + matcher.group(4) + ">http://</embed></OBJECT>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reSk(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[sk=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/sk])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7), "<object classid=clsid:166B1BCA-3F9C-11CF-8075-444553540000 codebase=http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=7,0,2,0 w=" + matcher
          .group(2) + " h=" + matcher.group(4) + "><param name=src value=" + matcher.group(6) + "><embed src=\\3 pluginspage=http://www.macromedia.com/shockwave/download/ w=" + matcher
          .group(2) + " h=" + matcher.group(4) + "></embed></object>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reRm(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[rm=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/rm])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7), "<OBJECT classid=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA class=OBJECT id=RAOCX w=" + matcher.group(2) + " h=" + matcher
          .group(4) + "><PARAM NAME=SRC VALUE=" + matcher.group(6) + "><PARAM NAME=CONSOLE VALUE=Clip1><PARAM NAME=CONTROLS VALUE=imagewindow><PARAM NAME=AUTOSTART VALUE=true></OBJECT><br><OBJECT classid=CLSID:CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA h=30 id=video2 w=" + matcher
          .group(2) + "><PARAM NAME=SRC VALUE=" + matcher.group(6) + "><PARAM NAME=AUTOSTART VALUE=-1><PARAM NAME=CONTROLS VALUE=controlpanel><PARAM NAME=CONSOLE VALUE=Clip1></OBJECT>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reMp(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[mp=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/mp])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7), "<object align=middle classid=CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95 class=OBJECT id=MediaPlayer w=" + matcher
          .group(2) + " h=" + matcher.group(4) + " ><param name=ShowStatusBar value=-1><param name=Filename value=" + matcher
          .group(6) + "><embed type=application/x-oleobject codebase=http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701 flename=mp src=" + matcher
          .group(6) + " w=" + matcher.group(2) + " h=" + matcher.group(4) + "></embed></object>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reQt(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[qt=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/qt])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5) + matcher.group(6) + matcher
          .group(7), "<embed src=" + matcher.group(6) + " w=" + matcher.group(2) + " h=" + matcher.group(4) + " autoplay=true loop=false controller=true playeveryframe=false cache=false scale=TOFIT bgcolor=#000000 kioskmode=false targetcache=false pluginspage=http://www.apple.com/quicktime/>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reSound(s: String): String {
    str1 = s
    str = s
    val us2 = str1.subSequence(0, str1.length)
    try {
      val pattern = Pattern.compile("(\\[sound])(.+?)(\\[/sound])", 2)
      val matcher = pattern.matcher(us2)
      result = matcher.find()
      while (result) {
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3), "<EMBED SRC=" + matcher.group(2) + " HIDDEN=TRUE AUTOSTART=TRUE LOOP=TRUE>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

}
