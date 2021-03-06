package filemanager;

import javax.microedition.lcdui.*;

/**
 *
 * @author Dmytro
 */
public class OptionsForm
        extends Form
        implements CommandListener, ItemStateListener
{

    ChoiceGroup cgOptions_showhidden, cgOptions_show3, cgOptions_showSplash,
            cgOptions_openNotSupp, cgOptions_noEffects, cgOptions_lang;
    Displayable parent;
    boolean select_firsttime_lang = true;
    String languages;

    /**
     *
     * @param parent
     */
    public OptionsForm(Displayable parent)
    {
        super(Locale.Strings[Locale.PREFERENCES_CMD]);
        this.parent = parent;
        // Показывать скрытые файлы и папки
        cgOptions_showhidden = new ChoiceGroup(Locale.Strings[Locale.PREF_SHOW_HIDDEN_FILES], ChoiceGroup.MULTIPLE);
        cgOptions_showhidden.append(Locale.Strings[Locale.PREF_SHOW], null);
        cgOptions_showhidden.setSelectedIndex(0, Options.showHidden);
        // Показывать диски b: и 3:
        cgOptions_show3 = new ChoiceGroup(Locale.Strings[Locale.PREF_DISK_3], ChoiceGroup.MULTIPLE);
        cgOptions_show3.append(Locale.Strings[Locale.PREF_OPEN], null);
        cgOptions_show3.setSelectedIndex(0, Options.showDisk3);
        // Пропускать сплэш
        cgOptions_showSplash = new ChoiceGroup(Locale.Strings[Locale.PREF_SPLASH], ChoiceGroup.MULTIPLE);
        cgOptions_showSplash.append(Locale.Strings[Locale.PREF_DONOTSHOW], null);
        cgOptions_showSplash.setSelectedIndex(0, Options.quickSplash);
        // Не показывать "Пожалуйста, подождите" (WaitCanvas)
        cgOptions_noEffects = new ChoiceGroup(Locale.Strings[Locale.PREF_NO_EFFECTS], ChoiceGroup.MULTIPLE);
        cgOptions_noEffects.append(Locale.Strings[Locale.PREFS_YES], null);
        cgOptions_noEffects.setSelectedIndex(0, Options.noEffects);
        // Открывать неподдерживаемые как текст
        cgOptions_openNotSupp = new ChoiceGroup(Locale.Strings[Locale.PREFS_OPEN_NOT_SUPP], ChoiceGroup.MULTIPLE);
        cgOptions_openNotSupp.append(Locale.Strings[Locale.PREFS_YES], null);
        cgOptions_openNotSupp.setSelectedIndex(0, Options.openNotSupported);
        // Список языков
        cgOptions_lang = new ChoiceGroup("Язык / Language:", ChoiceGroup.EXCLUSIVE);
        for (int i = 0; i < Locale.languages.length; i++)
        {
            cgOptions_lang.append(Locale.languages[i], null);
            cgOptions_lang.setSelectedIndex(i, Options.language.compareTo(Locale.locales[i]) == 0);
        }
        append(cgOptions_lang);
        append(cgOptions_showSplash);
        append(cgOptions_noEffects);
        append(cgOptions_showhidden);
        append(cgOptions_show3);
        append(cgOptions_openNotSupp);
        addCommand(new Command(Locale.Strings[Locale.CANCEL_CMD], Command.BACK, 1));
        addCommand(new Command(Locale.Strings[Locale.OK_CMD], Command.OK, 1));
        setCommandListener(this);
        setItemStateListener(this);
        com.siemens.mp.lcdui.Displayable.setHeadlineIcon(this, Images.getIcon(Images.iOptions));
    }

    /**
     * Обработчик команд.
     *
     * @param c
     * @param d
     */
    public void commandAction(Command c, Displayable d)
    {
        if (c.getCommandType() == Command.OK)
        {
            Options.showHidden = cgOptions_showhidden.isSelected(0);
            Options.quickSplash = cgOptions_showSplash.isSelected(0);
            if (cgOptions_show3.isSelected(0) != Options.showDisk3)
            {
                Options.showDisk3 = cgOptions_show3.isSelected(0);
                WaitCanvas.start();
            }
            Options.showDisk3 = cgOptions_show3.isSelected(0);
            Options.noEffects = cgOptions_noEffects.isSelected(0);
            Options.openNotSupported = cgOptions_openNotSupp.isSelected(0);
            Options.language = Locale.locales[cgOptions_lang.getSelectedIndex()];
            WaitCanvas.start();
        } else if (c.getCommandType() == Command.BACK)
            Main.dsp.setCurrent(parent);
    }

    /**
     * Обработчик переключения состояния элемента.
     *
     * @param item
     */
    public void itemStateChanged(Item item)
    {
        if (item == cgOptions_lang && select_firsttime_lang)
        {
            Alert al = new Alert(Locale.Strings[Locale.ATTENTION],
                    Locale.Strings[Locale.NEED_RESTART],
                    null, AlertType.WARNING);
            //images.warn, null);
            al.setTimeout(3000);
            Main.dsp.setCurrent(al, this);
            select_firsttime_lang = false;
        }
    }
}
