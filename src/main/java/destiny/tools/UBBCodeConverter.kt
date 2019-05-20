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

    return reImg(s)
      .let { reUrl(it) }
      .let { reFly(it) }
      .let { reImg(it) }
      .let { reUrl(it) }
      .let { reFly(it) }
      .let { reEmail(it) }
      .let { reBold(it) }
      .let { reSlope(it) }
      .let { reDownLine(it) }
      .let { reMove(it) }
      .let { reTxtUrl(it) }
      .let { reTxtEmail(it) }
      .let { reAlign(it) }
      .let { reTxtSize(it) }
      .let { reQuote(it) }
      .let { reCode(it) }
      .let { reSup(it) }
      .let { reSub(it) }
      .let { reDelLine(it) }
      .let { reFliph(it) }
      .let { reFlipv(it) }
      .let { reShadow(it) }
      .let { reGlow(it) }
      .let { reBlur(it) }
      .let { reSwf(it) }
      .let { reRm(it) }
      .let { reMp(it) }
      .let { reQt(it) }
      .let { reSk(it) }
      .let { reSound(it) }
      .let { reInvert(it) }
      .let { reXray(it) }
      .let { reLi(it) }
      .let { reLi1(it) }
      .let { reLi2(it) }
  }

  /**
   * 2017-03-23 新增 : 除了 [quote] 以外都處理
   */
  fun getAllExceptQuote(s: String): String {
    return reImg(s)
      .let { reUrl(it) }
      .let { reFly(it) }
      .let { reImg(it) }
      .let { reUrl(it) }
      .let { reFly(it) }
      .let { reEmail(it) }
      .let { reBold(it) }
      .let { reSlope(it) }
      .let { reDownLine(it) }
      .let { reMove(it) }
      .let { reTxtUrl(it) }
      .let { reTxtEmail(it) }
      .let { reAlign(it) }
      .let { reTxtSize(it) }
      .let { reCode(it) }
      .let { reSup(it) }
      .let { reSub(it) }
      .let { reDelLine(it) }
      .let { reFliph(it) }
      .let { reFlipv(it) }
      .let { reShadow(it) }
      .let { reGlow(it) }
      .let { reBlur(it) }
      .let { reSwf(it) }
      .let { reRm(it) }
      .let { reMp(it) }
      .let { reQt(it) }
      .let { reSk(it) }
      .let { reSound(it) }
      .let { reInvert(it) }
      .let { reXray(it) }
      .let { reLi(it) }
      .let { reLi1(it) }
      .let { reLi2(it) }
  }


  fun replace(s: String, s1: String, s2: String): String {
    return s.replace(s1, s2)
  }

  private fun reImg(s: String): String {
    val regex = "(\\[img])(.+?)(\\[/img])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<img src=" + r.groupValues[2] + " border=0>" }
  }

  private fun reInvert(s: String): String {

    val regex = "(\\[invert])(.+?)(\\[/invert])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<table style=\"filter:invert\"><img src=" + r.groupValues[2] + " border=0></table>"
    }
  }

  private fun reXray(s: String): String {
    val regex = "(\\[xray])(.+?)(\\[/xray])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<table style=\"filter:xray\"><img src=" + r.groupValues[2] + " border=0></table>" }
  }

  private fun reUrl(s: String): String {

    val regex = "(\\[url])(.+?)(\\[/url])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->  "<a href=" + r.groupValues[2] + " target=_blank>" + r.groupValues[2] + "</a>"}
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
        str = replace(str1, matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5), "<a href=" + matcher.group(2) + " target=_blank >" + matcher.group(4) + "</a>")
        str1 = str
        matcher.find()
      }
    } catch (ignored: Exception) {
    }

    return str
  }

  private fun reEmail(s: String): String {
    val regex = "(\\[email])(.+?)(\\[/email])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<a href=mailto:" + r.groupValues[2] + ">" + r.groupValues[2] + "</a>" }
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
    val regex = "(\\[fly])(.+?)(\\[/fly])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<div><marquee behavior=alternate>" + r.groupValues[2] + "</marquee></div>" }
  }

  private fun reMove(s: String): String {
    val regex = "(\\[move])(.+?)(\\[/move])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<div><marquee>"+r.groupValues[2]+"</marquee></div>"}
  }

  private fun reBold(s: String): String {
    val regex = "(\\[b])(.+?)(\\[/b])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<b>"+r.groupValues[2]+"</b>"}
  }

  private fun reSlope(s: String): String {

    val regex = "(\\[i])(.+?)(\\[/i])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<i>"+r.groupValues[2]+"</i>"}
  }

  private fun reDownLine(s: String): String {
    val regex = "(\\[u])(.+?)(\\[/u])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<u>"+r.groupValues[2]+"</u>"}
  }

  private fun reSup(s: String): String {
    val regex = "(\\[sup])(.+?)(\\[/sup])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<sup>"+r.groupValues[2]+"</sup>"}
  }

  private fun reSub(s: String): String {
    val regex = "(\\[sub])(.+?)(\\[/sub])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<sub>"+r.groupValues[2]+"</sub>"}
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

    return """(\[quote])(.+?)(\[/quote])""".toRegex(RegexOption.IGNORE_CASE)
      .toPattern().matcher(s)
      ?.takeIf { matcher -> matcher.find() }
      ?.let { matcher ->
        replace(s, matcher.group(1) + matcher.group(2) + matcher.group(3), "<hr noshade size=1 ><blockquote>" + matcher.group(2) + "</blockquote><hr noshade size=1>")
      }?:s
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
    val regex = "(\\[sound])(.+?)(\\[/sound])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<EMBED SRC=" + r.groupValues[2] + " HIDDEN=TRUE AUTOSTART=TRUE LOOP=TRUE>"
    }
  }

}
