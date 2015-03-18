package filemanager;

import javax.microedition.lcdui.*;
import java.util.Vector;
import com.vmx.*;

public class WorkingMenu
       implements MenuListener
{
    protected cvsMenu mn;
    public WorkingMenu (cvsMenu menu)
    {
        mn = menu;
    }
    /**
     * ���������� ��� ����
     */
    public void menuAction (int string)
    {
        if (string < 0)
            return;
        else if (string >= Locale.PANEL_NUMS && string < Locale.PANEL_NUMS+10)
        {
            main.FileSelect.changePanel (string-Locale.PANEL_NUMS);
            return;
        }
        int moveit = 0;
        Alert al;
        Vector v = null;
        String s;
        main.currentFile = main.FileSelect.files [main.FileSelect.scrSel];
        if (string == Locale.PROPERTY_CMD && mn.type == 3 &&
            main.currentPath.endsWith (":/") && "..".equals (main.currentFile))
            string = Locale.DISK_INFO_CMD;
        switch (string)
        {
            case Locale.EXIT_CMD: // �����
                main.midlet.destroyApp (true);
                break;
            case Locale.MARK_CMD: // ��������
                main.FileSelect.markSelected ();
                mn.ret ();
                break;
            case Locale.MARK_ALL_CMD: // �������� ���
                main.FileSelect.markAll ();
                mn.ret ();
                break;
            case Locale.DEMARK_ALL_CMD: // �������� ���������
                main.FileSelect.demarkAll ();
                mn.ret ();
                break;
            case Locale.HELP_CMD: // �������
                main.textEditor.openStream (Locale.getAboutStream (), "UTF-8");
                main.textEditor.parent = mn.parent;
                main.textEditor.caption = Locale.ABOUT_MIDLET_NAME;
                main.dsp.setCurrent (main.textEditor);
                break;
            case Locale.TO_FAVOUR_CMD: // ����� � ���������
                if ("..".equals (main.currentFile))
                    options.addFavorite (main.currentPath);
                else if (main.currentPath != null)
                    options.addFavorite (main.currentPath + main.currentFile);
                else options.addFavorite (main.currentFile.substring (0,3));
                main.dsp.setCurrent (mn.parent);
                break;
            case Locale.DISK_INFO_CMD: // ���� � �����
                main.diskinfo.showDiskProperties (mn.parent);
                break;
            case Locale.PROPERTY_CMD: // ��������
                main.dsp.setCurrent (new frmProperties (mn.parent));
                break;
            case Locale.BUFFER: // �������� ����� ������
                main.FileSelect.showBuffer ();
                break;
            case Locale.OPEN_CMD: // �������
                mn.ret ();
                main.FileSelect.selectFile ();
                break;
            case Locale.RENAME_CMD: // �������������
                main.dsp.setCurrent (new frmRename (mn.parent));
                break;
            case Locale.DELETE_CMD: // ������� ���������/����������
                if ("fav:/".equals (main.currentPath)) // ������� �� ����������
                {
                    if (main.FileSelect.markMode)
                    {
                        for (int i = 1; i < main.FileSelect.files.length; i++)
                            if (main.FileSelect.marked[i])
                                options.deleteFavorite (main.FileSelect.files[i]);
                    }
                    else if (main.FileSelect.scrSel > 0)
                        options.deleteFavorite (main.FileSelect.files[main.FileSelect.scrSel]);
                    cvsWait.start ();
                }
                else if ("buf:/".equals (main.currentPath)) // ������� �� ������
                {
                    if (main.FileSelect.markMode)
                    {
                        for (int i = main.FileSelect.files.length - 1; i > 0; i--)
                            if (main.FileSelect.marked[i])
                                Buffer.remove (i-1);
                    }
                    else if (main.FileSelect.scrSel > 0)
                        Buffer.remove (main.FileSelect.scrSel-1);
                    cvsWait.start ();
                }
                else
                {
                    if (!main.FileSelect.markMode) // ������� ������ ��������������� ����
                        main.dsp.setCurrent (new alConfirmDelete (main.currentPath + main.currentFile, mn.parent));
                    else
                        main.dsp.setCurrent (new alConfirmDeleteSel (mn.parent));
                }
                break;
            case Locale.NEW_FOLDER_CMD: // ������� �����
                main.dsp.setCurrent (new frmNewFolder (mn.parent));
                break;
            case Locale.NEW_FILE_CMD: // ������� ����
                main.dsp.setCurrent (new frmNewFile (mn.parent));
                break;
            case Locale.MOVE_CMD: // �����������
                moveit = 1;
            case Locale.COPY_CMD: // ����������
                if (!main.FileSelect.markMode) // �������� ����������
                    Buffer.add (main.currentPath + main.currentFile, moveit);
                else // �������� ���������� � �����
                {
                    for (int i = 1; i < main.FileSelect.files.length; i++)
                        if (main.FileSelect.marked [i])
                            Buffer.add (main.currentPath + main.FileSelect.files[i], moveit);
                    main.FileSelect.demarkAll ();
                }
                al = new Alert ("",
                        Locale.Strings[Locale.SELECT_PASTE_IN_FOLDER],
                        null, AlertType.WARNING);
                        //images.warn, null);
                al.setTimeout (3000);
                main.dsp.setCurrent (al, mn.parent);
                break;
            case Locale.INSERT_CMD: // ��������
                al = new Alert (Locale.Strings[Locale.WAIT],
                        Locale.Strings[Locale.WAIT_PLEASE],
                        null, AlertType.WARNING);
                        //images.warn, null);
                al.setTimeout (al.FOREVER);
                main.dsp.setCurrent (al, mn.parent);
                Buffer.copyMoveFiles ();
                break;
            case Locale.FAVOURITE: // �������� ���������
                main.currentPath = "fav:/";
                cvsWait.start ();
                break;
            case Locale.PREFERENCES_CMD: // ���������
                main.dsp.setCurrent (new frmOptions (mn.parent));
                break;
            case Locale.EXTRACT_ALL_CMD: // ������� ��
                main.dsp.setCurrent (new frmExtractTo (mn.parent, null));
                break;
            case Locale.EXTRACT_CMD: // �������
                v = new Vector ();
                if (main.FileSelect.markMode)
                {
                    for (int i = 1; i < main.FileSelect.files.length; i++)
                    {
                        if (main.FileSelect.marked [i])
                        {
                            s = main.currentPath + main.FileSelect.files [i];
                            v.addElement (s.substring (filesystem.divideZipName (s)));
                        }
                    }
                    main.FileSelect.demarkAll ();
                }
                else
                {
                    s = main.currentPath + main.currentFile;
                    v.addElement (s.substring (filesystem.divideZipName (s)));
                }
                if (v.size () > 0)
                    main.dsp.setCurrent (new frmExtractTo (mn.parent, v));
                break;
            case Locale.CREATE_ZIP: // ������� ZIP-����� �� ������ �� ������
                main.dsp.setCurrent (new frmCompress (mn.parent));
                break;
            case Locale.EDIT_ID3_CMD:
                try
                {
                    main.dsp.setCurrent (new id3Editor (main.currentPath + main.currentFile, main.FileSelect));
                }
                catch (Exception x)
                {
                    al = new Alert (Locale.Strings[Locale.ERROR],
                            x.getClass().getName() + ": " + x.getMessage (), null, AlertType.ERROR);
                    al.setTimeout (3000);
                    main.dsp.setCurrent (al, main.FileSelect);
                }
                break;
            case Locale.SELECT_CMD:
                mn.ret ();
                main.FileSelect.fireSelectAction ();
                break;
            case Locale.PREV_FILE_CMD:
                main.FileSelect.directKeyPressed (main.FileSelect.KEY_UP);
                mn.ret ();
                break;
            case Locale.NEXT_FILE_CMD:
                main.FileSelect.directKeyPressed (main.FileSelect.KEY_DOWN);
                mn.ret ();
                break;
            case Locale.PREV_SCREEN_CMD:
                main.FileSelect.directKeyPressed (main.FileSelect.KEY_LEFT);
                mn.ret ();
                break;
            case Locale.NEXT_SCREEN_CMD:
                main.FileSelect.directKeyPressed (main.FileSelect.KEY_RIGHT);
                mn.ret ();
                break;
            case Locale.UP_LEVEL_CMD:
                if (main.currentPath != null && main.currentPath.length () > 0)
                    main.FileSelect.upDir ();
                else mn.ret ();
                break;
            case Locale.OPTIONS_CMD:
                main.FileSelect.showMenu ();
                break;
            case Locale.FULLSCREEN_CMD:
                main.FileSelect.initFSMode (!main.FileSelect.fsmode);
                mn.ret ();
                break;
            case Locale.BACK_CMD:
                main.FileSelect.upDir ();
                break;
            case Locale.KEYBOARD_CONFIG_CMD:
                main.keycfg.show ();
                break;
        }
    }
}
