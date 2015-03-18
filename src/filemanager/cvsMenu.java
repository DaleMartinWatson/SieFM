package filemanager;

import javax.microedition.lcdui.*;
import java.util.*;
import com.vmx.*;

public class cvsMenu
       extends gkcCanvas
{
    public Displayable parent;
    public Image back;
    public MenuListener listen;
    protected int type, w, h;
    protected int sel1, sel2;
    protected int mw1, mw2, mh1, mh2; // width & height ��� ���� � �������
    protected int mx1, mx2, my1, my2; // ��������� ���� � �������
    protected Font mf; // ����� ����
    protected int mfh; // ������ ������
    protected FontWidthCache mfwc; // ��� ������ ������ %)))
    public boolean enabled [][];
    public static final int MENU_DISK_SELECTED = 0, MENU_FILE_SELECTED = 1,
            MENU_FOLDER_SELECTED = 2, MENU_DOTDOT_SELECTED = 3,
            MENU_FAVORITES_SELECTED = 4, MENU_INSIDE_ARCHIVE = 5,
            MENU_BUFFER_SELECTED = 6, MENU_SELECT_ACTION = 0x100;
    /** ����������� */
    public cvsMenu ()
    {
        listen = null;
        parent = null;
        back = null;
        type = -1;
        sel1 = 0;
        sel2 = -1;
        setFullScreenMode (true);
        w = getWidth ();
        h = getHeight ();
        menu_length = menu_length_fix;
        mf = Font.getFont (Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        mfh = mf.getHeight ();
        mfwc = FontWidthCache.getCache (mf);
        // ������� ��������� ������ � ������ ���� ...
        int i, j, tt;
        for (mw1 = 0, i = 0, tt = 0; i < menu.length; i++)
            if ((tt = mfwc.stringWidth (Locale.Strings[menu[i][0]])) > mw1)
                mw1 = tt;
        mw1 += 4;
        if (mw1 + 20 < w)
            mw1 += 20;
        mx1 = 7;
        mh1 = 2 + 17*menu_length;
        my1 = h-mh1-7;
        // ... � �������
        for (mw2 = 0, i = 0, tt = 0; i < menu.length; i++)
            for (j = 0; j < (menu[i].length-1)/2; j++)
                if ((tt = mfwc.stringWidth (Locale.Strings[menu[i][1+j*2]])) > mw2)
                    mw2 = tt;
        mw2 += 21;
        mh2 = -1;
        mx2 = 14;
        // � ����� ������ � ��������� enabled
        enabled = new boolean [menu.length][];
        for (i = 0; i < menu.length; i++)
        {
            enabled[i] = new boolean [1+(menu[i].length-1)/2];
            for (j = 0; j < 1+(menu[i].length-1)/2; j++)
                enabled [i][j] = true;
        }
        listen = new WorkingMenu (this);
    }
    /** ����� ���� ���� (���������� �������� ���������) */
    public void setType (int type)
    {
        this.type = type;
        sel1 = 0;
        sel2 = -1;
        if (type >= MENU_DISK_SELECTED && type <= MENU_BUFFER_SELECTED)
        {
            menu_length = menu_length_fix;
            mh1 = 2 + 17*menu_length;
            my1 = h-mh1-7;
            for (int i = 0; i < menu_length; i++)
                System.arraycopy (enabledModes[type][i], 0, enabled[i], 0, enabledModes[type][i].length);
            if (Buffer.buf.size() > 0)
            {
                if (type == MENU_FILE_SELECTED ||
                    type == MENU_FOLDER_SELECTED ||
                    type == MENU_DOTDOT_SELECTED)
                {
                    enabled [3][1] = true; //???//
                    enabled [4][3] = true;
                }
                enabled [5][1] = true;
            }
            if (type == MENU_FILE_SELECTED && main.currentFile.toLowerCase().endsWith(".mp3"))
                enabled [0][5] = true;
            for (int i = 0; i < enabled.length; i++)
            {
                enabled [i][0] = false;
                for (int j = 1; j < enabled[i].length; j++)
                    if (enabled [i][j])
                    {
                        enabled [i][0] = true;
                        break;
                    }
            }
            if (type == MENU_DISK_SELECTED && Locale.Strings[Locale.FAVOURITE].equals (main.currentFile))
            {
                enabled [2][0] = false;
                enabled [3][0] = false;
            }
        }
        else if (type == MENU_SELECT_ACTION)
        {
            menu_length = menu.length;
            mh1 = 2 + 17*menu_length;
            my1 = h-mh1-7;
            for (int i = 0; i < menu.length; i++)
                for (int j = 0; j < 1+(menu[i].length-1)/2; j++)
                    enabled [i][j] = true;
        }
    }
    /** ������� ��������� */
    public void paint (Graphics g)
    {
        if (back != null)
            g.drawImage (back, 0, 0, Graphics.LEFT|Graphics.TOP);
        g.setColor (Colors.back);
        g.fillRect (mx1, my1, mw1, mh1);
        g.setColor (Colors.border);
        g.drawRect (mx1, my1, mw1, mh1);
        g.setFont (mf);
        // ��������� ����
        for (int i = 0; i < menu_length; i++)
        {
            if (i == sel1)
            {
                g.setColor (Colors.selback);
                g.fillRect (mx1+1, my1 + 1 + i*17, mw1-1, 18);
                g.setColor (Colors.selfore);
            }
            else
                g.setColor (Colors.fore);
            if (!enabled[i][0])
                g.setColor (Colors.disabled);
            g.drawString (Locale.Strings[menu[i][0]] + " >", mx1 + 2, my1 + 2 + i*17 + 8 - mfh/2, Graphics.LEFT|Graphics.TOP);
        }
        // ���� ���������� ������� - ������ ���
        if (sel2 >= 0)
        {
            int m2l = (menu[sel1].length-1)/2;
            mh2 = 2+m2l*17;
            my2 = my1 + 2 + sel1*17 + 8;
            if (my2 + mh2 > h-14)
                my2 = h-mh2-14;
            if (my2 < 0)
                my2 = 0;
            g.setColor (Colors.back);
            g.fillRect (mx2, my2, mw2, mh2);
            g.setColor (Colors.border);
            g.drawRect (mx2, my2, mw2, mh2);
            for (int i = 0; i < m2l; i++)
            {
                if (i == sel2)
                {
                    g.setColor (Colors.selback);
                    g.fillRect (mx2+1, my2 + 1 + i*17, mw2-1, 18);
                    g.setColor (Colors.selfore);
                }
                else
                    g.setColor (Colors.fore);
                if (!enabled[sel1][i+1])
                    g.setColor (Colors.disabled);
                images.drawIcon (g, menu[sel1][i*2 + 2], mx2 + 2, my2 + 2 + i*17);
                g.drawString (Locale.Strings[menu[sel1][i*2 + 1]], mx2 + 19, my2 + 2 + i*17 + 8 - mfh/2, Graphics.LEFT|Graphics.TOP);
            }
        }
    }
    /** ������� ����������� � ����������� ������ (� parent) */
    public void ret ()
    {
        if (parent != null)
        {
            if (main.dsp.getCurrent () != parent)
                main.dsp.setCurrent (parent);
            parent = null;
        }
    }
    /** ���������� ������� ������ */
    protected void keyPressed (int keyCode)
    {
        int osel, ml;
        if (keyCode == KEY_DOWN)
        {
            if (sel2 == -1) // ������ �� ����
            {
                osel = sel1;
                ml = menu_length;
                do { sel1 = (sel1+1)%ml; }
                while (!enabled[sel1][0] && sel2 != osel);
            }
            else // ������ �� �������
            {
                osel = sel2;
                ml = (menu[sel1].length-1)/2;
                do { sel2 = (sel2+1)%ml; }
                while (!enabled[sel1][1+sel2] && sel2 != osel);
            }
            repaint ();
        }
        else if (keyCode == KEY_UP)
        {
            if (sel2 == -1) // ������ �� ����
            {
                osel = sel1;
                ml = menu_length;
                do { sel1 = (sel1-1+ml)%ml; }
                while (!enabled[sel1][0] && sel2 != osel);
            }
            else // ������ �� �������
            {
                osel = sel2;
                ml = (menu[sel1].length-1)/2;
                do { sel2 = (sel2-1+ml)%ml; }
                while (!enabled[sel1][1+sel2] && sel2 != osel);
            }
            repaint ();
        }
        else if ((!iAmS75 && keyCode == KEY_LSK) ||
            (iAmS75 && keyCode == KEY_RSK) || keyCode == KEY_LEFT ||
            keyCode == KEY_CANCEL)
        {
            if (sel2 == -1)
                ret ();
            else
            {
                sel2 = -1;
                repaint ();
            }
        }
        else if (keyCode == KEY_FIRE || keyCode == KEY_RIGHT ||
            (iAmS75 && keyCode == KEY_LSK) ||
            (!iAmS75 && keyCode == KEY_RSK))
        {
            if (sel2 == -1)
            {
                sel2 = 0;
                while (!enabled[sel1][1+sel2])
                    sel2++;
                repaint ();
            }
            else if (listen != null)
                listen.menuAction (menu [sel1][1 + sel2*2]);
        }
    }
    /** ���������� ���������� ������ */
    protected void keyRepeated (int keyCode)
    {
        if (keyCode == KEY_DOWN || keyCode == KEY_UP)
            keyPressed (keyCode);
    }
    /** ������ ���� */
    static final int menu[][] =
    {
        {
            Locale.MENU_FILE,
            Locale.OPEN_CMD, images.iNext,
            Locale.MARK_CMD, images.iMark,
            Locale.MARK_ALL_CMD, images.iMarkAll,
            Locale.DEMARK_ALL_CMD, images.iDemarkAll,
            Locale.EDIT_ID3_CMD, images.iMelody
        },
        {
            Locale.MENU_ARCHIVE,
            Locale.EXTRACT_CMD, images.iUnpack,
            Locale.EXTRACT_ALL_CMD, images.iUnpack
        },
        {
            Locale.MENU_PROPERTIES,
            Locale.PROPERTY_CMD, images.iProperties,
            Locale.DISK_INFO_CMD, images.iDisk
        },
        {
            Locale.MENU_OPERATIONS,
            Locale.INSERT_CMD, images.iPaste,
            Locale.COPY_CMD, images.iCopy,
            Locale.MOVE_CMD, images.iMove,
            Locale.DELETE_CMD, images.iDelete,
            Locale.RENAME_CMD, images.iRename,
            Locale.TO_FAVOUR_CMD, images.iFavorites
        },
        {
            Locale.MENU_CREATE,
            Locale.NEW_FILE_CMD, images.iFile,
            Locale.NEW_FOLDER_CMD, images.iFolder,
            Locale.CREATE_ZIP, images.iPack
        },
        {
            Locale.MENU_SHOW,
            Locale.BUFFER, images.iClipboard,
            Locale.FAVOURITE, images.iFavorites,
            Locale.HELP_CMD, images.iHelp,
            Locale.PREFERENCES_CMD, images.iOptions,
            Locale.KEYBOARD_CONFIG_CMD, images.iKey,
            Locale.EXIT_CMD, images.iExit
        },
        {
            Locale.MENU_ADDITIONAL,
            Locale.KEY_NO_ACTION, images.iMoveIt,
            Locale.OPTIONS_CMD, images.iMenu,
            Locale.SELECT_CMD, images.iSelect,
            Locale.PREV_FILE_CMD, -1,
            Locale.NEXT_FILE_CMD, -1,
            Locale.PREV_SCREEN_CMD, -1,
            Locale.NEXT_SCREEN_CMD, -1,
            Locale.FULLSCREEN_CMD, -1,
            Locale.UP_LEVEL_CMD, images.iUp
        },
        {
            Locale.PANELS,
            Locale.PANEL_NUMS+0, -1,
            Locale.PANEL_NUMS+1, -1,
            Locale.PANEL_NUMS+2, -1,
            Locale.PANEL_NUMS+3, -1,
            Locale.PANEL_NUMS+4, -1,
            Locale.PANEL_NUMS+5, -1,
            Locale.PANEL_NUMS+6, -1,
            Locale.PANEL_NUMS+7, -1,
            Locale.PANEL_NUMS+8, -1,
            Locale.PANEL_NUMS+9, -1
        }
    };
    static int menu_length = 6;
    static final int menu_length_fix = 6;
    /* ������ enabled ������� ���� */
    static final boolean enabledModes [][][] =
    {
        { // ������ ����
            { false, true, false, false, false, false },
            { false, false, false },
            { false, false, true },
            { false, false, false, false, false, false, true },
            { false, false, false, false },
            { false, false, true, true, true, true, true }
        },
        { // ������ ����
            { false, true, true, true, true, false },
            { false, false, false },
            { false, true, true },
            { false, false, true, true, true, true, false },
            { false, true, true, false },
            { false, false, true, true, true, true, true }
        },
        { // ������� �����
            { false, true, true, true, true, false },
            { false, false, false },
            { false, true, true },
            { false, false, true, true, true, true, true },
            { false, true, true, false },
            { false, false, true, true, true, true, true }
        },
        { // ������� ..
            { false, true, false, true, true, false },
            { false, false, false },
            { false, true, true },
            { false, false, false, false, false, false, true },
            { false, true, true, false },
            { false, false, true, true, true, true, true }
        },
        { // ��������� � ���������
            { false, true, true, true, true, false },
            { false, false, false },
            { false, true, false },
            { false, false, false, false, true, false, false },
            { false, false, false, false },
            { false, false, true, true, true, true, true }
        },
        { // ��������� � ������
            { false, true, true, true, true, false },
            { false, true, true },
            { false, true, true },
            { false, false, false, false, false, false, false },
            { false, false, false, false },
            { false, false, true, true, true, true, true }
        },
        { // ��������� � ������ ������
            { false, true, true, true, true, false },
            { false, false, false },
            { false, false, false },
            { false, false, false, false, true, false, false },
            { false, false, false, false },
            { false, false, true, true, true, true, true }
        }
    };
}
