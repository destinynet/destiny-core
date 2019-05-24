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

class UBBCodeConverter {

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
      .let { reFlipH(it) }
      .let { reFlipV(it) }
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
      .let { reFlipH(it) }
      .let { reFlipV(it) }
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
    return s.replace(regex) { r -> "<a href=" + r.groupValues[2] + " target=_blank>" + r.groupValues[2] + "</a>" }
  }

  private fun reTxtUrl(s: String): String {
    val regex = "(\\[url=)([^]]+)(])(.+?)(\\[/url])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<a href=" + r.groupValues[2] + " target=_blank >" + r.groupValues[4] + "</a>" }
  }

  private fun reEmail(s: String): String {
    val regex = "(\\[email])(.+?)(\\[/email])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<a href=mailto:" + r.groupValues[2] + ">" + r.groupValues[2] + "</a>" }
  }

  private fun reTxtEmail(s: String): String {
    val regex = "(\\[email=)([^]]+)([]])(.+?)(\\[/email])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<a href=mailto:" + r.groupValues[2] + ">" + r.groupValues[4] + "</a>" }
  }

  private fun reAlign(s: String): String {
    val regex = "(\\[align=)([^]]+)([]])(.+?)(\\[/align])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<pattern align=" + r.groupValues[2] + ">" + r.groupValues[4] + "</pattern>" }
  }

  private fun reFly(s: String): String {
    val regex = "(\\[fly])(.+?)(\\[/fly])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<div><marquee behavior=alternate>" + r.groupValues[2] + "</marquee></div>" }
  }

  private fun reMove(s: String): String {
    val regex = "(\\[move])(.+?)(\\[/move])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<div><marquee>" + r.groupValues[2] + "</marquee></div>" }
  }

  private fun reBold(s: String): String {
    val regex = "(\\[b])(.+?)(\\[/b])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<b>" + r.groupValues[2] + "</b>" }
  }

  private fun reSlope(s: String): String {

    val regex = "(\\[i])(.+?)(\\[/i])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<i>" + r.groupValues[2] + "</i>" }
  }

  private fun reDownLine(s: String): String {
    val regex = "(\\[u])(.+?)(\\[/u])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<u>" + r.groupValues[2] + "</u>" }
  }

  private fun reSup(s: String): String {
    val regex = "(\\[sup])(.+?)(\\[/sup])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<sup>" + r.groupValues[2] + "</sup>" }
  }

  private fun reSub(s: String): String {
    val regex = "(\\[sub])(.+?)(\\[/sub])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<sub>" + r.groupValues[2] + "</sub>" }
  }

  private fun reDelLine(s: String): String {
    val regex = "(\\[s])(.+?)(\\[/s])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<s>" + r.groupValues[2] + "</s>" }
  }

  private fun reFlipH(s: String): String {
    val regex = "(\\[fliph])(.+?)(\\[/fliph])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<table style=\"filter:fliph\">" + r.groupValues[2] + "</table>" }
  }

  private fun reFlipV(s: String): String {
    val regex = "(\\[flipv])(.+?)(\\[/flipv])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<table style=\"filter:flipv\">" + r.groupValues[2] + "</table>" }
  }

  private fun reLi(s: String): String {
    val regex = "(\\[list=)([^]]+)([]])(.+?)(\\[/list])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<ol type=" + r.groupValues[2] + ">" + r.groupValues[4] + "</ol>" }
  }

  private fun reLi1(s: String): String {
    val regex = "(\\[list])(.+?)(\\[/list])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<ul>" + r.groupValues[2] + "</ul>" }
  }

  private fun reLi2(s: String): String {
    return s.replace("[*]", "<li>")
  }

  private fun reShadow(s: String): String {
    val regex = "(\\[shadow=)([0-9]+)([,])([^,]+)([,])([^]]+)(])(.+?)(\\[/shadow])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<table w=" +
        r.groupValues[2] + " style=\"filter:shadow(color=" +
        r.groupValues[4] + ", direction=" +
        r.groupValues[6] + ")\">" +
        r.groupValues[8] + "</table>"
    }
  }

  private fun reGlow(s: String): String {
    val regex = "(\\[glow=)([0-9]+)([,])([^,]+)([,])([^]]+)(])(.+?)(\\[/glow])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<table w=" +
        r.groupValues[2] + " style=\"filter:glow(color=" +
        r.groupValues[4] + ", strength=" +
        r.groupValues[6] + ")\">" +
        r.groupValues[8] + "</table>"
    }
  }

  private fun reBlur(s: String): String {
    val regex = "(\\[blur=)([0-9]+)([,])([^,]+)([,])([^]]+)(])(.+?)(\\[/blur])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<table w=" +
        r.groupValues[2] + " style=\"filter:blur(Add=0,direction=" +
        r.groupValues[4] + ", strength=" +
        r.groupValues[6] + ")\">" +
        r.groupValues[8] + "</table>"
    }
  }

  private fun reQuote(s: String): String {
    val regex = """(\[quote])(.+?)(\[/quote])""".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<hr noshade size=1 ><blockquote>" + r.groupValues[2] + "</blockquote><hr noshade size=1>"
    }
  }

  private fun reCode(s: String): String {
    val regex = "(\\[code])(.+?)(\\[/code])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<table bgcolor=\"black\"><tr><td><font color=white>" + r.groupValues[2] + "</font></td></tr></table>"
    }
  }

  private fun reTxtSize(s: String): String {
    val regex = "(\\[size=)([1-9])([]])(.+?)(\\[/size])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r -> "<font size=" + r.groupValues[2] + ">" + r.groupValues[4] + "</font>" }
  }

  private fun reSwf(s: String): String {
    val regex = "(\\[swf=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/swf])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<a href=\"" +
        r.groupValues[6] + "\" target=\"_blank\" title=\"全螢幕播放\">全螢幕播放</a><br><OBJECT codeBase=http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=4,0,2,0 classId=clsid:D27CDB6E-AE6D-11cf-96B8-444553540000 w=" +
        r.groupValues[2] + " h=" +
        r.groupValues[4] + "><PARAM NAME=movie VALUE=\"" +
        r.groupValues[6] + "\"><PARAM NAME=quality VALUE=high><embed src=\"" +
        r.groupValues[6] + "\" quality=high pluginspage='<img align=absmiddle src=pic/url.gif border=0><a target=_blank href=http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'>http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'</a> type='application/x-shockwave-flash' w=" +
        r.groupValues[2] + " h=" +
        r.groupValues[4] + ">http://</embed></OBJECT>"
    }
  }

  private fun reSk(s: String): String {
    val regex = "(\\[sk=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/sk])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<object classId=clsid:166B1BCA-3F9C-11CF-8075-444553540000 codebase=http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=7,0,2,0 w=" +
        r.groupValues[2] + " h=" +
        r.groupValues[4] + "><param name=src value=" +
        r.groupValues[6] + "><embed src=\\3 pluginspage=http://www.macromedia.com/shockwave/download/ w=" +
        r.groupValues[2] + " h=" +
        r.groupValues[4] + "></embed></object>"

    }
  }

  private fun reRm(s: String): String {
    val regex = "(\\[rm=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/rm])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<OBJECT classId=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA class=OBJECT id=RAOCX w=" +
        r.groupValues[2] + " h="+
        r.groupValues[4] + "><PARAM NAME=SRC VALUE=" +
        r.groupValues[6] + "><PARAM NAME=CONSOLE VALUE=Clip1><PARAM NAME=CONTROLS VALUE=imagewindow><PARAM NAME=AUTOSTART VALUE=true></OBJECT><br><OBJECT classId=CLSID:CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA h=30 id=video2 w=" +
        r.groupValues[2] + "><PARAM NAME=SRC VALUE=" +
        r.groupValues[6] + "><PARAM NAME=AUTOSTART VALUE=-1><PARAM NAME=CONTROLS VALUE=controlpanel><PARAM NAME=CONSOLE VALUE=Clip1></OBJECT>"
    }
  }

  private fun reMp(s: String): String {
    val regex = "(\\[mp=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/mp])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<object align=middle classId=CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95 class=OBJECT id=MediaPlayer w=" +
        r.groupValues[2] + " h=" +
        r.groupValues[4] + " ><param name=ShowStatusBar value=-1><param name=Filename value=" +
        r.groupValues[6] + "><embed type=application/x-oleobject codebase=http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701 flename=mp src=" +
        r.groupValues[6] + " w=" +
        r.groupValues[2] + " h=" +
        r.groupValues[4] + "></embed></object>"
    }
  }

  private fun reQt(s: String): String {
    val regex = "(\\[qt=)([0-9]+)([,])([0-9]+)(])(.+?)(\\[/qt])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<embed src=" + r.groupValues[6] +
        " w=" + r.groupValues[2] +
        " h=" + r.groupValues[4] +
        " autoplay=true loop=false controller=true playEveryFrame=false cache=false scale=TOFIT bgcolor=#000000 kioskMode=false targetCache=false pluginspage=http://www.apple.com/quicktime/>"
    }
  }

  private fun reSound(s: String): String {
    val regex = "(\\[sound])(.+?)(\\[/sound])".toRegex(RegexOption.IGNORE_CASE)
    return s.replace(regex) { r ->
      "<EMBED SRC=" + r.groupValues[2] + " HIDDEN=TRUE AUTOSTART=TRUE LOOP=TRUE>"
    }
  }

}
