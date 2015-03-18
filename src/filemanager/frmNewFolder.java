package filemanager; // ���������

import javax.microedition.lcdui.*;

public class frmNewFolder
        extends Form
        implements CommandListener
{
    Displayable parent;
    TextField tf;
    String FolderName;
    
    public frmNewFolder (Displayable parent)
    {
        super (Locale.Strings[Locale.CREATE_NEW_FOLDER]);
        this.parent = parent;
        tf = new TextField (Locale.Strings[Locale.NAME], "", 256, TextField.ANY);
        this.append (tf);
        setCommandListener (this);
        addCommand (new Command (Locale.Strings[Locale.CANCEL_CMD], Command.BACK, 1));
        addCommand (new Command (Locale.Strings[Locale.OK_CMD], Command.OK, 1));
        com.siemens.mp.lcdui.Displayable.setHeadlineIcon (this, images.getIcon(images.iFolder));
    }
    
    public void commandAction (Command command, Displayable displayable)
    {
        if (command.getCommandType () == Command.BACK)
            main.dsp.setCurrent (parent);
        else
        {
            if (command.getCommandType () == Command.OK)
            {
                if (filesystem.makeNewDir (main.currentPath + tf.getString () + "/")) // ���� ����� �������
                    cvsWait.start ();
                else // �� �������
                {
                    if ((filesystem.isFileExist (main.currentPath + tf.getString () + "/")) &&
                        (!tf.getString ().equalsIgnoreCase (""))) // ����� ���� ��� ����
                    {
                        Alert al = new Alert (Locale.Strings[Locale.ERROR],
                                Locale.Strings[Locale.NAME_EXIST_SELECT_ANOTHER],
                                null, AlertType.ERROR);
                                //images.error, null);
                        al.setTimeout (3000);
                        main.dsp.setCurrent (al, this);
                    }
                    else
                    {
                        Alert al = new Alert (Locale.Strings[Locale.ERROR],
                                Locale.Strings[Locale.NOT_CREATE_NEW_FOLDER],
                                null, AlertType.ERROR);
                                //images.error, null);
                        al.setTimeout (3000);
                        main.dsp.setCurrent (al, parent);
                    }
                }
            }
        }
    }
}
