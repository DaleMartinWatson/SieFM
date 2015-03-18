package com.vmx;

import javax.microedition.lcdui.Font;
import java.util.Vector;

public class FontWidthCache
{
    protected static Vector fwc = new Vector ();
    protected static Vector fwc_f = new Vector ();
    /**
     * �������� ��� ������ ��� ������ font
     */
    public static FontWidthCache getCache (Font font)
    {
        for (int i = 0; i < fwc.size (); i++)
            if (fwc_f.elementAt(i).equals (font))
                return (FontWidthCache)fwc.elementAt(i);
        fwc_f.addElement (font);
        FontWidthCache f;
        fwc.addElement (f = new FontWidthCache (font));
        return f;
    }
    protected Font font;
    protected byte caches [][];
    /**
     * ���������� �����������
     */
    protected FontWidthCache (Font font)
    {
        this.font = font;
        caches = new byte [256][];
        for (int i = 0; i < 256; i++)
            caches [i] = null;
    }
    /**
     * ���������� (��� ����� �� ����) � ������� ������ ������� ch
     */
    public int charWidth (char ch)
    {
        int hi = (ch >> 8)&0xFF, lo = (ch)&0xFF;
        if (caches [hi] == null)
        {
            caches [hi] = new byte [256];
            for (int i = 0; i < 256; i++)
                caches [hi][i] = -1;
        }
        if (caches [hi][lo] == -1)
            caches [hi][lo] = (byte)font.charWidth (ch);
        return caches [hi][lo];
    }
    /**
     * ���������� � ������� ������ ������ s
     */
    public int stringWidth (String s)
    {
        int l = s.length (), i, r;
        for (r = 0, i = 0; i < l; i++)
            r += charWidth(s.charAt(i));
        return r;
    }
    /**
     * ���������� � ������� ������ ������ s
     */
    public int stringHeight (String s)
    {
        if (s == null)
            return 0;
        int i, l = s.length (), c;
        for (c = 1, i = 0; i < l; i++)
            if (s.charAt (i) == '\n')
                c++;
        return c * font.getHeight ();
    }
    /**
     * �������� � ������ \n-� ����� ��� ����������� �� ������
     * � ����� ������� width
     */
    public String insert_lf (String s, int width)
    {
        int cw = 0, i, l = s.length ();
        char c;
        for (i = 0; i < l; i++)
        {
            c = s.charAt (i);
            if (c == '\n')
                cw = 0;
            else
            {
                cw += charWidth (c);
                if (cw > width)
                {
                    s = s.substring (0, i) + "\n" + s.substring (i);
                    l++;
                    cw = 0;
                }
            }
        }
        return s;
    }
}