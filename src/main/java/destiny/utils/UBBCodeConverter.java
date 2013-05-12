/**
 * @author smallufo 
 * Created on 2007/2/5 at 上午 10:42:36
 * 
 * Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
 * Jad home page: <a href="http://www.geocities.com/SiliconValley/Bridge/8617/jad.html" target="_blank">http://www.geocities.com/SiliconValley/Bridge/8617/jad.html</a>
 * Decompiler options: packimports(3) fieldsfirst ansi 
 * Source File Name:   DataFilter.java
 */

package destiny.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UBBCodeConverter 
{
  String       u_s;
  String       u_s1;
  Pattern      u_p;
  Matcher      u_m;
  CharSequence u_s2;
  boolean      result;

  public UBBCodeConverter()
  {
    u_s = new String("haha");
    u_s1 = new String("haha");
  }

  public String getAll(String s)
  {
    String s1 = s;
    s1 = ReImg(s1);
    s1 = ReUrl(s1);
    s1 = ReFly(s1);
    s1 = ReEmail(s1);
    s1 = ReBold(s1);
    s1 = ReSlope(s1);
    s1 = ReDownLine(s1);
    s1 = ReMove(s1);
    s1 = ReTxtUrl(s1);
    s1 = ReTxtEmail(s1);
    s1 = ReAlign(s1);
    s1 = ReTxtSize(s1);
    s1 = ReQuote(s1);
    s1 = ReCode(s1);
    s1 = ReSup(s1);
    s1 = ReSub(s1);
    s1 = ReDelLine(s1);
    s1 = ReFliph(s1);
    s1 = ReFlipv(s1);
    s1 = ReShadow(s1);
    s1 = ReGlow(s1);
    s1 = ReBlur(s1);
    s1 = ReSwf(s1);
    s1 = ReRm(s1);
    s1 = ReMp(s1);
    s1 = ReQt(s1);
    s1 = ReSk(s1);
    s1 = ReSound(s1);
    s1 = ReInvert(s1);
    s1 = ReXray(s1);
    s1 = ReLi(s1);
    s1 = ReLi1(s1);
    s1 = ReLi2(s1);
    return s1;
  }

  public String replace(String s, String s1, String s2)
  {
    String s3 = "";
    int i = s1.length();
    int j;
    while ((j = s.indexOf(s1)) != -1)
    {
      s3 = s3 + s.substring(0, j);
      s3 = s3 + s2;
      s = s.substring(j + i);
    }
    s3 = s3 + s;
    return s3;
  }

  public String ReImg(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[img\\])(.+?)(\\[\\/img\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        //u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<a href=\"" + u_m.group(2) + "\" target=\"_blank\" title=\"開新視窗瀏覽\"><img src=" + u_m.group(2) + " border=0 onload=\"javascript:if(this.width>screen.width-333)this.width=screen.width-333;\" this.alt='點擊查看全圖'\" onmouseover=\"if(this.alt) this.style.cursor='hand';\"></a>");
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<img src=" + u_m.group(2) + " border=0>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReInvert(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[invert\\])(.+?)(\\[\\/invert\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<table style=\"filter:invert\"><img src=" + u_m
            .group(2) + " border=0></table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReXray(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[xray\\])(.+?)(\\[\\/xray\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<table style=\"filter:xray\"><img src=" + u_m.group(2) + " border=0></table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReUrl(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[url\\])(.+?)(\\[\\/url\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<a href=" + u_m.group(2) + " target=_blank>" + u_m
            .group(2) + "</a>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReTxtUrl(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[url=)([^\\]]+)(\\])(.+?)(\\[\\/url\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        //u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5), "<a href=" + u_m.group(2) + " target=_blank title=" + u_m.group(4) + ">" + u_m.group(4) + "</a>");
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5), "<a href=" + u_m.group(2) + " target=_blank >" + u_m.group(4) + "</a>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReEmail(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[email\\])(.+?)(\\[\\/email\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<a href=mailto:" + u_m.group(2) + ">" + u_m.group(2) + "</a>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReTxtEmail(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[email=)([^\\]]+)([\\]])(.+?)(\\[\\/email\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5), "<a href=mailto:" + u_m
            .group(2) + ">" + u_m.group(4) + "</a>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReAlign(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[align=)([^\\]]+)([\\]])(.+?)(\\[\\/align\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5), "<p align=" + u_m
            .group(2) + ">" + u_m.group(4) + "</p>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReFly(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[fly\\])(.+?)(\\[\\/fly\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<div><marquee behavior=alternate>" + u_m.group(2) + "</marquee></div>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReMove(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[move\\])(.+?)(\\[\\/move\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<div><marquee>" + u_m.group(2) + "</marquee></div>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReBold(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[b\\])(.+?)(\\[\\/b\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<b>" + u_m.group(2) + "</b>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReSlope(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[i\\])(.+?)(\\[\\/i\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<i>" + u_m.group(2) + "</i>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReDownLine(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[u\\])(.+?)(\\[\\/u\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<u>" + u_m.group(2) + "</u>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReSup(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[sup\\])(.+?)(\\[\\/sup\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<sup>" + u_m.group(2) + "</sup>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReSub(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[sub\\])(.+?)(\\[\\/sub\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<sub>" + u_m.group(2) + "</sub>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReDelLine(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[s\\])(.+?)(\\[\\/s\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<s>" + u_m.group(2) + "</s>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReFliph(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[fliph\\])(.+?)(\\[\\/fliph\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<table style=\"filter:fliph\">" + u_m.group(2) + "</table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReFlipv(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[flipv\\])(.+?)(\\[\\/flipv\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<table style=\"filter:flipv\">" + u_m.group(2) + "</table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReLi(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[list=)([^\\]]+)([\\]])(.+?)(\\[\\/list\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5), "<ol type=" + u_m
            .group(2) + ">" + u_m.group(4) + "</ol>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReLi1(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[list\\])(.+?)(\\[\\/list\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<ul>" + u_m.group(2) + "</ul>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReLi2(String s)
  {
    return replace(s, "[*]", "<li>");
  }

  public String ReShadow(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[shadow=)([0-9]+)([,])([^,]+)([,])([^\\]]+)(\\])(.+?)(\\[\\/shadow\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7) + u_m.group(8) + u_m.group(9), "<table width=" + u_m.group(2) + " style=\"filter:shadow(color=" + u_m
            .group(4) + ", direction=" + u_m.group(6) + ")\">" + u_m.group(8) + "</table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReGlow(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[glow=)([0-9]+)([,])([^,]+)([,])([^\\]]+)(\\])(.+?)(\\[\\/glow\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7) + u_m.group(8) + u_m.group(9), "<table width=" + u_m.group(2) + " style=\"filter:glow(color=" + u_m
            .group(4) + ", strength=" + u_m.group(6) + ")\">" + u_m.group(8) + "</table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReBlur(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[blur=)([0-9]+)([,])([^,]+)([,])([^\\]]+)(\\])(.+?)(\\[\\/blur\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7) + u_m.group(8) + u_m.group(9), "<table width=" + u_m.group(2) + " style=\"filter:blur(Add=0,direction=" + u_m
            .group(4) + ", strength=" + u_m.group(6) + ")\">" + u_m.group(8) + "</table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReQuote(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[quote\\])(.+?)(\\[\\/quote\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<hr noshade size=1 ><blockquote>" + u_m.group(2) + "</blockquote><hr noshade size=1>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReCode(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[code\\])(.+?)(\\[\\/code\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<table bgcolor=\"black\"><tr><td><font color=white>" + u_m
            .group(2) + "</font></td></tr></table>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReTxtSize(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[size=)([1-9])([\\]])(.+?)(\\[\\/size\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5), "<font size=" + u_m
            .group(2) + ">" + u_m.group(4) + "</font>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReSwf(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[swf=)([0-9]+)([,])([0-9]+)(\\])(.+?)(\\[\\/swf\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7), "<a href=\"" + u_m.group(6) + "\" target=\"_blank\" title=\"全螢幕播放\">全螢幕播放</a><br><OBJECT codeBase=http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=4,0,2,0 classid=clsid:D27CDB6E-AE6D-11cf-96B8-444553540000 width=" + u_m
            .group(2) + " height=" + u_m.group(4) + "><PARAM NAME=movie VALUE=\"" + u_m.group(6) + "\"><PARAM NAME=quality VALUE=high><embed src=\"" + u_m
            .group(6) + "\" quality=high pluginspage='<img align=absmiddle src=pic/url.gif border=0><a target=_blank href=http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'>http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'</a> type='application/x-shockwave-flash' width=" + u_m
            .group(2) + " height=" + u_m.group(4) + ">http://</embed></OBJECT>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReSk(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[sk=)([0-9]+)([,])([0-9]+)(\\])(.+?)(\\[\\/sk\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7), "<object classid=clsid:166B1BCA-3F9C-11CF-8075-444553540000 codebase=http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=7,0,2,0 width=" + u_m
            .group(2) + " height=" + u_m.group(4) + "><param name=src value=" + u_m.group(6) + "><embed src=\\3 pluginspage=http://www.macromedia.com/shockwave/download/ width=" + u_m
            .group(2) + " height=" + u_m.group(4) + "></embed></object>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReRm(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[rm=)([0-9]+)([,])([0-9]+)(\\])(.+?)(\\[\\/rm\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7), "<OBJECT classid=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA class=OBJECT id=RAOCX width=" + u_m.group(2) + " height=" + u_m
            .group(4) + "><PARAM NAME=SRC VALUE=" + u_m.group(6) + "><PARAM NAME=CONSOLE VALUE=Clip1><PARAM NAME=CONTROLS VALUE=imagewindow><PARAM NAME=AUTOSTART VALUE=true></OBJECT><br><OBJECT classid=CLSID:CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA height=30 id=video2 width=" + u_m
            .group(2) + "><PARAM NAME=SRC VALUE=" + u_m.group(6) + "><PARAM NAME=AUTOSTART VALUE=-1><PARAM NAME=CONTROLS VALUE=controlpanel><PARAM NAME=CONSOLE VALUE=Clip1></OBJECT>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReMp(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[mp=)([0-9]+)([,])([0-9]+)(\\])(.+?)(\\[\\/mp\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7), "<object align=middle classid=CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95 class=OBJECT id=MediaPlayer width=" + u_m
            .group(2) + " height=" + u_m.group(4) + " ><param name=ShowStatusBar value=-1><param name=Filename value=" + u_m
            .group(6) + "><embed type=application/x-oleobject codebase=http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701 flename=mp src=" + u_m
            .group(6) + " width=" + u_m.group(2) + " height=" + u_m.group(4) + "></embed></object>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReQt(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[qt=)([0-9]+)([,])([0-9]+)(\\])(.+?)(\\[\\/qt\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3) + u_m.group(4) + u_m.group(5) + u_m.group(6) + u_m
            .group(7), "<embed src=" + u_m.group(6) + " width=" + u_m.group(2) + " height=" + u_m.group(4) + " autoplay=true loop=false controller=true playeveryframe=false cache=false scale=TOFIT bgcolor=#000000 kioskmode=false targetcache=false pluginspage=http://www.apple.com/quicktime/>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

  public String ReSound(String s)
  {
    u_s1 = s;
    u_s = s;
    u_s2 = u_s1.subSequence(0, u_s1.length());
    try
    {
      u_p = Pattern.compile("(\\[sound\\])(.+?)(\\[\\/sound\\])", 2);
      u_m = u_p.matcher(u_s2);
      result = u_m.find();
      while (result)
      {
        u_s = replace(u_s1, u_m.group(1) + u_m.group(2) + u_m.group(3), "<EMBED SRC=" + u_m.group(2) + " HIDDEN=TRUE AUTOSTART=TRUE LOOP=TRUE>");
        u_s1 = u_s;
        u_m.find();
      }
    }
    catch (Exception exception)
    {
    }
    return u_s;
  }

}
