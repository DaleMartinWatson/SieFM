package com.vmx;

import java.io.UnsupportedEncodingException;

public class StringEncoder
{
    /** ������� ��������� "windows-1251" */
    protected static char cp1251 [] =
    {
        '\u0410', '\u0411', '\u0412', '\u0413', '\u0414', '\u0415', '\u0416',
        '\u0417', '\u0418', '\u0419', '\u041A', '\u041B', '\u041C', '\u041D',
        '\u041E', '\u041F', '\u0420', '\u0421', '\u0422', '\u0423', '\u0424',
        '\u0425', '\u0426', '\u0427', '\u0428', '\u0429', '\u042A', '\u042B',
        '\u042C', '\u042D', '\u042E', '\u042F', '\u0430', '\u0431', '\u0432',
        '\u0433', '\u0434', '\u0435', '\u0436', '\u0437', '\u0438', '\u0439',
        '\u043A', '\u043B', '\u043C', '\u043D', '\u043E', '\u043F', '\u0440',
        '\u0441', '\u0442', '\u0443', '\u0444', '\u0445', '\u0446', '\u0447',
        '\u0448', '\u0449', '\u042A', '\u044B', '\u044C', '\u044D', '\u044E',
        '\u044F'
    };
    /** �����������. ������. */
    public StringEncoder ()
    {
    }
    /** ���������� ������ s � ��������� enc */
    public static byte [] encodeString (String s, String enc) throws UnsupportedEncodingException
    {
        byte [] bs;
        try
        {
            bs = s.getBytes (enc);
        }
        catch (UnsupportedEncodingException x)
        {
            if (enc.compareTo ("windows-1251") == 0)
            {
                bs = new byte [s.length ()];
                for (int i = 0; i < s.length (); i++)
                    bs [i] = encodeCharCP1251 (s.charAt (i));
                return bs;
            }
            throw x;
        }
        return bs;
    }
    /** �������� ����� ������ s � ������ � ��������� enc */
    public static int getEncodedLength (String s, String enc) throws UnsupportedEncodingException
    {
        byte [] bs;
        try
        {
            bs = s.getBytes (enc);
            return bs.length;
        }
        catch (UnsupportedEncodingException x)
        {
            if (enc.compareTo ("windows-1251") == 0)
                return s.length ();
            throw x;
        }
    }
    /** ������������ ������� ������� b ������ len �� �������� off �� ��������� enc */
    public static String decodeString (byte [] bs, int off, int len, String enc) throws UnsupportedEncodingException
    {
        String s;
        try
        {
            s = new String (bs, off, len, enc);
        }
        catch (UnsupportedEncodingException x)
        {
            if (enc.compareTo ("windows-1251") == 0)
            {
                s = "";
                for (int i = 0; i < len; i++)
                    s += decodeCharCP1251 (bs [off+i]);
                return s;
            }
            throw x;
        }
        return s;
    }
    /** ������������ ������ � windows-1251 */
    public static char decodeCharCP1251 (byte b)
    {
        int ich = b & 0xff;
        if (ich == 0xb8) // �
            return 0x0451;
        else if (ich == 0xa8) // �
            return 0x0401;
        else if (ich >= 0xc0)
            return cp1251 [ich-192];
        return (char)ich;
    }
    /** ���������� ������ � windows-1251 */
    public static byte encodeCharCP1251 (char ch)
    {
        if (ch > 0 && ch < 128)
            return (byte) ch;
        else if (ch == 0x401)
            return -88; // �
        else if (ch == 0x404)
            return -86; // �
        else if (ch == 0x407)
            return -81; // �
        else if (ch == 0x451)
            return -72; // �
        else if (ch == 0x454)
            return -70; // �
        else if (ch == 0x457)
            return -65; // �
        return (byte)((byte)(ch) + 176);
    }
}
